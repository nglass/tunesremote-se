/*
 * This file is part of TunesRemote SE.
 *
 * TunesRemote SE is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * any later version.
 *
 * TunesRemote SE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TunesRemote SE; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright 2011 Nick Glass
 */
package net.firefly.client.player;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import net.firefly.client.controller.ListManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.context.events.MasterVolumeChangedEvent;
import net.firefly.client.gui.context.listeners.MasterVolumeChangedEventListener;
import net.firefly.client.model.data.Album;
import net.firefly.client.model.data.Artist;
import net.firefly.client.model.data.Genre;
import net.firefly.client.model.data.Song;
import net.firefly.client.model.data.SongContainer;
import net.firefly.client.model.data.list.SongList;
import net.firefly.client.model.playlist.IPlaylist;
import net.firefly.client.player.events.PlayerErrorOccurredEvent;
import net.firefly.client.player.events.PlayerModeChangedEvent;
import net.firefly.client.player.events.PlayerStatusChangedEvent;
import net.firefly.client.player.events.RepeatModeChangedEvent;
import net.firefly.client.player.events.SongChangedEvent;
import net.firefly.client.player.events.TimePlayedChangedEvent;
import net.firefly.client.player.listeners.PlayerErrorOccuredEventListener;
import net.firefly.client.player.listeners.PlayerModeChangedEventListener;
import net.firefly.client.player.listeners.PlayerStatusChangedEventListener;
import net.firefly.client.player.listeners.RepeatModeChangedEventListener;
import net.firefly.client.player.listeners.SongChangedEventListener;
import net.firefly.client.player.listeners.TimePlayedChangedEventListener;

import org.tunesremote.daap.RequestHelper;
import org.tunesremote.daap.Session;
import org.tunesremote.daap.Status;
import org.tunesremote.util.ThreadExecutor;

import android.graphics.Bitmap;

public class MediaPlayer implements android.os.Handler {
	protected Session session;

	protected Status status;
	
	protected Context context;
	
	protected PlayerMode playerMode;
	
	protected PlayerStatus playerStatus;
	
	protected RepeatMode repeatMode;
	
	protected boolean visualizerOn, fullscreen;

	protected boolean supportSeeking;
	
	protected SongContainer playingSong;
	
	protected EventListenerList listenerList;
	
	protected long initialVolume;
	
	protected byte[] cover;

	private long playingPlaylistId = 0;
	
	boolean closing = false;
	
	public void close() {
		status.destroy();
		session.logout();
	}
	
	public MediaPlayer(Context context, Session session) {
		this.context = context;
		this.session = session;
		this.status = this.session.singletonStatus(this);
		
		this.playingSong = new SongContainer();
		
		this.listenerList = new EventListenerList();
		this.playerStatus = PlayerStatus.STATUS_STOPPED;
		this.playerMode = PlayerMode.MODE_NORMAL;
		this.repeatMode = RepeatMode.REPEAT_OFF;
		this.supportSeeking = false;
		this.visualizerOn = false;
		this.fullscreen = false;
		this.cover = null;
		
		updateStatus();
		this.initialVolume = status.getVolume();
	}
	
	public String getSessionId() {
		return this.session.sessionId;
	}
	
	// Handle messages from the DACP backend
	public void handleTunesRemoteMessage(int what) {
		switch (what) {
		case Status.UPDATE_PROGRESS:
			updateProgress();
			break;
		case Status.UPDATE_STATE:
			updateStatus();
			break;
		case Status.UPDATE_TRACK:
			updatePlayingSong();		   
			break;
		case Status.UPDATE_COVER:
			updateCover();
			break;
		case Status.UPDATE_RATING:
			//System.out.println("UPDATE_RATING");
			break;
		case Status.UPDATE_REVISION:
		   //System.out.println("UPDATE_REVISION");
		   break;
		default:
			System.err.println("Error: handleTunesRemoteMessage " + what);
			break;
		}
	}
	
	
	// This class is used to synchronize TunesRemote messages
	// with the AWT event queue
    private class TunesRemoteMessage implements Runnable {
    	private MediaPlayer mediaPlayer;
    	private int what;
    	
    	public TunesRemoteMessage(MediaPlayer mediaPlayer, int what) {
    		this.mediaPlayer = mediaPlayer;
    		this.what = what;	
    	}

