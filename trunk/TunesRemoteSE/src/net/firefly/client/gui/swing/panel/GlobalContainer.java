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
package net.firefly.client.gui.swing.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.swing.table.AlbumTable;
import net.firefly.client.gui.swing.table.ArtistTable;
import net.firefly.client.gui.swing.table.GenreTable;
import net.firefly.client.gui.swing.table.SongTable;
import net.firefly.client.gui.swing.tree.RadioOutline;

public class GlobalContainer extends JPanel {
	private static final long serialVersionUID = -82889511099152496L;

	protected Context context;

	protected Frame rootContainer;

	protected JSplitPane globalSplitPane;
	
	protected JSplitPane musicSplitPane;
	
	protected JScrollPane radioScrollPane;
	
	protected JPanel genreArtistAlbumPane;

	protected JScrollPane genreScrollPane;

	protected GenreTable genreTable;
	
	protected CoverPanel coverPanel;

	public GlobalContainer(Context context, Frame rootContainer) {
		this.context = context;
		context.setGlobalContainer(this);
		this.rootContainer = rootContainer;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());

		globalSplitPane = new JSplitPane();
		globalSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		globalSplitPane.setDividerLocation(context.getConfig().getLeftPanelWidth());
		globalSplitPane.setDividerSize(2);
		globalSplitPane.setBorder(new EmptyBorder(0, 0, 0, 0));

		musicSplitPane = new JSplitPane();
		musicSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		musicSplitPane.setDividerLocation(250);
		// musicSplitPane.setOneTouchExpandable(true);
		musicSplitPane.setDividerSize(1);
		musicSplitPane.setBorder(new MatteBorder(0, 1, 0, 0, new Color(128, 128, 128)));

		genreArtistAlbumPane = new JPanel(new GridLayout(1, 3));

		genreScrollPane = new JScrollPane();
		genreTable = new GenreTable(context);
		genreScrollPane.setViewportView(genreTable);
		genreScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		genreScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

		JScrollPane artistScrollPane = new JScrollPane();
		artistScrollPane.setViewportView(new ArtistTable(context));
		artistScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		artistScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

		JScrollPane albumScrollPane = new JScrollPane();
		albumScrollPane.setViewportView(new AlbumTable(context));
		albumScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		albumScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

		JScrollPane songScrollPane = new JScrollPane();
		SongTable songTable = new SongTable(rootContainer, context);
		songScrollPane.setViewportView(songTable);
		songScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		songScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

		if (context.getConfig().isShowGenre()) {
			genreArtistAlbumPane.add(genreScrollPane);
		}
		genreArtistAlbumPane.add(artistScrollPane);
		genreArtistAlbumPane.add(albumScrollPane);

		musicSplitPane.setTopComponent(genreArtistAlbumPane);
		musicSplitPane.setBottomComponent(songScrollPane);

		LeftPanel controlPanel = new LeftPanel(context, rootContainer);
		this.coverPanel = controlPanel.getCoverPanel();
		globalSplitPane.setLeftComponent(controlPanel);
		globalSplitPane.setRightComponent(musicSplitPane);

		// -- limit size
		controlPanel.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {

				if (globalSplitPane.getDividerLocation() < 160) {
					globalSplitPane.setDividerLocation(160);
				}
				if (globalSplitPane.getDividerLocation() > 350) {
					globalSplitPane.setDividerLocation(350);
				}

			}
		});

		// -- general split pane : top=controls, bottom=music
		JSplitPane generalSplitPane = new JSplitPane();
		generalSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		generalSplitPane.setResizeWeight(0);
		generalSplitPane.setDividerLocation(65);
		generalSplitPane.setDividerSize(0);
		generalSplitPane.setBorder(new EmptyBorder(0, 0, 0, 0));

		generalSplitPane.setTopComponent(new ControlsPanel(context, rootContainer));
		generalSplitPane.setBottomComponent(globalSplitPane);

		this.add(generalSplitPane);

	   radioScrollPane = new JScrollPane();
	   radioScrollPane.setViewportView(new RadioOutline(context));
	   radioScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	   radioScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		
	}


	public CoverPanel getCoverPanel() {
		return coverPanel;
	}
	
	public void showGenre(){
		if (this.genreArtistAlbumPane.getComponentCount() == 2) {
			this.genreArtistAlbumPane.add(genreScrollPane, 0);
			refreshRootContainer();
		}
	}

	public void hideGenre(){
		if (this.genreArtistAlbumPane.getComponentCount() == 3) {
			genreTable.getSelectionModel().setSelectionInterval(0, 0);
			this.genreArtistAlbumPane.remove(genreScrollPane);
			refreshRootContainer();
		}
	}

	private void refreshRootContainer() {
		SwingUtilities.updateComponentTreeUI(rootContainer);
		if (rootContainer instanceof JFrame) {
			((JFrame) rootContainer).getRootPane().setBorder(rootPaneBorder);
		}
	}

	public void showMusic() {
	   int div = globalSplitPane.getDividerLocation();
	   globalSplitPane.setRightComponent(musicSplitPane);
	   globalSplitPane.setDividerLocation(div);
	}
	
	public void showRadio() {
	   int div = globalSplitPane.getDividerLocation();
	   globalSplitPane.setRightComponent(radioScrollPane);
	   globalSplitPane.setDividerLocation(div);
	}
	
	private Border rootPaneBorder = new MatteBorder(0, 1, 1, 1, new Color(128, 128, 128));

}
