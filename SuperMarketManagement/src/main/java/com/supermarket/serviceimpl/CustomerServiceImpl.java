package com.supermarket.serviceimpl;

import java.util.ArrayList;
import java.util.Collections;
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
	 * Customer Login
	 * 
	 * @param customerDTO
	 * @return
	 */
	@Override
	@Transactional
	public Response login(String mail, String password) {
		
		if(!( ValidationUtil.isNotEmpty(mail) && ValidationUtil.isNotEmpty(password) )) {
			throw new NullPointerException("mail and password is Null");
		} else if(!ValidationUtil.isNotEmpty(mail)) {
			throw new NullPointerException("mail is Null");
		} else if(!ValidationUtil.isNotEmpty(password)) {
			throw new NullPointerException("password is Null");
		}
		
		Customer customer = customerDAO.getCustomerDTOByMail(mail);

		List<ErrorResponse> errorResponseList = new ArrayList<ErrorResponse>();
		Response response = new Response();

		if (customer != null) {

			System.out.println(customer.getMail());
			System.out.println(customer.getPassword());

			if (password.equals(customer.getPassword())) {

				CustomerDTO customerDTO = new CustomerDTO();

				customerDTO.setCustomerId(customer.getCustomerId());
				customerDTO.setCustomerName((customer.getCustomerName()));
				customerDTO.setMobileNo(customer.getMobileNo());
				customerDTO.setMail(customer.getMail());
				customerDTO.setAddress(customer.getAddress());
				customerDTO.setLocation(customer.getLocation());
				customerDTO.setCity(customer.getCity());
				customerDTO.setPincode(customer.getPincode());

				response.setStatus(WebServiceUtil.SUCCESS);
				response.setData(customerDTO);
			} else {

				ErrorResponse errorResponse = new ErrorResponse();
				errorResponse.setFieldName(WebServiceUtil.CUSTOMER_PASSWORD);
				errorResponse.setErrorMessage("Incorrect Password");
				errorResponseList.add(errorResponse);
			}
		} else {

			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.CUSTOMER_MAIL);
			errorResponse.setErrorMessage("Incorrect Mail");
			errorResponseList.add(errorResponse);
		}

		if (!errorResponseList.isEmpty()) {

			response.setStatus(WebServiceUtil.FAILURE);
			response.setData(errorResponseList);
		}

		return response;
	}

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

			int checkValue = 0;

			if (customerDAO.isNotUniqueMobileno(customerDTO.getCustomerId(), customerDTO.getMobileNo())) {
				checkValue += 1;
			}

			if (customerDAO.isNotUniqueMail(customerDTO.getCustomerId(), customerDTO.getMail())) {
				checkValue += 2;
			}

			if (checkValue == 0) {

				if (customerDTO.getCustomerId() == null) {
					Customer customer = new Customer();
					customer.setCustomerName(WebServiceUtil.formatFullName(customerDTO.getCustomerName()).trim()); // need
																													// changes
					customer.setMobileNo(customerDTO.getMobileNo());
					customer.setMail(customerDTO.getMail());
					customer.setPassword(customerDTO.getPassword());
					customer.setAddress(customerDTO.getAddress());
					customer.setLocation(WebServiceUtil.formatFullName(customerDTO.getLocation()).trim());
					customer.setCity(WebServiceUtil.formatFullName(customerDTO.getCity()).trim());
					customer.setPincode(customerDTO.getPincode());
					customer.setCreatedDate(new Date());

					response.setStatus(WebServiceUtil.SUCCESS);
					customerDAO.addCustomer(customer);
					response.setData("Customer Added Successfully"); // + customerDAO.addCustomer(customer));

				} else {
					Customer ExistingCustomer = customerDAO.getCustomerById(customerDTO.getCustomerId());

					if (ExistingCustomer != null) {
						ExistingCustomer
								.setCustomerName(WebServiceUtil.formatFullName(customerDTO.getCustomerName()).trim());
						ExistingCustomer.setMobileNo(customerDTO.getMobileNo());
						ExistingCustomer.setMail(customerDTO.getMail());
						ExistingCustomer.setPassword(customerDTO.getPassword());
						ExistingCustomer.setAddress(customerDTO.getAddress());
						ExistingCustomer.setCity(WebServiceUtil.formatFullName(customerDTO.getCity()).trim());
						ExistingCustomer.setLocation(WebServiceUtil.formatFullName(customerDTO.getLocation()).trim());
						ExistingCustomer.setPincode(customerDTO.getPincode());

						response.setStatus(WebServiceUtil.SUCCESS);
						response.setData("Customer Details Updated Succesfully ");

					} else {
						response.setStatus(WebServiceUtil.FAILURE);
						response.setData("Customer Not Found");
					}
				}

			} else {

				if (checkValue == 1 || checkValue == 3) {

					ErrorResponse errorResponse = new ErrorResponse();
					errorResponse.setFieldName(WebServiceUtil.CUSTOMER_MOBILE_NUMBER);
					errorResponse.setErrorMessage("MobileNo Already Exist");
					errorResponseList.add(errorResponse);
				}

				if (checkValue == 2 || checkValue == 3) {

					ErrorResponse errorResponse = new ErrorResponse();
					errorResponse.setFieldName(WebServiceUtil.CUSTOMER_MAIL);
					errorResponse.setErrorMessage("Mail Already Exist");
					errorResponseList.add(errorResponse);

				}
			}

			if (!errorResponseList.isEmpty()) {

				response.setStatus(WebServiceUtil.FAILURE);
				response.setData(errorResponseList);
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

		if (customerDTO.getCustomerName() == null || customerDTO.getCustomerName().trim().isEmpty()
				|| !ValidationUtil.isValidName(customerDTO.getCustomerName())) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.CUSTOMER_NAME);
			errorResponse.setErrorMessage("Customer Name Should Contain only Alphabets");
			errorResponseList.add(errorResponse);
		}

		if (customerDTO.getMobileNo() == null || customerDTO.getMobileNo().trim().isEmpty()
				|| !ValidationUtil.isValidPhoneNumber(customerDTO.getMobileNo())) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.CUSTOMER_MOBILE_NUMBER);
