package com.spares.spring;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.spares.spring.model.CartItem;
import com.spares.spring.model.Product;
import com.spares.spring.model.User;
import com.spares.spring.service.AdminService;
import com.spares.spring.service.UserService;

@Controller
public class UserController {
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private UserService userService;
	
//	@Autowired
//	public void setAdminService(AdminService adminService) {
//		this.adminService = adminService;
//	}
//	
//	@Autowired
//	public void setUserService(UserService userService) {
//		this.userService = userService;
//	}
	
	@RequestMapping(value = "/userPageC",method = RequestMethod.POST)
	public String userPage(@Validated User inputUser, Model model) {
		User user = userService.validateUser(inputUser.getUsername(), inputUser.getPassword());
		if(user != null) {
			model.addAttribute("user", user);
			model.addAttribute("product", new Product());
			model.addAttribute("listProducts", this.adminService.listProducts());
			return "userLoginPage";
		} else {
			model.addAttribute("usererror", "Invalid username or password");
			return "adminLoginPage";
		}
	}
	
	@RequestMapping(value = "/userShowProductC/{userId}", method = {RequestMethod.POST, RequestMethod.GET})
	public String userlistProducts(@PathVariable("userId") int userId, Model model) {
		User user = userService.getUserById(userId);
		model.addAttribute("user", user);
		model.addAttribute("product", new Product());
		model.addAttribute("listProducts", this.adminService.listProducts());
		return "UserProductViewPage";
	}
	
	@RequestMapping(value = "/addToCartC/{productId}/{userId}", method = RequestMethod.POST)
	public String addToCart(@PathVariable("productId") int productId, 
	                        @PathVariable("userId") int userId, 
	                        @RequestParam("quantity") int quantity,
	                        Model model) {
	     
	    boolean isAdded = userService.addProductToCart(productId, userId, quantity);
	    
	    if (isAdded) {
			model.addAttribute("cartMessage", "Product added successfully!");
		} else {
			model.addAttribute("cartMessage", "There was an error adding the product!");
		}
		User user = userService.getUserById(userId);
		model.addAttribute("user", user);
		model.addAttribute("product", new Product());
		model.addAttribute("listProducts", this.adminService.listProducts());
		return "UserProductViewPage";
	}
}
