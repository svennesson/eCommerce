package se.hmpaj.ecommerce.service.sql;

import java.util.ArrayList;
import java.util.List;

public abstract class SqlQuery
{
	private String query;
	private List<Object> parameters;
	
	public SqlQuery()
	{
		this.parameters = new ArrayList<>();
	}
	
	public String getQuery()
	{
		return query;
	}
	
	private void setQuery(String query)
	{
		this.query = query;
	}
	
	public List<Object> getParameters()
	{
		return parameters;
	}
	
	private void setParameters(Object... parameters)
	{
		for(Object obj : parameters)
		{
			this.parameters.add(obj);
		}
	}
	
	public static QueryBuilder getBuilder()
	{
		return new QueryBuilder();
	}
	
	public static final class QueryBuilder
	{
		private final SqlQuery query;
		
		public QueryBuilder()
		{
			this.query = new SqlQuery(){};
		}
		
		public QueryBuilder query(String queryString)
		{
			query.setQuery(queryString);
			return this;
		}
		
		public QueryBuilder parameters(Object... parameters)
		{
			query.setParameters(parameters);
			return this;
		}
		
		public <T> List<T> executeQuery(RowMapper<T> mapper)
		{
			return new SqlOperation<T>(mapper, query.getQuery(), query.getParameters()).execute();
		}
	}
	
}