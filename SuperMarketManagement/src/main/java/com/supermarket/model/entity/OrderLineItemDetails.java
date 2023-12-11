package com.supermarket.model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "order_line_item_details")
public class OrderLineItemDetails {

	@ManyToOne
	@JoinColumn(name = "OLID_order_id")
	private OrderDetails orderId;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "OLID_id")
	private Integer olidId;

	@ManyToOne
	@JoinColumn(name = "OLID_product_id")
	private Product productId;

	@Column(name = "OLID_quantity_individual_unit")
	private Integer quantityIndividualUnit;

	@Column(name = "OLID_quantity_in_package")
	private Integer quantityInPackage;

	@Column(name = "OLID_status")
	private String olidStatus;

	@Column(name = "OLID_created_date")
	private Date createdDate;

	@Column(name = "OLID_update_date")
	private Date updateDate;

	public OrderDetails getOrderId() {
		return orderId;
	}

	public void setOrderId(OrderDetails orderId) {
		this.orderId = orderId;
	}

	public Integer getOlidId() {
		return olidId;
	}

	public void setOlidId(Integer olidId) {
		this.olidId = olidId;
	}

	public Product getProductId() {
		return productId;
	}

	public void setProductId(Product productId) {
		this.productId = productId;
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

	public String getOlidStatus() {
		return olidStatus;
	}

	public void setOlidStatus(String olidStatus) {
		this.olidStatus = olidStatus;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

}
