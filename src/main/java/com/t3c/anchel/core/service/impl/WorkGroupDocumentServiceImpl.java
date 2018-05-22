/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2016. Contribute to
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
package com.t3c.anchel.core.service.impl;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.lang.Validate;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.t3c.anchel.core.business.service.DocumentEntryBusinessService;
import com.t3c.anchel.core.business.service.OperationHistoryBusinessService;
import com.t3c.anchel.core.dao.MimeTypeMagicNumberDao;
import com.t3c.anchel.core.domain.constants.AuditLogEntryType;
import com.t3c.anchel.core.domain.constants.ContainerQuotaType;
import com.t3c.anchel.core.domain.constants.LogAction;
import com.t3c.anchel.core.domain.constants.LogActionCause;
import com.t3c.anchel.core.domain.constants.OperationHistoryTypeEnum;
import com.t3c.anchel.core.domain.entities.AbstractDomain;
import com.t3c.anchel.core.domain.entities.Account;
import com.t3c.anchel.core.domain.entities.DocumentEntry;
import com.t3c.anchel.core.domain.entities.Functionality;
import com.t3c.anchel.core.domain.entities.OperationHistory;
import com.t3c.anchel.core.domain.entities.StringValueFunctionality;
import com.t3c.anchel.core.domain.entities.Thread;
import com.t3c.anchel.core.domain.entities.User;
import com.t3c.anchel.core.exception.BusinessErrorCode;
import com.t3c.anchel.core.exception.BusinessException;
import com.t3c.anchel.core.repository.ThreadMemberRepository;
import com.t3c.anchel.core.service.AntiSamyService;
import com.t3c.anchel.core.service.FunctionalityReadOnlyService;
import com.t3c.anchel.core.service.LogEntryService;
import com.t3c.anchel.core.service.MimeTypeService;
import com.t3c.anchel.core.service.QuotaService;
import com.t3c.anchel.core.service.VirusScannerService;
import com.t3c.anchel.core.service.WorkGroupDocumentService;
import com.t3c.anchel.core.utils.UniqueName;
import com.t3c.anchel.mongo.entities.WorkGroupDocument;
import com.t3c.anchel.mongo.entities.WorkGroupNode;
import com.t3c.anchel.mongo.entities.logs.WorkGroupNodeAuditLogEntry;
import com.t3c.anchel.mongo.repository.WorkGroupNodeMongoRepository;

public class WorkGroupDocumentServiceImpl extends WorkGroupNodeAbstractServiceImpl implements WorkGroupDocumentService {

	private final DocumentEntryBusinessService documentEntryBusinessService;
	private final LogEntryService logEntryService;
	private final FunctionalityReadOnlyService functionalityReadOnlyService;
	private final MimeTypeService mimeTypeService;
	private final VirusScannerService virusScannerService;
	private final MimeTypeMagicNumberDao mimeTypeIdentifier;
	private final OperationHistoryBusinessService operationHistoryBusinessService;
	private final QuotaService quotaService;
	protected final WorkGroupNodeMongoRepository repository;

	public WorkGroupDocumentServiceImpl(DocumentEntryBusinessService documentEntryBusinessService,
			LogEntryService logEntryService, FunctionalityReadOnlyService functionalityReadOnlyService,
			MimeTypeService mimeTypeService, VirusScannerService virusScannerService,
			MimeTypeMagicNumberDao mimeTypeIdentifier, AntiSamyService antiSamyService,
			WorkGroupNodeMongoRepository workGroupNodeMongoRepository, ThreadMemberRepository threadMemberRepository,
			MongoTemplate mongoTemplate, OperationHistoryBusinessService operationHistoryBusinessService,
			QuotaService quotaService) {
		super(workGroupNodeMongoRepository, mongoTemplate, antiSamyService, threadMemberRepository, logEntryService);
		this.documentEntryBusinessService = documentEntryBusinessService;
		this.logEntryService = logEntryService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.mimeTypeService = mimeTypeService;
		this.virusScannerService = virusScannerService;
		this.mimeTypeIdentifier = mimeTypeIdentifier;
		this.operationHistoryBusinessService = operationHistoryBusinessService;
		this.quotaService = quotaService;
		this.repository = workGroupNodeMongoRepository;
	}

