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

public class Genre implements Comparable<Genre>, Cloneable, Serializable {

	private static final long serialVersionUID = -1464079256896563474L;

	private String genre;

	private boolean special;

	private static Collator collator = Collator.getInstance();

	private static final Set<String> UNKNOWN_GENRE_SIMPLE_STRINGS;

	static {
		collator.setStrength(Collator.PRIMARY);

		UNKNOWN_GENRE_SIMPLE_STRINGS = new TreeSet<String>(collator);
		UNKNOWN_GENRE_SIMPLE_STRINGS.add("unknown");
		UNKNOWN_GENRE_SIMPLE_STRINGS.add("inconnu");
		UNKNOWN_GENRE_SIMPLE_STRINGS.add("unclassifiable");
		UNKNOWN_GENRE_SIMPLE_STRINGS.add("autre");
		UNKNOWN_GENRE_SIMPLE_STRINGS.add("other");
		UNKNOWN_GENRE_SIMPLE_STRINGS.add("divers");
		UNKNOWN_GENRE_SIMPLE_STRINGS.add("miscellaneous");
		UNKNOWN_GENRE_SIMPLE_STRINGS.add("misc");
		UNKNOWN_GENRE_SIMPLE_STRINGS.add("misc.");
		UNKNOWN_GENRE_SIMPLE_STRINGS.add("<unknown>");
		UNKNOWN_GENRE_SIMPLE_STRINGS.add("undefined");
		UNKNOWN_GENRE_SIMPLE_STRINGS.add("genre");
		UNKNOWN_GENRE_SIMPLE_STRINGS.add("unknown genre");
		UNKNOWN_GENRE_SIMPLE_STRINGS.add("genre inconnu");
	}

	public Genre(String genre) {
		this(genre, false);
	}

	public Genre(String genre, boolean special) {
		this.genre = genre;
		this.special = special;
	}

	/**
	 * @return Returns the genre.
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * @param genre
	 *            The genre to set.
	 */
	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String toString() {
		return this.genre;
	}

	public int compareTo(Genre o) {
		return collator.compare(this.genre, o.getGenre());
	}

	public Object clone() {
		return new Genre(this.genre);
	}

	public boolean isSpecial() {
		return special;
	}

	public static boolean isUnknownGenre(String s) {
		if (UNKNOWN_GENRE_SIMPLE_STRINGS.contains(s)) {
			return true;
		}
		return false;
	}
}