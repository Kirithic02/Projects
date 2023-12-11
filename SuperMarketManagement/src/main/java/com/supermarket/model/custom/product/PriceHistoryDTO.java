package com.supermarket.model.custom.product;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.supermarket.util.serializer.TimestampSerializer;

public class PriceHistoryDTO {

	private Integer productPrice;

	@JsonSerialize(using = TimestampSerializer.class)
	private Date effectiveDate;

//	@JsonSerialize(using = TimestampSerializer.class)
//	private Date lastEffectiveDate;

	public Integer getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(Integer productPrice) {
		this.productPrice = productPrice;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

//	public Date getLastEffectiveDate() {
//		return lastEffectiveDate;
//	}
//
//	public void setLastEffectiveDate(Date lastEffectiveDate) {
//		this.lastEffectiveDate = lastEffectiveDate;
//	}
//	

}
