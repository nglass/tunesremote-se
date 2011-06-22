package net.firefly.client.gui.swing.other;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.Timer;

public class MarqueeLabel extends JLabel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1840701860151188952L;
	
	private static final int TIMER_PERIOD = 100;
	private static final int TIMER_DELAY = 5000;
	private static final int GAP = 20;
	
	private int stringwidth = 0;
	private int ascent = 0;
	private int pause = 0;
	
	private Timer timer = null;
	
	private int xOffset = 0;

	private void resetTimer() {
		Font font = getFont();
		if (font != null) {
			FontMetrics fontMetrics = getFontMetrics(font);
			String text = getText();
			stringwidth = fontMetrics.stringWidth(text);
			ascent = fontMetrics.getMaxAscent();
		}
		
		if (timer == null) {
			timer = new Timer(TIMER_PERIOD, this);
			timer.setRepeats(true);
			timer.start();
		}
	}
	
	/**
	 * 
	 */
	public MarqueeLabel() {
		super();
		resetTimer();
	}

	/**
	 * @param image
	 * @param horizontalAlignment
	 */
	public MarqueeLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
		resetTimer();
	}

	/**
	 * @param image
	 */
	public MarqueeLabel(Icon image) {
		super(image);
		resetTimer();
	}

	/**
	 * @param text
	 * @param icon
	 * @param horizontalAlignment
	 */
	public MarqueeLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
		resetTimer();
	}

	/**
	 * @param text
	 * @param horizontalAlignment
	 */
	public MarqueeLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
		resetTimer();
	}

	/**
	 * @param text
	 */
	public MarqueeLabel(String text) {
		super(text);
		this.setHorizontalAlignment(CENTER);
		resetTimer();
	}

	@Override
	public void setText(String text) {
		xOffset = 0;
		super.setText(text);
		resetTimer();
	}
	
	@Override
	public void setFont(Font font) {
		super.setFont(font);
		resetTimer();
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		String text = getText();

		Insets vInsets = this.getInsets();

		int w = this.getWidth() - (vInsets.left + vInsets.right);
		
		if (stringwidth <= w) {
			super.paint(g);
		} else {
			g.drawString(text, xOffset, ascent);
			
			// repeat string if necessary
			if (stringwidth + GAP + xOffset < w)
				g.drawString(text, xOffset + stringwidth + GAP, ascent);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		repaint();
		if (xOffset == 0 && pause < TIMER_DELAY/TIMER_PERIOD) {
			pause ++;
		} else {
			pause = 0;
			xOffset --;
			if (xOffset < -stringwidth - GAP) {
				xOffset = 0;
			}
		}
	}
}
