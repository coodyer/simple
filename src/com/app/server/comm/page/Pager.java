package com.app.server.comm.page;

import java.util.Collection;

import com.app.server.comm.base.BaseModel;

public class Pager extends BaseModel {

	private static final long serialVersionUID = 1L;
	private Integer totalRows;
	private Integer pageSize = 20;
	private Integer currentPage;
	private Integer totalPages=1;
	private Integer startRow;
	private Integer formNumber;
	private Collection<?> pageData;

	public Pager(Integer pageSize) {
		super();
		this.currentPage = 1;
		this.startRow = 0;
		this.pageSize = pageSize;
	}


	public Pager(Integer pageSize, Integer currentPage) {
		super();
		if(currentPage==null||currentPage<1){
			currentPage=1;
		}
		if(pageSize==null||pageSize>100){
			pageSize=20;
		}
		this.pageSize = pageSize;
		this.currentPage = currentPage;
	}


	public Pager() {
		this.currentPage = 1;
		this.startRow = 0;
	}

	public Collection<?> getPageData() {
		return pageData;
	}

	public void setPageData(Collection<?> pageData) {
		this.pageData = pageData;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		if(currentPage==null||currentPage<1){
			currentPage=1;
		}
		this.currentPage = currentPage;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		if(pageSize==null){
			pageSize=20;
		}
		if(pageSize>100){
			pageSize=100;
		}
		this.pageSize = pageSize;
	}

	public Integer getStartRow() {
		return startRow != 0 ? startRow : (currentPage - 1) * pageSize;
	}

	public void setStartRow(Integer startRow) {
		this.startRow = startRow;
	}

	public Integer getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}

	public Integer getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(Integer totalRows) {
		this.totalRows = totalRows;
		try {
			this.totalPages = totalRows / pageSize;
			Integer mod = totalRows % pageSize;
			if (mod > 0) {
				this.totalPages++;
			}
			if (this.totalPages == 0) {
				this.totalPages = 1;
			}
			if (this.currentPage > totalPages) {
				this.currentPage = totalPages;
			}
			this.startRow = (currentPage - 1) * pageSize;
			if (this.startRow < 0) {
				startRow = 0;
			}
			if (this.currentPage == 0 || this.currentPage < 0) {
				currentPage = 1;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public Integer getFormNumber() {
		return formNumber;
	}

	public void setFormNumber(Integer formNumber) {
		this.formNumber = formNumber;
	}
}
