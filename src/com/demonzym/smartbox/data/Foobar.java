package com.demonzym.smartbox.data;

import java.io.Serializable;

public class Foobar implements Serializable{
	
	public final static String foobar = "foobar";
	public final static String title = "title";
	public final static String url = "url";
	public final static String image = "img";
	public final static String mimg = "mimg";
	public final static String name = "name";
	public final static String link = "link";
	public final static String id = "id";
	public final static String type = "type";
	public final static String classnames = "class";
	public final static String classname = "classname";
	
	private static final long serialVersionUID = 1L; 
	
    private String mTitle, mUrl, mImage, mName, mLink, mId, mImg, mType;
    
    
    public String getType() {
		return mType;
	}
	public void setType(String mType) {
		this.mType = mType.replaceAll(" ", "");
	}
	public String getmImg() {
		return mImg;
	}
	public void setmImg(String mImg) {
		this.mImg = mImg.replaceAll(" ", "");
	}
	public String getId() {
		return mId;
	}
	public void setId(String mId) {
		this.mId = mId.replaceAll(" ", "");
	}
	public String getUrl() {
		return mUrl;
	}
	public void setUrl(String url) {
		this.mUrl = url.replaceAll(" ", "");
	}
	public String getImage() {
		return mImage;
	}
	public void setImage(String image) {
		this.mImage = image.replaceAll(" ", "");
	}
	public String getTitle() {
        return mTitle;
    }
    public void setTitle(String title) {
        this.mTitle = title.replaceAll(" ", "");
    }
    public String getName() {
        return mName==null?"":mName;
    }
    public void setName(String name) {
        this.mName = name.replaceAll(" ", "");
    }

	public String getLink() {
		return mLink;
	}
	public void setLink(String mLink) {
		this.mLink = mLink.replaceAll(" ", "");
	}
}
