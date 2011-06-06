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
package net.firefly.client.gui.swing.panel;

import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.swing.button.NextButton;
import net.firefly.client.gui.swing.button.PlayPauseButton;
import net.firefly.client.gui.swing.button.PrevButton;

public class PlayerPanel extends JPanel {
	
	private static final long serialVersionUID = -5592733944052891917L;

	private Context context;

	private PrevButton prevButton;
	private PlayPauseButton playPauseButton;
	private NextButton nextButton;
	
	public PlayerPanel(Context context) {
		this.context = context;
		initialize();
	}

	protected void initialize() {
		setLayout(new FlowLayout());
		this.prevButton = new PrevButton(context);
		this.playPauseButton = new PlayPauseButton(context);
		this.nextButton = new NextButton(context);
		add(prevButton);
		add(playPauseButton);
		add(nextButton);
		
		setBorder(new EmptyBorder(0,0,0,0));
	}

}
