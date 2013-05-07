package com.qipo.parser;

import com.demonzym.smartbox.R;
import com.qipo.bean.EpgInfo_item;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.net.http.AndroidHttpClient;
import android.util.Log;

public class TimeListXmlPullParser extends XmlPullParserBase {

	private static final String TAG = "TimeListXmlPullParser";

	// XmlPullParser
	public static ArrayList<EpgInfo_item> getTimeList(String Url) {
		ArrayList<EpgInfo_item> items = new ArrayList<EpgInfo_item>();
		boolean isAdd = false;
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
							String s1 = parser.getAttributeValue(1);
							EpgInfo_item items1 = new EpgInfo_item();
							if (s1.indexOf("Array") < 0) {
								items1.setPlayTime(s1.substring(0, 4).trim());
								items1.setTvProgram(s1.substring(5));
							} else {
								items1.setPlayTime("null");
								items1.setTvProgram("");
							}
							items.add(items1); // now

							String s2 = parser.getAttributeValue(2);
							EpgInfo_item items2 = new EpgInfo_item();
							if (s1.indexOf("Array") < 0) {
								items2.setPlayTime(s2.substring(0, 4).trim());
								items2.setTvProgram(s2.substring(5));
							} else {
								items2.setPlayTime("");
								items2.setTvProgram("");
							}
							items.add(items2); // next

						} else if (name.equals("item")) {
							if (!isAdd) {
								if (items.get(1).getTvProgram()
										.contains(parser.getAttributeValue(0))) {
									isAdd = true;
								}
							} else {
								EpgInfo_item item = new EpgInfo_item();
								item.setPlayTime(parser.getAttributeValue(0));
								item.setTvProgram(parser.getAttributeValue(1));
								items.add(item);
							}
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
			return items;
		}

		return null;
	}
}
