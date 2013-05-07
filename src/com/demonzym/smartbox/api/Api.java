package com.demonzym.smartbox.api;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.util.Log;

import com.demonzym.framework.net.http.HttpEngine;
import com.demonzym.framework.net.http.HttpListener;
import com.demonzym.framework.net.http.HttpListener.IHttpListener;
import com.demonzym.framework.net.http.HttpRequest;
import com.demonzym.framework.net.http.HttpResponse;
import com.demonzym.framework.net.http.IHttp;
import com.demonzym.framework.util.Util;
import com.demonzym.smartbox.activity.SettingActivity;
import com.demonzym.smartbox.data.Foobar;
import com.demonzym.smartbox.data.TvChannel;
import com.demonzym.smartbox.data.TvClass;
import com.demonzym.smartbox.data.UserInfo;
import com.demonzym.smartbox.data.XMLReader;
import com.demonzym.smartbox.data.XMLWriter;
import com.demonzym.smartbox.protocal.ConstValues;

/**
 * ���������ز�����������������дsetting��
 * @author Administrator
 *
 */
public class Api {
	
	private final static String TAG = "API";
	
	private static String URL_HDP = "";
	private static String URL_MOVIE = "";
	private static String URL_TV = "";
	private static String URL_CARTOON = "";
	private static String URL_ZY = "";
	
	public static List<Foobar> mMovieList;
	public static List<Foobar> mTvList;				
	public static List<Foobar> mCartoonList;
	public static List<Foobar> mZyList;
	
	private static boolean enable_hdp = true;
	
	public static boolean[] iniOk(){
		if(		Util.isStringEmpty(URL_HDP) ||
				Util.isStringEmpty(URL_MOVIE) ||
				Util.isStringEmpty(URL_TV) ||
				Util.isStringEmpty(URL_CARTOON) ||
				Util.isStringEmpty(URL_ZY) ||
				mMovieList == null ||
				mTvList == null ||
				mCartoonList == null ||
				mZyList == null
			)
			return new boolean[]{false, enable_hdp};
		return new boolean[]{true, enable_hdp};
	}
	
	public static void getHDP_URL(){
//		HttpRequest request = new HttpRequest(ConstValues.HOST_URL[new Random().nextInt(4)]);
//		HttpListener.getInstance().addHttpListener(request.getRequestID(), new IHttpListener() {
//			
//			public void onSuccess(int requestId, String data, IHttp http) {
//				Log.i(TAG, "getHDP_URL success");
//				List<Foobar> list = XMLReader.getFoobarsFromXml(data);
//				if(list != null){
//					URL_HDP = list.get(0).getUrl();
					URL_HDP = ConstValues.HOST_URL;
					getCHANNEL_URL();
//				}
//			}
//			
//			public void onError(int requestId, int errorCode, String error, IHttp http) {
//				Log.e("getHDP_URL", "error");
//				enable_hdp = false;
//			}
//		});
//		
//		request.setHttpCallBack(HttpListener.getInstance());
//		
//		HttpEngine.getInstance().addRequest(request);
	}
	
	public static void getCHANNEL_URL(){
		HttpRequest request = new HttpRequest(URL_HDP);
		HttpListener.getInstance().addHttpListener(request.getRequestID(), new IHttpListener() {
			
			public void onSuccess(int requestId, String data, IHttp http) {
				Log.i(TAG, "getCHANNEL_URL success");
				List<Foobar> list = XMLReader.getFoobarsFromXml(data);
				if(list != null){
					URL_MOVIE = list.get(0).getUrl();
					URL_TV = list.get(1).getUrl();
					URL_CARTOON = list.get(2).getUrl();
					URL_ZY = list.get(3).getUrl();
					
					getListInCartoon(null, null, null);
					getListInMovie(null, null, null);
					getListInTv(null, null, null);
					getListInZy(null, null, null);
				}
			}
			
			public void onError(int requestId, int errorCode, String error, IHttp http) {
				Log.e("getCHANNEL_URL", "error");
				enable_hdp = false;
			}

			public void onSuccess(int requestId, HttpResponse hr) {
				// TODO Auto-generated method stub
				
			}
		});
		
		request.setHttpCallBack(HttpListener.getInstance());
		
		HttpEngine.getInstance().addRequest(request);
	}
	
