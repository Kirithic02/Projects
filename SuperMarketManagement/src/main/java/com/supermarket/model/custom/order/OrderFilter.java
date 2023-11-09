package com.supermarket.model.custom.order;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.supermarket.util.deserializer.DateDeserializer;

public class OrderFilter {
	
	private String status;
	
	@JsonDeserialize(using = DateDeserializer.class)
	private Date fromDate;
	
	@JsonDeserialize(using = DateDeserializer.class)
	private Date toDate;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
