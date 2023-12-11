package com.supermarket.model.custom.order;

import com.supermarket.model.custom.OrderBy;

public class OrderFilterList {

	private Integer length;

	private Integer start;

	private OrderFilter filter;

	private OrderBy orderBy;

	private String search;

	private String searchColumn;

	public OrderFilter getFilter() {
		return filter;
	}

	public void setFilter(OrderFilter filter) {
		this.filter = filter;
	}

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

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getSearchColumn() {
		return searchColumn;
	}

	public void setSearchColumn(String searchColumn) {
		this.searchColumn = searchColumn;
	}

}
