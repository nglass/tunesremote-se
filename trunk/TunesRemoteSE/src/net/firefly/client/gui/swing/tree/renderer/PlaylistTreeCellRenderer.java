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
package net.firefly.client.gui.swing.tree.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.firefly.client.controller.ResourceManager;
import net.firefly.client.gui.swing.other.CellAnimator;
import net.firefly.client.gui.swing.tree.PlaylistTree;
import net.firefly.client.model.playlist.IPlaylist;
import net.firefly.client.model.playlist.ITunesPlaylist;
import net.firefly.client.model.playlist.M3UPlaylist;
import net.firefly.client.model.playlist.PlaylistStatus;
import net.firefly.client.model.playlist.SmartPlaylist;
import net.firefly.client.model.playlist.StaticPlaylist;

public class PlaylistTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 3098276147721584409L;

	private Font TITLE_FONT = null;

	private Color TITLE_COLOR = new Color(82, 85, 82);

	private Font PLAYLIST_FONT = null;

	private Font SELECTED_PLAYLIST_FONT = null;

	private Font NOT_LOADED_PLAYLIST_FONT = null;

	private static Border TITLE_BORDER = new EmptyBorder(5, 5, 5, 0);
	private static Border LEAF_BORDER = new EmptyBorder(1, 1, 1, 1);
	private static Border DROP_TARGET_BORDER = new MatteBorder(1, 1, 1, 1, UIManager.getColor("Tree.selectionBackground"));

	private int row;

	private Object value;

	public static ImageIcon SMART_PLAYLIST_ICON = new ImageIcon(ResourceManager.class
			.getResource("/net/firefly/client/resources/images/smart-playlist.png"));
	public static ImageIcon LOADING_PLAYLIST_ICON = new ImageIcon(ResourceManager.class
			.getResource("/net/firefly/client/resources/images/loading-animated.gif"));
	public static ImageIcon STATIC_PLAYLIST_ICON = new ImageIcon(ResourceManager.class
			.getResource("/net/firefly/client/resources/images/static-playlist.png"));
	public static ImageIcon STATIC_EDITABLE_PLAYLIST_ICON = new ImageIcon(ResourceManager.class
			.getResource("/net/firefly/client/resources/images/static-editable.png"));
	public static ImageIcon LIBRARY_ICON = new ImageIcon(ResourceManager.class
			.getResource("/net/firefly/client/resources/images/library.png"));

	private boolean expanded;

	public PlaylistTreeCellRenderer() {
		super();
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		String stringValue = tree.convertValueToText(value, sel, expanded, leaf, row, hasFocus);

		this.expanded = expanded;
		this.hasFocus = hasFocus;
		this.selected = sel;
		this.row = row;
		this.value = value;

		TITLE_FONT = tree.getFont().deriveFont(Font.BOLD, 11F);
		PLAYLIST_FONT = tree.getFont();
		SELECTED_PLAYLIST_FONT = tree.getFont().deriveFont(Font.BOLD);
		NOT_LOADED_PLAYLIST_FONT = tree.getFont().deriveFont(Font.ITALIC);

		setText(stringValue);

		if (sel && !expanded) { // selected and not a title
			setForeground(getTextSelectionColor());
		} else {
			setForeground(getTextNonSelectionColor());
		}
		// There needs to be a way to specify disabled icons.
		if (!tree.isEnabled()) {
			setEnabled(false);
			if (leaf) {
				setDisabledIcon(getLeafIcon());
			} else if (expanded) {
				setDisabledIcon(getOpenIcon());
			} else {
				setDisabledIcon(getClosedIcon());
			}
		} else {
			setEnabled(true);
			if (leaf) {
				setIcon(getLeafIcon());
			} else if (expanded) {
				setIcon(getOpenIcon());
			} else {
				setIcon(getClosedIcon());
			}
		}
		setComponentOrientation(tree.getComponentOrientation());

		// -- remove icon for titles
		setOpenIcon(null);
		setClosedIcon(null);

		if (!expanded) { // not a title
			if (sel) {
				setForeground(UIManager.getColor("Tree.selectionForeground"));
				setFont(SELECTED_PLAYLIST_FONT);
			} else {
				setForeground(UIManager.getColor("Tree.foreground"));
				Object o = ((DefaultMutableTreeNode) value).getUserObject();
				if (o instanceof IPlaylist) {
					PlaylistStatus ps = ((IPlaylist) o).getStatus();
					if (ps == PlaylistStatus.LOADED) {
						setFont(PLAYLIST_FONT);
					} else {
						setFont(NOT_LOADED_PLAYLIST_FONT);
					}
				} else {
					setFont(PLAYLIST_FONT);
				}
			}
			setForeground(getForeground());
		} else {
			setFont(TITLE_FONT);
			setForeground(TITLE_COLOR);
			setBorder(TITLE_BORDER);
		}

		if (!expanded) { // not a title
			if (row == ((PlaylistTree) tree).getHighlightRow()) {
				setBorder(DROP_TARGET_BORDER);
			} else {
				setBorder(LEAF_BORDER);
			}
		} else {
			setBorder(TITLE_BORDER);
		}
		CellAnimator.animate(tree, this, tree.getPathForRow(row));
		return this;
	}

	public Icon getLeafIcon() {
		Object o = ((DefaultMutableTreeNode) value).getUserObject();
		if (row == 1) {
			return LIBRARY_ICON;

		} else if (row > 2) {
			if (o instanceof StaticPlaylist) {
				if (((StaticPlaylist) o).getStatus() == PlaylistStatus.LOADING) {
					return LOADING_PLAYLIST_ICON;
				} else {
					return STATIC_EDITABLE_PLAYLIST_ICON;
				}
			} else if (o instanceof M3UPlaylist || o instanceof ITunesPlaylist) {
				if (((IPlaylist) o).getStatus() == PlaylistStatus.LOADING) {
					return LOADING_PLAYLIST_ICON;
				} else {
					return STATIC_PLAYLIST_ICON;
				}
			} else if (o instanceof SmartPlaylist) {
				if (((SmartPlaylist) o).getStatus() == PlaylistStatus.LOADING) {
					return LOADING_PLAYLIST_ICON;
				} else {
					return SMART_PLAYLIST_ICON;
				}
			}
		}
		return super.getLeafIcon();
	}

	public Color getBackground() {
		return null;
	}

	public Color getBackgroundNonSelectionColor() {
		return null;
	}

	public Color getBackgroundSelectionColor() {
		return null;
	}

	public Color getBorderSelectionColor() {
		return null;
	}

	// -- override to workaround text clipping with bold font
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		return new Dimension((int) (d.width * 1.4), (!expanded) ? 18 : 28);
	}

	public void updateUI() {
		super.updateUI();
		DROP_TARGET_BORDER = new MatteBorder(1, 1, 1, 1, UIManager.getColor("Tree.selectionBackground"));
	}
}
