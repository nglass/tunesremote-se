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

import java.util.Iterator;

import net.firefly.client.model.data.SongContainer;
import net.firefly.client.model.data.list.SongList;

public interface IPlaylist {
	
	public SongList getParentLibrarySongList();

	public SongList getSongList();

	public String getPersistentId();
	
	public void setPersistentId(String persistentId);

	public long getDatabaseId();
	
	public void setDatabaseId(long id);
	
	public long getPlaylistId();

	public void setPlaylistId(long id);

	public long getParentContainer();
	
	public void setParentContainer(long parentContainer);
	
	public long getSpecialPlaylist();
	
	public void setSpecialPlaylist(long specialPlaylist);
	
	public boolean isSavedGenius();
	
	public void setSavedGenius(boolean savedGenius);
	
	public String getPlaylistName();

	public void setPlaylistName(String playlistName);

	public PlaylistType getPlaylistType();

	public void addSong(SongContainer s);
	
	public PlaylistStatus getStatus();
	
	public void setStatus(PlaylistStatus status);
	
	// Nested playlists
   public int nestedSize();

   public IPlaylist getNested(int i);
   
   public Iterator<IPlaylist> nestedIterator();

   public int nestedIndexOf(IPlaylist playlist);
   
   public void addNested(IPlaylist playlist);
   
   public void removeNested(IPlaylist playlist);
}
