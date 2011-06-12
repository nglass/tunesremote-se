/*
 * This file is part of TunesRemote SE.
 *
 * TunesRemote SE is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * any later version.
 *
 * TunesRemote SE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TunesRemote SE; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright 2011 Nick Glass
 */
package net.firefly.client.player;

public class RepeatMode {

	protected String modeName;

	protected int mode;

	public static final RepeatMode REPEAT_OFF = new RepeatMode(0, "REPEAT_OFF");
	
	public static final RepeatMode REPEAT_SINGLE = new RepeatMode(1, "REPEAT_SINGLE");
	
	public static final RepeatMode REPEAT_ALL = new RepeatMode(2, "REPEAT_ALL");

	protected RepeatMode(int mode, String modeName) {
		this.mode = mode;
		this.modeName = modeName;
	}

	public boolean equals(Object o) {
		try {
			RepeatMode pm = (RepeatMode) o;
			return this.mode == pm.getMode();
		} catch (ClassCastException e) {
			return false;
		}
	}

	public String toString() {
		return modeName;
	}

	public int getMode() {
		return mode;
	}
}