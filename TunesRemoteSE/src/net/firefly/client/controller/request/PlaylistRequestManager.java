/*
 * This file is part of TunesRemote-SE.
 *
 * TunesRemote-SE is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * any later version.
 *
 * TunesRemote-SE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FireflyClient; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright 2011 Nick Glass
 */
package net.firefly.client.controller.request;

import java.util.regex.Pattern;

import org.tunesremote.TagListener;
import org.tunesremote.daap.RequestHelper;
import org.tunesremote.daap.Response;
import org.tunesremote.daap.ResponseParser;
import org.tunesremote.daap.Session;

import android.util.Log;

import net.firefly.client.controller.events.SongListLoadProgressEvent;
import net.firefly.client.controller.listeners.SongListLoadProgressListener;
import net.firefly.client.gui.context.Context;
import net.firefly.client.model.data.Song;
import net.firefly.client.model.data.SongContainer;
import net.firefly.client.model.data.list.SongList;
import net.firefly.client.model.playlist.IPlaylist;
import net.firefly.client.model.playlist.ITunesPlaylist;
import net.firefly.client.model.playlist.PlaylistStatus;
import net.firefly.client.model.playlist.SmartPlaylist;
import net.firefly.client.model.playlist.list.PlaylistList;
import net.firefly.client.tools.FireflyClientException;

public class PlaylistRequestManager {

	public final static String TAG = PlaylistRequestManager.class.toString();
	public final static Pattern MLIT_PATTERN = Pattern.compile("mlit");
	
	Session session;
	Context context;
	
	public PlaylistRequestManager(Context context, Session session) {
		this.context = context;
		this.session = session;
	}

	public static boolean supportPlaylistAdvancedManagement(String serverVersion){
		return true;
	}

