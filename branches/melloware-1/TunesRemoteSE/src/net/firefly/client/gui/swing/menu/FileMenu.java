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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import net.firefly.client.controller.ResourceManager;
import net.firefly.client.controller.request.PlaylistRequestManager;
//import net.firefly.client.gui.applet.ReloadLibraryMenuActionListener;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.swing.frame.ClientFrame;
import net.firefly.client.gui.swing.menu.action.AddStaticPlaylistMenuActionListener;
import net.firefly.client.gui.swing.menu.action.NewLibraryMenuActionListener;

public class FileMenu extends JMenu {

	private static final long serialVersionUID = 908548818961636064L;

	public static boolean MAC_OS_X = (System.getProperty("os.name").toLowerCase().startsWith("mac os x"));
	
	protected Context context;

	protected Frame rootContainer;

	protected JMenuItem newLibraryMenuItem;
	
	protected JMenuItem reloadLibraryMenuItem;

	protected JMenuItem addStaticPlaylistMenuItem;

	protected JMenuItem closeLibraryMenuItem;

	protected JMenu deleteLibrarySubmenu;

	protected JMenuItem exitMenuItem;

	public FileMenu(Context context, Frame rootContainer) {
		this.context = context;
		this.rootContainer = rootContainer;
		initialize();
		
	}

	private void initialize() {
		int shortcutKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		
		// -- create dialogs
		//this.newHostDialog = new NewHostDialog(context, rootContainer);

		// -- Title
		setText(ResourceManager.getLabel("menu.file.title", context.getConfig().getLocale()));
		
		// -- shortcut
		setMnemonic(Character.toUpperCase(getText().charAt(0)));


		// -- Menu items

		// -- New remote library menu item
		newLibraryMenuItem = new JMenuItem();
		newLibraryMenuItem.setText(ResourceManager.getLabel("menu.file.open.library", context.getConfig().getLocale()));
		newLibraryMenuItem.addActionListener(new NewLibraryMenuActionListener(rootContainer));
		newLibraryMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, shortcutKey, true));
		
		reloadLibraryMenuItem = new JMenuItem();
		reloadLibraryMenuItem.setText(ResourceManager.getLabel("menu.file.reload.library", context.getConfig().getLocale()));
		//reloadLibraryMenuItem.addActionListener(new ReloadLibraryMenuActionListener(context, rootContainer));
		reloadLibraryMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, shortcutKey, true));

		addStaticPlaylistMenuItem = new JMenuItem();
		
		addStaticPlaylistMenuItem.addActionListener(new AddStaticPlaylistMenuActionListener(context, rootContainer));
		addStaticPlaylistMenuItem.setText(ResourceManager
				.getLabel("menu.file.playlist.add", context.getConfig().getLocale()));

		// -- Close current library menu item
		final ClientFrame cf = (ClientFrame)rootContainer;
		closeLibraryMenuItem = new JMenuItem();
		closeLibraryMenuItem.setText(ResourceManager.getLabel("menu.file.close.library", context.getConfig().getLocale()));
		closeLibraryMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, shortcutKey, true));
		closeLibraryMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cf.processWindowEvent(new WindowEvent(rootContainer, WindowEvent.WINDOW_CLOSING));
			}
		});

		// -- Exit menu item menu item
		exitMenuItem = new JMenuItem();
		exitMenuItem.setText(ResourceManager.getLabel("menu.file.exit", context.getConfig().getLocale()));
		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.ALT_MASK, true));
		exitMenuItem.setActionCommand("Exit");
		exitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cf.setVisible(false);
				cf.updateConfigOnClose();
				System.exit(0);
			}
		});

		// -- Add the menu items
		add(newLibraryMenuItem);

		addSeparator();
		
		add(addStaticPlaylistMenuItem);

		addSeparator();

		add(closeLibraryMenuItem);

		if (!MAC_OS_X) {
			addSeparator();
			add(exitMenuItem);
		}

		// -- manage menu item activation
		addMenuListener(new MenuListener() {
			public void menuCanceled(MenuEvent e) {
			}

			public void menuDeselected(MenuEvent e) {
			}

			public void menuSelected(MenuEvent e) {
				if (context.getLibraryInfo() != null && context.getLibraryInfo().getLibraryId() != null) {
					closeLibraryMenuItem.setEnabled(true);
					if (PlaylistRequestManager.supportPlaylistAdvancedManagement(context.getServerVersion())) {
						addStaticPlaylistMenuItem.setEnabled(true);
					}
				} else {
					closeLibraryMenuItem.setEnabled(false);
					addStaticPlaylistMenuItem.setEnabled(false);
				}
			}
		});
	}
}
