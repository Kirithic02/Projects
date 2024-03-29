package com.supermarket.serviceimpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.supermarket.dao.CustomerDAO;
import com.supermarket.dao.OrderDAO;
import com.supermarket.dao.ProductDAO;
import com.supermarket.model.custom.ErrorResponse;
import com.supermarket.model.custom.FilteredResponse;
import com.supermarket.model.custom.Response;
import com.supermarket.model.custom.order.CustomerOrderDTO;
import com.supermarket.model.custom.order.OrderDetailsDTO;
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

	@Scheduled(cron = "00 56 17 * * ?")
	public void lowStockAlert() {

		System.out.println("S");
	}

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
//			ErrorResponse errorResponse = new ErrorResponse();
//			errorResponse.setFieldName(WebServiceUtil.CUSTOMER_ID);
//			errorResponse.setErrorMessage("Customer ID should not be null");
//			errorResponseList.add(errorResponse);
			throw new NullPointerException("Customer ID is null");

		} else {

			Customer customer = customerDAO.getCustomerById(customerOrderDTO.getCustomerId());

			if (customer == null) {
				ErrorResponse errorResponse = new ErrorResponse();
				errorResponse.setFieldName(WebServiceUtil.CUSTOMER_ID);
				errorResponse.setErrorMessage("Customer Not Found");
				errorResponseList.add(errorResponse);
			}
		}

		if (errorResponseList.isEmpty()) {

			Customer customer = customerDAO.getCustomerById(customerOrderDTO.getCustomerId());

			LocalDateTime now = LocalDateTime.now();
			LocalDateTime futureDateTime = now.plus(4, ChronoUnit.DAYS);

			OrderDetails order = new OrderDetails();
			order.setCustomerId(customer);
			order.setOrderExpectedDate(Date.from(futureDateTime.atZone(ZoneId.systemDefault()).toInstant())); // 4 days
			order.setOrderStatus(WebServiceUtil.NEW);
			order.setOrderedDate(new Date());
			order.setOrderCreatedDate(new Date());
			order.setOrderUpdatedDate(new Date());

			orderDAO.createOrder(order);

			int totalPrice = 0;
			StringBuilder bodyStringBuilder = new StringBuilder(
					"Dear Customer, \n\n              Thank you for choosing FreshMart! Your order has been successfully placed, "
							+ "and we're busy \npreparing your items. Here are the details: \n\nOrder ID : "
							+ order.getOrderId() + "\nOrder Date : " + order.getOrderedDate() + "\nExpected Date : "
							+ order.getOrderExpectedDate() + "\n\n\nProducts Details:\n");

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

				bodyStringBuilder.append("\nProduct Name : " + product.getProductName() + "\nQuantity : "
						+ item.getQuantityIndividualUnit() + "\nProduct Price : " + product.getProductPrice()
						+ "\nNet Price : " + (item.getQuantityInPackage() * product.getProductPrice()) + "\n");

				orderDAO.orderProduct(item);
			}

			bodyStringBuilder.append("\n\n\nShipping Details:"
					+ "\n\nYour order will be carefully packed and shipped as soon as possible. We will send you another \nemail once your order is on its way.");
			bodyStringBuilder.append("\n\n\nMode of Payment : Cash on Delivery\n" + "Total Price : " + totalPrice
					+ "\n\n\nDelivery Address :\n\n" + customer.getCustomerName() + "\n" + customer.getAddress() + "\n"
					+ customer.getLocation() + "\n" + customer.getCity() + " - " + customer.getPincode() + "\nMobile : "
					+ customer.getMobileNo() + "\n\n\nIf you have any questions about your order, "
					+ "feel free to contact our customer support team at \nkirithic@humworld.in (or) 9894507215."
					+ "\n\nWe appreciate your business and hope you enjoy your FreshMart products!"
					+ "\n\nBest regards,\n" + "The FreshMart Team");
			String to = customer.getMail();
			String subject = "Thank You for Your FreshMart Order!";
			String body = bodyStringBuilder.toString();

			try {
				mailService.sendEmail(to, subject, body);
			} catch (MessagingException e) {
				e.printStackTrace();
			}

			response.setStatus(WebServiceUtil.SUCCESS);
			response.setData("Your Order Has Been Placed");

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
				errorResponse.setFieldName(WebServiceUtil.ORDERLINEITEMDETAILS_QUANTITYINDIVIDUALUNIT);
				errorResponse.setErrorMessage(
						"Quantity should be greater than 0 for Product ID : " + orderLineItemDetailsDTO.getProductId());
				errorResponseList.add(errorResponse);
			}

			if (orderLineItemDetailsDTO.getProductId() == null) {

				ErrorResponse errorResponse = new ErrorResponse();
				errorResponse.setFieldName(WebServiceUtil.PRODUCT_ID);
				errorResponse.setErrorMessage("Product ID should not be null");
				errorResponseList.add(errorResponse);

			} else {
				Product product = productDAO.getProductById(orderLineItemDetailsDTO.getProductId());

				if (product == null) {

					ErrorResponse errorResponse = new ErrorResponse();
					errorResponse.setFieldName(WebServiceUtil.PRODUCT_ID);
					errorResponse.setErrorMessage("Product Not Found");
					errorResponseList.add(errorResponse);

				} else if (product.getLastEffectiveDate() != null || product.getEffectiveDate().after(new Date())) {
					ErrorResponse errorResponse = new ErrorResponse();
					errorResponse.setFieldName(WebServiceUtil.PRODUCT_ID);
					errorResponse.setErrorMessage(product.getProductName() + " is Not Effective For Sale");
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
						errorResponse.setFieldName(WebServiceUtil.PRODUCT_ID);
						errorResponse.setErrorMessage("Stock Not Available For " + product.getProductName());
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
					errorResponse.setFieldName(WebServiceUtil.ORDERDETAILS_ID);
					errorResponse.setErrorMessage("Order Not Found");
					errorResponseList.add(errorResponse);

				} else if (!order.getOrderStatus().equalsIgnoreCase(WebServiceUtil.NEW)) {
					ErrorResponse errorResponse = new ErrorResponse();
					errorResponse.setFieldName(WebServiceUtil.ORDERDETAILS_STATUS);
					errorResponse.setErrorMessage("Updated failed, Order is already " + order.getOrderStatus());
					errorResponseList.add(errorResponse);

				} else if (existingItem == null) {
					ErrorResponse errorResponse = new ErrorResponse();
					errorResponse.setFieldName(WebServiceUtil.PRODUCT_ID);
					errorResponse
							.setErrorMessage("Product ID " + itemDetailsDTO.getProductId() + " is not in your order");
					errorResponseList.add(errorResponse);
				}

				if (order != null && existingItem != null
						&& order.getOrderStatus().equalsIgnoreCase(WebServiceUtil.NEW)) {

					if (itemDetailsDTO.getQuantityIndividualUnit() == null) {
						ErrorResponse errorResponse = new ErrorResponse();
						errorResponse.setFieldName(WebServiceUtil.ORDERLINEITEMDETAILS_QUANTITYINDIVIDUALUNIT);
						errorResponse.setErrorMessage("Quantity Individual Unit Should not be null");
						errorResponseList.add(errorResponse);

					} else {
						int productPackCount = product.getPackQuantity();
						int orderPackCount = getRoundedPackageCount(itemDetailsDTO.getQuantityIndividualUnit(),
								productPackCount);

						itemDetailsDTO.setQuantityIndividualUnit(orderPackCount * productPackCount);
						itemDetailsDTO.setQuantityInPackage(orderPackCount);

						if (!isProductStockAvailable(itemDetailsDTO, customerOrderDTO.getOrderId(), product)) {
							ErrorResponse errorResponse = new ErrorResponse();
							errorResponse.setFieldName(WebServiceUtil.ORDERLINEITEMDETAILS_QUANTITYINDIVIDUALUNIT);
							errorResponse.setErrorMessage("Stock Not Available For " + product.getProductName());
							errorResponseList.add(errorResponse);
						}
					}
				}

			} else if (customerOrderDTO.getOrderId() == null) {
//				ErrorResponse errorResponse = new ErrorResponse();
//				errorResponse.setFieldName(WebServiceUtil.ORDERDETAILS_ID);
//				errorResponse.setErrorMessage("Order ID Should not be null");
//				errorResponseList.add(errorResponse);
				throw new NullPointerException("Order ID is Null");

			} else {
//				ErrorResponse errorResponse = new ErrorResponse();
//				errorResponse.setFieldName(WebServiceUtil.PRODUCT_ID);
//				errorResponse.setErrorMessage("Product ID Should not be null");
//				errorResponseList.add(errorResponse);
				throw new NullPointerException("Product ID is Null");
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

		if (orderId != null) {

			OrderDetails order = orderDAO.getOrderById(orderId);

			if (order != null) {

				if (newStatus.equalsIgnoreCase(WebServiceUtil.NEW) || newStatus.equalsIgnoreCase(WebServiceUtil.PACKED)
						|| newStatus.equalsIgnoreCase(WebServiceUtil.SHIPPED)
						|| newStatus.equalsIgnoreCase(WebServiceUtil.DELIVERED)
						|| newStatus.equalsIgnoreCase(WebServiceUtil.CANCELLED)) {

					if (isValidTransition(order.getOrderStatus(), newStatus)) {

						List<OrderLineItemDetails> orderItemList = orderDAO.getOrderItemListByOrderId(orderId);

						if (newStatus.equalsIgnoreCase(WebServiceUtil.PACKED)) {

							Product resultProduct = checkStock(orderItemList);

							if (resultProduct == null) {

								for (OrderLineItemDetails item : orderItemList) {
									Product product = item.getProductId();
									product.setCurrentStockPackageCount(
											product.getCurrentStockPackageCount() - item.getQuantityInPackage());
									product.setUpdatedDate(new Date());
								}

							} else {
								response.setStatus(WebServiceUtil.FAILURE);
								response.setData(
										"Update Failed, Stock not Available for " + resultProduct.getProductName());

								return response;
							}
						}

						orderDAO.updateOrderStatus(orderId, newStatus);
						orderDAO.updateOrderItemStatus(orderId, newStatus);

						int totalPrice = 0;
						StringBuilder bodyStringBuilder = new StringBuilder(
								"Dear Customer, \n\n          Great news! Your order from FreshMart is " + newStatus
										+ ". Here's a quick summary:" + "\n\nOrder Number: " + orderId
										+ "\n\n\nProducts:\n\n");
						Customer customer = new Customer();

						for (OrderLineItemDetails itemDetailsDTO : orderItemList) {

							totalPrice += itemDetailsDTO.getQuantityInPackage()
									* itemDetailsDTO.getProductId().getProductPrice();

							bodyStringBuilder.append("Name : " + itemDetailsDTO.getProductId().getProductName()
									+ "\nQuantity : " + itemDetailsDTO.getQuantityIndividualUnit()
									+ "\nProduct Price : " + itemDetailsDTO.getProductId().getProductPrice()
									+ "\nNet Price : " + (itemDetailsDTO.getQuantityInPackage()
											* itemDetailsDTO.getProductId().getProductPrice())
									+ "\n\n");

							customer = itemDetailsDTO.getOrderId().getCustomerId();
						}

						if (newStatus != "cancelled") {

							bodyStringBuilder
									.append("\nMode of Payment : Cash on Delivery\n" + "Total Price : " + totalPrice);
							bodyStringBuilder.append("\n\n\nDelivery Address :\n\n" + customer.getCustomerName() + "\n"
									+ customer.getAddress() + "\n" + customer.getLocation() + "\n" + customer.getCity()
									+ " - " + customer.getPincode() + "\nMobile : " + customer.getMobileNo()
									+ "\n\n\nIf you have any questions about your order, "
									+ "feel free to contact our customer support team at \nkirithic@humworld.in (or) 9894507215."
									+ "\n\nWe appreciate your business and hope you enjoy your FreshMart products!"
									+ "\n\nBest regards,\n" + "The FreshMart Team");
						}

						String to = order.getCustomerId().getMail();
						String subject = "Your FreshMart Order is " + newStatus;
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
						response.setData("Order cannot be Moved to " + newStatus + " Status, it is in "
								+ order.getOrderStatus() + " Status");
					}
				} else {
					response.setStatus(WebServiceUtil.FAILURE);
					response.setData(
							"Order Status Should Only Contain new (or) shipped (or) packed (or) delivered (or) cancelled");
				}

			} else {
				response.setStatus(WebServiceUtil.FAILURE);
				response.setData("Order Not Found");
			}

		} else {
//			response.setData(WebServiceUtil.FAILURE);
//			response.setData("Order ID is Null");
			throw new NullPointerException("Order ID is Null");
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

	private Product checkStock(List<OrderLineItemDetails> orderItemList) {

		for (OrderLineItemDetails item : orderItemList) {

			Product product = item.getProductId();

			if (product.getCurrentStockPackageCount() < item.getQuantityInPackage()) {

				return product;
			}
		}
		return null;
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

		if (orderId != null) {
			CustomerOrderDTO customerOrderDTO = new CustomerOrderDTO();
			Map<String, Object> resultMap = orderDAO.getOrderItemListDTOByOrderId(orderId);

			if (resultMap.get("customer") == null) {

				response.setStatus(WebServiceUtil.FAILURE);
				response.setData("No Data Found for This Order");

			} else {

				Customer customer = (Customer) resultMap.get("customer");
				customerOrderDTO.setCustomerId(customer.getCustomerId());
				customerOrderDTO.setCustomerName(customer.getCustomerName());
//				customerOrderDTO.setCustomerId((Integer) resultMap.get(WebServiceUtil.CUSTOMER_ID));
//				customerOrderDTO.setCustomerName();
				OrderDetails orderDetails = (OrderDetails) resultMap.get("order");
				customerOrderDTO.setOrderId(orderDetails.getOrderId());
				customerOrderDTO.setOrderStatus(orderDetails.getOrderStatus());
//				customerOrderDTO.setOrderId((Integer) resultMap.get(WebServiceUtil.ORDERDETAILS_ID));
				customerOrderDTO.setOrderList(
						(List<OrderLineItemDetailsDTO>) resultMap.get(WebServiceUtil.FILTEREDRESPONSE_DATA));
				response.setStatus(WebServiceUtil.SUCCESS);
				response.setData(customerOrderDTO);
			}

		} else {

//			response.setStatus(WebServiceUtil.FAILURE);
//			response.setData("orderId Should not be null");
			throw new NullPointerException("Order ID is Null");
		}

		return response;
	}

	/**
	 * Retrieves All Order Details
	 *
	 * @param orderFilterList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public FilteredResponse listOrder(OrderFilterList orderFilterList) {

		LOGGER.info("List All Order Details List");

		List<ErrorResponse> errorResponseList = orderFilterListValidation(orderFilterList);
		FilteredResponse filteredResponse = new FilteredResponse();

//		add searchcolumn(productname or id, customername or id)

		if (errorResponseList.isEmpty()) {

			Map<String, Object> resultMap = orderDAO.listOrder(orderFilterList);

			List<OrderDetailsDTO> transactionDetails = (List<OrderDetailsDTO>) resultMap.get("data");

			if (orderFilterList.getOrderBy().getColumn() != null && orderFilterList.getOrderBy().getColumn().equalsIgnoreCase("serialNumber")
					&& orderFilterList.getOrderBy().getType().equalsIgnoreCase("desc")) {
				Collections.reverse(transactionDetails);
				for (Integer i = transactionDetails.size() - 1; i >= 0; i--) {
					transactionDetails.get(i)
							.setSerialNumber(orderFilterList.getStart() + transactionDetails.size() - i);
				}
			} else {
				for (Integer i = 0; i < transactionDetails.size(); i++) {
					transactionDetails.get(i).setSerialNumber(orderFilterList.getStart() + i + 1);
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

	private List<ErrorResponse> orderFilterListValidation(OrderFilterList orderFilterList) {

		List<ErrorResponse> errorResponseList = new ArrayList<ErrorResponse>();

		if (orderFilterList.getLength() == null || orderFilterList.getLength() < 1) {

			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.FILTERLIST_LENGTH);
			errorResponse.setErrorMessage("Length Should be greater than 0");
			errorResponseList.add(errorResponse);
		}

		if (orderFilterList.getStart() == null) {

			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.FILTERLIST_START);
			errorResponse.setErrorMessage("Start Should not be null");
			errorResponseList.add(errorResponse);
		}

		if (!(orderFilterList.getFilter().getOrderStatus() == null
				|| orderFilterList.getFilter().getOrderStatus().trim().isEmpty()
				|| orderFilterList.getFilter().getOrderStatus().equalsIgnoreCase(WebServiceUtil.NEW)
				|| orderFilterList.getFilter().getOrderStatus().equalsIgnoreCase(WebServiceUtil.PACKED)
				|| orderFilterList.getFilter().getOrderStatus().equalsIgnoreCase(WebServiceUtil.SHIPPED)
				|| orderFilterList.getFilter().getOrderStatus().equalsIgnoreCase(WebServiceUtil.DELIVERED)
				|| orderFilterList.getFilter().getOrderStatus().equalsIgnoreCase(WebServiceUtil.CANCELLED))) {

			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.ORDERDETAILS_STATUS);
			errorResponse.setErrorMessage("OrderStatus Should Contain only new (or) packed (or) shipped (or) delivered (or) cancelled");
			errorResponseList.add(errorResponse);
		}

		if (!(orderFilterList.getSearchColumn() == null || orderFilterList.getSearchColumn().trim().isEmpty()
				|| orderFilterList.getSearchColumn().equalsIgnoreCase(WebServiceUtil.CUSTOMER_ID)
				|| orderFilterList.getSearchColumn().equalsIgnoreCase(WebServiceUtil.CUSTOMER_NAME)
				|| orderFilterList.getSearchColumn().equalsIgnoreCase(WebServiceUtil.PRODUCT_ID)
				|| orderFilterList.getSearchColumn().equalsIgnoreCase(WebServiceUtil.PRODUCT_NAME)
				|| orderFilterList.getSearchColumn().equalsIgnoreCase(WebServiceUtil.ORDERDETAILS_ID))) {

			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.FILTERLIST_SEARCHCOLUMN);
			errorResponse.setErrorMessage(
					"searchColumn Should Contain only customerid (or) customername (or) productid (or) productname (or) orderdid");
			errorResponseList.add(errorResponse);

		}

//		if (orderFilterList.getOrderBy() != null && ValidationUtil.isNotEmpty(orderFilterList.getOrderBy().getType())
//				&& ValidationUtil.isNotEmpty(orderFilterList.getOrderBy().getColumn())) {
//
//			if (!(orderFilterList.getOrderBy().getColumn().equalsIgnoreCase(WebServiceUtil.ORDERDETAILS_ORDEREDDATE)
//					|| orderFilterList.getOrderBy().getColumn()
//							.equalsIgnoreCase(WebServiceUtil.ORDERDETAILS_ORDEREXPECTEDDATE)
//					|| orderFilterList.getOrderBy().getColumn().equalsIgnoreCase(WebServiceUtil.ORDERDETAILS_STATUS))) {
//				
//				ErrorResponse errorResponse = new ErrorResponse();
//				errorResponse.setFieldName(WebServiceUtil.FILTERLIST_ORDERBY_COLUMN);
//				errorResponse.setErrorMessage(
//						"column Should Contain Only orderdate (or) expecteddate (or) orderstatus (or) null");
//				errorResponseList.add(errorResponse);
//			}
//
//			if (!(orderFilterList.getOrderBy().getType().equalsIgnoreCase(WebServiceUtil.FILTERLIST_ORDERBY_TYPE_ASC)
//					|| orderFilterList.getOrderBy().getType()
//							.equalsIgnoreCase(WebServiceUtil.FILTERLIST_ORDERBY_TYPE_DESC))) {
//				
//				ErrorResponse errorResponse = new ErrorResponse();
//				errorResponse.setFieldName(WebServiceUtil.FILTERLIST_ORDERBY_TYPE);
//				errorResponse.setErrorMessage("type Should Contain Only asc (or) desc (or) null");
//				errorResponseList.add(errorResponse);
//			}
//		}

		return errorResponseList;
	}
}
