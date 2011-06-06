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

public class FireflyClientException extends Exception {

	private static final long serialVersionUID = -5067275752600713942L;

	protected String message;
	
	protected Throwable t;
	
	public FireflyClientException(String message){
		this.message = message;
	}
	
	public FireflyClientException(Throwable t){
		this.t = t;
	}
	
	public FireflyClientException(Throwable t, String message){
		this.t = t;
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public void printStackTrace(){
		System.err.println(this.getClass());
		if (message != null){
			System.err.println("\t" + message);
		}
		if (t != null){
			t.printStackTrace();
			Throwable cause = t;
			while ((cause = cause.getCause()) != null){
				cause.printStackTrace();
			}
		} 
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("[FireflyClientException] ");
		if (t != null){
			sb.append(t.getClass());
		}
		if (message != null){
			sb.append(" - ").append(message);
		}
		return sb.toString();
	}
}
