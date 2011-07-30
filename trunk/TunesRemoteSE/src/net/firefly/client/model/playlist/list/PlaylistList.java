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
package net.firefly.client.model.playlist.list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import net.firefly.client.model.playlist.IPlaylist;

public class PlaylistList {

	public static final PlaylistList EMPTY_PLAYLIST_LIST = new PlaylistList();

	public static final int SPECIAL_MUSIC = 6;
	public static final int SPECIAL_RENTALS = 10;
	public static final int SPECIAL_MOVIES = 4;
	public static final int SPECIAL_TV = 5;
	public static final int SPECIAL_PODCASTS = 1;
	public static final int SPECIAL_ITUNES_U = 13;
	public static final int SPECIAL_BOOKS = 7;
	public static final int SPECIAL_PURCHASED = 8;
	public static final int SPECIAL_GENIUS = 12;
	public static final int SPECIAL_GENIUS_MIXES = 15;
	public static final int SPECIAL_MIX = 16;
	public static final int SPECIAL_ITUNES_DJ = 2;
	   
	protected ArrayList<IPlaylist> specialPlaylists;
	
	protected ArrayList<IPlaylist> geniusPlaylists;
	
	protected ArrayList<IPlaylist> playlists;
	
	protected SortedMap<Long,IPlaylist> playlistById;

	protected int index = 0;
	
	public PlaylistList() {
	   this.specialPlaylists = new ArrayList<IPlaylist>();
	   this.geniusPlaylists = new ArrayList<IPlaylist>();
		this.playlists = new ArrayList<IPlaylist>();
		this.playlistById = new TreeMap<Long,IPlaylist>();
	}

   public IPlaylist getPlaylistById(long containerId) {
      return this.playlistById.get(new Long(containerId));
   }
   
	public void add(IPlaylist playlist) {
	   int special = (int) playlist.getSpecialPlaylist();
	   long parentId = playlist.getParentContainer();
	   if (parentId > 0) {
	      IPlaylist parent = getPlaylistById(parentId);
	      if (parent != null) {
	         parent.addNested(playlist);
	      }
	   } else if (playlist.isSavedGenius()) {
         this.geniusPlaylists.add(playlist);
         
	   } else if (playlist.getSpecialPlaylist() > 0) {
	      switch (special) {
	      case SPECIAL_GENIUS:
	      case SPECIAL_GENIUS_MIXES:
	         this.geniusPlaylists.add(playlist);
	         break;
	         
	      case SPECIAL_ITUNES_DJ:
	         this.playlists.add(0, playlist);
	         break;
	      
	      default:
	         this.specialPlaylists.add(playlist);
            break;
	      }
		} else {
		   this.playlists.add(playlist);
		}
		this.playlistById.put(new Long(playlist.getPlaylistId()),playlist);
	}
	
	public void remove(IPlaylist playlist) {
	   this.playlistById.remove(new Long(playlist.getPlaylistId()));

		int special = (int) playlist.getSpecialPlaylist();
		long parentId = playlist.getParentContainer();
		if (parentId > 0) {
         IPlaylist parent = getPlaylistById(parentId);
         if (parent != null) {
            parent.removeNested(playlist);
         }
		} else if (playlist.isSavedGenius()) {
         this.geniusPlaylists.remove(playlist);
         
      } else if (playlist.getSpecialPlaylist() > 0) {
         switch (special) { 
         case SPECIAL_GENIUS:
         case SPECIAL_GENIUS_MIXES:
            this.geniusPlaylists.remove(playlist);
            break;
            
         case SPECIAL_ITUNES_DJ:
            this.playlists.remove(playlist);
            break;
            
         default:
            this.specialPlaylists.remove(playlist);
            break;  
         }
      } else {
         this.playlists.remove(playlist);
      }
	}
	
	// Regular Playlists
	public int playlistsSize() {
		return this.playlists.size();
	}

   public IPlaylist getPlaylist(int i) {
      return this.playlists.get(i);
   }
	
	public Iterator<IPlaylist> playlistIterator() {
		return this.playlists.iterator();
	}

   public int indexOfPlaylist(IPlaylist playlist) {
      return this.playlists.indexOf(playlist);
   }

   // Special Playlists
   public int specialPlaylistsSize() {
      return this.specialPlaylists.size();
   }

   public IPlaylist getSpecialPlaylist(int i) {
      return this.specialPlaylists.get(i);
   }
   
   public Iterator<IPlaylist> specialPlaylistIterator() {
      return this.specialPlaylists.iterator();
   }

   public int indexOfSpecialPlaylist(IPlaylist playlist) {
      return this.specialPlaylists.indexOf(playlist);
   }
   
   // Genius Playlists
   public int geniusPlaylistsSize() {
      return this.geniusPlaylists.size();
   }

   public IPlaylist getGeniusPlaylist(int i) {
      return this.geniusPlaylists.get(i);
   }
   
   public Iterator<IPlaylist> geniusPlaylistIterator() {
      return this.geniusPlaylists.iterator();
   }

   public int indexOfGeniusPlaylist(IPlaylist playlist) {
      return this.geniusPlaylists.indexOf(playlist);
   }
   
	//public void sort() {
	//	Collections.sort(playlists, PlaylistComparator.getInstance());
	//}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		Iterator<IPlaylist> it = playlists.iterator();
		IPlaylist p;
		while (it.hasNext()) {
			p = it.next();
			sb.append(p).append('\n');
		}
		return sb.toString();
	}
}
