package com.demonzym.framework.net.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;

import com.demonzym.framework.util.Util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class HttpListener implements HttpCallBack{
	
	InternalHandler mHandler;
	
	String encode = "utf-8";
	
	public HttpListener(){
		if(Looper.myLooper() == null){
			mHandler = new InternalHandler(Looper.getMainLooper());
		}
		else{
			mHandler = new InternalHandler(Looper.myLooper());
		}
	}
	
	private static HttpListener mInstance;
	public static HttpListener getInstance(){
		if(mInstance == null){
			mInstance = new HttpListener();
		}
		return mInstance;
	}
		
	private static final int TRANSACTION_SUCCESS = 0x1;
	private static final int TRANSACTION_ERROR = 0x2;
	private class InternalHandler extends Handler {
		public InternalHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case TRANSACTION_ERROR:{
				int id = msg.arg1;
				int errorCode = msg.arg2;
				HttpResponse hr = (HttpResponse) msg.obj;
				if(!mListenerMap.containsKey(id)){
					return;
				}
				IHttpListener listener = mListenerMap.get(id);
				try {
					if(hr.data != null)
						listener.onError(id, errorCode, new String(hr.data, encode), hr.http);
					else
						listener.onError(id, errorCode, "", hr.http);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				mListenerMap.remove(id);
			}
				break;
			case TRANSACTION_SUCCESS:{
				int id = msg.arg1;
				HttpResponse hr = (HttpResponse) msg.obj;
				if(!mListenerMap.containsKey(id)){
					return;
				}
				IHttpListener listener = mListenerMap.get(id);
				if(hr.data != null){
					try {
						if(listener != null)
							listener.onSuccess(id, new String(hr.data, encode), hr.http);
							listener.onSuccess(id, hr);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else if(hr.inputstream != null){
					try {
						listener.onSuccess(id, Util.inputStream2String(hr.inputstream), hr.http);
						listener.onSuccess(id, hr);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				mListenerMap.remove(id);
			}
				break;
			}
			super.handleMessage(msg);
		}
	}
	
	public HashMap<Integer, IHttpListener> mListenerMap =
			new HashMap<Integer, IHttpListener>();
	public void addHttpListener(int id, IHttpListener l){
		mListenerMap.put(id, l);
	}
	public interface IHttpListener{
		public void onError(int requestId, int errorCode, String error, IHttp http);
		public void onSuccess(int requestId, String data, IHttp http);
		public void onSuccess(int requestId, HttpResponse hr);
	}

	public void onError(int requestId, int errCode, byte[] errStr, IHttp http) {
		HttpResponse hr = new HttpResponse(http, errStr);
		mHandler.obtainMessage(TRANSACTION_ERROR, requestId, errCode, hr).sendToTarget();
	}

	public void onReceived(int requestId, byte[] data, IHttp http) {
		HttpResponse hr = new HttpResponse(http, data);
		mHandler.obtainMessage(TRANSACTION_SUCCESS, requestId, 0, hr).sendToTarget();
	}

	public void onReceived(int requestId, InputStream stream,
			long contentLength, IHttp http) {
		HttpResponse hr = new HttpResponse(http, stream);
		mHandler.obtainMessage(TRANSACTION_SUCCESS, requestId, 0, hr).sendToTarget();
	}

	public void onDataLenReceived(int requestId, int len, boolean isTotalLen) {
		
	}

	public void onProcess(int requestId, int curr, int max) {
		
	}
	
}
