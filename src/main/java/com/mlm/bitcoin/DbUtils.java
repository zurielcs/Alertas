package com.mlm.bitcoin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtils {
	public static Connection getConnection(){
		String url;
		if (System.getProperty("com.google.appengine.runtime.version").startsWith("Google App Engine/")) {
			url = System.getProperty("ae-cloudsql.cloudsql-database-url");
			try {
				// Load the class that provides the new "jdbc:google:mysql://"
				// prefix.
				Class.forName("com.mysql.jdbc.GoogleDriver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			// Set the url with the local MySQL database connection url when
			// running locally
			url = System.getProperty("ae-cloudsql.local-database-url");
		}
		Connection conn;
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return conn;
	}
}
