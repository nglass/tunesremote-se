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
import net.firefly.client.player.PlayerStatus;
import net.firefly.client.player.events.PlayerStatusChangedEvent;
import net.firefly.client.player.listeners.PlayerStatusChangedEventListener;

public class PlayPauseButton extends JLabel implements PlayerStatusChangedEventListener {
	private static final long serialVersionUID = 2929834979798244419L;

	protected Context context;

	protected ImageIcon playIcon;

	protected ImageIcon pressedPlayIcon;

	protected ImageIcon pauseIcon;

	protected ImageIcon pressedPauseIcon;

	protected ImageIcon stopIcon;

	protected ImageIcon pressedStopIcon;
	
	public PlayPauseButton(Context context) {
		this.context = context;
		initialize();
	}

	protected void initialize() {

		this.playIcon = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/play.png"));
		this.pressedPlayIcon = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/play-on.png"));
		this.pauseIcon = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/pause.png"));
		this.pressedPauseIcon = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/pause-on.png"));
		this.stopIcon = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/stop.png"));
		this.pressedStopIcon = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/stop-on.png"));

		
		setToolTipText(ResourceManager.getLabel("player.control.play.pause", context.getConfig().getLocale()));

		setOpaque(false);
		setVerticalAlignment(SwingConstants.CENTER);
		setIcon(this.playIcon);

		setBackground(null);
		setIconTextGap(0);
		setBorder(new EmptyBorder(0, 0, 0, 0));

		addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				PlayerStatus playerStatus = context.getPlayer().getPlayerStatus();
				if (playerStatus.equals(PlayerStatus.STATUS_STOPPED)) {
					context.getPlayer().resume();
				} else {
					context.getPlayer().pause();
				}
			}

			public void mousePressed(java.awt.event.MouseEvent e) {
				if (getIcon().toString().equals(playIcon.toString())) {
					setIcon(pressedPlayIcon);
				} else if (getIcon().toString().equals(pauseIcon.toString())) {
					setIcon(pressedPauseIcon);
				} else {
					setIcon(pressedStopIcon);
				}
			}

			public void mouseReleased(MouseEvent arg0) {
				if (getIcon().toString().equals(pressedPlayIcon.toString())) {
					setIcon(playIcon);
				} else if (getIcon().toString().equals(pressedPauseIcon.toString())) {
					setIcon(pauseIcon);
				} else {
					setIcon(stopIcon);
				}
			}
		});

		this.context.getPlayer().addPlayerStatusChangedEventListener(this);
	}

	public void onPlayerStatusChange(PlayerStatusChangedEvent evt) {
		PlayerStatus newPlayerStatus = evt.getNewStatus();
		if (newPlayerStatus.equals(PlayerStatus.STATUS_STOPPED)) {
			setIcon(this.playIcon);
		} else if (context.getPlayer().isSupportSeeking()) {
			setIcon(this.pauseIcon);
		} else {
			setIcon(this.stopIcon);
		}
	}
}
