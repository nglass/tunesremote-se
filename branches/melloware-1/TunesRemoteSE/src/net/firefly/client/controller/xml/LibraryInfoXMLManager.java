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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.firefly.client.model.library.LibraryInfo;
import net.firefly.client.tools.FireflyClientException;
import net.firefly.client.tools.XMLTools;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

public class LibraryInfoXMLManager {

	// -- CONSTANT(S)
	protected static LibraryInfoXMLManager instance;

	// -- library infos
	public static String XML_LIB_INFO_WRAPPER_ELEM = "library-info";

	public static String XML_LIB_INFO_ID = "id";

	public static String XML_LIB_INFO_NAME = "name";

	public static String XML_LIB_INFO_HOST = "host";

	public static String XML_LIB_INFO_PORT = "port";

	public static String XML_LIB_INFO_PASSWORD = "password";
	
	public static String XML_LIB_INFO_ENCODED_PASSWORD = "passwd";

	public static String XML_LIB_INFO_SONG_COUNT = "song-count";

	// -- CONSTRUCTOR(S)
	protected LibraryInfoXMLManager() {

	}

	// -- METHOD(S)
	public static synchronized LibraryInfoXMLManager getInstance() {
		if (instance == null) {
			instance = new LibraryInfoXMLManager();
		}
		return instance;
	}

	public void marshal(LibraryInfo libraryInfo, OutputStream outputStream) throws FireflyClientException {
		marshal(libraryInfo, outputStream, true);
	}

	public void marshal(LibraryInfo libraryInfo, OutputStream outputStream, boolean outputXMLHeader)
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
		pw.println("<" + XML_LIB_INFO_WRAPPER_ELEM + ">");

		// -- id
		if (libraryInfo.getLibraryId() != null) {
			pw.print("<" + XML_LIB_INFO_ID + ">");
			pw.print(XMLTools.escapeXml(libraryInfo.getLibraryId()));
			pw.println("</" + XML_LIB_INFO_ID + ">");
		} else {
			pw.println("<" + XML_LIB_INFO_ID + "/>");
		}

		// -- name
		if (libraryInfo.getLibraryName() != null) {
			pw.print("<" + XML_LIB_INFO_NAME + ">");
			pw.print(XMLTools.escapeXml(libraryInfo.getLibraryName()));
			pw.println("</" + XML_LIB_INFO_NAME + ">");
		} else {
			pw.println("<" + XML_LIB_INFO_NAME + "/>");
		}

		// -- host
		if (libraryInfo.getHost() != null) {
			pw.print("<" + XML_LIB_INFO_HOST + ">");
			pw.print(XMLTools.escapeXml(libraryInfo.getHost()));
			pw.println("</" + XML_LIB_INFO_HOST + ">");
		} else {
			pw.println("<" + XML_LIB_INFO_HOST + "/>");
		}

		// -- port

		pw.print("<" + XML_LIB_INFO_PORT + ">");
		if (libraryInfo.getPort() != 0) {
			pw.print(libraryInfo.getPort());
		} else {
			pw.print(LibraryInfo.DEFAULT_DAAP_PORT);
		}
		pw.println("</" + XML_LIB_INFO_PORT + ">");

		// -- song count
		pw.print("<" + XML_LIB_INFO_SONG_COUNT + ">");
		pw.print(libraryInfo.getSongCount());
		pw.println("</" + XML_LIB_INFO_SONG_COUNT + ">");

