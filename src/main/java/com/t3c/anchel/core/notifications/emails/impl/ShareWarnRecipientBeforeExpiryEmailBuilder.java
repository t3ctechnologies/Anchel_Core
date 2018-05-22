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

import java.util.List;

import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;
import com.t3c.anchel.core.domain.constants.Language;
import com.t3c.anchel.core.domain.constants.MailContentType;
import com.t3c.anchel.core.domain.entities.MailConfig;
import com.t3c.anchel.core.domain.entities.User;
import com.t3c.anchel.core.domain.objects.MailContainerWithRecipient;
import com.t3c.anchel.core.exception.BusinessException;
import com.t3c.anchel.core.notifications.context.EmailContext;
import com.t3c.anchel.core.notifications.context.ShareWarnRecipientBeforeExpiryEmailContext;
import com.t3c.anchel.core.notifications.dto.MailContact;
import com.t3c.anchel.core.notifications.dto.Share;

public class ShareWarnRecipientBeforeExpiryEmailBuilder extends EmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.SHARE_WARN_RECIPIENT_BEFORE_EXPIRY;
	}

	@Override
	protected MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		ShareWarnRecipientBeforeExpiryEmailContext emailCtx = (ShareWarnRecipientBeforeExpiryEmailContext) context;

		User shareOwner = emailCtx.getShareOwner();
		String linshareURL = getLinShareUrl(shareOwner);

		Share share = null;
		boolean anonymous = emailCtx.isAnonymous();
		if (anonymous) {
			share = new Share(emailCtx.getAnonymousShareEntry());
		} else {
			share = new Share(emailCtx.getShareEntry());
			share.setHref(getRecipientShareLink(linshareURL, share.getUuid()));
		}

		MailConfig cfg = shareOwner.getDomain().getCurrentMailConfiguration();
		Context ctx = new Context(emailCtx.getLocale());
		ctx.setVariable("anonymous", anonymous);
		ctx.setVariable("daysLeft", emailCtx.getDay());
		ctx.setVariable("linshareURL", linshareURL);
		ctx.setVariable("share", share);
		ctx.setVariable("shareOwner", new MailContact(shareOwner));
		ctx.setVariable("shareRecipient", emailCtx.getMailContactRecipient());

		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, getSupportedType(), ctx,
				emailCtx);
		return buildMailContainer;
	}

	@Override
	protected List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		Share share = new Share("a-shared-file.txt", true);
		share.setHref(getRecipientShareLink(fakeLinshareURL, share.getUuid()));
		Context ctx = newFakeContext(language);
		ctx.setVariable("anonymous", false);
		ctx.setVariable("daysLeft", new Integer(8));
		ctx.setVariable("share", share);
		ctx.setVariable("shareOwner", new MailContact("peter.wilson@t3c.io", "Peter", "Wilson"));
		ctx.setVariable("shareRecipient", new MailContact("amy.wolsh@t3c.io", "Amy", "Wolsh"));
		res.add(ctx);
		Context ctx2 = newFakeContext(language);
		ctx2.setVariable("anonymous", true);
		ctx2.setVariable("daysLeft", new Integer(8));
		ctx2.setVariable("share", share);
		ctx2.setVariable("shareOwner", new MailContact("peter.wilson@t3c.io", "Peter", "Wilson"));
		ctx2.setVariable("shareRecipient", new MailContact("unkown@t3c.io"));
		res.add(ctx2);
		return res;
	}
}