		@Override
		public void run() {
			mediaPlayer.handleTunesRemoteMessage(what);		
		} 	
    }
    
    // Android compatible message handler implementation
	@Override
	public boolean sendEmptyMessage(int what) {		
		SwingUtilities.invokeLater(new TunesRemoteMessage(this, what));	
		return true;
	}

	
	//
	// Player controls
	// 

	public void stopPlayback() {
		
	}
	
	public void next() {
		session.controlNext();
	}

	public void previous() {
		session.controlPrev();	
	}

	
	
	private void playMasterList(final int index) {
		//GET /ctrl-int/1/cue?command=play&query=('com.apple.itunes.mediakind:1',
		//'com.apple.itunes.mediakind:32')&index=2&sort=artist&
		//srcdatabase=0xBBD273CF5AE2139E&clear-first=1&session-id=1250589827 HTTP/1.1\r\n
		ThreadExecutor.runTask(new Runnable() {
			public void run() {
				RequestHelper.attemptRequest(String.format(
						"%s/ctrl-int/1/cue?command=play&" +
						"query=('com.apple.itunes.mediakind:1','com.apple.itunes.mediakind:32')" +
						"&index=%d&sort=artist&srcdatabase=0x%s&clear-first=1&session-id=%s",
						session.getRequestBase(),
						index,
						session.databasePersistentId,
						session.sessionId));
			}
		});
	}
	
	
	public String constructSearch(final String search, final int searchflag) {
		String[] searchStrings = search.split("\\s");
		String[] encodedStrings = new String[searchStrings.length];
		
		// the second replace is necessary to handle "'" symbols
		for (int i=0; i<searchStrings.length; i++) {
			try {
				encodedStrings[i] = URLEncoder.encode(searchStrings[i],"UTF-8")
					.replaceAll("\\+", "%20")
					.replaceAll("%27","%5C'");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "";
			}
		}
		
		boolean extend = false;
		StringBuilder query = new StringBuilder();
		
		// this is used to specify music only for baselist
		// will need to do something more clever if i ever get playlist search queries working
		query.append("('com.apple.itunes.mediakind:1','com.apple.itunes.mediakind:32')+(");
		
		if ((searchflag & ListManager.FLG_SEARCH_GENRE) > 0) {
			query.append("(");
			for (int i=0; i<encodedStrings.length; i++) {
				if (extend) query.append("+");
				query.append("'daap.songgenre:*");
				query.append(encodedStrings[i]);
				query.append("*'");
				extend = true;
			}
			query.append(")");
		}
		
		if ((searchflag & ListManager.FLG_SEARCH_ARTIST) > 0) {
			if (extend) {
				query.append(",");
				extend = false;
			}
			query.append("(");
			for (int i=0; i<encodedStrings.length; i++) {
				if (extend) query.append("+");
				query.append("'daap.songartist:*");
				query.append(encodedStrings[i]);
				query.append("*'");
				extend = true;
			}
			query.append(")");
		}

		if ((searchflag & ListManager.FLG_SEARCH_ALBUM) > 0) {
			if (extend) {
				query.append(",");
				extend = false;
			}
			query.append("(");
			for (int i=0; i<encodedStrings.length; i++) {
				if (extend) query.append("+");
				query.append("'daap.songalbum:*");
				query.append(encodedStrings[i]);
				query.append("*'");
				extend = true;
			}
			query.append(")");
		}
		
		if ((searchflag & ListManager.FLG_SEARCH_TITLE) > 0) {
			if (extend) {
				query.append(",");
				extend = false;
			}
			query.append("(");
			for (int i=0; i<encodedStrings.length; i++) {
				if (extend) query.append("+");
				query.append("'dmap.itemname:*");
				query.append(encodedStrings[i]);
				query.append("*'");
				extend = true;
			}
			query.append(")");
		}
		query.append(")");
		
		// now add on filtered searches
		String filter = constructFilter();
		if (filter.length() > 0) {
			query.append("+");
			query.append(filter);
		}
		
		return query.toString();
	}
	
	
	// queries use ',' for or
	//         use '+' for and
	//         use '!:' for not equal to???
	// and allow parentheses for specifying precedence
	public String constructFilter() {

		Genre[] genres = null;
		Artist[] artists = null;
		Album[] albums = null;
		boolean extend = false;
		StringBuilder query = new StringBuilder();
		
		if (!context.isAllGenresSelected() && 
				context.getSelectedGenres() != null && context.getSelectedGenres().length > 0) {
			genres = context.getSelectedGenres();
		}
		
		if (!context.isAllArtistsSelected() &&
				context.getSelectedArtists() != null && context.getSelectedArtists().length > 0) {
			artists = context.getSelectedArtists();
		}
		
		if (!context.isAllAlbumsSelected() &&
				context.getSelectedAlbums() != null && context.getSelectedAlbums().length > 0) {
			albums = context.getSelectedAlbums();
		}
		
		try {	
			if (genres != null) {
				query.append("(");
				for (int i=0; i < genres.length; i++) {
					String encodedGenre = URLEncoder.encode(genres[i].toString(), "UTF-8")
						.replaceAll("\\+", "%20");
					if (extend) query.append(",");
					query.append("'daap.songgenre:");
					query.append(encodedGenre);
					query.append("'");
					extend = true;
				}
				query.append(")");
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (artists != null || context.isCompilationSelected()) {
			if (extend) {
				query.append("+");
				extend = false;
			}
			query.append("(");
			
			if (artists != null) {
				for (int i=0; i < artists.length; i++) {
					if (extend) query.append(",");
					if (artists[i].getArtist().length() > 0) {
						query.append("'daap.songartistid:");
						query.append(artists[i].getArtistId());
						query.append("'");
					} else {
						query.append("'daap.songartist:'");
					}
					extend = true;
				}
			}
			
			if (context.isCompilationSelected()) {
				if (extend) query.append(",");	
				query.append("'daap.songcompilation:1'");
				extend = true;
			}
			
			query.append(")");
		}
		
		if (albums != null) {
			if (extend) {
				query.append("+");
				extend = false;
			}
			query.append("(");
			
			for (int i=0; i < albums.length; i++) {
				if (extend) query.append(",");
				if (albums[i].getAlbum().length() > 0) {
					query.append("'daap.songalbumid:");
					query.append(albums[i].getAlbumId());
					query.append("'");
				} else {
					query.append("'daap.songalbum:'");
				}
				extend = true;
			}
			
			query.append(")");
		}
		
		return query.toString();
	}
	
	public void controlPlaySearch(final String searchQuery, final int index) {
		// /ctrl-int/1/cue?command=play&query=(('com.apple.itunes.mediakind:1','com.apple.itunes.mediakind:4','com.apple.itunes.mediakind:8')+'dmap.itemname:*F*')&index=4&sort=name&session-id=1550976127
		// /ctrl-int/1/cue?command=play&query='dmap.itemname:*%s*'&index=%d&sort=name&session-id=%s
		
		ThreadExecutor.runTask(new Runnable() {
			public void run() {
				RequestHelper.attemptRequest(String.format(
						"%s/ctrl-int/1/cue?command=play&query=(%s)&type=music&" +
						"sort=artist&index=%d&clear-first=1&session-id=%s",
						session.getRequestBase(), searchQuery, index, session.sessionId));
			}
		});
	}
	
	// TODO work out if we can filter on playlists.  This doesnt work yet
	public void playFilteredPlaylist
	(final String playlistPersistentId, final long containerItemId, final String searchQuery) {
		// /ctrl-int/1/playspec?database-spec='dmap.persistentid:0x9031099074C14E05'&container-spec='dmap.persistentid:0xA1E1854E0B9A1B'&container-item-spec='dmap.containeritemid:0x1b47'&session-id=7491138

		ThreadExecutor.runTask(new Runnable() {
			public void run() {
				RequestHelper.attemptRequest(String.format(
						"%s/ctrl-int/1/playspec?" +
						"&database-spec='dmap.persistentid:0x%s'" +
						"&container-spec='dmap.persistentid:0x%s'" +
						"&container-item-spec='dmap.containeritemid:0x%x'" +
						"&query=(%s)" +
						"&session-id=%s",
						session.getRequestBase(), session.databasePersistentId, 
						playlistPersistentId, containerItemId,
						searchQuery,
						session.sessionId));
			}
		});
	}
	
	public void play() {
		String searchQuery = null;
		
		if (context.getSearchmask() != null && context.getSearchmask().length() > 0){
			searchQuery = constructSearch(
					context.getSearchmask(), 
					context.getConfig().getSearchFlag());
		} else {
			searchQuery = constructFilter();
		}
		
		// TODO work out requests for different sort options
		// - will probably have to ensure own sort == target applications sort first
		//System.err.println("Sort = " + context.getTableSorter().getTableHeader());
		
		SongContainer songToPlay = context.getFilteredSongList().selectedSong(context);		
		if (songToPlay != null && !songToPlay.equals(playingSong) ) {
			IPlaylist playList = context.getSelectedPlaylist();
			
			if (playList != null) {
				// TODO work out how to request filtered / searched tunes in playlist	
				session.controlPlayPlaylist(playList.getPersistentId(), 
						String.format("%x", songToPlay.getContainerId()));
				
			} else if (searchQuery != null && searchQuery.length() > 0){
				controlPlaySearch(searchQuery, context.getFilteredSongList().getSelectedIndex());
			} else {
				playMasterList(context.getFilteredSongList().getSelectedIndex());		
			}
		} else {
			if (playerStatus != PlayerStatus.STATUS_PLAYING) {
				session.controlPlay();	
			}
		}
	}

	public void resume() {
      if (playerStatus != PlayerStatus.STATUS_PLAYING) {
         session.controlPlay();
      }
	}
	
	public void pause() {
		if (playerStatus == PlayerStatus.STATUS_PLAYING) {
			session.controlPause();	
		}
	}

	// seek newTime in ms
	public void seek(long newTime) {
		session.controlProgress((int)(newTime/1000));
		
	}
	
	public void setVolume(double newVolume) {
		session.controlVolume((long)newVolume);
		fireMasterVolumeChanged(new MasterVolumeChangedEvent(newVolume));
	}
	
	public double getInitialVolume() {
		return initialVolume;
	}
	
   public void increaseVolume() {
      long volume = status.getVolume();
      volume = Math.min(100, volume + 5);
      setVolume(volume);  
   }
   
   public void decreaseVolume() {
      long volume = status.getVolume();
      volume = Math.max(0, volume - 5);
      setVolume(volume);  
   }
	
	public void setPlayerMode(PlayerMode mode) {
		if (mode == PlayerMode.MODE_NORMAL) {
			session.controlShuffle(0);
		} else {
			session.controlShuffle(1);
		}
		changeMode(mode);
	}
	
	public void setRepeatMode(RepeatMode mode) {
		if (mode == RepeatMode.REPEAT_OFF) {
			session.controlRepeat(Status.REPEAT_OFF);
		} else if (mode == RepeatMode.REPEAT_SINGLE) {
			session.controlRepeat(Status.REPEAT_SINGLE);
		} else if (mode == RepeatMode.REPEAT_ALL) {
			session.controlRepeat(Status.REPEAT_ALL);
		}
		changeRepeatMode(mode);
	}
	
	public void setVisualizer(boolean enabled) {
		session.controlVisualiser(enabled);
		visualizerOn = enabled;
	}

	public void setFullscreen(boolean enabled) {
		session.controlFullscreen(enabled);
		fullscreen = enabled;
	}
	
	//
	// Status
	//

	protected void changeStatus(PlayerStatus newStatus) {
		PlayerStatus oldStatus = this.playerStatus;
		this.playerStatus = newStatus;
		firePlayerStatusChange(new PlayerStatusChangedEvent(this, oldStatus, newStatus));
	}
	
	protected void changeMode(PlayerMode newMode) {
		PlayerMode oldMode = this.playerMode;
		this.playerMode = newMode;
		firePlayerModeChange(new PlayerModeChangedEvent(this, oldMode, newMode));	
	}
	
	protected void changeRepeatMode(RepeatMode newMode) {
		RepeatMode oldMode = this.repeatMode;
		this.repeatMode = newMode;
		fireRepeatModeChange(new RepeatModeChangedEvent(this, oldMode, newMode));	
	}
	
	protected void updateStatus() {
		visualizerOn = status.isVisualizerOn();
		fullscreen = status.isFullscreen();

		switch (status.getPlayStatus()) {
		case Status.STATE_PAUSED:
			changeStatus(PlayerStatus.STATUS_PAUSED);
			break;
		case Status.STATE_PLAYING:
			changeStatus(PlayerStatus.STATUS_PLAYING);
			break;
		default:
			changeStatus(PlayerStatus.STATUS_STOPPED);
			break;
		}

		switch(status.getShuffle()) {
		case Status.SHUFFLE_OFF:
			changeMode(PlayerMode.MODE_NORMAL);
			break;
		case Status.SHUFFLE_ON:
			changeMode(PlayerMode.MODE_SHUFFLE);
			break;
		default:
			System.err.println("Error shuffle mode = " + status.getShuffle());
			break;
		}

		switch(status.getRepeat()) {
		case Status.REPEAT_OFF:
			changeRepeatMode(RepeatMode.REPEAT_OFF);
			break;
		case Status.REPEAT_SINGLE:
			changeRepeatMode(RepeatMode.REPEAT_SINGLE);
			break;
		case Status.REPEAT_ALL:
			changeRepeatMode(RepeatMode.REPEAT_ALL);
			break;
		default:
			System.err.println("Error repeat mode = " + status.getRepeat());
			break;
		}
	}
	
	protected void updatePlayingSong() {
		
		SongContainer sc = null;
		SongList songList = null;
		SongList masterSongList = context.getMasterSongList();
		IPlaylist playlist = context.getPlaylists().getPlaylistById(status.getPlaylistId());

		if (playlist != null) {
			songList = playlist.getSongList();
		}
		
		if (songList == null) {
			songList = masterSongList;
		}
		
		if (songList != null) {
			sc = songList.getSongByContainerId(status.getContainerItemId());	
		}
		
		if (sc == null && masterSongList != null) {
			sc = masterSongList.getSongByDatabaseId(status.getTrackId());			
		}
		
		// If we really cant find the song in the database
		// (Or if we found the wrong one)
		// Make a new song based on now playing information
		if (sc == null || !sc.getSong().getTitle().equals(status.getTrackName())) {			
		   sc = new SongContainer();
		   Song s = new Song();
			
			s.setTitle(status.getTrackName());
			s.setTime(status.getProgressTotal() * 1000);
			s.setGenre(status.getTrackGenre());
			s.setDatabaseItemId(status.getTrackId());
			
			String artistName = status.getTrackArtist();
			Artist artist = new Artist(artistName, artistName, "");
			s.setArtist(artist);
			
			String albumName = status.getTrackAlbum();
			Album album = new Album(albumName, albumName, "");
         s.setAlbum(album);
			
			sc.setContainerId(status.getContainerItemId());
			sc.setPlaylistId(status.getPlaylistId());
			sc.setDatabaseId(status.getDatabaseId());
			
			sc.setSong(s);	
		}
		
		if (songList != null) {
			songList.selectSongIfExists(sc);		
		}
		
		this.playingSong = sc;
		this.playingPlaylistId = status.getPlaylistId();
		this.supportSeeking = (status.getProgressTotal() > 0);
		
		fireSongChange(new SongChangedEvent(this, sc));
		// force status objects to recheck state
		updateStatus();
	}
	
	protected void updateProgress() {
		if (this.playerStatus != PlayerStatus.STATUS_STOPPED) {
			fireTimePlayedChange(new TimePlayedChangedEvent
				(this, status.getProgress() * 1000, status.getProgressTotal() * 1000));	
		}
	}
	
	protected void updateCover() {
		Bitmap coverCache = status.coverCache;
		
		if (coverCache == null) {
			this.cover = null;
		} else {
			this.cover = coverCache.getData();
		}
		updateStatus();
	}
	
	public PlayerStatus getPlayerStatus() {
		return this.playerStatus;
	}

	public PlayerMode getPlayerMode() {
		return this.playerMode;
	}
	
	public RepeatMode getRepeatMode() {
		return this.repeatMode;
	}
	
	public SongContainer getPlayingSong() {
		return this.playingSong;
	}

	public long getPlayingPlaylistId() {
		return playingPlaylistId;
	}

	public boolean isSupportSeeking() {
		return supportSeeking;
	}

	public byte[] getCover() {
		return cover;
	}
	
	public boolean isVisualizerOn() {
		return visualizerOn;
	}
	
	public boolean isFullscreen() {
		return fullscreen;
	}
	
	// -- events management

	public void addPlayerStatusChangedEventListener(PlayerStatusChangedEventListener listener) {
		listenerList.add(PlayerStatusChangedEventListener.class, listener);
		listener.onPlayerStatusChange(new PlayerStatusChangedEvent(this, playerStatus, playerStatus));
	}

	public void removePlayerStatusChangedEventListener(PlayerStatusChangedEventListener listener) {
		listenerList.remove(PlayerStatusChangedEventListener.class, listener);
	}

	protected void firePlayerStatusChange(PlayerStatusChangedEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == PlayerStatusChangedEventListener.class) {
				((PlayerStatusChangedEventListener) listeners[i + 1]).onPlayerStatusChange(e);
			}
		}
	}
	
