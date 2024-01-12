package com.spares.spring.service;

import java.util.List;

import com.spares.spring.model.CartItem;
import com.spares.spring.model.User;

public interface UserService {
	
	public User validateUser(String username, String password);
	User getUserById(int userId);
	boolean addProductToCart(int productId, int userId, int quantity);
}
