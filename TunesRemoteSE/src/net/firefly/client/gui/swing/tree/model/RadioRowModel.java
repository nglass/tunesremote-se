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
package net.firefly.client.gui.swing.tree.model;

import net.firefly.client.controller.ResourceManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.model.data.RadioStation;
import net.firefly.client.model.data.Song;
import net.firefly.client.model.data.SongContainer;
import net.firefly.client.model.playlist.IPlaylist;

import org.netbeans.swing.outline.RowModel;

public class RadioRowModel implements RowModel {

   private String comments;
   
   public RadioRowModel(Context context) {
      comments =  ResourceManager.getLabel("radio.outline.comments", context.getConfig().getLocale());
   }
   
   @Override
   public Class<?> getColumnClass(int column) {
       switch (column) {
           case 0:
               return String.class;
           default:
               assert false;
       }
       return null;
   }

   @Override
   public int getColumnCount() {
       return 1;
   }

   @Override
   public String getColumnName(int column) {
      switch (column) {
      case 0:
         return this.comments;
      default:
         assert false;
      }
      return null;
   }

   @Override
   public Object getValueFor(Object node, int column) {
      if (node instanceof IPlaylist) {
         switch (column) {
         case 0:
            return "";
         default:
            assert false;
         }
         return null;
         
      } else if (node instanceof SongContainer) {
         SongContainer sc = (SongContainer) node;
         Song song = sc.getSong();
         if (song instanceof RadioStation) {
            RadioStation rs = (RadioStation) song;
            switch (column) {
            case 0:
               return rs.getDescription();
            default:
               assert false;
            }
            return null;
         }
      }
      return null;
   }

   @Override
   public boolean isCellEditable(Object node, int column) {
       return false;
   }

   @Override
   public void setValueFor(Object node, int column, Object value) {
       //do nothing for now
   }
   
}
