package com.spares.spring.dao;

import java.util.List;

import com.spares.spring.model.Admin;
import com.spares.spring.model.Cart;
import com.spares.spring.model.CartItem;
import com.spares.spring.model.Product;
import com.spares.spring.model.User;

public interface AdminDAO {

	public void addProduct(Product product);
	public void updateProduct(Product p);
	public Admin findByAdminname(String username);
	public Product findByName(String name);
	public List<Product> listProducts();
	public Product getProductById(int id);
	public void removeProduct(int id);
	List<CartItem> listCartItem();
}
