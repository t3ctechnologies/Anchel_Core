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
package com.t3c.anchel.core.service;

import java.io.File;
import java.util.List;

import com.t3c.anchel.core.domain.constants.WorkGroupNodeType;
import com.t3c.anchel.core.domain.entities.Account;
import com.t3c.anchel.core.domain.entities.DocumentEntry;
import com.t3c.anchel.core.domain.entities.Thread;
import com.t3c.anchel.core.domain.entities.User;
import com.t3c.anchel.core.exception.BusinessException;
import com.t3c.anchel.core.utils.FileAndMetaData;
import com.t3c.anchel.mongo.entities.WorkGroupNode;

public interface WorkGroupNodeService {

	List<WorkGroupNode> findAll(Account actor, User owner, Thread workGroup) throws BusinessException;

	List<WorkGroupNode> findAll(Account actor, User owner, Thread workGroup, String parentUuid, Boolean flatDocumentMode, WorkGroupNodeType nodeType)
			throws BusinessException;

	WorkGroupNode find(Account actor, User owner, Thread workGroup, String workGroupNodeUuid, boolean withTree) throws BusinessException;

	String findWorkGroupUuid(Account actor, User owner, String workGroupNodeUuid) throws BusinessException;

	WorkGroupNode create(Account actor, User owner, Thread workGroup, WorkGroupNode workGroupNode, Boolean strict, Boolean dryRun)
			throws BusinessException;

	WorkGroupNode create(Account actor, User owner, Thread workGroup, File tempFile, String fileName,
			String parentNodeUuid, Boolean strict) throws BusinessException;

	WorkGroupNode update(Account actor, User owner, Thread workGroup, WorkGroupNode workGroupNode)
			throws BusinessException;

	WorkGroupNode delete(Account actor, User owner, Thread workGroup, String workGroupNodeUuid)
			throws BusinessException;

	FileAndMetaData download(Account actor, User owner, Thread workGroup, String workGroupNodeUuid)
			throws BusinessException;

	FileAndMetaData thumbnail(Account actor, User owner, Thread workGroup, String workGroupNodeUuid)
			throws BusinessException;

	WorkGroupNode copy(Account actor, User owner, Thread workGroup, String workGroupNodeUuid, String destinationNodeUuid) throws BusinessException;

	WorkGroupNode copy(Account actor, User owner, Thread workGroup, DocumentEntry documentEntry, WorkGroupNode nodeParent)
			throws BusinessException;

	WorkGroupNode getRootFolder(Account actor, User owner, Thread workGroup);

}
