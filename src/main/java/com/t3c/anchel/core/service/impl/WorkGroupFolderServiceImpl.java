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
package com.t3c.anchel.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.t3c.anchel.core.domain.constants.AuditLogEntryType;
import com.t3c.anchel.core.domain.constants.LogAction;
import com.t3c.anchel.core.domain.entities.Account;
import com.t3c.anchel.core.domain.entities.Thread;
import com.t3c.anchel.core.domain.entities.User;
import com.t3c.anchel.core.exception.BusinessErrorCode;
import com.t3c.anchel.core.exception.BusinessException;
import com.t3c.anchel.core.repository.ThreadMemberRepository;
import com.t3c.anchel.core.service.AntiSamyService;
import com.t3c.anchel.core.service.LogEntryService;
import com.t3c.anchel.core.service.WorkGroupFolderService;
import com.t3c.anchel.mongo.entities.WorkGroupNode;
import com.t3c.anchel.mongo.entities.logs.WorkGroupNodeAuditLogEntry;
import com.t3c.anchel.mongo.entities.mto.AccountMto;
import com.t3c.anchel.mongo.repository.WorkGroupNodeMongoRepository;

public class WorkGroupFolderServiceImpl extends WorkGroupNodeAbstractServiceImpl implements WorkGroupFolderService {

	public WorkGroupFolderServiceImpl(
			WorkGroupNodeMongoRepository repository,
			ThreadMemberRepository threadMemberRepository,
			MongoTemplate mongoTemplate,
			AntiSamyService antiSamyService,
			LogEntryService logEntryService) {
		super(repository, mongoTemplate, antiSamyService, threadMemberRepository, logEntryService);
	}

	@Override
	protected BusinessErrorCode getBusinessExceptionAlreadyExists() {
		return BusinessErrorCode.WORK_GROUP_FOLDER_ALREADY_EXISTS;
	}

	@Override
	protected BusinessErrorCode getBusinessExceptionNotFound() {
		return BusinessErrorCode.WORK_GROUP_FOLDER_NOT_FOUND;
	}

	@Override
	protected BusinessErrorCode getBusinessExceptionForbidden() {
		return BusinessErrorCode.WORK_GROUP_FOLDER_FORBIDDEN;
	}

	@Override
	public WorkGroupNode create(Account actor, User owner, Thread workGroup, WorkGroupNode workGroupNode, WorkGroupNode nodeParent, Boolean strict, Boolean dryRun)
			throws BusinessException {
		List<WorkGroupNode> node = repository.findByWorkGroupAndParentAndName(
				workGroup.getLsUuid(),
				nodeParent.getUuid(),
				workGroupNode.getName());
		if (node != null && !node.isEmpty()) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_FOLDER_ALREADY_EXISTS,
					"Can not create a new folder, it already exists.");
		}
		try {
			workGroupNode.setId(null);
			workGroupNode.setParent(nodeParent.getUuid());
			workGroupNode.setWorkGroup(workGroup.getLsUuid());
			workGroupNode.setPathFromParent(nodeParent);
			workGroupNode.setUuid(UUID.randomUUID().toString());
			workGroupNode.setCreationDate(new Date());
			workGroupNode.setModificationDate(new Date());
			workGroupNode.setLastAuthor(new AccountMto(owner, true));
			if (dryRun) {
				return workGroupNode;
			}
			workGroupNode = repository.insert(workGroupNode);
		} catch (org.springframework.dao.DuplicateKeyException e) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_FOLDER_ALREADY_EXISTS,
					"Can not create a new folder, it already exists.");
		}
		WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.CREATE,
				AuditLogEntryType.WORKGROUP_FOLDER, workGroupNode, workGroup);
		addMembersToLog(workGroup, log);
		logEntryService.insert(log);
		return workGroupNode;
	}

	@Override
	public WorkGroupNode delete(Account actor, User owner, Thread workGroup, WorkGroupNode workGroupNode)
			throws BusinessException {
		WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.DELETE,
				AuditLogEntryType.WORKGROUP_FOLDER, workGroupNode, workGroup);
		addMembersToLog(workGroup, log);
		logEntryService.insert(log);
		repository.delete(workGroupNode);
		return workGroupNode;
	}

}
