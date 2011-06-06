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
package net.firefly.client.model.playlist.sorting;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import net.firefly.client.model.playlist.IPlaylist;

public class PlaylistComparator implements Comparator<IPlaylist> {

	protected static PlaylistComparator instance;

	protected static Comparator<Object> comparator = Collator.getInstance(Locale.FRANCE);

	private PlaylistComparator() {
	}

	public static synchronized PlaylistComparator getInstance() {
		if (instance == null) {
			instance = new PlaylistComparator();
		}
		return instance;
	}

	public int compare(IPlaylist p1, IPlaylist p2) {
		int result = p1.getPlaylistType().compareTo(p2.getPlaylistType());
		if (result != 0) {
			return result;
		} else {
			return comparator.compare(p1.getPlaylistName(), p2.getPlaylistName());
		}
	}
}