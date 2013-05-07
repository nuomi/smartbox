package com.qipo.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class TvList implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<Integer> ids;
	private HashMap<Integer, String> menu;
	private String server; // ���������
	private ArrayList<Channel> channels; // ���������Ŀ�б�

	public TvList(){
	}
	
	public HashMap<Integer, String> getMenu() {
		return menu;
	}

	public void setMenu(HashMap<Integer, String> menu) {
		this.menu = menu;
	}

	public ArrayList<Channel> getChannels() {
		return channels;
	}

	public void setChannels(ArrayList<Channel> channels) {
		this.channels = channels;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public ArrayList<Integer> getIds() {
		return ids;
	}

	public void setIds(ArrayList<Integer> ids) {
		this.ids = ids;
	}
}
