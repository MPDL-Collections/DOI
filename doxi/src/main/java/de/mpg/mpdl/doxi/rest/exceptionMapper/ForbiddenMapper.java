package de.mpg.mpdl.doxi.rest.exceptionMapper;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ForbiddenMapper implements ExceptionMapper<ForbiddenException> {
  @Override
  public Response toResponse(ForbiddenException exception) {
    return Response.status(Status.UNAUTHORIZED).entity(exception.getMessage())
        .header("WWW-Authenticate", "Basic realm=\"Please provide username and password\"").build();
  }
}
