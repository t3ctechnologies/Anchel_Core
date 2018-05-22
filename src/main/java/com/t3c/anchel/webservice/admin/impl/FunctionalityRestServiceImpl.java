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

package com.t3c.anchel.webservice.admin.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.t3c.anchel.core.exception.BusinessException;
import com.t3c.anchel.core.facade.webservice.admin.FunctionalityFacade;
import com.t3c.anchel.core.facade.webservice.admin.dto.FunctionalityAdminDto;
import com.t3c.anchel.webservice.WebserviceBase;
import com.t3c.anchel.webservice.admin.FunctionalityRestService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Path("/functionalities")
@Api(value = "/rest/admin/functionalities", description = "Functionalities service.")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class FunctionalityRestServiceImpl extends WebserviceBase implements
		FunctionalityRestService {

	private final FunctionalityFacade functionalityFacade;

	public FunctionalityRestServiceImpl(
			final FunctionalityFacade functionalityFacade) {
		this.functionalityFacade = functionalityFacade;
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Find all domain's functionalities.", response = FunctionalityAdminDto.class, responseContainer = "Set")
	@Override
	public List<FunctionalityAdminDto> findAll(
			@QueryParam(value = "domainId") String domainId,
			@QueryParam(value = "parentId") String parentId,
			@QueryParam("tree") @DefaultValue("false") boolean tree,
			@QueryParam("subs") @DefaultValue("false") boolean withSubFunctionalities)
			throws BusinessException {
		return functionalityFacade.findAll(domainId, parentId, tree, withSubFunctionalities);
	}

	@Path("/{funcId}")
	@GET
	@ApiOperation(value = "Find a domain's functionality.", response = FunctionalityAdminDto.class)
	@Override
	public FunctionalityAdminDto find(
			@QueryParam(value = "domainId") String domainId,
			@PathParam(value = "funcId") String funcId,
			@QueryParam("tree") @DefaultValue("false") boolean tree)
			throws BusinessException {
		return functionalityFacade.find(domainId, funcId, tree);
	}

	@Path("/")
	@PUT
	@ApiOperation(value = "Update a domain's functionality.")
	@Override
	public FunctionalityAdminDto update(FunctionalityAdminDto func)
			throws BusinessException {
		return functionalityFacade.update(func);
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete a domain's functionality.")
	@Override
	public void delete(FunctionalityAdminDto func) throws BusinessException {
		functionalityFacade.delete(func);
	}
}
