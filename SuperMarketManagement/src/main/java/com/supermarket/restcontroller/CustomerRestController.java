package com.supermarket.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.supermarket.model.custom.FilteredResponse;
import com.supermarket.model.custom.Response;
import com.supermarket.model.custom.customer.CustomerDTO;
import com.supermarket.model.custom.customer.CustomerFilterList;
import com.supermarket.service.CustomerService;

@CrossOrigin
@RestController
@RequestMapping(value = "/customer")
public class CustomerRestController {

	/**
	 * Instance of {@link CustomerServiceImpl}
	 */
	@Autowired
	private CustomerService customerService;

	/**
	 * Save or Update Customer
	 * 
	 * @param customerDTO
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "/saveorupdate", method = RequestMethod.POST)
	public ResponseEntity<Response> saveOrUpdate(@RequestBody CustomerDTO customerDTO) {
		return new ResponseEntity<>(customerService.saveOrUpdate(customerDTO), HttpStatus.OK);
	}

	/**
	 * Retrieves Customer Details Using customerId
	 * 
	 * @param customerId
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "/showCustomer/{customerId}", method = RequestMethod.GET)
	public ResponseEntity<Response> showCustomer(@PathVariable int customerId) {
		return new ResponseEntity<>(customerService.getCustomerDTOById(customerId), HttpStatus.OK);
	}

	/**
	 * Retrieves Customer List
	 * 
	 * @param customerFilterList
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "/listCustomer", method = RequestMethod.POST)
	public ResponseEntity<FilteredResponse> listCustomer(@RequestBody CustomerFilterList customerFilterList) {
		return new ResponseEntity<>(customerService.listCustomer(customerFilterList), HttpStatus.OK);
	}
}