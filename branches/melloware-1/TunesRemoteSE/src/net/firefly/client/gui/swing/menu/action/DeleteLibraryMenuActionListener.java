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
package net.firefly.client.gui.swing.menu.action;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import net.firefly.client.controller.LibraryManager;
import net.firefly.client.controller.ResourceManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.swing.dialog.ErrorDialog;
import net.firefly.client.tools.FireflyClientException;

public class DeleteLibraryMenuActionListener implements ActionListener {

	protected Frame rootContainer;

	protected String libraryId;

	protected Context context;

	public DeleteLibraryMenuActionListener(Context context, String libraryId, Frame rootContainer) {
		this.libraryId = libraryId;
		this.context = context;
		this.rootContainer = rootContainer;
	}

	public void actionPerformed(ActionEvent e) {
		// -- action thread
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					int answer = JOptionPane.showConfirmDialog(rootContainer, ResourceManager.getLabel(
							"dialog.delete.library.confirm.message", context.getConfig().getLocale(),
							new String[] { libraryId }), ResourceManager.getLabel("dialog.delete.library.confirm.title",
							context.getConfig().getLocale()), JOptionPane.YES_NO_OPTION);
					if (answer == JOptionPane.YES_OPTION) {
						try {
							LibraryManager.deleteLibrary(context.getConfig().getConfigRootDirectory(), libraryId);
						} catch (FireflyClientException e) {
							ErrorDialog.showDialog(rootContainer, ResourceManager.getLabel(
									"dialog.delete.library.error.message", context.getConfig().getLocale(), new String[] { e
											.getMessage() }), ResourceManager.getLabel("dialog.delete.library.error.title",
									context.getConfig().getLocale()), e, context.getConfig().getLocale());
							return;
						}
						JOptionPane.showMessageDialog(rootContainer, ResourceManager.getLabel(
								"dialog.delete.library.success.message", context.getConfig().getLocale(),
								new String[] { libraryId }), ResourceManager.getLabel("dialog.delete.library.success.title",
								context.getConfig().getLocale()), JOptionPane.INFORMATION_MESSAGE);
						context.setSavedLibrariesList(LibraryManager.listSavedLibraries(context.getConfig().getConfigRootDirectory()));
					}
				} catch (Throwable t) {
					ErrorDialog.showDialog(rootContainer, ResourceManager.getLabel(
							"dialog.delete.library.unexpected.error.message", context.getConfig().getLocale(),
							new String[] { ((t.getMessage() != null) ? " (" + t.getMessage() + ")." : ".") }),
							ResourceManager.getLabel("dialog.delete.library.unexpected.error.title", context.getConfig()
									.getLocale()), t, context.getConfig().getLocale());
					return;
				}
			}
		});
		t.start();
	}
}
