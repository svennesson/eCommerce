package se.hmpaj.ecommerce.web.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import se.hmpaj.ecommerce.model.Product;
import se.hmpaj.ecommerce.model.User;

//@Path("login")
public class UserSession {

	private static final AtomicLong sessionId = new AtomicLong();
	private static Map<Long, List<Product>> sessions;

	public UserSession() {
		sessions = new HashMap<>();
	}

	public static void verifySession(User user) {

	}

}
