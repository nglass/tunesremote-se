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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;

public class VolumeSliderUI extends BasicSliderUI {

	protected static Color borderColor;
	protected static Color trackColor;

	protected static ImageIcon thumbIcon;
	
	protected JSlider slider;

	public static ComponentUI createUI(JComponent c) {
		return new VolumeSliderUI((JSlider)c);
	}

	public VolumeSliderUI(JSlider slider) {
	   super(slider);
	   this.slider = slider;
	}

	public void installUI(JComponent c) {
		thumbIcon = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/volume.png"));
		borderColor = new Color(132, 130, 132);
		trackColor = new Color(247, 247, 247);
		super.installUI(c);
		scrollListener.setScrollByBlock(false);
	}

	public void paintThumb(Graphics g) {
	   if (slider.isEnabled()) {
   		Rectangle knobBounds = thumbRect;
   		g.translate(knobBounds.x, knobBounds.y);
   		thumbIcon.paintIcon(slider, g, 0, 1);
   		g.translate(-knobBounds.x, -knobBounds.y);
	   }
	}

	public void paintTrack(Graphics g) {

		Graphics2D g2D = (Graphics2D) g;

		g2D.translate(trackRect.x/2, trackRect.y);

		// Dimension
		int trackWidth = contentRect.width - 6;
		int trackHeight = thumbRect.height / 2;
		int trackTop = (thumbRect.height - trackHeight) / 2 + 1;
		int trackLeft = 0;

		// Draw the track
		GradientPaint gradient = new GradientPaint(trackLeft, trackTop + trackHeight, borderColor, trackLeft, trackTop
				+ (trackHeight / 2), trackColor, true);
		g2D.setPaint(gradient);
		g2D.fillRect(trackLeft, trackTop, trackWidth, trackHeight);

		// Draw the edges
		g2D.setColor(borderColor);
		g2D.drawLine(trackLeft, trackTop, trackLeft, trackTop + trackHeight - 1);
		g2D.drawLine(trackLeft + trackWidth, trackTop, trackLeft + trackWidth, trackTop + trackHeight - 1);
		
		g2D.translate(-trackRect.x/2, -trackRect.y);
	}

	public void paintFocus(Graphics g) {
	}

	protected Dimension getThumbSize() {
		Dimension size = new Dimension();
		size.width = thumbIcon.getIconWidth();
		size.height = thumbIcon.getIconHeight();
		return size;
	}

	/**
	 * Returns the shorter dimension of the track.
	 */
	protected int getTrackWidth() {
		// This strange calculation is here to keep the
		// track in proportion to the thumb.
		final double kIdealTrackWidth = 7.0;
		final double kIdealThumbHeight = 16.0;
		final double kWidthScalar = kIdealTrackWidth / kIdealThumbHeight;

		return (int) (kWidthScalar * thumbRect.height);

	}

	/**
	 * Returns the longer dimension of the slide bar. (The slide bar is only the
	 * part that runs directly under the thumb)
	 */
	protected int getTrackLength() {
		return trackRect.width;
	}

	/**
	 * Returns the amount that the thumb goes past the slide bar.
	 */
	protected int getThumbOverhang() {
		return (int) (getThumbSize().getHeight() - getTrackWidth()) / 2;
	}

}
