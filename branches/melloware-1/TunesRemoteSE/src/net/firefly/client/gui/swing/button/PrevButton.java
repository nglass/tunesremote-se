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

public class PrevButton extends JLabel {
	private static final long serialVersionUID = -3044296393954661866L;

	protected Context context;

	protected ImageIcon prevIcon;

	protected ImageIcon pressedPrevIcon;

	public PrevButton(Context context) {
		this.context = context;
		initialize();
	}

	protected void initialize() {
		this.prevIcon = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/prev.png"));
		this.pressedPrevIcon = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/prev-on.png"));

		setToolTipText(ResourceManager.getLabel("player.control.previous", context.getConfig().getLocale()));

		setOpaque(false);
		setVerticalAlignment(SwingConstants.CENTER);
		setIcon(this.prevIcon);

		setBackground(null);
		setIconTextGap(0);
		setBorder(new EmptyBorder(0, 0, 0, 0));

		addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				context.getPlayer().previous();
			}

			public void mousePressed(java.awt.event.MouseEvent e) {
				setIcon(pressedPrevIcon);
			}

			public void mouseReleased(MouseEvent arg0) {
				setIcon(prevIcon);
			}
		});
	}

}
