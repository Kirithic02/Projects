package com.supermarket.model.custom;

public class FilteredResponse {
	
	private String status;
	
	private Long totalCount;
	
	private Long filteredCount;
	
	private Object data;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

	public Long getFilteredCount() {
		return filteredCount;
	}

	public void setFilteredCount(Long filteredCount) {
		this.filteredCount = filteredCount;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	
}
