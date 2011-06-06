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
package net.firefly.client.model.configuration;

import java.util.Locale;

import javax.swing.event.EventListenerList;

import net.firefly.client.controller.ConfigurationManager;
import net.firefly.client.controller.ListManager;
import net.firefly.client.http.protocol.Protocol;

public class Configuration {

	// -- CONSTANT(S)

	public static final String CONFIG_DIRECTORY = ".tunesremote-se";
	
	public static final String CONFIG_FILENAME = "config.xml";

	public static final String CONFIG_LIBRARIES_SUBDIRECTORY = "libraries";
	
	public static final String CONFIG_LIBRARY_INFO_FILENAME = "library-info.xml";

	public static final String CONFIG_LIBRARY_SONG_LIST_FILENAME = "song-list.xml";

	public static final String SYSTEM_LOOK_AND_FEEL_NAME = "Normal";

	// -- default values for default configuration
	public static final String DEFAULT_CONFIG_ROOT_DIRECTORY = ConfigurationManager.getConfigRootDirectoryUser();
	
	public static final Locale DEFAULT_LOCALE = new Locale("en", "GB");

	public static final Locale[] AVAILABLE_LOCALES = { new Locale("de", "DE"), new Locale("en", "GB"),
			new Locale("fr", "FR"), new Locale("it", "IT"), new Locale("nl", "NL") };

	public static final int DEFAULT_WINDOW_TOP = -1;

	public static final int DEFAULT_WINDOW_LEFT = -1;

	public static final int DEFAULT_WINDOW_WIDTH = 1024;

	public static final int DEFAULT_WINDOW_HEIGHT = 768;

	public static final int DEFAULT_LEFT_PANEL_WIDTH = 200;

	public static final boolean DEFAULT_NOTIFICATION_ENABLED = true;

	public static final boolean DEFAULT_HTTP_COMPRESSION_ENABLED = false;

	public static final Protocol DEFAULT_MAIN_PROTOCOL = Protocol.XMLDAAP;

	public static final boolean DEFAULT_SORT_STATIC_PLAYLISTS = true;

	public static final String DEFAULT_SONGLIST_SORTING_CRITERIA = "";

	public static final boolean DEFAULT_SHOW_GENRE = true;

	public static final int DEFAULT_SEARCH_FLAG = ListManager.FLG_SEARCH_GENRE + ListManager.FLG_SEARCH_ARTIST
			+ ListManager.FLG_SEARCH_ALBUM + ListManager.FLG_SEARCH_TITLE;
		
	public static int DEFAULT_READ_BUFFER_SIZE = 4;
	
	public static int MAX_READ_BUFFER_SIZE = 10000;
	
	// -- ATTRIBUTE(S)
	protected Locale locale;

	protected boolean httpCompressionEnabled;

	protected Protocol mainProtocol;

	protected int windowTop;

	protected int windowLeft;

	protected int windowWidth;

	protected int windowHeight;

	protected int leftPanelWidth;

	protected boolean notificationEnabled;

	protected String songListSortingCriteria;

	protected boolean showGenre;

	protected int searchFlag;
	
	protected int readBufferSize;
	
	protected String configRootDirectory;
	
	protected EventListenerList listenerList = new EventListenerList();

	// -- INSTANCE

	protected static Configuration instance;

	// -- CONSTRUCTOR(S)
	protected Configuration() {

	}

	// -- METHOD(S)
	public static synchronized Configuration getInstance() {
		if (instance == null) {
			instance = initConfiguration();
		}
		return instance;
	}

