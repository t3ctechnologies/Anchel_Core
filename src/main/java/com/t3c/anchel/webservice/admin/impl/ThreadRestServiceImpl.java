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

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.t3c.anchel.core.exception.BusinessException;
import com.t3c.anchel.core.facade.webservice.admin.ThreadFacade;
import com.t3c.anchel.core.facade.webservice.common.dto.WorkGroupDto;
import com.t3c.anchel.core.facade.webservice.common.dto.WorkGroupMemberDto;
import com.t3c.anchel.webservice.admin.ThreadRestService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/threads")
@Api(value = "/rest/admin/threads", description = "Threads service")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ThreadRestServiceImpl implements ThreadRestService {

	private final ThreadFacade threadFacade;

	public ThreadRestServiceImpl(final ThreadFacade threadFacade) {
		super();
		this.threadFacade = threadFacade;
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Find all threads.", response = WorkGroupDto.class, responseContainer = "Set")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public Set<WorkGroupDto> findAll(
			@QueryParam("pattern") String pattern,
			@QueryParam("threadName") String threadName,
			@QueryParam("memberName") String memberName)
					throws BusinessException {
		return threadFacade.findAll(pattern, threadName, memberName);
	}

	@Path("/{uuid}")
	@GET
	@ApiOperation(value = "Find a thread", response = WorkGroupDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public WorkGroupDto find(@PathParam("uuid") String uuid)
			throws BusinessException {
		return threadFacade.find(uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@ApiOperation(value = "Find a thread")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public void head(@PathParam("uuid") String uuid)
			throws BusinessException {
		threadFacade.find(uuid);
	}

	@Path("/{uuid}/members")
	@GET
	@ApiOperation(value = "Find all thread members.", response = WorkGroupDto.class, responseContainer = "Set")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public Set<WorkGroupMemberDto> members(@PathParam("uuid") String uuid)
			throws BusinessException {
		return threadFacade.members(uuid);
	}

	@Path("/")
	@PUT
	@ApiOperation(value = "Update a thread.", response = WorkGroupDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public WorkGroupDto update(WorkGroupDto thread) throws BusinessException {
		return threadFacade.update(thread);
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete a thread.", response = WorkGroupDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public WorkGroupDto delete(WorkGroupDto thread) throws BusinessException {
		return threadFacade.delete(thread.getUuid());
	}
}