	public PlaylistList getPlaylistList(SongList parentLibrarySongList, boolean sortStaticPlaylists, String host,
			int port, String proxyHost, String proxyPort, String password) throws FireflyClientException {
		PlaylistList playlistList = new PlaylistList();

		// fetch playlists to find the overall magic "Music" playlist
		try {
			Response playlists = RequestHelper.requestParsed(String.format(
					"%s/databases/%d/containers?session-id=%s&" +
					"meta=dmap.itemname,dmap.itemcount,dmap.itemid,dmap.persistentid," +
					"daap.baseplaylist,com.apple.itunes.special-playlist," +
					"com.apple.itunes.smart-playlist,com.apple.itunes.saved-genius," +
					"dmap.parentcontainerid,dmap.editcommandssupported",
					session.getRequestBase(),
					session.databaseId, 
					session.sessionId), 
					false);

			for (Response resp : playlists.getNested("aply").getNested("mlcl").findArray("mlit")) {
				String name = resp.getString("minm");
				long id = resp.getNumberLong("miid");
				//long itemCount = resp.getNumberLong("mimc");
				String persistentId = resp.getNumberHex("mper");
				boolean basePlaylist = resp.containsKey("abpl");
				boolean isSmartPlaylist = resp.containsKey("aeSP");
				//boolean savedGenius = resp.containsKey("aeSG");
				//long editCommands = resp.getNumberLong("meds");
				//long specialPlaylist = resp.getNumberLong("aePS");
				//long parentContainer = resp.getNumberLong("mpco");
				
				if (basePlaylist)
					continue;
				
				if (isSmartPlaylist) {
					SmartPlaylist smartPlaylist = new SmartPlaylist(parentLibrarySongList);
					smartPlaylist.setPlaylistId(id);
					smartPlaylist.setPlaylistName(name);
					smartPlaylist.setPersistentId(persistentId);
					
					playlistList.add(smartPlaylist);
				} else {
					ITunesPlaylist iTunesPlaylist = new ITunesPlaylist(parentLibrarySongList);
					iTunesPlaylist.setPlaylistId(id);
					iTunesPlaylist.setPlaylistName(name);
					iTunesPlaylist.setPersistentId(persistentId);
					
					playlistList.add(iTunesPlaylist);
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return playlistList;
	}

	
	private class TrackListener implements TagListener {
		
		protected IPlaylist playlist;
		protected SongListLoadProgressListener listener;
		protected int loadedSongs = 0;

		public TrackListener(IPlaylist playlist, SongListLoadProgressListener listener) {
			this.playlist = playlist;
			this.listener = listener;
		}

		public void foundTag(String tag, Response resp) {
			// add a found search result to our list
			if (resp.containsKey("miid")) {
				try {
					long id = resp.getNumberLong("miid");
					long container_id = resp.getNumberLong("mcti");
					SongContainer sc = new SongContainer();
					sc.setContainerId(container_id);
					
					// Look for song in parent library first
					SongContainer parent_sc = playlist.getParentLibrarySongList().getSongByDatabaseId(id);
					if (parent_sc != null && parent_sc.getSong() != null) {
						sc.setSong(parent_sc.getSong());
					} else {
						Song song = new Song();
						
						song.setDatabaseItemId(resp.getNumberLong("miid"));
						song.setTitle(resp.getString("minm"));
						song.setArtist(resp.getString("asar"));
						song.setAlbum(resp.getString("asal"));
						song.setGenre(resp.getString("asgn"));
						song.setTime(resp.getNumberLong("astm"));
						song.setAlbumId(resp.getNumberString("asai"));
						song.setArtistId(resp.getNumberString("asri"));
						
						song.setSortArtist(song.getArtist());
						song.setSortAlbum(song.getAlbum());
						
						sc.setSong(song);
					}
					
					playlist.addSong(sc);
					loadedSongs ++;
					
					if (this.listener != null) {
						listener.onProgressChange(new SongListLoadProgressEvent(loadedSongs));
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		public void searchDone() {
			playlist.setStatus(PlaylistStatus.LOADED);
			if (playlist.getPlaylistId() == context.getPlayer().getPlayingPlaylistId()) {
				playlist.getSongList().selectSongIfExists(context.getPlayer().getPlayingSong());
			}
		}
	}
	
	public void readPlaylist(long playlistid, TagListener listener) {
		Log.d(TAG, " in readPlaylists");
		try {
			// http://192.168.254.128:3689/databases/36/containers/1234/items?session-id=2025037772&meta=dmap.itemname,dmap.itemid,daap.songartist,daap.songalbum,dmap.containeritemid,com.apple.tunes.has-video
	        byte[] raw = RequestHelper.request(String.format(
	        		"%s/databases/%d/containers/%s/items?session-id=%s&" +
	        		"meta=dmap.itemname,dmap.itemid,daap.songartist,daap.songartistid,daap.songalbumid," +
	        		"daap.songalbum,daap.songtime,dmap.containeritemid,com.apple.tunes.has-video",
	                session.getRequestBase(),
	                session.databaseId,
	                playlistid,
	                session.sessionId),
	                false);

	        // parse list, passing off events in the process
	        ResponseParser.performSearch(raw, listener, MLIT_PATTERN, false);

		} catch (Exception e) {
			Log.w(TAG, "readPlaylists Exception:" + e.getMessage());
		}
	}
	
	
	public void loadSongListForPlaylist(IPlaylist playlist, String host, int port, String proxyHost, String proxyPort,
			String password) throws FireflyClientException {
		loadSongListForPlaylist(playlist, host, port, proxyHost, proxyPort, password, null);
	}

	public void loadSongListForPlaylist(IPlaylist playlist, String host, int port, String proxyHost, String proxyPort,
			String password, SongListLoadProgressListener listener) throws FireflyClientException {
		
		TrackListener trackListener = new TrackListener(playlist, listener);
		playlist.setStatus(PlaylistStatus.LOADING);
		readPlaylist(playlist.getPlaylistId(), trackListener);
	}

	public int addStaticPlaylist(String playlistName, String host, int port, String proxyHost, String proxyPort,
			String password) throws FireflyClientException {
		return 0;
	}

	public void deleteStaticPlaylist(long playlistId, String host, int port, String proxyHost, String proxyPort,
			String password) throws FireflyClientException {
	}

	public void renameStaticPlaylist(long playlistId, String newPlaylistName, String host, int port, String proxyHost,
			String proxyPort, String password) throws FireflyClientException {
	}

	public void addSongsToStaticPlaylist(long playlistId, SongContainer[] songs, String host, int port,
			String proxyHost, String proxyPort, String password) throws FireflyClientException {
	}

	public void removeSongsFromStaticPlaylist(long playlistId, SongContainer[] songs, String host, int port,
			String proxyHost, String proxyPort, String password) throws FireflyClientException {
	}

	public boolean getUseHttpCompressionWhenPossible() {
		return true;
	}

	public void setUseHttpCompressionWhenPossible(boolean use) {
		;
	}
}