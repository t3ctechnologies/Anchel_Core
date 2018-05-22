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

import java.util.Date;
import java.util.List;

import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;
import com.t3c.anchel.core.domain.constants.Language;
import com.t3c.anchel.core.domain.constants.MailContentType;
import com.t3c.anchel.core.domain.entities.MailConfig;
import com.t3c.anchel.core.domain.entities.ShareEntry;
import com.t3c.anchel.core.domain.entities.User;
import com.t3c.anchel.core.domain.objects.MailContainerWithRecipient;
import com.t3c.anchel.core.exception.BusinessException;
import com.t3c.anchel.core.notifications.context.EmailContext;
import com.t3c.anchel.core.notifications.context.ShareFileShareDeletedEmailContext;
import com.t3c.anchel.core.notifications.dto.MailContact;
import com.t3c.anchel.core.notifications.dto.Share;

public class ShareFileShareDeletedEmailBuilder extends EmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.SHARE_FILE_SHARE_DELETED;
	}

	@Override
	protected MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		ShareFileShareDeletedEmailContext emailCtx = (ShareFileShareDeletedEmailContext) context;

		ShareEntry shareEntry = emailCtx.getShareEntry();
		User shareOwner = (User) shareEntry.getEntryOwner();
		User shareRecipient = shareEntry.getRecipient();
		String linshareURL = getLinShareUrl(shareOwner);

		MailConfig cfg = shareOwner.getDomain().getCurrentMailConfiguration();
		Context ctx = new Context(emailCtx.getLocale());
		ctx.setVariable("shareOwner", new MailContact(shareOwner));
		ctx.setVariable("shareRecipient", new MailContact(shareRecipient));
		ctx.setVariable("share", new Share(shareEntry));
		ctx.setVariable("linshareURL", linshareURL);
		ctx.setVariable("deletionDate", new Date());

		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, getSupportedType(), ctx,
				emailCtx);
		return buildMailContainer;
	}

	@Override
	protected List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		Context ctx = newFakeContext(language);
		ctx.setVariable("shareOwner", new MailContact("peter.wilson@t3c.io", "Peter", "Wilson"));
		ctx.setVariable("shareRecipient", new MailContact("amy.wolsh@t3c.io", "Amy", "Wolsh"));
		ctx.setVariable("share", new Share("a-shared-file.txt", true));
		ctx.setVariable("deletionDate", new Date());
		res.add(ctx);
		return res;
	}

}
