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
import net.firefly.client.gui.swing.dialog.PreferencesDialog;
import net.firefly.client.gui.swing.menu.action.PreferencesMenuActionListener;

public class EditMenu extends JMenu {

	private static final long serialVersionUID = 7485115033987600591L;

	protected Context context;

	protected Frame rootContainer;

	protected PreferencesDialog preferencesDialog;

	public EditMenu(Context context, Frame rootContainer) {
		this.context = context;
		this.rootContainer = rootContainer;
		initialize();
	}

	private void initialize() {

		// -- create dialogs
		this.preferencesDialog = new PreferencesDialog(context, rootContainer);

		// -- Title
		setText(ResourceManager.getLabel("menu.edit.title", context.getConfig().getLocale()));
		
		// -- shortcut
		setMnemonic(Character.toUpperCase(getText().charAt(0)));

		// -- Menu items

		JMenuItem prefLibraryMenuItem = new JMenuItem();
		prefLibraryMenuItem.setText(ResourceManager.getLabel("menu.edit.preferences", context.getConfig().getLocale()));
		prefLibraryMenuItem.addActionListener(new PreferencesMenuActionListener(preferencesDialog, rootContainer));

		// -- Add the menu items
		add(prefLibraryMenuItem);
	}

}
