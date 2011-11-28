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

public class VolumeSlider extends JSlider implements MouseWheelListener {

	private static final long serialVersionUID = -2941791192064395902L;

	protected boolean valueChanging = false;

	protected VolumeSliderUI ui;

	public VolumeSlider(double initialVolume) {
		initialize(initialVolume);
	}

	protected void initialize(double initialValue) {

		setMinimum(0);
		setMaximum(1000000);
		setValue((int)(initialValue*10000.0));
		setOrientation(HORIZONTAL);
		setPaintTicks(false);
		setPaintLabels(false);
		setFocusable(false);
		setPaintTrack(true);

		ui = new VolumeSliderUI(this);
		setUI(ui);

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				setValue(ui.valueForXPosition(e.getX()));
				e.consume();
			}
		});

		addMouseWheelListener(this);

	}

	public boolean isVolumeChanging() {
	   return valueChanging;
	}
	
	public void setVolumeChanging(boolean valueChanging) {
	   this.valueChanging = valueChanging;
	}
	
	public void setVolume(double volume) {
	   this.valueChanging = true;
	   this.setValue ((int)(volume * 10000.0));
	}
	
	public double getVolume() {
	   return (double)this.getValue() / 10000.0;
	}
	
	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		int scrollAmout = (getMaximum()-getMinimum())/20;
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
