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
package net.firefly.client.gui.swing.table.model;

import javax.swing.table.AbstractTableModel;

import net.firefly.client.controller.ResourceManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.context.events.FilteredAlbumListChangedEvent;
import net.firefly.client.gui.context.listeners.FilteredAlbumListChangedEventListener;

public class AlbumTableModel extends AbstractTableModel implements FilteredAlbumListChangedEventListener {

	private static final long serialVersionUID = -1550893019505612460L;

	protected Context context;
	
	protected String albumColumnName;

	public AlbumTableModel(Context context) {
		this.context = context;
		this.albumColumnName = ResourceManager.getLabel("table.album.column.album", context.getConfig().getLocale());
		context.addFilteredAlbumListChangedEventListener(this);
	}

	public AlbumTableModel() {
	}

	public int getColumnCount() {
		return 1;
	}

	public int getRowCount() {
		if (context.getFilteredAlbumList() != null) {
			return context.getFilteredAlbumList().size();
		}
		return 0;
	}

	public Object getValueAt(int row, int col) {
		if (context.getFilteredAlbumList() != null) {
			return context.getFilteredAlbumList().get(row);
		}
		return null;
	}

	public String getColumnName(int column) {
		return this.albumColumnName;
	}

	public void onFilteredAlbumListChange(FilteredAlbumListChangedEvent evt) {
		fireTableDataChanged();
	}

}
