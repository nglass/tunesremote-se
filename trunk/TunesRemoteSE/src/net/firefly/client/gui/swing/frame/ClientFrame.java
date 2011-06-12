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
package net.firefly.client.gui.swing.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;

import net.firefly.client.controller.ConfigurationManager;
import net.firefly.client.controller.LibraryManager;
import net.firefly.client.controller.ResourceManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.context.events.FilteredSongListChangedEvent;
import net.firefly.client.gui.context.events.GlobalSongListChangedEvent;
import net.firefly.client.gui.context.listeners.FilteredSongListChangedEventListener;
import net.firefly.client.gui.context.listeners.GlobalSongListChangedEventListener;
import net.firefly.client.gui.swing.button.RepeatButton;
import net.firefly.client.gui.swing.button.ShuffleButton;
import net.firefly.client.gui.swing.dialog.ErrorDialog;
import net.firefly.client.gui.swing.menu.ClientMenuBar;
import net.firefly.client.gui.swing.other.NotificationWindow;
import net.firefly.client.gui.swing.other.SplashScreen;
import net.firefly.client.gui.swing.panel.GlobalContainer;
import net.firefly.client.model.data.Song;
import net.firefly.client.model.data.SongContainer;
import net.firefly.client.model.data.list.SongList;
import net.firefly.client.model.library.Library;
import net.firefly.client.model.library.LibraryInfo;
import net.firefly.client.player.events.PlayerErrorOccurredEvent;
import net.firefly.client.player.events.SongChangedEvent;
import net.firefly.client.player.listeners.PlayerErrorOccuredEventListener;
import net.firefly.client.player.listeners.SongChangedEventListener;
import net.firefly.client.tools.FireflyClientException;

