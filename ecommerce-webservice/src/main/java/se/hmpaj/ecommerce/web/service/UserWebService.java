package se.hmpaj.ecommerce.web.service;

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import se.hmpaj.ecommerce.model.Order;
import se.hmpaj.ecommerce.model.User;
import se.hmpaj.ecommerce.service.ECommerceManager;
import se.hmpaj.ecommerce.service.exception.ECommerceManagerException;
import se.hmpaj.ecommerce.service.repository.OrderRepository;
import se.hmpaj.ecommerce.service.repository.ProductRepository;
import se.hmpaj.ecommerce.service.repository.UserRepository;
import se.hmpaj.ecommerce.service.repository.sql.SQLOrderRepository;
import se.hmpaj.ecommerce.service.repository.sql.SQLProductRepository;
import se.hmpaj.ecommerce.service.repository.sql.SQLUserRepository;
import se.hmpaj.ecommerce.web.exception.WebServiceException;

import com.google.gson.Gson;

@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public final class UserWebService
{

	OrderRepository orderRepo = new SQLOrderRepository();
	ProductRepository productRepo = new SQLProductRepository();
	UserRepository userRepo = new SQLUserRepository();

	ECommerceManager manager = new ECommerceManager(userRepo, productRepo,
			orderRepo);

	@Context
	private UriInfo uriInfo;

	// User mapping
	@GET
	@Path("{userId}")
	public Response getUser(@PathParam("userId") final long userId)
	{

		try
		{
			final User user;
			user = manager.getUserById(userId);
			return Response.ok(user).build();
		}
		catch (ECommerceManagerException e)
		{
			throw new WebServiceException("No user found on id: " + userId);
		}

	}

	@POST
	public Response addUser(User user)
	{

		try
		{
			final User userFromDB = manager.addUser(user);
			final URI location = uriInfo.getAbsolutePathBuilder()
					.path(String.valueOf(userFromDB.getId())).build();

			return Response.created(location).build();

		}
		catch (ECommerceManagerException e)
		{
			throw new WebServiceException("Could not add user");
		}

	}

	@PUT
	@Path("{userId}")
	public Response updateUser(@PathParam("userId") final long userId, User user)
	{

		try
		{
			manager.updateUser(user);
			final URI location = uriInfo.getAbsolutePathBuilder().build();
			return Response.status(Status.NO_CONTENT).location(location)
					.build();
		}
		catch (ECommerceManagerException e)
		{
			throw new WebServiceException("could not update the user with id: "
					+ user.getId());
		}


	}

	@DELETE
	@Path("{userId}")
	public Response deleteUser(@PathParam("userId") final long userId)
	{

		try
		{
			manager.deleteUser(userId);
			return Response.ok().build();
		}
		catch (ECommerceManagerException e)
		{
			throw new WebServiceException("could not delete the user with id: "
					+ userId);
		}

	}

	// Orders mapping
	@Path("{userId}/orders")
	@GET
	public Response getAllUserOrders(@PathParam("userId") final long userId)
	{
		try
		{
			List<Order> allOrders;
			allOrders = manager.getAllOrdersForUser(userId);
			return Response.ok(new Gson().toJson(allOrders)).build();

		}
		catch (ECommerceManagerException e)
		{
			throw new WebServiceException("No order for user with id: " + userId);
		}

	}

	@Path("{userId}/orders")
	@POST
	public Response addUserOrder(@PathParam("userId") final long userId, final Order postOrder)
	{
		try
		{
			final Order orderFromDb;

			orderFromDb = manager.addOrder(new Order(userId, postOrder.getProducts()));
			final URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(orderFromDb.getId())).build();

			return Response.created(location).build();
		}
		catch (ECommerceManagerException e)
		{
			throw new WebServiceException("Could not add order");
		}

	}

	@Path("{userId}/orders/{orderId}")
	@GET
	public Response getUserOrder(@PathParam("userId") final long userId,
			@PathParam("orderId") final long orderId)
	{
		try
		{
			final Order order = manager.getOrder(orderId);
			return Response.ok(order).build();
		}
		catch (ECommerceManagerException e)
		{
			throw new WebServiceException("Could not find order for user " + userId
					+ " with order id: " + orderId);
		}

	}

	@Path("{userId}/orders/{orderId}")
	@DELETE
	public Response removeOrder(@PathParam("userId") final long userId,
			@PathParam("orderId") final long orderId)
	{
		try
		{
			manager.deleteOrder(orderId);
			return Response.ok().build();
		}
		catch (ECommerceManagerException e)
		{
			throw new WebServiceException("Could not delete order with order id: "
					+ orderId);
		}

	}

	@Path("{userId}/orders/{orderId}")
	@PUT
	public Response updateOrder(@PathParam("userId") final long userId,
			@PathParam("orderId") final long orderId, final Order postOrder)
	{
		try
		{
			final Order order;

			// order = manager.updateOrder(new Order(orderId, userId,
			// postOrder.getProducts()));
			final URI location = uriInfo.getAbsolutePathBuilder().build();

			return Response.status(Status.NO_CONTENT).location(location).build();
		}
		catch (ECommerceManagerException e)
		{
			throw new WebServiceException("Could not update order with order id: "
					+ orderId);
		}

	}
}
