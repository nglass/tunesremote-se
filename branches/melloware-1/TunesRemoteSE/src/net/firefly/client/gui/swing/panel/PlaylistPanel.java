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

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.swing.tree.PlaylistTree;

public class PlaylistPanel extends JPanel {

	private static final long serialVersionUID = 2672640585770991732L;

	protected PlaylistTree tree;

	protected Context context;
	
	protected Frame rootContainer;

	public PlaylistPanel(Context context, Frame rootContainer) {
		this.context = context;
		this.rootContainer = rootContainer;

		this.tree = new PlaylistTree(context, this, rootContainer);
		this.tree.setBackground(getBackground());

		JScrollPane scrollpane = new JScrollPane(tree);
		scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollpane.setBorder(new EmptyBorder(0, 0, 0, 0));

		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		add(scrollpane);

		layout.putConstraint(SpringLayout.SOUTH, scrollpane, 0, SpringLayout.SOUTH, this);
		layout.putConstraint(SpringLayout.NORTH, scrollpane, 0, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.EAST, scrollpane, 0, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.WEST, scrollpane, 0, SpringLayout.WEST, this);
	}
}
