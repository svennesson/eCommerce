package se.hmpaj.ecommerce.service.repository;

import java.util.Map;

import se.hmpaj.ecommerce.model.Product;

public interface ProductRepository
{
	Product addProduct(Product product);

	Product getProduct(long id);
	
	Map<Long, Product> getAllProducts();

	Product updateProduct(Product product);

	Product deleteProduct(long id);
}
