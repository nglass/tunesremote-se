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

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import net.firefly.client.model.data.ColumnZero;

public class ColumnZeroRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 898992358057884830L;

	protected static Border noFocusBorder = new EmptyBorder(0, 5, 0, 5);

	private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(0, 5, 0, 5);

	private Color unselectedForeground;

	private Color unselectedBackground;

	protected int alignment;

	protected boolean alternateRowColor;

	public ColumnZeroRenderer(int alignment, boolean alternateRowColor) {
		super();
		this.alignment = alignment;
		this.alternateRowColor = alternateRowColor;
	}

	private static Border getNoFocusBorder() {
		if (System.getSecurityManager() != null) {
			return SAFE_NO_FOCUS_BORDER;
		} else {
			return noFocusBorder;
		}
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (isSelected) {
			super.setForeground(table.getSelectionForeground());
			super.setBackground(table.getSelectionBackground());
		} else {
			super.setForeground((unselectedForeground != null) ? unselectedForeground : table.getForeground());
			if (alternateRowColor) {
				if (row % 2 == 0) {
					super.setBackground((unselectedBackground != null) ? unselectedBackground : table.getBackground());
				} else {
					Color c = new Color(245, 245, 245);
					super.setBackground(c);
				}
			} else {
				super.setBackground((unselectedBackground != null) ? unselectedBackground : table.getBackground());
			}
		}

		setFont(table.getFont());

		setBorder(getNoFocusBorder());
		/*
		 * setBorder(UIManager.getBorder("Table.focusCellHighlightBorder")); if (table.isCellEditable(row, column)) {
		 * super.setForeground(UIManager.getColor("Table.focusCellForeground"));
		 * super.setBackground(UIManager.getColor("Table.focusCellBackground")); }
		 */

		setIcon((Icon)((ColumnZero)value).getImageIcon());
		setHorizontalAlignment(alignment);
		return this;
	}

}

