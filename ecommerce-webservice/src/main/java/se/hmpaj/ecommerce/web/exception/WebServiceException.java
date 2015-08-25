package se.hmpaj.ecommerce.web.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class WebServiceException extends WebApplicationException
{
	private static final long serialVersionUID = -3789217991359179867L;

	public WebServiceException(String message)
	{

		super(Response.status(Status.BAD_REQUEST)
				.entity("This is a bad request: " + message).build());
	}
}
