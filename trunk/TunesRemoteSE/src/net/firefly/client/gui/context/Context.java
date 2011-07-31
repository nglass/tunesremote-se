/*
 * This file is part of FireflyClient.
 *
 * FireflyClient is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * any later version.
 *
 * FireflyClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FireflyClient; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright 2007 Vincent Cariven
 */
package net.firefly.client.gui.context;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import org.tunesremote.daap.Session;

import net.firefly.client.controller.request.IRequestManager;
import net.firefly.client.controller.request.PlaylistRequestManager;
import net.firefly.client.gui.context.events.ContextResetEvent;
import net.firefly.client.gui.context.events.FilteredAlbumListChangedEvent;
import net.firefly.client.gui.context.events.FilteredArtistListChangedEvent;
import net.firefly.client.gui.context.events.FilteredGenreListChangedEvent;
import net.firefly.client.gui.context.events.FilteredSongListChangedEvent;
import net.firefly.client.gui.context.events.GlobalSongListChangedEvent;
import net.firefly.client.gui.context.events.InterfaceLockEvent;
import net.firefly.client.gui.context.events.PlaylistListChangedEvent;
import net.firefly.client.gui.context.events.SavedLibraryListChangedEvent;
import net.firefly.client.gui.context.events.SelectedPlaylistChangedEvent;
import net.firefly.client.gui.context.events.StaticPlaylistCreationEvent;
import net.firefly.client.gui.context.listeners.ContextResetEventListener;
import net.firefly.client.gui.context.listeners.FilteredAlbumListChangedEventListener;
import net.firefly.client.gui.context.listeners.FilteredArtistListChangedEventListener;
import net.firefly.client.gui.context.listeners.FilteredGenreListChangedEventListener;
import net.firefly.client.gui.context.listeners.FilteredSongListChangedEventListener;
import net.firefly.client.gui.context.listeners.GlobalSongListChangedEventListener;
import net.firefly.client.gui.context.listeners.InterfaceLockEventListener;
import net.firefly.client.gui.context.listeners.PlaylistListChangedEventListener;
import net.firefly.client.gui.context.listeners.RadiolistListChangedEventListener;
import net.firefly.client.gui.context.listeners.SavedLibraryListChangedEventListener;
import net.firefly.client.gui.context.listeners.SelectedPlaylistChangedEventListener;
import net.firefly.client.gui.context.listeners.StaticPlaylistCreationEventListener;
import net.firefly.client.gui.swing.panel.GlobalContainer;
import net.firefly.client.gui.swing.table.model.TableSorter;
import net.firefly.client.model.configuration.Configuration;
import net.firefly.client.model.data.Album;
import net.firefly.client.model.data.Artist;
import net.firefly.client.model.data.Genre;
import net.firefly.client.model.data.list.AlbumList;
import net.firefly.client.model.data.list.ArtistList;
import net.firefly.client.model.data.list.GenreList;
import net.firefly.client.model.data.list.SongList;
import net.firefly.client.model.library.LibraryInfo;
import net.firefly.client.model.playlist.IPlaylist;
import net.firefly.client.model.playlist.list.PlaylistList;
import net.firefly.client.model.playlist.list.RadiolistList;
import net.firefly.client.player.MediaPlayer;

public class Context {

	protected Configuration config;

	protected LibraryInfo libraryInfo;
	
	protected Session session;

	protected PlaylistList playlists;
	
	protected RadiolistList radiolists;

	protected IPlaylist selectedPlaylist;

	protected GenreList globalGenreList;

	protected GenreList filteredGenreList;

	protected ArtistList globalArtistList;

	protected ArtistList filteredArtistList;

	protected AlbumList globalAlbumList;

	protected AlbumList filteredAlbumList;

	protected SongList masterSongList;

	protected SongList globalSongList;

	protected SongList filteredSongList;

	protected MediaPlayer player;

	protected Genre[] selectedGenres;

	protected Artist[] selectedArtists;
	
	protected Album[] selectedAlbums;

	protected boolean allGenresSelected = false;

	protected boolean allArtistsSelected = false;
	
	protected boolean allAlbumsSelected = false;

	protected boolean compilationSelected = false;

	protected String[] savedLibrariesList;

