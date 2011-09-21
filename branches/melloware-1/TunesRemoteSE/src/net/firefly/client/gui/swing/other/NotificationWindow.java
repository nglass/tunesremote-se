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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.basic.BasicButtonUI;

import net.firefly.client.controller.ResourceManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.swing.button.NextButton;
import net.firefly.client.gui.swing.button.PrevButton;
import net.firefly.client.model.data.Song;
import net.firefly.client.model.data.SongContainer;
import net.firefly.client.player.PlayerStatus;
import net.firefly.client.player.events.PlayerStatusChangedEvent;
import net.firefly.client.player.listeners.PlayerStatusChangedEventListener;
import net.firefly.client.tools.ImageTools;
import net.firefly.client.tools.TimeFormatTools;

public class NotificationWindow extends Window implements PlayerStatusChangedEventListener, PropertyChangeListener {

	private static final long serialVersionUID = -3563253666291039673L;

	protected int NOTIFICATION_TIMEOUT = 10;

	protected boolean canHideNotification = true;

	protected String NO_IMAGE;

	protected static final int COVER_WIDTH = 60;

	protected Context context;

	protected Frame rootContainer;

	protected JLabel cover;

	protected JLabel title;

	protected JLabel artist;

	protected JLabel albumYear;

	protected JButton closeButton;

	protected int timeBeforeHiding = 0;

	protected GridBagConstraints coverGBC;

	protected GridBagConstraints titleGBC;

	protected GridBagConstraints artistGBC;

	protected GridBagConstraints albumYearGBC;

	protected GridBagConstraints closeButtonGBC;

	protected int width;

	protected int height;

	protected NotificationManager notificationManager;

	public NotificationWindow(Context context, Frame rootContainer) {
		super(SharedOwnerFrame.getInstance()); // -- allow to avoid app focus on click
		notificationManager = new NotificationManager();
		this.rootContainer = rootContainer;
		this.context = context;
		NO_IMAGE = " ";
		initialize();
	}
	
	public void close() {
		notificationManager.notifyStop();
	}
	
