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

package com.t3c.anchel.core.batches.impl;

import java.util.List;

import com.google.common.collect.Lists;
import com.t3c.anchel.core.batches.EnableUploadRequestBatch;
import com.t3c.anchel.core.domain.constants.UploadRequestStatus;
import com.t3c.anchel.core.domain.entities.Account;
import com.t3c.anchel.core.domain.entities.SystemAccount;
import com.t3c.anchel.core.domain.entities.UploadRequest;
import com.t3c.anchel.core.domain.entities.UploadRequestUrl;
import com.t3c.anchel.core.domain.entities.User;
import com.t3c.anchel.core.domain.objects.MailContainerWithRecipient;
import com.t3c.anchel.core.exception.BatchBusinessException;
import com.t3c.anchel.core.exception.BusinessException;
import com.t3c.anchel.core.job.quartz.BatchRunContext;
import com.t3c.anchel.core.job.quartz.ResultContext;
import com.t3c.anchel.core.job.quartz.UploadRequestBatchResultContext;
import com.t3c.anchel.core.notifications.service.MailBuildingService;
import com.t3c.anchel.core.repository.AccountRepository;
import com.t3c.anchel.core.service.NotifierService;
import com.t3c.anchel.core.service.UploadRequestService;

public class EnableUploadRequestBatchImpl extends GenericBatchImpl implements EnableUploadRequestBatch {

	private final UploadRequestService uploadRequestService;
	private final MailBuildingService mailBuildingService;
	private final NotifierService notifierService;

	public EnableUploadRequestBatchImpl(
			AccountRepository<Account> accountRepository,
			final UploadRequestService uploadRequestService,
			final MailBuildingService mailBuildingService,
			final NotifierService notifierService) {
		super(accountRepository);
		this.uploadRequestService = uploadRequestService;
		this.mailBuildingService = mailBuildingService;
		this.notifierService = notifierService;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		SystemAccount account = getSystemAccount();
		logger.info(getClass().toString() + " job starting ...");
		List<String> entries = uploadRequestService.findUnabledRequests(account);
		logger.info(entries.size()
				+ " Upload Request(s) have been found to be enabled");
		return entries;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		List<MailContainerWithRecipient> notifications = Lists.newArrayList();
		SystemAccount account = getSystemAccount();
		UploadRequest r = uploadRequestService.findRequestByUuid(account, null, identifier);
		ResultContext context = new UploadRequestBatchResultContext(r);
		logInfo(batchRunContext, total, position, "processing uplaod request : ", r.getUuid());
		r.updateStatus(UploadRequestStatus.STATUS_ENABLED);
		r = uploadRequestService.updateRequest(account, r.getOwner(), r);
		for (UploadRequestUrl u: r.getUploadRequestURLs()) {
			notifications.add(mailBuildingService.buildActivateUploadRequest((User) r.getOwner(), u));
		}
		notifierService.sendNotification(notifications);
		logger.error("Fail to update upload request status of the request : " + r.getUuid());
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		UploadRequestBatchResultContext uploadRequestContext = (UploadRequestBatchResultContext) context;
		UploadRequest r = uploadRequestContext.getResource();
		logInfo(batchRunContext, total, position, "The Upload Request "
				+ r.getUuid()
				+ " has been successfully enabled.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		UploadRequestBatchResultContext uploadRequestContext = (UploadRequestBatchResultContext) exception.getContext();
		UploadRequest r = uploadRequestContext.getResource();
		logError(
				total,
				position,
				"Enabling upload request has failed : "
						+ r.getUuid(), batchRunContext);
		logger.error(
				"Error occured while enabling upload request "
						+ r.getUuid()
						+ ". BatchBusinessException ", exception);
	}

	@Override
	public void terminate(BatchRunContext batchRunContext, List<String> all, long errors, long unhandled_errors, long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success
				+ " upload request(s) have been enabled.");
		if (errors > 0) {
			logger.error(errors
					+ " upload request(s) failed to be enabled.");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors
					+ " upload request(s) failed to be enabled(unhandled error).");
		}
		logger.info("EnableUploadRequestBatchImpl job terminated.");
	}
}