	protected String searchmask;

	protected String serverVersion;

	protected TableSorter tableSorter;

	protected GlobalContainer globalContainer;

	protected boolean isApplet = false;
	
	protected boolean appletActive = false;
	
	protected boolean autoload = true;
	
	protected EventListenerList listenerList = new EventListenerList();

	private PlaylistRequestManager playlistRequestManager;

	private IRequestManager mainRequestManager;

	public Context(Configuration config) {
		this.config = config;
		this.playlists = PlaylistList.EMPTY_PLAYLIST_LIST;
		this.radiolists = new RadiolistList();
		this.selectedPlaylist = null;
		this.globalSongList = SongList.EMPTY_SONG_LIST;
		this.globalGenreList = new GenreList(config.getLocale());
		this.globalArtistList = new ArtistList(config.getLocale());
		this.globalAlbumList = new AlbumList(config.getLocale());
		this.filteredSongList = SongList.EMPTY_SONG_LIST;
		this.filteredGenreList = new GenreList(config.getLocale());
		this.filteredArtistList = new ArtistList(config.getLocale());
		this.filteredAlbumList = new AlbumList(config.getLocale());
		this.libraryInfo = new LibraryInfo();
		this.savedLibrariesList = new String[0];
		this.searchmask = null;
		this.serverVersion = null;
	}

	public Configuration getConfig() {
		return config;
	}

	public void reset() {
		setLibraryInfo(null);
		setPlaylists(PlaylistList.EMPTY_PLAYLIST_LIST);
		this.radiolists = new RadiolistList();
		setGlobalGenreList(new GenreList(getConfig().getLocale()));
		setGlobalArtistList(new ArtistList(getConfig().getLocale()));
		setGlobalAlbumList(new AlbumList(getConfig().getLocale()));
		setGlobalSongList(SongList.EMPTY_SONG_LIST);
		setFilteredGenreList(new GenreList(getConfig().getLocale()));
		setFilteredArtistList(new ArtistList(getConfig().getLocale()));
		setFilteredAlbumList(new AlbumList(getConfig().getLocale()));
		setFilteredSongList(SongList.EMPTY_SONG_LIST);
		this.searchmask = null;
		this.serverVersion = null;
		if (this.player != null) {
			player.stopPlayback();
		}
		fireContextReset(new ContextResetEvent());
	}

	// -- GETTER(S) - SETTER(S)
	public GlobalContainer getGlobalContainer() {
		return globalContainer;
	}

	public void setGlobalContainer(GlobalContainer globalContainer) {
		this.globalContainer = globalContainer;
	}

	public TableSorter getTableSorter() {
		return tableSorter;
	}

	public void setTableSorter(TableSorter tableSorter) {
		this.tableSorter = tableSorter;
	}

	public String getServerVersion() {
		return serverVersion;
	}

	public void setServerVersion(String serverVersion) {
		this.serverVersion = serverVersion;
	}

	public void setConfig(Configuration config) {
		this.config = config;
	}

	public AlbumList getFilteredAlbumList() {
		return filteredAlbumList;
	}

	public void setFilteredAlbumList(AlbumList filteredAlbumList) {
		this.filteredAlbumList = filteredAlbumList;
		fireFilteredAlbumListChange(new FilteredAlbumListChangedEvent(filteredAlbumList));
	}

	public ArtistList getFilteredArtistList() {
		return filteredArtistList;
	}

	public void setFilteredArtistList(ArtistList filteredArtistList) {
		this.filteredArtistList = filteredArtistList;
		fireFilteredArtistListChange(new FilteredArtistListChangedEvent(filteredArtistList));
	}

	public SongList getFilteredSongList() {
		return filteredSongList;
	}

	public void setFilteredSongList(SongList filteredSongList) {
		this.filteredSongList = filteredSongList;
		fireFilteredSongListChange(new FilteredSongListChangedEvent(filteredSongList));
	}

	public AlbumList getGlobalAlbumList() {
		return globalAlbumList;
	}

	public void setGlobalAlbumList(AlbumList globalAlbumList) {
		this.globalAlbumList = globalAlbumList;
	}

	public ArtistList getGlobalArtistList() {
		return globalArtistList;
	}

