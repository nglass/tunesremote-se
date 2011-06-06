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
package net.firefly.client.gui.swing.menu;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;

import net.firefly.client.controller.ListManager;
import net.firefly.client.controller.ResourceManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.swing.other.SearchField;

public class SearchFieldContextMenu extends JPopupMenu {

	private static final long serialVersionUID = -6783592477488262451L;

	private SearchField searchField;

	protected Context context;

	private JMenuItem searchAllMenuItem;
	private JMenuItem searchGenreMenuItem;
	private JMenuItem searchArtistMenuItem;
	private JMenuItem searchAlbumMenuItem;
	private JMenuItem searchTitleMenuItem;

	public SearchFieldContextMenu(Context context, SearchField searchField) {
		this.context = context;
		this.searchField = searchField;
		initialize();
	}

	private void initialize() {

		// -- Menu items
		ButtonGroup group = new ButtonGroup();

		// -- give search filed the focus when the popup menu is hidden
		addPropertyChangeListener("visible", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (!((Boolean) event.getNewValue()).booleanValue()) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							searchField.focus();
						}
					});
				}
			}
		});

		JMenuItem searchMenuItem = new JMenuItem();
		searchMenuItem.setText(ResourceManager.getLabel("menu.search.label", context.getConfig().getLocale()));
		searchMenuItem.setFont(getFont().deriveFont(Font.ITALIC));
		searchMenuItem.setEnabled(false);

		searchAllMenuItem = new JRadioButtonMenuItem();
		searchAllMenuItem.setText(ResourceManager.getLabel("search.field.popup.menu.all", context.getConfig().getLocale()));
		searchAllMenuItem.addActionListener(new SearchTypeActionListener(ListManager.FLG_SEARCH_GENRE
				+ ListManager.FLG_SEARCH_ARTIST + ListManager.FLG_SEARCH_ALBUM + ListManager.FLG_SEARCH_TITLE));

		searchGenreMenuItem = new JRadioButtonMenuItem();
		searchGenreMenuItem.setText(ResourceManager.getLabel("search.field.popup.menu.genre", context.getConfig()
				.getLocale()));
		searchGenreMenuItem.addActionListener(new SearchTypeActionListener(ListManager.FLG_SEARCH_GENRE));

		searchArtistMenuItem = new JRadioButtonMenuItem();
		searchArtistMenuItem.setText(ResourceManager.getLabel("search.field.popup.menu.artist", context.getConfig()
				.getLocale()));
		searchArtistMenuItem.addActionListener(new SearchTypeActionListener(ListManager.FLG_SEARCH_ARTIST));

		searchAlbumMenuItem = new JRadioButtonMenuItem();
		searchAlbumMenuItem.setText(ResourceManager.getLabel("search.field.popup.menu.album", context.getConfig()
				.getLocale()));
		searchAlbumMenuItem.addActionListener(new SearchTypeActionListener(ListManager.FLG_SEARCH_ALBUM));

		searchTitleMenuItem = new JRadioButtonMenuItem();
		searchTitleMenuItem.setText(ResourceManager.getLabel("search.field.popup.menu.title", context.getConfig()
				.getLocale()));
		searchTitleMenuItem.addActionListener(new SearchTypeActionListener(ListManager.FLG_SEARCH_TITLE));

		group.add(searchMenuItem);
		group.add(searchAllMenuItem);
		group.add(searchGenreMenuItem);
		group.add(searchArtistMenuItem);
		group.add(searchAlbumMenuItem);
		group.add(searchTitleMenuItem);

		switch (context.getConfig().getSearchFlag()) {
		case ListManager.FLG_SEARCH_GENRE:
			searchGenreMenuItem.setSelected(true);
			break;
		case ListManager.FLG_SEARCH_ARTIST:
			searchArtistMenuItem.setSelected(true);
			break;
		case ListManager.FLG_SEARCH_ALBUM:
			searchAlbumMenuItem.setSelected(true);
			break;
		case ListManager.FLG_SEARCH_TITLE:
			searchTitleMenuItem.setSelected(true);
			break;
		default:
			searchAllMenuItem.setSelected(true);
			break;
		}

		// -- Add the menu items
		add(searchMenuItem);
		add(searchAllMenuItem);
		add(searchGenreMenuItem);
		add(searchArtistMenuItem);
		add(searchAlbumMenuItem);
		add(searchTitleMenuItem);

	}

	class SearchTypeActionListener implements ActionListener {

		private int searchFlag;

		public SearchTypeActionListener(int searchFlag) {
			this.searchFlag = searchFlag;
		}

		public void actionPerformed(ActionEvent evt) {
			context.getConfig().setSearchFlag(searchFlag);
			searchField.refresh();
		}
	}
}
