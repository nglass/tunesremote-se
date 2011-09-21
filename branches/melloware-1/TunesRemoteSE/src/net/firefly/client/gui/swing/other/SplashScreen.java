package net.firefly.client.gui.swing.other;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.net.URL;

import net.firefly.client.Version;

public final class SplashScreen extends Frame {

	private static final long serialVersionUID = -1409687917937894364L;
	private static final ImageObserver NO_OBSERVER = null;
	private static final int IMAGE_ID = 0;

	private MediaTracker mediaTracker;

	private Image image;

	private static SplashWindow splashWindow;
	
	public SplashScreen() {
	}

	public void splash() {
		initImageAndTracker();
		setSize(image.getWidth(NO_OBSERVER), image.getHeight(NO_OBSERVER));

		mediaTracker.addImage(image, IMAGE_ID);
		try {
			mediaTracker.waitForID(IMAGE_ID);
		} catch (InterruptedException ex) {
			System.out.println("Cannot track image load.");
		}

		splashWindow = new SplashWindow(image);
		this.setIconImage(image);
	}
	
	public static void close(){
		if (splashWindow != null){
			splashWindow.setVisible(false);
			splashWindow.dispose();
		}
	}

	private void initImageAndTracker() {
		mediaTracker = new MediaTracker(this);
		URL imageURL = SplashScreen.class.getResource("/net/firefly/client/resources/images/app.png");
		image = Toolkit.getDefaultToolkit().getImage(imageURL);
	}

	private class SplashWindow extends Window {
		
		private static final long serialVersionUID = 442979013708468534L;

		public SplashWindow(Image aImage) {
			super(SharedOwnerFrame.getInstance());
			image = aImage;
			setSize((int)(image.getWidth(NO_OBSERVER)*1.5), image.getHeight(NO_OBSERVER));
			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			Rectangle window = getBounds();
			setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					setVisible(false);
					dispose();
				}
			});
			setVisible(true);
		}

		public void paint(Graphics graphics) {
			Graphics2D g = (Graphics2D)graphics;
			
			// -- draw splash image
			if (image != null) {
				Rectangle window = getBounds();
				g.drawImage(image, (window.width - image.getWidth(NO_OBSERVER))/2 , 
						           (window.height - image.getHeight(NO_OBSERVER))/2, this);
			}
			
			// -- draw border
			g.setColor(new Color(128, 128, 128));
			Rectangle window = getBounds();
			g.drawRect(0, 0,  window.width-1, window.height-1);
			
			// -- draw information text
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g.setColor(new Color(38, 52, 62));
			g.setFont(this.getFont().deriveFont(Font.ITALIC));
			TextLayout tl = new TextLayout(Version.getLongApplicationName(), g.getFont(), g.getFontRenderContext());
			double x = 5;
			double y = 15;
			tl.draw(g, (float) x, (float) y);
			
			g.setFont(this.getFont().deriveFont(11F));
			tl = new TextLayout("http://code.google.com/p/tunesremote-se", g.getFont(), g.getFontRenderContext());
			Rectangle2D linkBounds = tl.getBounds();
			x = (window.width - linkBounds.getWidth()) / 2 - linkBounds.getX();
			y = window.height - 5;
			tl.draw(g, (float) x, (float) y);
		}

		private Image image;
	}

}
