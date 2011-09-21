package org.libtunesremote_se;

import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingUtilities;

import android.util.Log;

public class CloseOnLastWindow {
   public final static String TAG = CloseOnLastWindow.class.toString();
   
   // count open windows. exit when last is closed
	private static AtomicInteger OPEN_WINDOWS = new AtomicInteger(0);

	public static void registerWindow() {
		OPEN_WINDOWS.incrementAndGet();	
	}

	public static void unregisterWindow() {
		int windows = OPEN_WINDOWS.decrementAndGet();

		if (windows <= 0) {
			Log.i(TAG, "Last window closed exiting...");
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					System.exit(0);
				}
			});
		}	
	}
}
