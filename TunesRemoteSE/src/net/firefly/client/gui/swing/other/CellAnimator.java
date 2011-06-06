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
package net.firefly.client.gui.swing.other;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

public class CellAnimator {
	public static final class TreeUpdater extends Updater {

		public Rectangle getCellBounds(Component component) {
			return ((JTree) component).getPathBounds((TreePath) getKey());
		}

		public TreeUpdater(Component component, TreePath treepath) {
			super(component, treepath);
		}
	}

	public static class TableUpdater extends Updater {

		public Rectangle getCellBounds(Component component) {
			return ((JTable) component).getCellRect(row, col, false);
		}

		private final int col;
		private final int row;

		public TableUpdater(Component component, int i, int j) {
			super(component, CellAnimator.getKey(i, j));
			col = i;
			row = j;
		}
	}

	public static class ListUpdater extends Updater {

		public Rectangle getCellBounds(Component component) {
			return ((JList) component).getCellBounds(idx, idx);
		}

		private final int idx;

		protected ListUpdater(Component component, int i) {
			super(component, CellAnimator.getKey(i));
			idx = i;
		}
	}

	public static abstract class Updater {

		protected Component getComponent() {
			return (Component) ref.get();
		}

		public Object getKey() {
			return key;
		}

		public boolean equals(Object obj) {
			return (obj instanceof Updater) && key.equals(((Updater) obj).key);
		}

		public String toString() {
			return String.valueOf(key);
		}

		protected abstract Rectangle getCellBounds(Component component);

		public boolean repaint() {
			Component component = getComponent();
			Rectangle rectangle;
			if (component != null && (rectangle = getCellBounds(component)) != null) {
				component.repaint(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
				return true;
			} else {
				return false;
			}
		}

		private WeakReference<Component> ref;
		private Object key;

		public Updater(Component component, Object obj) {
			ref = new WeakReference<Component>(component);
			key = obj;
		}
	}

	private static class IconImageObserver implements ImageObserver {

		public boolean imageUpdate(Image image, int i, int j, int k, int l, int i1) {
			if ((i & 0x30) != 0) {
				Iterator<Map<Object, Updater>> iterator = contexts.values().iterator();
				do {
					if (!iterator.hasNext())
						break;
					Map<Object, Updater> map = iterator.next();
					Iterator<Updater> iterator1 = map.values().iterator();
					do {
						if (!iterator1.hasNext())
							break;
						Updater updater = iterator1.next();
						if (!updater.repaint())
							iterator1.remove();
					} while (true);
					if (map.size() == 0)
						iterator.remove();
				} while (true);
			}
			cleanup();
			return (i & 0xa0) == 0 && icon != null;
		}

		public synchronized void addCell(Component component, Updater updater) {
			Map<Object, Updater> map = contexts.get(component);
			if (map == null) {
				map = new HashMap<Object, Updater>();
				contexts.put(component, map);
			}
			map.put(updater.getKey(), updater);
		}

		public synchronized void removeCell(Component component, Object obj) {
			Map<Object, Updater> map = contexts.get(component);
			if (map != null) {
				map.remove(obj);
				if (map.size() == 0)
					contexts.remove(component);
			} else {
				contexts.remove(component);
			}
			cleanup();
		}

		private void cleanup() {
			if (contexts.size() == 0 && icon != null) {
				synchronized (CellAnimator.icons) {
					CellAnimator.icons.remove(original);
				}
				icon.setImageObserver(null);
				icon = original = null;
			}
		}

		private Map<Component, Map<Object, Updater>> contexts;
		private ImageIcon icon;
		private ImageIcon original;

		public IconImageObserver(ImageIcon imageicon) {
			contexts = new WeakHashMap<Component, Map<Object, Updater>>();
			original = imageicon;
			icon = new ImageIcon(imageicon.getImage());
			icon.setImageObserver(this);
			BufferedImage bufferedimage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), 2);
			Graphics g = bufferedimage.getGraphics();
			icon.paintIcon(null, g, 0, 0);
			g.dispose();
		}
	}

	public CellAnimator() {
	}

	public static Object getKey(int i) {
		return new Integer(i);
	}

	public static Object getKey(int i, int j) {
		return "[" + i + "," + j + "]";
	}

	public static boolean isAnimated(Icon icon) {
		if (icon instanceof ImageIcon) {
			String s = icon.toString();
			Object obj = ((ImageIcon) icon).getImage().getProperty("comment", null);
			if (s != null && s.indexOf("animated.gif") != -1 || String.valueOf(obj).startsWith("GifBuilder"))
				return true;
		}
		return false;
	}

	protected static ImageIcon getAnimatedIcon(Component component) {
		Icon icon;
		if ((component instanceof JLabel) && (icon = ((JLabel) component).getIcon()) != null && (icon instanceof ImageIcon)
				&& isAnimated(icon))
			return (ImageIcon) icon;
		else
			return null;
	}

	public static void animate(JTree jtree, Component component, TreePath treepath) {
		ImageIcon imageicon = getAnimatedIcon(component);
		if (treepath != null && imageicon != null)
			animate(((Component) (jtree)), ((Updater) (new TreeUpdater(jtree, treepath))), imageicon);
		else
			stop(jtree, treepath);
	}

	public static void animate(JTable jtable, Component component, int i, int j) {
		ImageIcon imageicon = getAnimatedIcon(component);
		if (imageicon != null)
			animate(((Component) (jtable)), ((Updater) (new TableUpdater(jtable, i, j))), imageicon);
		else
			stop(jtable, i, j);
	}

	public static void animate(JList jlist, Component component, int i) {
		ImageIcon imageicon = getAnimatedIcon(component);
		if (imageicon != null)
			animate(((Component) (jlist)), ((Updater) (new ListUpdater(jlist, i))), imageicon);
		else
			stop(jlist, i);
	}

	public static void animate(Component component, Updater updater, ImageIcon imageicon) {
		IconImageObserver iconimageobserver;
		synchronized (icons) {
			iconimageobserver = icons.get(imageicon);
			if (iconimageobserver == null) {
				iconimageobserver = new IconImageObserver(imageicon);
				icons.put(imageicon, iconimageobserver);
			}
		}
		iconimageobserver.addCell(component, updater);
	}

	public static void stop(JTable jtable, int i, int j) {
		stop(((Component) (jtable)), getKey(i, j));
	}

	public static void stop(JList jlist, int i) {
		stop(((Component) (jlist)), getKey(i));
	}

	public static void stop(Component component, Object obj) {
		ArrayList<IconImageObserver> arraylist;
		synchronized (icons) {
			arraylist = new ArrayList<IconImageObserver>(icons.values());
		}
		IconImageObserver iconimageobserver;
		for (Iterator<IconImageObserver> iterator = arraylist.iterator(); iterator.hasNext(); iconimageobserver.removeCell(component, obj))
			iconimageobserver = iterator.next();

	}

	private static final Map<ImageIcon, IconImageObserver> icons = 
		new WeakHashMap<ImageIcon, IconImageObserver>();

}