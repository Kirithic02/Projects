package com.supermarket.service;

import com.supermarket.model.custom.FilteredResponse;
import com.supermarket.model.custom.Response;
import com.supermarket.model.custom.customer.CustomerDTO;
import com.supermarket.model.custom.customer.CustomerFilterList;

public interface CustomerService {

	Response getCustomerDTOById(int customerId);

	Response saveOrUpdate(CustomerDTO customerDTO);

	FilteredResponse listCustomer(CustomerFilterList customerFilterList);

	Response login(String mail, String password);
}
