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
package net.firefly.client.gui.swing.table;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.Collator;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.swing.table.dnd.SongTranferHandler;
import net.firefly.client.gui.swing.table.menu.PlaylistContextMenu;
import net.firefly.client.gui.swing.table.model.SongTableModel;
import net.firefly.client.gui.swing.table.model.TableSorter;
import net.firefly.client.gui.swing.table.renderer.AlbumCellRenderer;
import net.firefly.client.gui.swing.table.renderer.ArtistCellRenderer;
import net.firefly.client.gui.swing.table.renderer.ColumnZeroRenderer;
import net.firefly.client.gui.swing.table.renderer.DefaultCellRenderer;
import net.firefly.client.gui.swing.table.renderer.NumberCellRenderer;
import net.firefly.client.gui.swing.table.renderer.TimeCellRenderer;
import net.firefly.client.model.data.SongContainer;
import net.firefly.client.model.playlist.IPlaylist;
import net.firefly.client.model.playlist.StaticPlaylist;
import net.firefly.client.player.events.SongChangedEvent;
import net.firefly.client.player.listeners.SongChangedEventListener;

public class SongTable extends JTable implements SongChangedEventListener {

	private static final long serialVersionUID = -4037076767371823099L;

	protected Context context;
	
	protected Frame rootContainer;
	
	protected PlaylistContextMenu contextMenu;

	public SongTable(Frame rootContainer, Context context) {
		this.rootContainer = rootContainer;
		this.context = context;
		initialize();
	}

