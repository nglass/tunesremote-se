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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.firefly.client.controller.ResourceManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.swing.other.MarqueeLabel;
import net.firefly.client.gui.swing.other.PlayingSlider;
import net.firefly.client.model.data.Song;
import net.firefly.client.model.data.SongContainer;
import net.firefly.client.player.PlayerStatus;
import net.firefly.client.player.events.PlayerStatusChangedEvent;
import net.firefly.client.player.events.TimePlayedChangedEvent;
import net.firefly.client.player.listeners.PlayerStatusChangedEventListener;
import net.firefly.client.player.listeners.TimePlayedChangedEventListener;
import net.firefly.client.tools.TimeFormatTools;

public class InfoPanel extends JPanel implements PlayerStatusChangedEventListener, TimePlayedChangedEventListener,
		ChangeListener {

	private static final long serialVersionUID = 118135527180691004L;

	protected Context context;

	protected JLabel playerStatusLabel;
	
	protected JLabel playerSongTitleLabel;

	protected JLabel playerSongInfoLabel;

	protected JLabel playerPlayedTimeLabel;

	protected JLabel playerTotalTimeLabel;

	protected PlayingSlider playingSlider;

	protected boolean sliderInhibited = false;

	protected static ImageIcon statusConnectingIcon;
	protected static ImageIcon statusReadingInfosIcon;
	protected static ImageIcon statusPlayingIcon;
	protected static ImageIcon statusStoppedIcon;

	public static final Color borderColor = new Color(128, 128, 128);
	public static final Color top = new Color(0xec,0xef,0xe0);
	public static final Color bottom = new Color(0xe9, 0xec, 0xd4);

	public InfoPanel(Context context) {
		this.context = context;
		initialize();
	}

	protected void initialize() {

		statusConnectingIcon = new ImageIcon(getClass().getResource(
				"/net/firefly/client/resources/images/status-connecting.png"));
		statusReadingInfosIcon = new ImageIcon(getClass().getResource(
				"/net/firefly/client/resources/images/status-reading-infos.png"));
		statusPlayingIcon = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/status-playing.png"));
		statusStoppedIcon = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/status-stopped.png"));

		setLayout(new GridBagLayout());
		Dimension d = new Dimension(40, 20);
		playerStatusLabel = new JLabel(getStatusIcon(PlayerStatus.STATUS_STOPPED));
		playerSongTitleLabel = new MarqueeLabel(getSongTitle(null));
		playerSongInfoLabel = new MarqueeLabel(" ");
		playerSongInfoLabel.setFont(getFont().deriveFont(Font.ITALIC));
		playerPlayedTimeLabel = new JLabel("", JLabel.RIGHT);
		playerPlayedTimeLabel.setPreferredSize(d);
		playerPlayedTimeLabel.setMinimumSize(d);
		playerTotalTimeLabel = new JLabel("", JLabel.LEFT);
		playerTotalTimeLabel.setPreferredSize(d);
		playerTotalTimeLabel.setMinimumSize(d);

		playingSlider = new PlayingSlider(context);

		GridBagConstraints playerStatusLabelGBC = new GridBagConstraints(0, 1, 1, 1, 0, 0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 15, -5, 5), 0, 0);
		
		GridBagConstraints playerPlayedTimeLabelGBC = new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 2), 0, 0);
		GridBagConstraints playerTotalTimeLabelGBC = new GridBagConstraints(3, 2, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 2, 0, 0), 0, 0);
		GridBagConstraints playingSliderGBC = new GridBagConstraints(2, 2, 1, 1, 200, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 2), 0, 0);
	
		GridBagConstraints playerSongTitleLabelGBC = new GridBagConstraints(1, 0, 3, 1, 200, 0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 2), 0, 0);

		GridBagConstraints playerSongInfoLabelGBC = new GridBagConstraints(1, 1, 3, 1, 200, 0, 
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 2), 0, 0);

		add(playerStatusLabel, playerStatusLabelGBC);
		add(playerPlayedTimeLabel, playerPlayedTimeLabelGBC);
		add(playerTotalTimeLabel, playerTotalTimeLabelGBC);
		add(playingSlider, playingSliderGBC);
		add(playerSongTitleLabel, playerSongTitleLabelGBC);
		add(playerSongInfoLabel, playerSongInfoLabelGBC);

		playerSongInfoLabel.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				super.componentResized(evt);
			}
		});

		context.getPlayer().addPlayerStatusChangedEventListener(this);
		context.getPlayer().addTimePlayedChangedEventListener(this);

		playingSlider.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				;
			}

			public void mousePressed(MouseEvent e) {
				;
			}

			public void mouseReleased(MouseEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					int newValue = source.getValue();
					if (newValue == 0) {
						// -- workaround to allow seeking to 0
						newValue = 1;
					}
					SongContainer sc = context.getPlayer().getPlayingSong();
					Song s = sc.getSong();
					if (s != null) {	
						if (context.getPlayer().isSupportSeeking()) {
							long newTime = s.getTime() * newValue / source.getMaximum();
							sliderInhibited = true;
							context.getPlayer().seek(newTime);
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									sliderInhibited = false;
								}
							});
						}
					}
				}
			}
		});

		setBackground(null);
	}

	public void onPlayerStatusChange(PlayerStatusChangedEvent evt) {
		PlayerStatus newPlayerStatus = evt.getNewStatus();
		playerStatusLabel.setIcon(getStatusIcon(newPlayerStatus));
		// playerStatusLabel.setText(getStatusString(newPlayerStatus));
		playerSongTitleLabel.setText(getSongTitle(context.getPlayer().getPlayingSong().getSong()));
		if (getStatusString(PlayerStatus.STATUS_STOPPED).equals(playerSongTitleLabel.getText())) {
			playerSongTitleLabel.setFont(getFont());
		} else {
			playerSongTitleLabel.setFont(getFont().deriveFont(Font.BOLD));
		}
		playerSongInfoLabel.setText(getSongInfo(context.getPlayer().getPlayingSong().getSong()));
		if (!newPlayerStatus.equals(PlayerStatus.STATUS_PLAYING)) {
			playerPlayedTimeLabel.setText("");
			playerTotalTimeLabel.setText("");
		}
		
		playingSlider.setSupportSeeking(context.getPlayer().isSupportSeeking());
		
		repaint();
	}

	protected String getStatusString(PlayerStatus status) {
		if (status.equals(PlayerStatus.STATUS_CONNECTING)) {
			return ResourceManager.getLabel("player.status.connecting", context.getConfig().getLocale());
		} else if (status.equals(PlayerStatus.STATUS_PLAYING)) {
			return ResourceManager.getLabel("player.status.playing", context.getConfig().getLocale());
		} else if (status.equals(PlayerStatus.STATUS_READING_INFO)) {
			return ResourceManager.getLabel("player.status.reading.info", context.getConfig().getLocale());
		} else if (status.equals(PlayerStatus.STATUS_SEEKING)) {
			return ResourceManager.getLabel("player.status.seeking", context.getConfig().getLocale());
		}
		return ResourceManager.getLabel("player.status.stopped", context.getConfig().getLocale());
	}

	protected ImageIcon getStatusIcon(PlayerStatus status) {
		if (status.equals(PlayerStatus.STATUS_CONNECTING)) {
			return statusConnectingIcon;
		} else if (status.equals(PlayerStatus.STATUS_PLAYING)) {
			return statusPlayingIcon;
		} else if (status.equals(PlayerStatus.STATUS_READING_INFO)) {
			return statusReadingInfosIcon;
		} else if (status.equals(PlayerStatus.STATUS_SEEKING)) {
			return null;
		}
		return statusStoppedIcon;
	}

	public String getSongInfo(Song s) {
		if (s != null) {
			String artist = "";
			String album = "";
			String year = "";
			String albumYear = "";
			if (s.getArtist() != null && s.getArtist().trim().length() > 0) {
				artist = s.getArtist();
			} else {
				artist = ResourceManager.getLabel("table.unknown.artist", context.getConfig().getLocale());
			}
			if (s.getAlbum() != null && s.getAlbum().trim().length() > 0) {
				album = s.getAlbum();
			} else {
				album = ResourceManager.getLabel("table.unknown.album", context.getConfig().getLocale());
			}
			if (s.getYear() != null && s.getYear().trim().length() > 0 && !"0".equals(s.getYear().trim())) {
				year = " (" + s.getYear() + ") ";
			}
			albumYear = album + year;
			return artist + " - " + albumYear;
		} else {
			return " ";
		}
	}

	public String getSongTitle(Song s) {
		if (s != null) {
			String title = " ";
			if (s.getTitle() != null && s.getTitle().trim().length() > 0) {
				title = s.getTitle();
			}
			return title;
		} else {
			return getStatusString(PlayerStatus.STATUS_STOPPED);
		}
	}

	public void stateChanged(ChangeEvent e) {
	}

	public void onTimePlayedChange(TimePlayedChangedEvent evt) {
		long totalTimeInMillis = evt.getTotalTime();
		long timePlayedInMillis = evt.getTimePlayed();
		Date totalTimeDate = new Date(totalTimeInMillis);
		Date timePlayedDate = new Date(timePlayedInMillis);
		playerPlayedTimeLabel.setText(TimeFormatTools.format(timePlayedDate));
		
		if (context.getPlayer().isSupportSeeking()) {
			playerTotalTimeLabel.setText(TimeFormatTools.format(totalTimeDate));
		} else {
			playerTotalTimeLabel.setText("");
		}
		
		if (!this.playingSlider.getValueIsAdjusting()) {
			if (!sliderInhibited) {
				if (totalTimeInMillis > 0) {
					int value = (int) (timePlayedInMillis * this.playingSlider.getMaximum() / totalTimeInMillis);
					this.playingSlider.setValue(value);
				} else {
					this.playingSlider.setValue(0);
				}
			}
		}
	}
	
	// paint the background
	public void paintComponent(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(getForeground());
		
		JComponent c = this;
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Insets vInsets = c.getInsets();

		int w = c.getWidth() - (vInsets.left + vInsets.right);
		int h = c.getHeight() - (vInsets.top + vInsets.bottom);

		int x = vInsets.left;
		int y = vInsets.top;
		int arc = 10;
		
		// Rear Border
		g2d.setPaint(Color.WHITE);
		g2d.drawRoundRect(x + 1, y + 1, w - 2, h - 2, arc, arc);
		
		// Fill
		g2d.setPaint(top);
		g2d.fillRoundRect(x, y, w - 2, (h - 2)/2, arc, arc);
		g2d.fillRect(x, y + ((h-2)/2)-arc, w-2, arc);
		
		g2d.setPaint(bottom);
		g2d.fillRoundRect(x, y + ((h-2)/2), w - 2, (h - 2)/2, arc, arc);
		g2d.fillRect(x, y + ((h-2)/2), w-2, arc);
		
		// Front Border
		g2d.setPaint(borderColor);
		g2d.drawRoundRect(x, y, w - 2, h - 2, arc, arc);
		
	}
}
