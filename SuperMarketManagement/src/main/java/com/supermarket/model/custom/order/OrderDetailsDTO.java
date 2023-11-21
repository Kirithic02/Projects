package com.supermarket.model.custom.order;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.supermarket.util.serializer.TimestampSerializer;

public class OrderDetailsDTO {

	private Integer orderId;

	@JsonSerialize(using = TimestampSerializer.class)
	private Date orderedDate;
	
	private Integer customerId;
	
	private String customerName;

	@JsonSerialize(using = TimestampSerializer.class)
	private Date orderExpectedDate;

	private String orderStatus;

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Date getOrderedDate() {
		return orderedDate;
	}

	public void setOrderedDate(Date orderedDate) {
		this.orderedDate = orderedDate;
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

	public Date getOrderExpectedDate() {
		return orderExpectedDate;
	}

	public void setOrderExpectedDate(Date orderExpectedDate) {
		this.orderExpectedDate = orderExpectedDate;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

}