	public static boolean canActivateNotification() {
		boolean canActivateNotification = false;
		try {
			String javaVersion = System.getProperty("java.specification.version");
			float f = Float.parseFloat(javaVersion);
			if (f > 1.4) {
				canActivateNotification = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return canActivateNotification;
	}

	protected static Configuration initConfiguration() {
		Configuration conf = new Configuration();
		conf.configRootDirectory = DEFAULT_CONFIG_ROOT_DIRECTORY;
		conf.locale = DEFAULT_LOCALE;
		conf.windowTop = DEFAULT_WINDOW_TOP;
		conf.windowLeft = DEFAULT_WINDOW_LEFT;
		conf.windowWidth = DEFAULT_WINDOW_WIDTH;
		conf.windowHeight = DEFAULT_WINDOW_HEIGHT;
		conf.leftPanelWidth = DEFAULT_LEFT_PANEL_WIDTH;
		conf.httpCompressionEnabled = DEFAULT_HTTP_COMPRESSION_ENABLED;
		conf.mainProtocol = DEFAULT_MAIN_PROTOCOL;
		if (canActivateNotification()) {
			conf.notificationEnabled = true;
		}
		conf.songListSortingCriteria = DEFAULT_SONGLIST_SORTING_CRITERIA;

		conf.showGenre = DEFAULT_SHOW_GENRE;
		conf.searchFlag = DEFAULT_SEARCH_FLAG;
		conf.readBufferSize = DEFAULT_READ_BUFFER_SIZE;		

		return conf;
	}

	// -- GETTER(S) and SETTER(S)
	
	
	public String getConfigRootDirectory() {
		return configRootDirectory;
	}

	public void setConfigRootDirectory(String configRootDirectory) {
		this.configRootDirectory = configRootDirectory;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public boolean isNotificationEnabled() {
		return notificationEnabled;
	}

	public void setNotificationEnabled(boolean notificationEnabled) {
		if (notificationEnabled && canActivateNotification()) {
			this.notificationEnabled = true;
		} else {
			this.notificationEnabled = false;
		}
	}

	public boolean isHttpCompressionEnabled() {
		return httpCompressionEnabled;
	}

	public void setHttpCompressionEnabled(boolean httpCompressionEnabled) {
		this.httpCompressionEnabled = httpCompressionEnabled;
	}

	public Protocol getMainProtocol() {
		return mainProtocol;
	}

	public void setMainProtocol(Protocol mainProtocol) {
		this.mainProtocol = mainProtocol;
	}

	/**
	 * @return Returns the windowHeight.
	 */
	public int getWindowHeight() {
		return windowHeight;
	}

	/**
	 * @param windowHeight
	 *            The windowHeight to set.
	 */
	public void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
	}

	public int getLeftPanelWidth() {
		return leftPanelWidth;
	}

	public void setLeftPanelWidth(int leftPanelWidth) {
		this.leftPanelWidth = leftPanelWidth;
	}

	public int getWindowLeft() {
		return windowLeft;
	}

	public void setWindowLeft(int windowLeft) {
		this.windowLeft = windowLeft;
	}

	public int getWindowTop() {
		return windowTop;
	}

	public void setWindowTop(int windowTop) {
		this.windowTop = windowTop;
	}

	public int getWindowWidth() {
		return windowWidth;
	}

	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
	}

	public String getSongListSortingCriteria() {
		return songListSortingCriteria;
	}

	public void setSongListSortingCriteria(String songListSortingCriteria) {
		this.songListSortingCriteria = songListSortingCriteria;
	}

	public boolean isShowGenre() {
		return showGenre;
	}

	public void setShowGenre(boolean showGenre) {
		this.showGenre = showGenre;
	}
	
	public int getSearchFlag() {
		return searchFlag;
	}

	public void setSearchFlag(int searchFlag) {
		this.searchFlag = searchFlag;
	}
	
	public int getReadBufferSize() {
		return readBufferSize;
	}

	public void setReadBufferSize(int readBufferSize) {
		this.readBufferSize = readBufferSize;
	}
	

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("------------------------ config ------------------------\n");
		sb.append("locale                         : ").append(this.locale).append("\n");
		sb.append("http-compression-enabled       : ").append(this.httpCompressionEnabled).append("\n");
		sb.append("main-protocol                  : ").append(this.mainProtocol).append("\n");
		sb.append("window-top                     : ").append(this.windowTop).append("\n");
		sb.append("window-left                    : ").append(this.windowLeft).append("\n");
		sb.append("window-width                   : ").append(this.windowWidth).append("\n");
		sb.append("window-height                  : ").append(this.windowHeight).append("\n");
		sb.append("notification-enabled           : ").append(this.notificationEnabled).append("\n");
		sb.append("song-list-sorting-criteria     : ").append(this.songListSortingCriteria).append("\n");
		sb.append("show-genre                     : ").append(this.showGenre).append("\n");
		sb.append("search-flag                    : ").append(this.searchFlag).append("\n");
		sb.append("read-buffer-size               : ").append(this.readBufferSize).append("\n");
		sb.append("---------------------------------------------------------\n");
		return sb.toString();
	}
}
