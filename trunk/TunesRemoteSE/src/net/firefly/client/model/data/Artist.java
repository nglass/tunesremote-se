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

public class Artist implements Comparable<Artist>, Cloneable, Serializable {

	private static final long serialVersionUID = -5166729311446676769L;

	private String artist;
	
	private String sortArtist;
	
	private String artistId;

	private boolean special;
	
	private static Collator collator = Collator.getInstance();

	private static final Set<String> UNKNOWN_ARTIST_STRINGS;

	static {
		collator.setStrength(Collator.PRIMARY);

		UNKNOWN_ARTIST_STRINGS = new TreeSet<String>(collator);
		UNKNOWN_ARTIST_STRINGS.add("unknown artist");
		UNKNOWN_ARTIST_STRINGS.add("unknown");
		UNKNOWN_ARTIST_STRINGS.add("artiste inconnu");
		UNKNOWN_ARTIST_STRINGS.add("inconnu");
	}
	
	public Artist(String artist, String sortArtist, String artistId, boolean special) {
		this.artist = artist;
		this.sortArtist = sortArtist;
		this.artistId = artistId;
		this.special = special;
	}

	public Artist(String artist, String sortArtist, String artistId) {
		this(artist,sortArtist,artistId,false);
	}

	/**
	 * @return Returns the artist.
	 */
	public String getArtist() {
		return artist;
	}
	
	/**
	 * @return Returns the sort artist.
	 */
	public String getSortArtist() {
		return sortArtist;
	}

	/**
	 * @return Returns the artist persistent id.
	 */
	public String getArtistId() {
		return artistId;
	}
	
	/**
	 * @param artist
	 *            The artist to set.
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}
	
	/**
	 * @param artist
	 *            The artist to set.
	 */
	public void setSortArtist(String sortArtist) {
		this.sortArtist = sortArtist;
	}

	/**
	 * @param artistId
	 *            The artistId to set.
	 */
	public void setArtistId(String artistId) {
		this.artistId = artistId;
	}
	
	public String toString() {
		return this.artist;
	}

	public int compareTo(Artist a) {
		return collator.compare(this.sortArtist, a.getSortArtist());
	}
	
	public boolean equals(Object o) {
		return this.artistId.equals(((Artist) o).getArtistId());
	}

	public Object clone(){
		return new Artist(this.artist, this.sortArtist, this.artistId);
	}

	public boolean isSpecial() {
		return special;
	}
	
	public static boolean isUnknownArtist(String s){
		if (UNKNOWN_ARTIST_STRINGS.contains(s)){
			return true;
		}
		return false;
	}
}