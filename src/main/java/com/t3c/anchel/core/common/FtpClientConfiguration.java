package com.t3c.anchel.core.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FtpClientConfiguration {
	 public Properties getclientProperties() {
		Properties properties = new Properties();
		try {
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("linshare.properties");
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}
}
