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
package net.firefly.client.controller.xml;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.firefly.client.model.configuration.Configuration;
import net.firefly.client.tools.FireflyClientException;
import net.firefly.client.tools.XMLTools;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

public class ConfigurationXMLManager {

	// -- CONSTANT(S)
	protected static ConfigurationXMLManager instance;

	public static String XML_CONF_WRAPPER_ELEM = "tunesremote-se-config";

	public static String XML_CONF_LOCALE_LANGUAGE_ELEM = "locale-language";

	public static String XML_CONF_LOCALE_COUNTRY_ELEM = "locale-country";

	public static String XML_CONF_WINDOW_TOP_ELEM = "window-top";

	public static String XML_CONF_WINDOW_LEFT_ELEM = "window-left";

	public static String XML_CONF_WINDOW_WIDTH_ELEM = "window-width";

	public static String XML_CONF_WINDOW_HEIGHT_ELEM = "window-height";

	public static String XML_CONF_LEFT_PANEL_WIDTH_ELEM = "left-panel-width";

	public static String XML_CONF_NOTIFICATION_ENABLED_ELEM = "notification-enabled";

	public static String XML_CONF_SONGLIST_SORTING_CRITERIA_ELEM = "song-list-sorting-criteria";
		
	public static String XML_CONF_SHOW_GENRE_ELEM = "show-genre";
	
	public static String XML_CONF_READ_BUFFER_SIZE_ELEM = "read-buffer-size";
	
	public static String XML_CONF_SEARCH_FLAG_ELEM = "search-flag";
	
	// -- CONSTRUCTOR(S)
	protected ConfigurationXMLManager() {

	}

	// -- METHOD(S)
	public static synchronized ConfigurationXMLManager getInstance() {
		if (instance == null) {
			instance = new ConfigurationXMLManager();
		}
		return instance;
	}

	public void marshal(Configuration config, OutputStream outputStream) throws FireflyClientException {
		marshal(config, outputStream, true);
	}

