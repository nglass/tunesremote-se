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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.swing.other.SearchField;

public class ControlsPanel extends JPanel {
	private static final long serialVersionUID = -1516613791429586876L;

	protected Context context;

	protected Frame rootContainer;
	
	PlayerPanel playerPanel;
	VolumePanel volumePanel;
	InfoPanel infoPanel;
	SearchField searchField;

	public ControlsPanel(Context context, Frame rootContainer) {
		this.context = context;
		this.rootContainer = rootContainer;
		initialize();
	}

	private void initialize() {

		// -- create components
		playerPanel = new PlayerPanel(context);
		playerPanel.setBorder(new EmptyBorder(0, 10, 0, 0));

		volumePanel = new VolumePanel(context);
		Dimension vpSize = new Dimension(115, 18);
		volumePanel.setMinimumSize(vpSize);
		volumePanel.setMaximumSize(vpSize);
		volumePanel.setPreferredSize(vpSize);
		volumePanel.setBorder(new EmptyBorder(0, 10, 0, 0));

		infoPanel = new InfoPanel(context);
		searchField = new SearchField(context);

		// -- set layout manager
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		// -- add components
		JPanel left = new JPanel();
		left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));

		left.add(playerPanel);
		left.add(volumePanel);
		left.setBorder(new EmptyBorder(1, 1, 1, 1));
		add(left);
		add(infoPanel);
		add(searchField);
		searchField.setBorder(new EmptyBorder(1, 1, 1, 1));

		// -- organize

		// -- left panel
		layout.putConstraint(SpringLayout.SOUTH, left, -5, SpringLayout.SOUTH, this);
		layout.putConstraint(SpringLayout.NORTH, left, 5, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, left, 0, SpringLayout.WEST, this);

		// -- search field
		layout.putConstraint(SpringLayout.SOUTH, searchField, -5, SpringLayout.SOUTH, this);
		layout.putConstraint(SpringLayout.NORTH, searchField, 5, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.EAST, searchField, 0, SpringLayout.EAST, this);

		// -- info panel
		int sideMargin = 30;
		layout.putConstraint(SpringLayout.SOUTH, infoPanel, -5, SpringLayout.SOUTH, this);
		layout.putConstraint(SpringLayout.NORTH, infoPanel, 5, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.EAST, infoPanel, -sideMargin, SpringLayout.WEST, searchField);
		layout.putConstraint(SpringLayout.WEST, infoPanel, sideMargin, SpringLayout.EAST, left);

		setBorder(new MatteBorder(0, 0, 1, 0, new Color(128, 128, 128)));
	}
}
