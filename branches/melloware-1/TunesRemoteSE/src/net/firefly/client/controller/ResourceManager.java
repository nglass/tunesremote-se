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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;

import net.firefly.client.tools.FireflyClientException;

public class ResourceManager {

	public static String THEMES_PATH = "net/firefly/client/resources/themes/";

	public static String IMAGES_PATH = "net/firefly/client/resources/images/";

	public static String TRANSLATIONS_PATH = "net/firefly/client/resources/translations/";
	
	public static String HTML_PATH = "net/firefly/client/resources/html/";
	
	public static String LICENCE_FILE = "net/firefly/client/resources/licence/COPYING";
	
	protected static Map<Locale, Properties> translations = new HashMap<Locale, Properties>();

	protected static InputStream loadResource(String resource) throws MissingResourceException {

		Thread thread = Thread.currentThread();
		ClassLoader cLoader = thread.getContextClassLoader();
		URL url = cLoader.getResource(resource);
		if (url == null) {
			throw new MissingResourceException("Unable to find resource '" + resource + "'.", resource, resource);
		}
		try {
			InputStream is = url.openStream();
			return is;
		} catch (IOException e) {
			throw new MissingResourceException("Unable to load resource '" + resource + "' (IOException).", resource,
					resource);
		}

	}

	public static InputStream loadTheme(String themeName) throws MissingResourceException {
		InputStream is = null;
		try {
			is = loadResource(themeName);
		} catch (MissingResourceException e1) {
			try {
				is = loadResource(themeName + ".zip");
			} catch (MissingResourceException e2) {
				try {
					is = loadResource(THEMES_PATH + themeName);
				} catch (MissingResourceException e3) {
					is = loadResource(THEMES_PATH + themeName + ".zip");
				}
			}
		}
		return is;
	}
	
	public static String loadHtml(String resourceName) throws MissingResourceException, FireflyClientException {
		InputStream is = null;
		try {
			is = loadResource(resourceName);
		} catch (MissingResourceException e1) {
			try {
				is = loadResource(resourceName + ".html");
			} catch (MissingResourceException e2) {
				try {
					is = loadResource(HTML_PATH + resourceName);
				} catch (MissingResourceException e3) {
					is = loadResource(HTML_PATH + resourceName + ".html");
				}
			}
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int c;
		BufferedInputStream bis = new BufferedInputStream(is);
		try {
			while ((c=bis.read()) != -1){
				baos.write(c);
			}
		} catch (IOException e) {
			throw new FireflyClientException(e, "Error while loading HTML resource '" + resourceName + "' (IOException).");
		}
		return new String(baos.toByteArray());
	}
	
	public static String getLicence() throws MissingResourceException, FireflyClientException {
		InputStream is = null;
		try {
			is = loadResource(LICENCE_FILE);
		} catch (MissingResourceException e1) {
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int c;
		if (is != null){
			BufferedInputStream bis = new BufferedInputStream(is);
			try {
				while ((c=bis.read()) != -1){
					baos.write(c);
				}
			} catch (IOException e) {
				throw new FireflyClientException(e, "Error while loading licence (IOException).");
			}
		}
		return new String(baos.toByteArray());
	}

	public static InputStream loadImage(String imageName) throws MissingResourceException {
		InputStream is = null;
		try {
			is = loadResource(imageName);
		} catch (MissingResourceException e1) {
			is = loadResource(IMAGES_PATH + imageName);
		}
		return is;
	}

	public static Properties loadProperties(String baseName, Locale locale) throws MissingResourceException {
		Properties prop = new Properties();
		String resourceName = baseName;
		if (locale != null) {
			if (locale.getLanguage() != null) {
				resourceName += "_" + locale.getLanguage();
			}
			if (locale.getCountry() != null) {
				resourceName += "_" + locale.getCountry();
			}
		}
		
		resourceName += ".properties";
		
		InputStream is = null;
		try {
			is = loadResource(resourceName);
		} catch (MissingResourceException e1) {
			is = loadResource(TRANSLATIONS_PATH + resourceName);
		}
		try {
			prop.load(is);
		} catch (IOException e) {
			throw new MissingResourceException("Unable to load resource '" + resourceName + "' (IOException).",
					resourceName, resourceName);
		}

		return prop;
	}

	public static URL getResourceURL(String resource) throws MissingResourceException {
		Thread thread = Thread.currentThread();
		ClassLoader cLoader = thread.getContextClassLoader();
		URL url = cLoader.getResource(resource);
		if (url == null) {
			throw new MissingResourceException("Unable to find resource '" + resource + "'.", resource, resource);
		}
		return url;
	}
	
	public static URI getResourceURI(String resource) throws MissingResourceException {
		URL url = getResourceURL(resource);
		
		URI uri;
		try {
			uri = new URI(url.toString());
		} catch (URISyntaxException e) {
			throw new MissingResourceException("Unable to find resource '" + resource + "'.", resource, resource);
		}
		return uri;
	}

	public static String getLabel(String labelId, Locale locale, String[] params){
		Properties props = (Properties)translations.get(locale);
		if (props == null){
			props = loadProperties("translations", locale);
			translations.put(locale, props);
		}
		String s = props.getProperty(labelId);
		if (s == null){
			return labelId;
		}
		if (params != null){
			for (int i=0; i<params.length; i++){
				String replacement = params[i];
				if (replacement == null){
					replacement = "";
				}
				replacement = replacement.replaceAll("\\\\", "\\\\\\\\"); // - replace \ by \\
				s = s.replaceAll("\\{" + (i+1) + "}", replacement);
			}
		}
		return s;
	}
	
	public static String getLabel(String labelId, Locale locale){
		return getLabel(labelId, locale, null);
	}
}