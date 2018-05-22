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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.t3c.anchel.core.domain.entities.ShareEntryGroup;
import com.t3c.anchel.core.domain.entities.User;
import com.t3c.anchel.core.exception.BusinessException;
import com.t3c.anchel.core.facade.webservice.common.dto.ShareEntryGroupDto;
import com.t3c.anchel.core.facade.webservice.delegation.ShareEntryGroupFacade;
import com.t3c.anchel.core.service.AccountService;
import com.t3c.anchel.core.service.ShareEntryGroupService;
import com.t3c.anchel.core.service.UserService;

public class ShareEntryGroupFacadeImpl extends DelegationGenericFacadeImpl implements ShareEntryGroupFacade {

	private final ShareEntryGroupService shareEntryGroupService;

	public ShareEntryGroupFacadeImpl(final AccountService accountService, final UserService userService,
			final ShareEntryGroupService shareEntryGroupService) {
		super(accountService, userService);
		this.shareEntryGroupService = shareEntryGroupService;
	}

	@Override
	public List<ShareEntryGroupDto> findAll(String ownerUuid, boolean full) throws BusinessException {
		Validate.notEmpty(ownerUuid, "Owner uuid must be set.");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		List<ShareEntryGroup> list = shareEntryGroupService.findAll(actor, owner);
		return	ImmutableList.copyOf(Lists.transform(list,
						ShareEntryGroupDto.toDto(full)));
	}

	@Override
	public ShareEntryGroupDto find(String ownerUuid, String uuid, boolean full) throws BusinessException {
		Validate.notEmpty(ownerUuid, "Owner uuid must be set.");
		Validate.notEmpty(uuid, "Shar entry group's uuid must be set.");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		ShareEntryGroup seg = shareEntryGroupService.find(actor, owner, uuid);
		return new ShareEntryGroupDto(seg, full);
	}

	@Override
	public ShareEntryGroupDto update(String ownerUuid, ShareEntryGroupDto shareEntryGroupDto) throws BusinessException {
		Validate.notEmpty(ownerUuid, "Owner uuid must be set.");
		Validate.notNull(shareEntryGroupDto, "Share entry group must be set.");
		Validate.notEmpty(shareEntryGroupDto.getUuid(), "Uuid must be set.");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		ShareEntryGroup seg = shareEntryGroupDto.toObject();
		seg = shareEntryGroupService.update(actor, owner, shareEntryGroupDto.getUuid(), seg);
		return new ShareEntryGroupDto(seg, false);
	}

	@Override
	public ShareEntryGroupDto delete(String ownerUuid, String uuid) throws BusinessException {
		Validate.notEmpty(ownerUuid, "Owner uuid must be set.");
		Validate.notEmpty(uuid, "Shar entry group's uuid must be set.");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		ShareEntryGroup seg = shareEntryGroupService.delete(actor, owner, uuid);
		return new ShareEntryGroupDto(seg, false);
	}
}
