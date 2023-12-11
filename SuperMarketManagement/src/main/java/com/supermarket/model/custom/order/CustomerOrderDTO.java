package com.supermarket.model.custom.order;

import java.util.List;

public class CustomerOrderDTO {

	private Integer orderId;

	private Integer customerId;

	private String customerName;

	private String orderStatus;

	private List<OrderLineItemDetailsDTO> orderList;

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public List<OrderLineItemDetailsDTO> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<OrderLineItemDetailsDTO> orderList) {
		this.orderList = orderList;
	}

}
