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
package net.firefly.client.model.data;

public class Database{
	
	protected String serverVersion;
	
	protected int databaseId;
	
	protected String databaseName;
	
	protected int databaseItemsCount;
	
	public Database() {
		
	}

	/**
	 * @return Returns the databaseId.
	 */
	public int getDatabaseId() {
		return databaseId;
	}

	/**
	 * @param databaseId The databaseId to set.
	 */
	public void setDatabaseId(int databaseId) {
		this.databaseId = databaseId;
	}

	/**
	 * @return Returns the databaseItemsCount.
	 */
	public int getDatabaseItemsCount() {
		return databaseItemsCount;
	}

	/**
	 * @param databaseItemsCount The databaseItemsCount to set.
	 */
	public void setDatabaseItemsCount(int databaseItemsCount) {
		this.databaseItemsCount = databaseItemsCount;
	}

	/**
	 * @return Returns the databaseName.
	 */
	public String getDatabaseName() {
		return databaseName;
	}

	/**
	 * @param databaseName The databaseName to set.
	 */
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	
	public String getServerVersion() {
		return serverVersion;
	}

	public void setServerVersion(String serverVersion) {
		this.serverVersion = serverVersion;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("------------------------ database ------------------------\n");
		sb.append("database-id         : ").append(this.databaseId).append("\n");
		sb.append("database-name       : ").append(this.databaseName).append("\n");
		sb.append("database-items-count: ").append(this.databaseItemsCount).append("\n");
		sb.append("----------------------------------------------------------\n");
		return sb.toString();
	}
}