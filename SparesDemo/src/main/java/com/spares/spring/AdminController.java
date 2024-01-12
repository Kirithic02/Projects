package com.spares.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.spares.spring.model.Admin;
import com.spares.spring.model.Product;
import com.spares.spring.service.AdminService;

@Controller
public class AdminController {
	
	@Autowired
	private AdminService adminService;
	
//	@Autowired
//	public void setAdminService(AdminService adminService) {
//		this.adminService = adminService;
//	}
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String adminLogin() {
		return "adminLoginPage";
	}
	
	@RequestMapping(value = "/adminPageC",method = RequestMethod.POST)
	public String adminPage(@Validated Admin inputAdmin, Model model) {
	    Admin admin = adminService.validateAdmin(inputAdmin.getUsername(), inputAdmin.getPassword());
	    if (admin != null) {
	        model.addAttribute("admin", admin);
	        return "adminPage";
	    } else {
	        model.addAttribute("adminerror", "Invalid username or password");
	        return "adminLoginPage";
	    }
	}
	
	@RequestMapping(value = "/gotoAdminPageC", method = RequestMethod.GET)
	public String gotoAdminPage() {
		return "adminPage";
	}
	
	@RequestMapping(value = "/addNewC", method = RequestMethod.POST)
	public String addProduct(@Validated Product product, Model model) {		
		if(product.getId() == 0) {
			//new person, add it
			int check = this.adminService.validateProduct(product);
			if(check == 0) {
				model.addAttribute("addMessage", "Product Already Exist");
			}else {
				model.addAttribute("addMessage", "New Product Has Been Added");
			}
		}else {
			//existing person, call update
			this.adminService.updateProduct(product);
			model.addAttribute("addMessage", "Product Details Has Been Updated");
		}
		model.addAttribute("product", new Product());
		model.addAttribute("listProducts", this.adminService.listProducts());
		return "addNewProductPage";
	}
	
	@RequestMapping(value = "/adminShowCartItemsC", method = RequestMethod.POST)
	public String adminListCartItems(Model model) {
		model.addAttribute("product", new Product());
		model.addAttribute("cartItems", this.adminService.listCartItem());
		return "viewCartItems";
	}
	
	@RequestMapping(value = "/adminShowProductC", method = RequestMethod.POST)
	public String adminlistProducts(Model model) {
		model.addAttribute("product", new Product());
		model.addAttribute("listProducts", this.adminService.listProducts());
		return "addNewProductPage";
	}
	
	@RequestMapping(value = "/editC/{id}")
	public String editProduct(@PathVariable("id") int id, Model model) {
		model.addAttribute("product", this.adminService.getProductById(id));
		model.addAttribute("listProducts", this.adminService.listProducts());
		return "addNewProductPage";
	}
	
	@RequestMapping(value = "/removeC/{id}")
	public String removeProduct(@PathVariable("id") int id, Model model) {
		this.adminService.removeProduct(id);
		model.addAttribute("addMessage", "Product Has Been Deleted");
		model.addAttribute("product", new Product());
		model.addAttribute("listProducts", this.adminService.listProducts());
		return "addNewProductPage";
	}
}
