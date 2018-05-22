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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.t3c.anchel.core.business.service.DomainBusinessService;
import com.t3c.anchel.core.business.service.DomainPermissionBusinessService;
import com.t3c.anchel.core.domain.entities.AbstractDomain;
import com.t3c.anchel.core.domain.entities.Account;
import com.t3c.anchel.core.domain.entities.Guest;
import com.t3c.anchel.core.domain.entities.UploadRequest;
import com.t3c.anchel.core.domain.entities.User;

public class DomainPermissionBusinessServiceImpl implements
		DomainPermissionBusinessService {

	private final Logger logger = LoggerFactory
			.getLogger(DomainPermissionBusinessServiceImpl.class);

	private final DomainBusinessService domainBusinessService;

	public DomainPermissionBusinessServiceImpl(
			final DomainBusinessService domainBusinessService) {
		super();
		this.domainBusinessService = domainBusinessService;
	}

	@Override
	public boolean isAdminforThisDomain(Account actor, AbstractDomain domain) {
		if (!(actor.hasSuperAdminRole() || actor.hasSystemAccountRole())) {
			if (!domain.isManagedBy(actor)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isAdminForThisUser(Account actor, User user) {
		return isAdminforThisDomain(actor, user.getDomain())
				|| isOwner(actor, user);
	}

	@Override
	public boolean isAdminForThisUploadRequest(Account actor, UploadRequest request) {
		return isAdminforThisDomain(actor, request.getAbstractDomain())
				|| isOwner(actor, request);
	}

	@Override
	public List<AbstractDomain> getMyAdministredDomains(Account actor) {
		if (!(actor.hasAdminRole() || actor.hasSuperAdminRole())) {
			return Lists.newArrayList();
		}
		return findRecursivelyDomains(actor.getDomain());
	}

	private List<AbstractDomain> findRecursivelyDomains(AbstractDomain root) {
		List<AbstractDomain> list = Lists.newArrayList();
		list.add(root);
		for (AbstractDomain sub : root.getSubdomain()) {
			list.addAll(findRecursivelyDomains(sub));
		}
		return list;
	}

	private boolean isOwner(Account actor, User guest) {
		return guest instanceof Guest && guest.getOwner().equals(actor);
	}

	private boolean isOwner(Account actor, UploadRequest request) {
		return request.getOwner().equals(actor);
	}
}
