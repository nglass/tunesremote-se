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
package net.firefly.client.gui.swing.tree.ui;

import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;

public class PlaylistTreeUI extends BasicTreeUI {

	public static ComponentUI createUI(JComponent x) {
		return new PlaylistTreeUI();
	}

	public PlaylistTreeUI() {
		super();
	}

	protected void paintVerticalPartOfLeg(Graphics g, Rectangle clipBounds, Insets insets, TreePath path) {
		return;
	}

	protected void paintHorizontalPartOfLeg(Graphics g, Rectangle clipBounds, Insets insets, Rectangle bounds,
			TreePath path, int row, boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf) {

		boolean selected = row >= tree.getMinSelectionRow() && row <= tree.getMaxSelectionRow();
		int depth = path.getPathCount() - 1;
		if (selected && depth > 1) {
			if (g instanceof Graphics2D) {
				GradientPaint gradient = new GradientPaint(bounds.x, bounds.height + bounds.y, UIManager
						.getColor("Tree.selectionBackground"), bounds.x, bounds.y - 10, tree.getBackground(), false);
				((Graphics2D) g).setPaint(gradient);
				((Graphics2D) g).fillRect(0, bounds.y, tree.getWidth(), bounds.height);
			} else {
				g.setColor(UIManager.getColor("Tree.selectionBackground"));
				g.fillRect(0, bounds.y, tree.getWidth(), bounds.height);
			}
		}
	}

	//protected MouseListener createMouseListener() {
	//	return new FullRowMouseListener();
	//}

	protected class FullRowMouseListener extends MouseAdapter {

		public void mousePressed(MouseEvent evt) {
			if (tree != null && tree.isEnabled()) {
				if (isEditing(tree) && tree.getInvokesStopCellEditing() && !stopEditing(tree)) {
					return;
				}

				if (tree.isRequestFocusEnabled()) {
					tree.requestFocus();
				}
				TreePath path = getClosestPathForLocation(tree, evt.getX(), evt.getY());

				if (path != null) {
					Rectangle bounds = getPathBounds(tree, path);
					if (evt.getY() > (bounds.y + bounds.height)) {
						return;
					}
					if (SwingUtilities.isLeftMouseButton(evt))
						checkForClickInExpandControl(path, evt.getX(), evt.getY());
					if (!startEditing(path, evt)) {
						selectPathForEvent(path, evt);
					}
				}
			}
		}
	}
}