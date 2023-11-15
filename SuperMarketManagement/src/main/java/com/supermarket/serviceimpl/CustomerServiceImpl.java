package com.supermarket.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.supermarket.dao.CustomerDAO;
import com.supermarket.model.custom.ErrorResponse;
import com.supermarket.model.custom.FilteredResponse;
import com.supermarket.model.custom.Response;
import com.supermarket.model.custom.customer.CustomerDTO;
import com.supermarket.model.custom.customer.CustomerFilterList;
import com.supermarket.model.entity.Customer;
import com.supermarket.service.CustomerService;
import com.supermarket.util.ValidationUtil;
import com.supermarket.util.WebServiceUtil;

@Service
public class CustomerServiceImpl implements CustomerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);

	/**
	 * Instance of {@link CustomerDAOImpl}
	 */
	@Autowired
	private CustomerDAO customerDAO;

	/**
	 * Save or Update Customer
	 * 
	 * @param customerDTO
	 * @return
	 */
	@Override
	@Transactional
	public Response saveOrUpdate(CustomerDTO customerDTO) {

		Response response = new Response();

		List<ErrorResponse> errorResponseList = customerValiation(customerDTO);

		if (errorResponseList.isEmpty()) {

			if (customerDAO.isUniqueCustomer(customerDTO.getCustomerId(), customerDTO.getMobileNo(),
					customerDTO.getMail())) {

				if (customerDTO.getCustomerId() == null) {
					Customer customer = new Customer();
					customer.setCustomerName(customerDTO.getCustomerName().trim());
					customer.setMobileNo(customerDTO.getMobileNo());
					customer.setAddress(customerDTO.getAddress());
					customer.setLocation(customerDTO.getLocation());
					customer.setCity(customerDTO.getCity());
					customer.setPincode(customerDTO.getPincode());
					customer.setMail(customerDTO.getMail());
					customer.setCreatedDate(new Date());

					response.setStatus(WebServiceUtil.SUCCESS);
					response.setData("Customer Added Successfully, ID : " + customerDAO.addCustomer(customer));

				} else {
					Customer ExistingCustomer = customerDAO.getCustomerById(customerDTO.getCustomerId());

					if (ExistingCustomer != null) {
						ExistingCustomer.setCustomerName(customerDTO.getCustomerName().trim());
						ExistingCustomer.setMobileNo(customerDTO.getMobileNo());
						ExistingCustomer.setAddress(customerDTO.getAddress());
						ExistingCustomer.setCity(customerDTO.getCity());
						ExistingCustomer.setLocation(customerDTO.getLocation());
						ExistingCustomer.setPincode(customerDTO.getPincode());
						ExistingCustomer.setMail(customerDTO.getMail());

						response.setStatus(WebServiceUtil.SUCCESS);
						response.setData("Customer Details Updated Succesfully ");

					} else {
						response.setStatus(WebServiceUtil.FAILURE);
						response.setData("Customer Id " + customerDTO.getCustomerId() + " Not Found");
					}
				}

			} else {
				response.setStatus(WebServiceUtil.FAILURE);
				response.setData("mail or mobileNo Already Exist");
			}

		} else {
			response.setStatus(WebServiceUtil.FAILURE);
			response.setData(errorResponseList);
		}

		LOGGER.info("Save or Update Customer ID : " + customerDTO.getCustomerId());

		return response;
	}

	public List<ErrorResponse> customerValiation(CustomerDTO customerDTO) {

		List<ErrorResponse> errorResponseList = new ArrayList<ErrorResponse>();

		if (customerDTO.getCustomerName() == null || customerDTO.getCustomerName().isBlank()
				|| !ValidationUtil.isValidName(customerDTO.getCustomerName())) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName("customerName");
			errorResponse.setErrorMessage("Customer Name Should Contain only Alphabets and Should Not be Null");
			errorResponseList.add(errorResponse);
		}

		if (customerDTO.getMobileNo() == null || customerDTO.getMobileNo().isBlank()
				|| !ValidationUtil.isValidPhoneNumber(customerDTO.getMobileNo())) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName("mobileNo");
			errorResponse.setErrorMessage("Mobile Number Should Contain only Numbers and Should Not be Null");
			errorResponseList.add(errorResponse);
		}

		if (!ValidationUtil.isValidAddressLine(customerDTO.getAddress())) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName("address");
			errorResponse.setErrorMessage("Address is invalid or null");
			errorResponseList.add(errorResponse);
		}

		if (customerDTO.getLocation() == null || customerDTO.getLocation().isBlank()
				|| !ValidationUtil.isValidName(customerDTO.getLocation())) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName("location");
			errorResponse.setErrorMessage("Location Should Contain only Alphabets and Should Not be Null");
			errorResponseList.add(errorResponse);
		}

		if (customerDTO.getCity() == null || customerDTO.getCity().isBlank()
				|| !ValidationUtil.isValidName(customerDTO.getCity())) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName("city");
			errorResponse.setErrorMessage("City Name Should Contain only Alphabets and Should Not be Null");
			errorResponseList.add(errorResponse);
		}

		if (customerDTO.getPincode() == null || customerDTO.getPincode().isBlank()
				|| !ValidationUtil.isValidPincode(customerDTO.getPincode())) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName("pincode");
			errorResponse.setErrorMessage("Pincode Should Contain only Numbers and Should Not be Null");
			errorResponseList.add(errorResponse);
		}

		if (customerDTO.getMail() == null || customerDTO.getMail().isBlank()
				|| !ValidationUtil.isValidEmail(customerDTO.getMail())) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName("mail");
			errorResponse.setErrorMessage("Email is invalid or null");
			errorResponseList.add(errorResponse);
		}
		return errorResponseList;
	}

	/**
	 * Retrieves Customer Details Using customerId
	 * 
	 * @param customerId
	 * @return
	 */
	@Override
	@Transactional
	public Response getCustomerDTOById(int customerId) {

		LOGGER.info("View Details for Customer ID : " + customerId);

		CustomerDTO customerDTO = customerDAO.getCustomerDTOById(customerId);
		Response response = new Response();

		if (customerDTO != null) {
			response.setStatus(WebServiceUtil.SUCCESS);
			response.setData(customerDTO);
			return response;
		} else {
			response.setStatus(WebServiceUtil.FAILURE);
			response.setData("Customer ID " + customerId + " Not Found");
			return response;
		}
	}

	/**
	 * Retrieves Customer List
	 * 
	 * @param customerFilterList
	 * @return
	 */
	@Override
	@Transactional
	public FilteredResponse listCustomer(CustomerFilterList customerFilterList) {

		LOGGER.info("View Customer List");

		List<ErrorResponse> errorResponseList = customerFilterListValidation(customerFilterList);
		FilteredResponse filteredResponse = new FilteredResponse();

		if (errorResponseList.isEmpty()) {

			Map<String, Object> resultMap = customerDAO.listCustomer(customerFilterList);

			if ((Long) resultMap.get("filteredCount") > 0) {
				filteredResponse.setStatus(WebServiceUtil.SUCCESS);
				filteredResponse.setTotalCount((Long) resultMap.get("totalCount"));
				filteredResponse.setFilteredCount((Long) resultMap.get("filteredCount"));
				filteredResponse.setData(resultMap.get("data"));
			} else {
				filteredResponse.setStatus(WebServiceUtil.SUCCESS);
				filteredResponse.setTotalCount((Long) resultMap.get("totalCount"));
				filteredResponse.setFilteredCount((Long) resultMap.get("filteredCount"));
				filteredResponse.setData(resultMap.get("No Matching Records Found"));
			}
		} else {
			filteredResponse.setStatus(WebServiceUtil.FAILURE);
			filteredResponse.setData(errorResponseList);
		}

		return filteredResponse;
	}

	private List<ErrorResponse> customerFilterListValidation(CustomerFilterList customerFilterList) {

		List<ErrorResponse> errorResponseList = new ArrayList<ErrorResponse>();

		if (customerFilterList.getLength() == null || customerFilterList.getLength() < 1) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName("length");
			errorResponse.setErrorMessage("Length Should be greater than 0 and Should not be Null");
			errorResponseList.add(errorResponse);
		}

		if (customerFilterList.getStart() == null) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName("start");
			errorResponse.setErrorMessage("Start Should not be Null");
			errorResponseList.add(errorResponse);
		}

		if ( !(customerFilterList.getSearchColumn() == null || customerFilterList.getSearchColumn().equalsIgnoreCase("customername")
				|| customerFilterList.getSearchColumn().equalsIgnoreCase("mobileno")
				|| customerFilterList.getSearchColumn().equalsIgnoreCase("mail")
				|| customerFilterList.getSearchColumn().isBlank()) ) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName("searchColumn");
			errorResponse.setErrorMessage("searchColumn Should Contain Only CUSTOMERNAME (or) MOBILENO (or) MAIL");
			errorResponseList.add(errorResponse);
		} 
		
		if (customerFilterList.getOrderBy() != null
				&& ValidationUtil.isNotEmpty(customerFilterList.getOrderBy().getType())
				&& ValidationUtil.isNotEmpty(customerFilterList.getOrderBy().getColumn())) {
			
			if (!(customerFilterList.getOrderBy().getColumn().equalsIgnoreCase("customername")
					|| customerFilterList.getOrderBy().getColumn().equalsIgnoreCase("createddate"))) {
				ErrorResponse errorResponse = new ErrorResponse();
				errorResponse.setFieldName("column");
				errorResponse.setErrorMessage("column Should Contain Only CUSTOMERNAME (or) CREATEDDATE (or) NULL");
				errorResponseList.add(errorResponse);
			}
		}

		return errorResponseList;
	}

}
