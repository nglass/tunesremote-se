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
package net.firefly.client.gui.swing.other;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import net.firefly.client.controller.ListManager;
import net.firefly.client.controller.ResourceManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.context.events.GlobalSongListChangedEvent;
import net.firefly.client.gui.context.events.SelectedPlaylistChangedEvent;
import net.firefly.client.gui.context.listeners.GlobalSongListChangedEventListener;
import net.firefly.client.gui.context.listeners.SelectedPlaylistChangedEventListener;
import net.firefly.client.gui.swing.menu.SearchFieldContextMenu;
import net.firefly.client.model.data.list.AlbumList;
import net.firefly.client.model.data.list.ArtistList;
import net.firefly.client.model.data.list.GenreList;
import net.firefly.client.model.data.list.SongList;
import net.firefly.client.model.playlist.IPlaylist;

public class SearchField extends JPanel implements SelectedPlaylistChangedEventListener, GlobalSongListChangedEventListener {

	private static final long serialVersionUID = 3357926731545923178L;

	private Context context;
	
	private ImageIcon leftOff;

	private ImageIcon leftOn;

	private ImageIcon rightOff;

	private ImageIcon rightOn;

	private ImageIcon rightClearOff;

	private ImageIcon rightClearOn;

	private ImageIcon backgroundOff;

	private ImageIcon backgroundOn;

	private JLabel searchLabel;

	private JLabel leftLabel;

	private JLabel rightLabel;

	private BackgroundPanel backgroundPanel;

	private JTextField searchTextField;

	private SongList songListToSearch;
	
	private SearchFieldContextMenu menu;
	
	private TimerTask searchTask;

	private Timer timer;
	
	public SearchField(Context context) {
		this.context = context;
		initialize();
	}

