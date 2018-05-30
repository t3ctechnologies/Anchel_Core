package com.t3c.anchel.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.t3c.anchel.core.common.FtpClientConfiguration;

public class AccessClass {

	Properties properties = null;
	String url = null;
	String user = null;
	String password = null;

	public AccessClass() {
		properties = new FtpClientConfiguration().getclientProperties();
		url = properties.getProperty("linshare.db.url");
		user = properties.getProperty("linshare.db.username");
		password = properties.getProperty("linshare.db.password");
	}

	public void updateS3Bucket(String specialKey) throws SQLException {

		try {
			Connection conn = (Connection) DriverManager.getConnection(url, user, password);
			String query = " UPDATE S3BUCKETMAPPING set deleted=1 where specialKey=?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setNString(1, specialKey);
			preparedStmt.executeUpdate();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateS3Filename(String uuid) throws SQLException {

		try {
			Connection conn = (Connection) DriverManager.getConnection(url, user, password);
			String query = " UPDATE S3FILENAMEHANDLER set deleted=1 where uuid=?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setNString(1, uuid);
			preparedStmt.executeUpdate();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String TakeSpecialId(String uuid) {
		String splId = null;
		try {
			Connection conn = (Connection) DriverManager.getConnection(url, user, password);
			String query = " SELECT specialKey FROM S3BUCKETMAPPING WHERE uuid =?";

			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setNString(1, uuid);

			ResultSet rs = preparedStmt.executeQuery();
			rs.next();
			splId = rs.getString(1);

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return splId;
	}

}