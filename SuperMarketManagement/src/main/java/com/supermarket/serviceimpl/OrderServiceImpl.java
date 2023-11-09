package com.supermarket.serviceimpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.supermarket.dao.CustomerDAO;
import com.supermarket.dao.OrderDAO;
import com.supermarket.dao.ProductDAO;
import com.supermarket.model.custom.ErrorResponse;
import com.supermarket.model.custom.FilteredResponse;
import com.supermarket.model.custom.Response;
import com.supermarket.model.custom.order.CustomerOrderDTO;
import com.supermarket.model.custom.order.OrderFilterList;
import com.supermarket.model.custom.order.OrderLineItemDetailsDTO;
import com.supermarket.model.entity.Customer;
import com.supermarket.model.entity.OrderDetails;
import com.supermarket.model.entity.OrderLineItemDetails;
import com.supermarket.model.entity.Product;
import com.supermarket.service.OrderService;
import com.supermarket.util.WebServiceUtil;

@Service
public class OrderServiceImpl implements OrderService {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

	/**
	 * Instance of {@link OrderDAOImpl}
	 */
	@Autowired
	private OrderDAO orderDAO;

	/**
	 * Instance of {@link CustomerDAOImpl}
	 */
	@Autowired
	private CustomerDAO customerDAO;

	/**
	 * Instance of {@link ProductDAOImpl}
	 */
	@Autowired
	private ProductDAO productDAO;

	/**
	 * Instance of {@link MailServiceImpl}
	 */
	@Autowired
	private MailService mailService;

	/**
	 * Place New Order
	 * 
	 * @param customerOrderDTO
	 * @return
	 */
	@Override
	@Transactional
	public Response orderProduct(CustomerOrderDTO customerOrderDTO) {

		LOGGER.info("Order Placed For Customer ID : " + customerOrderDTO.getCustomerId());
		Response response = new Response();

		List<ErrorResponse> errorResponseList = productListValidation(customerOrderDTO);

		if (customerOrderDTO.getCustomerId() == null) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName("customerId");
			errorResponse.setErrorMessage("Customer ID should not be Null");
			errorResponseList.add(errorResponse);
		} else {

			Customer customer = customerDAO.getCustomerById(customerOrderDTO.getCustomerId());

			if (customer == null) {
				ErrorResponse errorResponse = new ErrorResponse();
				errorResponse.setFieldName("customerId");
				errorResponse.setErrorMessage("Customer ID " + customerOrderDTO.getCustomerId() + " Not Found");
				errorResponseList.add(errorResponse);
			}
		}

		if (errorResponseList.isEmpty()) {

			Customer customer = customerDAO.getCustomerById(customerOrderDTO.getCustomerId());

			LocalDateTime now = LocalDateTime.now();
			LocalDateTime futureDateTime = now.plus(24, ChronoUnit.HOURS);

			OrderDetails order = new OrderDetails();
			order.setCustomerId(customer);
			order.setOrderExpectedDate(Date.from(futureDateTime.atZone(ZoneId.systemDefault()).toInstant()));
			order.setOrderStatus(WebServiceUtil.NEW);
			order.setOrderedDate(new Date());
			order.setOrderCreatedDate(new Date());
			order.setOrderUpdatedDate(new Date());

			orderDAO.createOrder(order);

			int totalPrice = 0;
			StringBuilder bodyStringBuilder = new StringBuilder(
					"Dear Customer, \n\n Your Order Has Been Placed Successfully. \nOrder ID : " + order.getOrderId()
							+ "\nExpected Date : " + order.getOrderExpectedDate() + "\n\nProducts:\n\n");

			for (OrderLineItemDetailsDTO itemDTO : customerOrderDTO.getOrderList()) {

				Product product = productDAO.getProductById(itemDTO.getProductId());

				OrderLineItemDetails item = new OrderLineItemDetails();
				item.setOrderId(order);
				item.setProductId(product);
				item.setQuantityIndividualUnit(itemDTO.getQuantityIndividualUnit());
				item.setQuantityInPackage(itemDTO.getQuantityInPackage());
				item.setOlidStatus(WebServiceUtil.NEW);
				item.setCreatedDate(new Date());
				item.setUpdateDate(new Date());

				totalPrice += item.getQuantityInPackage() * product.getProductPrice();

				bodyStringBuilder.append("\nName : " + product.getProductName() + "\nQuantity : "
						+ item.getQuantityIndividualUnit() + "\nProduct Price : " + product.getProductPrice()
						+ "\nNet Price : " + (item.getQuantityInPackage() * product.getProductPrice()) + "\n\n");

				orderDAO.orderProduct(item);
			}

			bodyStringBuilder.append("Total Price : " + totalPrice + "\nMode of Payment : Cash on Delivery\n");
			bodyStringBuilder.append("\n\nDelivery Address :\n\n" + customer.getCustomerName() + "\n"
					+ customer.getAddress() + "\n" + customer.getLocation() + "\n" + customer.getCity() + " - "
					+ customer.getPincode() + "\nMobile : " + customer.getMobileNo());
			String to = customer.getMail();
			String subject = "Order Placed";
			String body = bodyStringBuilder.toString();

			try {
				mailService.sendEmail(to, subject, body);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			response.setStatus(WebServiceUtil.SUCCESS);
			response.setData("Order Has Been Placed, Order ID : " + order.getOrderId());
		} else {
			response.setStatus(WebServiceUtil.FAILURE);
			response.setData(errorResponseList);
		}
		return response;
	}

