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
import net.firefly.client.model.data.Artist;

public class ArtistList implements Cloneable {

	protected ArrayList<Artist> artists;

	protected Artist allSelectedArtists;

	protected Artist compilation;

	protected Locale locale;

	protected boolean compilationAdded = false;

	public ArtistList(Locale locale) {
		this.locale = locale;
		this.artists = new ArrayList<Artist>();
		String allSelectedArtistsString = ResourceManager.getLabel("list.artist.all", locale) + " (0)";
		allSelectedArtists = new Artist(allSelectedArtistsString, allSelectedArtistsString, "0", true);
		String compilationString = ResourceManager.getLabel("list.artist.compilation", locale);
		compilation = new Artist(compilationString, compilationString, "1", true);
		this.artists.add(allSelectedArtists);
	}

	public void addCompilation() {
		if (!compilationAdded) {
			this.artists.add(1, compilation);
			compilationAdded = true;
		}
	}

	public void add(Artist artist) {
		this.artists.add(artist);
		allSelectedArtists.setArtist(ResourceManager.getLabel("list.artist.all", locale) + " (" + (this.artists.size() - 1) + ")");
	}

	public void addAll(Collection<Artist> artists) {
		this.artists.addAll(artists);
		allSelectedArtists.setArtist(ResourceManager.getLabel("list.artist.all", locale) + " (" + (this.artists.size() - 1) + ")");
	}

	public Artist get(int i) {
		return (Artist) this.artists.get(i);
	}

	public int size() {
		return this.artists.size();
	}

	public Iterator<Artist> iterator() {
		return this.artists.iterator();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		Iterator<Artist> it = artists.iterator();
		Artist a;
		while (it.hasNext()) {
			a = (Artist) it.next();
			sb.append(a).append('\n');
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public Object clone() {
		ArtistList artistList = new ArtistList(locale);
		artistList.allSelectedArtists = allSelectedArtists;
		artistList.artists = (ArrayList<Artist>) artists.clone();
		artistList.compilation = compilation;
		artistList.compilationAdded = compilationAdded;
		return artistList;
	}

}