	public void playSong() {
		final int selectedRowIndex = getSelectedRow();
		final int modelIndex = ((TableSorter)getModel()).modelIndex(selectedRowIndex);
		if (modelIndex != -1) {
			context.getFilteredSongList().selectSong(modelIndex);
			context.getPlayer().stopPlayback();
			context.getPlayer().play();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					selectionModel.setSelectionInterval(selectedRowIndex, selectedRowIndex);
				}
			});
		}
	}
	
	private void initialize() {
		// -- table model
		TableSorter tableSorter = new TableSorter(new SongTableModel(rootContainer, context), context);
		tableSorter.setColumnComparator(String.class, Collator.getInstance(Locale.FRANCE));
		tableSorter.setSortingDirectives(context.getConfig().getSongListSortingCriteria());
		setModel(tableSorter);
		
		context.setTableSorter(tableSorter);

		// -- show vertical lines only
		setShowGrid(false);
		setShowHorizontalLines(false);
		setShowVerticalLines(true);
		setGridColor(new Color(217, 217, 217)); // -- vertical lines colors
		setIntercellSpacing(new Dimension(1, 0)); // -- space needed by
		// vertical line

		// -- selection mode
		// setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		// -- unplugged table selection from songlist selection
		// SongSelectionChangedListener listener = new
		// SongSelectionChangedListener(this, context);
		// getSelectionModel().addListSelectionListener(listener);

		// -- fixed columns (bo reordering
		getTableHeader().setReorderingAllowed(false);

		// column size
		// setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		TableColumn iconColumn = getColumnModel().getColumn(0);
		iconColumn.setPreferredWidth(26);
		iconColumn.setMaxWidth(26);
		iconColumn.setMinWidth(26);
		iconColumn.setCellRenderer(new ColumnZeroRenderer(JLabel.CENTER, true));

		TableColumn titleColumn = getColumnModel().getColumn(1);
		titleColumn.setPreferredWidth(255);
		titleColumn.setCellRenderer(new DefaultCellRenderer(JLabel.LEFT, true));

		TableColumn timeColumn = getColumnModel().getColumn(2);
		timeColumn.setPreferredWidth(53);
		timeColumn.setMaxWidth(53);
		timeColumn.setMinWidth(53);
		timeColumn.setCellRenderer(new TimeCellRenderer(JLabel.RIGHT, true, context));

		TableColumn artistColumn = getColumnModel().getColumn(3);
		artistColumn.setPreferredWidth(185);
		artistColumn.setCellRenderer(new ArtistCellRenderer(JLabel.LEFT, true, context));

		TableColumn albumColumn = getColumnModel().getColumn(4);
		albumColumn.setPreferredWidth(185);
		albumColumn.setCellRenderer(new AlbumCellRenderer(JLabel.LEFT, true, context));

		TableColumn yearColumn = getColumnModel().getColumn(5);
		yearColumn.setPreferredWidth(52);
		yearColumn.setMaxWidth(52);
		yearColumn.setMinWidth(52);
		yearColumn.setCellRenderer(new NumberCellRenderer(JLabel.RIGHT, true, context));

		TableColumn trackNumberColumn = getColumnModel().getColumn(6);
		trackNumberColumn.setPreferredWidth(30);
		trackNumberColumn.setMaxWidth(30);
		trackNumberColumn.setMinWidth(30);
		trackNumberColumn.setCellRenderer(new NumberCellRenderer(JLabel.RIGHT, true, context));

		TableColumn discNumberColumn = getColumnModel().getColumn(7);
		discNumberColumn.setPreferredWidth(37);
		discNumberColumn.setMaxWidth(37);
		discNumberColumn.setMinWidth(37);
		discNumberColumn.setCellRenderer(new NumberCellRenderer(JLabel.RIGHT, true, context));

		TableColumn lastColumn = getColumnModel().getColumn(8);
		lastColumn.setMinWidth(0);
		lastColumn.setMaxWidth(2000);
		lastColumn.setCellRenderer(new DefaultCellRenderer(JLabel.LEFT, true));

		tableSorter.setTableHeader(getTableHeader());
		
		contextMenu = new PlaylistContextMenu(context, this, rootContainer);
		
		addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(final java.awt.event.MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() >= 2) {
					playSong();
				} else if (SwingUtilities.isRightMouseButton(e)){
					IPlaylist p = context.getSelectedPlaylist();
					if (p != null && p instanceof StaticPlaylist){
						int clickedRowIndex = SongTable.this.rowAtPoint(e.getPoint());
						if (SongTable.this.isRowSelected(clickedRowIndex)){
							contextMenu.show(SongTable.this, e.getX(), e.getY());
						} else {
							selectionModel.setSelectionInterval(clickedRowIndex, clickedRowIndex);
							contextMenu.show(SongTable.this, e.getX(), e.getY());
						}
					}
				}
			}
		});

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					e.consume();
				}
			}
			
			public void keyReleased(KeyEvent e) {
				int keycode = e.getKeyCode();
				if (keycode == KeyEvent.VK_ENTER) {
					playSong();
				}
				else if (keycode == KeyEvent.VK_DELETE) {
					IPlaylist p = context.getSelectedPlaylist();
					if (p != null && p instanceof StaticPlaylist){
						int [] selectedRows = getSelectedRows();
						if (selectedRows.length > 0){
							SongContainer[] songs = new SongContainer[selectedRows.length];
							for (int i=0; i<selectedRows.length; i++){
								songs[i] = p.getSongList().get(selectedRows[i]);
							}
							((SongTableModel)((TableSorter)getModel()).getTableModel()).removeSongs(songs);
						}
					}
				}
			}
		});

		setTransferHandler(new SongTranferHandler(context));
		setDragEnabled(true);

		context.getPlayer().addSongChangedEventListener(this);
	}

	protected void scrollToVisible(JTable table, int rowIndex) {
		if (!(table.getParent() instanceof JViewport)) {
			return;
		}
		JViewport viewport = (JViewport) table.getParent();
		Rectangle rect = table.getCellRect(rowIndex, 0, true);
		Point pt = viewport.getViewPosition();
		rect.setLocation(rect.x - pt.x, rect.y - pt.y);
		viewport.scrollRectToVisible(rect);
	}

	public void onSongChange(SongChangedEvent evt) {
		SongContainer sc = evt.getSongPlayed();
		if (sc != null) {
			int index = context.getFilteredSongList().getSelectedIndex();
			if (index > -1 && index < context.getFilteredSongList().size()) {
				// -- scroll the current played song row into view
				int viewIndex = ((TableSorter)getModel()).viewIndex(index);
				scrollToVisible(this, viewIndex);
				repaint();
			}
		}

	}
}
