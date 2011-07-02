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
package net.firefly.client.gui.swing.tree.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.netbeans.swing.outline.DefaultOutlineCellRenderer;

public class RadiolistTreeCellRenderer extends DefaultOutlineCellRenderer {

   private static final long serialVersionUID = -185142438898068637L;

   protected static Border noFocusBorder = new EmptyBorder(0, 5, 0, 5);

   private Color unselectedForeground;

   private Color unselectedBackground;

   protected int alignment;

   protected boolean alternateRowColor;

   public RadiolistTreeCellRenderer(int alignment, boolean alternateRowColor) {
      super();
      this.alignment = alignment;
      this.alternateRowColor = alternateRowColor;
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {      
      Component result = super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
      
      if (isSelected) {
         result.setForeground(table.getSelectionForeground());
         result.setBackground(table.getSelectionBackground());
      } else {
         result.setForeground((unselectedForeground != null) ? unselectedForeground : table.getForeground());
         if (alternateRowColor) {
            if (row % 2 == 0) {
               result.setBackground((unselectedBackground != null) ? unselectedBackground : table.getBackground());
            } else {
               Color c = new Color(245, 245, 245);
               result.setBackground(c);
            }
         } else {
            result.setBackground((unselectedBackground != null) ? unselectedBackground : table.getBackground());
         }
      }
      
      result.setFont(table.getFont());
      
      return result;
   }
}