	public void setGlobalArtistList(ArtistList globalArtistList) {
		this.globalArtistList = globalArtistList;
	}

	public GenreList getGlobalGenreList() {
		return globalGenreList;
	}

	public void setGlobalGenreList(GenreList globalGenreList) {
		this.globalGenreList = globalGenreList;
	}

	public GenreList getFilteredGenreList() {
		return filteredGenreList;
	}

	public void setFilteredGenreList(GenreList filteredGenreList) {
		this.filteredGenreList = filteredGenreList;
		fireFilteredGenreListChange(new FilteredGenreListChangedEvent(filteredGenreList));
	}

	public SongList getMasterSongList() {
		return masterSongList;
	}

	public void setMasterSongList(SongList masterSongList) {
		this.masterSongList = masterSongList;
	}

	public SongList getGlobalSongList() {
		return globalSongList;
	}

	public void setGlobalSongList(SongList globalSongList) {
		this.globalSongList = globalSongList;
		fireGlobalSongListChange(new GlobalSongListChangedEvent(globalSongList));
	}

	public MediaPlayer getPlayer() {
		return player;
	}

	public void setPlayer(MediaPlayer player) {
		this.player = player;
	}

	public Genre[] getSelectedGenres() {
		return selectedGenres;
	}

	public void setSelectedGenres(Genre[] selectedGenres) {
		this.selectedGenres = selectedGenres;
	}

	public Artist[] getSelectedArtists() {
		return selectedArtists;
	}

	public void setSelectedArtists(Artist[] selectedArtists) {
		this.selectedArtists = selectedArtists;
	}
	
	public Album[] getSelectedAlbums() {
		return selectedAlbums;
	}

	public void setSelectedAlbums(Album[] selectedAlbums) {
		this.selectedAlbums = selectedAlbums;
	}

	public boolean isAllAlbumsSelected() {
		return allAlbumsSelected;
	}

	public void setAllAlbumsSelected(boolean allAlbumsSelected) {
		this.allAlbumsSelected = allAlbumsSelected;
	}
	
	public boolean isAllArtistsSelected() {
		return allArtistsSelected;
	}

	public void setAllArtistsSelected(boolean allArtistsSelected) {
		this.allArtistsSelected = allArtistsSelected;
	}

	public boolean isAllGenresSelected() {
		return allGenresSelected;
	}

	public void setAllGenresSelected(boolean allGenresSelected) {
		this.allGenresSelected = allGenresSelected;
	}

	public boolean isCompilationSelected() {
		return compilationSelected;
	}

	public void setCompilationSelected(boolean compilationSelected) {
		this.compilationSelected = compilationSelected;
	}

	public String[] getSavedLibrariesList() {
		return savedLibrariesList;
	}

	public void setSavedLibrariesList(String[] savedLibrariesList) {
		this.savedLibrariesList = savedLibrariesList;
		fireSavedLibraryListChange(new SavedLibraryListChangedEvent(savedLibrariesList));
	}

	public LibraryInfo getLibraryInfo() {
		return libraryInfo;
	}

	public void setLibraryInfo(LibraryInfo libraryInfo) {
		this.libraryInfo = libraryInfo;
	}

	public Session getSession() {
      return session;
   }

   public void setSession(Session session) {
      this.session = session;
   }

   public String getSearchmask() {
		return searchmask;
	}

	public void setSearchmask(String searchmask) {
		this.searchmask = searchmask;
	}

	public PlaylistList getPlaylists() {
		return playlists;
	}
	
	public RadiolistList getRadiolists() {
	   return radiolists;
	}

