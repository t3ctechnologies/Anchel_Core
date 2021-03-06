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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.t3c.anchel.core.domain.constants.LinShareConstants;
import com.t3c.anchel.core.domain.entities.AbstractDomain;
import com.t3c.anchel.core.domain.entities.Account;
import com.t3c.anchel.core.domain.entities.Internal;
import com.t3c.anchel.core.domain.entities.MailingList;
import com.t3c.anchel.core.domain.entities.User;
import com.t3c.anchel.core.exception.BusinessException;
import com.t3c.anchel.core.repository.AbstractDomainRepository;
import com.t3c.anchel.core.repository.AccountRepository;
import com.t3c.anchel.core.repository.MailingListRepository;

@ContextConfiguration(locations = { "classpath:springContext-test.xml",
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml" })
public class MailingListRepositoryImplTest extends
		AbstractJUnit4SpringContextTests {

	// default import.sql
	private static final String DOMAIN_IDENTIFIER = LinShareConstants.rootDomainIdentifier;

	private static final String FIRST_NAME = "first name";
	private static final String LAST_NAME = "last name";
	private static final String MAIL = "mail";
	private static final String UID = "uid";

	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

	@Autowired
	private MailingListRepository mailingListRepository;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	private AbstractDomain domain;

	private User internal;

	private static String identifier = "TestMailingList0";

	@Before
	public void setUp() throws Exception {
		logger.debug("Begin setUp");

		domain = abstractDomainRepository.findById(DOMAIN_IDENTIFIER);
		internal = new Internal(FIRST_NAME, LAST_NAME, MAIL, UID);
		internal.setLocale(domain.getDefaultTapestryLocale());
		internal.setCmisLocale(domain.getDefaultTapestryLocale().toString());
		internal.setDomain(domain);
		accountRepository.create(internal);

		logger.debug("End setUp");
	}

	@After
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");

		accountRepository.delete(internal);

		logger.debug("End tearDown");
	}

	@Test
	public void testCreateMailingList1() throws BusinessException {

		MailingList current = new MailingList();
		current.setIdentifier(identifier);
		current.setOwner(internal);
		current.setDomain(domain);
		current.setPublic(true);
		current.setDescription("yoyo");

		mailingListRepository.create(current);

		Assert.assertNotNull(current.getPersistenceId());

		MailingList myList = mailingListRepository.findByIdentifier(internal,
				identifier);
		Assert.assertTrue(myList != null);

		mailingListRepository.delete(myList);
	}
}
