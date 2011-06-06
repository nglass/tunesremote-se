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

import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import net.firefly.client.controller.LibraryManager;
import net.firefly.client.controller.ResourceManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.swing.dialog.ErrorDialog;
import net.firefly.client.model.library.Library;
import net.firefly.client.tools.FireflyClientException;

public class SaveLibraryMenuActionListener implements ActionListener {

	protected Frame rootContainer;

	protected Context context;

	public SaveLibraryMenuActionListener(Context context, Frame rootContainer) {
		this.rootContainer = rootContainer;
		this.context = context;
	}

	public void actionPerformed(ActionEvent e) {
		if (context.getLibraryInfo() != null && context.getLibraryInfo().getLibraryId() != null
				&& context.getMasterSongList() != null && context.getMasterSongList().size() > 1) {
			Library library = new Library();
			library.setLibraryInfo(context.getLibraryInfo());
			library.setSongList(context.getMasterSongList());
			boolean save = false;
			boolean overwrite = false;
			if (LibraryManager.alreadySavedLibrary(context.getConfig().getConfigRootDirectory(), library.getLibraryInfo().getLibraryId())) {
				int answer = JOptionPane.showConfirmDialog(rootContainer, ResourceManager.getLabel(
						"dialog.save.library.already.exists.message", context.getConfig().getLocale()), ResourceManager
						.getLabel("dialog.save.library.title", context.getConfig().getLocale()), JOptionPane.YES_NO_OPTION);
				if (answer == JOptionPane.YES_OPTION) {
					save = true;
					overwrite = true;
				}
			} else {
				save = true;
				overwrite = false;
				;
			}
			if (save) {
				rootContainer.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				try {
					LibraryManager.saveLibrary(context.getConfig().getConfigRootDirectory(), library, overwrite);
				} catch (FireflyClientException ex) {
					ErrorDialog.showDialog(rootContainer, ResourceManager.getLabel("dialog.save.library.error.message",
							context.getConfig().getLocale(), new String[] { ex.getMessage() }), ResourceManager.getLabel(
							"dialog.save.library.error.title", context.getConfig().getLocale()), ex, context.getConfig()
							.getLocale());
					return;
				} finally {
					rootContainer.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
				JOptionPane.showMessageDialog(rootContainer, ResourceManager.getLabel("dialog.save.library.message", context
						.getConfig().getLocale(), new String[] { context.getLibraryInfo().getLibraryId() }), ResourceManager
						.getLabel("dialog.save.library.title", context.getConfig().getLocale()),
						JOptionPane.INFORMATION_MESSAGE);
				context.setSavedLibrariesList(LibraryManager.listSavedLibraries(context.getConfig().getConfigRootDirectory()));
			}
		} else {
			JOptionPane.showMessageDialog(rootContainer, ResourceManager.getLabel("dialog.save.library.no.library.message",
					context.getConfig().getLocale()), ResourceManager.getLabel("dialog.save.library.no.library.title",
					context.getConfig().getLocale()), JOptionPane.WARNING_MESSAGE);

		}
	}

}
