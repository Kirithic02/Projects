package com.supermarket.model.custom.order;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.supermarket.util.deserializer.DateDeserializer;

public class OrderFilter {

	private String orderStatus;

	@JsonDeserialize(using = DateDeserializer.class)
	private Date fromDate;

	@JsonDeserialize(using = DateDeserializer.class)
	private Date toDate;

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

}
