package se.hmpaj.ecommerce.model;

import java.util.HashMap;
import java.util.Map;

import se.hmpaj.ecommerce.model.util.Constants;

public final class User
{
	private final long id;
	private final String email;
	private final String password;
    private Map<Long, Product> cart;
	
	public User(final String email, final String password)
	{
		this(Constants.EMPTY_ID, email, password);
	}
	
	public User(final long id, final String email, final String password)
	{
		this.id = id;
		this.email = email.toLowerCase();
		this.password = password;
	this.cart = new HashMap<>();
	}
	
	public long getId()
	{
		return id;
	}
	
	public String getEmail()
	{
		return email;
	}
	
	public String getPassword()
	{
		return password;
	}
	
    public Map<Long, Product> getCart()
	{
		return cart;
	}
	
	@Override
	public String toString()
	{
		return "id: " + id + "\n" + "email: " + email + "\n" + "password: " + password; 
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof User)
		{
			User otherUser = (User) other;
			return this.id == otherUser.getId() && email.equals(otherUser.getEmail());
		}
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
		int result = 1;
		result *= 31 + id;
		result *= 31 + email.hashCode();
		
		return result;
	}
}