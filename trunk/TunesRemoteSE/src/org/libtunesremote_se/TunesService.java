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

package org.libtunesremote_se;

import java.io.Closeable;
import java.io.IOException;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jmdns.JmDNS;
import javax.jmdns.JmmDNS;
import javax.jmdns.NetworkTopologyEvent;
import javax.jmdns.NetworkTopologyListener;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;

import android.util.Log;

public class TunesService extends Thread implements ServiceListener, NetworkTopologyListener, Closeable {

   public final static String TAG = TunesService.class.toString();

   public final static String TOUCH_ABLE_TYPE = "_touch-able._tcp.local.";
   public final static String REMOTE_TYPE = "_touch-remote._tcp.local.";

   private final String applicationName;

   private volatile PairingServer pairingServer;

   private JmmDNS jmmdns = null;

   private final DefaultListModel serviceList = new DefaultListModel();

   private static TunesService instance = null;

   private static String configDirectory = null;

   private String pairCode = null;

   private AtomicInteger servicecount;
   
   private TunesService(String applicationName) {
      this.applicationName = applicationName;
      
      servicecount = new AtomicInteger(0);
   }

   @Override
   public void run() {
      // Start the pairing server
      pairingServer = new PairingServer(configDirectory);
      pairingServer.start();
      pairCode = pairingServer.getPairCode();

      jmmdns = JmmDNS.Factory.getInstance();
      jmmdns.addNetworkTopologyListener(this);
   }

   @Override
   public void close() {
      try {
         if (jmmdns != null) {
            jmmdns.unregisterAllServices();
            jmmdns.close();

            pairingServer.destroy();
         }
      } catch (IOException e) {
         Log.e(TAG, "Exception shutting down TunesService", e);
      }
   }

   public void updateService(String serviceName, ServiceInfo serviceInfo) {
      final String libraryName = serviceInfo.getPropertyString("CtlN");
      final String address = serviceInfo.getHostAddresses()[0];
      final String library = serviceInfo.getPropertyString("DbId");
      final String libraryType = serviceInfo.getPropertyString("DvTy");
      final int port = serviceInfo.getPort();

      LibraryDetails ent = new LibraryDetails(libraryName, libraryType, serviceName, address, library, port);

      if (serviceList.contains(ent)) {
         serviceList.setElementAt(ent, serviceList.indexOf(ent));
      } else {
         serviceList.addElement(ent);
      }
   }

   @Override
   public void serviceAdded(ServiceEvent event) {
      Log.i(TAG, "serviceAdded(event=" + event.toString() + ")");

      // Force resolution of new service
      final String serviceName = event.getName();
      final ServiceInfo info = event.getDNS().getServiceInfo(event.getType(), event.getName());

      if (info != null) {
         SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
               updateService(serviceName, info);
            }
         });
      }
   }

   @Override
   public void serviceRemoved(ServiceEvent event) {
      Log.i(TAG, "serviceRemoved(event=" + event.toString() + ")");

      // remove entry
      final String serviceName = event.getName();
      final LibraryDetails ent = new LibraryDetails(null, null, serviceName, null, null, 0);
      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            serviceList.removeElement(ent);
         }
      });
   }

   @Override
   public void serviceResolved(ServiceEvent event) {
      Log.i(TAG, "serviceResolved(event=" + event.toString() + ")");

      final String serviceName = event.getName();
      final ServiceInfo serviceInfo = event.getInfo();
      updateService(serviceName, serviceInfo);
   }

   public static void startService(String configDirectory, String applicationName) {
      if (instance == null) {
         TunesService.configDirectory = configDirectory;
         instance = new TunesService(applicationName);
         instance.start();

         Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
               if (instance != null) {
                  instance.close();
               }
               Log.i(TAG, "TunesService Stopped");
            }
         });
      }
   }

   public static String getConfigDirectory() {
      return TunesService.configDirectory;
   }

   public static DefaultListModel getServiceList() {
      if (instance != null) {
         return instance.serviceList;
      }
      return new DefaultListModel();
   }
   
   @Override
   public void inetAddressAdded(NetworkTopologyEvent event) {
      Log.i(TAG, "inetAddressAdded(event=" + event.toString() + ")");
      JmDNS mdns = event.getDNS();

      // Start listening for DACP servers on this interface
      mdns.addServiceListener(TOUCH_ABLE_TYPE, this);

      // and re-register the pair service
      try {
         String name = event.getInetAddress().getHostName();
         
         Hashtable<String, String> serviceValues;
         // Register the Pairing Service
         serviceValues = new Hashtable<String, String>();
         serviceValues.put("DvNm", applicationName + "@" + name);
         serviceValues.put("RemV", "10000");
         serviceValues.put("DvTy", "iPod");
         serviceValues.put("RemN", "Remote");
         serviceValues.put("txtvers", "1");
         serviceValues.put("Pair", pairCode);
         
         ServiceInfo newpairservice = ServiceInfo.create(REMOTE_TYPE, pairingServer.getServiceGuid() + servicecount.incrementAndGet(), pairingServer.getPortNumber(), 0, 0, serviceValues);
         mdns.registerService(newpairservice);
      } catch (IOException e) {
         Log.e(TAG, "Error registering service on " + event.getInetAddress(), e);
      }
   }

   @Override
   public void inetAddressRemoved(NetworkTopologyEvent event) {
      Log.i(TAG, "inetAddressRemoved(event=" + event.toString() + ")");
   }
}
