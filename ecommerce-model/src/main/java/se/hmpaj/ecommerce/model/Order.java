package se.hmpaj.ecommerce.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import se.hmpaj.ecommerce.model.util.Constants;

public final class Order
{
	private final long id;
	private final double sum;
	private final User user;
    private List<Product> products = new ArrayList<>();

	public Order(final User user)
	{
		this(Constants.EMPTY_ID, user, null);
	}

	public Order(final long id, final User user)
	{
		this(id, user, null);
	}
	
	public Order(final long id, final List<Product> products)
	{
		this(id, null, products);
	}

	public Order(final Order order, final List<Product> products)
	{
		this(order.getId(), order.getUser(), products);
	}
	
	public Order(final long id, final User user, final List<Product> products)
	{
		this.id = id;
		this.user = user;
		this.products = products;
		if(products == null)
		{
	    this.sum = setSum(user.getCart().values());
		}
		else
		{
			this.sum = setSum(this.products);
		}
		
	}

	public long getId()
	{
		return id;
	}

	public User getUser()
	{
		return user;
	}

	public double getSum()
	{
		return sum;
	}
	
	public List<Product> getProducts()
	{
		return products;
	}

    private double setSum(Collection<Product> products)
	{
		double sum = 0;
		for(Product product : products)
		{
			sum += product.getPrice();
		}
		return sum;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		if (products != null)
		{
			for (Product product : products)
			{
				builder.append("Product: \n" + product.getTitle() + "\n" + product.getPrice());
			}
		}

		return "id: " + id + "\n" + "total price: " + sum + "\n" + builder;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Order)
		{
			Order otherOrder = (Order) other;
			return id == otherOrder.getId() && user.equals(otherOrder.getUser());
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		int result = 1;
		result *= 31 + id;
		result *= 31 + user.hashCode();

		return result;
	}
	
}