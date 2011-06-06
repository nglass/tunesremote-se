package org.tunesremote_se;

import org.libtunesremote_se.LibraryDetails;
import org.libtunesremote_se.TunesRemoteSessionCallback;
import org.tunesremote.daap.Session;

public class NewSessionCallback implements TunesRemoteSessionCallback {
	public void newSession(LibraryDetails l, Session s) {
		new TunesRemoteSession(l,s);
	}
}
