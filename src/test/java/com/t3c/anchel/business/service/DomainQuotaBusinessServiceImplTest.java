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
package com.t3c.anchel.business.service;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.t3c.anchel.core.business.service.DomainQuotaBusinessService;
import com.t3c.anchel.core.domain.entities.AbstractDomain;
import com.t3c.anchel.core.domain.entities.Account;
import com.t3c.anchel.core.domain.entities.DomainQuota;
import com.t3c.anchel.core.domain.entities.User;
import com.t3c.anchel.core.repository.AbstractDomainRepository;
import com.t3c.anchel.core.repository.AccountRepository;
import com.t3c.anchel.core.repository.UserRepository;
import com.t3c.anchel.service.LoadingServiceTestDatas;

@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml" })
public class DomainQuotaBusinessServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

	@Autowired
	private DomainQuotaBusinessService businessService;

	@Autowired
	private AbstractDomainRepository domainRepository;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	LoadingServiceTestDatas datas;

	private AbstractDomain guestDomain;

	private AbstractDomain topDomain;

	@Before
	public void setUp() {
		this.executeSqlScript("import-tests-default-domain-quotas.sql", false);
		this.executeSqlScript("import-tests-domain-quota-updates.sql", false);
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
//		root = userRepository.findByMailAndDomain(LoadingServiceTestDatas.sqlRootDomain, "root@localhost.localdomain");
		guestDomain = domainRepository.findById(LoadingServiceTestDatas.sqlGuestDomain);
		topDomain = domainRepository.findById(LoadingServiceTestDatas.sqlDomain);
	}

	@Test
	public void testCascadeDefaultQuota() {
		Long quotaValue = 698L;

		DomainQuota quota = businessService.find(topDomain);
		DomainQuota dq = new DomainQuota(quota);
		dq.setQuotaOverride(true);
		dq.setQuota(quotaValue);
		dq.setDefaultQuotaOverride(true);
		dq.setDefaultQuota(quotaValue);

		businessService.update(quota, dq);

		quota = businessService.find(guestDomain);
		assertEquals(quotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(quotaValue, quota.getQuota());

		quota = businessService.find(topDomain);
		assertEquals(quotaValue, quota.getDefaultQuota());
		assertEquals(true, quota.getDefaultQuotaOverride());
		assertEquals(quotaValue, quota.getQuota());
		assertEquals(true, quota.getQuotaOverride());
	}
}
