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
package com.t3c.anchel.core.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.Validate;

import com.t3c.anchel.core.business.service.StatisticBusinessService;
import com.t3c.anchel.core.domain.constants.StatisticType;
import com.t3c.anchel.core.domain.entities.AbstractDomain;
import com.t3c.anchel.core.domain.entities.Account;
import com.t3c.anchel.core.domain.entities.AccountStatistic;
import com.t3c.anchel.core.domain.entities.Statistic;
import com.t3c.anchel.core.exception.BusinessErrorCode;
import com.t3c.anchel.core.exception.BusinessException;
import com.t3c.anchel.core.rac.StatisticResourceAccessControl;
import com.t3c.anchel.core.service.StatisticService;

public class StatisticServiceImpl extends GenericServiceImpl<Account, Statistic>implements StatisticService {

	private StatisticBusinessService statisticBusinessService;

	public StatisticServiceImpl(StatisticResourceAccessControl rac, StatisticBusinessService statisticBusinessService) {
		super(rac);
		this.statisticBusinessService = statisticBusinessService;
	}

	@Override
	public List<Statistic> findBetweenTwoDates(Account actor, Account owner, AbstractDomain domain, Date beginDate,
			Date endDate, StatisticType statisticType) throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		checkReadPermission(actor, owner, AccountStatistic.class, BusinessErrorCode.STATISTIC_FORBIDDEN, null, domain);
		return statisticBusinessService.findBetweenTwoDates(owner, domain, null, beginDate, endDate, statisticType);
	}
}
