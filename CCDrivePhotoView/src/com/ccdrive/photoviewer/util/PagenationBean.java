package com.ccdrive.photoviewer.util;


public class PagenationBean {
	/** 总记录数 */
	private int totalRows=0;

	/** 当前页开始记录号 */
	private int offset=1;

	/** 当前页结束记录号 */
	private int endset=1;

	public int getEndset() {
		return endset;
	}

	public void setEndset(int endset) {
		this.endset = endset;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	/** 总页数 */
	private int totalPage=0;

	/** 当前页 */
	private int currentPage=1;

	/** 每页记录数 */
	private int pageSize=16;

	/** 是否为第一页 */
	private boolean isFirstPage;

	/** 是否为最后一页 */
	private boolean isLastPage;

	/** 是否有前一页 */
	private boolean hasPreviousPage;

	/** 是否有下一页 */
	private boolean hasNextPage;

	public int getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * Description:初始化分页信息
	 */
	public void init() {
		
		this.currentPage = countCurrentPage(currentPage);
		
		this.totalPage = this.countTotalPage(this.pageSize, this.totalRows);
		this.currentPage = this.currentPage > this.totalPage ? this.totalPage
				: this.currentPage;
		this.offset = this.countOffset(this.pageSize, this.currentPage);
		this.endset = this.offset + this.pageSize;
		
		
		this.isFirstPage = isFirstPage();
		this.isLastPage = isLastPage();
		this.hasPreviousPage = isHasPreviousPage();
		this.hasNextPage = isHasNextPage();

	}

	/**
	 * 　* 计算当前页开始记录 　* @param pageSize 每页记录数 　* @param currentPage 当前第几页
	 * 
	 * @param totalRows
	 *            总记录数 　* @return 当前页开始记录号 　
	 */
	public void init(int currentPage, int pageSize, int totalRows) {
		this.currentPage = countCurrentPage(currentPage);
		this.pageSize = pageSize > 0 ? pageSize : 1;
		this.totalRows = totalRows > 0 ? totalRows : 0;	
		
		init();
	}

	public void init(String currentPage, String pageSize, int totalRows) {
		int c = 1;
		if (isNumeric(currentPage)) {
			c = Integer.parseInt(currentPage);
		}
		int p = 20;
		if (isNumeric(pageSize)) {
			p = Integer.parseInt(pageSize);
		}
		this.init(c, p, totalRows);
	}

	public void init(Object currentPage, Object pageSize, int totalRows) {
		String c = "1";
		if (currentPage != null) {
			c = currentPage.toString();
		}
		String p = "5";
		if (pageSize != null) {
			p = pageSize.toString();
		}
		this.init(c, p, totalRows);
	}

	public boolean isNumeric(String checkStr) {
		if (checkStr == null)
			return false;
		return checkStr.matches("\\d\\d*");

	}

	/**
	 * Description:以下判断页的信息,只需is方法即可
	 */
	public boolean isFirstPage() {
		return currentPage == 1; // 如果当前页是第一页
	}

	public boolean isLastPage() {
		return currentPage >= totalPage; // 如果当前页是最后一页
	}

	public boolean isHasPreviousPage() {
		return currentPage > 1; // 只要当前页不是第1页
	}

	public boolean isHasNextPage() {
		return currentPage < totalPage; // 只要当前页不是最后1页
	}

	/**
	 * Description:计算总页数,静态方法,供外部直接通过类名调用
	 * 
	 * @param pageSize
	 *            每页记录数
	 * @param totalRows
	 *            总记录数
	 * @return 总页数
	 */
	public static int countTotalPage(final int pageSize, final int totalRows) {
		int totalPage = totalRows % pageSize == 0 ? totalRows / pageSize : totalRows
				/ pageSize + 1;
		return totalPage;
	}

	/**
	 * 　* 计算当前页开始记录 　* @param pageSize 每页记录数 　* @param currentPage 当前第几页 　* @return
	 * 当前页开始记录号 　
	 */
	public static int countOffset(final int pageSize, final int currentPage) {
		final int offset = pageSize * (currentPage - 1);
		return offset;
	}

	/**
	 * 计算当前页
	 * 
	 * @param page
	 *            传入的参数(可能为空,即0,则返回1)
	 * @return 当前页
	 */
	public static int countCurrentPage(int page) {
		final int curPage = (page <= 0 ? 1 : page);
		return curPage;
	}

}
