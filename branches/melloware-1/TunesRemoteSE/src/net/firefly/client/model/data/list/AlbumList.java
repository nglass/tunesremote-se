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
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import net.firefly.client.controller.ResourceManager;
import net.firefly.client.model.data.Album;

public class AlbumList implements Cloneable {

	protected ArrayList<Album> albums;

	protected Album allSelectedAlbums;
	
	protected Locale locale;

	public AlbumList(Locale locale) {
		this.locale = locale;
		this.albums = new ArrayList<Album>();
		String s = new String(ResourceManager.getLabel("list.album.all", locale) + " (0)");
		allSelectedAlbums = new Album(s, s, null, true);
		this.albums.add(allSelectedAlbums);

	}

	public void add(Album album) {
		this.albums.add(album);
		allSelectedAlbums.setAlbum(ResourceManager.getLabel("list.album.all", locale) + " (" + (this.albums.size() - 1) + ")");
	}

	public void addAll(Collection<Album> albums) {
		this.albums.addAll(albums);
		allSelectedAlbums.setAlbum(ResourceManager.getLabel("list.album.all", locale) + " (" + (this.albums.size() - 1) + ")");
	}

	public Album get(int i) {
		return (Album) this.albums.get(i);
	}

	public int size() {
		return this.albums.size();
	}

	public Iterator<Album> iterator() {
		return this.albums.iterator();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		Iterator<Album> it = albums.iterator();
		Album a;
		while (it.hasNext()) {
			a = it.next();
			sb.append(a).append('\n');
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public Object clone() {
		AlbumList albumList = new AlbumList(locale);
		albumList.albums = (ArrayList<Album>) albums.clone();
		albumList.allSelectedAlbums = allSelectedAlbums;
		return albumList;
	}

}
