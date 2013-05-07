package com.demonzym.smartbox.activity;

import java.util.ArrayList;
import java.util.Iterator;

import com.demonzym.framework.util.Util;
import com.demonzym.smartbox.data.Foobar;
import com.demonzym.smartbox.data.TvChannel;
import com.demonzym.smartbox.data.TvLink;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceActivity;
import android.text.Editable;

/**
 * ����
 * @author Administrator
 *
 */
public class SettingActivity extends PreferenceActivity {

	public static final String SETTING_FAV = "fav"; // �ղ�
	public static final String SETTING_HISTORY = "history"; // ��ʷ
	

	public static final String SETTING_ACCOUNT = "accout"; //�ʺ�
	public static final String SETTING_PWD = "pwd"; //����
	
	public static final String SETTING_CHANNELLIST = "channellist"; //����Ƶ���б�

	synchronized public static void setFav(Context context,
			String fav) {
		SharedPreferences settings = context
				.getSharedPreferences(
						context.getPackageName() + "_preferences",
						Context.MODE_PRIVATE);
		settings.edit().putString(SETTING_FAV, fav).commit();

	}
	synchronized public static void remFav(Context context,
			String fav) {
		SharedPreferences settings = context
				.getSharedPreferences(
						context.getPackageName() + "_preferences",
						Context.MODE_PRIVATE);
		Editor editor=settings.edit();
		editor.remove(SETTING_FAV);
		editor.remove(fav);
		editor.commit();
	}

	synchronized public static String getFav(Context context) {
		SharedPreferences settings = context
				.getSharedPreferences(
						context.getPackageName() + "_preferences",
						Context.MODE_PRIVATE);
		String str = null;
		str = settings.getString(SETTING_FAV, "");
		return str;
	}
	
	synchronized public static void setAccount(Context context,
			String account) {
		SharedPreferences settings = context
				.getSharedPreferences(
						context.getPackageName() + "_preferences",
						Context.MODE_PRIVATE);
		settings.edit().putString(SETTING_ACCOUNT, account).commit();

	}

	synchronized public static String getAccount(Context context) {
		SharedPreferences settings = context
				.getSharedPreferences(
						context.getPackageName() + "_preferences",
						Context.MODE_PRIVATE);
		String str = "";
		str = settings.getString(SETTING_ACCOUNT, "");
		return str;
	}
	
	synchronized public static void setPwd(Context context,
			String pwd) {
		SharedPreferences settings = context
				.getSharedPreferences(
						context.getPackageName() + "_preferences",
						Context.MODE_PRIVATE);
		settings.edit().putString(SETTING_PWD, pwd).commit();

	}

	synchronized public static String getPwd(Context context) {
		SharedPreferences settings = context
				.getSharedPreferences(
						context.getPackageName() + "_preferences",
						Context.MODE_PRIVATE);
		String str = "";
		str = settings.getString(SETTING_PWD, "");
		return str;
	}
	
	synchronized public static void setHistory(Context context,
			String fav) {
		SharedPreferences settings = context
				.getSharedPreferences(
						context.getPackageName() + "_preferences",
						Context.MODE_PRIVATE);
		settings.edit().putString(SETTING_HISTORY, fav).commit();

	}

	synchronized public static String getHistory(Context context) {
		SharedPreferences settings = context
				.getSharedPreferences(
						context.getPackageName() + "_preferences",
						Context.MODE_PRIVATE);
		String str = null;
		str = settings.getString(SETTING_HISTORY, "");
		return str;
	}

	public static void addTvChannel(Context context, TvChannel tc) {
		Iterator<TvChannel> iterator1 = getTvChannelList(context).iterator();
		while(iterator1.hasNext()){
			TvChannel tc1 = iterator1.next();
			if(tc.id.equals(tc1.id))
				//�Ѵ��ڣ�����
				return;
		}
		SharedPreferences settings = context
				.getSharedPreferences(
						context.getPackageName() + "_preferences",
						Context.MODE_PRIVATE);
		String channeldata = "";
		//###&&&cid&&&epg&&&id&&&img&&&name&&&qid&&&%%%link%%%source
		channeldata = channeldata + "&&&" + tc.cid;
		channeldata = channeldata + "&&&" + tc.epg;
		channeldata = channeldata + "&&&" + tc.id;
		channeldata = channeldata + "&&&" + tc.img;
		channeldata = channeldata + "&&&" + tc.name;
		channeldata = channeldata + "&&&" + tc.qid;
		String linkdata = "";
		Iterator<TvLink> iterator = tc.tvlink_list.iterator();
		while(iterator.hasNext()){
			TvLink tl = iterator.next();
			linkdata = linkdata + "%%%" + tl.link;
			linkdata = linkdata + "%%%" + tl.source;			
		}
		channeldata = channeldata + "&&&" + linkdata;
		
		String data = getTvChannelString(context) + "###" + channeldata;
		
		settings.edit().putString(SETTING_CHANNELLIST, data).commit();
	}
	
	public static String getTvChannelString(Context context){
		SharedPreferences settings = context
				.getSharedPreferences(
						context.getPackageName() + "_preferences",
						Context.MODE_PRIVATE);
		String str = null;
		str = settings.getString(SETTING_CHANNELLIST, "");
		return str;
	}
	
	public static ArrayList<TvChannel> getTvChannelList(Context context){
		SharedPreferences settings = context
				.getSharedPreferences(
						context.getPackageName() + "_preferences",
						Context.MODE_PRIVATE);
		String str = null;
		str = settings.getString(SETTING_CHANNELLIST, "");
		ArrayList<TvChannel> list = new ArrayList<TvChannel>();
		
		String[] channeldataarray = str.split("###");
		if(channeldataarray != null && channeldataarray.length > 0){
			for(int i = 0; i < channeldataarray.length; i++){
				if(Util.isStringEmpty(channeldataarray[i]))
					continue;
				String[] channeldata = channeldataarray[i].split("&&&");
				String cid, id, qid, name, epg, img;
				cid = channeldata[1];
				epg = channeldata[2];
				id = channeldata[3];
				img = channeldata[4];
				name = channeldata[5];
				qid = channeldata[6];
				String[] tvlinkdata = channeldata[7].split("%%%");
				ArrayList<TvLink> tvlink_list = new ArrayList<TvLink>();
				for(int j = 0; j < tvlinkdata.length / 2; j++){
					String link, source;
					link = tvlinkdata[j * 2 + 1];
					source = tvlinkdata[j * 2 + 2];
					TvLink tl = new TvLink(link, source);
					tvlink_list.add(tl);
				}
				TvChannel tc = new TvChannel(cid, id, qid, name, epg, img, tvlink_list);
				list.add(tc);
			}
		}
		return list;
	}

}