	private void initialize() {

		context.addSelectedPlaylistChangedEventListener(this);
		context.addGlobalSongListChangedEventListener(this);

		this.leftOff = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/search-left-option-off.png"));
		this.leftOn = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/search-left-option-on.png"));

		this.rightOff = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/search-right-off.png"));
		this.rightOn = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/search-right-on.png"));

		this.rightClearOff = new ImageIcon(getClass().getResource(
				"/net/firefly/client/resources/images/search-right-clear-off.png"));
		this.rightClearOn = new ImageIcon(getClass().getResource(
				"/net/firefly/client/resources/images/search-right-clear-on.png"));

		this.backgroundOff = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/search-bg-off.png"));
		this.backgroundOn = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/search-bg-on.png"));

		this.searchLabel = new JLabel(ResourceManager.getLabel("menu.search.label", context.getConfig().getLocale()));
		this.leftLabel = new JLabel();
		leftLabel.setOpaque(false);
		leftLabel.setVerticalAlignment(SwingConstants.CENTER);
		leftLabel.setIcon(this.leftOff);
		leftLabel.setBackground(null);
		leftLabel.setIconTextGap(0);
		leftLabel.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
		

		this.rightLabel = new JLabel();
		rightLabel.setOpaque(false);
		rightLabel.setVerticalAlignment(SwingConstants.CENTER);
		rightLabel.setIcon(this.rightOff);
		rightLabel.setBackground(null);
		rightLabel.setIconTextGap(0);
		rightLabel.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));

		rightLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				searchTextField.requestFocus();
				if (rightLabel.getIcon().equals(rightClearOff) || rightLabel.getIcon().equals(rightClearOn)) {
					searchTextField.setText("");
					context.setSearchmask(null);
					rightLabel.setIcon(rightOn);
					context.setFilteredSongList(songListToSearch);
					context.setFilteredGenreList(ListManager.extractGenreList(songListToSearch, context.getConfig()
							.getLocale()));
					context.setFilteredArtistList(ListManager.extractArtistList(songListToSearch, context.getConfig()
							.getLocale()));
					context.setFilteredAlbumList(ListManager.extractAlbumList(songListToSearch, context.getConfig()
							.getLocale()));

					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
						}
					});
				}
			}
		});

		menu = new SearchFieldContextMenu(context, this);
		leftLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				menu.show(SearchField.this, leftLabel.getX() + 13, leftLabel.getY() + SearchField.this.leftOn.getIconHeight() - 1);
			}
		});

		this.backgroundPanel = new BackgroundPanel();

		this.searchTextField = new JTextField(15);
		searchTextField.setBorder(new EmptyBorder(0, 0, 0, 0));
		searchTextField.setPreferredSize(new Dimension(90, 15));

		this.searchTextField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				searchTask.cancel();
				searchTask = new SearchTimerTask();
				timer.schedule(searchTask, 250);
			}

			public void keyTyped(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
				
			}
		});

		this.searchTextField.addFocusListener(new FieldFocusListener());

		GridBagConstraints searchTextFieldGBC = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
		
		backgroundPanel.add(searchTextField, searchTextFieldGBC);

		GridBagConstraints searchLabelGBC = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);

		GridBagConstraints leftLabelGBC = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);

		GridBagConstraints backgroundPanelGBC = new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);

		GridBagConstraints rightLabelGBC = new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);

		setLayout(new GridBagLayout());

		add(searchLabel, searchLabelGBC);
		add(leftLabel, leftLabelGBC);
		add(backgroundPanel, backgroundPanelGBC);
		add(rightLabel, rightLabelGBC);
		
		this.timer = new Timer();
		this.searchTask = new SearchTimerTask();
	}
	
	public void refresh(){
		searchTask.cancel();
		searchTask = new SearchTimerTask();
		timer.schedule(searchTask, 250);
	}

	class BackgroundPanel extends JPanel {

		private static final long serialVersionUID = -2022928639272871093L;
		
		protected ImageIcon background;

		public BackgroundPanel() {
			super();
			background = backgroundOff;
			Dimension d = new Dimension(120, background.getIconHeight());
			setSize(d);
			setPreferredSize(d);
			setMinimumSize(d);
			setMaximumSize(d);
			setBorder(new EmptyBorder(3, 0, 1, 0));
			setLayout(new GridBagLayout());
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			int x, y;
			int width, height;

			Rectangle clip = g.getClipBounds();

			width = background.getIconWidth();
			height = background.getIconHeight();

			if (width > 0 && height > 0) {
				for (x = clip.x; x < (clip.x + clip.width); x += width) {
					for (y = clip.y; y < (clip.y + clip.height); y += height) {
						g.drawImage(background.getImage(), x, y, this);
					}
				}
			}
		}

		public void setBackground(ImageIcon background) {
			this.background = background;
		}
	}

	class FieldFocusListener extends FocusAdapter {
		public void focusGained(FocusEvent focusEvent) {
			leftLabel.setIcon(leftOn);
			if (rightLabel.getIcon().equals(rightClearOff)) {
				rightLabel.setIcon(rightClearOn);
			} else if (rightLabel.getIcon().equals(rightOff)) {
				rightLabel.setIcon(rightOn);
			}
			backgroundPanel.setBackground(backgroundOn);
			backgroundPanel.repaint();
			JTextField field = (JTextField) focusEvent.getSource();
			field.selectAll();
		}

		public void focusLost(FocusEvent arg0) {
			leftLabel.setIcon(leftOff);
			if (rightLabel.getIcon().equals(rightClearOn)) {
				rightLabel.setIcon(rightClearOff);
			} else if (rightLabel.getIcon().equals(rightOn)) {
				rightLabel.setIcon(rightOff);
			}
			backgroundPanel.setBackground(backgroundOff);
			backgroundPanel.repaint();
		}
	}
	
	class SearchTimerTask extends TimerTask {
		public void run() {
			if (songListToSearch == null){
				return;
			}
			String text = searchTextField.getText();
			context.setSearchmask(text);
			int searchFieldLength = searchTextField.getText().trim().length();
			if (searchFieldLength > 0) {
				rightLabel.setIcon(rightClearOn);
				SongList filteredSongList = ListManager.filterList(songListToSearch, text, context.getConfig().getSearchFlag());
				GenreList filteredGenreList = ListManager.extractGenreList(filteredSongList, context.getConfig()
						.getLocale());
				ArtistList filteredArtistList = ListManager.extractArtistList(filteredSongList, context.getConfig()
						.getLocale());
				AlbumList filteredAlbumList = ListManager.extractAlbumList(filteredSongList, context.getConfig()
						.getLocale());
				context.setFilteredGenreList(filteredGenreList);
				context.setFilteredArtistList(filteredArtistList);
				context.setFilteredAlbumList(filteredAlbumList);
				context.setFilteredSongList(filteredSongList);

			} else if (searchFieldLength == 0) {
				rightLabel.setIcon(rightOn);
				if (songListToSearch != null) {
					context.setFilteredSongList(songListToSearch);
					context.setFilteredGenreList(ListManager.extractGenreList(songListToSearch, context.getConfig()
							.getLocale()));
					context.setFilteredArtistList(ListManager.extractArtistList(songListToSearch, context.getConfig()
							.getLocale()));
					context.setFilteredAlbumList(ListManager.extractAlbumList(songListToSearch, context.getConfig()
							.getLocale()));
				}
			} else {
				rightLabel.setIcon(rightOn);
			}
		}
	}

	public void onSelectedPlaylistChange(SelectedPlaylistChangedEvent evt) {
		IPlaylist p = evt.getNewSelectedPlaylist();
		if (p == null) {
			// -- library is selected
			this.songListToSearch = context.getGlobalSongList();
		} else {
			this.songListToSearch = p.getSongList();
		}
		this.searchTextField.setText("");
		this.context.setSearchmask(null);
	}

	public void onGlobalSongListChange(GlobalSongListChangedEvent evt) {
		this.searchTextField.setText("");
		this.context.setSearchmask(null);
		this.songListToSearch = SongList.EMPTY_SONG_LIST;
	}
	
	public void focus(){
		searchTextField.requestFocus();
	}
}
