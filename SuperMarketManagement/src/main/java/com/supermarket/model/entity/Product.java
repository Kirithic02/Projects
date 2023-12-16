package com.supermarket.model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Product_info")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PROD_id")
	private Integer productId;

	@Column(name = "PROD_name")
	private String productName;

	@Column(name = "PROD_pack_quantity")
	private Integer packQuantity;

	@Column(name = "PROD_price")
	private Integer productPrice;

	@Column(name = "PROD_current_stock_package_count")
	private Integer currentStockPackageCount;
	
	@Column(name = "PROD_category")
	private String productCategory;

	@Column(name = "PROD_effective_date")
	private Date effectiveDate;

	@Column(name = "PROD_last_effective_date")
	private Date lastEffectiveDate;

	@OneToOne
	@JoinColumn(name = "PROD_old_id")
	private Product oldProductId;

	@Column(name = "PROD_created_date")
	private Date createdDate;

	@Column(name = "PROD_updated_date")
	private Date updatedDate;

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

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
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

	public Product getOldProductId() {
		return oldProductId;
	}

	public void setOldProductId(Product oldProductId) {
		this.oldProductId = oldProductId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

}
