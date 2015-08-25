package se.hmpaj.ecommerce.service.repository.sql;

import java.util.List;

import se.hmpaj.ecommerce.model.User;
import se.hmpaj.ecommerce.service.exception.RepositoryException;
import se.hmpaj.ecommerce.service.exception.SQLRepositoryException;
import se.hmpaj.ecommerce.service.mapper.SQLUserMapper;
import se.hmpaj.ecommerce.service.repository.UserRepository;
import se.hmpaj.ecommerce.service.sql.RowMapper;
import se.hmpaj.ecommerce.service.sql.SqlQuery;

public final class SQLUserRepository implements UserRepository
{
	private static final String SELECT_USER_ID = "SELECT * FROM users WHERE id = ?";
	private static final String SELECT_USER_EMAIL = "SELECT * FROM users WHERE email = ?";
	private static final String INSERT_USER = "INSERT INTO users (email, password) VALUES (?, ?)";
	private static final String UPDATE_USER = "UPDATE users SET email = ?, password = ? WHERE id = ?";
	private static final String DELETE_USER = "DELETE FROM users WHERE id = ?";

	@Override
	public User addUser(final User user)
	{
		try
		{
			final List<User> users = executeQuery(INSERT_USER, new SQLUserMapper(user), user.getEmail(), user.getPassword());

			return users.get(0);
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new RepositoryException("Could not add user " + e);
		}
	}

	@Override
	public User getUserById(final long id)
	{
		try
		{
			final List<User> users = executeQuery(SELECT_USER_ID, new SQLUserMapper(), id);

			return users.get(0);
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new RepositoryException("Could not get user " + e);
		}
	}
	
	@Override
	public User getUserByEmail(String email)
	{
		try
		{
			final List<User> users = executeQuery(SELECT_USER_EMAIL, new SQLUserMapper(), email);

			return users.get(0);
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new RepositoryException("Could not get user " + e);
		}
	}

	@Override
	public User updateUser(final User user)
	{
		try
		{
			final List<User> users = executeQuery(UPDATE_USER, new SQLUserMapper(user), user.getEmail(), user.getPassword(), user.getId());

			return users.get(0);
		}
		catch (Exception e)
		{
			throw new RepositoryException("Could not update user " + e);
		}
	}

	@Override
	public void deleteUser(final long id)
	{
		executeQuery(DELETE_USER, new SQLUserMapper(), id);
	}

	private <T> List<T> executeQuery(final String queryString, RowMapper<T> mapper, final Object... parameters)
	{
		try
		{
			final List<T> result = SqlQuery.getBuilder()
										   .query(queryString)
										   .parameters(parameters)
										   .executeQuery(mapper);

			return result;
		}
		catch (SQLRepositoryException e)
		{
			throw new RepositoryException(e.getMessage());
		}
	}
}