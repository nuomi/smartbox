package com.demonzym.smartbox.data;

import java.io.Serializable;
import java.util.ArrayList;

public class MovieListData  implements Serializable{

	private static final long serialVersionUID = 2L;
	
	public final static String INFO = "INFO";
	public final static String COUNT = "COUNT";
	public final static String PAGECOUNT = "PAGECOUNT";
	public final static String PAGE = "PAGE";
	public final static String PAGESIZE = "PAGESIZE";
	
	public final static String ROWS = "ROWS";
	
	public ArrayList<Foobar> mFoobarList = new ArrayList<Foobar>();
	
	private String mCount, mPageCount, mPage, mPagesize;
	public String getCount() {
		return mCount;
	}
	public void setCount(String mCount) {
		this.mCount = mCount;
	}
	public String getPageCount() {
		return mPageCount;
	}
	public void setPageCount(String mPageCount) {
		this.mPageCount = mPageCount;
	}
	public String getPage() {
		return mPage;
	}
	public void setPage(String mPage) {
		this.mPage = mPage;
	}
	public String getPagesize() {
		return mPagesize;
	}
	public void setPagesize(String mPagesize) {
		this.mPagesize = mPagesize;
	}
	
}
