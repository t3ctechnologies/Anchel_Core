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
package com.t3c.anchel.core.domain.entities;

import java.util.Date;
import java.util.Set;

import com.google.common.collect.Sets;
import com.t3c.anchel.core.domain.constants.UploadPropositionMatchType;

public class UploadPropositionFilter {

	private long id;

	private String uuid;

	private String name;

	private UploadPropositionMatchType match;

	private boolean enable;

	private Set<UploadPropositionRule> rules = Sets.newHashSet();

	private Set<UploadPropositionAction> actions = Sets.newHashSet();

	private Date creationDate;

	private Date modificationDate;

	private int order;

	private AbstractDomain domain;

	public UploadPropositionFilter() {
	}

	@Override
	public String toString() {
		return "UploadPropositionFilter [uuid=" + uuid + ", name=" + name
				+ ", match=" + match + ", enable=" + enable + ", order="
				+ order + "]";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UploadPropositionMatchType getMatch() {
		return match;
	}

	public void setMatch(UploadPropositionMatchType match) {
		this.match = match;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public Set<UploadPropositionRule> getRules() {
		return rules;
	}

	public void setRules(
			Set<UploadPropositionRule> rules) {
		this.rules = rules;
	}

	public Set<UploadPropositionAction> getActions() {
		return actions;
	}

	public void setActions(
			Set<UploadPropositionAction> actions) {
		this.actions = actions;
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

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public AbstractDomain getDomain() {
		return domain;
	}

	public void setDomain(AbstractDomain domain) {
		this.domain = domain;
	}
}
