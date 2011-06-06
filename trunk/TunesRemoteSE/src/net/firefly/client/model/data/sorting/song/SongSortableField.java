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

public class SongSortableField {

	protected int sortableFieldId;

	protected String sortableFieldLabel;

	protected SongSortableField(int sortableFieldId, String sortableFieldLabel) {
		this.sortableFieldId = sortableFieldId;
		this.sortableFieldLabel = sortableFieldLabel;
	}

	public static final SongSortableField SF_PART_OF_A_COMPILATION = new SongSortableField(1, "Part of a compilation");

	public static final SongSortableField SF_ARTIST_ALBUM = new SongSortableField(2, "Artist album");
	
	public static final SongSortableField SF_ARTIST = new SongSortableField(3, "Artist");

	public static final SongSortableField SF_ALBUM = new SongSortableField(4, "Album");

	public static final SongSortableField SF_YEAR = new SongSortableField(5, "Year");

	public static final SongSortableField SF_TITLE = new SongSortableField(6, "Title");

	public static final SongSortableField SF_DISC_NUMBER = new SongSortableField(7, "Disc number");

	public static final SongSortableField SF_TRACK_NUMBER = new SongSortableField(8, "Track number");

	public boolean equals(Object o) {
		try {
			SongSortableField ssf = (SongSortableField) o;
			return (this.sortableFieldId == ssf.getSortableFieldId());
		} catch (Exception e) {
			return false;
		}
	}

	public String toString() {
		return this.sortableFieldLabel;
	}

	/**
	 * @return Returns the sortableFieldId.
	 */
	public int getSortableFieldId() {
		return sortableFieldId;
	}

	/**
	 * @return Returns the sortableFieldLabel.
	 */
	public String getSortableFieldLabel() {
		return sortableFieldLabel;
	}
}