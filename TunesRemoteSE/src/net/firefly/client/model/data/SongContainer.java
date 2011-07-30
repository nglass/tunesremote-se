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
package net.firefly.client.model.data;

public class SongContainer implements Cloneable {

   protected long databaseId = 0;
   protected long playlistId = 0;
	protected long containerId = 0;
	protected Song song = null;
	
	public long getContainerId() {
		return containerId;
	}

	public void setContainerId(long containerId) {
		this.containerId = containerId;
	}
	
	public long getPlaylistId() {
	   return playlistId;
	}
	
	public void setPlaylistId(long playlistId) {
	   this.playlistId = playlistId;
	}

   public long getDatabaseId() {
      return databaseId;
   }
   
   public void setDatabaseId(long databaseId) {
      this.databaseId = databaseId;
   }
	
	public Song getSong() {
		return song;
	}

	public void setSong(Song song) {
		this.song = song;
	}

	public SongContainer() {
		
	}
	
	public boolean equals(Object o) {
		SongContainer that;
		try {
			that = (SongContainer) o;
		} catch (Exception e) {
			return false;
		}
		if (o == null) {
			return false;
		}
		
		if (this.containerId == that.containerId &&
		    this.playlistId == that.playlistId &&
		    this.databaseId == that.databaseId &&
		    (this.song == that.song || 
			 (this.song != null && that.song != null &&
			  this.song.getDatabaseItemId() == that.song.getDatabaseItemId()))) {
			return true;
		}
		return false;
	}
	
	public String toString() {
	   return this.song.getTitle();
	}
	
	public Object clone() {
		SongContainer sc = new SongContainer();
		sc.databaseId = databaseId;
		sc.playlistId = playlistId;
		sc.containerId = containerId;
		sc.song = (Song) song.clone();

		return sc;
	}
	
}
