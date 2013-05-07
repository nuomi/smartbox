package com.qipo.bean;

import java.io.Serializable;

public class Channel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id; // id
	private String title; // Ƶ������
	private String url; // ���ŵ�ַ
	private String icon; // Ƶ��ͼ��ĵ�ַ
	private String epg; // ��ϸ�Ľ�Ŀ����ַ
	private String replay; // ��������ʱ��Ľ�Ŀ��
	private String cid; // ��Ŀ�ķ��� 0-����Ƶ��,1-���ָ���Ƶ��,2-����Ƶ��,3-��������,4-�۰�̨��Ƶ��,5-�ط�Ƶ��
	private int tn;
	private String type;
	private String himg;

	public String getHimg() {
		return himg;
	}

	public void setHimg(String himg) {
		this.himg = himg;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getTn() {
		return tn;
	}

	public void setTn(int tn) {
		this.tn = tn;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReplay() {
		return replay;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public void setReplay(String replay) {
		this.replay = replay;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getEpg() {
		return epg;
	}

	public void setEpg(String epg) {
		this.epg = epg;
	}

}
