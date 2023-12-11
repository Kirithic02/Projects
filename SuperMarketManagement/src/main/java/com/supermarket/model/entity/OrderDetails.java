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
@Table(name = "order_details")
public class OrderDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ORDE_id")
	private Integer orderId;

	@Column(name = "ORDE_date", insertable = false, updatable = false)
	private Date orderedDate;

	@ManyToOne
	@JoinColumn(name = "ORDE_customer_id")
	private Customer customerId;

	@Column(name = "ORDE_expected_date")
	private Date orderExpectedDate;

	@Column(name = "ORDE_status")
	private String orderStatus;

	@Column(name = "ORDE_created_date")
	private Date orderCreatedDate;

	@Column(name = "ORDE_update_date")
	private Date orderUpdatedDate;

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

	public Customer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Customer customerId) {
		this.customerId = customerId;
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

	public Date getOrderCreatedDate() {
		return orderCreatedDate;
	}

	public void setOrderCreatedDate(Date orderCreatedDate) {
		this.orderCreatedDate = orderCreatedDate;
	}

	public Date getOrderUpdatedDate() {
		return orderUpdatedDate;
	}

	public void setOrderUpdatedDate(Date orderUpdatedDate) {
		this.orderUpdatedDate = orderUpdatedDate;
	}

}