//			errorResponse.setErrorMessage("Mobile Number Should Contain only Numbers and Should Not be null");
			errorResponse.setErrorMessage("Invalid Mobile Number");
			errorResponseList.add(errorResponse);
		}

		if (customerDTO.getMail() == null || customerDTO.getMail().trim().isEmpty()
				|| !ValidationUtil.isValidEmail(customerDTO.getMail())) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.CUSTOMER_MAIL);
			errorResponse.setErrorMessage("Email is invalid");
			errorResponseList.add(errorResponse);
		}

		if (customerDTO.getPassword() == null || customerDTO.getPassword().trim().isEmpty()
				|| !ValidationUtil.isValidPassword(customerDTO.getPassword())) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.CUSTOMER_PASSWORD);
			errorResponse.setErrorMessage(
					"Password must contain at least one alphabetical character, one number, one special character and minimum length of 8 characters.");
			errorResponseList.add(errorResponse);
		}

		if (!ValidationUtil.isValidAddressLine(customerDTO.getAddress())) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.CUSTOMER_ADDRESS);
			errorResponse.setErrorMessage("Invalid Address");
			errorResponseList.add(errorResponse);
		}

		if (customerDTO.getLocation() == null || customerDTO.getLocation().trim().isEmpty()
				|| !ValidationUtil.isValidName(customerDTO.getLocation())) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.CUSTOMER_LOCATION);
			errorResponse.setErrorMessage("Location Should Contain only Alphabets");
			errorResponseList.add(errorResponse);
		}

		if (customerDTO.getCity() == null || customerDTO.getCity().trim().isEmpty()
				|| !ValidationUtil.isValidName(customerDTO.getCity())) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.CUSTOMER_CITY);
			errorResponse.setErrorMessage("City Name Should Contain only Alphabets");
			errorResponseList.add(errorResponse);
		}

		if (customerDTO.getPincode() == null || customerDTO.getPincode().trim().isEmpty()
				|| !ValidationUtil.isValidPincode(customerDTO.getPincode())) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.CUSTOMER_PINCODE);