	private List<ErrorResponse> productListValidation(CustomerOrderDTO customerOrderDTO) {
		List<ErrorResponse> errorResponseList = new ArrayList<ErrorResponse>();

		for (OrderLineItemDetailsDTO orderLineItemDetailsDTO : customerOrderDTO.getOrderList()) {

			if (orderLineItemDetailsDTO.getQuantityIndividualUnit() <= 0
					|| orderLineItemDetailsDTO.getQuantityIndividualUnit() == null) {
				ErrorResponse errorResponse = new ErrorResponse();
				errorResponse.setFieldName("quantityIndividualUnit");
				errorResponse.setErrorMessage(
						"Quantity should be greater than 0 for Product ID : " + orderLineItemDetailsDTO.getProductId());
				errorResponseList.add(errorResponse);
			}

			if (orderLineItemDetailsDTO.getProductId() == null) {
				ErrorResponse errorResponse = new ErrorResponse();
				errorResponse.setFieldName("productId");
				errorResponse.setErrorMessage("Product ID should not be Null");
				errorResponseList.add(errorResponse);
			} else {
				Product product = productDAO.getProductById(orderLineItemDetailsDTO.getProductId());

				if (product == null) {
					ErrorResponse errorResponse = new ErrorResponse();
					errorResponse.setFieldName("Product");
					errorResponse
							.setErrorMessage("Product ID " + orderLineItemDetailsDTO.getProductId() + " Not Found");
					errorResponseList.add(errorResponse);
				} else if (product.getLastEffectiveDate() != null || product.getEffectiveDate().after(new Date())) {
					ErrorResponse errorResponse = new ErrorResponse();
					errorResponse.setFieldName("Product");
					errorResponse.setErrorMessage("Product ID " + product.getProductId() + " Not Effective For Sale");
					errorResponseList.add(errorResponse);
				} else {
					int productPackCount = product.getPackQuantity();
					int orderPackCount = getRoundedPackageCount(orderLineItemDetailsDTO.getQuantityIndividualUnit(),
							productPackCount);
					orderLineItemDetailsDTO.setQuantityIndividualUnit(orderPackCount * productPackCount);
					orderLineItemDetailsDTO.setQuantityInPackage(orderPackCount);

//					check stock for each product
					if (!isProductStockAvailable(orderLineItemDetailsDTO, customerOrderDTO.getOrderId(), product)) {
						ErrorResponse errorResponse = new ErrorResponse();
						errorResponse.setFieldName("Product");
						errorResponse.setErrorMessage("Stock Not Available For Product ID : " + product.getProductId());
						errorResponseList.add(errorResponse);
					}
				}
			}
		}
		return errorResponseList;
	}

	private boolean isProductStockAvailable(OrderLineItemDetailsDTO itemDetailsDTO, Integer orderId, Product product) {

		Long reserevedPackage = orderDAO.getProductReservedPackageCount(itemDetailsDTO.getProductId(), orderId);

		if (reserevedPackage == null) {
			reserevedPackage = 0L;
		}

		if ((product.getCurrentStockPackageCount() - reserevedPackage) < itemDetailsDTO.getQuantityInPackage()) { // reserved
																													// count
			return false;
		}
		return true;
	}

