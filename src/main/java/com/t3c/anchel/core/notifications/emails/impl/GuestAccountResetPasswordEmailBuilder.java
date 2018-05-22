/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
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
package com.t3c.anchel.core.notifications.emails.impl;

import java.util.Formatter;
import java.util.List;

import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;
import com.t3c.anchel.core.domain.constants.Language;
import com.t3c.anchel.core.domain.constants.MailContentType;
import com.t3c.anchel.core.domain.entities.Guest;
import com.t3c.anchel.core.domain.entities.MailConfig;
import com.t3c.anchel.core.domain.objects.MailContainerWithRecipient;
import com.t3c.anchel.core.exception.BusinessException;
import com.t3c.anchel.core.notifications.context.EmailContext;
import com.t3c.anchel.core.notifications.context.GuestAccountResetPasswordEmailContext;
import com.t3c.anchel.core.notifications.dto.MailContact;

public class GuestAccountResetPasswordEmailBuilder extends EmailBuilder {

	protected String urlTemplateForGuestReset;

	public void setUrlTemplateForGuestReset(String urlTemplateForGuestReset) {
		this.urlTemplateForGuestReset = urlTemplateForGuestReset;
	}

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.GUEST_ACCOUNT_RESET_PASSWORD_LINK;
	}

	@Override
	public MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		GuestAccountResetPasswordEmailContext emailCtx = (GuestAccountResetPasswordEmailContext) context;
		Guest guest = emailCtx.getGuest();
		String linshareURL = getLinShareUrl(guest);
		MailConfig cfg = guest.getDomain().getCurrentMailConfiguration();
		Context ctx = new Context(emailCtx.getLocale());
		ctx.setVariable("customMessage", null);
		ctx.setVariable("guest", new MailContact(guest));
		ctx.setVariable("guestExpirationDate", guest.getExpirationDate());
		ctx.setVariable("linshareURL", linshareURL);
		ctx.setVariable("resetLink", getResetLink(linshareURL, emailCtx.getResetPasswordTokenUuid()));
		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, getSupportedType(), ctx,
				emailCtx);
		return buildMailContainer;
	}

	@Override
	public List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		Context ctx = newFakeContext(language);
		ctx.setVariable("customMessage", null);
		ctx.setVariable("guest", new MailContact("amy.wolsh@t3c.io", "Amy", "Wolsh"));
		ctx.setVariable("guestExpirationDate", getFakeExpirationDate());
		ctx.setVariable("resetLink", getResetLink(fakeLinshareURL, "cb1443d0-a34f-4d0b-92e4-c19d4eeb7fae"));
		res.add(ctx);
		return res;
	}

	protected String getResetLink(String linshareURL, String token) {
		StringBuilder sb = new StringBuilder();
		sb.append(linshareURL);
		Formatter formatter = new Formatter(sb);
		formatter.format(urlTemplateForGuestReset, token);
		formatter.close();
		return sb.toString();
	}
}
