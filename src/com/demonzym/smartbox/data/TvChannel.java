package com.demonzym.smartbox.data;

import java.util.ArrayList;

public class TvChannel {
	public String cid, id, qid, name, epg, img;
	public ArrayList<TvLink> tvlink_list = new ArrayList<TvLink>();
	public TvChannel(String cid, String id, String qid, String name,
			String epg, String img, ArrayList<TvLink> tvlink_list){
		this.id = id;
		this.qid = qid;
		this.tvlink_list = tvlink_list;
		this.cid = cid;
		this.name = name;
		this.img = img;
		this.epg = epg;
	}
	
}
