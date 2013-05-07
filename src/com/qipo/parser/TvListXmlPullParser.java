package com.qipo.parser;

import com.qipo.bean.Channel;
import com.qipo.bean.TvList;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

//import android.annotation.SuppressLint;
import android.net.http.AndroidHttpClient;
import android.util.Log;

public class TvListXmlPullParser extends XmlPullParserBase {

	private static final String TAG = "TvListXmlPullParser";

//	@SuppressLint("UseSparseArrays")
	public static TvList getTvList(String Url) {
		TvList tvlist = new TvList();
		ArrayList<Channel> channelList = new ArrayList<Channel>();
		XmlPullParser parser = null;
		AndroidHttpClient mHttpClient = AndroidHttpClient
				.newInstance("Android");
		parser = getUriXmlPullParser(Url, mHttpClient);
		if (parser != null) {
			try {
				int eventType = parser.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_TAG) {
						String name = parser.getName();
						if (name.equals("header")) {
							tvlist.setServer(parser.getAttributeValue(null,
									"server"));
							String s = parser
									.getAttributeValue(null, "classes");
							if (s != null) {
								HashMap<Integer, String> menu = new HashMap<Integer, String>();
								ArrayList<Integer> ids = new ArrayList<Integer>();
								String[] ss = s.trim().split(",");
								for (int i = 0; i < ss.length; i++) {
									String[] stemp = ss[i].trim().split("-");
									menu.put(Integer.parseInt(stemp[0]),
											stemp[1]); // key-menu
									ids.add(Integer.parseInt(stemp[0])); // keylist
								}
								tvlist.setMenu(menu);
								tvlist.setIds(ids);
							}
						} else if (name.equals("channel")) {
							Channel element = new Channel();
							element.setId(parser.getAttributeValue(null, "id")); // id
							// element.setReplay(parser.getAttributeValue(null,"replay"));
							// //��������ʱ����
							element.setUrl(parser
									.getAttributeValue(null, "src"));
							element.setEpg(parser
									.getAttributeValue(null, "epg")); // �����Ŀ��ϸ���б�
							element.setIcon(parser.getAttributeValue(null,
									"icon")); // ̨��
							element.setTitle(parser.getAttributeValue(null,
									"name")); // ̨����
							element.setCid(parser
									.getAttributeValue(null, "cid")); // ̨�ķ���
																		// (����ȥ��������)
							element.setTn(Integer.parseInt(parser
									.getAttributeValue(null, "tn")));
							element.setType(parser.getAttributeValue(null,
									"class"));

							element.setHimg(parser.getAttributeValue(null,
									"himg"));
							channelList.add(element);
						}
					}
					eventType = parser.next();
				}
			} catch (MalformedURLException e) {
				Log.i(TAG, e.toString());
				return null;
			} catch (IOException e) {
				Log.i(TAG, e.toString());
				return null;
			} catch (XmlPullParserException e) {
				Log.i(TAG, e.toString());
				return null;
			} finally {
				if (mHttpClient != null) {
					mHttpClient.close();
				}
			}
			tvlist.setChannels(channelList);
			return tvlist;
		}
		return null;
	}
}
