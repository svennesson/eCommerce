package se.hmpaj.ecommerce.service.mapper;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;

import se.hmpaj.ecommerce.model.Product;
import se.hmpaj.ecommerce.model.util.Constants;
import se.hmpaj.ecommerce.service.sql.RowMapper;

public final class SQLProductMapper implements RowMapper<Product>
{
	private final Product product;

	public SQLProductMapper()
	{
		this(null);
	}

	public SQLProductMapper(Product product)
	{
		this.product = product;
	}
	
	@Override
	public Product mapRow(ResultSet rs) throws SQLException
	{
		Product newProduct = null;

		if (this.product == null)
		{
			newProduct = new Product(rs.getLong(1), rs.getString(2), rs.getDouble(3));
		}
		else if (this.product.getId() == Constants.EMPTY_ID)
		{
			newProduct = new Product(rs.getLong(1), this.product.getTitle(), this.product.getPrice());
		}
		else
		{
			return this.product;
		}

		return newProduct;
	}

	@Override
	public Type getType()
	{
		return Product.class;
	}
}