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
import net.firefly.client.gui.context.events.FilteredArtistListChangedEvent;
import net.firefly.client.gui.context.listeners.FilteredArtistListChangedEventListener;

public class ArtistTableModel extends AbstractTableModel implements FilteredArtistListChangedEventListener {

	private static final long serialVersionUID = -20828851244186315L;

	protected Context context;
	
	protected String artistColumnName;

	public ArtistTableModel(Context context) {
		this.context = context;
		this.artistColumnName = ResourceManager.getLabel("table.artist.column.artist", context.getConfig().getLocale());
		context.addFilteredArtistListChangedEventListener(this);
	}

	public int getColumnCount() {
		return 1;
	}

	public int getRowCount() {
		if (this.context.getFilteredArtistList() != null) {
			return this.context.getFilteredArtistList().size();
		}
		return 0;
	}

	public Object getValueAt(int row, int col) {
		if (this.context.getFilteredArtistList() != null) {
			return this.context.getFilteredArtistList().get(row);
		}
		return null;
	}

	public String getColumnName(int column) {
		return this.artistColumnName;
	}

	public void onFilteredArtistListChange(FilteredArtistListChangedEvent evt) {
		fireTableDataChanged();
	}

}
