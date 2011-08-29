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
//import java.text.Collator;
//import java.util.Locale;

import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.netbeans.swing.etable.ETable;

import net.firefly.client.gui.context.Context;
//import net.firefly.client.gui.swing.table.dnd.SongTranferHandler;
import net.firefly.client.gui.swing.table.menu.PlaylistContextMenu;
import net.firefly.client.gui.swing.table.model.SongTableColumnModel;
import net.firefly.client.gui.swing.table.model.SongTableModel;
import net.firefly.client.model.data.SongContainer;
import net.firefly.client.model.playlist.IPlaylist;
import net.firefly.client.model.playlist.StaticPlaylist;
import net.firefly.client.player.events.SongChangedEvent;
import net.firefly.client.player.listeners.SongChangedEventListener;

public class SongTable extends ETable implements SongChangedEventListener {

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
		final int selectedModelIndex = convertRowIndexToModel(selectedRowIndex);
		
		if (selectedRowIndex != -1) {
			context.getFilteredSongList().selectSong(selectedModelIndex);
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
		setModel(new SongTableModel(rootContainer, context));

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
		
		setColumnModel(new SongTableColumnModel(this, context));
		
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
							((SongTableModel)getModel()).removeSongs(songs);
						}
					}
				}
			}
		});

		// TODO: reenable drag when you get playlists going
		//setTransferHandler(new SongTranferHandler(context));
		//setDragEnabled(true);

		context.getPlayer().addSongChangedEventListener(this);
	}

	protected void scrollToVisible(JTable table, int rowIndex) {
		if (!(table.getParent() instanceof JViewport)) {
			return;
		}
		
		int viewIndex = this.convertRowIndexToView(rowIndex);
		JViewport viewport = (JViewport) table.getParent();
		Rectangle rect = table.getCellRect(viewIndex, 0, true);
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
				scrollToVisible(this, index);
				repaint();
			}
		}

	}
}
