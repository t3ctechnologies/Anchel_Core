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
package com.t3c.anchel.core.facade.webservice.common.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.t3c.anchel.core.domain.constants.Language;
import com.t3c.anchel.core.domain.constants.SupportedLanguage;
import com.t3c.anchel.core.domain.entities.Account;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "Account")
@ApiModel(value = "Account", description = "")
public class AccountDto {

	@ApiModelProperty(value = "Uuid")
	protected String uuid;

	@ApiModelProperty(value = "CreationDate")
	protected Date creationDate;

	@ApiModelProperty(value = "ModificationDate")
	protected Date modificationDate;

	@ApiModelProperty(value = "Locale")
	protected SupportedLanguage locale;

	@ApiModelProperty(value = "ExternalMailLocale")
	protected Language externalMailLocale;

	@ApiModelProperty(value = "Domain")
	protected String domain;

	public AccountDto() {
		super();
	}

	public AccountDto(Account a, boolean full) {
		this.uuid = a.getLsUuid();
		this.domain = a.getDomainId();
		this.creationDate = a.getCreationDate();
		this.modificationDate = a.getModificationDate();
		if (full) {
			this.locale = a.getLocale();
			this.externalMailLocale = a.getExternalMailLocale();
		}
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public SupportedLanguage getLocale() {
		return locale;
	}

	public void setLocale(SupportedLanguage locale) {
		this.locale = locale;
	}

	public Language getExternalMailLocale() {
		return externalMailLocale;
	}

	public void setExternalMailLocale(Language extertalMailLocale) {
		this.externalMailLocale = extertalMailLocale;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
}
