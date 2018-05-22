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
package com.t3c.anchel.core.repository;

import com.t3c.anchel.core.domain.constants.ContainerQuotaType;
import com.t3c.anchel.core.domain.entities.AbstractDomain;
import com.t3c.anchel.core.domain.entities.ContainerQuota;
import com.t3c.anchel.core.domain.entities.DomainQuota;

public interface ContainerQuotaRepository extends GenericQuotaRepository<ContainerQuota> {

	ContainerQuota find(AbstractDomain domain, ContainerQuotaType containerQuotaType);

	Long sumOfCurrentValue(DomainQuota domainQuota);

	Long cascadeMaintenanceMode(ContainerQuota container, boolean maintenance);

	Long cascadeDefaultQuota(AbstractDomain domain, Long quota, ContainerQuotaType containerType);

	Long cascadeDefaultMaxFileSize(AbstractDomain domain, Long maxFileSize, ContainerQuotaType containerType);

	Long cascadeMaxFileSize(ContainerQuota container, Long maxFileSize);

	Long cascadeDefaultAccountQuota(AbstractDomain domain, Long quota, ContainerQuotaType containerType);

	Long cascadeAccountQuota(ContainerQuota container, Long accountQuota);
}
