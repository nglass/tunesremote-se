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
package net.firefly.client.gui.swing.tree;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

import net.firefly.client.controller.ResourceManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.swing.table.renderer.DefaultCellRenderer;
import net.firefly.client.gui.swing.tree.model.RadioRowModel;
import net.firefly.client.gui.swing.tree.model.RadioTreeModel;
import net.firefly.client.gui.swing.tree.renderer.RadiolistTreeCellRenderer;
import net.firefly.client.gui.swing.tree.renderer.RadiolistTreeRenderer;
import net.firefly.client.model.data.Song;
import net.firefly.client.model.data.SongContainer;

import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;

public class RadioOutline extends Outline {

   private static final long serialVersionUID = -6303227009636837268L;

   protected Context context;
   
   public RadioOutline(Context context) {
      super();
      this.context = context;
      
      RadioTreeModel rtm = new RadioTreeModel(context);
      RadioRowModel rrm = new RadioRowModel(context);
      
      OutlineModel om = DefaultOutlineModel.createOutlineModel(
            rtm, rrm, true, ResourceManager.getLabel("radio.outline.stream", context.getConfig().getLocale()));
      
      setModel(om);
      setRootVisible(false);
      setColumnHidingAllowed(false);
      
      // -- show vertical lines only
      setShowGrid(false);
      setShowHorizontalLines(false);
      setShowVerticalLines(true);
      setGridColor(new Color(217, 217, 217)); // -- vertical lines colors
      setIntercellSpacing(new Dimension(1, 0)); // -- space needed by
      
      setRowHeight(18);
      
      TableColumn firstColumn = getColumnModel().getColumn(0);
      firstColumn.setCellRenderer(new RadiolistTreeCellRenderer(JLabel.LEFT, true));
      firstColumn.setPreferredWidth(100);
      firstColumn.setWidth(100);
      setRenderDataProvider(new RadiolistTreeRenderer());
      
      TableColumn lastColumn = getColumnModel().getColumn(1);
      lastColumn.setPreferredWidth(300);
      lastColumn.setWidth(300);
      lastColumn.setCellRenderer(new DefaultCellRenderer(JLabel.LEFT, true));
      
      setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
      
      addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(final java.awt.event.MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() >= 2) {
               playRadio();
            }
         }
      });
   }
   
   public void playRadio() {      
      Object o = getValueAt(getSelectedRow(),0);
      if (o instanceof SongContainer) {
         SongContainer sc = (SongContainer) o;
         Song song = sc.getSong();
         
         context.getSession().controlPlayRadio(sc.getPlaylistId(), song.getDatabaseItemId());  
      }
   }
}
