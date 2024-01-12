package com.spares.spring.dao;

import java.util.List;

import com.spares.spring.model.CartItem;
import com.spares.spring.model.User;

public interface UserDAO {
	
	public User findByUsername(String username);
	boolean addProductToCart(int productId, int userId, int quantity);
	User finduserById(int id);
//	public Cart findCartByUserId(int userId);
}
