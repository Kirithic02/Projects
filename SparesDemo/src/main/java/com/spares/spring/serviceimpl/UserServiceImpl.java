package com.spares.spring.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spares.spring.dao.UserDAO;
import com.spares.spring.model.CartItem;
import com.spares.spring.model.User;
import com.spares.spring.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserDAO userDao;
	
//	@Autowired
//	public void setUserDao(UserDAO userDao) {
//		this.userDao = userDao;
//	}
	
	@Override
	@Transactional
	public User validateUser(String username, String password) {
		User user = this.userDao.findByUsername(username);
		if(user != null && user.getPassword().equalsIgnoreCase(password)) {
			return user;
		}
		return null;
	}

	@Override
	@Transactional
	public User getUserById(int userId) {
		return this.userDao.finduserById(userId);
	}
	
	@Override
	@Transactional
	public boolean addProductToCart(int productId, int userId, int quantity) {
		return userDao.addProductToCart(productId, userId, quantity);
	}

}
