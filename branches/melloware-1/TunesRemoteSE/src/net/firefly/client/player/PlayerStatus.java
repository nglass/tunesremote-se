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
package net.firefly.client.player;

public class PlayerStatus {

	protected String statusName;

	protected int status;

	public static final PlayerStatus STATUS_STOPPED = new PlayerStatus(0, "STOPPED");

	public static final PlayerStatus STATUS_PLAYING = new PlayerStatus(1, "PLAYING");

	public static final PlayerStatus STATUS_CONNECTING = new PlayerStatus(2, "CONNECTING");

	public static final PlayerStatus STATUS_READING_INFO = new PlayerStatus(3, "READING_INFO");

	public static final PlayerStatus STATUS_SEEKING = new PlayerStatus(4, "SEEKING");
	
	public static final PlayerStatus STATUS_PAUSED = new PlayerStatus(0, "PAUSED");

	protected PlayerStatus(int status, String statusName) {
		this.status = status;
		this.statusName = statusName;
	}

	public boolean equals(Object o) {
		try {
			PlayerStatus ps = (PlayerStatus) o;
			return this.status == ps.getStatus();
		} catch (ClassCastException e) {
			return false;
		}
	}

	public String toString() {
		return statusName;
	}

	/**
	 * @return Returns the status.
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @return Returns the statusName.
	 */
	public String getStatusName() {
		return statusName;
	}

}