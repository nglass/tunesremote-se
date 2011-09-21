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
package net.firefly.client.gui.swing.table.renderer;

import java.awt.Component;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import net.firefly.client.gui.context.Context;
import net.firefly.client.tools.TimeFormatTools;

public class TimeCellRenderer extends DefaultCellRenderer {

	private static final long serialVersionUID = -7925559116579701624L;
	
	protected Context context;
	
	public TimeCellRenderer(int alignment, boolean alternateRowColor, Context context) {
		super(alignment, alternateRowColor);
		this.context = context;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		DefaultTableCellRenderer c = (DefaultTableCellRenderer) super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		if (value == null || !(value instanceof Date)) {
			super.setValue("");
		} else {
			super.setValue(TimeFormatTools.format((Date)value));
		}
		return c;
	}

}