	public void marshal(Configuration config, OutputStream outputStream, boolean outputXMLHeader)
			throws FireflyClientException {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(outputStream), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			pw = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(outputStream)));
		}
		if (outputXMLHeader) {
			// -- XML header
			pw.println(XMLTools.getXMLHeader());
		}
		// -- XML root
		pw.println("<" + XML_CONF_WRAPPER_ELEM + ">");
		// -- locale
		if (config.getLocale() != null) {
			if (config.getLocale().getLanguage() != null) {
				pw.print("\t<" + XML_CONF_LOCALE_LANGUAGE_ELEM + ">");
				pw.print(config.getLocale().getLanguage());
				pw.println("</" + XML_CONF_LOCALE_LANGUAGE_ELEM + ">");
			} else {
				pw.println("\t<" + XML_CONF_LOCALE_LANGUAGE_ELEM + "/>");
			}
			if (config.getLocale().getCountry() != null) {
				pw.print("\t<" + XML_CONF_LOCALE_COUNTRY_ELEM + ">");
				pw.print(config.getLocale().getCountry());
				pw.println("</" + XML_CONF_LOCALE_COUNTRY_ELEM + ">");
			} else {
				pw.println("\t<" + XML_CONF_LOCALE_COUNTRY_ELEM + "/>");
			}
		} else {
			pw.println("\t<" + XML_CONF_LOCALE_LANGUAGE_ELEM + "/>");
			pw.println("\t<" + XML_CONF_LOCALE_COUNTRY_ELEM + "/>");
		}

		// -- notification enabled
		pw.print("\t<" + XML_CONF_NOTIFICATION_ENABLED_ELEM + ">");
		pw.print(config.isNotificationEnabled());
		pw.println("</" + XML_CONF_NOTIFICATION_ENABLED_ELEM + ">");

		// -- window top
		pw.print("\t<" + XML_CONF_WINDOW_TOP_ELEM + ">");
		pw.print(config.getWindowTop());
		pw.println("</" + XML_CONF_WINDOW_TOP_ELEM + ">");

		// -- window left
		pw.print("\t<" + XML_CONF_WINDOW_LEFT_ELEM + ">");
		pw.print(config.getWindowLeft());
		pw.println("</" + XML_CONF_WINDOW_LEFT_ELEM + ">");

		// -- window width
		pw.print("\t<" + XML_CONF_WINDOW_WIDTH_ELEM + ">");
		pw.print(config.getWindowWidth());
		pw.println("</" + XML_CONF_WINDOW_WIDTH_ELEM + ">");

		// -- window height
		pw.print("\t<" + XML_CONF_WINDOW_HEIGHT_ELEM + ">");
		pw.print(config.getWindowHeight());
		pw.println("</" + XML_CONF_WINDOW_HEIGHT_ELEM + ">");

		// -- left panel width
		pw.print("\t<" + XML_CONF_LEFT_PANEL_WIDTH_ELEM + ">");
		pw.print(config.getLeftPanelWidth());
		pw.println("</" + XML_CONF_LEFT_PANEL_WIDTH_ELEM + ">");
		
		// -- sort-static-playlists

		// -- search-missing-covers-on-amazon
		
		// -- songlist-sorting-criteria
		pw.print("\t<" + XML_CONF_SONGLIST_SORTING_CRITERIA_ELEM+ ">");
		pw.print(config.getSongListSortingCriteria());
		pw.println("</" + XML_CONF_SONGLIST_SORTING_CRITERIA_ELEM + ">");
				
		// -- excluded extensions
	
		// -- show genre
		pw.print("\t<" + XML_CONF_SHOW_GENRE_ELEM+ ">");
		pw.print(config.isShowGenre());
		pw.println("</" + XML_CONF_SHOW_GENRE_ELEM + ">");
		
		// -- search flag
		pw.print("\t<" + XML_CONF_SEARCH_FLAG_ELEM+ ">");
		pw.print(config.getSearchFlag());
		pw.println("</" + XML_CONF_SEARCH_FLAG_ELEM + ">");
		
		// -- read-buffer-size
		pw.print("\t<" + XML_CONF_READ_BUFFER_SIZE_ELEM + ">");
		pw.print(config.getReadBufferSize());
		pw.println("</" + XML_CONF_READ_BUFFER_SIZE_ELEM + ">");
		
		pw.println("</" + XML_CONF_WRAPPER_ELEM + ">");
		pw.flush();
	}

	public Configuration unmarshal(InputStream inputStream) throws FireflyClientException {

		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		saxParserFactory.setValidating(false);
		SAXParser saxParser = null;

		try {
			saxParser = saxParserFactory.newSAXParser();
		} catch (ParserConfigurationException e) {
			throw new FireflyClientException(e, "Parser configuration exception while unmarshalling the configuration.");
		} catch (SAXException e) {
			throw new FireflyClientException(e,
					"SAXException while unmarshalling the configuration (instanciating the SAX parser).");
		}

		ConfigurationXmlFilter xmlFilter = new ConfigurationXmlFilter();
		XMLReader xmlReader = null;

		try {
			xmlReader = saxParser.getXMLReader();
		} catch (SAXException e) {
			throw new FireflyClientException(e,
					"SAXException while unmarshalling the configuration (getting the XML reader).");
		}
		xmlFilter.setParent(xmlReader);

		InputSource inputSource = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, XMLTools.DEFAULT_XML_ENCODING));
			inputSource = new InputSource(br);
		} catch (UnsupportedEncodingException e) {
			// -- should not happen
			inputSource = new InputSource(inputStream);
		}

		try {
			xmlFilter.parse(inputSource);
		} catch (SAXException e) {
			throw new FireflyClientException(e, "SAXException while unmarshalling the configuration (during parsing).");
		} catch (IOException e) {
			throw new FireflyClientException(e, "IOException while unmarshalling the configuration (during parsing).");
		}

		// -- output
		Configuration config = xmlFilter.getConfiguration();

		return config;

	}

	// -- INNER CLASS(ES)
	class ConfigurationXmlFilter extends XMLFilterImpl {

		protected Configuration config;

		protected String currentTag;

		protected StringBuffer buffer;

		protected String language;

		protected String country;

		public ConfigurationXmlFilter() {
		}

		public void startDocument() throws SAXException {
			super.startDocument();
			this.config = Configuration.getInstance();
			this.config.setLocale(Configuration.DEFAULT_LOCALE);
		}

		public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
			String tagName = qName; // (localName != null)?localName:qName;
			this.currentTag = tagName;
			super.startElement(uri, localName, qName, attrs);
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			String tagName = qName; // (localName != null)?localName:qName;
			super.endElement(uri, localName, qName);
			if (XML_CONF_LOCALE_LANGUAGE_ELEM.equals(tagName)) {
				String value = this.buffer.toString().trim();
				if (value.length() > 0) {
					this.language = value.toString();
				} else {
					this.language = null;
				}
				this.buffer = new StringBuffer();
			} else if (XML_CONF_LOCALE_COUNTRY_ELEM.equals(tagName)) {
				String value = this.buffer.toString().trim();
				if (value.length() > 0) {
					this.country = value.toString();
				} else {
					this.country = null;
				}
				this.buffer = new StringBuffer();
			} else if (XML_CONF_NOTIFICATION_ENABLED_ELEM.equals(tagName)) {
				String value = this.buffer.toString().trim();
				if (value.length() > 0) {
					try {
						config.setNotificationEnabled(new Boolean(value.toString().trim()).booleanValue());
					} catch (Exception e) {
						config.setNotificationEnabled(Configuration.DEFAULT_NOTIFICATION_ENABLED);
					}
				} else {
					config.setNotificationEnabled(Configuration.DEFAULT_NOTIFICATION_ENABLED);
				}
				this.buffer = new StringBuffer();
			} else if (XML_CONF_WINDOW_TOP_ELEM.equals(tagName)) {
				try {
					config.setWindowTop(Integer.parseInt(this.buffer.toString().trim()));
				} catch (Exception e) {
					config.setWindowTop(Configuration.DEFAULT_WINDOW_TOP);
				}
				this.buffer = new StringBuffer();
			} else if (XML_CONF_WINDOW_LEFT_ELEM.equals(tagName)) {
				try {
					config.setWindowLeft(Integer.parseInt(this.buffer.toString().trim()));
				} catch (Exception e) {
					config.setWindowLeft(Configuration.DEFAULT_WINDOW_LEFT);
				}
				this.buffer = new StringBuffer();
			} else if (XML_CONF_WINDOW_WIDTH_ELEM.equals(tagName)) {
				try {
					config.setWindowWidth(Integer.parseInt(this.buffer.toString().trim()));
				} catch (Exception e) {
					config.setWindowWidth(Configuration.DEFAULT_WINDOW_WIDTH);
				}
				this.buffer = new StringBuffer();
			} else if (XML_CONF_WINDOW_HEIGHT_ELEM.equals(tagName)) {
				try {
					config.setWindowHeight(Integer.parseInt(this.buffer.toString().trim()));
				} catch (Exception e) {
					config.setWindowHeight(Configuration.DEFAULT_WINDOW_HEIGHT);
				}
				this.buffer = new StringBuffer();
			} else if (XML_CONF_LEFT_PANEL_WIDTH_ELEM.equals(tagName)) {
				try {
					config.setLeftPanelWidth(Integer.parseInt(this.buffer.toString().trim()));
				} catch (Exception e) {
					config.setLeftPanelWidth(Configuration.DEFAULT_LEFT_PANEL_WIDTH);
				}
				this.buffer = new StringBuffer();
			} else if (XML_CONF_SONGLIST_SORTING_CRITERIA_ELEM.equals(tagName)) {
				String value = this.buffer.toString().trim();
				try {
					config.setSongListSortingCriteria(value);
				} catch (Exception e) {
					config.setSongListSortingCriteria(Configuration.DEFAULT_SONGLIST_SORTING_CRITERIA);
				}
				this.buffer = new StringBuffer();
			} else if (XML_CONF_SHOW_GENRE_ELEM.equals(tagName)) {
				String value = this.buffer.toString().trim();
				try {
					config.setShowGenre(new Boolean(value.toString()).booleanValue());
				} catch (Exception e) {
					config.setShowGenre(Configuration.DEFAULT_SHOW_GENRE);
				}
				this.buffer = new StringBuffer();
			} else if (XML_CONF_SEARCH_FLAG_ELEM.equals(tagName)) {
				try {
					config.setSearchFlag(Integer.parseInt(this.buffer.toString().trim()));
				} catch (Exception e) {
					config.setSearchFlag(Configuration.DEFAULT_SEARCH_FLAG);
				}
				this.buffer = new StringBuffer();
			} else if (XML_CONF_READ_BUFFER_SIZE_ELEM.equals(tagName)) {
				try {
					int size = Integer.parseInt(this.buffer.toString().trim());
					if (size > 4 && size < Configuration.MAX_READ_BUFFER_SIZE){
						config.setReadBufferSize(size);
					}
				} catch (Exception e) {
					config.setReadBufferSize(Configuration.DEFAULT_READ_BUFFER_SIZE);
				}
				this.buffer = new StringBuffer();
			}
		}

		public void endDocument() throws SAXException {
			if (this.language != null) {
				if (this.country != null) {
					this.config.setLocale(new Locale(language, country));
				} else {
					this.config.setLocale(new Locale(language));
				}
			}
			super.endDocument();
		}

		public void characters(char buf[], int offset, int length) throws SAXException {
			if (XML_CONF_LOCALE_LANGUAGE_ELEM.equals(this.currentTag)
					|| XML_CONF_LOCALE_COUNTRY_ELEM.equals(this.currentTag)
					|| XML_CONF_WINDOW_HEIGHT_ELEM.equals(this.currentTag)
					|| XML_CONF_WINDOW_LEFT_ELEM.equals(this.currentTag) || XML_CONF_WINDOW_TOP_ELEM.equals(this.currentTag)
					|| XML_CONF_WINDOW_WIDTH_ELEM.equals(this.currentTag)
					|| XML_CONF_NOTIFICATION_ENABLED_ELEM.equals(this.currentTag)
					|| XML_CONF_LEFT_PANEL_WIDTH_ELEM.equals(this.currentTag)
					|| XML_CONF_SONGLIST_SORTING_CRITERIA_ELEM.equals(this.currentTag)
					|| XML_CONF_SHOW_GENRE_ELEM.equals(this.currentTag)
					|| XML_CONF_SEARCH_FLAG_ELEM.equals(this.currentTag)
					|| XML_CONF_READ_BUFFER_SIZE_ELEM.equals(this.currentTag)
			) {
				if (this.buffer == null) {
					this.buffer = new StringBuffer();
				}
				for (int i = offset; i < offset + length; i++) {
					this.buffer.append(buf[i]);
				}
			}
			super.characters(buf, offset, length);
		}

		/**
		 * @return
		 */
		public Configuration getConfiguration() {
			return config;
		}

	}

}