	private int getRoundedPackageCount(int orderQuantity, int packageCount) {
		int basePackages = orderQuantity / packageCount;
		int remainder = orderQuantity % packageCount;

		if (remainder > 0) {
			basePackages += 1;
		}

		return basePackages;
	}

	/**
	 * Update Order Which Has Status New
	 * 
	 * @param customerOrderDTO
	 * @return
	 */
	@Override
	@Transactional
	public Response updateOrder(CustomerOrderDTO customerOrderDTO) {
		Response response = new Response();
		List<ErrorResponse> errorResponseList = new ArrayList<ErrorResponse>();

		for (OrderLineItemDetailsDTO itemDetailsDTO : customerOrderDTO.getOrderList()) {

			if (customerOrderDTO.getOrderId() != null && itemDetailsDTO.getProductId() != null) {

				OrderDetails order = orderDAO.getOrderById(customerOrderDTO.getOrderId());
				Product product = productDAO.getProductById(itemDetailsDTO.getProductId());
				OrderLineItemDetails existingItem = orderDAO.getOrderItemByOlidId(customerOrderDTO.getOrderId(),
						itemDetailsDTO.getProductId());

				if (order == null) {
					ErrorResponse errorResponse = new ErrorResponse();
					errorResponse.setFieldName("orderId");
					errorResponse.setErrorMessage("Order ID " + customerOrderDTO.getOrderId() + " Not Found");
					errorResponseList.add(errorResponse);
				} else if (!order.getOrderStatus().equalsIgnoreCase(WebServiceUtil.NEW)) {
					ErrorResponse errorResponse = new ErrorResponse();
					errorResponse.setFieldName("orderStatus");
					errorResponse.setErrorMessage("Updated failed, Order is already " + order.getOrderStatus());
					errorResponseList.add(errorResponse);
				} else if (existingItem == null) {
					ErrorResponse errorResponse = new ErrorResponse();
					errorResponse.setFieldName("productId");
					errorResponse
							.setErrorMessage("Product ID " + itemDetailsDTO.getProductId() + " is not in your order");
					errorResponseList.add(errorResponse);
				}

				if (order != null && existingItem != null
						&& order.getOrderStatus().equalsIgnoreCase(WebServiceUtil.NEW)) {

					if (itemDetailsDTO.getQuantityIndividualUnit() == null) {
						ErrorResponse errorResponse = new ErrorResponse();
						errorResponse.setFieldName("quantityIndividualUnit");
						errorResponse.setErrorMessage("Quantity Individual Unit Should not be Null");
						errorResponseList.add(errorResponse);
					} else {
						int productPackCount = product.getPackQuantity();
						int orderPackCount = getRoundedPackageCount(itemDetailsDTO.getQuantityIndividualUnit(),
								productPackCount);

						itemDetailsDTO.setQuantityIndividualUnit(orderPackCount * productPackCount);
						itemDetailsDTO.setQuantityInPackage(orderPackCount);

						if (!isProductStockAvailable(itemDetailsDTO, customerOrderDTO.getOrderId(), product)) {
							ErrorResponse errorResponse = new ErrorResponse();
							errorResponse.setFieldName("Product");
							errorResponse
									.setErrorMessage("Stock Not Available For Product ID : " + product.getProductId());
							errorResponseList.add(errorResponse);
						}
					}
				}
			} else if (customerOrderDTO.getOrderId() == null) {
				ErrorResponse errorResponse = new ErrorResponse();
				errorResponse.setFieldName("orderId");
				errorResponse.setErrorMessage("Order ID Should not be Null");
				errorResponseList.add(errorResponse);
			} else {
				ErrorResponse errorResponse = new ErrorResponse();
				errorResponse.setFieldName("productId");
				errorResponse.setErrorMessage("Product ID Should not be Null");
				errorResponseList.add(errorResponse);
			}
		}

		OrderDetails order = new OrderDetails();

		if (errorResponseList.isEmpty()) {
			int productCount = 0;
			int cancelledProduct = 0;

			for (OrderLineItemDetailsDTO itemDTO : customerOrderDTO.getOrderList()) {

				order = orderDAO.getOrderById(customerOrderDTO.getOrderId());
				OrderLineItemDetails existingItem = orderDAO.getOrderItemByOlidId(customerOrderDTO.getOrderId(),
						itemDTO.getProductId());

				order.setOrderUpdatedDate(new Date());
				existingItem.setQuantityIndividualUnit(itemDTO.getQuantityIndividualUnit());
				existingItem.setQuantityInPackage(itemDTO.getQuantityInPackage());
				existingItem.setUpdateDate(new Date());
				productCount++;

				if (itemDTO.getQuantityInPackage() == 0) {
					existingItem.setOlidStatus(WebServiceUtil.CANCELLED);
					cancelledProduct++;
				}

			}

			if (cancelledProduct == productCount) {
				orderDAO.updateOrderStatus(order.getOrderId(), WebServiceUtil.CANCELLED);// check whether to cancel
			}

			response.setStatus(WebServiceUtil.SUCCESS);
			response.setData("Order has been updated");
		} else {
			response.setStatus(WebServiceUtil.FAILURE);
			response.setData(errorResponseList);
		}

		LOGGER.info("Update Order For Order ID : " + order.getOrderId());

		return response;
	}

