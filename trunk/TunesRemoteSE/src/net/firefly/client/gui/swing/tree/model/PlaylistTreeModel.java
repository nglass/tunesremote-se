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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.firefly.client.controller.ResourceManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.context.events.PlaylistListChangedEvent;
import net.firefly.client.gui.context.events.StaticPlaylistCreationEvent;
import net.firefly.client.gui.context.listeners.PlaylistListChangedEventListener;
import net.firefly.client.gui.context.listeners.StaticPlaylistCreationEventListener;
import net.firefly.client.gui.swing.dialog.ErrorDialog;
import net.firefly.client.model.playlist.IPlaylist;
import net.firefly.client.model.playlist.PlaylistStatus;
import net.firefly.client.model.playlist.StaticPlaylist;
import net.firefly.client.model.playlist.list.PlaylistList;
import net.firefly.client.model.playlist.sorting.PlaylistComparator;
import net.firefly.client.tools.FireflyClientException;

public class PlaylistTreeModel extends DefaultTreeModel implements PlaylistListChangedEventListener,
		StaticPlaylistCreationEventListener {

	private static final long serialVersionUID = 7602180983197714855L;

	public static final DefaultMutableTreeNode ROOT = new DefaultMutableTreeNode("Root");

	protected Context context;

	protected Frame rootContainer;

	protected JTree tree;

	public PlaylistTreeModel(Context context, Frame rootContainer) {
		super(buildTree(context));
		this.context = context;
		this.rootContainer = rootContainer;
		context.addPlaylistListChangedEventListener(this);
		context.addStaticPlaylistCreationEventListener(this);
	}

	private static DefaultMutableTreeNode buildTree(Context c) {
		ROOT.removeAllChildren();
		if (c.getLibraryInfo() != null && c.getLibraryInfo().getLibraryName() != null) {
			DefaultMutableTreeNode library = new DefaultMutableTreeNode(ResourceManager.getLabel("playlist.tree.library",
					c.getConfig().getLocale()).toUpperCase(c.getConfig().getLocale()));
			DefaultMutableTreeNode playlists = new DefaultMutableTreeNode(ResourceManager.getLabel(
					"playlist.tree.playlists", c.getConfig().getLocale()).toUpperCase(c.getConfig().getLocale()));
			DefaultMutableTreeNode music = new DefaultMutableTreeNode(c.getLibraryInfo().getLibraryName());
			ROOT.add(library);
			library.add(music);
			DefaultMutableTreeNode aPlaylist = null;
			PlaylistList pll = c.getPlaylists();
			if (pll != null && pll.size() > 0) {
				ROOT.add(playlists);
				Iterator<IPlaylist> it = pll.iterator();
				while (it.hasNext()) {
					aPlaylist = new DefaultMutableTreeNode(it.next());
					playlists.add(aPlaylist);
				}
			}
		}
		return ROOT;
	}

	/**
	 * Create playlists root node if still not, add the playlist in the tree,
	 * and trigger its immediate edition.
	 */
	public void addPlaylist(final IPlaylist playlist) throws FireflyClientException {
		int newPlaylistId = context.getPlaylistRequestManager().addStaticPlaylist(playlist.getPlaylistName(), context.getLibraryInfo()
				.getHost(), context.getLibraryInfo().getPort(), "","", context.getLibraryInfo().getPassword());
		playlist.setPlaylistId(newPlaylistId);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(playlist);
				TreePath playlistRootNodeTreePath = tree.getPathForRow(2);
				if (playlistRootNodeTreePath == null) {
					// -- create playlist root node
					DefaultMutableTreeNode playlists = new DefaultMutableTreeNode(ResourceManager.getLabel(
							"playlist.tree.playlists", context.getConfig().getLocale()).toUpperCase(
							context.getConfig().getLocale()));
					ROOT.add(playlists);
					nodesWereInserted(ROOT, new int[] { 1 });
				}
				DefaultMutableTreeNode playlistRootNode = (DefaultMutableTreeNode) (tree.getPathForRow(2)
						.getLastPathComponent());
				int insertionPoint = getPlaylistInsertionPoint(playlistRootNode, playlist);
				insertNodeInto(node, playlistRootNode, insertionPoint);
				TreePath tp = new TreePath(node.getPath());
				tree.setSelectionPath(tp);
				tree.makeVisible(tp);
				tree.scrollPathToVisible(tp);
				tree.startEditingAtPath(tp);
			}
		});
	}

	/**
	 * Remove the playlist from the tree, remove playlists root node if no
	 * playlist left to display
	 * 
	 * @throws FireflyClientException
	 */
	public void removePlaylist(IPlaylist playlist) throws FireflyClientException {
		context.getPlaylistRequestManager().deleteStaticPlaylist(playlist.getPlaylistId(), context.getLibraryInfo().getHost(), context
				.getLibraryInfo().getPort(), "","", context
				.getLibraryInfo().getPassword());
		Enumeration e = ROOT.breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			final DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
			if (node.getUserObject() instanceof IPlaylist) {
				final IPlaylist p = (IPlaylist) node.getUserObject();
				if (p.getPlaylistId() == playlist.getPlaylistId()) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
							removeNodeFromParent(node);
							context.getPlaylists().remove(p);
							if (parentNode.getChildCount() == 0) {
								// -- No more playlist, remove the playlists
								// root node
								removeNodeFromParent(parentNode);
							}
							// -- select the library
							tree.setSelectionPath(tree.getPathForRow(1));
						}
					});
					break;
				}
			}
		}
	}

	/**
	 * Rename a playlist, and move it at the right place (regarding
	 * PlaylistComparator)
	 */
	public void renamePlaylist(IPlaylist playlist, String newName) throws FireflyClientException {
		context.getPlaylistRequestManager().renameStaticPlaylist(playlist.getPlaylistId(), newName, context.getLibraryInfo().getHost(),
				context.getLibraryInfo().getPort(), "","",
				context.getLibraryInfo().getPassword());
		Enumeration e = ROOT.breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			final DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
			if (node.getUserObject() instanceof IPlaylist) {
				final IPlaylist p = (IPlaylist) node.getUserObject();
				// if (playlist.getPlaylistId() == p.getPlaylistId()) {
				if (playlist == p) {
					p.setPlaylistName(newName);
					context.getPlaylists().sort();
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
							removeNodeFromParent(node);
							int insertionPoint = getPlaylistInsertionPoint(parentNode, p);
							insertNodeInto(node, parentNode, insertionPoint);
							TreePath tp = new TreePath(node.getPath());
							tree.setSelectionPath(tp);
							tree.makeVisible(tp);
							tree.scrollPathToVisible(tp);
						}
					});
					break;
				}
			}
		}
	}

	private int getPlaylistInsertionPoint(TreeNode playlistRootNode, IPlaylist playlist) {
		int index = 0;
		Enumeration e = playlistRootNode.children();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
			IPlaylist p = (IPlaylist) node.getUserObject();
			if (PlaylistComparator.getInstance().compare(playlist, p) <= 0 && playlist.getPlaylistId() != p.getPlaylistId()) {
				break;
			}
			index++;
		}
		return index;
	}

	public void onPlaylistListChange(PlaylistListChangedEvent evt) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setRoot(buildTree(context));
			}
		});
	}

	private String getNewPlaylistName(int from) {
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
			Iterator<IPlaylist> it = pl.iterator();
			while (it.hasNext()) {
				IPlaylist p = it.next();
				if (playlistName.equals(p.getPlaylistName())) {
					return true;
				}
			}
		}
		return false;
	}

	private static Random random = new Random();

	public void onStaticPlaylistCreation(StaticPlaylistCreationEvent evt) {
		StaticPlaylist playlist = new StaticPlaylist(context.getMasterSongList(), false);
		playlist.setStatus(PlaylistStatus.LOADED);
		playlist.setPlaylistId(random.nextInt());
		int index = 1;
		int limit = 20;
		boolean success = false;
		while (!success && index < limit) {
			playlist.setPlaylistName(getNewPlaylistName(index));
			try {
				addPlaylist(playlist);
				context.getPlaylists().add(playlist);
				success = true;
			} catch (FireflyClientException e) {
				String message = e.getMessage();
				if (message != null && message.indexOf("Duplicate Playlist") != -1) {
					index++;
				} else {
					index = limit;
					ErrorDialog.showDialog(rootContainer, ResourceManager.getLabel("add.playlist.unexpected.error.message",
							context.getConfig().getLocale()), ResourceManager.getLabel(
							"add.playlist.unexpected.error.title", context.getConfig().getLocale()), e, context.getConfig()
							.getLocale());
				}
			}
		}
	}

	public void setTree(JTree tree) {
		this.tree = tree;
	}

}
