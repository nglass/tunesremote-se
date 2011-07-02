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
package net.firefly.client.gui.swing.tree.renderer;

import javax.swing.ImageIcon;

import net.firefly.client.model.data.Song;
import net.firefly.client.model.data.SongContainer;
import net.firefly.client.model.playlist.IPlaylist;
import net.firefly.client.model.playlist.list.RadiolistList;

import org.netbeans.swing.outline.RenderDataProvider;

public class RadiolistTreeRenderer implements RenderDataProvider {

   private static final ImageIcon EMPTY_ICON = new ImageIcon();
   
   private ImageIcon radioIcon;
   
   public RadiolistTreeRenderer() {
      this.radioIcon = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/radiolist.png"));
   }
   
   @Override
   public java.awt.Color getBackground(Object o) {
       return null;
   }

   @Override
   public String getDisplayName(Object o) {
      if (o instanceof RadiolistList) {
         RadiolistList rll = (RadiolistList) o;
         return rll.getName();
      } else if (o instanceof IPlaylist) {
         IPlaylist pl = (IPlaylist) o;
         return pl.getPlaylistName();
      } else if (o instanceof SongContainer) {
         SongContainer sc = (SongContainer) o;
         Song s = sc.getSong();
         if (s != null) {
            return s.getTitle();
         }
      }
      return "";
   }

   @Override
   public java.awt.Color getForeground(Object o) {
       return null;
   }

   @Override
   public javax.swing.Icon getIcon(Object o) {
      if (o instanceof IPlaylist) {
         return EMPTY_ICON;
      } else if (o instanceof SongContainer) {
         return radioIcon;
      }
      return null;
   }

   @Override
   public String getTooltipText(Object o) {
       return null;
   }

   @Override
   public boolean isHtmlDisplayName(Object o) {
       return false;
   }

}
