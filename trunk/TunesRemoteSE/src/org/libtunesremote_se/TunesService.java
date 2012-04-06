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
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
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

public class TunesService implements ServiceListener, NetworkTopologyListener, Closeable {

   public final static String TAG = TunesService.class.toString();

   public final static String TOUCH_ABLE_TYPE = "_touch-able._tcp.local.";
   public final static String REMOTE_TYPE = "_touch-remote._tcp.local.";

   private JmmDNS jmmdns = null;

   private final DefaultListModel serviceList = new DefaultListModel();

   private static TunesService instance = null;

   private static String applicationName;
   
   private Hashtable<String, String> serviceValues = new Hashtable<String, String>();
   
   private Map<JmDNS, InetAddress> interfaces = new HashMap<JmDNS, InetAddress>();
   
   private AtomicInteger interfaceCount = new AtomicInteger();
   private Map<InetAddress, Integer> interfaceNumber = new HashMap<InetAddress, Integer>();
   
   private String serviceGuid = null;
   
   private int portNumber = 0;
   
   protected TunesService() {
      this.jmmdns = JmmDNS.Factory.getInstance();
      jmmdns.addNetworkTopologyListener(this);
      
      serviceValues.put("DvNm", applicationName);
      serviceValues.put("RemV", "10000");
      serviceValues.put("DvTy", "iPod");
      serviceValues.put("RemN", "Remote");
      serviceValues.put("txtvers", "1");
   }
   
   public static TunesService getInstance() {
      return instance;
   }
   
   public static void startService(String applicationName) {
      if (instance == null) {
         TunesService.applicationName = applicationName;
         
         instance = new TunesService();
         
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
   
   protected void registerPairingService(JmDNS mdns, InetAddress address) {
      Log.i(TAG, "Registered Pairing Service @ " + address.getHostAddress());
      
      if (!interfaceNumber.containsKey(address)) {
         interfaceNumber.put(address, interfaceCount.getAndIncrement());
      }
      
      serviceValues.put("DvNm", applicationName + "@" + address.getHostAddress());
      ServiceInfo pairservice = ServiceInfo.create
            (REMOTE_TYPE, serviceGuid + interfaceNumber.get(address), portNumber, 0, 0, serviceValues);
   
      try {
         mdns.registerService(pairservice);
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
   
   public void registerPairingService(String serviceGuid, String pairCode, int portNumber) {
      serviceValues.put("Pair", pairCode);
      this.serviceGuid = serviceGuid;
      this.portNumber = portNumber;
      
      for (Map.Entry<JmDNS, InetAddress> entry : interfaces.entrySet()) {
         JmDNS mdns = entry.getKey();
         InetAddress address = entry.getValue();
         registerPairingService(mdns, address);
      }
   }
   
   public void unregisterPairingService() {
      serviceGuid = null;
      jmmdns.unregisterAllServices();
   }

   @Override
   public void close() {
      try {
         if (jmmdns != null) {
            jmmdns.unregisterAllServices();
            jmmdns.close();
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
      InetAddress address = event.getInetAddress();
      
      // Start listening for DACP servers on this interface
      mdns.addServiceListener(TOUCH_ABLE_TYPE, this);
      
      interfaces.put(mdns, address);

      if (serviceGuid != null) {
         registerPairingService(mdns, address);
      }
   }

   @Override
   public void inetAddressRemoved(NetworkTopologyEvent event) {
      Log.i(TAG, "inetAddressRemoved(event=" + event.toString() + ")");
      
      JmDNS mdns = event.getDNS();
      mdns.removeServiceListener(TOUCH_ABLE_TYPE, this);
      mdns.unregisterAllServices();
      
      interfaces.remove(mdns);
   }
}
