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

public class XMLTools {

	public static final String DEFAULT_XML_ENCODING = "UTF-8";

	public static String escapeXml(String s) {
		return s.replaceAll("&", "&amp;").replaceAll(">", "&gt;").replaceAll("<", "&lt;").replaceAll("'", "&apos;").replaceAll("\"",
				"&quot;");
	}

	public static String getXMLHeader(String encoding) {
		return "<?xml version=\"1.0\" encoding=\"" + encoding.toUpperCase() + "\" standalone=\"yes\"?>";
	}

	public static String getXMLHeader() {
		return "<?xml version=\"1.0\" encoding=\"" + DEFAULT_XML_ENCODING.toUpperCase() + "\" standalone=\"yes\"?>";
	}

}