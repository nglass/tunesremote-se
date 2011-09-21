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

package net.firefly.client.gui.swing.table.model;

import javax.swing.JLabel;

import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.swing.table.renderer.AlbumCellRenderer;
import net.firefly.client.gui.swing.table.renderer.ArtistCellRenderer;
import net.firefly.client.gui.swing.table.renderer.ColumnZeroRenderer;
import net.firefly.client.gui.swing.table.renderer.DefaultCellRenderer;
import net.firefly.client.gui.swing.table.renderer.NumberCellRenderer;
import net.firefly.client.gui.swing.table.renderer.TimeCellRenderer;

import org.netbeans.swing.etable.ETable;
import org.netbeans.swing.etable.ETableColumn;
import org.netbeans.swing.etable.ETableColumnModel;
import org.netbeans.swing.etable.TableColumnSelector;
import org.netbeans.swing.etable.TableColumnSelector.TreeNode;

public class SongTableColumnModel extends ETableColumnModel implements TableColumnSelector.TreeNode {

   private static final long serialVersionUID = 9130588259813362866L;

   private Column[] columns;
   
   public SongTableColumnModel(ETable table, Context context) {
      Column iconColumn = new Column(0, table);
      iconColumn.setHidingAllowed(false);
      iconColumn.setHeaderValue(table.getModel().getColumnName(0));
      iconColumn.setPreferredWidth(26);
      iconColumn.setMaxWidth(26);
      iconColumn.setMinWidth(26);
      iconColumn.setCellRenderer(new ColumnZeroRenderer(JLabel.CENTER, true));
      addColumn(iconColumn);

      Column titleColumn = new Column(1, table);
      titleColumn.setHeaderValue(table.getModel().getColumnName(1));
      titleColumn.setPreferredWidth(255);
      titleColumn.setCellRenderer(new DefaultCellRenderer(JLabel.LEFT, true));
      addColumn(titleColumn);

      Column timeColumn = new Column(2, table);
      timeColumn.setHeaderValue(table.getModel().getColumnName(2));
      timeColumn.setPreferredWidth(53);
      timeColumn.setMaxWidth(53);
      timeColumn.setMinWidth(53);
      timeColumn.setCellRenderer(new TimeCellRenderer(JLabel.RIGHT, true, context));
      addColumn(timeColumn);

      Column artistColumn = new Column(3, table);
      artistColumn.setHeaderValue(table.getModel().getColumnName(3));
      artistColumn.setPreferredWidth(185);
      artistColumn.setCellRenderer(new ArtistCellRenderer(JLabel.LEFT, true, context));
      addColumn(artistColumn);

      Column albumColumn = new Column(4, table);
      albumColumn.setHeaderValue(table.getModel().getColumnName(4));
      albumColumn.setPreferredWidth(185);
      albumColumn.setCellRenderer(new AlbumCellRenderer(JLabel.LEFT, true, context));
      addColumn(albumColumn);

      Column yearColumn = new Column(5, table);
      yearColumn.setHeaderValue(table.getModel().getColumnName(5));
      yearColumn.setPreferredWidth(52);
      yearColumn.setMaxWidth(52);
      yearColumn.setMinWidth(52);
      yearColumn.setCellRenderer(new NumberCellRenderer(JLabel.RIGHT, true, context));
      addColumn(yearColumn);

      Column trackNumberColumn = new Column(6, table);
      trackNumberColumn.setHeaderValue(table.getModel().getColumnName(6));
      trackNumberColumn.setPreferredWidth(30);
      trackNumberColumn.setMaxWidth(30);
      trackNumberColumn.setMinWidth(30);
      trackNumberColumn.setCellRenderer(new NumberCellRenderer(JLabel.RIGHT, true, context));
      addColumn(trackNumberColumn);

      Column discNumberColumn = new Column(7, table);
      discNumberColumn.setHeaderValue(table.getModel().getColumnName(7));
      discNumberColumn.setPreferredWidth(37);
      discNumberColumn.setMaxWidth(37);
      discNumberColumn.setMinWidth(37);
      discNumberColumn.setCellRenderer(new NumberCellRenderer(JLabel.RIGHT, true, context));
      addColumn(discNumberColumn);

      Column lastColumn = new Column(8, table);
      lastColumn.setHidingAllowed(false);
      lastColumn.setSortingAllowed(false);
      lastColumn.setHeaderValue(table.getModel().getColumnName(8));
      lastColumn.setMinWidth(0);
      lastColumn.setMaxWidth(2000);
      lastColumn.setCellRenderer(new DefaultCellRenderer(JLabel.LEFT, true));
      //addColumn(lastColumn);
      
      this.columns = new Column[7];
      columns[0] = titleColumn;
      columns[1] = timeColumn;
      columns[2] = artistColumn;
      columns[3] = albumColumn;
      columns[4] = yearColumn;
      columns[5] = trackNumberColumn;
      columns[6] = discNumberColumn;
   }
   
   @Override
   public TableColumnSelector.TreeNode getColumnHierarchyRoot() {
      return this;
   }

   @Override
   public TreeNode[] getChildren() {
      return columns;
   }

   @Override
   public String getText() {
      return "";
   }

   @Override
   public boolean isLeaf() {
      return false;
   }
   
   public class Column extends ETableColumn implements TableColumnSelector.TreeNode {

      private static final long serialVersionUID = 7685487733470143317L;
      private boolean hidingAllowed = true;
      private boolean sortingAllowed = true;
      
      public Column(int modelindex, ETable table) {
         super(modelindex, table);
      }
      
      @Override
      public boolean isHidingAllowed() {
         return hidingAllowed;
      }
      
      public void setHidingAllowed(boolean hidingAllowed) {
         this.hidingAllowed = hidingAllowed;
      }
      
      @Override
      public boolean isSortingAllowed() {
         return sortingAllowed;
      }  
      
      public void setSortingAllowed(boolean sortingAllowed) {
         this.sortingAllowed = sortingAllowed;
      }
      
      @Override
      public TreeNode[] getChildren() {
         return null;
      }

      @Override
      public String getText() {
         return (String) getHeaderValue();
      }

      @Override
      public boolean isLeaf() {
         return true;
      }
   }   
}
