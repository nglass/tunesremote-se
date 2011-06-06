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

import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;
import javax.swing.Spring;
import javax.swing.SpringLayout;

import net.firefly.client.controller.ConfigurationManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.tools.FireflyClientException;

public class LeftPanel extends JPanel {
	private static final long serialVersionUID = -818337315641836797L;

	protected Context context;

	protected PlaylistPanel playlistPanel;
	protected CoverPanel coverPanel;
	public CoverPanel getCoverPanel() {
		return coverPanel;
	}

	protected Frame rootContainer;
	
	protected SpringLayout layout;

	public LeftPanel(Context context, Frame rootContainer) {
		this.context = context;
		this.rootContainer = rootContainer;
		initialize();
	}

	private void initialize() {

		// -- create components
		playlistPanel = new PlaylistPanel(context, rootContainer);
		coverPanel = new CoverPanel(context);

		// -- set layout manager
		layout = new SpringLayout();
		setLayout(layout);

		// -- add components
		add(playlistPanel);
		add(coverPanel);

		computeLayout();

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				context.getConfig().setLeftPanelWidth(e.getComponent().getWidth());
				// -- save configuration
				Thread t = new Thread(new Runnable(){
					public void run() {
						try {
							ConfigurationManager.saveConfiguration(context.getConfig());
						} catch (FireflyClientException ex) {
							ex.printStackTrace();
						}
					}
				});
				t.start();
			}
		});
	}

	private void computeLayout() {

		// -- playlist panel
		layout.putConstraint(SpringLayout.SOUTH, playlistPanel, -10, SpringLayout.NORTH, coverPanel);
		layout.putConstraint(SpringLayout.NORTH, playlistPanel, 5, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.EAST, playlistPanel, 0, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.WEST, playlistPanel, 0, SpringLayout.WEST, this);

		// -- cover panel
		layout.putConstraint(SpringLayout.EAST, coverPanel, 0, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.WEST, coverPanel, 0, SpringLayout.WEST, this);
		SpringLayout.Constraints cpCst = layout.getConstraints(coverPanel);
		Spring s = Spring.sum(Spring.constant(CoverPanel.SONG_INFO_HEIGHT - CoverPanel.COVER_MARGIN), cpCst.getWidth());
		cpCst.setHeight(s);
		layout.putConstraint(SpringLayout.SOUTH, coverPanel, 0, SpringLayout.SOUTH, this);
	}
}
