package de.mpg.mpdl.doxi.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.mpg.mpdl.doxi.controller.DoiControllerInterface;
import de.mpg.mpdl.doxi.exception.DoxiException;
import de.mpg.mpdl.doxi.model.DOI;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;

@Path("doi")
@Api(value = "MPDL DOXI DOI REST API")
public class DOIResource {
  @Inject
  private DoiControllerInterface doiController;// = DataciteAPIController.getInstance();

  @ApiOperation(value = "Register a DOI with known value",
      notes = "Registers and mints a concrete DOI. The DOi in the given metadata XML is overwritten with the one provided in the path.")
  @ApiResponses({
      @ApiResponse(code = 201, message = "DOI sucessfully created.", response = String.class),
      @ApiResponse(code = 409, message = "DOI already exists."),
      @ApiResponse(code = 400, message = "DOI, URL or provided metadata have invalid format.")
  })
  @Path("{doi:10\\..+/.+}")
  @PUT
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  @RolesAllowed("user")
  public Response create(
      @ApiParam(value = "the DOI to be registered", required = true) @PathParam("doi") String doi,
      @ApiParam(value = "the URL to which this DOI should point",
          required = true) @QueryParam("url") String url,
      @ApiParam(
          value = "the metadata of this DOI in XML format. The identifier tag in the XML must either match the provided DOI or be empty (will be filled with provided DOI).",
          required = true) String metadataXml)
      throws Exception {
    String resultDoi = doiController.createDOI(doi, url, metadataXml).getDoi();
    Response r = Response.status(Status.CREATED).entity(resultDoi).build();
    
    return r;
  }

  @ApiOperation(value = "Generate and register a DOI",
      notes = "Generates, registers and mints a new DOI. If a certain suffix is required, it can be optionally provided. The DOi in the given metadata XML is overwritten with the generated one.")
  @ApiResponses({
      @ApiResponse(code = 201, message = "DOI sucessfully created.", response = String.class),
      @ApiResponse(code = 409, message = "DOI already exists."),
      @ApiResponse(code = 400, message = "DOI, URL or provided metadata have invalid format.")
  })
  @PUT
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  @RolesAllowed("user")
  public Response createAutoOrSuffix(
      @ApiParam(value = "the URL to which this DOI should point",
          required = true) @QueryParam("url") String url,
      @ApiParam(value = "an optional suffix", required = false) @QueryParam("suffix") String suffix,
      @ApiParam(
          value = "the metadata of this DOI in XML format. The identifier tag in the XML must be empty, it will automatically be filled with the generated DOI.",
          required = true) String metadataXml)
      throws DoxiException {
    String resultDoi = "";
    if (suffix == null) {
      resultDoi = doiController.createDOIAutoGenerated(url, metadataXml).getDoi();
    } else {
      resultDoi = doiController.createDOIKnownSuffix(suffix, url, metadataXml).getDoi();
    }

    Response r = Response.status(Status.CREATED).entity(resultDoi).build();
    
    return r;
  }

  @ApiOperation(value = "Update an existing DOI",
      notes = "Updates an existing DOI with a new URL and new metadata.")
  @ApiResponses({
      @ApiResponse(code = 201, message = "DOI sucessfully updated.", response = String.class,
          responseHeaders = {@ResponseHeader(name = "Location", description = "the URL of this DOI",
              response = String.class)}),
      @ApiResponse(code = 400, message = "DOI, URL or provided metadata have invalid format.")
  })
  @Path("{doi:10\\..+/.+}")
  @POST
  @Produces(MediaType.APPLICATION_XML)
  @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  @RolesAllowed("user")
  public Response updateDOI(
      @ApiParam(value = "the DOI to be updated", required = true) @PathParam("doi") String doi,
      @ApiParam(value = "the new URL", required = true) @QueryParam("url") String url,
      @ApiParam(value = "the new metadata", required = true) String metadataXml)
      throws DoxiException {

    DOI resultDoi = doiController.updateDOI(doi, url, metadataXml);
    
    return Response.status(Status.CREATED).entity(resultDoi.getMetadata())
        .header(HttpHeaders.LOCATION, resultDoi.getUrl().toString()).build();
  }

  @ApiOperation(value = "Get Metadata and URL of a DOI")
  @ApiResponses({
      @ApiResponse(code = 200, message = "DOI sucessfully retrieved.", response = String.class,
          responseHeaders = {@ResponseHeader(name = "Location", description = "the URL of this DOI",
              response = String.class)}),
      @ApiResponse(code = 400, message = "DOI, URL or provided metadata have invalid format.")})
  @Path("{doi:10\\..+/.+}")
  @GET
  @Produces(MediaType.APPLICATION_XML)
  @RolesAllowed("user")
  public Response getDOI(@ApiParam(value = "the DOI", required = true) @PathParam("doi") String doi)
      throws DoxiException {
    DOI resultDoi = doiController.getDOI(doi);
    
    return Response.status(Status.OK).entity(resultDoi.getMetadata())
        .header(HttpHeaders.LOCATION, resultDoi.getUrl().toString()).build();
  }

  @ApiOperation(value = "Get a list of registered DOIs")
  @ApiResponses({
      @ApiResponse(code = 200, message = "DOIs sucessfully retrieved.", response = String.class),
      @ApiResponse(code = 400, message = "DOI, URL or provided metadata have invalid format.")})
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @RolesAllowed("user")
  public Response getDOIList() throws DoxiException {
    // TODO DOI prefix depending on current user
    List<DOI> resultDoiList = doiController.getDOIList();
    StringBuffer sb = new StringBuffer();
    for (DOI doi : resultDoiList) {
      sb.append(doi.getDoi());
      sb.append("\n");
    }

    return Response.status(Status.OK).entity(sb.toString()).build();
  }

  @ApiOperation(value = "Deactivate a DOI")
  @ApiResponses({
      @ApiResponse(code = 200, message = "DOI sucessfully inactivated.", response = String.class),
      @ApiResponse(code = 400, message = "Problem with inactivation.")})
  @Path("{doi:10\\..+/.+}")
  @DELETE
  @Produces(MediaType.APPLICATION_XML)
  @RolesAllowed("user")
  public Response inactivate(
      @ApiParam(value = "the DOI to be inactivated", required = true) @PathParam("doi") String doi)
      throws DoxiException {
    DOI resultDoi = doiController.inactivateDOI(doi);
    
    return Response.status(Status.OK).entity(resultDoi.getMetadata()).build();
  }
}
