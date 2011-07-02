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
package net.firefly.client.model.data.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.Stack;
import java.util.TreeMap;

import net.firefly.client.gui.context.Context;
import net.firefly.client.model.data.SongContainer;
import net.firefly.client.model.data.sorting.song.ArtistAlbumComparator;
import net.firefly.client.player.PlayerMode;

public class SongList implements Cloneable {

	public static final SongList EMPTY_SONG_LIST = new SongList();
	
	protected static final Random generator = new Random();

	protected ArrayList<SongContainer> songs;

	private Stack<SongContainer> playedSongsStack;

	private Set<Integer> playedSongsIndexesSet;
	
	protected SortedMap<Long,SongContainer> songByDatabaseId;
	
	protected SortedMap<Long,SongContainer> songByContainerId;

	protected int index = 0;

	public SongList() {
		this.songs = new ArrayList<SongContainer>();
		this.playedSongsStack = new Stack<SongContainer>();
		this.playedSongsIndexesSet = new HashSet<Integer>();
		this.songByDatabaseId = new TreeMap<Long, SongContainer>();
		this.songByContainerId = new TreeMap<Long, SongContainer>();
	}
	
	public void add(SongContainer song) {
		this.songs.add(song);		
		this.songByDatabaseId.put(new Long(song.getSong().getDatabaseItemId()),song);
		this.songByContainerId.put(new Long(song.getContainerId()),song);
	}
	
	public void remove(SongContainer song) {
		this.songs.remove(song);
		this.songByDatabaseId.remove(new Long(song.getSong().getDatabaseItemId()));
		this.songByContainerId.remove(new Long(song.getContainerId()));
		int newSelectedIndex = Math.max(index, 0);
		newSelectedIndex = Math.min(newSelectedIndex, size()-1);
		index = newSelectedIndex;
	}

	public void sort() {
		Collections.sort(this.songs, ArtistAlbumComparator.getInstance());
	}

	public SongContainer get(int i) {
		return this.songs.get(i);
	}
	
	public SongContainer getSongByDatabaseId(long dbId) {
		return this.songByDatabaseId.get(new Long(dbId));
	}
	
	public SongContainer getSongByContainerId(long containerId) {
		return this.songByContainerId.get(new Long(containerId));
	}

	public int size() {
		return this.songs.size();
	}

	public Iterator<SongContainer> iterator() {
		return this.songs.iterator();
	}

	public void selectSong(int index) {
		this.index = index;
	}

	public void selectSongIfExists(SongContainer song) {
		int i = this.songs.indexOf(song);
		if (i > -1) {
			this.index = i;
		}
	}

   public int indexOf(SongContainer song) {
      return this.songs.indexOf(song);
   }
	
	public SongContainer selectedSong(Context context) {
		if (context.getPlayer() != null) {
			if (PlayerMode.MODE_SHUFFLE == context.getPlayer().getPlayerMode() && index == 0) {
				return nextRandomizedSong();
			}
		}
		if (this.songs.size() > 0) {
			try {
				SongContainer s = this.songs.get(index);
				playedSongsIndexesSet.add(new Integer(index));
				if (playedSongsStack.empty()) {
					playedSongsStack.push(s);
				} else {
					SongContainer lastSong = playedSongsStack.peek();
					if (!lastSong.equals(s)) {
						playedSongsStack.push(s);
					}
				}
				return s;
			} catch (Exception e){
				return null;
			}
		} else {
			return null;
		}
	}

	// -- navigation
	public SongContainer nextSong(Context context) {
		int viewIndex = context.getTableSorter().viewIndex(index);
		if (viewIndex < this.songs.size() - 1) {
			viewIndex++;
			index = context.getTableSorter().modelIndex(viewIndex);
			SongContainer s = this.songs.get(index);
			playedSongsIndexesSet.add(new Integer(index));
			if (playedSongsStack.empty()) {
				playedSongsStack.push(s);
			} else {
				SongContainer lastSong = playedSongsStack.peek();
				if (!lastSong.equals(s)) {
					playedSongsStack.push(s);
				}
			}
			return s;
			
		} else {
			return null;
		}
	}

	public SongContainer nextRandomizedSong() {
		if (playedSongsIndexesSet.size() == songs.size()){
			// -- all songs have already been played once;
			playedSongsIndexesSet.clear();
		}
		int newIndex = generator.nextInt(this.songs.size());
		if (songs.size() > 1) {
			while (playedSongsIndexesSet.contains(new Integer(newIndex)) || newIndex == index) {
				newIndex = generator.nextInt(this.songs.size());
			}
		}
		index = newIndex;
		playedSongsIndexesSet.add(new Integer(index));
		SongContainer s = this.songs.get(index);
		playedSongsStack.push(s);
		return s;
	}

	public SongContainer previousSong(Context context) {
		if (playedSongsStack.size() < 2) {
			if (!playedSongsStack.empty()){
				playedSongsStack.pop(); // -- remove last song
			}
			int viewIndex = context.getTableSorter().viewIndex(index);
			if (viewIndex > 0) {
				viewIndex--;
				index =  context.getTableSorter().modelIndex(viewIndex);
				return this.songs.get(index);
			} else {
				return null;
			}
		} else {
			playedSongsStack.pop(); // -- remove current song
			SongContainer s = playedSongsStack.pop();
			index = songs.indexOf(s);
			return s;
		}
	}

	public int getSelectedIndex(){
		return this.index;
	}
	
	public long getMaxDatabaseId(){
		return songByDatabaseId.lastKey().longValue();
	}
	// -- Object overriden methods

	public String toString() {
		StringBuffer sb = new StringBuffer();
		Iterator<SongContainer> it = songs.iterator();
		SongContainer s;
		while (it.hasNext()) {
			s = it.next();
			sb.append(s).append('\n');
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public Object clone() {
		SongList songList = new SongList();
		songList.index = index;
		songList.songs = (ArrayList<SongContainer>) songs.clone();
		return songList;
	}
}
