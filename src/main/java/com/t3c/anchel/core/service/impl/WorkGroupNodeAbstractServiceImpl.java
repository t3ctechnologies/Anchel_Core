/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.t3c.anchel.core.domain.constants.WorkGroupNodeType;
import com.t3c.anchel.core.domain.entities.Account;
import com.t3c.anchel.core.domain.entities.Thread;
import com.t3c.anchel.core.domain.entities.User;
import com.t3c.anchel.core.exception.BusinessErrorCode;
import com.t3c.anchel.core.exception.BusinessException;
import com.t3c.anchel.core.repository.ThreadMemberRepository;
import com.t3c.anchel.core.service.AntiSamyService;
import com.t3c.anchel.core.service.LogEntryService;
import com.t3c.anchel.core.service.WorkGroupNodeAbstractService;
import com.t3c.anchel.core.utils.UniqueName;
import com.t3c.anchel.mongo.entities.WorkGroupNode;
import com.t3c.anchel.mongo.entities.logs.AuditLogEntryUser;
import com.t3c.anchel.mongo.repository.WorkGroupNodeMongoRepository;

public abstract class WorkGroupNodeAbstractServiceImpl implements WorkGroupNodeAbstractService {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected final WorkGroupNodeMongoRepository repository;

	protected final ThreadMemberRepository threadMemberRepository;

	protected final MongoTemplate mongoTemplate;

	protected final LogEntryService logEntryService;

	protected final AntiSamyService antiSamyService;

	public WorkGroupNodeAbstractServiceImpl(
			WorkGroupNodeMongoRepository repository,
			MongoTemplate mongoTemplate,
			AntiSamyService antiSamyService,
			ThreadMemberRepository threadMemberRepository,
			LogEntryService logEntryService) {
		super();
		this.repository = repository;
		this.mongoTemplate = mongoTemplate;
		this.logEntryService = logEntryService;
		this.antiSamyService = antiSamyService;
		this.threadMemberRepository = threadMemberRepository;
	}

	protected abstract BusinessErrorCode getBusinessExceptionAlreadyExists();

	protected abstract BusinessErrorCode getBusinessExceptionNotFound();

	protected abstract BusinessErrorCode getBusinessExceptionForbidden();

	@Override
	public WorkGroupNode find(Account actor, User owner, Thread workGroup, String workGroupNodeUuid)
			throws BusinessException {
		WorkGroupNode folder = repository.findByWorkGroupAndUuid(workGroup.getLsUuid(), workGroupNodeUuid);
		if (folder == null) {
			logger.error("Node not found " + workGroupNodeUuid);
			throw new BusinessException(getBusinessExceptionNotFound(),
					"Node not found : " + workGroupNodeUuid);
		}
		return folder;
	}

	@Override
	public void checkUniqueName(Thread workGroup, WorkGroupNode nodeParent, String name) {
		if (!isUniqueName(workGroup, nodeParent, name)) {
			throw new BusinessException(getBusinessExceptionAlreadyExists(),
					"Can not create a new node, it already exists.");
		}
	}

	protected boolean isUniqueName(Thread workGroup, WorkGroupNode nodeParent, String name) {
		List<WorkGroupNode> nodes = repository.findByWorkGroupAndParentAndName(workGroup.getLsUuid(),
				nodeParent.getUuid(), name);
		if (nodes.isEmpty()) {
			return true;
		}
		return false;
	}

	protected UniqueName getQueryForNodesWithSameName(String name) {
		UniqueName un = new UniqueName(name);
		String pattern = " \\(([0-9]+)\\)$";
		un.setSearchPattern("^" + UniqueName.getEscapeName(name) + pattern);
		un.setExtractPattern(pattern);
		return un;
	}

	@Override
	public String getNewName(Account actor, User owner, Thread workGroup, WorkGroupNode nodeParent,
			String currentName) {
		if (!isUniqueName(workGroup, nodeParent, currentName)) {
			UniqueName res = getQueryForNodesWithSameName(currentName);
			Query query = res.validate().buildQuery();
			query.addCriteria(Criteria.where("workGroup").is(workGroup.getLsUuid()));
			query.addCriteria(Criteria.where("parent").is(nodeParent.getUuid()));
			List<WorkGroupNode> nodes = mongoTemplate.find(query, WorkGroupNode.class);
			res.setFoundNodes(nodes);
			currentName = res.computeNewName();
			logger.info("new computed name {} ", currentName);
		}
		return currentName;
	}

	protected boolean isDocument(WorkGroupNode node) {
		return node.getNodeType().equals(WorkGroupNodeType.DOCUMENT);
	}

	protected boolean isFolder(WorkGroupNode node) {
		return node.getNodeType().equals(WorkGroupNodeType.FOLDER);
	}

	protected boolean isRevison(WorkGroupNode node) {
		return node.getNodeType().equals(WorkGroupNodeType.DOCUMENT_REVISION);
	}

	protected void addMembersToLog(Thread thread, AuditLogEntryUser log) {
		List<String> members = threadMemberRepository.findAllAccountUuidForThreadMembers(thread);
		log.addRelatedAccounts(members);
	}
}
