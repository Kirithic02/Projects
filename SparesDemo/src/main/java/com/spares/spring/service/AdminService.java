package com.spares.spring.service;


import java.util.List;

import com.spares.spring.model.Admin;
import com.spares.spring.model.CartItem;
import com.spares.spring.model.Product;
import com.spares.spring.model.User;

public interface AdminService {

	public int validateProduct(Product p);
	public void updateProduct(Product p);
	public Admin validateAdmin(String username, String password);
	public List<Product> listProducts();
	public Product getProductById(int id);
	public void removeProduct(int id);
	List<CartItem> listCartItem();
}
