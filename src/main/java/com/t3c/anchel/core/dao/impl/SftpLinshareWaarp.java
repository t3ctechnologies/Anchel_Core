package com.t3c.anchel.core.dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;

import com.t3c.anchel.core.common.FtpClientConfiguration;

public class SftpLinshareWaarp {

	public void insert(String uuid, String file) {
		PreparedStatement preparedStmt = null;
		Connection conn = null;
		Properties properties = new FtpClientConfiguration().getclientProperties();
		String url = properties.getProperty("linshare.db.url");
		String user = properties.getProperty("linshare.db.username");
		String password = properties.getProperty("linshare.db.password");
		try {
			conn = DriverManager.getConnection(url, user, password);
			String query = " insert into S3BUCKETMAPPING(filename,uuid,processedOn,deleted)" 
							+ " values (?,?,?,?)";
			preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, file);
			preparedStmt.setString(2, uuid);
			preparedStmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			preparedStmt.setBoolean(4, false);
			
			preparedStmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
