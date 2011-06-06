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
import net.firefly.client.model.data.Genre;

public class GenreList implements Cloneable {

	protected ArrayList<Genre> genres;

	protected Genre allSelectedGenres;
	
	protected Locale locale;

	public GenreList(Locale locale) {
		this.locale = locale;
		this.genres = new ArrayList<Genre>();
		allSelectedGenres = new Genre(ResourceManager.getLabel("list.genre.all", locale) + " (0)", true);
		this.genres.add(allSelectedGenres);

	}

	public void add(Genre genre) {
		this.genres.add(genre);
		allSelectedGenres.setGenre(ResourceManager.getLabel("list.genre.all", locale) + " (" + (this.genres.size() - 1) + ")");
	}

	public void addAll(Collection<Genre> genres) {
		this.genres.addAll(genres);
		allSelectedGenres.setGenre(ResourceManager.getLabel("list.genre.all", locale) + " (" + (this.genres.size() - 1) + ")");
	}

	public Genre get(int i) {
		return (Genre) this.genres.get(i);
	}

	public int size() {
		return this.genres.size();
	}

	public Iterator<Genre> iterator() {
		return this.genres.iterator();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		Iterator<Genre> it = genres.iterator();
		Genre g;
		while (it.hasNext()) {
			g = it.next();
			sb.append(g).append('\n');
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public Object clone() {
		GenreList genreList = new GenreList(locale);
		genreList.genres = (ArrayList<Genre>) genres.clone();
		genreList.allSelectedGenres = allSelectedGenres;
		return genreList;
	}

}
