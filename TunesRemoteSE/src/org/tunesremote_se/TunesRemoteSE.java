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

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.libtunesremote_se.LibrarySelector;
import org.libtunesremote_se.TunesService;

import net.firefly.client.Version;
import net.firefly.client.controller.ConfigurationManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.swing.dialog.AboutDialog;
import net.firefly.client.gui.swing.dialog.ConfigLocationDialog;
//import net.firefly.client.gui.swing.other.SplashScreen;
import net.firefly.client.model.configuration.Configuration;
import net.firefly.client.tools.FireflyClientException;

import java.lang.reflect.*;
import java.awt.Image;
import java.awt.Toolkit;

import apple.dts.samplecode.osxadapter.OSXAdapter;

public class TunesRemoteSE {
	
	private static String configRootDirectory;
	
   public static boolean MAC_OS_X = (System.getProperty("os.name").toLowerCase().startsWith("mac os x"));
	
   protected AboutDialog aboutDialog = null;
  
   Configuration config = null;
    
   Image icon;
    
	public static void main (String [] args) throws ClassNotFoundException {
		System.out.println("Launching " + Version.getLongApplicationName() + " ...");

		for (int i = 0; i < args.length - 1; i+=2) {
		   String arg   = args[i];
		   String value = args[i+1];
		   
		   if (arg.equals("-loglevel")) {
		      try {
		         int level = Integer.parseInt(value);
		         System.out.println("Setting -loglevel to " + value);
		         android.util.Log.setLogLevel(level); 
		      } catch (NumberFormatException e) {
		         System.err.println("Illegal value for -loglevel : " + value);
		      }
		   }
		}
		
		System.setProperty("http.agent", TunesRemoteSE.class.getSimpleName());
		
	   UIManager.put("Tree.expandedIcon", 
	         new ImageIcon(TunesRemoteSE.class.getResource("/net/firefly/client/resources/images/tree-down.png")));
		
	   UIManager.put("Tree.collapsedIcon", 
            new ImageIcon(TunesRemoteSE.class.getResource("/net/firefly/client/resources/images/tree-right.png")));

	   try {
	      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	   } catch (Exception ex) {
	      System.err.println("Look and Feel Error : " + ex.getMessage());
	   }
	   
		if (MAC_OS_X) {
			// ensure property is set to place menus at top of screen
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "TunesRemote SE");
			System.setProperty("apple.laf.useScreenMenuBar", "true");	
		}
		
		new TunesRemoteSE();
	}
    
	private TunesRemoteSE() {
		
		icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/net/firefly/client/resources/images/app.png"));

		// try setting dock icon on mac
		if (MAC_OS_X) {
			try {
				Class<?> applicationClass = Class.forName("com.apple.eawt.Application");
				Object macOSXApplication = applicationClass.getConstructor((Class[])null).newInstance((Object[])null);
				Method setDockIconImage = applicationClass.getDeclaredMethod("setDockIconImage", java.awt.Image.class);
				setDockIconImage.invoke(macOSXApplication, icon);
			} catch (ClassNotFoundException cnfe) {
				System.err.println("This version of Mac OS X does not support the Apple EAWT. (" + cnfe + ")");
			} catch (Exception ex) {  // Likely a NoSuchMethodException or an IllegalAccessException loading/invoking eawt.Application methods
				System.err.println("Mac OS X Adapter could not talk to EAWT:");
				ex.printStackTrace();
			}
		}
		
		//final SplashScreen splashScreen = new SplashScreen();
		//splashScreen.splash();
		
		// Set up config directory
		ConfigurationManager.loadDefaultConfiguration();
		final boolean configAlreadyExists;
		if (ConfigurationManager.isConfigurationDirectoryValid(ConfigurationManager.getConfigRootDirectoryApp())){
			configRootDirectory = ConfigurationManager.getConfigRootDirectoryApp();
			configAlreadyExists = true;
		} else if (ConfigurationManager.isConfigurationDirectoryValid(ConfigurationManager.getConfigRootDirectoryUser())){
			configRootDirectory = ConfigurationManager.getConfigRootDirectoryUser();
			configAlreadyExists = true;
		} else {
			configAlreadyExists = false;
			Context temporaryContext = new Context(Configuration.getInstance());	
			ConfigLocationDialog cld = new ConfigLocationDialog(temporaryContext, null);
			cld.setVisible(true);
			configRootDirectory = cld.getConfigRootDirectory();
			if (configRootDirectory == null){
				System.exit(0);
			}
		}	

		boolean configValid = ConfigurationManager.isConfigurationDirectoryValid(configRootDirectory);
		if (!configValid) {
			try {
				ConfigurationManager.createConfigurationDirectory(configRootDirectory);
			} catch (FireflyClientException e) {
				e.printStackTrace();
			}
		}
		if (configAlreadyExists) {
			try {
				config = ConfigurationManager.loadSavedConfiguration(configRootDirectory);
			} catch (FireflyClientException e) {
				// throws exception if not found
				config = Configuration.getInstance();
			}
		} else {
			// - loads default config
			config = Configuration.getInstance();
		}
		config.setConfigRootDirectory(configRootDirectory);
		
		if (MAC_OS_X) {
			this.aboutDialog = new AboutDialog(config, null);
			
            try {
                // Generate and register the OSXAdapter, passing it a hash of all the methods we wish to
                // use as delegates for various com.apple.eawt.ApplicationListener methods
                OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("quit", (Class[])null));
                OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("about", (Class[])null));
                //OSXAdapter.setPreferencesHandler(this, getClass().getDeclaredMethod("preferences", (Class[])null));
            } catch (Exception e) {
                System.err.println("Error while loading the OSXAdapter:");
                e.printStackTrace();
            }		
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				TunesService.startService(configRootDirectory, Version.APPLICATION_NAME);
				
				LibrarySelector librarySelector = new LibrarySelector(new NewSessionCallback(), null);
				librarySelector.setIconImage(icon);
				librarySelector.setVisible(true);
				//splashScreen.setVisible(false);
			}
		});
	}
	
	///////////////////////////////////////////////////////////////////////////
	// Mac Callbacks
	//
    public boolean quit() {
    	
    	try {
    		if (config != null) {
    			ConfigurationManager.saveConfiguration(config);
    		}
		} catch (FireflyClientException e) {
			e.printStackTrace();
		}
		
	    System.exit(0);
	    return true;
    }
    
    public boolean about() {
    	if (this.aboutDialog != null) {
    		aboutDialog.setVisible(true);
    	}
    	return true;
    }
    
    // TODO
    public boolean preferences() {
    	return false;
    }

}
