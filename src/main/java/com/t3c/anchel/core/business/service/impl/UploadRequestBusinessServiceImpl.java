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
package com.t3c.anchel.core.business.service.impl;

import java.util.Date;
import java.util.List;

import com.t3c.anchel.core.business.service.UploadRequestBusinessService;
import com.t3c.anchel.core.domain.constants.UploadRequestStatus;
import com.t3c.anchel.core.domain.entities.AbstractDomain;
import com.t3c.anchel.core.domain.entities.UploadRequest;
import com.t3c.anchel.core.domain.entities.User;
import com.t3c.anchel.core.exception.BusinessException;
import com.t3c.anchel.core.repository.UploadRequestRepository;

public class UploadRequestBusinessServiceImpl implements
		UploadRequestBusinessService {

	private final UploadRequestRepository uploadRequestRepository;

	public UploadRequestBusinessServiceImpl(
			final UploadRequestRepository uploadRequestRepository) {
		super();
		this.uploadRequestRepository = uploadRequestRepository;
	}

	@Override
	public List<UploadRequest> findAll(User owner, List<UploadRequestStatus> statusList) {
		return uploadRequestRepository.findByOwner(owner, statusList);
	}

//	AKO: method unused.
	@Override
	public List<UploadRequest> findAll(UploadRequestStatus... status) {
		return uploadRequestRepository.findByStatus(status);
	}

	@Override
	public List<UploadRequest> findAll(List<AbstractDomain> domains,
			List<UploadRequestStatus> status, Date afterDate, Date beforeDate) {
		return uploadRequestRepository.findByDomainsAndStatus(domains, status,
				afterDate, beforeDate);
	}

	@Override
	public UploadRequest findByUuid(String uuid) {
		return uploadRequestRepository.findByUuid(uuid);
	}

	@Override
	public UploadRequest create(UploadRequest req) throws BusinessException {
		req.setStatus(UploadRequestStatus.STATUS_CREATED);
		return uploadRequestRepository.create(req);
	}

	@Override
	public UploadRequest update(UploadRequest req) throws BusinessException {
		return uploadRequestRepository.update(req);
	}

	@Override
	public UploadRequest update(UploadRequest req, UploadRequest object) throws BusinessException {
		req.setBusinessMaxFileCount(object.getMaxFileCount());
		req.setBusinessMaxFileSize(object.getMaxFileSize());
		req.setBusinessMaxDepositSize(object.getMaxDepositSize());
		req.setBusinessActivationDate(object.getActivationDate());
		req.setBusinessExpiryDate(object.getExpiryDate());
		req.setBusinessLocale(object.getLocale());
		req.setBusinessCanClose(object.isCanClose());
		req.setBusinessCanDelete(object.isCanDelete());
		return uploadRequestRepository.update(req);
	}

	@Override
	public void delete(UploadRequest req) throws BusinessException {
		uploadRequestRepository.delete(req);
	}

	@Override
	public List<String> findOutdatedRequests() throws BusinessException {
		return uploadRequestRepository.findOutdatedRequests();
	}

	@Override
	public List<String> findUnabledRequests() throws BusinessException {
		return uploadRequestRepository.findCreatedUploadRequests();
	}

	@Override
	public List<String> findAllRequestsToBeNotified() throws BusinessException {
		return uploadRequestRepository.findAllRequestsToBeNotified();
	}
}
