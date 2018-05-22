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
package com.t3c.anchel.repository.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.t3c.anchel.core.domain.constants.ContainerQuotaType;
import com.t3c.anchel.core.domain.entities.AbstractDomain;
import com.t3c.anchel.core.domain.entities.Account;
import com.t3c.anchel.core.domain.entities.ContainerQuota;
import com.t3c.anchel.core.domain.entities.DomainQuota;
import com.t3c.anchel.core.domain.entities.Quota;
import com.t3c.anchel.core.domain.entities.User;
import com.t3c.anchel.core.repository.AccountQuotaRepository;
import com.t3c.anchel.core.repository.AccountRepository;
import com.t3c.anchel.core.repository.ContainerQuotaRepository;
import com.t3c.anchel.core.repository.DomainQuotaRepository;
import com.t3c.anchel.core.repository.UserRepository;
import com.t3c.anchel.service.LoadingServiceTestDatas;

@ContextConfiguration(locations = {
		"classpath:springContext-test.xml",
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})

public class QuotaRepositoryImplTest
		extends AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

	@Autowired
	private AccountQuotaRepository accountQuotaRepository;

	@Autowired
	private DomainQuotaRepository domainQuotaRepository;

	@Autowired
	private ContainerQuotaRepository ensembleQuotaRepository;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	LoadingServiceTestDatas dates;
	private User jane;

	@Before
	public void setUp(){
		this.executeSqlScript("import-tests-stat.sql", false);
		this.executeSqlScript("import-tests-operationHistory.sql", false);
		this.executeSqlScript("import-tests-quota.sql", false);
		dates = new LoadingServiceTestDatas(userRepository);
		dates.loadUsers();
		jane = dates.getUser2();
	}

	@Test
	public void test() { 
		AbstractDomain domain2 = jane.getDomain();
		AbstractDomain domain1 = domain2.getParentDomain();
		Account account1 = jane;
		Quota result = accountQuotaRepository.find(account1);
		assertNotNull(result);
		DomainQuota domain2Quota = domainQuotaRepository.find(domain2);
		ContainerQuota entity = new ContainerQuota(domain2, domain1, domain2Quota, 100, 90, 10, 40, 20, ContainerQuotaType.WORK_GROUP);
		ensembleQuotaRepository.create(entity);
		Quota result3 = domainQuotaRepository.find(domain2);
		assertEquals(new Long(1096), result3.getCurrentValue());
		assertEquals(new Long(1900), result3.getQuota());
		result3 = domainQuotaRepository.find(domain1);
		assertEquals(new Long(1096), result3.getCurrentValue());
		assertEquals(new Long(100), result3.getLastValue());
		assertEquals(new Long(2300), result3.getQuota());
		assertEquals(new Long(2000), result3.getQuotaWarning());
		List<String> listIdentifier = accountQuotaRepository.findDomainUuidByBatchModificationDate(yesterday());
		assertEquals(1, listIdentifier.size());
		logger.debug(" domain identifier : "+listIdentifier.get(0));
	}

	private Date yesterday() {
		GregorianCalendar dateCalender = new GregorianCalendar();
		dateCalender.add(GregorianCalendar.DATE, -1);
		dateCalender.set(GregorianCalendar.HOUR_OF_DAY, 23);
		dateCalender.set(GregorianCalendar.MINUTE, 59);
		dateCalender.set(GregorianCalendar.SECOND, 59);
		return dateCalender.getTime();
	}

}
