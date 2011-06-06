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
package net.firefly.client.model.playlist;

public class PlaylistType implements Comparable<PlaylistType> {

	public static final int PLAYLIST_TYPE_STATIC = 0;
	public static final int PLAYLIST_TYPE_SMART = 1;
	public static final int PLAYLIST_TYPE_STATIC_M3U = 2;
	public static final int PLAYLIST_TYPE_STATIC_ITUNES = 3;

	private String playlistTypeName;

	private int playlistType;

	private int order;

	public static final PlaylistType STATIC = new PlaylistType(PLAYLIST_TYPE_STATIC, "STATIC", 1);

	public static final PlaylistType SMART = new PlaylistType(PLAYLIST_TYPE_SMART, "SMART", 0);

	public static final PlaylistType STATIC_M3U = new PlaylistType(PLAYLIST_TYPE_STATIC_M3U, "STATIC M3U", 1);

	public static final PlaylistType STATIC_ITUNES = new PlaylistType(PLAYLIST_TYPE_STATIC_ITUNES, "STATIC_ITUNES", 1);

	private PlaylistType(int playlistType, String playlistTypeName, int order) {
		this.playlistType = playlistType;
		this.playlistTypeName = playlistTypeName;
		this.order = order;
	}

	public boolean equals(Object o) {
		try {
			PlaylistType ps = (PlaylistType) o;
			return this.playlistType == ps.playlistType;
		} catch (ClassCastException e) {
			return false;
		}
	}

	public static PlaylistType getPlaylistType(int playlistType) {
		switch (playlistType) {
		case PLAYLIST_TYPE_STATIC:
			return STATIC;
		case PLAYLIST_TYPE_SMART:
			return SMART;
		case PLAYLIST_TYPE_STATIC_M3U:
			return STATIC_M3U;
		case PLAYLIST_TYPE_STATIC_ITUNES:
			return STATIC_ITUNES;
		default:
			// should not happen
			return SMART;
		}
	}

	public String toString() {
		return playlistTypeName;
	}

	// SMART < ITUNES < M3U < STATIC
	public int compareTo(PlaylistType pt) {
		return this.order - pt.order;
	}
}