public class ClientFrame extends JFrame implements PlayerErrorOccuredEventListener,
		GlobalSongListChangedEventListener, FilteredSongListChangedEventListener, SongChangedEventListener {

	private static final long serialVersionUID = 2027443707315807626L;

	protected Context context;

	protected Rectangle maxBounds;

	protected String DEFAULT_TITLE;

	protected NumberFormat nf;

	protected JLabel statusBarLabel;

	protected NotificationWindow notificationDialog;

	protected Border rootPaneBorder;

	public ClientFrame(Context context) {
		super();
		this.context = context;
		DEFAULT_TITLE = ResourceManager.getLabel("application.title", context.getConfig().getLocale());
		nf = NumberFormat.getInstance(context.getConfig().getLocale());
		nf.setMaximumFractionDigits(1);
		rootPaneBorder = new MatteBorder(0, 1, 1, 1, new Color(128, 128, 128));
		initialize();
	}

	private void initialize() {
		// -- root pane border
		getRootPane().setBorder(rootPaneBorder);

		// -- application icon
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("/net/firefly/client/resources/images/app.png")));

		// -- title
		this.setTitle(DEFAULT_TITLE);

		// -- menu bar
		this.setJMenuBar(new ClientMenuBar(context, this));

		// -- content
		this.setContentPane(new GlobalContainer(context, this));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// -- status bar
		JPanel statusBar = new JPanel(new GridBagLayout());
		GridBagConstraints separatorGBC = new GridBagConstraints(0, 0, 3, 1, 300, 0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 1, 1);
		GridBagConstraints shuffleButtonGBC = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 2, 0, 0), 1, 1);
		GridBagConstraints repeatButtonGBC = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 2, 0, 0), 1, 1);
		GridBagConstraints statusLabelGBC = new GridBagConstraints(2, 1, 1, 1, 300, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 1, 1);

		statusBarLabel = new JLabel(" ");
		statusBarLabel.setFont(statusBarLabel.getFont().deriveFont(Font.PLAIN));
		statusBar.add(new JSeparator(JSeparator.HORIZONTAL), separatorGBC);
		statusBar.add(new ShuffleButton(context), shuffleButtonGBC);
		statusBar.add(new RepeatButton(context), repeatButtonGBC);
		statusBar.add(statusBarLabel, statusLabelGBC);

		getContentPane().add(statusBar, BorderLayout.SOUTH);

		// -- size and position
		if (context.getConfig().getWindowTop() != -1) {
			this.setBounds(new Rectangle(context.getConfig().getWindowLeft(), context.getConfig().getWindowTop(), context
					.getConfig().getWindowWidth(), context.getConfig().getWindowHeight()));
		} else {
			this.setSize(new Dimension(1024, 768));
			center();
		}
		addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				JFrame tmp = (JFrame) e.getSource();
				if (tmp.getWidth() < 800 || tmp.getHeight() < 600) {
					tmp.setSize(800, 600);
				}
			}
		});
		
		addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentShown(ComponentEvent evt) {
				SplashScreen.close();
			}
		});

		if (context.getConfig().isNotificationEnabled()) {
			notificationDialog = new NotificationWindow(context, this);
		}

		// -- events
		context.addGlobalSongListChangedEventListener(this);
		context.addFilteredSongListChangedEventListener(this);
		context.getPlayer().addPlayerErrorOccuredEventListener(this);
		context.getPlayer().addSongChangedEventListener(this);

		// -- tootips
		ToolTipManager tooltipManager = ToolTipManager.sharedInstance();
		tooltipManager.setInitialDelay(1500);
		tooltipManager.setReshowDelay(1500);
	}

	private void center() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Point center = ge.getCenterPoint();
		Rectangle bounds = ge.getMaximumWindowBounds();
		int w = Math.max(bounds.width / 2, Math.min(getWidth(), bounds.width));
		int h = Math.max(bounds.height / 2, Math.min(getHeight(), bounds.height));
		int x = center.x - w / 2, y = center.y - h / 2;
		setBounds(x, y, w, h);
		if (w == bounds.width && h == bounds.height)
			setExtendedState(Frame.MAXIMIZED_BOTH);
		validate();
	}

	public Rectangle getMaximizedBounds() {
		return (maxBounds);
	}

	public synchronized void setMaximizedBounds(Rectangle maxBounds) {
		this.maxBounds = maxBounds;
		super.setMaximizedBounds(maxBounds);
	}

	public synchronized void setExtendedState(int state) {
		if (maxBounds == null && (state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
			Insets screenInsets = getToolkit().getScreenInsets(getGraphicsConfiguration());
			Rectangle screenSize = getGraphicsConfiguration().getBounds();
			Rectangle maxBounds = new Rectangle(screenInsets.left + screenSize.x, screenInsets.top + screenSize.y,
					screenSize.x + screenSize.width - screenInsets.right - screenInsets.left, screenSize.y
							+ screenSize.height - screenInsets.bottom - screenInsets.top);
			super.setMaximizedBounds(maxBounds);
		}

		super.setExtendedState(state);
	}

	public void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
	}

	
	public void updateConfigOnClose() {
		boolean libraryUpdated = false;
		boolean passwordUpdated = false;

		if (context.getLibraryInfo() != null) {
			String currentLibraryId = context.getLibraryInfo().getLibraryId();
			if (currentLibraryId != null) {
				boolean libraryAlreadySaved = LibraryManager.alreadySavedLibrary(context.getConfig().getConfigRootDirectory(), currentLibraryId);
				if (!libraryAlreadySaved) {
					libraryUpdated = true;
				} else {
					try {
						LibraryInfo previouslySavedLibraryInfos = LibraryManager.loadSavedLibraryInfos(context.getConfig().getConfigRootDirectory(), currentLibraryId);
						passwordUpdated = (context.getLibraryInfo().getPassword() != null && !context.getLibraryInfo()
								.getPassword().equals(previouslySavedLibraryInfos.getPassword()))
								|| (previouslySavedLibraryInfos.getPassword() != null && !previouslySavedLibraryInfos
										.getPassword().equals(context.getLibraryInfo().getPassword()));
						if (previouslySavedLibraryInfos.getSongCount() != context.getLibraryInfo().getSongCount()) {
							libraryUpdated = true;
						}
					} catch (FireflyClientException e1) {
						// ignore.
					}
				}
			}
		}

		int status = JOptionPane.YES_OPTION;
		if (libraryUpdated || passwordUpdated) {
			status = JOptionPane.showConfirmDialog(this, ResourceManager.getLabel("exit.dialog.message", context
					.getConfig().getLocale()), ResourceManager.getLabel("application.title", context.getConfig()
							.getLocale()), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		}

		if (status == JOptionPane.YES_OPTION) {
			Library library = new Library();
			library.setLibraryInfo(context.getLibraryInfo());
			library.setSongList(context.getMasterSongList());
			if (libraryUpdated) {
				// -- save library
				try {
					LibraryManager.saveLibrary(context.getConfig().getConfigRootDirectory(), library, true);
				} catch (FireflyClientException ex) {
					ErrorDialog.showDialog(this, ResourceManager.getLabel("dialog.save.library.error.message", context
							.getConfig().getLocale(), new String[] { ex.getMessage() }), ResourceManager.getLabel(
									"dialog.save.library.error.title", context.getConfig().getLocale()), ex, context.getConfig()
									.getLocale());
					return;
				}
			} else if (passwordUpdated) {
				// -- save library info
				try {
					LibraryManager.saveLibraryInfos(context.getConfig().getConfigRootDirectory(), library.getLibraryInfo(), true);
				} catch (FireflyClientException ex) {
					ErrorDialog.showDialog(this, ResourceManager.getLabel("dialog.save.library.error.message", context
							.getConfig().getLocale(), new String[] { ex.getMessage() }), ResourceManager.getLabel(
									"dialog.save.library.error.title", context.getConfig().getLocale()), ex, context.getConfig()
									.getLocale());
					return;
				}
			}

			// -- save configuration
			Rectangle rect = this.getBounds();
			context.getConfig().setWindowLeft(rect.x);
			context.getConfig().setWindowTop(rect.y);
			context.getConfig().setWindowWidth(rect.width);
			context.getConfig().setWindowHeight(rect.height);
			try {
				ConfigurationManager.saveConfiguration(context.getConfig());
			} catch (FireflyClientException ex) {
				ex.printStackTrace();
			}
			// -- quit
			//System.exit(0);
		} else if (status == JOptionPane.NO_OPTION){
			// -- save configuration
			Rectangle rect = this.getBounds();
			context.getConfig().setWindowLeft(rect.x);
			context.getConfig().setWindowTop(rect.y);
			context.getConfig().setWindowWidth(rect.width);
			context.getConfig().setWindowHeight(rect.height);
			try {
				ConfigurationManager.saveConfiguration(context.getConfig());
			} catch (FireflyClientException ex) {
				ex.printStackTrace();
			}
			// -- quit
			//System.exit(0);
		}
	}
	
	public void refresh(){
		SwingUtilities.updateComponentTreeUI(this);
		getRootPane().setBorder(rootPaneBorder);
	}

	public void onPlayerError(PlayerErrorOccurredEvent e) {
		ErrorDialog.showDialog(this, e.getError().getMessage(), "Player error", e.getError(), context.getConfig()
				.getLocale());
		context.getPlayer().stopPlayback();
	}

	public void onGlobalSongListChange(GlobalSongListChangedEvent evt) {
		if (context.getLibraryInfo() != null) {
			LibraryInfo info = context.getLibraryInfo();
			String libraryId = info.getLibraryId();
			String libraryName = info.getLibraryName();
			String host = info.getHost();
			String port = "" + info.getPort();
			String title = DEFAULT_TITLE + "  -  [" + libraryId + "]    " + libraryName + " @ " + host + ":" + port;
			if (context.getServerVersion() != null && context.getServerVersion().trim().length() > 0){
				title += " [" + context.getServerVersion() + "]";
			}
			setTitle(title);
		} else {
			setTitle(DEFAULT_TITLE);
		}
	}
	
	public void onSongChange(SongChangedEvent evt) {
		SongContainer sc = evt.getSongPlayed();
		Song s = sc.getSong();
		if (s != null){
			String artist = s.getArtist();
			if (artist == null || artist.trim().length() == 0 ){
				artist = ResourceManager.getLabel("table.unknown.artist", context.getConfig().getLocale()); 
			}
			setTitle(s.getTitle() + " (" + artist + ")  -  " + DEFAULT_TITLE);
		} else {
			if (context.getLibraryInfo() != null) {
				LibraryInfo info = context.getLibraryInfo();
				String libraryId = info.getLibraryId();
				String libraryName = info.getLibraryName();
				String host = info.getHost();
				String port = "" + info.getPort();
				String title = DEFAULT_TITLE + "  -  [" + libraryId + "]    " + libraryName + " @ " + host + ":" + port;
				if (context.getServerVersion() != null && context.getServerVersion().trim().length() > 0){
					title += " [" + context.getServerVersion() + "]";
				}
				setTitle(title);
			} else {
				setTitle(DEFAULT_TITLE);
			}
		}
	}

	public void onFilteredSongListChange(FilteredSongListChangedEvent evt) {
		SongList sl = evt.getNewFilteredSongList();
		int nbSongs = 0;
		long totalTime = 0;
		long totalSize = 0;
		if (sl != null) {
			Iterator<SongContainer> it = sl.iterator();
			while (it.hasNext()) {
				SongContainer sc = it.next();
				Song s = sc.getSong();
				if (s != null) {
					nbSongs++;
					totalTime += s.getTime();
					totalSize += s.getSize();
				}
			}
			if (nbSongs != 0) {
				String time = "";
				// time
				totalTime = totalTime / 1000; // --> seconds
				if (totalTime < 60) {
					time = totalTime + " "
							+ ResourceManager.getLabel("status.bar.time.second", context.getConfig().getLocale());

				} else if (totalTime < 3600) {
					float itime = ((float) totalTime / 60);
					time = nf.format(itime) + " "
							+ ResourceManager.getLabel("status.bar.time.minute", context.getConfig().getLocale());
				} else if (totalTime < (3600 * 24)) {
					float itime = ((float) totalTime / 3600);
					time = nf.format(itime) + " "
							+ ResourceManager.getLabel("status.bar.time.hour", context.getConfig().getLocale());
				} else {
					float itime = ((float) totalTime / (3600 * 24));
					time = nf.format(itime) + " "
							+ ResourceManager.getLabel("status.bar.time.day", context.getConfig().getLocale());
				}

				String song = nbSongs + " " + ResourceManager.getLabel("status.bar.song", context.getConfig().getLocale());
				String info;
				
				if (totalSize > 0) {
					String size = "";
					totalSize = totalSize / 1024; // --> kbytes
					if (totalSize < 1024) {
						size = totalSize + " "
						+ ResourceManager.getLabel("status.bar.size.kilo", context.getConfig().getLocale());
					} else if (totalSize < (1024 * 1024)) {
						float isize = ((float) totalSize / 1024);
						size = nf.format(isize) + " "
						+ ResourceManager.getLabel("status.bar.size.mega", context.getConfig().getLocale());
					} else if (totalSize < (1024 * 1024 * 1024)) {
						float isize = ((float) totalSize / (1024 * 1024));
						size = nf.format(isize) + " "
						+ ResourceManager.getLabel("status.bar.size.giga", context.getConfig().getLocale());
					}
					
					info = song + " - " + time + " - " + size;
				} else {
					info = song + " - " + time;
				}

				statusBarLabel.setText(info);
			} else {
				statusBarLabel.setText(" ");
			}
		} else {
			statusBarLabel.setText(" ");
		}
	}
	
	/**
	 * Workaround for the bug #2682496 : minimize button sometimes disappears
	 * see com.l2fprod.gui.plaf.skin.FrameWindow.isIcon()
	 * Hope this won't have side effects !!
	 */
	public synchronized int getState() {
	     return Frame.NORMAL;
	}
}
