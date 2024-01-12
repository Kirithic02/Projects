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

import com.spares.spring.dao.UserDAO;
import com.spares.spring.model.Cart;
import com.spares.spring.model.CartItem;
import com.spares.spring.model.Product;
import com.spares.spring.model.User;

@Repository
public class UserDAOImpl implements UserDAO{
	
	private static final Logger logger = LoggerFactory.getLogger(AdminDAOImpl.class);
	
	@Autowired
	private SessionFactory sessionFactory;
	
//	@Autowired
//	public void setSessionFactory(SessionFactory sf){
//		this.sessionFactory = sf;
//	}
	
	@Override
	public User findByUsername(String username) {
		Session session = this.sessionFactory.getCurrentSession();
	    Query query = session.createQuery("FROM User WHERE username = :username");
	    query.setParameter("username", username);
	    return (User) query.uniqueResult();  
	}

	@Override
	public User finduserById(int id) {
	    Session session = this.sessionFactory.getCurrentSession();
	    Query query = session.createQuery("FROM User WHERE id = :id");
	    query.setParameter("id", id);
	    return (User) query.uniqueResult();  
	}
	
	@Override
    public boolean addProductToCart(int productId, int userId, int quantity) {
        Session session = sessionFactory.getCurrentSession();
        Cart cart = findCartByUserId(userId);
        if(cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            session.persist(cart);
        }
        CartItem cartItem = new CartItem();
//      cartItem.setCartId(cart.getId());
        cartItem.setCartId(cart);
        cartItem.setProductId(productId);
        cartItem.setItemQuantity(quantity);
        session.persist(cartItem);
        return true;
    }
	
	private Cart findCartByUserId(int userId) {
	    Session session = sessionFactory.getCurrentSession();
	    Query query = session.createQuery("FROM Cart WHERE userId = :userId");
	    query.setParameter("userId", userId);
	    return (Cart) query.uniqueResult();  
	}
}
