package com.demonzym.smartbox.data;

import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TvEpgData {
	public String time;
	public String title;
	
	public TvEpgData(JSONObject jsonObj) {
		if(jsonObj != null){
			time = jsonObj.optString("ftime");
			title = jsonObj.optString("ftitle");
		}
	}
	
	public static String Array2String(TvEpgData[] array){
		String data = "";
		for(int i = 0; i < array.length; i++){
			data += array[i].time;
			data += "\n";
			data += array[i].title;
			data += "\n";
		}
		return data;
	}
	
	public static TvEpgData[] parseTvData(String data){
		try {
			JSONArray jsArr = new JSONArray(data);
			int count = jsArr.length();
			TvEpgData[] banners = new TvEpgData[count];
			for(int i = 0; i < count; i++){
				banners[i] = new TvEpgData(jsArr.optJSONObject(i));
			}
			
			return banners;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}