	public static void getListInMovie(final String name, final IHttpListener listener, final String index){
		if(mMovieList != null){
			Iterator<Foobar> iterator = mMovieList.iterator();
			while(iterator.hasNext()){
				Foobar foobar = iterator.next();
				if(foobar.getName().trim().equals(name)){
					int i = foobar.getUrl().lastIndexOf("/") + 1;
					String url = foobar.getUrl();
					if(!Util.isStringEmpty(index)){
						url = url.substring(0, i);
						url = url + index;
					}
					HttpRequest hr = new HttpRequest(url);
					HttpListener.getInstance().addHttpListener(hr.getRequestID(), listener);
					hr.setHttpCallBack(HttpListener.getInstance());
					
					HttpEngine.getInstance().addRequest(hr);
				}
			}
			return;
		}
		
		HttpRequest request = new HttpRequest(URL_MOVIE);
		
		HttpListener.getInstance().addHttpListener(request.getRequestID(), new IHttpListener() {
			
			public void onSuccess(int requestId, String data, IHttp http) {
				Log.i(TAG, "getListInMovie success");
				Log.v("data.......", data+"");
				mMovieList = XMLReader.getFoobarsFromXmlNew(data);			//xlh 获取 MovieList
				
				Log.v("mMovieList.size......", mMovieList.size()+"");
				Log.v("mMovieList.......", mMovieList+"");
				
				if(mMovieList != null){
					
					Log.v("mMovieList.size......", mMovieList.size()+"");
					Log.v("mMovieList.......", mMovieList+"");
					
					Iterator<Foobar> iterator = mMovieList.iterator();
					while(iterator.hasNext()){
						Foobar foobar = iterator.next();
						if(foobar.getName().trim().equals(name)){
							int i = foobar.getUrl().lastIndexOf("/") + 1;
							String url = foobar.getUrl();
							if(!Util.isStringEmpty(index)){
								url = url.substring(0, i);
								url = url + index;
							}
							HttpRequest hr = new HttpRequest(url);
							HttpListener.getInstance().addHttpListener(hr.getRequestID(), listener);
							hr.setHttpCallBack(HttpListener.getInstance());
							
							HttpEngine.getInstance().addRequest(hr);
						}
					}
				}
			}
			
			public void onError(int requestId, int errorCode, String error, IHttp http) {
				Log.e("getListInMovie", "error");
				enable_hdp = false;
			}

			public void onSuccess(int requestId, HttpResponse hr) {
				// TODO Auto-generated method stub
				
			}
		});
		
		request.setHttpCallBack(HttpListener.getInstance());
		
		HttpEngine.getInstance().addRequest(request);
	}
	
	public static void getListInTv(final String name, final IHttpListener listener, final String index){
		if(mTvList != null){
			Iterator<Foobar> iterator = mTvList.iterator();
			while(iterator.hasNext()){
				Foobar foobar = iterator.next();
				if(foobar.getName().trim().equals(name)){
					int i = foobar.getUrl().lastIndexOf("/") + 1;
					String url = foobar.getUrl();
					if(!Util.isStringEmpty(index)){
						url = url.substring(0, i);
						url = url + index;
					}
					HttpRequest hr = new HttpRequest(url);
					HttpListener.getInstance().addHttpListener(hr.getRequestID(), listener);
					hr.setHttpCallBack(HttpListener.getInstance());
					
					HttpEngine.getInstance().addRequest(hr);
				}
			}
			return;
		}
		HttpRequest request = new HttpRequest(URL_TV);
		HttpListener.getInstance().addHttpListener(request.getRequestID(), new IHttpListener() {
			
			public void onSuccess(int requestId, String data, IHttp http) {
				Log.i(TAG, "getListInTv success");
				mTvList = XMLReader.getFoobarsFromXmlNew(data);
				if(mTvList != null){
					Iterator<Foobar> iterator = mTvList.iterator();
					while(iterator.hasNext()){
						Foobar foobar = iterator.next();
						if(foobar.getName().trim().equals(name)){
							int i = foobar.getUrl().lastIndexOf("/") + 1;
							String url = foobar.getUrl();
							if(!Util.isStringEmpty(index)){
								url = url.substring(0, i);
								url = url + index;
							}
							HttpRequest hr = new HttpRequest(url);
							HttpListener.getInstance().addHttpListener(hr.getRequestID(), listener);
							hr.setHttpCallBack(HttpListener.getInstance());
							
							HttpEngine.getInstance().addRequest(hr);
						}
					}
				}
			}
			
			public void onError(int requestId, int errorCode, String error, IHttp http) {
				Log.e("getListInTv", "error");
				enable_hdp = false;
			}

			public void onSuccess(int requestId, HttpResponse hr) {
				// TODO Auto-generated method stub
				
			}
		});
		
		request.setHttpCallBack(HttpListener.getInstance());
		
		HttpEngine.getInstance().addRequest(request);
	}
	
