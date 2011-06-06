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

import javax.swing.JSlider;

import net.firefly.client.gui.context.Context;
//import net.firefly.client.gui.context.events.ContextResetEvent;
//import net.firefly.client.gui.context.listeners.ContextResetEventListener;

// TODO: context reset handler
public class PlayingSlider extends JSlider /*implements ContextResetEventListener*/ {

	private static final long serialVersionUID = 2321434758978390770L;

	protected Context context;

	protected PlayingSliderUI ui;

	public PlayingSlider(Context context) {
		this.context = context;
		initialize();
	}

	protected void initialize() {
		setMinimum(0);
		setMaximum(10000);
		setValue(0);
		setOrientation(HORIZONTAL);
		setPaintTicks(false);
		setPaintLabels(false);
		setFocusable(false);
		setPaintTrack(true);

		ui = new PlayingSliderUI();
		setUI(ui);

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				setValue(ui.valueForXPosition(e.getX()));
				e.consume();
			}
		});
		
		setBackground(null);
		
//		context.addContextResetEventListener(this);
	}

	public void updateUI() {
		if (getUI() != null) {
			getUI().installUI(this);
		}
	}

//	public void onContextReset(ContextResetEvent evt) {
//		setValue(0);
//	}
}
