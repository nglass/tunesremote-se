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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import net.firefly.client.controller.xml.ConfigurationXMLManager;
import net.firefly.client.model.configuration.Configuration;
import net.firefly.client.tools.FireflyClientException;

public class ConfigurationManager {

	public static boolean isConfigurationDirectoryValid(String configRootDir) {
		File configRootDirectory = new File(configRootDir);
		if (configRootDirectory.exists()) {
			if (configRootDirectory.canRead() && configRootDirectory.canWrite()) {
				File librariesDirectory = new File(configRootDirectory,
						Configuration.CONFIG_LIBRARIES_SUBDIRECTORY);
				if (librariesDirectory.exists()) {
					if (librariesDirectory.canRead()
							&& librariesDirectory.canWrite()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static void createConfigurationDirectory(String configRootDir)
			throws FireflyClientException {
		File configRootDirectory = new File(configRootDir);
		File librariesDirectory = new File(configRootDir,
				Configuration.CONFIG_LIBRARIES_SUBDIRECTORY);
		boolean success;
		if (!configRootDirectory.exists()) {
			success = configRootDirectory.mkdir();
			if (!success) {
				throw new FireflyClientException(
						"Unable to create configuration root directory : "
								+ configRootDirectory.getAbsolutePath());
			}
		}
		if (!librariesDirectory.exists()) {
			success = librariesDirectory.mkdir();
			if (!success) {
				throw new FireflyClientException(
						"Unable to create configuration libraries directory : "
								+ librariesDirectory.getAbsolutePath());
			}
		}
	}

	public static Configuration loadSavedConfiguration(String configRootDir)
			throws FireflyClientException {
		File configurationFile = new File(configRootDir,Configuration.CONFIG_FILENAME);
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(configurationFile);
		} catch (FileNotFoundException e) {
			throw new FireflyClientException(e,
					"Cannot read configuration file: "
							+ configurationFile.getAbsolutePath());
		}

		BufferedInputStream bis = new BufferedInputStream(fis);

		ConfigurationXMLManager configXMLManager = ConfigurationXMLManager
				.getInstance();

		Configuration config = null;

		try {
			config = configXMLManager.unmarshal(bis);
		} finally {
			try {
				bis.close();
			} catch (IOException e) {
				throw new FireflyClientException(e,
						"IOException while unmarshalling the configuration.");
			}
		}
		return config;
	}

	public static Configuration loadDefaultConfiguration() {
		return Configuration.getInstance();
	}

	public static void saveConfiguration(Configuration config)
			throws FireflyClientException {
		File configurationFile = new File(config.getConfigRootDirectory(),
				Configuration.CONFIG_FILENAME);
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(configurationFile);
		} catch (FileNotFoundException e) {
			throw new FireflyClientException(e,
					"Cannot create configuration file: "
							+ configurationFile.getAbsolutePath());
		}

		ConfigurationXMLManager configXMLManager = ConfigurationXMLManager
				.getInstance();

		BufferedOutputStream bos = new BufferedOutputStream(fos);

		try {
			configXMLManager.marshal(config, bos);
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
				throw new FireflyClientException(e,
						"IOException while writing configuration file: "
								+ configurationFile.getAbsolutePath());
			}
		}
	}

	public static String getConfigRootDirectoryUser() {
		return System.getProperty("user.home") + File.separatorChar
				+ Configuration.CONFIG_DIRECTORY;
	}

	public static String getConfigRootDirectoryApp() {
		try {
			URL url = ConfigurationManager.class.getProtectionDomain().getCodeSource()
					.getLocation();
			URI uri = url.toURI();
			File f = new File(uri);
			if (!f.isDirectory()){
				// -- might be the jar, so get the containing directory
				f = f.getParentFile();
			}
			return f.getAbsolutePath() + File.separatorChar
					+ Configuration.CONFIG_DIRECTORY;
		} catch (URISyntaxException e) {
			return getConfigRootDirectoryUser();
		}

	}

}