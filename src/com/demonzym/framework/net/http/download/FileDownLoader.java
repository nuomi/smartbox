package com.demonzym.framework.net.http.download;

import java.io.InputStream;
import java.util.HashMap;

import android.util.Log;

import com.demonzym.framework.net.http.HttpCallBack;
import com.demonzym.framework.net.http.HttpEngine;
import com.demonzym.framework.net.http.HttpRequest;
import com.demonzym.framework.net.http.IHttp;
import com.demonzym.framework.net.http.download.FileDownloadListener.IDownLoadListener;
import com.demonzym.framework.util.Util;

public class FileDownLoader implements HttpCallBack{

	private static FileDownLoader mInstance;
	
	private HashMap<Integer, String> mDownLoadMap = new HashMap<Integer, String>();
	
	public static FileDownLoader getInstance(){
		if(mInstance == null){
			mInstance = new FileDownLoader();
		}
		return mInstance;
	}
	
	public void downloadFile(String url, String path, IDownLoadListener idl){
		if(Util.isStringEmpty(url))
			return;
		
		Log.i("DownLoad", url);
		
		HttpRequest request = new HttpRequest(url);
		request.setHttpCallBack(this);
		request.setPath(path);
		request.setStreamCallBack(true);
		
		FileDownloadListener.getInstance().addIDownLoadListener(request.getRequestID(), idl);
		mDownLoadMap.put(request.getRequestID(), path);
		
		HttpEngine.getInstance().addRequest(request);
	}	

	public void onError(int requestId, int errCode, byte[] errStr, IHttp http) {
		if(mDownLoadMap.containsKey(requestId)){
			String path = mDownLoadMap.get(requestId);
			FileDownloadListener.getInstance().onError(requestId, path, errCode, errStr, http);
		}
	}

	public void onReceived(int requestId, byte[] data, IHttp http) {
		if(mDownLoadMap.containsKey(requestId)){
			String path = mDownLoadMap.get(requestId);
			FileDownloadListener.getInstance().onDownload(requestId, path, data, http);
		}
	}

	public void onReceived(int requestId, InputStream stream,
			long contentLength, IHttp http) {
		if(mDownLoadMap.containsKey(requestId)){
			String path = mDownLoadMap.get(requestId);
			FileDownloadListener.getInstance().onDownload(requestId, path, stream, contentLength, http);
		}
	}

	public void onDataLenReceived(int requestId, int len, boolean isTotalLen) {
		// TODO Auto-generated method stub
		
	}

	public void onProcess(int requestId, int curr, int max) {
//		if(mDownLoadMap.containsKey(requestId)){
//			FileDownloadListener.getInstance().onProcess(requestId, curr, max);
//		}
	}
}
