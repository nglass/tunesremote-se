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
import net.firefly.client.model.data.Genre;
import net.firefly.client.model.data.list.SongList;

public class GenreSelectionChangedListener implements ListSelectionListener {

	protected JTable genreTable;

	protected Context context;

	// It is necessary to keep the table since it is not possible
	// to determine the table from the event's source
	public GenreSelectionChangedListener(JTable genreTable, Context context) {
		this.genreTable = genreTable;
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
			int[] selectedRows = genreTable.getSelectedRows();
			Set<Genre> s = new HashSet<Genre>();
			boolean allGenresSelected = false;
			for (int i=0; i<selectedRows.length; i++){
				Genre g = (Genre) genreTable.getModel().getValueAt(selectedRows[i], 0);
				if (selectedRows[i] == 0){
					allGenresSelected = true;
					continue;
				}
				s.add(g);
			}
			Genre[] genres = (Genre[])s.toArray(new Genre[s.size()]);
			
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
			
			if (allGenresSelected){
				context.setFilteredSongList(sl);	
			} else {
				context.setFilteredSongList(ListManager.filterListByGenresAlbumsAndArtists(
						sl, allGenresSelected?null:genres, null, null, false));
			}
			
			context.setFilteredArtistList(ListManager.extractArtistList(context.getFilteredSongList(), context.getConfig().getLocale()));
			context.setFilteredAlbumList(ListManager.extractAlbumList(context.getFilteredSongList(), context.getConfig().getLocale()));
			context.setSelectedGenres(genres);
			context.setAllGenresSelected(allGenresSelected);
		}
	}
}