	/**
	 * Update Order Status
	 * 
	 * @param orderId
	 * @param newStatus
	 * @return
	 */
	@Override
	@Transactional
	public Response updateStatus(Integer orderId, String newStatus) {

		LOGGER.info("Update Status For Order ID : " + orderId);

		Response response = new Response();
		
		if(orderId != null) {
			
			OrderDetails order = orderDAO.getOrderById(orderId);

			if (order != null) {
				if (isValidTransition(order.getOrderStatus(), newStatus)) {
					List<OrderLineItemDetails> orderItemList = orderDAO.getOrderItemListByOrderId(orderId);

					if (newStatus.equalsIgnoreCase(WebServiceUtil.PACKED)) {
						int result = checkStock(orderItemList);

						if (result == 0) {
							for (OrderLineItemDetails item : orderItemList) {
								Product product = item.getProductId();
								product.setCurrentStockPackageCount(
										product.getCurrentStockPackageCount() - item.getQuantityInPackage());
								product.setUpdatedDate(new Date());
							}
						} else {
							response.setStatus(WebServiceUtil.FAILURE);
							response.setData("Update Failed, Stock not Available for Product ID : " + result);
							return response;
						}
					}

					orderDAO.updateOrderStatus(orderId, newStatus);
					orderDAO.updateOrderItemStatus(orderId, newStatus);

					int totalPrice = 0;
					StringBuilder bodyStringBuilder = new StringBuilder(
							"Dear Customer, \n\n Your Order Has Been " + newStatus);
					Customer customer = new Customer();

					for (OrderLineItemDetails itemDetailsDTO : orderItemList) {

						totalPrice += itemDetailsDTO.getQuantityInPackage()
								* itemDetailsDTO.getProductId().getProductPrice();

						bodyStringBuilder.append("\n\nName : " + itemDetailsDTO.getProductId().getProductName()
								+ "\nQuantity : " + itemDetailsDTO.getQuantityIndividualUnit() + "\nProduct Price : "
								+ itemDetailsDTO.getProductId().getProductPrice() + "\nNet Price : "
								+ (itemDetailsDTO.getQuantityInPackage() * itemDetailsDTO.getProductId().getProductPrice())
								+ "\n\n");

						customer = itemDetailsDTO.getOrderId().getCustomerId();
					}

					if (newStatus != "cancelled") {

						bodyStringBuilder.append("Total Price : " + totalPrice + "\nMode of Payment : Cash on Delivery\n");
						bodyStringBuilder.append("\n\nDelivery Address :\n\n" + customer.getCustomerName() + "\n"
								+ customer.getAddress() + "\n" + customer.getLocation() + "\n" + customer.getCity() + " - "
								+ customer.getPincode() + "\nMobile : " + customer.getMobileNo());
					}

					String to = order.getCustomerId().getMail();
					String subject = "Order " + newStatus;
					String body = bodyStringBuilder.toString();

					try {
						mailService.sendEmail(to, subject, body);
					} catch (MessagingException e) {
						e.printStackTrace();
					}

					response.setStatus(WebServiceUtil.SUCCESS);
					response.setData("Status Updated Successfully");

				} else {
					response.setStatus(WebServiceUtil.FAILURE);
					response.setData("Order cannot be Moved to " + newStatus + " Status, it is in " + order.getOrderStatus() + " Status");
				}

			} else {
				response.setStatus(WebServiceUtil.FAILURE);
				response.setData("Order ID " + orderId + " Not Found");
			}
		} else {
			response.setData(WebServiceUtil.FAILURE);
			response.setData("productId Should not be Null");
		}

		return response;
	}

