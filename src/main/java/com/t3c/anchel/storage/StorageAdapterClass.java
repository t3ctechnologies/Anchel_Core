package com.t3c.anchel.storage;

import java.io.FileNotFoundException;
import java.net.BindException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.t3c.anchel.core.common.FtpClientConfiguration;

public abstract class StorageAdapterClass {

	String accesskey = null;
	String secretkey = null;
	String bucketName = null;
	Properties properties = null;

	public StorageAdapterClass() {
		Properties properties = new FtpClientConfiguration().getclientProperties();
		this.accesskey = properties.getProperty(SMConstants.ACCESS_KEY);
		this.secretkey = properties.getProperty(SMConstants.SECRET_KEY);
		this.bucketName = properties.getProperty(SMConstants.BUCKET_NAME);
	}

	String fileName = null;
	// AmazoneDTO amazoneDTO = null;
	static AmazonS3 s3client = null;
	static AWSCredentials credentials = null;

	public StorageAdapterClass create(StorageAdapterClass entity) throws BindException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public StorageAdapterClass load(StorageAdapterClass entity) throws BindException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public StorageAdapterClass update(StorageAdapterClass entity) throws BindException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<StorageAdapterClass> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public abstract void DeleteFile(String specialId, String uuid) throws SQLException;

	public abstract StorageAccessDTO sendFile(String filePath, String folderName, String key)
			throws FileNotFoundException;

	public abstract void GetAll(String bucketName, String folderName);

	public StorageAccessDTO get(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	public boolean delete(StorageAccessDTO storageaccess) {
		credentials = new BasicAWSCredentials(this.accesskey, this.secretkey);
		s3client = new AmazonS3Client(credentials);
		java.util.List<S3ObjectSummary> fileList = (List<S3ObjectSummary>) s3client.listObjects(this.bucketName,
				storageaccess.getFolder());
		for (S3ObjectSummary file : fileList) {

			s3client.deleteObject(this.bucketName, file.getKey());
		}
	
		return false;
	}

}