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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SkipControlCharacterFilterInputStream extends FilterInputStream {

	private final static int SPACE = 0x20;

	private final static int LF = 0xA;

	private final static int CR = 0xD;

	public SkipControlCharacterFilterInputStream(InputStream is) {
		super(is);
	}

	public int read() throws IOException {
		int c = in.read();
		if (c < SPACE) {
			if (c != LF && c != CR && c != -1) {
				return read();
			}
		}
		return c;
	}

	public int read(byte[] b, int off, int len) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		} else if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return 0;
		}

		int c = read();
		if (c == -1) {
			return -1;
		}
		b[off] = (byte) c;

		int i = 1;
		try {
			for (; i < len; i++) {
				c = read();
				if (c == -1) {
					break;
				}
				if (b != null) {
					b[off + i] = (byte) c;
				}
			}
		} catch (IOException ee) {
		}
		return i;
	}
}
