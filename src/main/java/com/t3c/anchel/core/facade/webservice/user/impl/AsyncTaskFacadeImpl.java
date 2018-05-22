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

package com.t3c.anchel.core.facade.webservice.user.impl;

import java.util.List;

import org.apache.commons.lang.Validate;

import com.google.common.collect.Lists;
import com.t3c.anchel.core.domain.constants.AsyncTaskType;
import com.t3c.anchel.core.domain.constants.UpgradeTaskType;
import com.t3c.anchel.core.domain.entities.AsyncTask;
import com.t3c.anchel.core.domain.entities.UpgradeTask;
import com.t3c.anchel.core.domain.entities.User;
import com.t3c.anchel.core.exception.BusinessException;
import com.t3c.anchel.core.facade.webservice.admin.dto.UpgradeTaskDto;
import com.t3c.anchel.core.facade.webservice.common.dto.AsyncTaskDto;
import com.t3c.anchel.core.facade.webservice.user.AsyncTaskFacade;
import com.t3c.anchel.core.service.AccountService;
import com.t3c.anchel.core.service.AsyncTaskService;
import com.t3c.anchel.core.service.UpgradeTaskService;

public class AsyncTaskFacadeImpl extends UserGenericFacadeImp implements
		AsyncTaskFacade {

	protected final AsyncTaskService service;

	protected UpgradeTaskService upgradeTaskService;

	public AsyncTaskFacadeImpl(AccountService accountService,
			UpgradeTaskService upgradeTaskService,
			AsyncTaskService asyncTaskService) {
		super(accountService);
		this.service = asyncTaskService;
		this.upgradeTaskService = upgradeTaskService;
	}

	@Override
	public AsyncTaskDto create(Long size, Long transfertDuration,
			String fileName, Integer frequency, AsyncTaskType taskType) {
		User actor = checkAuthentication();
		AsyncTask task =  new AsyncTask(size, transfertDuration, fileName, frequency, taskType);
		return new AsyncTaskDto(service.create(actor, actor, task));
	}

	@Override
	public AsyncTaskDto create(String fileName, Integer frequency,
			AsyncTaskType taskType) {
		User actor = checkAuthentication();
		AsyncTask task =  new AsyncTask(fileName, frequency, taskType);
		return new AsyncTaskDto(service.create(actor, actor, task));
	}

	@Override
	public AsyncTaskDto create(String fileName, AsyncTaskType taskType) {
		User actor = checkAuthentication();
		AsyncTask task =  new AsyncTask(fileName, null, taskType);
		return new AsyncTaskDto(service.create(actor, actor, task));
	}

	@Override
	public AsyncTaskDto create(UpgradeTaskDto upgradeTaskDto, AsyncTaskType taskType) {
		User actor = checkAuthentication();
		UpgradeTask upgradeTask = upgradeTaskService.find(actor, upgradeTaskDto.getIdentifier());
		AsyncTask task =  new AsyncTask(upgradeTask, taskType);
		return new AsyncTaskDto(service.create(actor, actor, task));
	}

	@Override
	public AsyncTaskDto find(String uuid) {
		User actor = checkAuthentication();
		Validate.notEmpty(uuid, "Missing uuid");
		AsyncTask task = service.find(actor, actor, uuid);
		return new AsyncTaskDto(task);
	}

	@Override
	public AsyncTaskDto fail(AsyncTaskDto asyncTask, Exception e) {
		User actor = checkAuthentication();
		Validate.notNull(asyncTask, "Missing AsyncTask");
		Validate.notEmpty(asyncTask.getUuid(), "Missing AsyncTask uuid");
		AsyncTask task = null;
		if (e instanceof BusinessException) {
			BusinessException eb = (BusinessException) e;
			task = service.fail(actor, actor, asyncTask.getUuid(),
					eb.getErrorCode().getCode(), eb.getErrorCode().name(), eb.getMessage());
		} else {
			String message = e.getMessage();
			if (message == null) {
				message = e.toString();
			}
			task = service.fail(actor, actor, asyncTask.getUuid(), message);
		}
		return new AsyncTaskDto(task);
	}

	@Override
	public List<AsyncTaskDto> findAll(UpgradeTaskType upgradeTaskIdentifier) {
		User actor = checkAuthentication();
		Validate.notNull(upgradeTaskIdentifier, "Missing AsyncTask identifier");
		UpgradeTask upgradeTask = upgradeTaskService.find(actor, upgradeTaskIdentifier);
		List<AsyncTask> findAll = service.findAll(actor, actor, upgradeTask);
		List<AsyncTaskDto> res = Lists.newArrayList();
		for (AsyncTask asyncTask : findAll) {
			res.add(new AsyncTaskDto(asyncTask));
		}
		return res;
	}
}
