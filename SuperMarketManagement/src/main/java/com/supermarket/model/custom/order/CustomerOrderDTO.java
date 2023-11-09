package com.supermarket.model.custom.order;

import java.util.List;

public class CustomerOrderDTO {
	
	private Integer orderId;
	
	private Integer customerId;
	
	private List<OrderLineItemDetailsDTO> orderList;

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public List<OrderLineItemDetailsDTO> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<OrderLineItemDetailsDTO> orderList) {
		this.orderList = orderList;
	}
	
}
