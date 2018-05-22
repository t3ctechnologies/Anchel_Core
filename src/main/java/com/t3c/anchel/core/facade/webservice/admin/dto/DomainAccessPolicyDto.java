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
package com.t3c.anchel.core.facade.webservice.admin.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Lists;
import com.t3c.anchel.core.domain.entities.DomainAccessPolicy;
import com.t3c.anchel.core.domain.entities.DomainAccessRule;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "DomainAccessPolicy")
@ApiModel(value = "DomainAccessPolicy", description = "Access policy of a domain, defining it's communication rules")
public class DomainAccessPolicyDto {

	@ApiModelProperty(value = "Communication rules")
	private List<DomainAccessRuleDto> rules;

	public DomainAccessPolicyDto() {
	}

	public DomainAccessPolicyDto(DomainAccessPolicy dap) {
		this.rules = Lists.newArrayList();
		for (DomainAccessRule rule : dap.getRules()) {
			// FIXME : the list can contain a null rule object
			/**
			 * If you delete a domain referenced by a domain access policy,
			 * it seems the proxy keep a null object in the dap list.
			 * May be because the rule is deleted by cascade instead of being deleted by the repository.
			 */
			if (rule != null) {
				this.rules.add(new DomainAccessRuleDto(rule));
			}
		}
	}

	public List<DomainAccessRuleDto> getRules() {
		return rules;
	}

	public void setRules(List<DomainAccessRuleDto> rules) {
		this.rules = rules;
	}
}
