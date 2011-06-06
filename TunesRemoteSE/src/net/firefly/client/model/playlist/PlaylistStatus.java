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

public class PlaylistStatus {

	protected String statusName;

	protected int status;

	public static final PlaylistStatus NOT_LOADED = new PlaylistStatus(0, "NOT LOADED");

	public static final PlaylistStatus LOADING = new PlaylistStatus(1, "LOADING");

	public static final PlaylistStatus LOADED = new PlaylistStatus(2, "LOADED");

	protected PlaylistStatus(int status, String statusName) {
		this.status = status;
		this.statusName = statusName;
	}

	public boolean equals(Object o) {
		try {
			PlaylistStatus ps = (PlaylistStatus) o;
			return this.status == ps.status;
		} catch (ClassCastException e) {
			return false;
		}
	}

	public String toString() {
		return statusName;
	}
}