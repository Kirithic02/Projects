package com.spares.spring.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.spares.spring.dao.AdminDAO;
import com.spares.spring.model.Admin;
import com.spares.spring.model.Cart;
import com.spares.spring.model.CartItem;
import com.spares.spring.model.Product;
import com.spares.spring.model.User;

@Repository
public class AdminDAOImpl implements AdminDAO {

	private static final Logger logger = LoggerFactory.getLogger(AdminDAOImpl.class);
	
	@Autowired
	private SessionFactory sessionFactory;
	
//	@Autowired
//	public void setSessionFactory(SessionFactory sf){
//		this.sessionFactory = sf;
//	}
	
	@Override
	public Admin findByAdminname(String username) {
	    Session session = this.sessionFactory.getCurrentSession();
	    Query query = session.createQuery("FROM Admin WHERE username = :username");
	    query.setParameter("username", username);
	    return (Admin) query.uniqueResult();  // Returns null if no user found and cast the result to Admin
	}
	
	@Override
	public Product findByName(String name) {
	    Session session = this.sessionFactory.getCurrentSession();
	    Query query = session.createQuery("FROM Product WHERE name = :name");
	    query.setParameter("name", name);
	    return (Product) query.uniqueResult();  // Returns null if no product found and cast the result to Product
	}
	
	@Override
	public void addProduct(Product product) {
		Session session = this.sessionFactory.getCurrentSession();
		session.persist(product);
	}
	
	@Override
	public void updateProduct(Product p) {
		Session session = this.sessionFactory.getCurrentSession();
		session.update(p);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Product> listProducts(){
		Session session = this.sessionFactory.getCurrentSession();
		List<Product> productList = session.createQuery("from Product").list();
		return productList;
	}
	
	@Override
	public Product getProductById(int id) {
		Session session = this.sessionFactory.getCurrentSession();
		Product p = (Product) session.get(Product.class, id);
		return p;
	}

	@Override
	public void removeProduct(int id) {
//		Product p = (Product) sessionFactory.getCurrentSession().load(Product.class, id);
		Session session = this.sessionFactory.getCurrentSession();
		Product p = (Product) session.load(Product.class, new Integer(id));
		if(null != p) {
			session.delete(p);
		}
	}
	
	@Override
	public List<CartItem> listCartItem(){
		Session session = this.sessionFactory.getCurrentSession();
		List<CartItem> cartItemList = session.createQuery("from CartItem").list();
		return cartItemList;
	}
}
