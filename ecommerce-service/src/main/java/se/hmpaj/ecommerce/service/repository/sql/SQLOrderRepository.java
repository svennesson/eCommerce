package se.hmpaj.ecommerce.service.repository.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import se.hmpaj.ecommerce.model.Order;
import se.hmpaj.ecommerce.model.Product;
import se.hmpaj.ecommerce.service.exception.RepositoryException;
import se.hmpaj.ecommerce.service.exception.SQLRepositoryException;
import se.hmpaj.ecommerce.service.mapper.SQLOrderMapper;
import se.hmpaj.ecommerce.service.mapper.SQLProductMapper;
import se.hmpaj.ecommerce.service.repository.OrderRepository;
import se.hmpaj.ecommerce.service.sql.RowMapper;
import se.hmpaj.ecommerce.service.sql.SqlQuery;

public class SQLOrderRepository implements OrderRepository
{
	private static final String SELECT_ALL_ORDERS = "SELECT * FROM orders INNER JOIN users ON orders.user_id = users.id WHERE user_id = ?";
	private static final String SELECT_ORDER = "SELECT * FROM orders INNER JOIN users ON orders.user_id = users.id WHERE orders.id = ?";
	private static final String DELETE_ORDER = "DELETE FROM orders WHERE id = ?";
	private static final String INSERT_ORDER = "INSERT INTO orders (sum, user_id) VALUES (?, ?)";
	private static final String UPDATE_ORDER = "UPDATE orders SET sum = ? WHERE id = ?";
	private static final String INSERT_PRODUCT_ORDER = "INSERT INTO orders_has_products VALUES (?, ?)";
	private static final String SELECT_PRODUCT_ORDER = "SELECT id, title, price FROM products INNER JOIN orders_has_products ON products.id = orders_has_products.product_id WHERE orders_has_products.order_id = ?";

	@Override
	public Order addOrder(Order order)
	{
		try
		{
			final SQLOrderMapper mapper = new SQLOrderMapper(order);
			final List<Order> orders = executeQuery(INSERT_ORDER, mapper, order.getSum(), order.getUser().getId());

			if (order.getProducts() != null)
			{
				final List<Product> products = addAllProductsForOrder(order.getProducts(), orders.get(0));
				return new Order(orders.get(0), products);
			}

			return orders.get(0);
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new RepositoryException("Could not add order " + e);
		}
	}

	@Override
	public Order getOrder(long id)
	{
		try
		{
			final SQLOrderMapper mapper = new SQLOrderMapper();
			final List<Order> orders = executeQuery(SELECT_ORDER, mapper, id);
			final List<Product> products = getAllProductsForOrder(id);
			return new Order(orders.get(0), products);
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new RepositoryException("Could not get order " + e);
		}
	}

	@Override
	public Order updateOrder(Order order)
	{
		try
		{
			final SQLOrderMapper mapper = new SQLOrderMapper(order);
			final List<Order> orders = executeQuery(UPDATE_ORDER, mapper, order.getSum(), order.getId());
			final List<Product> products = addAllProductsForOrder(order.getProducts(), orders.get(0));

			return new Order(orders.get(0), products);
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new RepositoryException("Could not update order " + e);
		}
	}

	@Override
	public Order deleteOrder(long id)
	{
		try
		{
			final Order order = getOrder(id);
			final SQLOrderMapper mapper = new SQLOrderMapper(order);

			final List<Order> orders = executeQuery(DELETE_ORDER, mapper, id);

			return orders.get(0);
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new RepositoryException("Could not get order " + e);
		}
	}

	@Override
	public List<Order> getAllOrdersForUser(final long userId)
	{
		final SQLOrderMapper mapper = new SQLOrderMapper();
		final List<Order> orders = executeQuery(SELECT_ALL_ORDERS, mapper, userId);
		
		final List<Order> ordersWithProducts = new ArrayList<>();
		
		for(Order order : orders)
		{
			List<Product> products = getAllProductsForOrder(order.getId());
			ordersWithProducts.add(new Order(order, products));
		}

		return ordersWithProducts;
	}
	
	private List<Product> addAllProductsForOrder(Collection<Product> products, Order order) throws IndexOutOfBoundsException
	{
		List<Product> productsToReturn = new ArrayList<>();

		for (Product product : products)
		{
			final SQLProductMapper mapper = new SQLProductMapper(product);
			final List<Product> productsFromSql = executeQuery(INSERT_PRODUCT_ORDER, mapper, order.getId(), product.getId());
			productsToReturn.add(productsFromSql.get(0));
		}
	
		return productsToReturn;
	}
	

	private List<Product> getAllProductsForOrder(long id)
	{
		final List<Product> products = executeQuery(SELECT_PRODUCT_ORDER, new SQLProductMapper(), id);
		return products;
	}

	private <T> List<T> executeQuery(String queryString, RowMapper<T> mapper, Object... parameters)
	{
		try
		{
			final List<T> orders = SqlQuery.getBuilder().query(queryString).parameters(parameters).executeQuery(mapper);

			return orders;

		}
		catch (SQLRepositoryException e)
		{
			throw new RepositoryException(e.getMessage());
		}
	}
}