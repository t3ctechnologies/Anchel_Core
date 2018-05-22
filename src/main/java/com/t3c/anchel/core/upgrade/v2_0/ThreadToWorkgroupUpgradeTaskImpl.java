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
package com.t3c.anchel.core.upgrade.v2_0;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.dao.support.DataAccessUtils;

import com.t3c.anchel.core.batches.impl.GenericUpgradeTaskImpl;
import com.t3c.anchel.core.batches.utils.OperationKind;
import com.t3c.anchel.core.domain.constants.LogActionV1;
import com.t3c.anchel.core.domain.constants.UpgradeTaskType;
import com.t3c.anchel.core.domain.constants.WorkGroupNodeType;
import com.t3c.anchel.core.domain.entities.Account;
import com.t3c.anchel.core.domain.entities.LogEntry;
import com.t3c.anchel.core.domain.entities.SystemAccount;
import com.t3c.anchel.core.domain.entities.Thread;
import com.t3c.anchel.core.domain.entities.ThreadEntry;
import com.t3c.anchel.core.exception.BatchBusinessException;
import com.t3c.anchel.core.exception.BusinessErrorCode;
import com.t3c.anchel.core.exception.BusinessException;
import com.t3c.anchel.core.job.quartz.BatchResultContext;
import com.t3c.anchel.core.job.quartz.BatchRunContext;
import com.t3c.anchel.core.job.quartz.ResultContext;
import com.t3c.anchel.core.repository.AccountRepository;
import com.t3c.anchel.core.repository.LogEntryRepository;
import com.t3c.anchel.core.repository.ThreadEntryRepository;
import com.t3c.anchel.core.repository.ThreadRepository;
import com.t3c.anchel.core.service.WorkGroupDocumentService;
import com.t3c.anchel.mongo.entities.WorkGroupDocument;
import com.t3c.anchel.mongo.entities.WorkGroupFolder;
import com.t3c.anchel.mongo.entities.WorkGroupNode;
import com.t3c.anchel.mongo.entities.mto.AccountMto;
import com.t3c.anchel.mongo.repository.UpgradeTaskLogMongoRepository;
import com.t3c.anchel.mongo.repository.WorkGroupNodeMongoRepository;
import com.t3c.anchel.view.tapestry.beans.LogCriteriaBean;
import com.t3c.anchel.view.tapestry.enums.CriterionMatchMode;

