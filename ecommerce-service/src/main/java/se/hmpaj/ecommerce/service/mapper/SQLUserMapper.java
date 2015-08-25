package se.hmpaj.ecommerce.service.mapper;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;

import se.hmpaj.ecommerce.model.User;
import se.hmpaj.ecommerce.model.util.Constants;
import se.hmpaj.ecommerce.service.sql.RowMapper;

public final class SQLUserMapper implements RowMapper<User>
{
	private final User user;

	public SQLUserMapper()
	{
		this(null);
	}

	public SQLUserMapper(User user)
	{
		this.user = user;
	}

	@Override
	public User mapRow(ResultSet rs) throws SQLException
	{
		User newUser = null;

		if (this.user == null)
		{
			newUser = new User(rs.getLong("id"), rs.getString("email"), rs.getString("password"));
		}
		else if (this.user.getId() == Constants.EMPTY_ID)
		{
	    newUser = new User(rs.getLong(1), this.user.getEmail(),
		    this.user.getPassword());
		}
		else
		{
			return this.user;
		}

		return newUser;
	}

	@Override
	public Type getType()
	{
		return User.class;
	}
}