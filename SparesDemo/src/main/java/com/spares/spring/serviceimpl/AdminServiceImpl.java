package com.spares.spring.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spares.spring.dao.AdminDAO;
import com.spares.spring.model.Admin;
import com.spares.spring.model.CartItem;
import com.spares.spring.model.Product;
import com.spares.spring.model.User;
import com.spares.spring.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService{
	
	@Autowired
	private AdminDAO adminDAO;
	
//	@Autowired
//	public void setAdminDAO(AdminDAO adminDAO) {
//		this.adminDAO = adminDAO;
//	}
	
	@Override
	@Transactional
	public Admin validateAdmin(String username, String password) {
	    Admin admin = this.adminDAO.findByAdminname(username);
	    if (admin != null && admin.getPassword().equalsIgnoreCase(password)) {
	        return admin;  // User is valid
	    }
	    return null;  // User is invalid
	}
	
	@Override
	@Transactional
	public int validateProduct(Product p) {
		Product product = this.adminDAO.findByName(p.getName());
		if(product != null && product.getName().equalsIgnoreCase(p.getName())) {
			return 0;
		}else {
			this.adminDAO.addProduct(p);
			return 1;
		}
	}
	
	@Override
	@Transactional
	public void updateProduct(Product p) {
		this.adminDAO.updateProduct(p);
	}
	
	@Override
	@Transactional
	public List<Product> listProducts() {
		return this.adminDAO.listProducts();
	}
	
	@Override
	@Transactional
	public Product getProductById(int id) {
		return this.adminDAO.getProductById(id);
	}

	@Override
	@Transactional
	public void removeProduct(int id) {
		this.adminDAO.removeProduct(id);
	}
	
	@Override
	@Transactional
	public List<CartItem> listCartItem() {
		System.out.println("cart items"+adminDAO.listCartItem());
		return this.adminDAO.listCartItem();
	}
}
