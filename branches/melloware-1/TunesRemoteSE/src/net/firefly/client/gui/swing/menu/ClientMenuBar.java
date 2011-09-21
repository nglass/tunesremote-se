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

import javax.swing.JMenuBar;
import javax.swing.border.EmptyBorder;

import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.context.events.InterfaceLockEvent;
import net.firefly.client.gui.context.listeners.InterfaceLockEventListener;

public class ClientMenuBar extends JMenuBar implements InterfaceLockEventListener {

	private static final long serialVersionUID = -716580509644083079L;

	public static boolean MAC_OS_X = (System.getProperty("os.name").toLowerCase().startsWith("mac os x"));
	
	private Context context;

	private Frame rootContainer;

	private FileMenu fileMenu;
	
	private EditMenu editMenu;
	
	private ControlsMenu controlsMenu;
	
	private ViewMenu viewMenu;
	
	private HelpMenu helpMenu;
	

	public ClientMenuBar(Context context, Frame rootContainer) {
		this.context = context;
		this.rootContainer = rootContainer;
		initialize();
		context.addInterfaceLockEventListener(this);
	}

	private void initialize() {
		this.fileMenu = new FileMenu(context, rootContainer);
		this.editMenu = new EditMenu(context, rootContainer);
		this.viewMenu = new ViewMenu(context, rootContainer);
		this.controlsMenu = new ControlsMenu(context, rootContainer);
		add(fileMenu);
		add(editMenu);
		add(viewMenu);
		add(controlsMenu);
		
		if (!MAC_OS_X) {
			this.helpMenu = new HelpMenu(context, rootContainer);
			add(helpMenu);
		}
		setBorder(new EmptyBorder(0, 0, 0, 0));
	}

	public void onInterfaceLockChange(InterfaceLockEvent evt) {
		fileMenu.setEnabled(!evt.getLock());
		editMenu.setEnabled(!evt.getLock());
		viewMenu.setEnabled(!evt.getLock());
		controlsMenu.setEnabled(!evt.getLock());
		helpMenu.setEnabled(!evt.getLock());
	}
}
