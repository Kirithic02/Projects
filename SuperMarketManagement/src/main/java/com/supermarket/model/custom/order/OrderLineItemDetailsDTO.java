package com.supermarket.model.custom.order;

public class OrderLineItemDetailsDTO {

	private Integer olidId;

	private Integer productId;

	private String productName;

	private Integer productPrice;

	private Integer quantityIndividualUnit;

	private Integer quantityInPackage;

	private String olidStatus;

	public Integer getOlidId() {
		return olidId;
	}

	public void setOlidId(Integer olidId) {
		this.olidId = olidId;
	}

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

	public Integer getQuantityIndividualUnit() {
		return quantityIndividualUnit;
	}

	public void setQuantityIndividualUnit(Integer quantityIndividualUnit) {
		this.quantityIndividualUnit = quantityIndividualUnit;
	}

	public Integer getQuantityInPackage() {
		return quantityInPackage;
	}

	public void setQuantityInPackage(Integer quantityInPackage) {
		this.quantityInPackage = quantityInPackage;
	}

	public Integer getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(Integer productPrice) {
		this.productPrice = productPrice;
	}

	public String getOlidStatus() {
		return olidStatus;
	}

	public void setOlidStatus(String olidStatus) {
		this.olidStatus = olidStatus;
	}

}
