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
import java.util.Collections;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import net.firefly.client.model.playlist.IPlaylist;
import net.firefly.client.model.playlist.sorting.PlaylistComparator;

public class PlaylistList {

	public static final PlaylistList EMPTY_PLAYLIST_LIST = new PlaylistList();

	protected ArrayList<IPlaylist> playlists;
	
	protected SortedMap<Long,IPlaylist> playlistById;

	protected int index = 0;

	public PlaylistList() {
		this.playlists = new ArrayList<IPlaylist>();
		this.playlistById = new TreeMap<Long,IPlaylist>();
	}

	public void add(IPlaylist playlist) {
		int insertionPoint = Collections.binarySearch(this.playlists, playlist, PlaylistComparator.getInstance());
		if (insertionPoint < 0) {
			insertionPoint = -insertionPoint - 1;
		}
		this.playlists.add(insertionPoint, playlist);
		this.playlistById.put(new Long(playlist.getPlaylistId()),playlist);
	}
	
	public void remove(IPlaylist playlist) {
		this.playlists.remove(playlist);
		this.playlistById.remove(new Long(playlist.getPlaylistId()));
	}

	public IPlaylist get(int i) {
		return this.playlists.get(i);
	}

	public IPlaylist getPlaylistById(long containerId) {
		return this.playlistById.get(new Long(containerId));
	}
	
	public int size() {
		return this.playlists.size();
	}

	public Iterator<IPlaylist> iterator() {
		return this.playlists.iterator();
	}

	public void selectPlaylist(int index) {
		this.index = index;
	}

	public void selectPlaylistIfExists(IPlaylist playlist) {
		int i = this.playlists.indexOf(playlist);
		if (i > -1) {
			this.index = i;
		}
	}

	public IPlaylist selectedPlaylist() {
		if (this.playlists.size() > 0) {
			IPlaylist p = this.playlists.get(index);
			return p;
		} else {
			return null;
		}
	}

	public int getSelectedIndex() {
		return this.index;
	}
	
	public void sort() {
		Collections.sort(playlists, PlaylistComparator.getInstance());
	}

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
