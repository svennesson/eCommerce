package se.hmpaj.ecommerce.web.service.mapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import se.hmpaj.ecommerce.model.Order;
import se.hmpaj.ecommerce.model.Product;
import se.hmpaj.ecommerce.model.util.Constants;
import se.hmpaj.ecommerce.web.exception.WebServiceException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonWriter;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderMapper implements MessageBodyReader<Order>,
		MessageBodyWriter<Order>
{

	private Gson gson;

	public OrderMapper()
	{
		gson = new GsonBuilder().registerTypeAdapter(Order.class,
				new OrderAdapter()).create();
	}

	// Message body reader

	@Override
	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType)
	{
		return type.isAssignableFrom(Order.class);
	}

	@Override
	public Order readFrom(Class<Order> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException
	{

		final Order order = gson.fromJson(new InputStreamReader(entityStream),
				Order.class);

		return order;
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType)
	{
		return type.isAssignableFrom(Order.class);
	}

	@Override
	public long getSize(Order t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType)
	{
		return 0;
	}

	@Override
	public void writeTo(Order order, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException,
			WebApplicationException
	{

		try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(
				entityStream)))
		{
			gson.toJson(order, Order.class, writer);
		}
	}

	private static final class OrderAdapter implements JsonDeserializer<Order>,
			JsonSerializer<Order>
	{

		@Override
		public Order deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException
		{
			final JsonObject jsonProduct = json.getAsJsonObject();
			final long id;
			final JsonArray jsonProductsArray;
			final List<Product> products = new ArrayList<>();

			if (jsonProduct.has("products"))
			{
				try
				{
					jsonProductsArray = jsonProduct.get("products").getAsJsonArray();
				}
				catch (IllegalStateException e)
				{
					throw new WebServiceException("Products not parsed, " + e.getMessage());
				}

				if (jsonProductsArray != null)
				{
					for (int i = 0; i < jsonProductsArray.size(); i++)
					{
						final long productId = jsonProductsArray.get(i).getAsJsonObject().get("id").getAsLong();
						final String productTitle = jsonProductsArray.get(i).getAsJsonObject().get("title").getAsString();
						final double productPrice = jsonProductsArray.get(i).getAsJsonObject().get("price").getAsDouble();

						final Product product = new Product(productId, productTitle, productPrice);
						products.add(product);
					}
				}

				if (jsonProduct.has("orderId"))
				{
					id = jsonProduct.get("id").getAsLong();
				}
				else
				{
					id = Constants.EMPTY_ID;
				}

				return new Order(id, products);
			}

			throw new WebServiceException("Order have to contain body");
		}

		@Override
		public JsonElement serialize(Order order, Type typeOfSrc,
				JsonSerializationContext context)
		{

			final JsonObject jsonOrder = new JsonObject();
			jsonOrder.add("orderId", new JsonPrimitive(order.getId()));
			jsonOrder.add("username", new JsonPrimitive(order.getUser()
					.getEmail()));
			jsonOrder.add("Sum: ", new JsonPrimitive(order.getSum()));

			final JsonArray productsArray = new JsonArray();

			for (Product product : order.getProducts())
			{
				JsonObject jsonProduct = new JsonObject();
				jsonProduct.add("id", new JsonPrimitive(product.getId()));
				jsonProduct.add("title", new JsonPrimitive(product.getTitle()));
				jsonProduct.add("price", new JsonPrimitive(product.getPrice()));

				productsArray.add(jsonProduct);
			}

			jsonOrder.add("products", productsArray);

			return jsonOrder;
		}

	}

}