	private boolean isValidTransition(String oldStatus, String newStatus) {

		if (oldStatus.equalsIgnoreCase(WebServiceUtil.NEW) && (newStatus.equalsIgnoreCase(WebServiceUtil.PACKED)
				|| newStatus.equalsIgnoreCase(WebServiceUtil.CANCELLED))) {
			return true;
		} else if (oldStatus.equalsIgnoreCase(WebServiceUtil.PACKED)
				&& newStatus.equalsIgnoreCase(WebServiceUtil.SHIPPED)) {
			return true;
		} else if (oldStatus.equalsIgnoreCase(WebServiceUtil.SHIPPED)
				&& newStatus.equalsIgnoreCase(WebServiceUtil.DELIVERED)) {
			return true;
		} else {
			return false;
		}
	}

	private int checkStock(List<OrderLineItemDetails> orderItemList) {
		for (OrderLineItemDetails item : orderItemList) {
			Product product = item.getProductId();
			if (product.getCurrentStockPackageCount() < item.getQuantityInPackage()) {
				return product.getProductId();
			}
		}
		return 0;
	}

	/**
	 * Retrieves OrderItemList Using OrderId
	 * 
	 * @param orderId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public Response getOrderItemListByOrderId(Integer orderId) {

		LOGGER.info("View Order Details List for Order ID : " + orderId);
		
		Response response = new Response();
		
		if(orderId != null) {
			CustomerOrderDTO customerOrderDTO = new CustomerOrderDTO();
			Map<String, Object> resultMap = orderDAO.getOrderItemListDTOByOrderId(orderId);

			if (resultMap.get("customerId") == null) {
				response.setStatus(WebServiceUtil.SUCCESS);
				response.setData("No Data Found for Order ID : " + orderId);
			} else {
				customerOrderDTO.setCustomerId((Integer) resultMap.get("customerId"));
				customerOrderDTO.setOrderId((Integer) resultMap.get("orderId"));
				customerOrderDTO.setOrderList((List<OrderLineItemDetailsDTO>) resultMap.get("data"));
				response.setStatus(WebServiceUtil.SUCCESS);
				response.setData(customerOrderDTO);
			}
		} else {
			response.setStatus(WebServiceUtil.FAILURE);
			response.setData("orderId Should not be Null");
		}
		
		
		return response;
	}

	/**
	 * Retrieves All Order Details
	 *
	 *@param orderFilterList
	 * @return
	 */
	@Override
	@Transactional
	public FilteredResponse listOrder(OrderFilterList orderFilterList) {

		LOGGER.info("List All Order Details List");

		List<ErrorResponse> errorResponseList = orderFilterListValidation(orderFilterList);
		FilteredResponse filteredResponse = new FilteredResponse();

		if (errorResponseList.isEmpty()) {

			Map<String, Object> resultMap = orderDAO.listOrder(orderFilterList);

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

	private List<ErrorResponse> orderFilterListValidation(OrderFilterList orderFilterList) {

		List<ErrorResponse> errorResponseList = new ArrayList<ErrorResponse>();

		if (orderFilterList.getLength() == null || orderFilterList.getLength() < 1) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName("length");
			errorResponse.setErrorMessage("Length Should be greater than 0 and Should not be Null");
			errorResponseList.add(errorResponse);
		}

		if (orderFilterList.getStart() == null) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName("start");
			errorResponse.setErrorMessage("Start Should not be Null");
			errorResponseList.add(errorResponse);
		}

		if ( !(orderFilterList.getFilter().getStatus() == null || orderFilterList.getFilter().getStatus().isBlank()
				|| orderFilterList.getFilter().getStatus().equalsIgnoreCase(WebServiceUtil.NEW)
				|| orderFilterList.getFilter().getStatus().equalsIgnoreCase(WebServiceUtil.PACKED)
				|| orderFilterList.getFilter().getStatus().equalsIgnoreCase(WebServiceUtil.SHIPPED)
				|| orderFilterList.getFilter().getStatus().equalsIgnoreCase(WebServiceUtil.DELIVERED)
				|| orderFilterList.getFilter().getStatus().equalsIgnoreCase(WebServiceUtil.CANCELLED)) ) {
			
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName("start");
			errorResponse.setErrorMessage("Start Should not be Null");
			errorResponseList.add(errorResponse);
		}

		return errorResponseList;
	}
}
