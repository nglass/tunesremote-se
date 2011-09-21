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

import java.util.LinkedList;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreeModel;

import org.tunesremote.util.ThreadExecutor;

import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.context.events.SelectedPlaylistChangedEvent;
import net.firefly.client.gui.context.listeners.RadiolistListChangedEventListener;
import net.firefly.client.model.data.SongContainer;
import net.firefly.client.model.playlist.IPlaylist;
import net.firefly.client.model.playlist.PlaylistStatus;
import net.firefly.client.model.playlist.list.RadiolistList;
import net.firefly.client.tools.FireflyClientException;

public class RadioTreeModel implements TreeModel, RadiolistListChangedEventListener {

   protected Context context;
   
   protected List<javax.swing.event.TreeModelListener> listenerList;
   
   public RadioTreeModel(Context context) {
      this.context = context;
      this.listenerList = new LinkedList<javax.swing.event.TreeModelListener>();
      context.addRadiolistListChangedEventListener(this);
   }

   @Override
   public void addTreeModelListener(javax.swing.event.TreeModelListener l) {
      listenerList.add(l);
   }

   @Override
   public Object getChild(Object parent, int index) {
      if (parent instanceof RadiolistList) {
         RadiolistList rll = (RadiolistList) parent;
         return rll.getPlaylist(index);
      } else if (parent instanceof IPlaylist) {
         IPlaylist pl = (IPlaylist) parent;
         return pl.getSongList().get(index);
      } else {
         return null;
      }
   }

   @Override
   public int getChildCount(Object parent) {
      if (parent instanceof RadiolistList) {
         RadiolistList rll = (RadiolistList) parent;
         return rll.playlistsSize();
      } else if (parent instanceof IPlaylist) {
         final IPlaylist pl = (IPlaylist) parent;
         
         if (pl.getStatus() == PlaylistStatus.NOT_LOADED) {
            pl.setStatus(PlaylistStatus.LOADING);
            ThreadExecutor.runTask(new Runnable() {
               public void run() {
                  try {
                     context.getPlaylistRequestManager().loadRadioListForPlaylist(pl);
                  } catch (FireflyClientException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                  }
               }
            });   
            return 0;
         }

         return pl.getSongList().size();
      } else {
         return 0;
      }
   }

   @Override
   public int getIndexOfChild(Object parent, Object child) {
      if (parent instanceof RadiolistList && child instanceof IPlaylist) {
         RadiolistList rll = (RadiolistList) parent;
         IPlaylist pl = (IPlaylist) child;
         return rll.indexOfPlaylist(pl);
      } else if (parent instanceof IPlaylist && child instanceof SongContainer) {
         IPlaylist pl = (IPlaylist) parent;
         SongContainer sc = (SongContainer) child;
         return pl.getSongList().indexOf(sc);
      } else {
         return -1;
      }
   }

   @Override
   public Object getRoot() {
      return context.getRadiolists();
   }

   @Override
   public boolean isLeaf(Object node) {
      return (node instanceof SongContainer);
   }

   @Override
   public void removeTreeModelListener(javax.swing.event.TreeModelListener l) {
      listenerList.remove(l);
   }

   @Override
   public void valueForPathChanged(javax.swing.tree.TreePath path, Object newValue) {
      //do nothing
      System.err.println("valueForPathChanged");
   }

   // need to synchronize
   @Override
   public void onRadiolistListChange(final SelectedPlaylistChangedEvent evt) {
      Object[] path;
      if (evt.getNewSelectedPlaylist() == null) {
         path = new Object[1];
      } else {
         path = new Object[2];
         path[1] = evt.getNewSelectedPlaylist();
      }
      
      path[0] = getRoot();
      
      for (javax.swing.event.TreeModelListener listener : listenerList) {
         listener.treeStructureChanged(new TreeModelEvent(this, path));
      }
   }
}
