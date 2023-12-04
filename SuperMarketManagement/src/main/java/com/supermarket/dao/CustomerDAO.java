package com.supermarket.dao;

import java.util.Map;

import com.supermarket.model.custom.customer.CustomerDTO;
import com.supermarket.model.custom.customer.CustomerFilterList;
import com.supermarket.model.entity.Customer;

public interface CustomerDAO {

	public int addCustomer(Customer customer);

	Customer getCustomerById(Integer customerId);

	Customer getCustomerByName(String custName);

	Map<String, Object> listCustomer(CustomerFilterList customerFilterList);

	CustomerDTO getCustomerDTOById(int customerId);

	boolean isNotUniqueMobileno(Integer customerId, String mobileNo);

	boolean isNotUniqueMail(Integer customerId, String mail);

//	int isUniqueCustomer(Integer customerId, String mobileNo, String mail);

}
