package com.supermarket.model.custom.product;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.supermarket.util.deserializer.DateDeserializer;
import com.supermarket.util.serializer.TimestampSerializer;

public class ProductDTO {

	private Integer productId;

	private String productName;

	private Integer packQuantity;

	private Integer productPrice;

	private Integer currentStockPackageCount;

	@JsonSerialize(using = TimestampSerializer.class)
	@JsonDeserialize(using = DateDeserializer.class)
	private Date effectiveDate;

	@JsonSerialize(using = TimestampSerializer.class)
	@JsonDeserialize(using = DateDeserializer.class)
	private Date lastEffectiveDate;

	private Integer oldProductId;

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getPackQuantity() {
		return packQuantity;
	}

	public void setPackQuantity(Integer packQuantity) {
		this.packQuantity = packQuantity;
	}

	public Integer getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(Integer productPrice) {
		this.productPrice = productPrice;
	}

	public Integer getCurrentStockPackageCount() {
		return currentStockPackageCount;
	}

	public void setCurrentStockPackageCount(Integer currentStockPackageCount) {
		this.currentStockPackageCount = currentStockPackageCount;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Date getLastEffectiveDate() {
		return lastEffectiveDate;
	}

	public void setLastEffectiveDate(Date lastEffectiveDate) {
		this.lastEffectiveDate = lastEffectiveDate;
	}

	public Integer getOldProductId() {
		return oldProductId;
	}

	public void setOldProductId(Integer oldProductId) {
		this.oldProductId = oldProductId;
	}

//	public int getOldProductId() {
//		return oldProductId;
//	}
//
//	public void setOldProductId(Product oldProductId) {
//		if(oldProductId == null) {
//			this.oldProductId = 0;
//		} else {
//			this.oldProductId = oldProductId.getProductId();
//		}
//	}

}
