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
package net.firefly.client.gui.swing.table.menu;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.firefly.client.controller.ResourceManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.swing.table.SongTable;
import net.firefly.client.gui.swing.table.model.SongTableModel;
import net.firefly.client.model.data.SongContainer;
import net.firefly.client.model.playlist.IPlaylist;
import net.firefly.client.model.playlist.StaticPlaylist;

public class PlaylistContextMenu extends JPopupMenu {

	private static final long serialVersionUID = -7258508887924787282L;

	protected Context context;

	protected Frame rootContainer;

	protected SongTable songTable;
	
	protected JMenuItem deleteSelectedItemsMenuItem;

	public PlaylistContextMenu(Context context,  SongTable songTable, Frame rootContainer) {
		this.context = context;
		this.songTable = songTable;
		this.rootContainer = rootContainer;
		initialize();
	}

	private void initialize() {

		// -- Menu items
		deleteSelectedItemsMenuItem = new JMenuItem();
		deleteSelectedItemsMenuItem.setText(ResourceManager.getLabel("playlist.tree.popup.menu.delete", context.getConfig()
				.getLocale()));
		deleteSelectedItemsMenuItem.addActionListener(new DeletePlaylistItemsActionListener());
		// -- Add the menu items
		add(deleteSelectedItemsMenuItem);
	}

	class DeletePlaylistItemsActionListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			IPlaylist p = context.getSelectedPlaylist();
			if (p != null && p instanceof StaticPlaylist){
				int [] selectedRows = songTable.getSelectedRows();
				if (selectedRows.length > 0){
					SongContainer[] songs = new SongContainer[selectedRows.length];
					for (int i=0; i<selectedRows.length; i++){
						songs[i] = p.getSongList().get(selectedRows[i]);
					}
					((SongTableModel)songTable.getModel()).removeSongs(songs);
				}
			}
		}
	}
}