	public void setPlaylists(final PlaylistList playlists) {
		final Context c = this;

      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            c.playlists = playlists;
            firePlaylistListChange(new PlaylistListChangedEvent(playlists));
         }
      });
	}

	public IPlaylist getSelectedPlaylist() {
		return selectedPlaylist;
	}

	public void setSelectedPlaylist(IPlaylist selectedPlaylist) {
		this.selectedPlaylist = selectedPlaylist;
		fireSelectedPlaylistChange(new SelectedPlaylistChangedEvent(selectedPlaylist));
	}

	public void createStaticPlaylist() {
		fireStaticPlaylistCreation(new StaticPlaylistCreationEvent());
	}

	
	public boolean isApplet() {
		return isApplet;
	}

	public void setApplet(boolean isApplet) {
		this.isApplet = isApplet;
	}

	public boolean isAutoload() {
		return autoload;
	}

	public void setAutoload(boolean autoload) {
		this.autoload = autoload;
	}
	
	public boolean isAppletActive() {
		return appletActive;
	}

	public void setAppletActive(boolean appletActive) {
		this.appletActive = appletActive;
	}
	
	// -- EVENTS

	// -- look and feel
	public void addFilteredAlbumListChangedEventListener(FilteredAlbumListChangedEventListener listener) {
		listenerList.add(FilteredAlbumListChangedEventListener.class, listener);
	}

	public void removeFilteredAlbumListChangedEventListener(FilteredAlbumListChangedEventListener listener) {
		listenerList.remove(FilteredAlbumListChangedEventListener.class, listener);
	}

	protected void fireFilteredAlbumListChange(FilteredAlbumListChangedEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == FilteredAlbumListChangedEventListener.class) {
				((FilteredAlbumListChangedEventListener) listeners[i + 1]).onFilteredAlbumListChange(e);
			}
		}
	}

	public void addFilteredArtistListChangedEventListener(FilteredArtistListChangedEventListener listener) {
		listenerList.add(FilteredArtistListChangedEventListener.class, listener);
	}

	public void removeFilteredArtistListChangedEventListener(FilteredArtistListChangedEventListener listener) {
		listenerList.remove(FilteredArtistListChangedEventListener.class, listener);
	}

	protected void fireFilteredArtistListChange(FilteredArtistListChangedEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == FilteredArtistListChangedEventListener.class) {
				((FilteredArtistListChangedEventListener) listeners[i + 1]).onFilteredArtistListChange(e);
			}
		}
	}

	public void addFilteredGenreListChangedEventListener(FilteredGenreListChangedEventListener listener) {
		listenerList.add(FilteredGenreListChangedEventListener.class, listener);
	}

	public void removeFilteredGenreListChangedEventListener(FilteredGenreListChangedEventListener listener) {
		listenerList.remove(FilteredGenreListChangedEventListener.class, listener);
	}

	protected void fireFilteredGenreListChange(FilteredGenreListChangedEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == FilteredGenreListChangedEventListener.class) {
				((FilteredGenreListChangedEventListener) listeners[i + 1]).onFilteredGenreListChange(e);
			}
		}
	}

	public void addFilteredSongListChangedEventListener(FilteredSongListChangedEventListener listener) {
		listenerList.add(FilteredSongListChangedEventListener.class, listener);
	}

	public void removeFilteredSongListChangedEventListener(FilteredSongListChangedEventListener listener) {
		listenerList.remove(FilteredSongListChangedEventListener.class, listener);
	}

	protected void fireFilteredSongListChange(FilteredSongListChangedEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == FilteredSongListChangedEventListener.class) {
				((FilteredSongListChangedEventListener) listeners[i + 1]).onFilteredSongListChange(e);
			}
		}
	}

	public void addGlobalSongListChangedEventListener(GlobalSongListChangedEventListener listener) {
		listenerList.add(GlobalSongListChangedEventListener.class, listener);
	}

	public void removeGlobalSongListChangedEventListener(GlobalSongListChangedEventListener listener) {
		listenerList.remove(GlobalSongListChangedEventListener.class, listener);
	}

	protected void fireGlobalSongListChange(GlobalSongListChangedEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == GlobalSongListChangedEventListener.class) {
				((GlobalSongListChangedEventListener) listeners[i + 1]).onGlobalSongListChange(e);
			}
		}
	}

	public void addSavedLibraryListChangedEventListener(SavedLibraryListChangedEventListener listener) {
		listenerList.add(SavedLibraryListChangedEventListener.class, listener);
	}

	public void removeSavedLibraryListChangedEventListener(SavedLibraryListChangedEventListener listener) {
		listenerList.remove(SavedLibraryListChangedEventListener.class, listener);
	}

	protected void fireSavedLibraryListChange(SavedLibraryListChangedEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == SavedLibraryListChangedEventListener.class) {
				((SavedLibraryListChangedEventListener) listeners[i + 1]).onSavedLibraryListChange(e);
			}
		}
	}

	public void addPlaylistListChangedEventListener(PlaylistListChangedEventListener listener) {
		listenerList.add(PlaylistListChangedEventListener.class, listener);
	}

	public void removePlaylistListChangedEventListener(PlaylistListChangedEventListener listener) {
		listenerList.remove(PlaylistListChangedEventListener.class, listener);
	}

	protected void firePlaylistListChange(PlaylistListChangedEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == PlaylistListChangedEventListener.class) {
				((PlaylistListChangedEventListener) listeners[i + 1]).onPlaylistListChange(e);
			}
		}
	}

   public void addRadiolistListChangedEventListener(RadiolistListChangedEventListener listener) {
      listenerList.add(RadiolistListChangedEventListener.class, listener);
   }

   public void removeRadiolistListChangedEventListener(RadiolistListChangedEventListener listener) {
      listenerList.remove(RadiolistListChangedEventListener.class, listener);
   }

   public void fireRadiolistListChange(SelectedPlaylistChangedEvent e) {
      Object[] listeners = listenerList.getListenerList();
      for (int i = 0; i < listeners.length; i += 2) {
         if (listeners[i] == RadiolistListChangedEventListener.class) {
            ((RadiolistListChangedEventListener) listeners[i + 1]).onRadiolistListChange(e);
         }
      }
   }
	
	public void addSelectedPlaylistChangedEventListener(SelectedPlaylistChangedEventListener listener) {
		listenerList.add(SelectedPlaylistChangedEventListener.class, listener);
	}

	public void removeSelectedPlaylistChangedEventListener(SelectedPlaylistChangedEventListener listener) {
		listenerList.remove(SelectedPlaylistChangedEventListener.class, listener);
	}

	protected void fireSelectedPlaylistChange(SelectedPlaylistChangedEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == SelectedPlaylistChangedEventListener.class) {
				((SelectedPlaylistChangedEventListener) listeners[i + 1]).onSelectedPlaylistChange(e);
			}
		}
	}

	public void addStaticPlaylistCreationEventListener(StaticPlaylistCreationEventListener listener) {
		listenerList.add(StaticPlaylistCreationEventListener.class, listener);
	}

	public void removeStaticPlaylistCreationEventListener(StaticPlaylistCreationEventListener listener) {
		listenerList.remove(StaticPlaylistCreationEventListener.class, listener);
	}

	protected void fireStaticPlaylistCreation(StaticPlaylistCreationEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == StaticPlaylistCreationEventListener.class) {
				((StaticPlaylistCreationEventListener) listeners[i + 1]).onStaticPlaylistCreation(e);
			}
		}
	}

	public void addContextResetEventListener(ContextResetEventListener listener) {
		listenerList.add(ContextResetEventListener.class, listener);
	}

	public void removeContextResetEventListener(ContextResetEventListener listener) {
		listenerList.remove(ContextResetEventListener.class, listener);
	}

	protected void fireContextReset(ContextResetEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == ContextResetEventListener.class) {
				((ContextResetEventListener) listeners[i + 1]).onContextReset(e);
			}
		}
	}
	
	public void addInterfaceLockEventListener(InterfaceLockEventListener listener) {
		listenerList.add(InterfaceLockEventListener.class, listener);
	}

	public void removeInterfaceLockEventListener(InterfaceLockEventListener listener) {
		listenerList.remove(InterfaceLockEventListener.class, listener);
	}

	public void fireInterfaceLockChange(InterfaceLockEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == InterfaceLockEventListener.class) {
				((InterfaceLockEventListener) listeners[i + 1]).onInterfaceLockChange(e);
			}
		}
	}

	public void setPlaylistRequestManager(
			PlaylistRequestManager playlistRequestManager) {
		this.playlistRequestManager = playlistRequestManager;	
	}

	public PlaylistRequestManager getPlaylistRequestManager() {
		return this.playlistRequestManager;
	}

	public void setMainRequestManager(IRequestManager mainRequestManager) {
		this.mainRequestManager = mainRequestManager;
	}

	public IRequestManager getMainRequestManager() {
		return mainRequestManager;
	}

}