public class ThreadToWorkgroupUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	protected ThreadRepository threadRepository;

	protected ThreadEntryRepository threadEntryRepository;

	protected WorkGroupDocumentService workGroupDocumentService;

	protected WorkGroupNodeMongoRepository repository;

	protected LogEntryRepository logEntryRepository;

	public ThreadToWorkgroupUpgradeTaskImpl(
			AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			ThreadEntryRepository threadEntryRepository,
			WorkGroupDocumentService workGroupDocumentService,
			ThreadRepository threadRepository,
			WorkGroupNodeMongoRepository repository,
			LogEntryRepository logEntryRepository
			) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.threadRepository = threadRepository;
		this.threadEntryRepository = threadEntryRepository;
		this.workGroupDocumentService = workGroupDocumentService;
		this.repository = repository;
		this.logEntryRepository = logEntryRepository;
		this.operationKind = OperationKind.UPGRADED;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_2_0_THREAD_TO_WORKGROUP;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		List<String> uuids = threadRepository.findAllThreadToUpgrade();
		return uuids;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		Thread workGroup = threadRepository.findByLsUuid(identifier);
		BatchResultContext<Thread> res = new BatchResultContext<Thread>(workGroup);
		if (workGroup == null) {
			res.setIdentifier(identifier);
			return res;
		}
		SystemAccount actor = accountRepository.getBatchSystemAccount();
		WorkGroupNode rootFolder = getRootFolder(new AccountMto(actor), workGroup);
		List<ThreadEntry> entries = threadEntryRepository.findAllThreadEntries(workGroup);
		long total2 = entries.size();
		logDebug(batchRunContext, total, position, total2 + " Thread entries found. Converting to workgroup documents ...");
		long position2 = 0;
		for (ThreadEntry entry : entries) {
			position2 += 1;
			String name = entry.getName();
			AccountMto lastAuthor = getLastAuthor(actor, entry, name);
			// manage unicity.
			name = workGroupDocumentService.getNewName(null, null, workGroup, rootFolder, name);
			WorkGroupDocument node = new WorkGroupDocument(actor, name, entry.getDocument(), workGroup, rootFolder);
			node.setCreationDate(entry.getCreationDate().getTime());
			node.setModificationDate(entry.getModificationDate().getTime());
			node.setUuid(entry.getUuid());
			node.setUploadDate(entry.getCreationDate().getTime());
			node.setPathFromParent(rootFolder);
			node.setLastAuthor(lastAuthor);
			try {
				node = repository.insert(node);
				logDebug(batchRunContext, total2, position2, "New workgroup document: " + node.getName() + " : " + node.getUuid());
			} catch (org.springframework.dao.DuplicateKeyException e) {
				List<WorkGroupNode> entities = repository.findByWorkGroup(workGroup.getLsUuid());
				repository.delete(entities);
				throw new BusinessException(BusinessErrorCode.WORK_GROUP_DOCUMENT_ALREADY_EXISTS,
						"Can not create a new document, it already exists.");
			}
		}
		threadRepository.setAsUpgraded(workGroup);
		res.setProcessed(true);
		return res;
	}

	protected AccountMto getLastAuthor(SystemAccount actor, ThreadEntry entry, String name) {
		AccountMto lastAuthor = new AccountMto(actor, true);
		LogCriteriaBean crit = new LogCriteriaBean();
		crit.setFileNameMatchMode(CriterionMatchMode.EXACT);
		crit.setFileName(name);
		crit.addLogActions(LogActionV1.THREAD_UPLOAD_ENTRY);
		Calendar endCal = (Calendar)entry.getCreationDate().clone();
		endCal.add(Calendar.SECOND, 1);
		crit.setAfterDate(endCal);
		Calendar beginCal = (Calendar)entry.getCreationDate().clone();
		beginCal.add(Calendar.SECOND, -1);
		crit.setBeforeDate(beginCal);
		logger.debug("beginCal : " + crit.getBeforeDate().getTime());
		logger.debug("endCal : " + crit.getAfterDate().getTime());
		List<LogEntry> logEntries = logEntryRepository.findByCriteria(crit, null);
		logger.debug("logEntries.size() : " + logEntries.size());
		if (!logEntries.isEmpty() && logEntries.size() == 1) {
			LogEntry logEntry = logEntries.get(0);
			String firstName = logEntry.getActorFirstname();
			String lastName = logEntry.getActorLastname();
			String mail = logEntry.getActorMail();
			lastAuthor.setFirstName(firstName);
			lastAuthor.setLastName(lastName);
			lastAuthor.setMail(mail);
			lastAuthor.setName(getFullName(firstName, lastName, mail));
		}
		logger.debug("lastAuthor " + lastAuthor.toString());
		return lastAuthor;
	}

	protected String getFullName(String firstName, String lastName, String mail) {
		StringBuffer b = new StringBuffer();
		boolean bf = (firstName != null && !firstName.equals(""));
		boolean bl = (lastName != null && !lastName.equals(""));
		if (bf || bl) {
			if (bf) {
				b.append(firstName);
				b.append(" ");
			}
			if (bl) {
				b.append(lastName);
			}
		} else {
			b.append(mail);
		}
		return b.toString();
	}

	protected WorkGroupNode getRootFolder(AccountMto author, Thread workGroup) {
		WorkGroupNode wgnParent = null;
		String workGroupUuid = workGroup.getLsUuid();
		List<WorkGroupNode> results = repository.findByWorkGroupAndParent(workGroupUuid, workGroupUuid);
		wgnParent = DataAccessUtils.singleResult(results);
		if (wgnParent == null) {
			// creation of the root folder.
			wgnParent = new WorkGroupFolder(author, workGroup.getName(), workGroupUuid, workGroupUuid);
			wgnParent.setNodeType(WorkGroupNodeType.ROOT_FOLDER);
			wgnParent.setUuid(UUID.randomUUID().toString());
			wgnParent.setCreationDate(new Date());
			wgnParent.setModificationDate(new Date());
			wgnParent = repository.insert(wgnParent);
		}
		return wgnParent;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<Account> res = (BatchResultContext<Account>) context;
		Account resource = res.getResource();
		if (resource != null) {
			logInfo(batchRunContext, total, position, "Workgroup was upgraded : " + resource.toString());
		} else {
			logInfo(batchRunContext, total, position, "Workgroup upgrade skipped, can not find related workgroup : " + res.toString());
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<Account> res = (BatchResultContext<Account>) exception.getContext();
		Account resource = res.getResource();
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occured while processing domain : "
				+ resource +
				". BatchBusinessException", exception);
	}
}
