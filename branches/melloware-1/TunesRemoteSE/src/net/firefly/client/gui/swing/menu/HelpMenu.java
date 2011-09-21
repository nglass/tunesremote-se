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

import java.awt.Frame;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.firefly.client.controller.ResourceManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.swing.dialog.AboutDialog;
import net.firefly.client.gui.swing.menu.action.AboutMenuActionListener;

public class HelpMenu extends JMenu {

	private static final long serialVersionUID = 4590612205302419157L;

	protected Context context;

	protected Frame rootContainer;

	protected AboutDialog aboutDialog;

	public HelpMenu(Context context, Frame rootContainer) {
		this.context = context;
		this.rootContainer = rootContainer;
		initialize();
	}

	private void initialize() {
		
		// -- Title
		String menuTitle = ResourceManager.getLabel("menu.help.title", context.getConfig().getLocale());
		setText(menuTitle);
		
		// -- shortcut
		char mnemonic = Character.toUpperCase(menuTitle.charAt(0)); 
		setMnemonic(mnemonic);

		// -- create dialogs
		this.aboutDialog = new AboutDialog(context.getConfig(), rootContainer);

		// -- Menu items
		JMenuItem aboutLibraryMenuItem = new JMenuItem();
		aboutLibraryMenuItem.setText(ResourceManager.getLabel("menu.help.about", context.getConfig().getLocale()));
		aboutLibraryMenuItem.addActionListener(new AboutMenuActionListener(aboutDialog, rootContainer));

		// -- Add the menu items
		add(aboutLibraryMenuItem);
	}

}
