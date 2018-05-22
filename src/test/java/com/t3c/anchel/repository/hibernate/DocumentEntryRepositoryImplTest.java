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
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.t3c.anchel.core.domain.constants.LinShareTestConstants;
import com.t3c.anchel.core.domain.entities.AbstractDomain;
import com.t3c.anchel.core.domain.entities.Document;
import com.t3c.anchel.core.domain.entities.DocumentEntry;
import com.t3c.anchel.core.domain.entities.Guest;
import com.t3c.anchel.core.domain.entities.User;
import com.t3c.anchel.core.exception.BusinessException;
import com.t3c.anchel.core.repository.AbstractDomainRepository;
import com.t3c.anchel.core.repository.DocumentEntryRepository;
import com.t3c.anchel.core.repository.DocumentRepository;
import com.t3c.anchel.core.repository.UserRepository;
import com.t3c.anchel.service.LoadingServiceTestDatas;

@ContextConfiguration(locations={"classpath:springContext-test.xml", 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class DocumentEntryRepositoryImplTest  extends AbstractTransactionalJUnit4SpringContextTests{

	private static final Logger logger = LoggerFactory.getLogger(DocumentEntryRepositoryImplTest.class);
	
	
    private static final String FIRST_NAME = "first name";
    private static final String LAST_NAME = "last name";
    private static final String MAIL = "mail";
    private static final String PASSWORD = "password";

    
    private final String identifier = "docId";
    private final String type = "doctype";
    private final long fileSize = 1L;
    
    
    // Members
    private AbstractDomain rootDomain;
    private User user;
    private Document document;
    
    //Services
	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;
	

	@Autowired
	private DocumentRepository documentRepository;
	
	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	
	@Autowired
	private DocumentEntryRepository documentEntryRepository;
	
	@Before
	public void setUp() throws Exception {
		logger.debug("Begin setUp");
		rootDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlRootDomain);
		user = new Guest(FIRST_NAME, LAST_NAME, MAIL, PASSWORD, true, "comment");
		user.setDomain(rootDomain);
		user.setLocale(rootDomain.getDefaultTapestryLocale());
		user.setCmisLocale(rootDomain.getDefaultTapestryLocale().toString());
		
		userRepository.create(user);
		
		document = new Document(identifier, type, fileSize);
		documentRepository.create(document);
		
		logger.debug("End setUp");
	}

	@After
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");
		documentRepository.delete(document);
		userRepository.delete(user);
		logger.debug("End tearDown");
	}
	
	@Test
	public void testCreateDocumentEntry() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		DocumentEntry d = new DocumentEntry(user, "name", "comment", document);
		documentEntryRepository.create(d);
		documentEntryRepository.delete(d);
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	

}