	public static void getListInCartoon(final String name, final IHttpListener listener, final String index){
		if(mCartoonList != null){
			Iterator<Foobar> iterator = mCartoonList.iterator();
			while(iterator.hasNext()){
				Foobar foobar = iterator.next();
				if(foobar.getName().trim().equals(name)){
					int i = foobar.getUrl().lastIndexOf("/") + 1;
					String url = foobar.getUrl();
					if(!Util.isStringEmpty(index)){
						url = url.substring(0, i);
						url = url + index;
					}
					HttpRequest hr = new HttpRequest(url);
					HttpListener.getInstance().addHttpListener(hr.getRequestID(), listener);
					hr.setHttpCallBack(HttpListener.getInstance());
					
					HttpEngine.getInstance().addRequest(hr);
				}
			}
			return;
		}
		HttpRequest request = new HttpRequest(URL_CARTOON);
		HttpListener.getInstance().addHttpListener(request.getRequestID(), new IHttpListener() {
			
			public void onSuccess(int requestId, String data, IHttp http) {
				Log.i(TAG, "getListInCartoon success");
				mCartoonList = XMLReader.getFoobarsFromXmlNew(data);
				if(mCartoonList != null){
					Iterator<Foobar> iterator = mCartoonList.iterator();
					while(iterator.hasNext()){
						Foobar foobar = iterator.next();
						if(foobar.getName().trim().equals(name)){
							int i = foobar.getUrl().lastIndexOf("/") + 1;
							String url = foobar.getUrl();
							if(!Util.isStringEmpty(index)){
								url = url.substring(0, i);
								url = url + index;
							}
							HttpRequest hr = new HttpRequest(url);
							HttpListener.getInstance().addHttpListener(hr.getRequestID(), listener);
							hr.setHttpCallBack(HttpListener.getInstance());
							
							HttpEngine.getInstance().addRequest(hr);
						}
					}
				}
			}
			
			public void onError(int requestId, int errorCode, String error, IHttp http) {
				Log.e("getListInCartoon", "error");
				enable_hdp = false;
			}

			public void onSuccess(int requestId, HttpResponse hr) {
				// TODO Auto-generated method stub
				
			}
		});
		
		request.setHttpCallBack(HttpListener.getInstance());
		
		HttpEngine.getInstance().addRequest(request);
	}

	public static void getListInZy(final String name, final IHttpListener listener, final String index){
		if(mZyList != null){
			Iterator<Foobar> iterator = mZyList.iterator();
			while(iterator.hasNext()){
				Foobar foobar = iterator.next();
				if(foobar.getName().trim().equals(name)){
					int i = foobar.getUrl().lastIndexOf("/") + 1;
					String url = foobar.getUrl();
					if(!Util.isStringEmpty(index)){
						url = url.substring(0, i);
						url = url + index;
					}
					HttpRequest hr = new HttpRequest(url);
					HttpListener.getInstance().addHttpListener(hr.getRequestID(), listener);
					hr.setHttpCallBack(HttpListener.getInstance());
					
					HttpEngine.getInstance().addRequest(hr);
				}
			}
			return;
		}
		HttpRequest request = new HttpRequest(URL_ZY);
		HttpListener.getInstance().addHttpListener(request.getRequestID(), new IHttpListener() {
			
			public void onSuccess(int requestId, String data, IHttp http) {
				Log.i(TAG, "getListInZy success");
				mZyList = XMLReader.getFoobarsFromXmlNew(data);
				if(mZyList != null){
					Iterator<Foobar> iterator = mZyList.iterator();
					while(iterator.hasNext()){
						Foobar foobar = iterator.next();
						if(foobar.getName().trim().equals(name)){
							int i = foobar.getUrl().lastIndexOf("/") + 1;
							String url = foobar.getUrl();
							if(!Util.isStringEmpty(index)){
								url = url.substring(0, i);
								url = url + index;
							}
							HttpRequest hr = new HttpRequest(url);
							HttpListener.getInstance().addHttpListener(hr.getRequestID(), listener);
							hr.setHttpCallBack(HttpListener.getInstance());
							
							HttpEngine.getInstance().addRequest(hr);
						}
					}
				}
			}
			
			public void onError(int requestId, int errorCode, String error, IHttp http) {
				Log.e("getListInZy", "error");
				enable_hdp = false;
			}

			public void onSuccess(int requestId, HttpResponse hr) {
				// TODO Auto-generated method stub
				
			}
		});
		
		request.setHttpCallBack(HttpListener.getInstance());
		
		HttpEngine.getInstance().addRequest(request);
	}

