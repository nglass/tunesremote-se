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
package net.firefly.client.model.library;

public class LibraryInfo {

	// -- CONSTANT(S)

	public static final int DEFAULT_DAAP_PORT = 3689;

	// -- ATTRIBUTE(S)
	protected String libraryId;

	protected String libraryName;

	protected String host;

	protected int port;

	protected boolean authenticationNeeded = false;

	protected String password;

	protected int songCount;

	// -- CONSTRUCTOR(S)
	public LibraryInfo() {
		authenticationNeeded = false;
	}

	// -- METHOD(S)

	/**
	 * @return Returns the password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return Returns the songCount.
	 */
	public int getSongCount() {
		return songCount;
	}

	/**
	 * @param songCount
	 *            The songCount to set.
	 */
	public void setSongCount(int songCount) {
		this.songCount = songCount;
	}

	/**
	 * @return Returns the host.
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host
	 *            The host to set.
	 */
	public void setHost(String host) {
		this.host = host;
	}

	public String getLibraryId() {
		return libraryId;
	}

	public void setLibraryId(String libraryId) {
		this.libraryId = libraryId;
	}

	/**
	 * @return Returns the port.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            The port to set.
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return Returns the authenticationNeeded.
	 */
	public boolean isAuthenticationNeeded() {
		return authenticationNeeded;
	}

	/**
	 * @param authenticationNeeded
	 *            The authenticationNeeded to set.
	 */
	public void setAuthenticationNeeded(boolean authenticationNeeded) {
		this.authenticationNeeded = authenticationNeeded;
	}

	public String getLibraryName() {
		return libraryName;
	}

	public void setLibraryName(String libraryName) {
		this.libraryName = libraryName;
	}

}