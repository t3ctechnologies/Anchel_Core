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
package com.t3c.anchel.mongo.entities.logs;

import javax.xml.bind.annotation.XmlRootElement;

import com.t3c.anchel.core.domain.constants.AuditLogEntryType;
import com.t3c.anchel.core.domain.constants.LogAction;
import com.t3c.anchel.core.domain.entities.Account;
import com.t3c.anchel.mongo.entities.mto.AccountMto;

@XmlRootElement
public class AuthenticationAuditLogEntryUser extends AuditLogEntryUser {

	protected String message;

	protected String login;

	protected String domainIdentifier;

	public AuthenticationAuditLogEntryUser() {
		super();
	}

	public AuthenticationAuditLogEntryUser(Account authUser, LogAction action, AuditLogEntryType type, String message) {
		super(new AccountMto(authUser), new AccountMto(authUser), action, type, authUser.getLsUuid());
		this.message = message;
	}

	public AuthenticationAuditLogEntryUser(String login, String domainIdentifier, LogAction action,
			AuditLogEntryType type, String message) {
		this.login = login;
		this.domainIdentifier = domainIdentifier;
		this.message = message;
		this.action = action;
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getDomainIdentifier() {
		return domainIdentifier;
	}

	public void setDomainIdentifier(String domainIdentifier) {
		this.domainIdentifier = domainIdentifier;
	}

}
