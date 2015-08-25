package se.hmpaj.ecommerce.service.mapper;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;

import se.hmpaj.ecommerce.model.Order;
import se.hmpaj.ecommerce.model.User;
import se.hmpaj.ecommerce.model.util.Constants;
import se.hmpaj.ecommerce.service.sql.RowMapper;

public final class SQLOrderMapper implements RowMapper<Order>
{
	private final Order order;

	public SQLOrderMapper()
	{
		this(null);
	}

	public SQLOrderMapper(Order order)
	{
		this.order = order;
	}

	@Override
	public Order mapRow(ResultSet rs) throws SQLException
	{
		Order newOrder = null;

		if (this.order == null)
		{
			User user = new User(rs.getLong("user_id"), rs.getString("email"), rs.getString("password"));
			newOrder = new Order(rs.getLong("id"), user);
		}
		else if (this.order.getId() == Constants.EMPTY_ID)
		{
			newOrder = new Order(rs.getLong(1), this.order.getUser());
		}
		else
		{
			return this.order;
		}

		return newOrder;
	}

	@Override
	public Type getType()
	{
		return Order.class;
	}

}