package org.libtunesremote_se;

/**
 * 
 */

public class LibraryDetails {
	private String libraryName;
	private String serviceName;
	private String address;
	private String library;
	private int port;

	public LibraryDetails
		(String libraryName, 
		 String serviceName, 
		 String address, 
		 String library, 
		 int port) {
		
		this.libraryName = libraryName;
		this.serviceName = serviceName;
		this.address = address;
		this.library = library;   
		this.port = port;
	}
	
	public String getLibraryName() {
		return libraryName;
	}
	
	public String getServiceName() {
		return serviceName;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getLibrary() {
		return library;
	}

	public int getPort() {
		return port;
	}
	
	// this is how the object will appear in the list
	@Override
	public String toString() {
		return libraryName;	
	}
	
	@Override
	public boolean equals(Object aThat){
		if ( this == aThat ) return true;
		if ( !(aThat instanceof LibraryDetails) ) return false;
		LibraryDetails that = (LibraryDetails)aThat;
		return this.serviceName.equals(that.serviceName);
	}
}