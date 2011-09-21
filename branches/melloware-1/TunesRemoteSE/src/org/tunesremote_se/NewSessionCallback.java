/*
 * This file is part of TunesRemote SE.
 *
 * TunesRemote SE is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * any later version.
 *
 * TunesRemote SE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TunesRemote SE; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright 2011 Nick Glass
 */
package org.tunesremote_se;

import org.libtunesremote_se.LibraryDetails;
import org.libtunesremote_se.TunesRemoteSessionCallback;
import org.tunesremote.daap.Session;

public class NewSessionCallback implements TunesRemoteSessionCallback {
	public void newSession(LibraryDetails l, Session s) {
		new TunesRemoteSession(l,s);
	}
}
