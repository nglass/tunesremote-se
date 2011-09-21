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
package net.firefly.client.gui.swing.table.listeners;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.firefly.client.controller.ListManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.model.data.Album;
import net.firefly.client.model.data.Artist;
import net.firefly.client.model.data.Genre;
import net.firefly.client.model.data.list.SongList;

public class AlbumSelectionChangedListener implements ListSelectionListener {

	protected JTable albumTable;

	protected Context context;

	// It is necessary to keep the table since it is not possible
	// to determine the table from the event's source
	public AlbumSelectionChangedListener(JTable albumTable, Context context) {
		this.albumTable = albumTable;
		this.context = context;
	}

	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) {
			return;
		}
		ListSelectionModel lsm = (ListSelectionModel) e.getSource();
		if (lsm.isSelectionEmpty()) {
			// -- do nothing;
		} else {
			int[] selectedRows = albumTable.getSelectedRows();
			Set<Album> s = new HashSet<Album>();
			boolean allAlbumsSelected = false;
			for (int i=0; i<selectedRows.length; i++){
				Album a = (Album) albumTable.getModel().getValueAt(selectedRows[i], 0);
				if (selectedRows[i] == 0){
					allAlbumsSelected = true;
					continue;
				}
				s.add(a);
			}
			Album[] albums = (Album[])s.toArray(new Album[s.size()]);
			
			Artist[] artists = context.getSelectedArtists();
			boolean allArtistsSelected = context.isAllArtistsSelected();
			boolean compilationSelected = context.isCompilationSelected();;
			
			Genre[] genres = context.getSelectedGenres();
			boolean allGenresSelected = context.isAllGenresSelected();
			
			SongList sl = context.getGlobalSongList();
			if (context.getSelectedPlaylist() != null){
				sl = context.getSelectedPlaylist().getSongList();
			}

			if (context.getSearchmask() != null && context.getSearchmask().trim().length() > 0) {
				// -- search filter
				int searchFieldLength = context.getSearchmask().trim().length();
				if (searchFieldLength > 2) {
					sl = ListManager
							.filterList(sl, context.getSearchmask(),
									ListManager.FLG_SEARCH_GENRE + ListManager.FLG_SEARCH_ARTIST + ListManager.FLG_SEARCH_ALBUM 
											+ ListManager.FLG_SEARCH_TITLE);
				}
			}
			context.setFilteredSongList(ListManager.filterListByGenresAlbumsAndArtists(
					sl, allGenresSelected?null:genres, allAlbumsSelected?null:albums, 
					allArtistsSelected?null:artists, compilationSelected));
	
			context.setSelectedAlbums(albums);
			context.setAllAlbumsSelected(allAlbumsSelected);
		}
	}
}