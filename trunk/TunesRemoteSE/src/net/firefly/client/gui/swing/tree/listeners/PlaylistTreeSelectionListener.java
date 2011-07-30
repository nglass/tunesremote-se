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
package net.firefly.client.gui.swing.tree.listeners;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import net.firefly.client.controller.ListManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.swing.tree.PlaylistTree;
import net.firefly.client.model.playlist.IPlaylist;

public class PlaylistTreeSelectionListener implements TreeSelectionListener {

	protected Context context;

	protected PlaylistTree tree;

	public PlaylistTreeSelectionListener(PlaylistTree tree, Context context) {
		this.context = context;
		this.tree = tree;
	}

	public void valueChanged(TreeSelectionEvent e) {
	   Object o = tree.getLastSelectedPathComponent();
		if (o instanceof IPlaylist) {
			// a playlist is selected
			IPlaylist pl = (IPlaylist)o;
			context.setFilteredSongList(pl.getSongList());
			context.setFilteredGenreList(ListManager.extractGenreList(pl.getSongList(), context.getConfig().getLocale()));
			context.setFilteredArtistList(ListManager.extractArtistList(pl.getSongList(), context.getConfig().getLocale()));
			context.setFilteredAlbumList(ListManager.extractAlbumList(pl.getSongList(), context.getConfig().getLocale()));
			context.setSelectedPlaylist(pl);
		} else {
			// the library is selected
			context.setFilteredSongList(context.getGlobalSongList());
			context.setFilteredGenreList(context.getGlobalGenreList());
			context.setFilteredArtistList(context.getGlobalArtistList());
			context.setFilteredAlbumList(context.getGlobalAlbumList());
			context.setSelectedPlaylist(null);
		}
	}
}