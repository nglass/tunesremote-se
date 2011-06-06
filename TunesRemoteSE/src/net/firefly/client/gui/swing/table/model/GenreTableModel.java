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
import net.firefly.client.gui.context.events.FilteredGenreListChangedEvent;
import net.firefly.client.gui.context.listeners.FilteredGenreListChangedEventListener;

public class GenreTableModel extends AbstractTableModel implements FilteredGenreListChangedEventListener {

	private static final long serialVersionUID = -9219315768320148850L;

	protected Context context;
	
	protected String genreColumnName;

	public GenreTableModel(Context context) {
		this.context = context;
		this.genreColumnName = ResourceManager.getLabel("table.genre.column.genre", context.getConfig().getLocale());
		context.addFilteredGenreListChangedEventListener(this);
	}

	public GenreTableModel() {
	}

	public int getColumnCount() {
		return 1;
	}

	public int getRowCount() {
		if (context.getFilteredGenreList() != null) {
			return context.getFilteredGenreList().size();
		}
		return 0;
	}

	public Object getValueAt(int row, int col) {
		if (context.getFilteredGenreList() != null) {
			return context.getFilteredGenreList().get(row);
		}
		return null;
	}

	public String getColumnName(int column) {
		return this.genreColumnName;
	}

	public void onFilteredGenreListChange(FilteredGenreListChangedEvent evt) {
		fireTableDataChanged();
	}

}