//			errorResponse.setErrorMessage("Pincode Should Contain only Numbers and Should Not be null");
			errorResponse.setErrorMessage("Invalid Pincode Number");
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
			response.setData("Customer Not Found");
			return response;
		}
	}

	/**
	 * Retrieves Customer List
	 * 
	 * @param customerFilterList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public FilteredResponse listCustomer(CustomerFilterList customerFilterList) {

		LOGGER.info("View Customer List");

		List<ErrorResponse> errorResponseList = customerFilterListValidation(customerFilterList);
		FilteredResponse filteredResponse = new FilteredResponse();

		if (errorResponseList.isEmpty()) {

			Map<String, Object> resultMap = customerDAO.listCustomer(customerFilterList);

			List<CustomerDTO> transactionDetails = (List<CustomerDTO>) resultMap.get("data");

			if (customerFilterList.getOrderBy().getColumn() != null && customerFilterList.getOrderBy().getColumn().equalsIgnoreCase("serialNumber")
					&& customerFilterList.getOrderBy().getType().equalsIgnoreCase("desc")) {
				Collections.reverse(transactionDetails);
				for (Integer i = transactionDetails.size() - 1; i >= 0; i--) {
					transactionDetails.get(i)
							.setSerialNumber(customerFilterList.getStart() + transactionDetails.size() - i);
				}
			} else {
				for (Integer i = 0; i < transactionDetails.size(); i++) {
					transactionDetails.get(i).setSerialNumber(customerFilterList.getStart() + i + 1);
				}
			}

			filteredResponse.setStatus(WebServiceUtil.SUCCESS);
			filteredResponse.setRecordsTotal((Long) resultMap.get(WebServiceUtil.FILTEREDRESPONSE_RECORDSTOTAL));
			filteredResponse.setRecordsFiltered((Long) resultMap.get(WebServiceUtil.FILTEREDRESPONSE_RECORDSFILTERED));
			filteredResponse.setData(transactionDetails);

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
			errorResponse.setFieldName(WebServiceUtil.FILTERLIST_LENGTH);
			errorResponse.setErrorMessage("Length Should be greater than 0");
			errorResponseList.add(errorResponse);
		}

		if (customerFilterList.getStart() == null) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.FILTERLIST_START);
			errorResponse.setErrorMessage("Start Should not be null");
			errorResponseList.add(errorResponse);
		}

		if (!(customerFilterList.getSearchColumn() == null
				|| customerFilterList.getSearchColumn().equalsIgnoreCase(WebServiceUtil.CUSTOMER_ID)
				|| customerFilterList.getSearchColumn().equalsIgnoreCase(WebServiceUtil.CUSTOMER_NAME)
				|| customerFilterList.getSearchColumn().equalsIgnoreCase(WebServiceUtil.CUSTOMER_MOBILE_NUMBER)
				|| customerFilterList.getSearchColumn().equalsIgnoreCase(WebServiceUtil.CUSTOMER_MAIL)
				|| customerFilterList.getSearchColumn().trim().isEmpty())) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.FILTERLIST_SEARCHCOLUMN);
			errorResponse.setErrorMessage(
					"searchColumn Should Contain Only customerid (or) customername (or) mobileno (or) mail");
			errorResponseList.add(errorResponse);

		}

//		if (customerFilterList.getOrderBy() != null
//				&& ValidationUtil.isNotEmpty(customerFilterList.getOrderBy().getType())
//				&& ValidationUtil.isNotEmpty(customerFilterList.getOrderBy().getColumn())) {
//			
//			if (!(customerFilterList.getOrderBy().getColumn().equalsIgnoreCase(WebServiceUtil.CUSTOMER_NAME)
//					|| customerFilterList.getOrderBy().getColumn().equalsIgnoreCase(WebServiceUtil.CUSTOMER_CREATEDDATE))) {
//				ErrorResponse errorResponse = new ErrorResponse();
//				errorResponse.setFieldName(WebServiceUtil.FILTERLIST_ORDERBY_COLUMN);
//				errorResponse.setErrorMessage("column Should Contain Only customername (or) createddate (or) null");
//				errorResponseList.add(errorResponse);
//			}
//			
//			if( !(customerFilterList.getOrderBy().getType().equalsIgnoreCase(WebServiceUtil.FILTERLIST_ORDERBY_TYPE_ASC)
//					|| customerFilterList.getOrderBy().getType().equalsIgnoreCase(WebServiceUtil.FILTERLIST_ORDERBY_TYPE_DESC)) ) {
//				ErrorResponse errorResponse = new ErrorResponse();
//				errorResponse.setFieldName(WebServiceUtil.FILTERLIST_ORDERBY_TYPE);
//				errorResponse.setErrorMessage("type Should Contain Only asc (or) desc (or) null");
//				errorResponseList.add(errorResponse);
//			}
//		}

		return errorResponseList;
	}

}