	public static void getMovieData(String link, IHttpListener l) {
		HttpRequest request = new HttpRequest(link);
		HttpListener.getInstance().addHttpListener(request.getRequestID(), l);
		
		request.setHttpCallBack(HttpListener.getInstance());
		
		HttpEngine.getInstance().addRequest(request);
	}
	
	public static void getMovieFoobar(String link, IHttpListener l) {
		HttpRequest request = new HttpRequest(link);
		HttpListener.getInstance().addHttpListener(request.getRequestID(), l);
		
		request.setHttpCallBack(HttpListener.getInstance());
		
		HttpEngine.getInstance().addRequest(request);
	}
		//xlh budong
	public static int getSearchList(String key, IHttpListener l){		
		HttpRequest request = new HttpRequest(
				ConstValues.SEARCH_URL
						+ key);
		HttpListener.getInstance().addHttpListener(request.getRequestID(), l);
		
		request.setHttpCallBack(HttpListener.getInstance());
		
		HttpEngine.getInstance().addRequest(request);
	
		return request.getRequestID();
	}
	
	public static HashMap<TvClass,ArrayList<TvChannel>> mTvLiveDataMap
		= new HashMap<TvClass, ArrayList<TvChannel>>();
	//����ֱ��
	public static void getLiveTv(){
		HttpRequest request = new HttpRequest(ConstValues.TV_URL);
		HttpListener.getInstance().addHttpListener(request.getRequestID(), new IHttpListener() {
			
			public void onSuccess(int requestId, String data, IHttp http) {
				Log.i(TAG, "getLiveTv success");
				mTvLiveDataMap = XMLReader.getTvLiveMapFromXml(data);
				
			}
			
			public void onError(int requestId, int errorCode, String error, IHttp http) {
				Log.e("getLiveTv", "error");
				enable_hdp = false;
			}

			public void onSuccess(int requestId, HttpResponse hr) {
				// TODO Auto-generated method stub
				
			}
		});
		
		request.setHttpCallBack(HttpListener.getInstance());
		
		HttpEngine.getInstance().addRequest(request);
	}

	public static void regAccount(String account, String pwd, String email, IHttpListener l) {
		HttpRequest request = new HttpRequest(ConstValues.URL_REG, HttpRequest.METHOD_POST);
		request.addHttpParams("userid", account);
		request.addHttpParams("userpwd", pwd);
		request.addHttpParams("userpwdok", pwd);
		request.addHttpParams("email", email);
		
		HttpListener.getInstance().addHttpListener(request.getRequestID(), l);
		
		request.setHttpCallBack(HttpListener.getInstance());
		
		HttpEngine.getInstance().addRequest(request);
	}
	
	public static void login(String account, String pwd, IHttpListener l) {
		HttpRequest request = new HttpRequest(ConstValues.URL_LOGIN, HttpRequest.METHOD_POST);
		request.addHttpParams("userid", account);
		request.addHttpParams("pwd", pwd);
		
		HttpListener.getInstance().addHttpListener(request.getRequestID(), l);
		
		request.setHttpCallBack(HttpListener.getInstance());
		
		HttpEngine.getInstance().addRequest(request);
	}
	
