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

package com.t3c.anchel.core.facade.webservice.delegation.impl;

import java.util.List;

import org.apache.commons.lang.Validate;

import com.google.common.collect.Lists;
import com.t3c.anchel.core.domain.entities.Thread;
import com.t3c.anchel.core.domain.entities.ThreadMember;
import com.t3c.anchel.core.domain.entities.User;
import com.t3c.anchel.core.exception.BusinessException;
import com.t3c.anchel.core.facade.webservice.common.dto.WorkGroupMemberDto;
import com.t3c.anchel.core.facade.webservice.delegation.ThreadMemberFacade;
import com.t3c.anchel.core.service.AccountService;
import com.t3c.anchel.core.service.ThreadService;
import com.t3c.anchel.core.service.UserService;

public class ThreadMemberFacadeImpl extends DelegationGenericFacadeImpl
		implements ThreadMemberFacade {

	private final ThreadService threadService;

	private final UserService userService;

	public ThreadMemberFacadeImpl(
			final AccountService accountService,
			final UserService userService,
			final ThreadService threadService) {
		super(accountService, userService);
		this.threadService = threadService;
		this.userService = userService;
	}

	@Override
	public List<WorkGroupMemberDto> findAll(String ownerUuid, String threadUuid)
			throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		Thread thread = threadService.find(actor, owner, threadUuid);
		List<WorkGroupMemberDto> res = Lists.newArrayList();

		for (ThreadMember m : threadService.findAllThreadMembers(actor, owner, thread)) {
			res.add(new WorkGroupMemberDto(m));
		}
		return res;
	}

	@Override
	public WorkGroupMemberDto create(String ownerUuid, String threadUuid,
			String domainId, String mail, boolean readonly, boolean admin)
			throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(domainId, "Missing required domain id");
		Validate.notEmpty(mail, "Missing required mail");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		User user = userService.findOrCreateUser(mail, domainId);
		Thread thread = threadService.find(actor, owner, threadUuid);
		return new WorkGroupMemberDto(threadService.addMember(actor, owner,
				thread, user, admin, !readonly));
	}

	@Override
	public WorkGroupMemberDto update(String ownerUuid, String threadUuid,
			WorkGroupMemberDto threadMember) {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notNull(threadMember, "Missing required thread member");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		return new WorkGroupMemberDto(threadService.updateMember(actor, owner,
				threadUuid, threadMember.getUserUuid(), threadMember.isAdmin(),
				!threadMember.isReadonly()));
	}

	@Override
	public WorkGroupMemberDto delete(String ownerUuid, String threadUuid, String userUuid)
			throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(userUuid, "Missing required user uuid");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		ThreadMember member = threadService.deleteMember(actor, owner, threadUuid, userUuid);
		return new WorkGroupMemberDto(member);
	}

}
