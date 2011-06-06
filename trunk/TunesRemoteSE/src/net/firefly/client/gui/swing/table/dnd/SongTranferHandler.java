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

import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.swing.table.SongTable;
import net.firefly.client.gui.swing.table.model.TableSorter;
import net.firefly.client.model.data.Song;
import net.firefly.client.model.data.SongContainer;

public class SongTranferHandler extends TransferHandler {

	private static final long serialVersionUID = -4910017214984846170L;
	
	private Context context;

	public SongTranferHandler(Context context) {
		super();
		this.context = context;
	}

	protected Transferable createTransferable(JComponent c) {
		if (c instanceof SongTable) {
			SongTable table = (SongTable) c;
			int[] rows;
			int[] cols;

			if (!table.getRowSelectionAllowed() && !table.getColumnSelectionAllowed()) {
				return null;
			}

			if (!table.getRowSelectionAllowed()) {
				int rowCount = table.getRowCount();

				rows = new int[rowCount];
				for (int counter = 0; counter < rowCount; counter++) {
					rows[counter] = counter;
				}
			} else {
				rows = table.getSelectedRows();
			}

			if (!table.getColumnSelectionAllowed()) {
				int colCount = table.getColumnCount();

				cols = new int[colCount];
				for (int counter = 0; counter < colCount; counter++) {
					cols[counter] = counter;
				}
			} else {
				cols = table.getSelectedColumns();
			}

			if (rows == null || cols == null || rows.length == 0 || cols.length == 0) {
				return null;
			}

			List<SongContainer> songs = new ArrayList<SongContainer>();
			TableSorter ts = (TableSorter)table.getModel();
			for (int row = 0; row < rows.length; row++) {
				int modelIndex = ts.modelIndex(rows[row]);
				SongContainer sc = context.getFilteredSongList().get(modelIndex);
				if (sc != null) {
					songs.add(sc);
				}
			}
			return new SongTransferable((Song[])songs.toArray(new Song[songs.size()]));
		}
		return null;
	}

	public int getSourceActions(JComponent c) {
		return COPY;
	}
}