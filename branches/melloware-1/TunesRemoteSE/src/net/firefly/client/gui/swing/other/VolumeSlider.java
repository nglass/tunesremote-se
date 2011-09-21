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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JSlider;

import net.firefly.client.gui.context.Context;

public class VolumeSlider extends JSlider implements MouseWheelListener {

	private static final long serialVersionUID = -2941791192064395902L;

	protected Context context;

	protected VolumeSliderUI ui;

	public VolumeSlider(Context context) {
		this.context = context;
		initialize();
	}

	protected void initialize() {

		setMinimum(0);
		setMaximum(100);
		// apply inital gain value
		setValue((int)(context.getPlayer().getInitialGain() / 0.01f), false);
		setOrientation(HORIZONTAL);
		setPaintTicks(false);
		setPaintLabels(false);
		setFocusable(false);
		setPaintTrack(true);

		ui = new VolumeSliderUI();
		setUI(ui);

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				setValue(ui.valueForXPosition(e.getX()));
				e.consume();
			}
		});

		addMouseWheelListener(this);

	}

	public void setValue(int n, boolean send) {
		if (n != getValue()) {
			super.setValue(n);
			if (send) {
				float newGain = n * 0.01f;
				context.getPlayer().setGain(newGain);
			}
		}
	}
	
	public void setValue(int n) {
		setValue(n, true);
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		int scrollAmout = 5;
		if (notches < 0) {
			// -- move up
			setValue(Math.min(getValue() - (notches * scrollAmout), getMaximum()));
		} else {
			// -- move down
			setValue(Math.max(getValue() - (notches * scrollAmout), getMinimum()));
		}
	}

	public void updateUI() {
		if (getParent() != null) {
			setBackground(getParent().getBackground());
		}
		if (getUI() != null) {
			getUI().installUI(this);
		}
	}
}