	@Override
	public WorkGroupNode create(Account actor, Account owner, Thread workgroup, File tempFile, String fileName,
			WorkGroupNode nodeParent) throws BusinessException {
		Validate.notNull(nodeParent);

		Long size = tempFile.length();
		WorkGroupDocument document = null;

		try {
			String mimeType = mimeTypeIdentifier.getMimeType(tempFile);
			AbstractDomain domain = owner.getDomain();
			checkSpace(workgroup, size);

			// check if the file MimeType is allowed
			Functionality mimeFunctionality = functionalityReadOnlyService.getMimeTypeFunctionality(domain);
			if (mimeFunctionality.getActivationPolicy().getStatus()) {
				mimeTypeService.checkFileMimeType(owner, fileName, mimeType);
			}

			virusScannerService.checkVirus(fileName, owner, tempFile, size);

			// want a timestamp on doc ?
			String timeStampingUrl = null;
			StringValueFunctionality timeStampingFunctionality = functionalityReadOnlyService
					.getTimeStampingFunctionality(domain);
			if (timeStampingFunctionality.getActivationPolicy().getStatus()) {
				timeStampingUrl = timeStampingFunctionality.getValue();
			}

			Functionality enciphermentFunctionality = functionalityReadOnlyService.getEnciphermentFunctionality(domain);
			Boolean checkIfIsCiphered = enciphermentFunctionality.getActivationPolicy().getStatus();
			document = documentEntryBusinessService.createWorkGroupDocument(owner, workgroup, tempFile, size, fileName,
					checkIfIsCiphered, timeStampingUrl, mimeType, nodeParent);

			WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.CREATE,
					AuditLogEntryType.WORKGROUP_DOCUMENT, document, workgroup);
			addMembersToLog(workgroup, log);
			logEntryService.insert(log);
			addToQuota(workgroup, size);

		} finally {
			try {
				logger.debug("deleting temp file : " + tempFile.getName());
				tempFile.delete(); // remove the temporary file
			} catch (Exception e) {
				logger.error("can not delete temp file : " + e.getMessage());
			}
		}
		return document;
	}

	@Override
	public WorkGroupNode copy(Account actor, Account owner, Thread workGroup, DocumentEntry documentEntry,
			WorkGroupNode nodeParent) throws BusinessException {
		// checkCreatePermission(actor, owner, ThreadEntry.class,
		// BusinessErrorCode.THREAD_ENTRY_FORBIDDEN, null, workGroup);
		checkSpace(workGroup, documentEntry.getSize());
		WorkGroupDocument node = documentEntryBusinessService.copy(actor, workGroup, nodeParent,
				documentEntry.getDocument().getUuid(), documentEntry.getName());
		WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.CREATE,
				AuditLogEntryType.WORKGROUP_DOCUMENT, node, workGroup);
		log.setCause(LogActionCause.COPY);
		addMembersToLog(workGroup, log);
		logEntryService.insert(log);
		addToQuota(workGroup, documentEntry.getSize());
		return node;
	}

	@Override
	public WorkGroupNode copy(Account actor, Account owner, Thread workGroup, WorkGroupDocument nodeSource,
			WorkGroupNode nodeParent, String newName) throws BusinessException {
		// checkCreatePermission(actor, owner, ThreadEntry.class,
		// BusinessErrorCode.THREAD_ENTRY_FORBIDDEN, null, workGroup);
		checkSpace(workGroup, nodeSource.getSize());
		WorkGroupDocument node = documentEntryBusinessService.copy(actor, workGroup, nodeParent,
				nodeSource.getDocumentUuid(), newName);
		WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.CREATE,
				AuditLogEntryType.WORKGROUP_DOCUMENT, node, workGroup);
		log.setCause(LogActionCause.COPY);
		addMembersToLog(workGroup, log);
		logEntryService.insert(log);
		addToQuota(workGroup, nodeSource.getSize());
		return node;
	}

	@Override
	public WorkGroupNode delete(Account actor, User owner, Thread workGroup, WorkGroupNode workGroupNode)
			throws BusinessException {
		WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.DELETE,
				AuditLogEntryType.WORKGROUP_DOCUMENT, workGroupNode, workGroup);
		addMembersToLog(workGroup, log);
		logEntryService.insert(log);
		delFromQuota(workGroup, ((WorkGroupDocument) workGroupNode).getSize());
		repository.delete(workGroupNode);
		return workGroupNode;
	}

	@Override
	public InputStream getDocumentStream(Account actor, Account owner, Thread workGroup, WorkGroupDocument node)
			throws BusinessException {
		WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.DOWNLOAD,
				AuditLogEntryType.WORKGROUP_DOCUMENT, node, workGroup);
		addMembersToLog(workGroup, log);
		logEntryService.insert(log);
		return documentEntryBusinessService.getDocumentStream(node);
	}

	@Override
	public InputStream getThumbnailStream(Account actor, Account owner, Thread workGroup, WorkGroupDocument node)
			throws BusinessException {
		return documentEntryBusinessService.getThreadEntryThumbnailStream(node);
	}

	protected void checkSpace(Thread thread, long size) throws BusinessException {
		quotaService.checkIfUserCanAddFile(thread, size, ContainerQuotaType.WORK_GROUP);
	}

	protected void addToQuota(Thread thread, Long size) {
		OperationHistory oh = new OperationHistory(thread, thread.getDomain(), size, OperationHistoryTypeEnum.CREATE,
				ContainerQuotaType.WORK_GROUP);
		operationHistoryBusinessService.create(oh);
	}

	protected void delFromQuota(Thread thread, Long size) {
		OperationHistory oh = new OperationHistory(thread, thread.getDomain(), size, OperationHistoryTypeEnum.DELETE,
				ContainerQuotaType.WORK_GROUP);
		operationHistoryBusinessService.create(oh);
	}

	@Override
	protected UniqueName getQueryForNodesWithSameName(String name) {
		String[] split = name.split("\\.");
		if (split.length == 1) {
			return super.getQueryForNodesWithSameName(name);
		} else {
			String extension = split[split.length - 1];
			if (mimeTypeIdentifier.isKnownExtension("." + extension)) {
				String baseName = name.substring(0, name.length() - extension.length() - 1);
				UniqueName un = new UniqueName(name);
				un.setBaseName(baseName);
				un.setSuffix("." + extension);
				String pattern = " \\(([0-9]+)\\)\\." + extension + "$";
				un.setSearchPattern("^" + UniqueName.getEscapeName(baseName) + pattern);
				un.setExtractPattern(pattern);
				return un;
			} else {
				return super.getQueryForNodesWithSameName(name);
			}
		}
	}

	@Override
	protected BusinessErrorCode getBusinessExceptionAlreadyExists() {
		return BusinessErrorCode.WORK_GROUP_DOCUMENT_ALREADY_EXISTS;
	}

	@Override
	protected BusinessErrorCode getBusinessExceptionNotFound() {
		return BusinessErrorCode.WORK_GROUP_DOCUMENT_NOT_FOUND;
	}

	@Override
	protected BusinessErrorCode getBusinessExceptionForbidden() {
		return BusinessErrorCode.WORK_GROUP_DOCUMENT_FORBIDDEN;
	}

}
