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
package net.firefly.client.gui.swing.table.model;

import java.awt.Frame;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import net.firefly.client.controller.ListManager;
import net.firefly.client.controller.ResourceManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.context.events.FilteredSongListChangedEvent;
import net.firefly.client.gui.context.listeners.FilteredSongListChangedEventListener;
import net.firefly.client.gui.swing.dialog.ErrorDialog;
import net.firefly.client.model.data.Album;
import net.firefly.client.model.data.Artist;
import net.firefly.client.model.data.ColumnZero;
import net.firefly.client.model.data.Song;
import net.firefly.client.model.data.SongContainer;
import net.firefly.client.tools.FireflyClientException;

public class SongTableModel extends AbstractTableModel implements FilteredSongListChangedEventListener {

	private static final long serialVersionUID = 8371063200383599671L;

	protected Context context;
	
	protected Frame rootContainer;

	protected String titleColumnName;
	protected String timeColumnName;
	protected String artistColumnName;
	protected String albumColumnName;
	protected String yearColumnName;
	protected String trackNumberColumnName;
	protected String discNumberColumnName;
	
	protected ImageIcon playingImage;
	
	public SongTableModel(Frame rootContainer, Context context) {
		this.context = context;
		this.rootContainer = rootContainer;
		this.titleColumnName = ResourceManager.getLabel("table.song.column.title", context.getConfig().getLocale());
		this.timeColumnName = ResourceManager.getLabel("table.song.column.time", context.getConfig().getLocale());
		this.artistColumnName = ResourceManager.getLabel("table.song.column.artist", context.getConfig().getLocale());
		this.albumColumnName = ResourceManager.getLabel("table.song.column.album", context.getConfig().getLocale());
		this.yearColumnName = ResourceManager.getLabel("table.song.column.year", context.getConfig().getLocale());
		this.trackNumberColumnName = ResourceManager.getLabel("table.song.column.track.number", context.getConfig()
				.getLocale());
		this.discNumberColumnName = ResourceManager.getLabel("table.song.column.disc.number", context.getConfig()
				.getLocale());
		
		this.playingImage = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/playing.png"));

		context.addFilteredSongListChangedEventListener(this);
	}

	public int getColumnCount() {
		return 8;
	}

	public int getRowCount() {
		if (context.getFilteredSongList() != null) {
			return context.getFilteredSongList().size();
		}
		return 0;
	}
	
	public Object getValueAt(int row, int col) {
		if (context.getFilteredSongList() != null && row > -1 && row < context.getFilteredSongList().size()) {
			SongContainer sc = context.getFilteredSongList().get(row);
			Song s = sc.getSong();
			switch (col) {
			case 0:
				if (context.getPlayer().getPlayingSong() != null && context.getPlayer().getPlayingSong().equals(sc)) {
					return new ColumnZero(row, playingImage);
				} else {
					return new ColumnZero(row, null);
				}
			case 1:
				return s;
			case 2:
				return new Date(s.getTime());
			case 3:
				return s.getArtist();
			case 4:
				return s.getAlbum();
			case 5:
				int year = 0;
				try {
					year = Integer.parseInt(s.getYear().trim());
				} catch (Exception e){
				}
				return new Integer(year);
			case 6:
				int tn = 0;
				try {
					tn = Integer.parseInt(s.getTrackNumber().trim());
				} catch (Exception e){
				}
				return new Integer(tn);
			case 7:
				int dn = 0;
				try {
					dn = Integer.parseInt(s.getDiscNumber().trim());
				} catch (Exception e){
				}
				return new Integer(dn);
			default:
				return " ";
			}
		}
		return null;
	}

	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return " ";
		case 1:
			return this.titleColumnName;
		case 2:
			return this.timeColumnName;
		case 3:
			return this.artistColumnName;
		case 4:
			return this.albumColumnName;
		case 5:
			return this.yearColumnName;
		case 6:
			return this.trackNumberColumnName;
		case 7:
			return this.discNumberColumnName;
		default:
			return "";
		}
	}

	@SuppressWarnings("unchecked")
	public Class getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return ColumnZero.class;
		case 1:
			return Song.class;
		case 2:
			return Date.class;
		case 3:
			return Artist.class;
		case 4:
			return Album.class;
		case 5:
			return Integer.class;
		case 6:
			return Integer.class;
		case 7:
			return Integer.class;
		default:
			return String.class;
		}
	}

	public void removeSongs(SongContainer[] songs){
		try {
			this.context.getPlaylistRequestManager().removeSongsFromStaticPlaylist(context.getSelectedPlaylist().getPlaylistId(), songs, 
					context.getLibraryInfo().getHost(), context.getLibraryInfo().getPort(), 
					"", "", 
					context.getLibraryInfo().getPassword());
		} catch (FireflyClientException e) {
			ErrorDialog.showDialog(rootContainer, ResourceManager.getLabel("remove.songs.from.playlist.unexpected.error.message",
					context.getConfig().getLocale()), ResourceManager.getLabel(
					"remove.songs.from.playlist.unexpected.error.title", context.getConfig().getLocale()), e, context.getConfig()
					.getLocale());
			return;
		}
		for (int i=0; i<songs.length; i++){
			context.getSelectedPlaylist().getSongList().remove(songs[i]);
		}
		fireTableDataChanged();
		context.setFilteredAlbumList(ListManager.extractAlbumList(context.getFilteredSongList(), context.getConfig().getLocale()));
		context.setFilteredArtistList(ListManager.extractArtistList(context.getFilteredSongList(), context.getConfig().getLocale()));
		context.setFilteredGenreList(ListManager.extractGenreList(context.getFilteredSongList(), context.getConfig().getLocale()));
	}
	
	public void onFilteredSongListChange(FilteredSongListChangedEvent evt) {
		fireTableDataChanged();
	}
}
