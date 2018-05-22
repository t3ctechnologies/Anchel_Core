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
package com.t3c.anchel.core.business.service.impl;

import java.util.Date;
import java.util.List;

import com.t3c.anchel.core.business.service.ThreadWeeklyStatisticBusinessService;
import com.t3c.anchel.core.domain.entities.Account;
import com.t3c.anchel.core.domain.entities.Thread;
import com.t3c.anchel.core.domain.entities.ThreadWeeklyStat;
import com.t3c.anchel.core.exception.BusinessException;
import com.t3c.anchel.core.repository.ThreadDailyStatisticRepository;
import com.t3c.anchel.core.repository.ThreadWeeklyStatisticRepository;

public class ThreadWeeklyStatBusinessServiceImpl implements ThreadWeeklyStatisticBusinessService {

	private final ThreadWeeklyStatisticRepository repository;
	private final ThreadDailyStatisticRepository threadDailyStatRepository;

	public ThreadWeeklyStatBusinessServiceImpl(ThreadWeeklyStatisticRepository repository,
			ThreadDailyStatisticRepository threadDailyStatBusinessService) {
		this.repository = repository;
		this.threadDailyStatRepository = threadDailyStatBusinessService;
	}

	@Override
	public ThreadWeeklyStat create(Thread thread, Date beginDate, Date endDate) throws BusinessException {
		Long actualOperationSum = threadDailyStatRepository.sumOfActualOperationSum(null, thread, beginDate, endDate);
		Long operationCount = threadDailyStatRepository.sumOfOperationCount(null, thread, beginDate, endDate);
		Long createOperationSum = threadDailyStatRepository.sumOfCreateOperationSum(null, thread, beginDate, endDate);
		Long createOperationCount = threadDailyStatRepository.sumOfCreateOperationCount(null, thread, beginDate,
				endDate);
		Long deleteOperationSum = threadDailyStatRepository.sumOfDeleteOperationSum(null, thread, beginDate, endDate);
		Long deleteOperationCount = threadDailyStatRepository.sumOfDeleteOperationCount(null, thread, beginDate,
				endDate);
		Long diffOperationSum = threadDailyStatRepository.sumOfDiffOperationSum(null, thread, beginDate, endDate);
		ThreadWeeklyStat entity = new ThreadWeeklyStat(thread, thread.getDomain(), thread.getDomain().getParentDomain(),
				operationCount, deleteOperationCount, createOperationCount, createOperationSum, deleteOperationSum,
				diffOperationSum, actualOperationSum);
		entity.setStatisticDate(endDate);
		entity = repository.create(entity);
		return entity;
	}

	@Override
	public List<ThreadWeeklyStat> findBetweenTwoDates(Thread thread, Date beginDate, Date endDate) {
		return repository.findBetweenTwoDates(thread, null, null, beginDate, endDate, null);
	}

	@Override
	public void deleteBeforeDate(Date date) {
		repository.deleteBeforeDate(date);
	}

	@Override
	public List<Account> findAccountBetweenTwoDates(Date beginDate, Date endDate) {
		return repository.findAccountBetweenTwoDates(beginDate, endDate);
	}

	@Override
	public List<String> findUuidAccountBetweenTwoDates(Date beginDate, Date endDate) {
		return repository.findUuidAccountBetweenTwoDates(beginDate, endDate);
	}

}
