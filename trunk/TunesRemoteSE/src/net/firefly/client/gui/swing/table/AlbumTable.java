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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

import net.firefly.client.controller.ListManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.swing.table.listeners.AlbumSelectionChangedListener;
import net.firefly.client.gui.swing.table.model.AlbumTableModel;
import net.firefly.client.gui.swing.table.model.TableSorter;
import net.firefly.client.gui.swing.table.renderer.AlbumCellRenderer;
import net.firefly.client.model.data.list.SongList;

public class AlbumTable extends JTable {
	
	private static final long serialVersionUID = 4515051176478254832L;

	private Context context;

	private TimerTask searchTask;

	private Timer timer;
	
	private String searchMask;
	
	public AlbumTable(Context context) {
		this.context = context;
		initialize();
	}

	public void playAlbum() {
		context.getPlayer().stopPlayback();
		SongList sl = context.getFilteredSongList();
		if (sl != null){
			TableSorter ts = context.getTableSorter();
			if (ts != null){
				int modelIndex = ts.modelIndex(0);
				if (modelIndex != -1) {
					context.getFilteredSongList().selectSong(modelIndex);
				}
			}
		}
		context.getPlayer().play();
	}
	
	private void initialize() {
		setModel(new AlbumTableModel(context));
		setShowGrid(false);
		setShowHorizontalLines(false);
		setShowVerticalLines(false);
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		getTableHeader().setReorderingAllowed(false);
		AlbumSelectionChangedListener listener = new AlbumSelectionChangedListener(this, context);
		getSelectionModel().addListSelectionListener(listener);
		setIntercellSpacing(new Dimension(0, 0));

		// column size
		// setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		TableColumn albumColumn = getColumnModel().getColumn(0);
		albumColumn.setPreferredWidth(250);
		albumColumn.setCellRenderer(new AlbumCellRenderer(JLabel.LEFT, false,context));

		addKeyListener(new AlbumTableKeyListener(this));

		addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() >= 2) {
					playAlbum();
				}
			}
		});
		
		this.timer = new Timer();
		this.searchTask = new SearchTimerTask(this);
		this.searchMask = "";

	}

	protected void scrollToVisible(JTable table, final int rowIndex) {
		if (!(table.getParent() instanceof JViewport)) {
			return;
		}
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				JViewport viewport = (JViewport) AlbumTable.this.getParent();
				Rectangle rect = AlbumTable.this.getCellRect(rowIndex, 0, true);
				Point pt = viewport.getViewPosition();
				rect.setLocation(rect.x - pt.x, rect.y - pt.y);
				viewport.scrollRectToVisible(rect);
				SwingUtilities.updateComponentTreeUI(getParent());		
			}
		});
	}

	class AlbumTableKeyListener extends KeyAdapter {

		private JTable table;

		public AlbumTableKeyListener(JTable table) {
			this.table = table;
		}

		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				e.consume();
			}
		}
		
		public void keyReleased(KeyEvent e) {
			char c = e.getKeyChar();
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == ' ') {
				searchMask += c;
				searchTask.cancel();
				searchTask = new SearchTimerTask(table);
				timer.schedule(searchTask, 250);
			}
			else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				playAlbum();
				e.consume();
			}
		}
	}
	
	class SearchTimerTask extends TimerTask {
		
		private JTable table;
		
		public SearchTimerTask(JTable table){
			this.table = table;
		}
		
		public void run() {
			int index = ListManager.getFirstAlbumIndexStartingWith(context.getFilteredAlbumList(), searchMask);
			if (index != -1) {
				// -- selection the given row
				table.getSelectionModel().clearSelection();
				table.getSelectionModel().addSelectionInterval(index, index);
				// -- scroll the given row into view (at the top of the viewport)
				scrollToVisible(table, context.getFilteredAlbumList().size() - 1); 
				// -- trick to have the row at the top
				scrollToVisible(table, index);
			}
			searchMask = "";
		}
	}
}
