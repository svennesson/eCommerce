package se.hmpaj.ecommerce.service.repository.sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.hmpaj.ecommerce.model.Product;
import se.hmpaj.ecommerce.service.exception.RepositoryException;
import se.hmpaj.ecommerce.service.exception.SQLRepositoryException;
import se.hmpaj.ecommerce.service.mapper.SQLProductMapper;
import se.hmpaj.ecommerce.service.repository.ProductRepository;
import se.hmpaj.ecommerce.service.sql.RowMapper;
import se.hmpaj.ecommerce.service.sql.SqlQuery;

public final class SQLProductRepository implements ProductRepository
{
	private static final String INSERT_PRODUCT = "INSERT INTO products (title, price) VALUES (?, ?)";
	private static final String SELECT_PRODUCT = "SELECT * FROM products WHERE id = ?";
	private static final String SELECT_ALL_PRODUCTS = "SELECT * FROM products";
	private static final String UPDATE_PRODUCT = "UPDATE products SET title = ?, price = ? WHERE id = ?";
	private static final String DELETE_PRODUCT = "DELETE FROM products WHERE id = ?";

	@Override
	public Product addProduct(Product product)
	{
		try
		{
			final SQLProductMapper mapper = new SQLProductMapper(product);
			final List<Product> products = executeQuery(INSERT_PRODUCT, mapper, product.getTitle(), product.getPrice());

			return products.get(0);
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new RepositoryException("Could not add product " + e);
		}
	}

	@Override
	public Product getProduct(long id)
	{
		try
		{
			final SQLProductMapper mapper = new SQLProductMapper();
			final List<Product> products = executeQuery(SELECT_PRODUCT, mapper, id);

			return products.get(0);
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new RepositoryException("Could not get product " + e);
		}
	}

	@Override
	public Map<Long, Product> getAllProducts()
	{
		final SQLProductMapper mapper = new SQLProductMapper();
		final List<Product> products = executeQuery(SELECT_ALL_PRODUCTS, mapper);

		final Map<Long, Product> productMap = new HashMap<>();

		products.forEach((product) -> {
			productMap.put(product.getId(), product);
		});

		return productMap;
	}

	@Override
	public Product updateProduct(Product product)
	{
		try
		{
			final SQLProductMapper mapper = new SQLProductMapper(product);
			final List<Product> products = executeQuery(UPDATE_PRODUCT, mapper, product.getTitle(), product.getPrice(), product.getId());

			return products.get(0);
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new RepositoryException("Could not update product " + e);
		}
	}

	@Override
	public Product deleteProduct(long id)
	{
		try
		{
			final Product product = getProduct(id);
			final SQLProductMapper mapper = new SQLProductMapper(product);

			List<Product> products = executeQuery(DELETE_PRODUCT, mapper, id);

			return products.get(0);
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new RepositoryException("Could not delete product " + e);
		}
	}

	public <T> List<T> executeQuery(String queryString, RowMapper<T> mapper, Object... parameters)
	{
		try
		{
			final List<T> products = SqlQuery.getBuilder()
											 .query(queryString)
											 .parameters(parameters)
											 .executeQuery(mapper);

			return products;
		}
		catch (SQLRepositoryException e)
		{
			throw new RepositoryException(e.getMessage());
		}
	}
}