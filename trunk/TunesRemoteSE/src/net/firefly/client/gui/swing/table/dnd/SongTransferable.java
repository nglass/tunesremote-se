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
package net.firefly.client.gui.swing.table.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.Arrays;
import java.util.List;

import net.firefly.client.model.data.Song;

public class SongTransferable implements Transferable {

	public static DataFlavor songsArrayFlavor = null;

	static {
		try {
			songsArrayFlavor = new DataFlavor(Song.class, "Local song");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final DataFlavor[] flavors = { songsArrayFlavor };

	private static final List<DataFlavor> flavorList = Arrays.asList(flavors);

	private Song[] songs;

	public SongTransferable(Song[] songs) {
		this.songs = songs;
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
		if (isDataFlavorSupported(flavor)) {
			return this.songs;
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}

	public synchronized DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (flavorList.contains(flavor));
	}
}