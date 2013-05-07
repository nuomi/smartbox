package com.demonzym.framework.net.http.download;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.demonzym.framework.net.http.IHttp;
import com.demonzym.framework.util.Util;

public class FileDownloadListener {
	
	InternalHandler mHandler;
	
	public FileDownloadListener(){
		if(Looper.myLooper() == null){
			mHandler = new InternalHandler(Looper.getMainLooper());
		}
		else{
			mHandler = new InternalHandler(Looper.myLooper());
		}
	}
	
	private static FileDownloadListener mInstance;
	public static FileDownloadListener getInstance(){
		if(mInstance == null){
			mInstance = new FileDownloadListener();
		}
		return mInstance;
	}
	
	private HashMap<Integer, IDownLoadListener> mListenerMap =
			new HashMap<Integer, IDownLoadListener>();
	public void addIDownLoadListener(int requestId, IDownLoadListener idl){
		if(!mListenerMap.containsKey(requestId)){
			mListenerMap.put(requestId, idl);
		}
	}
	
	private class Downloader{
		IHttp http;
		IDownLoadListener listener;
		String path;
		public Downloader(IHttp h, IDownLoadListener l, String p){
			http = h;
			listener = l;
			path = p;
		}
	}
	
	public void onError(int requestId, String path, int errCode, byte[] errStr, IHttp http){
		if(!mListenerMap.containsKey(requestId)){
			return;
		}
		Downloader dl = new Downloader(http, mListenerMap.get(requestId), path);
		mHandler.obtainMessage(TRANSACTION_ERROR, dl).sendToTarget();
		
		mListenerMap.remove(requestId);
	}

	public void onDownload(int requestId, String path, byte[] data, IHttp http) {
		if (!mListenerMap.containsKey(requestId)) {
			return;
		}

		Downloader dl = new Downloader(http, mListenerMap.get(requestId), path);

		boolean bDecompress = true;
		try {
			String encoding = http.getHeaderField("Content-Encoding");
			if (encoding != null && encoding.equals("gzip")) {
				data = Util.gzipDecompress(data);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			bDecompress = false;
		}

		if (bDecompress) {
			if (path != null) {
				saveToFile(path, data, dl);
			}
			mHandler.obtainMessage(TRANSACTION_SUCCESS, dl).sendToTarget();
		} else {
			mHandler.obtainMessage(TRANSACTION_ERROR, dl).sendToTarget();
		}

		mListenerMap.remove(requestId);
	}

	public void onDownload(int requestId, String path, InputStream stream,
			long length, IHttp http) {
		if (!mListenerMap.containsKey(requestId)) {
			return;
		}

		Downloader dl = new Downloader(http, mListenerMap.get(requestId), path);
		boolean bDecompress = true;
		try {
			String encoding = http.getHeaderField("Content-Encoding");
			if (encoding != null && encoding.equals("gzip")) {
				stream = Util.gzipDecompress(stream);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			bDecompress = false;
		}

		if (bDecompress) {
			if (path != null) {
				try {
					saveToFile(path, stream, http.getContentLength(),
							dl);
					mHandler.obtainMessage(TRANSACTION_SUCCESS, dl).sendToTarget();
				} catch (IOException e) {
					mHandler.obtainMessage(TRANSACTION_ERROR, dl)
							.sendToTarget();
					e.printStackTrace();
				}
			}
		} else {
			mHandler.obtainMessage(TRANSACTION_ERROR, dl).sendToTarget();
		}
		
		mListenerMap.remove(requestId);
	}
	
	public void onProcess(int requestId, int curr, int max){
//		Log.e("process", "curr:" + curr + "   " + "max:" + max);
//		if (!mListenerMap.containsKey(requestId)) {
//			return;
//		}
//		Downloader dl = new Downloader(null, mListenerMap.get(requestId), null);
//		mHandler.obtainMessage(TRANSACTION_PROCESS, curr, max, dl).sendToTarget();
	}

	private static final int TRANSACTION_SUCCESS = 0x1;
	private static final int TRANSACTION_ERROR = 0x2;
	private static final int TRANSACTION_PROCESS = 0x3;
	private class InternalHandler extends Handler {
		public InternalHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case TRANSACTION_ERROR:{
				Downloader dl = (Downloader) msg.obj;
				dl.listener.onError(dl.http);
			}
				break;
			case TRANSACTION_PROCESS:{
				Downloader dl = (Downloader) msg.obj;
				dl.listener.onProcess(msg.arg1, msg.arg2);
				break;
			}
			case TRANSACTION_SUCCESS:{
				Downloader dl = (Downloader) msg.obj;
				dl.listener.onSuccess(dl.http, dl.path);
			}
				break;
			}
			super.handleMessage(msg);
		}
	}
	
	public interface IDownLoadListener{
		public void onError(IHttp http);
		public void onProcess(int cur, int max);
		public void onSuccess(IHttp http, String path);
	}

	private boolean saveToFile(String path, byte[] data, Downloader dl){
		FileOutputStream fos;
		
		if(dl != null){
			mHandler.obtainMessage(TRANSACTION_PROCESS, 0, data.length, dl).sendToTarget();
		}
		
		try {
			fos = new FileOutputStream(path);
			fos.write(data);
			
			if(dl != null){
				mHandler.obtainMessage(TRANSACTION_PROCESS, data.length, data.length, dl).sendToTarget();
			}
			
			fos.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void saveToFile(String path, InputStream in, long streamLen, Downloader dl){
		FileOutputStream fos;
				
		if(dl != null){
			mHandler.obtainMessage(TRANSACTION_PROCESS, 0, (int) streamLen, dl).sendToTarget();
		}
		
		String file = path;
		
		try {
			byte[] buffer = new byte[1024];
			fos = new FileOutputStream(file);
			
			int readBytes = 0;
			while (true) {
				int size = in.read(buffer);				
				if (size <= 0) {
					break;
				}
				else{
					readBytes += size;
					if(dl != null){
						mHandler.obtainMessage(TRANSACTION_PROCESS, readBytes, (int) streamLen, dl).sendToTarget();
					}
				}
				
				fos.write(buffer, 0, size);
			}
			fos.close();
			in.close();
//			if(idl != null){
//				mHandler.obtainMessage(TRANSACTION_SUCCESS, idl).sendToTarget();
//			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
