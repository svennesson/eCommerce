package se.hmpaj.ecommerce.service.session;

import java.util.concurrent.atomic.AtomicLong;

import se.hmpaj.ecommerce.model.Product;
import se.hmpaj.ecommerce.model.User;

public class ECommerceSession
{
	private final static AtomicLong sessionIds = new AtomicLong();
	private final User user;
	private final long id;
	
	public ECommerceSession(User user)
	{
		this.user = user;
		this.id = sessionIds.incrementAndGet();
	}
	
	public long getId()
	{
		return id;
	}
	
	public User getUser()
	{
		return user;
	}
	
    public long addProduct(Long productId, Product product)
	{
	user.getCart().put(productId, product);
		return product.getId();
	}
}