package org.libtunesremote_se;

import java.io.Closeable;
import java.io.IOException;
import java.util.Hashtable;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import javax.swing.DefaultListModel;

public class TunesService extends Thread implements ServiceListener, Closeable {

	public final static String TOUCH_ABLE_TYPE = "_touch-able._tcp.local.";
	public final static String DACP_TYPE = "_dacp._tcp.local.";
	public final static String REMOTE_TYPE = "_touch-remote._tcp.local.";

	private String applicationName;
	private ServiceInfo pairservice;
	private volatile PairingServer pairingServer;

	private JmDNS zeroConf = null;

	private DefaultListModel serviceList = new DefaultListModel();

	private static TunesService instance = null;
	
	private static String configDirectory = null;
	
	private TunesService(String applicationName) {
		this.applicationName = applicationName;
	}

	@Override
	public void run() {
		try {
			zeroConf = JmDNS.create();
			zeroConf.addServiceListener(TOUCH_ABLE_TYPE, this);
			zeroConf.addServiceListener(DACP_TYPE, this);

			// Start the pairing server
			pairingServer = new PairingServer(configDirectory);
			pairingServer.start();
			
			// Register the Pairing Service
			final Hashtable<String, String> values = new Hashtable<String, String>();
			values.put("DvNm", applicationName);
			values.put("RemV", "10000");
			values.put("DvTy", "iPod");
			values.put("RemN", "Remote");
			values.put("txtvers", "1");
			values.put("Pair", pairingServer.getPairCode());

			// NOTE: this "Pair" above is *not* the guid--we generate and return that in PairingServer
			pairservice = ServiceInfo.create(REMOTE_TYPE, pairingServer.getServiceGuid(), pairingServer.getPortNumber(), 0, 0, values);

			zeroConf.registerService(pairservice);

		} catch ( java.io.IOException e) {
		}
	}
	
	public void close() {
		try {
			if (zeroConf != null) {
				zeroConf.unregisterAllServices();
				zeroConf.close();

				pairingServer.destroy();
			}
		} catch (IOException e) {
			e.printStackTrace();
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
		System.out.println("serviceAdded(event=" + event.toString() + ")");
	
		// Force resolution of new service
		ServiceInfo info = event.getDNS().getServiceInfo(event.getType(), event.getName());
		updateService(event.getName(), info);
	}

	public void serviceRemoved(ServiceEvent event) {
		//Log.w(TAG, String.format("serviceRemoved(event=\n%s\n)", event.toString()));
		System.out.println("serviceRemoved(event=" + event.toString() + ")");

		// remove entry
		final String serviceName = event.getName();
		final LibraryDetails ent = new LibraryDetails(null, serviceName, null, null, 0);
		serviceList.removeElement(ent);
	}

	public void serviceResolved(ServiceEvent event) {
		System.out.println("serviceResolved(event=" + event.toString() + ")");

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
					System.out.println("TunesService Stopped");
				}
			});
		}
	}
	
	public static String getConfigDirectory() {
		return TunesService.configDirectory;
	}
	
	public static JmDNS getZeroConf() {
		if (instance != null) {
			return instance.zeroConf;
		}
		return null;
	}
	
	public static DefaultListModel getServiceList() {
		if (instance != null) {
			return instance.serviceList;
		}
		return new DefaultListModel();
	}
}
