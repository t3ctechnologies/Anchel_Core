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

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.t3c.anchel.core.business.service.TechnicalAccountBusinessService;
import com.t3c.anchel.core.domain.constants.SupportedLanguage;
import com.t3c.anchel.core.domain.entities.AbstractDomain;
import com.t3c.anchel.core.domain.entities.TechnicalAccount;
import com.t3c.anchel.core.exception.BusinessException;
import com.t3c.anchel.core.repository.AbstractDomainRepository;
import com.t3c.anchel.core.repository.TechnicalAccountRepository;
import com.t3c.anchel.core.service.PasswordService;
import com.t3c.anchel.core.utils.HashUtils;

public class TechnicalAccountBusinessServiceImpl implements
		TechnicalAccountBusinessService {

	private final AbstractDomainRepository abstractDomainRepository;

	private final TechnicalAccountRepository technicalAccountRepository;

	private final PasswordService passwordService;

	public TechnicalAccountBusinessServiceImpl(
			AbstractDomainRepository abstractDomainRepository,
			TechnicalAccountRepository technicalAccountRepository,
			PasswordService passwordService) {
		super();
		this.abstractDomainRepository = abstractDomainRepository;
		this.technicalAccountRepository = technicalAccountRepository;
		this.passwordService = passwordService;
	}

	@Override
	public TechnicalAccount find(String uuid) {
		return technicalAccountRepository.findByLsUuid(uuid);
	}

	@Override
	public Set<TechnicalAccount> findAll(String domainId) {
		List<TechnicalAccount> list = technicalAccountRepository.findByDomain(domainId);
		// Dirty hook.
		Set<TechnicalAccount> res = Sets.newHashSet();
		res.addAll(list);
		return res;
	}

	@Override
	public TechnicalAccount create(String domainId, TechnicalAccount account)
			throws BusinessException {
		AbstractDomain domain = abstractDomainRepository.findById(domainId);
		account.setPassword(HashUtils.hashSha1withBase64(account.getPassword()
				.getBytes()));
		account.setCmisLocale(SupportedLanguage.toLanguage(domain.getDefaultTapestryLocale()).getTapestryLocale());
		return this.create(domain, account);
	}

	@Override
	public TechnicalAccount create(AbstractDomain domain,
			TechnicalAccount account) throws BusinessException {
		account.setDomain(domain);
		return technicalAccountRepository.create(account);
	}

	@Override
	public TechnicalAccount update(TechnicalAccount account)
			throws BusinessException {
		return technicalAccountRepository.update(account);
	}

	@Override
	public void delete(TechnicalAccount account) throws BusinessException {
		technicalAccountRepository.delete(account);
	}
}