	public static void addFav(Foobar foobar, Context context){
		
		if(!UserInfo.isOnline()){
			//未登录，存储于本地
			String fl = SettingActivity.getFav(context);
			
			List<Foobar> list = XMLReader.getFoobarsFromXml(fl);
			if(!Util.isContainFoobar(list, foobar)){
				list.add(foobar);
				String s = XMLWriter.foobar2xmlDOM(list);
				SettingActivity.setFav(context, s);
			}
			else{
				//已经收藏过了
				return;
			}
			
		}else{
			HttpRequest request = new HttpRequest(ConstValues.URL_FAV, HttpRequest.METHOD_POST);
			request.addHttpParams("did", foobar.getId());
			request.addHttpParams("uid", UserInfo.uid);
			
			HttpListener.getInstance().addHttpListener(request.getRequestID(), new IHttpListener() {
				
				public void onSuccess(int requestId, String data, IHttp http) {
					Log.e("tag", "" + data);
				}
				
				public void onError(int requestId, int errorCode, String error, IHttp http) {
					// TODO Auto-generated method stub
					
				}

				public void onSuccess(int requestId, HttpResponse hr) {
					// TODO Auto-generated method stub
					
				}
			});
			
			request.setHttpCallBack(HttpListener.getInstance());
			
			HttpEngine.getInstance().addRequest(request);

			//同时存储于本地
			String fl = SettingActivity.getFav(context);

			List<Foobar> list = XMLReader.getFoobarsFromXml(fl);
			if(!Util.isContainFoobar(list, foobar)){
				list.add(foobar);
				String s = XMLWriter.foobar2xmlDOM(list);
				SettingActivity.setFav(context, s);
			}
			else{
				//已经收藏过了
				return;
			}
		}
	}
	
	public static void remFav(Foobar foobar, Context context){
		String fl = SettingActivity.getFav(context);

		List<Foobar> list = XMLReader.getFoobarsFromXml(fl);
		if(Util.isContainFoobar(list, foobar)){

			Iterator<Foobar> iterator = list.iterator();
			while(iterator.hasNext()){
				Foobar fb = iterator.next();
				if(fb.getName().equals(foobar.getName()) ||
						fb.getId().equals(foobar.getId())){
					iterator.remove();					
				}
			}
			String s = XMLWriter.foobar2xmlDOM(list);
			SettingActivity.setFav(context, s);
		}
	}	
/*	
	public static boolean addFav(Foobar foobar, Context context){
		if(UserInfo.isOnline()){
			HttpRequest request = new HttpRequest(ConstValues.URL_FAV, HttpRequest.METHOD_POST);
			request.addHttpParams("did", foobar.getId());
			request.addHttpParams("uid", UserInfo.uid);
			
			HttpListener.getInstance().addHttpListener(request.getRequestID(), new IHttpListener() {
				
				public void onSuccess(int requestId, String data, IHttp http) {
					Log.e("tag", "" + data);
				}
				
				public void onError(int requestId, int errorCode, String error, IHttp http) {
					// TODO Auto-generated method stub
					
				}

				public void onSuccess(int requestId, HttpResponse hr) {
					// TODO Auto-generated method stub
					
				}
			});
			
			request.setHttpCallBack(HttpListener.getInstance());			
			HttpEngine.getInstance().addRequest(request);
		}		
			//同时存储于本地
			String fl = SettingActivity.getFav(context);				
			List<Foobar> list = XMLReader.getFoobarsFromXml(fl); 
			
			int index=Util.isContainFoobar(list, foobar);
			if(index==0){
				list.add(foobar);
				String s = XMLWriter.foobar2xmlDOM(list);
				SettingActivity.setFav(context, s);	
				
				Log.w("shoucang", "收藏了");
				return true;
			}
			else{
				//已经收藏过了
				list.remove(index);							//xlh_error
				String s = XMLWriter.foobar2xmlDOM(list);
				SettingActivity.setFav(context, s);	
				
				Log.w("shoucang", "取消了收藏");
				return false;
			}		
	}
*/
	public static boolean hasFav(String id, Context context) {
		if(Util.isStringEmpty(id)){
			return false;
		}
		String fav = SettingActivity.getFav(context);
		if(fav.indexOf("<id>" + id + "</id>") > -1)
			return true;
		return false;
	}

	public static void addHistory(Foobar foobar,
			Context context) {
		String fl = SettingActivity.getHistory(context);

		if(fl.endsWith(",")){
			fl = fl.substring(0, fl.length() - 1);
		}
//		fl = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><data><uid/><foobar><id>1936</id><name>�������������յ�</name><img>http://v.guozitv.com/txt/360/img/998/1344752743-6994.jpg</img><link>http://v.guozitv.com/info/1936</link></foobar></data>";
		
		List<Foobar> list = XMLReader.getFoobarsFromXml(fl);
		if(!Util.isContainFoobar(list, foobar)){
			list.add(foobar);
			String s = XMLWriter.foobar2xmlDOM(list);
			SettingActivity.setHistory(context, s);
		}
		else{
			//�Ѿ��ղع���
			return;
		}
	}
}
