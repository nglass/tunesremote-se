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
package net.firefly.client.model.data.sorting.song;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import net.firefly.client.model.data.SongContainer;
import net.firefly.client.model.data.Song;

public class SongComparator implements Comparator<SongContainer> {

	protected SongSortableField[] sortableFields;
	
	protected static Comparator<Object> comparator = Collator.getInstance(Locale.FRANCE);

	public SongComparator(SongSortableField[] sortableFields) {
		this.sortableFields = sortableFields;
	}

	public int compare(SongContainer o1, SongContainer o2) {
		Song s1 = o1.getSong();
		Song s2 = o2.getSong();
		int c;

		for (int i = 0; i < sortableFields.length; i++) {
			SongSortableField ssf = sortableFields[i];
			if (ssf.equals(SongSortableField.SF_PART_OF_A_COMPILATION)) {
				if (s1.isPartOfACompilation() && !s2.isPartOfACompilation()) {
					return -1;
				}
				if (!s1.isPartOfACompilation() && s2.isPartOfACompilation()) {
					return 1;
				}
			}
			if (ssf.equals(SongSortableField.SF_ARTIST_ALBUM)) {
				if (s1.getArtistAlbum() != null && s2.getArtistAlbum() != null) {
					// -- following tests ensure that unknown artist comes last
					if (s1.getArtistAlbum().length() != s2.getArtistAlbum().length()){
						if (s1.getArtistAlbum().length() == 0){
							return 1;
						}
						if (s2.getArtistAlbum().length() == 0){
							return -1;
						}
					}
					c = comparator.compare(s1.getArtistAlbum(), s2.getArtistAlbum());
					if (c != 0) {
						return  c;
					}
				}
			}

			if (ssf.equals(SongSortableField.SF_ARTIST)) {
				if (s1.getSortArtist() != null && s2.getSortArtist() != null) {
					c = comparator.compare(s1.getSortArtist(), s2.getSortArtist());
					if (c != 0) {
						return  c;
					}
				}
			}
			if (ssf.equals(SongSortableField.SF_ALBUM)) {
				if (s1.getSortAlbum() != null && s2.getSortAlbum() != null) {
					// -- following tests ensure that unknown album comes last
					if (s1.getSortAlbum().length() != s2.getSortAlbum().length()){
						if (s1.getSortAlbum().length() == 0){
							return 1;
						}
						if (s2.getSortAlbum().length() == 0){
							return -1;
						}
					}
					c = comparator.compare(s1.getSortAlbum(), s2.getSortAlbum());
					if (c != 0) {
						return  c;
					}
				}
			}
			if (ssf.equals(SongSortableField.SF_YEAR)) {
				if (s1.getYear() != null && s2.getYear() != null) {
					c = comparator.compare(s1.getYear(), s2.getYear());
					if (c != 0) {
						return  c;
					}
				}
			}
			if (ssf.equals(SongSortableField.SF_TITLE)) {
				if (s1.getTitle() != null && s2.getTitle() != null) {
					c = comparator.compare(s1.getTitle(), s2.getTitle());
					if (c != 0) {
						return  c;
					}
				}
			}
			if (ssf.equals(SongSortableField.SF_DISC_NUMBER)) {
				if (s1.getDiscNumber() != null && s2.getDiscNumber() != null) {
					try {
						int i1 = Integer.parseInt(s1.getDiscNumber());
						int i2 = Integer.parseInt(s2.getDiscNumber());
						if (i1 != i2) {
							return (i1 - i2);
						}
					} catch (NumberFormatException e) {

					}
				}
			}
			if (ssf.equals(SongSortableField.SF_TRACK_NUMBER)) {
				if (s1.getTrackNumber() != null && s2.getTrackNumber() != null) {
					try {
						int i1 = Integer.parseInt(s1.getTrackNumber());
						int i2 = Integer.parseInt(s2.getTrackNumber());
						if (i1 != i2) {
							return (i1 - i2);
						}
					} catch (NumberFormatException e) {

					}
				}
			}
		}
		return 0;
	}

}