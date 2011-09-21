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
package net.firefly.client.player.events;

import java.util.EventObject;

import net.firefly.client.player.MediaPlayer;
import net.firefly.client.player.RepeatMode;

public class RepeatModeChangedEvent extends EventObject {

	private static final long serialVersionUID = -3637964574676708378L;

	protected RepeatMode oldMode;

	protected RepeatMode newMode;

	public RepeatModeChangedEvent(MediaPlayer player, RepeatMode oldMode, RepeatMode newMode) {
		super(player);
		this.oldMode = oldMode;
		this.newMode = newMode;
	}

	public RepeatMode getNewMode() {
		return newMode;
	}

	public RepeatMode getOldMode() {
		return oldMode;
	}

	public MediaPlayer getPlayer() {
		return (MediaPlayer) source;
	}
}
