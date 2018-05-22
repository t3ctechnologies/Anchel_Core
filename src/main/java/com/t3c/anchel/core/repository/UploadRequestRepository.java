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

import java.util.Date;
import java.util.List;

import com.t3c.anchel.core.domain.constants.UploadRequestStatus;
import com.t3c.anchel.core.domain.entities.AbstractDomain;
import com.t3c.anchel.core.domain.entities.UploadRequest;
import com.t3c.anchel.core.domain.entities.User;

public interface UploadRequestRepository extends
		AbstractRepository<UploadRequest> {

	/**
	 * Find a uploadRequestEntry using its uuid.
	 * 
	 * @param uuid
	 * @return found uploadRequest (null if no uploadRequestEntry found).
	 */
	UploadRequest findByUuid(String uuid);

	/**
	 * Find uploadRequests using their owner.
	 * 
	 * @param owner
	 * @param statusList List of status.
	 * @return found uploadRequests otherwise null.
	 */
	List<UploadRequest> findByOwner(User owner, List<UploadRequestStatus> statusList);

	/**
	 * Find uploadRequests using their status.
	 *
	 * @param status
	 * @return found uploadRequests otherwise null.
	 */
	List<UploadRequest> findByStatus(UploadRequestStatus... status);

	/**
	 * Find uploadRequests using their status and their domains.
	 *
	 * @param domains
	 * @param status
	 * @param after based on creation date
	 * @param before based on creation date
	 * @return found uploadRequests otherwise null.
	 */
	List<UploadRequest> findByDomainsAndStatus(List<AbstractDomain> domains, List<UploadRequestStatus> status, Date after, Date before);

	List<String> findOutdatedRequests();

	List<String> findCreatedUploadRequests();

	List<String> findAllRequestsToBeNotified();
}
