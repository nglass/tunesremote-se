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

import java.awt.Insets;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import net.firefly.client.controller.ResourceManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.player.PlayerMode;
import net.firefly.client.player.events.PlayerModeChangedEvent;
import net.firefly.client.player.listeners.PlayerModeChangedEventListener;

public class ShuffleButton extends JLabel implements PlayerModeChangedEventListener {
	private static final long serialVersionUID = -496537732231831110L;

	protected Context context;

	protected ImageIcon enabledIcon;

	protected ImageIcon disabledIcon;
	
	protected ImageIcon currentIcon;
	
	protected ImageIcon pressedIcon;

	public ShuffleButton(Context context) {
		this.context = context;
		initialize();
	}

	protected void initialize() {
		this.disabledIcon = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/shuffle-off.png"));
		this.enabledIcon = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/shuffle-on.png"));

		this.pressedIcon = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/shuffle-pressed.png"));
		this.currentIcon = this.disabledIcon;
		
		setToolTipText(ResourceManager.getLabel("player.control.shuffle", context.getConfig().getLocale()));
		
		setOpaque(false);
		setVerticalAlignment(SwingConstants.CENTER);
		setIcon(this.currentIcon);

		setBackground(null);
		setIconTextGap(0);
		setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));

		addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent e) {
				setIcon(pressedIcon);
			}

			public void mouseReleased(MouseEvent arg0) {
				setIcon(currentIcon);
			}
			
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (context.getPlayer().getPlayerMode().equals(PlayerMode.MODE_SHUFFLE)) {
					context.getPlayer().setPlayerMode(PlayerMode.MODE_NORMAL);
				} else {
					// -- normal mode
					context.getPlayer().setPlayerMode(PlayerMode.MODE_SHUFFLE);
				}
			}
		});
		
		context.getPlayer().addPlayerModeChangedEventListener(this);
	}

	public void onPlayerModeChange(PlayerModeChangedEvent evt) {
		if (context.getPlayer().getPlayerMode().equals(PlayerMode.MODE_SHUFFLE)) {
			this.currentIcon = enabledIcon;
		} else {
			this.currentIcon = disabledIcon;
		}
		setIcon(this.currentIcon);
	}
}
