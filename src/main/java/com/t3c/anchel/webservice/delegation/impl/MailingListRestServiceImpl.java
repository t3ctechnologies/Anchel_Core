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

package com.t3c.anchel.webservice.delegation.impl;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.Validate;

import com.t3c.anchel.core.exception.BusinessException;
import com.t3c.anchel.core.facade.webservice.common.dto.MailingListContactDto;
import com.t3c.anchel.core.facade.webservice.common.dto.MailingListDto;
import com.t3c.anchel.core.facade.webservice.user.MailingListFacade;
import com.t3c.anchel.webservice.delegation.MailingListRestServcice;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/{ownerUuid}/lists")
@Api(value = "/rest/delegaton/{ownerUuid}/lists", description = "Mailing lists delegation api.")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class MailingListRestServiceImpl implements MailingListRestServcice {

	private final MailingListFacade mailingListFacade;

	public MailingListRestServiceImpl(final MailingListFacade mailingListFacade) {
		this.mailingListFacade = mailingListFacade;
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Find all an user mailing lists.", response = MailingListDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public Set<MailingListDto> findAll(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid)
					throws BusinessException {
		Validate.notEmpty(ownerUuid, "Owner uuid must be set.");
		return mailingListFacade.findAll(ownerUuid, null);
	}

	@Path("/{uuid}")
	@GET
	@ApiOperation(value = "Find an user mailing list.", response = MailingListDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Mailing list not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public MailingListDto find(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The mailing list uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		Validate.notEmpty(ownerUuid, "Owner uuid must be set.");
		return mailingListFacade.find(ownerUuid, uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@ApiOperation(value = "Find an user mailing list.", response = MailingListDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
		@ApiResponse(code = 404, message = "Mailing list not found."),
		@ApiResponse(code = 400, message = "Bad request : missing required fields."),
		@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public void head(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The mailing list uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		Validate.notEmpty(ownerUuid, "Owner uuid must be set.");
		mailingListFacade.find(ownerUuid, uuid);
	}

	@Path("/")
	@POST
	@ApiOperation(value = "Create a mailing list.", response = MailingListDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public MailingListDto create(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The mailing list to create.", required = true) MailingListDto dto)
					throws BusinessException {
		Validate.notEmpty(ownerUuid, "Owner uuid must be set.");
		return mailingListFacade.create(ownerUuid, dto);
	}

	@Path("/")
	@PUT
	@ApiOperation(value = "Update a mailing list.", response = MailingListDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public MailingListDto update(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The mailing list to update.", required = true) MailingListDto dto)
					throws BusinessException {
		Validate.notEmpty(ownerUuid, "Owner uuid must be set.");
		return mailingListFacade.update(ownerUuid, dto, null);
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete a mailing list.", response = MailingListDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 404, message = "Mailing list not found."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public MailingListDto delete(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The mailing list to delete.", required = true) MailingListDto dto)
					throws BusinessException {
		Validate.notEmpty(ownerUuid, "Owner uuid must be set.");
		return mailingListFacade.delete(ownerUuid, dto.getUuid());
	}

	@Path("/{uuid}")
	@DELETE
	@ApiOperation(value = "Find an user mailing list.", response = MailingListDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "Mailing list not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public MailingListDto delete(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The mailing list uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		Validate.notEmpty(ownerUuid, "Owner uuid must be set.");
		return mailingListFacade.delete(ownerUuid, uuid);
	}

	@Path("/{uuid}/contacts")
	@POST
	@ApiOperation(value = "Create a contact in a mailing list.")
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") })
	@Override
	public void createContact(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The mailing list uuid.", required = true) @PathParam("uuid") String uuid,
			@ApiParam(value = "The mailing list contact to create.", required = true) MailingListContactDto dto)
					throws BusinessException {
		Validate.notEmpty(ownerUuid, "Owner uuid must be set.");
		mailingListFacade.addContact(ownerUuid, uuid, dto);
	}

	@Path("/{uuid}/contacts")
	@PUT
	@ApiOperation(value = "Delete a contact from a mailing list.")
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") })
	@Override
	public void updateContact(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The mailing list contact uuid.", required = true) @PathParam("uuid") String uuid,
			@ApiParam(value = "The mailing list contact to create.", required = true) MailingListContactDto dto)
					throws BusinessException {
		Validate.notEmpty(ownerUuid, "Owner uuid must be set.");
		mailingListFacade.updateContact(ownerUuid, dto);
	}

	@Path("/{uuid}/contacts")
	@DELETE
	@ApiOperation(value = "Delete a contact from a mailing list.")
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") })
	@Override
	public void deleteContact(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The mailing list uuid.", required = true) @PathParam("uuid") String uuid,
			@ApiParam(value = "The mailing list contact.", required = true) MailingListContactDto dto)
					throws BusinessException {
		Validate.notEmpty(ownerUuid, "Owner uuid must be set.");
		mailingListFacade.deleteContact(ownerUuid, dto.getUuid());
	}

	@Path("/{uuid}/contacts")
	@GET
	@ApiOperation(value = "Delete a contact from a mailing list.")
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") })
	@Override
	public Set<MailingListContactDto> findAllContacts(
			@ApiParam(value = "The owner (user) uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "The mailing list uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		Validate.notEmpty(ownerUuid, "Owner uuid must be set.");
		return mailingListFacade.findAllContacts(ownerUuid, uuid);
	}

	@Path("/{uuid}/contacts/{contactUuid}")
	@DELETE
	@ApiOperation(value = "Delete a contact from a mailing list.")
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role.") })
	@Override
	public void deleteContact(
			@ApiParam(value = "Mailing list contact uuid.", required = true) @PathParam("ownerUuid") String ownerUuid,
			@ApiParam(value = "Mailing list contact uuid.", required = true) @PathParam("uuid") String uuid,
			@ApiParam(value = "Mailing list contact uuid.", required = true) @PathParam("contactUuid") String contactUuid)
					throws BusinessException {
		mailingListFacade.deleteContact(ownerUuid, contactUuid);
	}
}
