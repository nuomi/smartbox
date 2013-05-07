package com.demonzym.smartbox.data;

import java.util.LinkedList;

public class TvUrlData {

	public static String url = "url";
	public static String foobar = "foobar";
	public static String dur = "dur";
	public static String total_dur = "total_dur";
	
	private String mUrl, mTotalDur;
	
	private LinkedList<String> mFoobarList = new LinkedList<String>();
	private LinkedList<String> mDurList = new LinkedList<String>();

	public LinkedList<String> getDurList() {
		return mDurList;
	}

	public void addDur(String dur) {
		this.mDurList.add(dur);
	}

	public LinkedList<String> getFoobarList() {
		return mFoobarList;
	}

	public void addFoobar(String foobar) {
		this.mFoobarList.add(foobar);
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String mUrl) {
		this.mUrl = mUrl;
	}

	public String getTotalDur() {
		return mTotalDur;
	}

	public void setTotalDur(String mTotalDur) {
		this.mTotalDur = mTotalDur;
	}
}
