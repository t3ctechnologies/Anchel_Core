/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and t3c.io, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */

package com.t3c.anchel.core.dao.impl;

import java.io.File;
import java.io.InputStream;

import com.t3c.anchel.core.dao.FileDataStore;
import com.t3c.anchel.core.domain.constants.FileMetaDataKind;
import com.t3c.anchel.core.domain.objects.FileMetaData;

public class DataKindBalancerFileDataStoreImpl implements FileDataStore {

	private FileDataStore bigFilesDataStore;

	private FileDataStore smallFilesDataStore;

	public DataKindBalancerFileDataStoreImpl(FileDataStore bigFilesDataStore, FileDataStore smallFilesDataStore) {
		super();
		this.bigFilesDataStore = bigFilesDataStore;
		this.smallFilesDataStore = smallFilesDataStore;
	}

	@Override
	public void remove(FileMetaData metadata) {
		if (metadata.getKind().equals(FileMetaDataKind.DATA)) {
			bigFilesDataStore.remove(metadata);
		} else {
			smallFilesDataStore.remove(metadata);
		}
	}

	@Override
	public FileMetaData add(File file, FileMetaData metadata) {

		if (metadata.getKind().equals(FileMetaDataKind.DATA)) {
			return bigFilesDataStore.add(file, metadata);
		} else {
			return smallFilesDataStore.add(file, metadata);
		}
	}

	@Override
	public FileMetaData add(InputStream inputStream, FileMetaData metadata) {
		if (metadata.getKind().equals(FileMetaDataKind.DATA)) {
			return bigFilesDataStore.add(inputStream, metadata);
		} else {
			return smallFilesDataStore.add(inputStream, metadata);
		}
	}

	@Override
	public InputStream get(FileMetaData metadata) {

		// TODO Calling download request to ftp-client
		/*
		 * String fileName = new
		 * SftpLinshareWaarp().getByUuid(metadata.getUuid());
		 * 
		 * Properties properties = new
		 * FtpClientConfiguration().getclientProperties(); if (fileName != null)
		 * { String download_dir =
		 * properties.getProperty("com.sgs.waarp.download_dir"); fileName =
		 * download_dir.concat(new File(fileName).getName()); } try { String
		 * server = properties.getProperty("com.sgs.waarp.gatewayserver");
		 * String user = properties.getProperty("com.sgs.waarp.user"); String
		 * password = properties.getProperty("com.sgs.waarp.password"); String
		 * account = properties.getProperty("com.sgs.waarp.accountname"); String
		 * mode = properties.getProperty("com.sgs.waarp.download.mode");
		 * String[] configArray = { fileName, mode, server, user, password,
		 * account }; String gateway_path =
		 * properties.getProperty("com.sgs.waarp.gateway_path"); String
		 * gateway_file = gateway_path.concat(new File(fileName).getName());
		 * boolean secondMethodStatus = false; FtpClient.init(configArray); for
		 * (int i = 10; i < 1000; i++) { if (new File(gateway_file).exists()) {
		 * secondMethodStatus = true; break; } else { Thread.sleep(i * 1000); }
		 * } if (secondMethodStatus) { FtpClient.init(configArray); }
		 * System.err.
		 * println("********************FILE DOWNLOADING IS COMPLETED*******************"
		 * ); //TODO Deleting GatewayFile new File(gateway_file).delete(); }
		 * catch (IOException | InterruptedException e) { e.printStackTrace(); }
		 */

		if (metadata.getKind().equals(FileMetaDataKind.DATA)) {
			return bigFilesDataStore.get(metadata);
		} else {
			return smallFilesDataStore.get(metadata);
		}
	}

	@Override
	public boolean exists(FileMetaData metadata) {
		if (metadata.getKind().equals(FileMetaDataKind.DATA)) {
			return bigFilesDataStore.exists(metadata);
		} else {
			return smallFilesDataStore.exists(metadata);
		}
	}
}
