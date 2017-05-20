package com.mlm.bitcoin.commons;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;

public class DataSource {
	private static DataSource datasource;
	private BasicDataSource ds;

	private DataSource() throws IOException, SQLException, PropertyVetoException {String url;
		if (System.getProperty("com.google.appengine.runtime.version").startsWith("Google App Engine/")) {
			url = System.getProperty("ae-cloudsql.cloudsql-database-url");
			try {
				Class.forName("com.mysql.jdbc.GoogleDriver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			url = System.getProperty("ae-cloudsql.local-database-url");
		}
		ds = new BasicDataSource();
//		ds.setDriverClassName("com.mysql.jdbc.Driver");
//		ds.setUsername("root");
//		ds.setPassword("root");
		ds.setUrl(url);

		// the settings below are optional -- dbcp can work with defaults
		ds.setMinIdle(5);
		ds.setMaxIdle(20);
		ds.setMaxOpenPreparedStatements(180);
	}

	public static DataSource getInstance() throws IOException, SQLException, PropertyVetoException {
		if (datasource == null) {
			datasource = new DataSource();
			return datasource;
		} else {
			return datasource;
		}
	}

	public Connection getConnection() throws SQLException {
		return this.ds.getConnection();
	}
}
