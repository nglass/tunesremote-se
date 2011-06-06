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
package net.firefly.client.gui.swing.tree.menu;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.firefly.client.controller.ResourceManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.swing.dialog.ErrorDialog;
import net.firefly.client.gui.swing.tree.PlaylistTree;
import net.firefly.client.gui.swing.tree.model.PlaylistTreeModel;
import net.firefly.client.model.playlist.IPlaylist;
import net.firefly.client.model.playlist.SmartPlaylist;
import net.firefly.client.model.playlist.StaticPlaylist;
import net.firefly.client.tools.FireflyClientException;

public class PlaylistContextMenu extends JPopupMenu {

	private static final long serialVersionUID = -5431124312152478396L;

	protected Context context;

	protected PlaylistTree tree;

	protected Frame rootContainer;

	protected TreePath currentTreePath;

	protected JMenuItem renamePlaylistMenuItem;
	protected JMenuItem deletePlaylistMenuItem;

	public PlaylistContextMenu(Context context, PlaylistTree tree, Frame rootContainer) {
		this.context = context;
		this.tree = tree;
		this.rootContainer = rootContainer;
		initialize();
	}

	private void initialize() {

		// -- Menu items
		renamePlaylistMenuItem = new JMenuItem();
		renamePlaylistMenuItem.setText(ResourceManager.getLabel("playlist.tree.popup.menu.rename", context.getConfig()
				.getLocale()));
		renamePlaylistMenuItem.addActionListener(new RenamePlaylistActionListener());

		deletePlaylistMenuItem = new JMenuItem();
		deletePlaylistMenuItem.setText(ResourceManager.getLabel("playlist.tree.popup.menu.delete", context.getConfig()
				.getLocale()));
		deletePlaylistMenuItem.addActionListener(new DeletePlaylistActionListener());

		// -- Add the menu items
		add(renamePlaylistMenuItem);
		addSeparator();
		add(deletePlaylistMenuItem);
	}

	public TreePath getCurrentTreePath() {
		return currentTreePath;
	}

	public void setCurrentTreePath(TreePath currentTreePath) {
		this.currentTreePath = currentTreePath;
	}

	class RenamePlaylistActionListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			TreePath path = getCurrentTreePath();
			if (path != null) {
				tree.startEditingAtPath(path);
			}
		}
	}

	class DeletePlaylistActionListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {

			TreePath path = getCurrentTreePath();
			if (path != null) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
				Object value = node.getUserObject();
				if (value instanceof IPlaylist) {
					IPlaylist p = (IPlaylist) value;
					PlaylistTreeModel model = (PlaylistTreeModel) tree.getModel();
					if (value instanceof StaticPlaylist || value instanceof SmartPlaylist) {
						int result = JOptionPane.showConfirmDialog(rootContainer, ResourceManager.getLabel(
								"delete.playlist.dialog.message", context.getConfig().getLocale(), new String[] { value
										.toString() }), ResourceManager.getLabel("delete.playlist.dialog.title", context
								.getConfig().getLocale()), JOptionPane.YES_NO_OPTION);
						if (result == JOptionPane.OK_OPTION) {
							try {
								model.removePlaylist(p);
							} catch (FireflyClientException e){
								ErrorDialog.showDialog(rootContainer, ResourceManager.getLabel("remove.playlist.unexpected.error.message",
										context.getConfig().getLocale()), ResourceManager.getLabel("remove.playlist.unexpected.error.title",
										context.getConfig().getLocale()), e, context.getConfig().getLocale());
							}
						}
					}
				}
			}
		}
	}
}
