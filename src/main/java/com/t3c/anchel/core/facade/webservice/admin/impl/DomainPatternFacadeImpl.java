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
package com.t3c.anchel.core.facade.webservice.admin.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;

import com.t3c.anchel.core.domain.constants.Role;
import com.t3c.anchel.core.domain.entities.User;
import com.t3c.anchel.core.domain.entities.UserLdapPattern;
import com.t3c.anchel.core.exception.BusinessException;
import com.t3c.anchel.core.facade.webservice.admin.DomainPatternFacade;
import com.t3c.anchel.core.facade.webservice.admin.dto.DomainPatternDto;
import com.t3c.anchel.core.service.AccountService;
import com.t3c.anchel.core.service.UserProviderService;

public class DomainPatternFacadeImpl extends AdminGenericFacadeImpl implements DomainPatternFacade {

	private final UserProviderService userProviderService;

	public DomainPatternFacadeImpl(final AccountService accountService, final UserProviderService userProviderService) {
		super(accountService);
		this.userProviderService = userProviderService;
	}

	@Override
	public Set<DomainPatternDto> findAll() throws BusinessException {
		checkAuthentication(Role.SUPERADMIN);
		List<UserLdapPattern> domainPatterns = userProviderService.findAllUserDomainPattern();
		Set<DomainPatternDto> res = new HashSet<DomainPatternDto>();
		for (UserLdapPattern domainPattern : domainPatterns) {
			res.add(new DomainPatternDto(domainPattern));
		}
		return res;
	}

	@Override
	public DomainPatternDto find(String uuid) throws BusinessException {
		checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(uuid, "domain pattern uuid must be set.");
		return new DomainPatternDto(userProviderService.findDomainPattern(uuid));
	}

	@Override
	public Set<DomainPatternDto> findAllModels() throws BusinessException {
		checkAuthentication(Role.SUPERADMIN);
		List<UserLdapPattern> domainPatterns = userProviderService.findAllDomainPattern();
		Set<DomainPatternDto> res = new HashSet<DomainPatternDto>();
		for (UserLdapPattern domainPattern : domainPatterns) {
			res.add(new DomainPatternDto(domainPattern));
		}
		return res;
	}

	@Override
	public DomainPatternDto update(DomainPatternDto domainPatternDto) throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(domainPatternDto.getUuid(), "domain pattern uuid must be set.");
		return new DomainPatternDto(
				userProviderService.updateDomainPattern(actor, new UserLdapPattern(domainPatternDto)));
	}

	@Override
	public DomainPatternDto create(DomainPatternDto domainPatternDto) throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(domainPatternDto.getLabel(), "domain pattern label must be set.");
		return new DomainPatternDto(
				userProviderService.createDomainPattern(actor, new UserLdapPattern(domainPatternDto)));
	}

	@Override
	public DomainPatternDto delete(DomainPatternDto domainPatternDto) throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(domainPatternDto.getUuid(), "domain pattern uuid must be set.");
		UserLdapPattern pattern = userProviderService.deletePattern(actor, domainPatternDto.getUuid());
		return new DomainPatternDto(pattern);
	}

}
