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

import com.t3c.anchel.core.domain.constants.Role;
import com.t3c.anchel.core.domain.entities.MailConfig;
import com.t3c.anchel.core.domain.entities.MailFooter;
import com.t3c.anchel.core.domain.entities.MailFooterLang;
import com.t3c.anchel.core.domain.entities.User;
import com.t3c.anchel.core.exception.BusinessErrorCode;
import com.t3c.anchel.core.exception.BusinessException;
import com.t3c.anchel.core.facade.webservice.admin.MailFooterLangFacade;
import com.t3c.anchel.core.facade.webservice.admin.dto.MailFooterLangDto;
import com.t3c.anchel.core.service.AccountService;
import com.t3c.anchel.core.service.MailConfigService;

public class MailFooterLangFacadeImpl extends AdminGenericFacadeImpl implements
		MailFooterLangFacade {

	private final MailConfigService mailConfigService;

	public MailFooterLangFacadeImpl(final AccountService accountService,
			final MailConfigService mailConfigService) {
		super(accountService);
		this.mailConfigService = mailConfigService;
	}

	@Override
	public MailFooterLangDto find(String uuid) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		return new MailFooterLangDto(findFooterLang(actor, uuid), getOverrideReadonly());
	}

	@Override
	public MailFooterLangDto create(MailFooterLangDto dto) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		MailFooterLang footerLang = new MailFooterLang();
		transform(actor, footerLang, dto);
		return new MailFooterLangDto(mailConfigService.createFooterLang(actor, footerLang));
	}

	@Override
	public MailFooterLangDto update(MailFooterLangDto dto) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		MailFooterLang footerLang = findFooterLang(actor, dto.getUuid());

		transform(actor, footerLang, dto);
		return new MailFooterLangDto(mailConfigService.updateFooterLang(actor, footerLang));
	}

	@Override
	public void delete(String uuid) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		mailConfigService.deleteFooterLang(actor, uuid);
	}

	/*
	 * Helpers
	 */

	private void transform(User actor, MailFooterLang footerLang,
			MailFooterLangDto dto) throws BusinessException {
		footerLang.setLanguage(dto.getLanguage().toInt());
		footerLang.setMailConfig(findConfig(actor, dto.getMailConfig()));
		footerLang.setMailFooter(findFooter(actor, dto.getMailFooter()));
	}

	private MailConfig findConfig(User actor, String uuid)
			throws BusinessException {
		MailConfig mailConfig = mailConfigService.findConfigByUuid(actor, uuid);

		if (mailConfig == null)
			throw new BusinessException(BusinessErrorCode.MAILCONFIG_NOT_FOUND,
					uuid + " not found.");
		return mailConfig;
	}

	private MailFooter findFooter(User actor, String uuid)
			throws BusinessException {
		MailFooter mailFooter = mailConfigService.findFooterByUuid(actor, uuid);

		if (mailFooter == null)
			throw new BusinessException(BusinessErrorCode.MAILFOOTER_NOT_FOUND,
					uuid + " not found.");
		return mailFooter;
	}

	private MailFooterLang findFooterLang(User actor, String uuid)
			throws BusinessException {
		MailFooterLang mailFooterLang = mailConfigService.findFooterLangByUuid(
				actor, uuid);

		if (mailFooterLang == null)
			throw new BusinessException(
					BusinessErrorCode.MAILFOOTERLANG_NOT_FOUND, uuid
							+ " not found.");
		return mailFooterLang;
	}

	private boolean getOverrideReadonly() {
		return mailConfigService.isTemplatingOverrideReadonlyMode();
	}
}