	public void addPlayerModeChangedEventListener(PlayerModeChangedEventListener listener) {
		listenerList.add(PlayerModeChangedEventListener.class, listener);
		listener.onPlayerModeChange(new PlayerModeChangedEvent(this, playerMode, playerMode));
	}

	public void removePlayerModeChangedEventListener(PlayerModeChangedEventListener listener) {
		listenerList.remove(PlayerModeChangedEventListener.class, listener);
	}

	protected void firePlayerModeChange(PlayerModeChangedEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == PlayerModeChangedEventListener.class) {
				((PlayerModeChangedEventListener) listeners[i + 1]).onPlayerModeChange(e);
			}
		}
	}

	public void addRepeatModeChangedEventListener(RepeatModeChangedEventListener listener) {
		listenerList.add(RepeatModeChangedEventListener.class, listener);
		listener.onRepeatModeChange(new RepeatModeChangedEvent(this, repeatMode, repeatMode));
	}

	public void removeRepeatModeChangedEventListener(RepeatModeChangedEventListener listener) {
		listenerList.remove(RepeatModeChangedEventListener.class, listener);
	}

	protected void fireRepeatModeChange(RepeatModeChangedEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == RepeatModeChangedEventListener.class) {
				((RepeatModeChangedEventListener) listeners[i + 1]).onRepeatModeChange(e);
			}
		}
	}
	
	public void addSongChangedEventListener(SongChangedEventListener listener) {
		listenerList.add(SongChangedEventListener.class, listener);
		listener.onSongChange(new SongChangedEvent(this, playingSong));
	}

	public void removeSongChangedEventListener(SongChangedEventListener listener) {
		listenerList.remove(SongChangedEventListener.class, listener);
	}

	protected void fireSongChange(SongChangedEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == SongChangedEventListener.class) {
				((SongChangedEventListener) listeners[i + 1]).onSongChange(e);
			}
		}
	}

	public void addTimePlayedChangedEventListener(TimePlayedChangedEventListener listener) {
		listenerList.add(TimePlayedChangedEventListener.class, listener);
	}

	public void removeTimePlayedChangedEventListener(TimePlayedChangedEventListener listener) {
		listenerList.remove(TimePlayedChangedEventListener.class, listener);
	}

	protected void fireTimePlayedChange(TimePlayedChangedEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == TimePlayedChangedEventListener.class) {
				((TimePlayedChangedEventListener) listeners[i + 1]).onTimePlayedChange(e);
			}
		}
	}

	public void addPlayerErrorOccuredEventListener(PlayerErrorOccuredEventListener listener) {
		listenerList.add(PlayerErrorOccuredEventListener.class, listener);
	}

	public void removePlayerErrorOccuredEventListener(PlayerErrorOccuredEventListener listener) {
		listenerList.remove(PlayerErrorOccuredEventListener.class, listener);
	}

	protected void firePlayerErrorOccured(PlayerErrorOccurredEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == PlayerErrorOccuredEventListener.class) {
				((PlayerErrorOccuredEventListener) listeners[i + 1]).onPlayerError(e);
			}
		}
	}
   
   public void addMasterVolumeChangedEventListener(MasterVolumeChangedEventListener listener) {
      listenerList.add(MasterVolumeChangedEventListener.class, listener);
   }

   public void removeMasterVolumeChangedEventListener(MasterVolumeChangedEventListener listener) {
      listenerList.remove(MasterVolumeChangedEventListener.class, listener);
   }

   public void fireMasterVolumeChanged(MasterVolumeChangedEvent e) {
      Object[] listeners = listenerList.getListenerList();
      for (int i = 0; i < listeners.length; i += 2) {
         if (listeners[i] == MasterVolumeChangedEventListener.class) {
            ((MasterVolumeChangedEventListener) listeners[i + 1]).onMasterVolumeChange(e);
         }
      }
   }
}
