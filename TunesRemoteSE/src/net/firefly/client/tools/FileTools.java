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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileTools {

	public static void move(File from, File to) throws FireflyClientException {
		boolean success = from.renameTo(to);
		if (!success) {
			// -- try to make it in two steps
			FileInputStream fis = null;
			FileOutputStream fos = null;

			try {
				fis = new FileInputStream(from);
			} catch (FileNotFoundException e) {
				throw new FireflyClientException(e, "[FileTools.move] File not found (source):" + from.getAbsolutePath());
			}
			BufferedInputStream bis = new BufferedInputStream(fis);

			try {
				fos = new FileOutputStream(to);
			} catch (FileNotFoundException e) {
				throw new FireflyClientException(e, "[FileTools.move] File not found (dest):" + to.getAbsolutePath());
			}
			BufferedOutputStream bos = new BufferedOutputStream(fos);

			int c;

			try {
				while ((c = bis.read()) != -1) {
					bos.write(c);
				}

				bos.flush();
			} catch (IOException e) {
				throw new FireflyClientException(e, "[FileTools.move] IOException while moving '" + from.getAbsolutePath() + "' to '"
						+ to.getAbsolutePath() + "'");
			} finally {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			success = from.delete();
			if (!success) {
				throw new FireflyClientException("[FileTools.move] Unable to delete source file after copy: '" + from.getAbsolutePath());
			}
		}
	}
	
	public static void copy(File from, File to) throws FireflyClientException {
		FileInputStream fis = null;
		FileOutputStream fos = null;

		try {
			fis = new FileInputStream(from);
		} catch (FileNotFoundException e) {
			throw new FireflyClientException(e, "[FileTools.copy] File not found (source):" + from.getAbsolutePath());
		}
		BufferedInputStream bis = new BufferedInputStream(fis);

		try {
			fos = new FileOutputStream(to);
		} catch (FileNotFoundException e) {
			throw new FireflyClientException(e, "[FileTools.copy] File not found (dest):" + to.getAbsolutePath());
		}
		BufferedOutputStream bos = new BufferedOutputStream(fos);

		int c;

		try {
			while ((c = bis.read()) != -1) {
				bos.write(c);
			}

			bos.flush();
		} catch (IOException e) {
			throw new FireflyClientException(e, "[FileTools.copy] IOException while copying '" + from.getAbsolutePath() + "' to '"
					+ to.getAbsolutePath() + "'");
		} finally {
			try {
				bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean deleteAllFiles(File dir) {
		if (!dir.exists()) {
			return true;
		}
		boolean res = true;
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				res &= deleteAllFiles(files[i]);
			}
			res = dir.delete();// Delete dir itself
		} else {
			res = dir.delete();
		}
		return res;
	}

}