package com.demonzym.smartbox.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class MovieData implements Serializable{
	
	private static final long serialVersionUID = 3L;
	
	public static final String name = "name";
	public static final String site = "site";
	public static final String img = "mimg";
	public static final String desc = "desc";
	public static final String director = "director";
	public static final String actor = "actor";
	public static final String url = "url";
	public static final String date = "date";
	
	public static final String year = "year";
	public static final String comm = "comm";
	public static final String score = "score";
	public static final String anothername = "anothername";
	public static final String lanage = "lanage";
	public static final String mlong = "mlong";
	public static final String type = "type";
	public static final String region = "region";
	
	public static final String id = "did";
	
	public ArrayList<Foobar> mFoobarList = new ArrayList<Foobar>();
	
	public HashMap<String, ArrayList<Foobar>> mMap =
			new HashMap<String, ArrayList<Foobar>>();
	
	private String mName, mSite, mImg, mDesc, mDirector, mActor, mUrl, mDate,
		mYear, mComm, mScore, mAnotherName, mLanage, mLong, mType, mRegion, mId;

	public String getId() {
		return mId;
	}

	public void setId(String mId) {
		this.mId = mId;
	}

	public String getYear() {
		return mYear;
	}

	public void setYear(String mYear) {
		this.mYear = mYear;
	}

	public String getComm() {
		return mComm;
	}

	public void setComm(String mComm) {
		this.mComm = mComm;
	}

	public String getScore() {
		return mScore;
	}

	public void setScore(String mScore) {
		this.mScore = mScore;
	}

	public String getAnotherName() {
		return mAnotherName;
	}

	public void setAnotherName(String mAnotherName) {
		this.mAnotherName = mAnotherName;
	}

	public String getLanage() {
		return mLanage;
	}

	public void setLanage(String mLanage) {
		this.mLanage = mLanage;
	}

	public String getLong() {
		return mLong;
	}

	public void setLong(String mLong) {
		this.mLong = mLong;
	}

	public String getType() {
		return mType;
	}

	public void setType(String mType) {
		this.mType = mType;
	}

	public String getRegion() {
		return mRegion;
	}

	public void setRegion(String mRegion) {
		this.mRegion = mRegion;
	}

	public String getDate() {
		return mDate;
	}

	public void setDate(String mDate) {
		this.mDate = mDate;
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public String getSite() {
		return mSite;
	}

	public void setSite(String mSite) {
		this.mSite = mSite;
	}

	public String getImg() {
		return mImg;
	}

	public void setImg(String mImg) {
		this.mImg = mImg;
	}

	public String getDesc() {
		return mDesc;
	}

	public void setDesc(String mDesc) {
		this.mDesc = mDesc;
	}

	public String getDirector() {
		return mDirector;
	}

	public void setDirector(String mDirector) {
		this.mDirector = mDirector;
	}

	public String getActor() {
		return mActor;
	}

	public void setActor(String mActor) {
		this.mActor = mActor;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String mUrl) {
		this.mUrl = mUrl;
	}
}