		pw.println("</" + XML_LIB_INFO_WRAPPER_ELEM + ">");
		pw.flush();
	}

	public LibraryInfo unmarshal(InputStream inputStream) throws FireflyClientException {

		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		saxParserFactory.setValidating(false);
		SAXParser saxParser = null;

		try {
			saxParser = saxParserFactory.newSAXParser();
		} catch (ParserConfigurationException e) {
			throw new FireflyClientException(e, "Parser configuration exception while unmarshalling the library info.");
		} catch (SAXException e) {
			throw new FireflyClientException(e,
					"SAXException while unmarshalling the library info (instanciating the SAX parser).");
		}

		LibraryInfoXmlFilter xmlFilter = new LibraryInfoXmlFilter();
		XMLReader xmlReader = null;

		try {
			xmlReader = saxParser.getXMLReader();
		} catch (SAXException e) {
			throw new FireflyClientException(e,
					"SAXException while unmarshalling the library info (getting the XML reader).");
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
			throw new FireflyClientException(e, "SAXException while unmarshalling the library info (during parsing).");
		} catch (IOException e) {
			throw new FireflyClientException(e, "IOException while unmarshalling the library info (during parsing).");
		}

		// -- output
		LibraryInfo libraryInfo = xmlFilter.getLibraryInfo();

		return libraryInfo;

	}

	// -- INNER CLASS(ES)
	class LibraryInfoXmlFilter extends XMLFilterImpl {

		protected LibraryInfo libraryInfo;

		protected String currentTag;

		protected StringBuffer buffer;

		public LibraryInfoXmlFilter() {
		}

		public void startDocument() throws SAXException {
			super.startDocument();
			this.libraryInfo = new LibraryInfo();
		}

		public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
			String tagName = qName; // (localName != null)?localName:qName;
			this.currentTag = tagName;
			super.startElement(uri, localName, qName, attrs);
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			String tagName = qName; // (localName != null)?localName:qName;
			super.endElement(uri, localName, qName);

			if (XML_LIB_INFO_ID.equals(tagName)) {
				String value = this.buffer.toString().trim();
				if (value.length() > 0) {
					libraryInfo.setLibraryId(value);
				}
				this.buffer = new StringBuffer();
			} else if (XML_LIB_INFO_NAME.equals(tagName)) {
				String value = this.buffer.toString().trim();
				if (value.length() > 0) {
					libraryInfo.setLibraryName(value);
				}
				this.buffer = new StringBuffer();
			} else if (XML_LIB_INFO_HOST.equals(tagName)) {
				String value = this.buffer.toString().trim();
				if (value.length() > 0) {
					libraryInfo.setHost(value);
				}
				this.buffer = new StringBuffer();
			} else if (XML_LIB_INFO_PORT.equals(tagName)) {
				String value = this.buffer.toString().trim();
				try {
					libraryInfo.setPort(Integer.parseInt(value));
				} catch (Exception e) {
					libraryInfo.setPort(LibraryInfo.DEFAULT_DAAP_PORT);
				}
				this.buffer = new StringBuffer();
			} else if (XML_LIB_INFO_PASSWORD.equals(tagName)) {
				String value = this.buffer.toString().trim();
				if (value.length() > 0) {
					libraryInfo.setPassword(value);
				}
				this.buffer = new StringBuffer();
			} else if (XML_LIB_INFO_SONG_COUNT.equals(tagName)) {
				String value = this.buffer.toString().trim();
				try {
					libraryInfo.setSongCount(Integer.parseInt(value));
				} catch (Exception e) {
					libraryInfo.setSongCount(0);
				}
				this.buffer = new StringBuffer();
			}
		}

		public void characters(char buf[], int offset, int length) throws SAXException {
			if (XML_LIB_INFO_ID.equals(this.currentTag) || XML_LIB_INFO_NAME.equals(this.currentTag)
					|| XML_LIB_INFO_HOST.equals(this.currentTag) || XML_LIB_INFO_PORT.equals(this.currentTag)
					|| XML_LIB_INFO_PASSWORD.equals(this.currentTag) || XML_LIB_INFO_SONG_COUNT.equals(this.currentTag)
					|| XML_LIB_INFO_ENCODED_PASSWORD.equals(this.currentTag)
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
		public LibraryInfo getLibraryInfo() {
			return libraryInfo;
		}

	}

}
