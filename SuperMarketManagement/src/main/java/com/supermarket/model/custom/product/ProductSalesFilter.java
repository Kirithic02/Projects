package com.supermarket.model.custom.product;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.supermarket.util.deserializer.DateDeserializer;

public class ProductSalesFilter {

//	private String status;
	
	private String productCategory;
	
	@JsonDeserialize(using = DateDeserializer.class)
	private Date fromDate;
	
	@JsonDeserialize(using = DateDeserializer.class)
	private Date toDate;

//	public String getStatus() {
//		return status;
//	}
//
//	public void setStatus(String status) {
//		this.status = status;
//	}

	public Date getFromDate() {
		return fromDate;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
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
