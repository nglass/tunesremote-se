package org.libtunesremote_se;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;

public class PairingDatabase {

	private static final String DB_NAME = "pairing.db";
	
	private final static String TABLE_PAIR = "pairing";
	private final static String FIELD_PAIR_SERVICENAME = "servicename";
	private final static String FIELD_PAIR_GUID = "guid";
	
	private String configDirectory = "";
	
	private Connection connection = null;
	private Statement statement = null;
	
	private void initDBConnection()
	{
		if (connection == null)
		{
			try {
				Class.forName("org.sqlite.JDBC");

				// create a database connection
				try {
					this.connection = DriverManager.getConnection("jdbc:sqlite:" + configDirectory + DB_NAME);
					
					this.statement = connection.createStatement();
					this.statement.setQueryTimeout(30);  // set timeout to 30 sec.
					
					this.statement.executeUpdate
						("create table if not exists " +
								TABLE_PAIR + "(" +
								FIELD_PAIR_SERVICENAME + " text primary key, " +
								FIELD_PAIR_GUID + " text)");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				// sqlite distributed with app.  real problems if we get here so exit
				System.exit(1);
			}
		}
	}
	
	public PairingDatabase(String configDirectory) {
		if (configDirectory != null) {
			this.configDirectory = new String(configDirectory + File.separator);
		}
	}
	
	public String findCode(String serviceName) {
		initDBConnection();
		
		String result = null;
		
		if (this.statement != null && serviceName != null) {		
			try {
				ResultSet rs = this.statement.executeQuery
					("select " + FIELD_PAIR_GUID + " from " + TABLE_PAIR + " where " +
							FIELD_PAIR_SERVICENAME + " = '" + serviceName + "';");
				
				if(rs.next()) {
					result = rs.getString(FIELD_PAIR_GUID);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public void updateCode(String serviceName, String guid) {
		initDBConnection();
		
		if (this.statement != null && serviceName != null && guid != null) {
			try {
				statement.executeUpdate
					("insert or replace into " + TABLE_PAIR + 
					 " values ('" + serviceName + "', '" + guid + "');");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}
	
}
