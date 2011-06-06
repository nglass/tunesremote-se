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
package net.firefly.client.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.firefly.client.controller.listeners.SongListLoadProgressListener;
import net.firefly.client.controller.xml.LibraryInfoXMLManager;
import net.firefly.client.model.configuration.Configuration;
import net.firefly.client.model.library.Library;
import net.firefly.client.model.library.LibraryInfo;
import net.firefly.client.tools.DirectoryFileFilter;
import net.firefly.client.tools.FileTools;
import net.firefly.client.tools.FireflyClientException;

public class LibraryManager {

	public static Library loadSavedLibrary(String configRootDir, String libraryId) throws FireflyClientException {
		return loadSavedLibrary(configRootDir, libraryId, null);
	}

	public static Library loadSavedLibrary(String configRootDir, String libraryId, SongListLoadProgressListener listener)
			throws FireflyClientException {
		Library library = new Library();

		LibraryInfo libraryInfo = loadSavedLibraryInfos(configRootDir, libraryId);
		library.setLibraryInfo(libraryInfo);

		return library;
	}

	public static void saveLibrary(String configRootDir, Library library, boolean overwrite) throws FireflyClientException {
		saveLibraryInfos(configRootDir, library.getLibraryInfo(), overwrite);
	}

	public static LibraryInfo loadSavedLibraryInfos(String configRootDir, String libraryId) throws FireflyClientException {
		// -- check directory existency
		File configRootDirectory = new File(configRootDir);
		File librariesDirectory = new File(configRootDirectory, Configuration.CONFIG_LIBRARIES_SUBDIRECTORY);
		File libraryDir = new File(librariesDirectory, libraryId);
		if (!libraryDir.exists()) {
			throw new FireflyClientException("Unable to load library infos for library '" + libraryId
					+ "'. Directory does not exist (" + libraryDir.getAbsolutePath() + ")");
		}
		if (!libraryDir.canRead()) {
			throw new FireflyClientException("Unable to load library infos for library '" + libraryId
					+ "'. Directory cannot be read (" + libraryDir.getAbsolutePath() + ")");
		}

		// -- check directory consistence
		File libraryInfoFile = new File(libraryDir, Configuration.CONFIG_LIBRARY_INFO_FILENAME);
		if (!libraryInfoFile.exists() || !libraryInfoFile.canRead()) {
			throw new FireflyClientException("Unable to load library for library '" + libraryId + "'. Info file ("
					+ libraryInfoFile.getAbsolutePath() + ") does not exist or cannot be read.");
		}

		// -- load object from file

		// -- library info
		LibraryInfoXMLManager libraryInfoXMLManager = LibraryInfoXMLManager.getInstance();
		FileInputStream libraryInfoInputStream = null;

		try {
			libraryInfoInputStream = new FileInputStream(libraryInfoFile);
		} catch (FileNotFoundException e) {
			// -- should not happen after previous controls
			throw new FireflyClientException("Unable to load library for library '" + libraryId + "'. Info file ("
					+ libraryInfoFile.getAbsolutePath() + ") does not exist or cannot be read.");
		}

		BufferedInputStream libraryInfoBufferedInputStream = new BufferedInputStream(libraryInfoInputStream);

		LibraryInfo libraryInfo = null;
		try {
			libraryInfo = libraryInfoXMLManager.unmarshal(libraryInfoBufferedInputStream);
		} finally {
			try {
				libraryInfoBufferedInputStream.close();
			} catch (IOException e) {
				throw new FireflyClientException(e, "IOException while unmarshalling the library info.");
			}
		}

		return libraryInfo;
	}

