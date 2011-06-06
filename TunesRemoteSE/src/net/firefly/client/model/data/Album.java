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
package net.firefly.client.model.data;

import java.io.Serializable;
import java.text.Collator;
import java.util.Set;
import java.util.TreeSet;

public class Album implements Comparable<Album>, Cloneable, Serializable {

	private static final long serialVersionUID = 7154129399275145735L;

	private String album;
	
	private String sortAlbum;
	
	private String albumId;

	private boolean special;
	
	private static Collator collator = Collator.getInstance();

	private static final Set<String> UNKNOWN_ALBUM_STRINGS;

	static {
		collator.setStrength(Collator.PRIMARY);

		UNKNOWN_ALBUM_STRINGS = new TreeSet<String>(collator);
		UNKNOWN_ALBUM_STRINGS.add("unknown album");
		UNKNOWN_ALBUM_STRINGS.add("unknown");
		UNKNOWN_ALBUM_STRINGS.add("album inconnu");
		UNKNOWN_ALBUM_STRINGS.add("inconnu");
		UNKNOWN_ALBUM_STRINGS.add("titre inconnu");
		
	}

	public Album(String album, String sortAlbum, String albumId) {
		this(album, sortAlbum, albumId, false);
	}

	public Album(String album, String sortAlbum, String albumId, boolean special) {
		this.album = album;
		this.sortAlbum = sortAlbum;
		this.albumId = albumId;
		this.special = special;
	}

	/**
	 * @return Returns the album.
	 */
	public String getAlbum() {
		return album;
	}
	
	public String getAlbumId() {
		return albumId;
	}

	/**
	 * @return Returns the album.
	 */
	public String getSortAlbum() {
		return sortAlbum;
	}
	
	/**
	 * @param album
	 *            The album to set.
	 */
	public void setAlbum(String album) {
		this.album = album;
	}

	/**
	 * @param album
	 *            The album to set.
	 */
	public void setSortAlbum(String sortAlbum) {
		this.sortAlbum = sortAlbum;
	}
	
	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}
	
	public String toString() {
		return this.album;
	}

	public int compareTo(Album a) {
		return collator.compare(this.sortAlbum, a.getSortAlbum());
	}

	public boolean equals(Object o) {
		return this.albumId.equals(((Album) o).getAlbumId());
	}
	
	public Object clone() {
		return new Album(this.album, this.sortAlbum, this.albumId);
	}

	public boolean isSpecial() {
		return special;
	}

	public static boolean isUnknownAlbum(String s){
		if (UNKNOWN_ALBUM_STRINGS.contains(s)){
			return true;
		}
		return false;
	}
}