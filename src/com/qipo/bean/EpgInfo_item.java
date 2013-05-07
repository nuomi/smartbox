package com.qipo.bean;

import java.io.Serializable;

public class EpgInfo_item implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String playTime; // ��Ŀ���н�Ŀ��ʱ��
	private String tvProgram; // ���� �����ͣ�

	public String getPlayTime() {
		return playTime;
	}

	public void setPlayTime(String playTime) {
		this.playTime = playTime;
	}

	public String getTvProgram() {
		return tvProgram;
	}

	public void setTvProgram(String tvProgram) {
		this.tvProgram = tvProgram;
	}

}
