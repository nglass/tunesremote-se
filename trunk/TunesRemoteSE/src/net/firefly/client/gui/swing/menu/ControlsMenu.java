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

import java.awt.Event;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import net.firefly.client.controller.ResourceManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.player.PlayerMode;
import net.firefly.client.player.PlayerStatus;
import net.firefly.client.player.events.PlayerModeChangedEvent;
import net.firefly.client.player.listeners.PlayerModeChangedEventListener;

public class ControlsMenu extends JMenu implements PlayerModeChangedEventListener {

	private static final long serialVersionUID = -9090183035989912470L;

	protected Context context;

	protected Frame rootContainer;

	protected ImageIcon enabledIcon;

	protected ImageIcon disabledIcon;

	protected final JCheckBoxMenuItem shuffleMenuItem = new JCheckBoxMenuItem();

	public ControlsMenu(Context context, Frame rootContainer) {
		this.context = context;
		this.rootContainer = rootContainer;
		initialize();
	}

	private void initialize() {
		// -- icons
		this.disabledIcon = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/uncheck.gif"));
		this.enabledIcon = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/check.gif"));

		// -- Title
		setText(ResourceManager.getLabel("menu.controls.title", context.getConfig().getLocale()));

		// -- shortcut
		setMnemonic(Character.toUpperCase(getText().charAt(0)));
		
		// -- Menu items

		JMenuItem playPauseMenuItem = new JMenuItem();
		playPauseMenuItem.setText(ResourceManager.getLabel("menu.controls.playpause", context.getConfig().getLocale()));
		playPauseMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				PlayerStatus playerStatus = context.getPlayer().getPlayerStatus();
				if (playerStatus.equals(PlayerStatus.STATUS_STOPPED)) {
					context.getPlayer().play();
				} else {
					context.getPlayer().pause();
				}
			}
		});
		playPauseMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, Event.CTRL_MASK, true));

		JMenuItem nextMenuItem = new JMenuItem();
		nextMenuItem.setText(ResourceManager.getLabel("menu.controls.next", context.getConfig().getLocale()));
		nextMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				context.getPlayer().next();
			}
		});
		nextMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, Event.CTRL_MASK, true));

		JMenuItem previousMenuItem = new JMenuItem();
		previousMenuItem.setText(ResourceManager.getLabel("menu.controls.previous", context.getConfig().getLocale()));
		previousMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				context.getPlayer().previous();
			}
		});
		previousMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, Event.CTRL_MASK, true));

		shuffleMenuItem.setText(ResourceManager.getLabel("player.control.shuffle", context.getConfig().getLocale()));
		shuffleMenuItem.setIconTextGap(0);
		shuffleMenuItem.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (shuffleMenuItem.isSelected()) {
					context.getPlayer().setPlayerMode(PlayerMode.MODE_SHUFFLE);
				} else {
					context.getPlayer().setPlayerMode(PlayerMode.MODE_NORMAL);
				}
			}
		});

		// -- Add the menu items
		add(playPauseMenuItem);
		addSeparator();
		add(nextMenuItem);
		add(previousMenuItem);
		addSeparator();
		add(shuffleMenuItem);

		context.getPlayer().addPlayerModeChangedEventListener(this);
	}

	public void onPlayerModeChange(PlayerModeChangedEvent evt) {
		if (context.getPlayer().getPlayerMode().equals(PlayerMode.MODE_SHUFFLE)) {
			shuffleMenuItem.setSelected(true);
		} else {
			shuffleMenuItem.setSelected(false);
		}
	}
}
