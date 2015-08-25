package se.hmpaj.ecommerce.web.service;

import java.net.URI;
import java.util.Map;

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
import javax.ws.rs.core.UriInfo;

import se.hmpaj.ecommerce.model.Product;
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

@Path("products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductWebService
{

	OrderRepository orderRepo = new SQLOrderRepository();
	ProductRepository productRepo = new SQLProductRepository();
	UserRepository userRepo = new SQLUserRepository();
	ECommerceManager manager = new ECommerceManager(userRepo, productRepo,
			orderRepo);

	@Context
	private UriInfo uriInfo;

	@GET
	public Response getAllProducts()
	{
		try
		{
			Map<Long, Product> allProducts = manager.getAllProducts();
			return Response.ok(new Gson().toJson(allProducts)).build();
		}
		catch (ECommerceManagerException e)
		{
			e.printStackTrace();
		}
		throw new WebServiceException("No products found");
	}

	@GET
	@Path("{productId}")
	public Response getProduct(@PathParam("productId") final long productId)
	{
		try
		{
			final Product product = manager.getProduct(productId);
			return Response.ok(product).build();
		}
		catch (ECommerceManagerException e)
		{
			e.printStackTrace();
		}

		throw new WebServiceException("Could not find product");
	}

	@PUT
	@Path("{productId}")
	public Response updateProduct(@PathParam("productId") final long id,
			final Product product)
	{
		try
		{
			Product updatedProduct = manager.updateProduct(product);
			return Response.ok(updatedProduct).build();
		}
		catch (ECommerceManagerException e)
		{
			e.printStackTrace();
			throw new WebServiceException("Could not update product" + e);
		}

	}

	@POST
	public Response addProduct(final Product product)
	{
		try
		{

			final Product productFromDB = manager.addProduct(product);
			final URI location = uriInfo.getAbsolutePathBuilder()
					.path(String.valueOf(productFromDB.getId())).build();

			return Response.ok().contentLocation(location).build();

		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (ECommerceManagerException e)
		{
			e.printStackTrace();
		}

		throw new WebServiceException("Could not add product");
	}

	@DELETE
	@Path("{productId}")
	public Response removeProduct(@PathParam("productId") final long productId)
	{
		try
		{
			manager.deleteProduct(productId);
			return Response.ok().build();
		}
		catch (ECommerceManagerException e)
		{
			e.printStackTrace();
		}

		throw new WebServiceException("Could not delete the product");
	}
}
