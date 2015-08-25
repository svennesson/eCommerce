package se.hmpaj.ecommerce.service.sql;

public class SqlRepositoryException extends RuntimeException
{
	private static final long serialVersionUID = -5737807323688546307L;
	
	public SqlRepositoryException(String message)
	{
		super(message);
	}
	
	public SqlRepositoryException(String message, Throwable throwable)
	{
		super(message, throwable);
	}
}
