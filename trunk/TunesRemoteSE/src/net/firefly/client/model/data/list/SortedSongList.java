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

import net.firefly.client.model.data.SongContainer;
import net.firefly.client.model.data.sorting.song.ArtistAlbumComparator;

public class SortedSongList extends SongList {

	public void add(SongContainer song) {
		int insertionPoint = Collections.binarySearch(this.songs, song, ArtistAlbumComparator.getInstance());
		if (insertionPoint < 0){
			insertionPoint = - insertionPoint - 1;
		}
		this.songs.add(insertionPoint, song);
		this.songByDatabaseId.put(new Long(song.getSong().getDatabaseItemId()),song);
		this.songByContainerId.put(new Long(song.getContainerId()),song);
	}
	
	public void sort() {
		// do nothing: the list is already sorted by the call to add() method
	}
	
	@SuppressWarnings("unchecked")
	public Object clone() {
		SongList songList = new SortedSongList();
		songList.index = index;
		songList.songs = (ArrayList<SongContainer>) songs.clone();
		return songList;
	}

}
