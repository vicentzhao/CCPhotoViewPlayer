package com.ccdrive.photoviewer.util;


public class PagenationBean {
	/** �ܼ�¼�� */
	private int totalRows=0;

	/** ��ǰҳ��ʼ��¼�� */
	private int offset=1;

	/** ��ǰҳ������¼�� */
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

	/** ��ҳ�� */
	private int totalPage=0;

	/** ��ǰҳ */
	private int currentPage=1;

	/** ÿҳ��¼�� */
	private int pageSize=16;

	/** �Ƿ�Ϊ��һҳ */
	private boolean isFirstPage;

	/** �Ƿ�Ϊ���һҳ */
	private boolean isLastPage;

	/** �Ƿ���ǰһҳ */
	private boolean hasPreviousPage;

	/** �Ƿ�����һҳ */
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
	 * Description:��ʼ����ҳ��Ϣ
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
	 * ��* ���㵱ǰҳ��ʼ��¼ ��* @param pageSize ÿҳ��¼�� ��* @param currentPage ��ǰ�ڼ�ҳ
	 * 
	 * @param totalRows
	 *            �ܼ�¼�� ��* @return ��ǰҳ��ʼ��¼�� ��
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
	 * Description:�����ж�ҳ����Ϣ,ֻ��is��������
	 */
	public boolean isFirstPage() {
		return currentPage == 1; // �����ǰҳ�ǵ�һҳ
	}

	public boolean isLastPage() {
		return currentPage >= totalPage; // �����ǰҳ�����һҳ
	}

	public boolean isHasPreviousPage() {
		return currentPage > 1; // ֻҪ��ǰҳ���ǵ�1ҳ
	}

	public boolean isHasNextPage() {
		return currentPage < totalPage; // ֻҪ��ǰҳ�������1ҳ
	}

	/**
	 * Description:������ҳ��,��̬����,���ⲿֱ��ͨ����������
	 * 
	 * @param pageSize
	 *            ÿҳ��¼��
	 * @param totalRows
	 *            �ܼ�¼��
	 * @return ��ҳ��
	 */
	public static int countTotalPage(final int pageSize, final int totalRows) {
		int totalPage = totalRows % pageSize == 0 ? totalRows / pageSize : totalRows
				/ pageSize + 1;
		return totalPage;
	}

	/**
	 * ��* ���㵱ǰҳ��ʼ��¼ ��* @param pageSize ÿҳ��¼�� ��* @param currentPage ��ǰ�ڼ�ҳ ��* @return
	 * ��ǰҳ��ʼ��¼�� ��
	 */
	public static int countOffset(final int pageSize, final int currentPage) {
		final int offset = pageSize * (currentPage - 1);
		return offset;
	}

	/**
	 * ���㵱ǰҳ
	 * 
	 * @param page
	 *            ����Ĳ���(����Ϊ��,��0,�򷵻�1)
	 * @return ��ǰҳ
	 */
	public static int countCurrentPage(int page) {
		final int curPage = (page <= 0 ? 1 : page);
		return curPage;
	}

}
