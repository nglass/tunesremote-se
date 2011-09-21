package org.libtunesremote_se;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

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

	private String applicationName;
	private ServiceInfo pairservice;
	private volatile PairingServer pairingServer;

	private JmmDNS jmmdns = null;

	private DefaultListModel serviceList = new DefaultListModel();

	private static TunesService instance = null;
	
	private static String configDirectory = null;
	
   private Hashtable<String, String> serviceValues;
   
   private Map<InetAddress, JmDNS> mdnsMap;
	
	private TunesService(String applicationName) {
		this.applicationName = applicationName;
		
		this.mdnsMap = new HashMap<InetAddress, JmDNS>();
	}

	@Override
	public void run() {
		// Start the pairing server
		pairingServer = new PairingServer(configDirectory);
		pairingServer.start();
		
		// Register the Pairing Service
		serviceValues = new Hashtable<String, String>();
		serviceValues.put("DvNm", applicationName);
		serviceValues.put("RemV", "10000");
		serviceValues.put("DvTy", "iPod");
		serviceValues.put("RemN", "Remote");
		serviceValues.put("txtvers", "1");
		serviceValues.put("Pair", pairingServer.getPairCode());

      // NOTE: this "Pair" above is *not* the guid--we generate and return that in PairingServer
      pairservice = ServiceInfo.create
         (REMOTE_TYPE, pairingServer.getServiceGuid(), pairingServer.getPortNumber(), 0, 0, serviceValues);
		
		jmmdns = JmmDNS.Factory.getInstance();
		jmmdns.addNetworkTopologyListener(this);
	}
	
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
		final String address = serviceInfo.getHostAddress();
		final String library = serviceInfo.getPropertyString("DbId");
		final int port = serviceInfo.getPort();
		
		LibraryDetails ent = new LibraryDetails
		    (libraryName, serviceName, address, library, port);

		if (serviceList.contains(ent)) {
			serviceList.setElementAt(ent, serviceList.indexOf(ent));
		} else {
			serviceList.addElement(ent);
		}
	}
	
	public void serviceAdded(ServiceEvent event) {
	   Log.i(TAG, "serviceAdded(event=" + event.toString() + ")");
	
		// Force resolution of new service
		final String serviceName = event.getName();
		final ServiceInfo info = event.getDNS().getServiceInfo(event.getType(), event.getName());
		
		SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            updateService(serviceName, info);
         }
      });
	}

	public void serviceRemoved(ServiceEvent event) {
	   Log.i(TAG, "serviceRemoved(event=" + event.toString() + ")");
		
		// remove entry
		final String serviceName = event.getName();
		final LibraryDetails ent = new LibraryDetails(null, serviceName, null, null, 0);
      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            serviceList.removeElement(ent);
         }
      });
	}

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
      
      // and register the pair service
      try {
         mdns.registerService(pairservice);
      } catch (IOException e) {
         Log.e(TAG, "Error registering service on " + event.getInetAddress(), e);
      }
      mdnsMap.put(event.getInetAddress(), mdns);
   }

   @Override
   public void inetAddressRemoved(NetworkTopologyEvent event) {
      Log.i(TAG, "inetAddressRemoved(event=" + event.toString() + ")");
      mdnsMap.remove(event.getInetAddress());
   }
}