	protected void initialize() {
		width = 400;
		height = 100;

		setAlwaysOnTop(true);

		setSize(width, height);
		setBackground(rootContainer.getBackground());
		cover = new JLabel(NO_IMAGE);
		title = new JLabel(" ");
		artist = new JLabel(" ");
		albumYear = new JLabel(" ");

		closeButton = new CloseButton(this);

		JPanel quickControlPanel = new JPanel();
		quickControlPanel.setLayout(new FlowLayout());
		PrevButton prevButton = new PrevButton(context);
		NextButton nextButton = new NextButton(context);
		quickControlPanel.add(prevButton);
		quickControlPanel.add(nextButton);

		JPanel rootPane = new JPanel();
		rootPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED));

		rootPane.setLayout(new GridBagLayout());

		coverGBC = new GridBagConstraints(0, 0, 1, 3, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 1, 1);

		titleGBC = new GridBagConstraints(1, 0, 1, 1, 0, 200, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0,
				5), 1, 1);
		artistGBC = new GridBagConstraints(1, 1, 1, 1, 0, 200, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0,
				5), 1, 1);

		albumYearGBC = new GridBagConstraints(1, 2, 1, 1, 0, 200, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5,
				0, 5), 1, 1);

		GridBagConstraints quickControlPanelGBC = new GridBagConstraints(2, 1, 1, 2, 0, 0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 1, 1);

		closeButtonGBC = new GridBagConstraints(2, 0, 1, 3, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 5,
				0, 5), 1, 1);

		rootPane.add(cover, coverGBC);
		rootPane.add(title, titleGBC);
		rootPane.add(artist, artistGBC);
		rootPane.add(albumYear, albumYearGBC);
		rootPane.add(quickControlPanel, quickControlPanelGBC);

		rootPane.add(closeButton, closeButtonGBC);

		add(rootPane);

		// -- events
		context.getPlayer().addPlayerStatusChangedEventListener(this);
		UIManager.addPropertyChangeListener(this);

		addMouseListener(new NotificationMouseListener());
		prevButton.addMouseListener(new NotificationMouseListener());
		nextButton.addMouseListener(new NotificationMouseListener());
		closeButton.addMouseListener(new NotificationMouseListener());

		notificationManager.start();
	}

	public void onPlayerStatusChange(PlayerStatusChangedEvent evt) {
		PlayerStatus newPlayerStatus = evt.getNewStatus();
		// disable notification for applet as focus ownership is not fully detectable
		boolean showNotification = !rootContainer.isFocused() && !context.isApplet();
		if (showNotification) {
			if (newPlayerStatus.equals(PlayerStatus.STATUS_PLAYING)) {
				setVisible(false);
				reset();
				if (context.getPlayer().getCover() != null) {
					try {
						BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(context.getPlayer().getCover()));
						int originalWidth = bufferedImage.getWidth();
						int originalHeight = bufferedImage.getHeight();
						int coverWidth = COVER_WIDTH;
						int coverHeight = coverWidth * originalHeight / originalWidth;
						cover.setText("");
						cover.setIcon(new ImageIcon(ImageTools.getSmoothScaledInstance(bufferedImage,coverWidth, coverHeight)));
					} catch (Throwable t) {
						cover.setText(NO_IMAGE);
						t.printStackTrace();
					}
				} else {
					cover.setText(NO_IMAGE);
					cover.setIcon(null);
				}
				SongContainer sc = context.getPlayer().getPlayingSong();
				Song s = sc.getSong();
				if (s == null) {
					s = new Song();
				}
				title.setText(s.getTitle() + " (" + TimeFormatTools.format(new Date(s.getTime())) + ")");
				
				String albumYearString = "<html>";
				if (s.getArtist() != null && s.getArtist().toString().length() > 0) {
					artist.setText("<html><b>" + s.getArtist());
				} else {
					artist.setText("<html><b>" + ResourceManager.getLabel("table.unknown.artist",context.getConfig().getLocale()));
				}
				if (s.getAlbum() != null && s.getAlbum().toString().length() > 0) {
					albumYearString += "    <i>" + s.getAlbum() + "</i>";
				} else {
					albumYearString += "    <i>" + (ResourceManager.getLabel("table.unknown.album",context.getConfig().getLocale())) + "</i>";;
				}
				if (s.getYear() != null && s.getYear().length() > 0 && !"0".equals(s.getYear().trim())) {
					albumYearString += "    <i>(" + s.getYear() + ")</i>";
				}
				albumYear.setText(albumYearString);
				pack();
				position();
				notificationManager.show();
			}
		}
	}

	protected void position() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle bounds = ge.getMaximumWindowBounds();
		setBounds(bounds.width - getWidth(), bounds.height - getHeight(), getWidth(), getHeight());
		validate();
	}

	protected void reset() {
		cover.setText(NO_IMAGE);
		cover.setIcon(null);
		title.setText(" ");
		artist.setText(" ");
		albumYear.setText(" ");
	}

	class NotificationMouseListener extends MouseAdapter {
		public void mouseEntered(MouseEvent e) {
			canHideNotification = false;
		}

		public void mouseExited(MouseEvent e) {
			canHideNotification = true;
		}
	}

	class NotificationManager extends Thread {
		private volatile boolean shouldRun = true;
		
		public void notifyStop() {
			this.shouldRun = false;
		}
		
		public void run() {
			while (shouldRun) {
				try {
					Thread.sleep(1000);
					while (timeBeforeHiding > 0 || !canHideNotification) {
						Thread.sleep(1000);
						if (timeBeforeHiding > 0) {
							timeBeforeHiding--;
						}
					}
				} catch (Throwable t) {
					t.printStackTrace();
				} finally {
					setVisible(false);
				}
			}
		}

		public synchronized void show() {
			canHideNotification = true;
			timeBeforeHiding = NOTIFICATION_TIMEOUT;
			setVisible(true);
		}
	}

	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals("lookAndFeel")) {
			SwingUtilities.updateComponentTreeUI(this);
			return;
		}
	}

	private class CloseButton extends JButton implements ActionListener {

		private static final long serialVersionUID = -3132508117621281039L;
		
		protected NotificationWindow notificationWindow;

		public CloseButton(NotificationWindow notificationWindow) {
			this.notificationWindow = notificationWindow;
			int size = 17;
			setPreferredSize(new Dimension(size, size));
			setUI(new BasicButtonUI());
			setContentAreaFilled(false);
			setFocusable(false);
			setBorder(BorderFactory.createEtchedBorder());
			setBorderPainted(false);
			setRolloverEnabled(true);
			addActionListener(this);
		}

		public void updateUI() {
		}

		// paint the cross
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			// shift the image for pressed buttons
			if (getModel().isPressed()) {
				g2.translate(1, 1);
			}
			g2.setStroke(new BasicStroke(2));
			g2.setColor(Color.GRAY);
			if (getModel().isRollover()) {
				g2.setColor(Color.BLACK);
			}
			int delta = 6;
			g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
			g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
			g2.dispose();
		}

		public void actionPerformed(ActionEvent arg0) {
			notificationWindow.setVisible(false);
		}
	}

}
