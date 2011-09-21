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
 * along with FireflyClient; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright 2011 Nick Glass
 */
package net.firefly.client.gui.swing.menu;

import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import net.firefly.client.controller.ResourceManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.player.MediaPlayer;
import net.firefly.client.player.events.PlayerStatusChangedEvent;
import net.firefly.client.player.listeners.PlayerStatusChangedEventListener;

public class ViewMenu extends JMenu implements PlayerStatusChangedEventListener  {

	private static final long serialVersionUID = -3104591096799947047L;

	protected Context context;

	protected Frame rootContainer;

	protected ImageIcon enabledIcon;

	protected ImageIcon disabledIcon;

	protected final JCheckBoxMenuItem visualizerMenuItem = new JCheckBoxMenuItem();
	
	protected final JCheckBoxMenuItem fullscreenMenuItem = new JCheckBoxMenuItem();

	public ViewMenu(Context context, Frame rootContainer) {
		this.context = context;
		this.rootContainer = rootContainer;
		initialize();
	}

	private void initialize() {	
		// -- Title
		setText(ResourceManager.getLabel("menu.view.title", context.getConfig().getLocale()));

		// -- shortcut
		setMnemonic(Character.toUpperCase(getText().charAt(0)));
		
		// -- Menu items

		visualizerMenuItem.setText(ResourceManager.getLabel("menu.view.visualizer", context.getConfig().getLocale()));
		visualizerMenuItem.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				context.getPlayer().setVisualizer(visualizerMenuItem.isSelected());
			}
		});
		
		fullscreenMenuItem.setText(ResourceManager.getLabel("menu.view.fullscreen", context.getConfig().getLocale()));
		fullscreenMenuItem.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				context.getPlayer().setFullscreen(fullscreenMenuItem.isSelected());
			}
		});

		// -- Add the menu items
		add(visualizerMenuItem);
		add(fullscreenMenuItem);

		context.getPlayer().addPlayerStatusChangedEventListener(this);
	}


	@Override
	public void onPlayerStatusChange(PlayerStatusChangedEvent evt) {
		MediaPlayer player = context.getPlayer();
		visualizerMenuItem.setSelected(player.isVisualizerOn());
		fullscreenMenuItem.setSelected(player.isFullscreen());
	}
}
