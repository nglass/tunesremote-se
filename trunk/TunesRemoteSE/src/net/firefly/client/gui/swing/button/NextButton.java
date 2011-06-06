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
package net.firefly.client.gui.swing.button;

import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import net.firefly.client.controller.ResourceManager;
import net.firefly.client.gui.context.Context;

public class NextButton extends JLabel {
	private static final long serialVersionUID = 8342419596124737611L;

	protected Context context;

	protected ImageIcon nextIcon;

	protected ImageIcon pressedNextIcon;

	public NextButton(Context context) {
		this.context = context;
		initialize();
	}

	protected void initialize() {
		this.nextIcon = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/next.png"));
		this.pressedNextIcon = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/next-on.png"));

		setToolTipText(ResourceManager.getLabel("player.control.next", context.getConfig().getLocale()));

		setOpaque(false);
		setVerticalAlignment(SwingConstants.CENTER);
		setIcon(this.nextIcon);

		setBackground(null);
		setIconTextGap(0);
		setBorder(new EmptyBorder(0, 0, 0, 0));

		addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				context.getPlayer().next();
			}

			public void mousePressed(java.awt.event.MouseEvent e) {
				setIcon(pressedNextIcon);
			}

			public void mouseReleased(MouseEvent arg0) {
				setIcon(nextIcon);
			}
		});

	}
}
