/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
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
package com.t3c.anchel.core.domain.entities;

import java.io.Serializable;

import com.t3c.anchel.core.domain.constants.EntryType;

public class UploadRequestEntry extends Entry implements Serializable {

	private static final long serialVersionUID = 54638444450061115L;

	private DocumentEntry documentEntry;

	private UploadRequestUrl uploadRequestUrl;

	private long size;

	public UploadRequestEntry() {
		super();
	}

	public UploadRequestEntry(DocumentEntry documentEntry,
			UploadRequestUrl requestUrl) {
		super(documentEntry.getEntryOwner(), documentEntry.getName(),
				documentEntry.getComment());
		this.documentEntry = documentEntry;
		this.uploadRequestUrl = requestUrl;
		this.size = documentEntry.getSize();
	}

	public DocumentEntry getDocumentEntry() {
		return documentEntry;
	}

	public void setDocumentEntry(DocumentEntry documentEntry) {
		this.documentEntry = documentEntry;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public EntryType getEntryType() {
		return EntryType.UPLOAD_REQUEST;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public UploadRequestUrl getUploadRequestUrl() {
		return uploadRequestUrl;
	}

	public void setUploadRequestUrl(UploadRequestUrl uploadRequestUrl) {
		this.uploadRequestUrl = uploadRequestUrl;
	}
}
