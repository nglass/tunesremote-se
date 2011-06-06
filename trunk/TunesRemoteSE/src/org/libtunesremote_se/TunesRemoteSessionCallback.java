package org.libtunesremote_se;

import org.tunesremote.daap.Session;

public interface TunesRemoteSessionCallback {

	public void newSession(LibraryDetails l, Session s);
	
}
