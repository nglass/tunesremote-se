package org.libtunesremote_se;

/*
    TunesRemote+ - http://code.google.com/p/tunesremote-plus/

    Copyright (C) 2008 Jeffrey Sharkey, http://jsharkey.org/
    Copyright (C) 2010 TunesRemote+, http://code.google.com/p/tunesremote-plus/

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    The Initial Developer of the Original Code is Jeffrey Sharkey.
    Portions created by Jeffrey Sharkey are
    Copyright (C) 2008. Jeffrey Sharkey, http://jsharkey.org/
    All Rights Reserved.
 */

//package org.tunesremote.daap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import android.util.Log;

public class PairingServer extends Thread {

	// the pairing service waits for any incoming requests from itunes
	// it always returns a valid pairing code
	public final static String TAG = PairingServer.class.toString();
	// public final static int PORT = 1024;

	protected final static byte[] CHAR_TABLE = new byte[] { (byte) '0',
		(byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5',
		(byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) 'A',
		(byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F' };

	public static byte[] PAIRING_RAW = new byte[] { 0x63, 0x6d, 0x70, 0x61,
		0x00, 0x00, 0x00, 0x3a, 0x63, 0x6d, 0x70, 0x67, 0x00, 0x00, 0x00,
		0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x63, 0x6d,
		0x6e, 0x6d, 0x00, 0x00, 0x00, 0x16, 0x41, 0x64, 0x6d, 0x69, 0x6e,
		0x69, 0x73, 0x74, 0x72, 0x61, 0x74, 0x6f, 0x72, (byte) 0xe2,
		(byte) 0x80, (byte) 0x99, 0x73, 0x20, 0x69, 0x50, 0x6f, 0x64, 0x63,
		0x6d, 0x74, 0x79, 0x00, 0x00, 0x00, 0x04, 0x69, 0x50, 0x6f, 0x64 };

	protected ServerSocket server;
	protected final Random random = new Random();
	protected int portNumber = 0;
	protected String pairCode;
	protected String serviceGuid;

	private PairingDatabase pairingDatabase; 

	public int getPortNumber() {
		return portNumber;
	}
	
	// this code is used in validating pair codes by hashing with response
	public String getPairCode() {
		return pairCode;
	}
	
	// this is the GUID that uniquely identifies this remote
	public String getServiceGuid() {
		return serviceGuid;
	}

	public PairingServer(String configDirectory) {
		pairingDatabase = new PairingDatabase(configDirectory);
		
		pairCode = pairingDatabase.getPairCode();
		serviceGuid = pairingDatabase.getServiceGuid();
		
		try {
			// open a free port
			this.server = new ServerSocket(0);
			this.portNumber = this.server.getLocalPort();
		} catch (IOException e) {
			Log.w(TAG, e);
		} 
	}

	@Override
	public void destroy() {
		Log.d(TAG, "Destroying PairingServer " + this.portNumber);
		try {
			if ((this.server != null) && (!this.server.isClosed())) {
				Log.i(TAG, "Destroying Socket " + this.portNumber);
				this.server.close();
				this.server = null;
			}
			this.interrupt();
		} catch (IOException e) {
			Log.w(TAG, e);
		}
	}

	@Override
	public void run() {
		// start listening on a specific port for any requests
		Log.i(TAG, "Pairing Server Listening on Port " + this.portNumber);

		Thread thisThread = Thread.currentThread();

		while (this == thisThread && server != null) {
			Log.i(TAG, "awaiting connection....");
			try {
				// start accepting data on incoming socket
				final Socket socket = server.accept();
				final String address = socket.getInetAddress().getHostAddress();

				Log.i(TAG, "accepted connection from " + address + "...");

				// we dont care about checking the incoming pairing md5 from
				// itunes
				// and we always just accept the pairing
				OutputStream output = null;

				try {
					String serviceName = null;
					output = socket.getOutputStream();

					// output the contents for debugging
					final BufferedReader br = new BufferedReader(
							new InputStreamReader(socket.getInputStream()));
					while (br.ready()) {
						String line = br.readLine();
						if (line.contains("servicename=")) {
							int index = line.indexOf("servicename=");
							serviceName = line.substring(index + 12,
									index + 12 + 16);
						}

						Log.d(TAG, line);
					}

					// edit our local PAIRING_RAW to return the correct guid
					byte[] code = new byte[8];
					random.nextBytes(code);
					System.arraycopy(code, 0, PAIRING_RAW, 16, 8);
					final String niceCode = toHex(code);

					byte[] header = String.format(
							"HTTP/1.1 200 OK\r\nContent-Length: %d\r\n\r\n",
							PAIRING_RAW.length).getBytes();
					byte[] reply = new byte[header.length + PAIRING_RAW.length];

					System.arraycopy(header, 0, reply, 0, header.length);
					System.arraycopy(PAIRING_RAW, 0, reply, header.length,
							PAIRING_RAW.length);

					output.write(reply);

					Log.i(TAG, "someone paired with me!");
					if (serviceName != null) {
						// add this to the pairing db
						Log.i(TAG, "address = " + address);
						Log.i(TAG, "servicename = " + serviceName);
						Log.i(TAG, "niceCode = " + niceCode);

						pairingDatabase.updateCode(serviceName, niceCode);
					}

				} finally {
					if (output != null) {
						output.flush();
						output.close();
					}
					output = null;
				}
			} catch (java.net.SocketException e) {
				Log.i(TAG, e.getMessage());
			} catch (IOException e) {
				Log.w(TAG, e);
			} 
		}

		Log.i(TAG, "PairingServer thread stopped.");

	}

	public static String toHex(byte[] code) {
		// somewhat borrowed from rgagnon.com
		byte[] result = new byte[2 * code.length];
		int index = 0;
		for (byte b : code) {
			int v = b & 0xff;
			result[index++] = CHAR_TABLE[v >>> 4];
			result[index++] = CHAR_TABLE[v & 0xf];
		}
		return new String(result);
	}

}
