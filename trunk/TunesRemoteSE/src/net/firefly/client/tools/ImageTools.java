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
package net.firefly.client.tools;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class ImageTools {
	
	public static Color IMAGE_BORDER_COLOR = new Color(82,85,82);

	public static BufferedImage getSmoothScaledInstance(BufferedImage original, int width, int height) {
		BufferedImage ret = (BufferedImage) original;
		int originalWidth = original.getWidth();
		int originalHeight = original.getHeight();
		if (originalWidth == width){
			return original;
		}
		if (width < originalWidth){
			try {
				// -- can be memory consumming for too large images
				int i = 1;
				BufferedImage tmp = null;
				do {
					if (i != 1) {
						if (originalWidth > width) {
							originalWidth /= 2;
							if (originalWidth < width) {
								originalWidth = width;
							}
						}
						if (originalHeight > height) {
							originalHeight /= 2;
							if (originalHeight < height) {
								originalHeight = height;
							}
						}
					}
					tmp = new BufferedImage(originalWidth, originalHeight, (original.getType()!=0)?original.getType():BufferedImage.TYPE_INT_RGB);
					Graphics2D g2 = tmp.createGraphics();
					g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
					g2.drawImage(ret, 0, 0, originalWidth, originalHeight, null);
					g2.dispose();
					ret = tmp;
					i++;
				} while (originalWidth != width || originalHeight != height);
			} catch (Throwable t) {
				t.printStackTrace();
				return getScaledInstance(original, width, height);
			}
		} else {
			return getScaledInstance(original, width, height);
		}
		return ret;
	}
	
	public static BufferedImage getScaledInstance(BufferedImage original, int width, int height) {
		BufferedImage ret = (BufferedImage) original;
		try {
			ret = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics2D = ret.createGraphics();
			graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			graphics2D.drawImage(original, 0, 0, width, height, null);
			return ret;
		} catch (Throwable t2) {
			return null;
		}
	}

	public static BufferedImage createReflectBottom(BufferedImage source, int reflectSize) {
		BufferedImage outBuff = null;
		outBuff = new BufferedImage(source.getWidth(), source.getHeight() + reflectSize, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2D = outBuff.createGraphics();
		// -- draw original image
		g2D.drawImage(source, 0, 0, source.getWidth(), source.getHeight(), null);
		// -- draw border
		g2D.setPaint(IMAGE_BORDER_COLOR);
		g2D.drawRect(0, 0, source.getWidth()-1, source.getHeight()-1);
		//g2D.drawLine(0, 0, 0, source.getHeight()-1);
		//g2D.drawLine(0, source.getHeight()-1, source.getWidth(), source.getHeight()-1);
		//g2D.drawLine(source.getWidth()-1, 0, source.getWidth()-1, source.getHeight()-1);
		
		// -- draw bottom reflect (height: see reflectsize)
		g2D.translate(0, source.getHeight());
		g2D.scale(1.0D, -1D);
		g2D.drawImage(source, 0, -source.getHeight(), source.getWidth(), source.getHeight(), null);
		g2D.scale(1.0D, -1D);

		// -- paint gradient
		GradientPaint paintGradient = new GradientPaint(0, 0, new Color(1.0f, 1.0f, 1.0f, 0.7f), 0, reflectSize / 5 * 4,
				new Color(1.0f, 1.0f, 1.0f, 0.0f));
		g2D.setComposite(AlphaComposite.DstIn);
		g2D.setPaint(paintGradient);
		g2D.fillRect(0, 0, outBuff.getWidth(), reflectSize);

		g2D.dispose();
		return outBuff;
	}
}