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
package net.firefly.client.model.playlist;

import java.util.ArrayList;
import java.util.Iterator;

import net.firefly.client.model.data.SongContainer;
import net.firefly.client.model.data.list.SongList;

public abstract class AbstractPlaylist implements IPlaylist {

	protected SongList parentLibrarySongList;

	protected SongList songList;

	protected long databaseId;
	
	protected long playlistId;
	
	protected long parentContainer;
	
	protected long specialPlaylist;
	
	protected boolean savedGenius;

	protected String playlistName;
	
	protected String persistentId;
	
	protected PlaylistStatus status;
	
	protected ArrayList<IPlaylist> nested;

	public AbstractPlaylist(SongList parentLibrarySongList) {
		this.parentLibrarySongList = parentLibrarySongList;
		this.songList = SongList.EMPTY_SONG_LIST;
		this.status = PlaylistStatus.NOT_LOADED;
		this.playlistId = -1;
		this.nested = new ArrayList<IPlaylist>();
	}

	public SongList getParentLibrarySongList() {
		return this.parentLibrarySongList;
	}

	public SongList getSongList() {
		return this.songList;
	}

   public long getDatabaseId() {
      return this.databaseId;
   }

   public void setDatabaseId(long databaseId) {
      this.databaseId = databaseId;
   }
	
	public long getPlaylistId() {
		return this.playlistId;
	}

	public void setPlaylistId(long playlistId) {
		this.playlistId = playlistId;
	}
	
	public long getParentContainer() {
	   return this.parentContainer;
	}
	
	public void setParentContainer(long parentContainer) {
	   this.parentContainer = parentContainer;
	}

	public long getSpecialPlaylist() {
	   return this.specialPlaylist;
	}
	
	public void setSpecialPlaylist(long specialPlaylist) {
	   this.specialPlaylist = specialPlaylist;
	}
	
	public boolean isSavedGenius() {
	   return this.savedGenius;
	}
	
	public void setSavedGenius(boolean savedGenius) {
	   this.savedGenius = savedGenius;
	}
	
	public String getPlaylistName() {
		return this.playlistName;
	}

	public void setPlaylistName(String playlistName) {
		this.playlistName = playlistName;
	}

	public String getPersistentId() {
		return this.persistentId;
	}
	
	public void setPersistentId(String persistentId) {
		this.persistentId = persistentId;
	}
	
	public void addSong(SongContainer s) {
		this.songList.add(s);
	}
	

	public PlaylistStatus getStatus() {
		return status;
	}

	public boolean equals(Object o) {
		AbstractPlaylist p;
		try {
			p = (AbstractPlaylist) o;
		} catch (Exception e) {
			return false;
		}
		if (o == null) {
			return false;
		}
		if (this.playlistId == p.playlistId
				&& this.playlistName == p.playlistName
				&& ((this.playlistName != null && this.playlistName.equals(p.getPlaylistName())) || (this.playlistName == null && p
						.getPlaylistName() == null))) {
			return true;
		}

		return false;
	}
	
	public void setStatus(PlaylistStatus status) {
		this.status = status;
		if (PlaylistStatus.NOT_LOADED.equals(status)) {
			this.songList = new SongList();
		}
	}
	
	public String toString(){
		return this.playlistName;
	}

   // Nested playlists
   public int nestedSize() {
      return this.nested.size();
   }

   public IPlaylist getNested(int i) {
      return this.nested.get(i);
   }
   
   public Iterator<IPlaylist> nestedIterator() {
      return this.nested.iterator();
   }

   public int nestedIndexOf(IPlaylist playlist) {
      return this.nested.indexOf(playlist);
   }
   
   public void addNested(IPlaylist playlist) {
      this.nested.add(playlist);
   }
   
   public void removeNested(IPlaylist playlist) {
      this.nested.remove(playlist);
   }
	
}
