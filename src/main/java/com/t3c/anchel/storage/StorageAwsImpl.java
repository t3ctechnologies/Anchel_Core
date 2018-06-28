package com.t3c.anchel.storage;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.t3c.anchel.core.common.FtpClientConfiguration;

public class StorageAwsImpl extends StorageAdapterClass {

	String accesskey = null;
	String secretkey = null;
	String bucketname = null;
	Properties properties = null;
	String region = null;

	public StorageAwsImpl() {
		properties = new FtpClientConfiguration().getclientProperties();
		this.accesskey = properties.getProperty(SMConstants.ACCESS_KEY);
		this.secretkey = properties.getProperty(SMConstants.SECRET_KEY);
		this.bucketname = properties.getProperty(SMConstants.BUCKET_NAME);
		this.region = properties.getProperty(SMConstants.CLIENT_REGION);

	}

	private static final Logger logger = LoggerFactory.getLogger(StorageAwsImpl.class);

	public void DeleteFile(String uuid) throws SQLException, AmazonServiceException, AmazonClientException {
		credentials = new BasicAWSCredentials(this.accesskey, this.secretkey);
		s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSCredentialsProvider() {
			public void refresh() {
			}

			public AWSCredentials getCredentials() {
				return credentials;
			}
		}).withRegion(this.region).build();
		s3client.deleteObject(this.bucketname, uuid);
		logger.debug("File with this {} id deleted successfully", uuid);
		new AccessClass().updateS3Bucket(uuid);
	}

	@Override
	public void GetAll(String bucketName, String folderName) {

	}

	public void GetById(String bucketName, String folderName, String key, String targetFile) {

	}

	@Override
	public void sendFile(String filePath, String folderName, String key) throws FileNotFoundException {
		// TODO Auto-generated method stub
	}

}
