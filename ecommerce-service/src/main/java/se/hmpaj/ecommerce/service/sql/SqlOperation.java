package se.hmpaj.ecommerce.service.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class SqlOperation<T> extends SqlConnection
{
	private final RowMapper<T> mapper;
	private final String queryString;
	private final List<Object> parameters;

	public SqlOperation(RowMapper<T> mapper, String queryString, List<Object> parameters)
	{
		this.mapper = mapper;
		this.queryString = queryString;
		this.parameters = parameters;
	}

	public List<T> execute()
	{
		if (queryString.toUpperCase().contains("SELECT"))
		{
			return executeSelectQuery(queryString);
		}
		else
		{
			return executeUpdateQuery(queryString);
		}
	}

	private List<T> executeSelectQuery(String queryString)
	{
		try (final Connection connection = getConnection())
		{
			PreparedStatement stmt = connection.prepareStatement(queryString);

			setStatementValues(stmt);

			List<T> result = new ArrayList<>();

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{
				result.add(mapper.mapRow(rs));
			}

			return result;
		}
		catch (SQLException e)
		{
			throw new SqlRepositoryException(e.getMessage());
		}
	}

	private List<T> executeUpdateQuery(String queryString)
	{
		try (final Connection connection = getConnection())
		{
			connection.setAutoCommit(false);

			try (final PreparedStatement stmt = connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS))
			{
				setStatementValues(stmt);

				final int affectedRows = stmt.executeUpdate();
				List<T> result = new ArrayList<>();

				if (affectedRows > 0)
				{
					ResultSet rs = stmt.getGeneratedKeys();

					if (queryString.toUpperCase().contains("DELETE") || queryString.toUpperCase().contains("UPDATE"))
					{
						connection.commit();
						result.add(mapper.mapRow(rs));

						return result;
					}
					else
					{
						if (rs.next())
						{
							do
							{
								result.add(mapper.mapRow(rs));
							}
							while (rs.next());

							// connection.commit();
						}

						if (result.size() == 0)
						{
							result.add(mapper.mapRow(rs));
						}

						connection.commit();
						return result;
					}
				}
				throw new SqlRepositoryException("Not found");
			}
			catch (SQLException e)
			{
				connection.rollback();
				throw new SqlRepositoryException(e.getMessage());
			}
		}
		catch (SQLException e)
		{
			throw new SqlRepositoryException(e.getMessage());
		}
	}

	private void setStatementValues(PreparedStatement stmt) throws SQLException
	{
		for (int i = 1; i <= parameters.size(); i++)
		{
			Object obj = parameters.get(i - 1);
			stmt.setObject(i, obj);
		}
	}
}
