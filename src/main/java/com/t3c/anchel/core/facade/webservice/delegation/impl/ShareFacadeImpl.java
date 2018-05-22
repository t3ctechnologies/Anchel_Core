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

import java.util.Set;

import org.apache.commons.lang.Validate;

import com.google.common.collect.Sets;
import com.t3c.anchel.core.domain.entities.Entry;
import com.t3c.anchel.core.domain.entities.User;
import com.t3c.anchel.core.domain.objects.ShareContainer;
import com.t3c.anchel.core.exception.BusinessErrorCode;
import com.t3c.anchel.core.exception.BusinessException;
import com.t3c.anchel.core.facade.webservice.delegation.ShareFacade;
import com.t3c.anchel.core.facade.webservice.delegation.dto.ShareCreationDto;
import com.t3c.anchel.core.facade.webservice.delegation.dto.ShareDto;
import com.t3c.anchel.core.service.AccountService;
import com.t3c.anchel.core.service.ShareService;
import com.t3c.anchel.core.service.UserService;

public class ShareFacadeImpl extends DelegationGenericFacadeImpl implements
		ShareFacade {

	private final ShareService shareService;

	public ShareFacadeImpl(
			final AccountService accountService,
			final ShareService shareService,
			final UserService userService) {
		super(accountService, userService);
		this.shareService = shareService;
	}

	@Override
	public Set<ShareDto> create(String ownerUuid, ShareCreationDto createDto) {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		if ((actor.isGuest() && !actor.getCanUpload()))
			throw new BusinessException(
					BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		ShareContainer sc = new ShareContainer();
		sc.addDocumentUuid(createDto.getDocuments());
		sc.setSubject(createDto.getSubject());
		sc.setMessage(createDto.getMessage());
		sc.setSecured(createDto.getSecured());
		sc.setExpiryDate(createDto.getExpirationDate());
		sc.addGenericUserDto(createDto.getRecipients());
		sc.setAcknowledgement(createDto.isCreationAcknowledgement());
		sc.setSharingNote(createDto.getSharingNote());
		Set<Entry> shares = shareService.create(actor, owner, sc);
		Set<ShareDto> sharesDto = Sets.newHashSet();
		for (Entry entry : shares) {
			sharesDto.add(ShareDto.getSentShare(entry));
		}
		return sharesDto;
	}
}