	public static void saveLibraryInfos(String configRootDir, LibraryInfo libraryInfo, boolean overwrite) throws FireflyClientException {
		// -- check directory existency
		if (!ConfigurationManager.isConfigurationDirectoryValid(configRootDir)) {
			ConfigurationManager.createConfigurationDirectory(configRootDir);
		}

		File configRootDirectory = new File(configRootDir);
		File librariesDirectory = new File(configRootDirectory, Configuration.CONFIG_LIBRARIES_SUBDIRECTORY);
		File libraryDir = new File(librariesDirectory, libraryInfo.getLibraryId());

		if (!libraryDir.exists()) {
			boolean success = libraryDir.mkdirs();
			if (!success) {
				throw new FireflyClientException("Unable to create library directory for '" + libraryInfo.getLibraryId()
						+ "' (" + libraryDir.getAbsolutePath() + ".");
			}
		}
		if (!libraryDir.canWrite()) {
			throw new FireflyClientException("Unable to save library infos for '" + libraryInfo.getLibraryId()
					+ "'. Directory cannot be written (" + libraryDir.getAbsolutePath() + ")");
		}

		// -- marshal to XML
		File libraryInfoFile = new File(libraryDir, Configuration.CONFIG_LIBRARY_INFO_FILENAME);

		// -- library info
		LibraryInfoXMLManager libraryInfoXMLManager = LibraryInfoXMLManager.getInstance();
		FileOutputStream libraryInfoOutputStream = null;

		try {
			libraryInfoOutputStream = new FileOutputStream(libraryInfoFile);
		} catch (FileNotFoundException e) {
			throw new FireflyClientException("Unable to save library infos for '" + libraryInfo.getLibraryId() + "'. "
					+ ((e.getMessage() != null) ? e.getMessage() : ""));
		}

		BufferedOutputStream libraryInfoBufferedOutputStream = new BufferedOutputStream(libraryInfoOutputStream);

		try {
			libraryInfoXMLManager.marshal(libraryInfo, libraryInfoBufferedOutputStream, true);
		} finally {
			try {
				libraryInfoOutputStream.close();
			} catch (IOException e) {
				throw new FireflyClientException("Unable to save library '" + libraryInfo.getLibraryId() + "'. "
						+ ((e.getMessage() != null) ? e.getMessage() : ""));
			}
		}
	}

	public static void deleteLibrary(String configRootDir, String libraryId) throws FireflyClientException {
		// -- check directory existency
		if (!ConfigurationManager.isConfigurationDirectoryValid(configRootDir)) {
			ConfigurationManager.createConfigurationDirectory(configRootDir);
		}

		File configRootDirectory = new File(configRootDir);
		File librariesDirectory = new File(configRootDirectory, Configuration.CONFIG_LIBRARIES_SUBDIRECTORY);
		File libraryDir = new File(librariesDirectory, libraryId);

		boolean success = FileTools.deleteAllFiles(libraryDir);
		if (!success) {
			throw new FireflyClientException("Unable to delete library '" + libraryId + "'.");
		}
	}

	public static String[] listSavedLibraries(String configRootDir) {
		File configRootDirectory = new File(configRootDir);
		File librariesDirectory = new File(configRootDirectory, Configuration.CONFIG_LIBRARIES_SUBDIRECTORY);
		if (librariesDirectory.exists() && librariesDirectory.canRead()) {
			File[] libraries = librariesDirectory.listFiles(new DirectoryFileFilter());
			String[] result = new String[libraries.length];
			for (int i = 0; i < libraries.length; i++) {
				result[i] = libraries[i].getName();
			}
			return result;
		}
		return new String[0];
	}

	public static boolean libraryExists(String configRootDir, String libraryId) {
		if (libraryId == null || libraryId.trim().length() == 0) {
			return false;
		}
		File configRootDirectory = new File(configRootDir);
		File librariesDirectory = new File(configRootDirectory, Configuration.CONFIG_LIBRARIES_SUBDIRECTORY);
		File libraryDir = new File(librariesDirectory, libraryId);
		return libraryDir.exists();
	}
	
	public static String[] searchSavedLibraries(String configRootDir, String host, int port) {
		String[] savedLibraires = listSavedLibraries(configRootDir);
		List<String> result = new ArrayList<String>();
		for (int i=0; i<savedLibraires.length; i++){
			try {
				LibraryInfo li = loadSavedLibraryInfos(configRootDir, savedLibraires[i]);
				if ((
						li.getHost().equalsIgnoreCase(host) ||
						(li.getHost().equalsIgnoreCase("localhost") && "127.0.0.1".equals(host)) ||
						(li.getHost().equalsIgnoreCase("127.0.0.1") && "localhost".equalsIgnoreCase(host))
					) 
						&& li.getPort() == port){
					result.add(li.getLibraryId());
				}
			} catch (FireflyClientException e){
				continue;
			}
		}
		return (String[])result.toArray(new String[result.size()]);
	}

	public static boolean alreadySavedLibrary(String configRootDir, String libraryId) {
		File configRootDirectory = new File(configRootDir);
		File librariesDirectory = new File(configRootDirectory, Configuration.CONFIG_LIBRARIES_SUBDIRECTORY);
		File library = new File(librariesDirectory, libraryId);
		if (library.exists() && library.isDirectory() && library.canRead()) {
			return true;
		}
		return false;
	}
}