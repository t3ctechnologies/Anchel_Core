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
import com.t3c.anchel.core.domain.entities.UploadRequest;
import com.t3c.anchel.core.domain.entities.UploadRequestUrl;
import com.t3c.anchel.core.domain.entities.User;
import com.t3c.anchel.core.domain.objects.MailContainerWithRecipient;
import com.t3c.anchel.core.exception.BusinessException;
import com.t3c.anchel.core.notifications.context.EmailContext;
import com.t3c.anchel.core.notifications.context.UploadRequestUnavailableSpaceEmailContext;
import com.t3c.anchel.core.notifications.dto.Document;
import com.t3c.anchel.core.notifications.dto.MailContact;

public class UploadRequestUnavailableSpaceEmailBuilder extends GenericUploadRequestEmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.UPLOAD_REQUEST_UNAVAILABLE_SPACE;
	}

	@Override
	protected MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		UploadRequestUnavailableSpaceEmailContext emailCtx = (UploadRequestUnavailableSpaceEmailContext) context;

		User owner = emailCtx.getOwner();
		boolean warnOwner = emailCtx.isWarnOwner();
		UploadRequestUrl requestUrl = emailCtx.getRequestUrl();
		UploadRequest request = emailCtx.getUploadRequest();

		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();

		List<MailContact> recipients = getRecipients(request);
		List<Document> documents = getDocuments(warnOwner, request, requestUrl);

		Context ctx = newTmlContext(emailCtx);
		ctx.setVariable("body", request.getUploadRequestGroup().getBody());
		ctx.setVariable("documents", documents);
		ctx.setVariable("documentsCount", documents.size());
		ctx.setVariable("maxDepositSize", request.getMaxDepositSize());
		ctx.setVariable("recipients", recipients);
		ctx.setVariable("recipientsCount", recipients.size());

		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, getSupportedType(), ctx,
				emailCtx);
		return buildMailContainer;
	}

	@Override
	protected List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		res.add(getFake(language));
		return res;
	}

	private Context getFake(Language language) {
		List<MailContact> recipients = Lists.newArrayList();
		recipients.add(new MailContact("unknown@t3c.io"));
		recipients.add(new MailContact("unknown2@t3c.io"));

		List<Document> documents = Lists.newArrayList();
		Document document = getNewFakeDocument("a-upload-request-file.txt", fakeLinshareURL);
		document.setSize(65985L);
		document.setCreationDate(new Date());
		document.setHref(fakeLinshareURL + "/#ownerlink");
		documents.add(document);
		document = getNewFakeDocument("my-upload-request-file.txt", fakeLinshareURL);
		document.setSize(659L);
		document.setCreationDate(new Date());
		document.setHref(fakeLinshareURL + "/#ownerlink");
		document.setMine(true);
		documents.add(document);

		Context ctx = newFakeContext(language, true);
		ctx.setVariable("body", "upload request body message");
		ctx.setVariable("documents", documents);
		ctx.setVariable("documentsCount", documents.size());
		ctx.setVariable("maxDepositSize", new Long(566135489));
		ctx.setVariable("recipients", recipients);
		ctx.setVariable("recipientsCount", recipients.size());

		return ctx;
	}
}
