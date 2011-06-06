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
package net.firefly.client.http.protocol;

public class Protocol {

	private static final int PROTOCOL_RSP = 0;

	private static final int PROTOCOL_XMLDAAP = 1;

	private static final String PROTOCOL_RSP_NAME = "RSP";

	private static final String PROTOCOL_XMLDAAP_NAME = "XML-DAAP";

	private String protocolName;

	private int protocolType;

	public static final Protocol RSP = new Protocol(PROTOCOL_RSP, PROTOCOL_RSP_NAME);

	public static final Protocol XMLDAAP = new Protocol(PROTOCOL_XMLDAAP, PROTOCOL_XMLDAAP_NAME);

	private Protocol(int protocolType, String protocolName) {
		this.protocolType = protocolType;
		this.protocolName = protocolName;
	}

	public boolean equals(Object o) {
		try {
			Protocol p = (Protocol) o;
			return this.protocolType == p.protocolType;
		} catch (ClassCastException e) {
			return false;
		}
	}

	public static Protocol getProtocol(String protocolName) {
		if (PROTOCOL_XMLDAAP_NAME.equals(protocolName)) {
			return XMLDAAP;
		} else {
			return RSP;
		}
	}

	public String toString() {
		return protocolName;
	}
}