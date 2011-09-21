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

import java.awt.Frame;
import java.awt.Toolkit;

public class SharedOwnerFrame extends Frame {

	private static final long serialVersionUID = 4668281429729411553L;
	
	protected static SharedOwnerFrame instance;

	protected SharedOwnerFrame() {
		super();
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/net/firefly/client/resources/images/app.png")));
	}

	public synchronized static SharedOwnerFrame getInstance() {
		if (instance == null) {
			instance = new SharedOwnerFrame();
		}
		return instance;
	}

	public void show() {
	}

	public synchronized void dispose() {
		try {
			getToolkit().getSystemEventQueue();
			super.dispose();
		} catch (Exception e) {
		}
	}
}