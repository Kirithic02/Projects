package com.supermarket.model.custom.product;

import com.supermarket.model.custom.OrderBy;

public class ProductSalesFilterList {

	private Integer length;

	private Integer start;

	private ProductSalesFilter filter;

	private OrderBy orderBy;

//	private String search;

//	private String searchColumn;

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public OrderBy getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(OrderBy orderBy) {
		this.orderBy = orderBy;
	}

	public ProductSalesFilter getFilter() {
		return filter;
	}

	public void setFilter(ProductSalesFilter filter) {
		this.filter = filter;
	}

}
