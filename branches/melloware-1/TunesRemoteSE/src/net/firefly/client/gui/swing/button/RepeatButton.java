/*
 * This file is part of TunesRemote SE.
 *
 * TunesRemote SE is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * any later version.
 *
 * TunesRemote SE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TunesRemote SE; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright 2011 Nick Glass
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
import net.firefly.client.player.RepeatMode;
import net.firefly.client.player.events.RepeatModeChangedEvent;
import net.firefly.client.player.listeners.RepeatModeChangedEventListener;

public class RepeatButton extends JLabel implements RepeatModeChangedEventListener {

	private static final long serialVersionUID = -2199913577254765237L;

	protected Context context;

	protected ImageIcon repeatOffIcon;
	protected ImageIcon repeatSingleIcon;
	protected ImageIcon repeatAllIcon;

	protected ImageIcon raPressedIcon;
	protected ImageIcon rsPressedIcon;
	
	protected ImageIcon currentIcon;
	protected ImageIcon pressedIcon;

	public RepeatButton(Context context) {
		this.context = context;
		initialize();
	}

	protected void initialize() {
		this.repeatOffIcon = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/repeat.png"));
		this.repeatSingleIcon = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/repeat-single.png"));
		this.repeatAllIcon = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/repeat-on.png"));
		
		this.raPressedIcon = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/repeat-pressed.png"));
		this.rsPressedIcon = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/repeat-single-pressed.png"));
		
		this.currentIcon = this.repeatOffIcon;
		this.pressedIcon = this.raPressedIcon;
		
		setToolTipText(ResourceManager.getLabel("player.control.repeat", context.getConfig().getLocale()));
		
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
			
			// cycle off->all->single
			public void mouseClicked(java.awt.event.MouseEvent e) {
				RepeatMode mode = context.getPlayer().getRepeatMode();
				
				if (mode.equals(RepeatMode.REPEAT_OFF)) {
					context.getPlayer().setRepeatMode(RepeatMode.REPEAT_ALL);
				} else if (mode.equals(RepeatMode.REPEAT_ALL)) {
					context.getPlayer().setRepeatMode(RepeatMode.REPEAT_SINGLE);
				} else if (mode.equals(RepeatMode.REPEAT_SINGLE)) {
					context.getPlayer().setRepeatMode(RepeatMode.REPEAT_OFF);
				}
			}
		});
		
		context.getPlayer().addRepeatModeChangedEventListener(this);
	}

	public void onRepeatModeChange(RepeatModeChangedEvent evt) {
		if (evt.getNewMode().equals(RepeatMode.REPEAT_OFF)) {
			this.currentIcon = repeatOffIcon;
			this.pressedIcon = this.raPressedIcon;
		} else if (evt.getNewMode().equals(RepeatMode.REPEAT_SINGLE)) {
			this.currentIcon = repeatSingleIcon;
			this.pressedIcon = this.rsPressedIcon;
		} else {
			this.currentIcon = repeatAllIcon;
			this.pressedIcon = this.raPressedIcon;
		}
		
		setIcon(currentIcon);
	}
}
