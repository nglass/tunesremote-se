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
package net.firefly.client.gui.swing.tree.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

import net.firefly.client.controller.ResourceManager;
import net.firefly.client.controller.request.PlaylistRequestManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.swing.dialog.ErrorDialog;
import net.firefly.client.gui.swing.tree.model.PlaylistTreeModel;
import net.firefly.client.gui.swing.tree.renderer.PlaylistTreeCellRenderer;
import net.firefly.client.model.playlist.IPlaylist;
import net.firefly.client.model.playlist.SmartPlaylist;
import net.firefly.client.model.playlist.StaticPlaylist;
import net.firefly.client.tools.FireflyClientException;

public class PlaylistTreeCellEditor extends DefaultTreeCellEditor {

	protected Object editedValue;

	protected DefaultTextField defaultTextField;

	protected Context context;

	protected Frame rootContainer;

	public PlaylistTreeCellEditor(Frame rootContainer, JTree tree, DefaultTreeCellRenderer renderer, Context context) {
		super(tree, renderer);
		this.context = context;
		this.rootContainer = rootContainer;
	}

	public Component getTreeCellEditorComponent(JTree jtree, Object obj, boolean flag, boolean flag1, boolean flag2, int i) {
		Component c = super.getTreeCellEditorComponent(jtree, obj, flag, flag1, flag2, i);
		// -- ugly way to have the text field with the good look and feel after
		// change
		SwingUtilities.updateComponentTreeUI(c);
		return c;
	}

	/**
	 * Tries to update playlist name if successfull: sort the PlaylistList,
	 * update the tree else: leave the PlaylistList and the tree unchanged
	 */
	public boolean stopCellEditing() {
		String oldPlaylistName = (String) ((IPlaylist)editedValue).getPlaylistName();
		String newPlaylistName = (String) realEditor.getCellEditorValue();
		if (!newPlaylistName.equals(oldPlaylistName)) {
			try {
				((PlaylistTreeModel) (tree.getModel())).renamePlaylist((IPlaylist) editedValue, newPlaylistName);
				return true;
			} catch (FireflyClientException e) {
				String message = e.getMessage();
				if (message != null && message.indexOf("Duplicate Playlist") != -1) {
					JOptionPane.showMessageDialog(rootContainer, ResourceManager.getLabel(
							"rename.playlist.duplicate.error.message", context.getConfig().getLocale()), ResourceManager
							.getLabel("rename.playlist.duplicate.error.title", context.getConfig().getLocale()),
							JOptionPane.WARNING_MESSAGE);
					startEditingTimer();
				} else {
					ErrorDialog.showDialog(rootContainer, ResourceManager.getLabel("rename.playlist.unexpected.error.message",
							context.getConfig().getLocale()), ResourceManager.getLabel("rename.playlist.unexpected.error.title",
							context.getConfig().getLocale()), e, context.getConfig().getLocale());
				}
				return false;
			}
		} else {
			return false;
		}
	}

	public Object getCellEditorValue() {
		return editedValue;
	}

	public boolean isCellEditable(EventObject event) {
		if (!PlaylistRequestManager.supportPlaylistAdvancedManagement(context.getServerVersion())) {
			return false;
		}
		boolean retValue = false;
		boolean editable = false;
		if (event != null) {
			if (event.getSource() instanceof JTree) {
				setTree((JTree) event.getSource());
				if (event instanceof MouseEvent) {
					TreePath path = tree.getPathForLocation(((MouseEvent) event).getX(), ((MouseEvent) event).getY());
					boolean editableNode = false;
					if (path != null) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
						if (node != null) {
							if (node.getUserObject() instanceof StaticPlaylist
									|| node.getUserObject() instanceof SmartPlaylist) {
								editableNode = true;
							}
						}
					}
					editable = (editableNode && lastPath != null && path != null && lastPath.equals(path));
				}
			}
		}
		if (!realEditor.isCellEditable(event))
			return false;
		if (canEditImmediately(event))
			retValue = true;
		else if (editable && shouldStartEditingTimer(event)) {
			startEditingTimer();
		} else if (timer != null && timer.isRunning())
			timer.stop();
		if (retValue)
			prepareForEditing();
		return retValue;
	}

	/**
	 * Set the timer time to 100 ms.
	 */
	protected void startEditingTimer() {
		if (timer == null) {
			timer = new Timer(100, this);
			timer.setRepeats(false);
		}
		timer.start();
	}

	protected boolean canEditImmediately(EventObject event) {
		return (event == null);
	}

	protected TreeCellEditor createTreeCellEditor() {
		// Border aBorder = UIManager.getBorder("Tree.editorBorder");
		Border aBorder = new LineBorder(new Color(128, 128, 128), 1);
		defaultTextField = new DefaultTextField(aBorder);
		DefaultCellEditor editor = new DefaultCellEditor(defaultTextField) {
			private static final long serialVersionUID = -373506614051800430L;

			public boolean shouldSelectCell(EventObject event) {
				boolean retValue = super.shouldSelectCell(event);
				return retValue;
			}
		};
		editor.setClickCountToStart(1);
		return editor;
	}

	/**
	 * Set the correct icons regarding playlist type
	 */
	protected void determineOffset(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
		if (renderer != null) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			if (value != null) {
				Object o = node.getUserObject();
				if (o != null) {
					editedValue = o;
					if (o instanceof StaticPlaylist) {
						editingIcon = PlaylistTreeCellRenderer.STATIC_EDITABLE_PLAYLIST_ICON;
					} else if (o instanceof SmartPlaylist) {
						editingIcon = PlaylistTreeCellRenderer.SMART_PLAYLIST_ICON;
					}
				}
			}
			if (editingIcon != null)
				offset = renderer.getIconTextGap() + editingIcon.getIconWidth();
			else
				offset = renderer.getIconTextGap();
		} else {
			editingIcon = null;
			offset = 0;
		}
	}

	public class DefaultTextField extends JTextField {
		private static final long serialVersionUID = -8374101573571881655L;
		
		protected Border border;

		public DefaultTextField(Border border) {
			setBorder(border);
			// -- stop editing when focus is lost
			addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent focusevent) {
					stopCellEditing();
				}
			});
		}

		public void setBorder(Border border) {
			super.setBorder(border);
			this.border = border;
		}

		public Border getBorder() {
			return border;
		}

		public Dimension getPreferredSize() {
			JViewport viewport = (JViewport) (PlaylistTreeCellEditor.this.tree.getParent());
			Dimension d = super.getPreferredSize();
			// -- set a fixed width so that it's not larger than to the parent
			// viewport width
			d.width = Math.min(d.width, viewport.getWidth() - 55);
			return d;
		}

		public Font getFont() {
			Font font = super.getFont();
			if (font instanceof FontUIResource) {
				Container parent = getParent();
				if (parent != null && parent.getFont() != null)
					font = parent.getFont();
			}
			return font;
		}
	}
}
