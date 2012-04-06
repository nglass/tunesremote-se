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
package net.firefly.client.gui.swing.tree.model;

import java.awt.Frame;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.firefly.client.controller.ResourceManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.context.events.PlaylistListChangedEvent;
import net.firefly.client.gui.context.events.StaticPlaylistCreationEvent;
import net.firefly.client.gui.context.listeners.PlaylistListChangedEventListener;
import net.firefly.client.gui.context.listeners.StaticPlaylistCreationEventListener;
import net.firefly.client.model.library.LibraryInfo;
import net.firefly.client.model.playlist.IPlaylist;
import net.firefly.client.model.playlist.list.PlaylistList;
import net.firefly.client.model.playlist.list.RadiolistList;
import net.firefly.client.tools.FireflyClientException;

public class PlaylistTreeModel implements TreeModel, PlaylistListChangedEventListener,
		StaticPlaylistCreationEventListener {

	public String LibraryNode;
	public String GeniusNode;
	public String PlaylistNode;

	protected Context context;

	protected Frame rootContainer;
	
	protected List<javax.swing.event.TreeModelListener> listenerList;

	public PlaylistTreeModel(Context c, Frame rootContainer) {
		this.context = c;
		this.rootContainer = rootContainer;
		this.listenerList = new LinkedList<javax.swing.event.TreeModelListener>();
		
      this.LibraryNode = new String(ResourceManager.getLabel(
            "playlist.tree.library", c.getConfig().getLocale()).toUpperCase(
            c.getConfig().getLocale()));

      this.GeniusNode = new String(ResourceManager.getLabel(
            "playlist.tree.genius", c.getConfig().getLocale()).toUpperCase(
            c.getConfig().getLocale()));
      
      this.PlaylistNode = new String(ResourceManager.getLabel(
            "playlist.tree.playlists", c.getConfig().getLocale()).toUpperCase(
            c.getConfig().getLocale()));

		context.addPlaylistListChangedEventListener(this);
		context.addStaticPlaylistCreationEventListener(this);
	}

	/**
	 * Create playlists root node if still not, add the playlist in the tree,
	 * and trigger its immediate edition.
	 */
	public void addPlaylist(final IPlaylist playlist) throws FireflyClientException {

	}

	/**
	 * Remove the playlist from the tree, remove playlists root node if no
	 * playlist left to display
	 * 
	 * @throws FireflyClientException
	 */
	public void removePlaylist(IPlaylist playlist) throws FireflyClientException {

	}

	/**
	 * Rename a playlist, and move it at the right place (regarding
	 * PlaylistComparator)
	 */
	public void renamePlaylist(IPlaylist playlist, String newName) throws FireflyClientException {

	}

	public void onPlaylistListChange(PlaylistListChangedEvent evt) {
	   Object[] path = new Object[1];      
      path[0] = getRoot();
      
      TreeModelEvent t = new TreeModelEvent(this, path);
      for (javax.swing.event.TreeModelListener listener : listenerList) {
         listener.treeStructureChanged(t);
      }
	}

	public String getNewPlaylistName(int from) {
		int i = from;
		String basename = ResourceManager.getLabel("playlist.tree.new.playlist.base", context.getConfig().getLocale())
				.trim();
		String playlistName = basename + " " + i;
		while (playlistNameAlreadyExists(playlistName)) {
			i++;
			playlistName = basename + " " + i;
		}
		return playlistName;
	}

	private boolean playlistNameAlreadyExists(String playlistName) {
		PlaylistList pl = context.getPlaylists();
		if (pl != null) {
			Iterator<IPlaylist> it = pl.playlistIterator();
			while (it.hasNext()) {
				IPlaylist p = it.next();
				if (playlistName.equals(p.getPlaylistName())) {
					return true;
				}
			}
		}
		return false;
	}

	public void onStaticPlaylistCreation(StaticPlaylistCreationEvent evt) {

	}

   @Override
   public void addTreeModelListener(javax.swing.event.TreeModelListener l) {
      listenerList.add(l);
   }

   @Override
   public void removeTreeModelListener(javax.swing.event.TreeModelListener l) {
      listenerList.remove(l);
   } 

   @Override
   public Object getChild(Object parent, int index) {
      if (parent == this) {
         if (index == 0) {
            return LibraryNode;
         } else if (context.getPlaylists().geniusPlaylistsSize() > 0) {
            if (index == 1) {
               return GeniusNode;
            } else if (index == 2) {
               return PlaylistNode;
            }
         } else if (index == 1) {
            return PlaylistNode;
         }
         return null;
      } else if (parent instanceof String) {
         if (parent == LibraryNode) {
            int specialSize = context.getPlaylists().specialPlaylistsSize() + 1;
            if (context.getSession().supportsRadio()) {
               specialSize++;
            }
            
            if (index == 0) {
               return context.getLibraryInfo();
            } else if (index == specialSize-1 && context.getSession().supportsRadio()) {
               return context.getRadiolists();
            } else {
               return context.getPlaylists().getSpecialPlaylist(index - 1);
            }
         } else if (parent == GeniusNode) {
            return context.getPlaylists().getGeniusPlaylist(index);
         } else if (parent == PlaylistNode) {
            return context.getPlaylists().getPlaylist(index);
         }
      } else if (parent instanceof IPlaylist) {
         IPlaylist playlist = (IPlaylist) parent;
         return playlist.getNested(index);
      }
      
      return null;
   }


   @Override
   public int getChildCount(Object parent) {
      if (parent == this) {
         if (context.getPlaylists().geniusPlaylistsSize() > 0) {
            return 3;
         } else {
            return 2;
         }
      } else if (parent instanceof String) {
         if (parent == LibraryNode) {
            int specialSize = context.getPlaylists().specialPlaylistsSize() + 1;
            if (context.getSession().supportsRadio()) {
               specialSize++;
            }
            return specialSize;
         } else if (parent == GeniusNode) {
            return context.getPlaylists().geniusPlaylistsSize();
         } else if (parent == PlaylistNode) {
            return context.getPlaylists().playlistsSize();
         }
      } else if (parent instanceof IPlaylist) {
         IPlaylist playlist = (IPlaylist) parent;
         return playlist.nestedSize();
      }
      
      return 0;
   }

   @Override
   public int getIndexOfChild(Object parent, Object child) {
      if (parent == this && child instanceof String) {
         if (child == LibraryNode) return 0;
         else if (child == GeniusNode) return 1;
         else if (child == PlaylistNode) return 2;
         else return -1;
      } else if (parent == LibraryNode) {
         int specialSize = context.getPlaylists().specialPlaylistsSize() + 1;
         if (context.getSession().supportsRadio()) {
            specialSize++;
         }
         
         if (child instanceof LibraryInfo) {
            return 0;
         } else if (child instanceof RadiolistList) {
            return specialSize - 1;
         } else if (child instanceof IPlaylist) {
            IPlaylist playlist = (IPlaylist) child;
            return context.getPlaylists().indexOfSpecialPlaylist(playlist) + 1;
         } else {
            return -1;
         }
      } else if (parent == GeniusNode) {
         IPlaylist playlist = (IPlaylist) child;
         return context.getPlaylists().indexOfGeniusPlaylist(playlist);
      } else if (parent == PlaylistNode) {
         IPlaylist playlist = (IPlaylist) child;
         return context.getPlaylists().indexOfPlaylist(playlist);
      } else if (parent instanceof IPlaylist && child instanceof IPlaylist) {
         IPlaylist parentlist = (IPlaylist) parent;
         IPlaylist childlist = (IPlaylist) child;
         return parentlist.nestedIndexOf(childlist);
      }
      
      return -1;
   }


   @Override
   public Object getRoot() {
      return this;
   }


   @Override
   public boolean isLeaf(Object node) {
      if (node instanceof IPlaylist) {
         IPlaylist playlist = (IPlaylist) node;
         return playlist.nestedSize() == 0;
      } else if (node instanceof RadiolistList) {
         return true;
      } else if (node instanceof LibraryInfo) {
         return true;
      }
      return false;
   }

   @Override
   public void valueForPathChanged(TreePath path, Object newValue) {
      // TODO Auto-generated method stub
      
   }
}
