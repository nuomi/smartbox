package com.demonzym.framework.net.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.demonzym.framework.net.http.IHttp.ProcessCallBack;

public class HttpEngine extends Service{
	
	/**
	 * Net �������ֵ.
	 */
	public static final int NET_CODE = 6000;	
	// ���簲ȫ�쳣����
	public static final int ERR_NETWORK_SECURITY = NET_CODE + 1;
	// �����޷�����
	public static final int ERR_NETWORK_DONT_CONNECT = NET_CODE + 2;
	// �����������
	public static final int ERR_NETWORK_OTHER = NET_CODE + 3;
	// ��������ȡ��	
	public static final int ERR_NETWORK_CANCEL = NET_CODE + 4;
	
	private static final int MAX_RETRY_COUNT = 2; // �����������
	private static final int NET_BUFFER = 1 * 1024; // ��ݽ���buff
	private static final int NET_TIMEOUT = 1 * 20 * 1000; //1���ӳ�ʱ
	
	
	private static HttpEngine mInstance;
	public static HttpEngine getInstance(){
		if(mInstance == null)
			return new HttpEngine();
		return mInstance;
	}
	
	private final String TAG = "HttpEngin";
	
	private ExecutorService executorService;
	private final int POOL_SIZE = 1;      //2���̲߳�������
	
	private static int mReqeustNum = 0;
	
	private IHttp mRunningHttp;
	
	public HttpEngine(){
		Log.d("HttpEngine","Http Engine start");

        //��ȡ��ǰϵͳ��CPU ��Ŀ 
		int cpuNums = Runtime.getRuntime().availableProcessors();
        //ExecutorServiceͨ�����ϵͳ��Դ��������̳߳ش�С 	
		executorService = Executors.newFixedThreadPool(cpuNums * POOL_SIZE);
	}
	
	public void addRequest(HttpRequest task){
		if(task == null)
			return;
		
		executorService.submit(new HttpThread(task));
	}
	
	public void stop() {
		executorService.shutdown();
	}
	
	public boolean isStop() {
		return executorService.isShutdown();
	}

	private class HttpThread implements Runnable{
		
		private final String TAG = "HttpThread";
		public HttpRequest request;

		public HttpThread(HttpRequest hr){
			if(hr == null){
				throw new IllegalStateException("task must be not null");
			}
			request = hr;
		}
		
		public void run() {
			Log.i(TAG, "open url:" + request.mUrl);
			
			int times = 0;
			
			while (times < MAX_RETRY_COUNT && !request.isCancel())
			{
				long start = System.currentTimeMillis();
				try
				{
					int code = doHttpReqeust(request);
					if(code != -1){
						break;
					}
				} catch (IOException e)
				{
					e.printStackTrace();
					Log.e("[Error]HttpThread","HttpThread Catch IOException : "
							+ e.toString());	
					
					if(System.currentTimeMillis() - start >= NET_TIMEOUT){
						notifyError(request, ERR_NETWORK_DONT_CONNECT,
								null,null);
						break;
					}
				}catch (SecurityException e)
				{
					e.printStackTrace();
					Log.e("[Error]HttpThread","HttpThread Catch SecurityException : "
							+ e.toString());
					notifyError(request, ERR_NETWORK_SECURITY,
							null,null);
					break;
					
				}  catch (Exception e)
				{
					e.printStackTrace();
					Log.e("[Error]HttpThread","HttpThread Catch Exception : "
							+ e.toString());	
					notifyError(request, ERR_NETWORK_OTHER, null,null);
					break;
				}
				times++;
			}
			
			if(isCancel(request))		//ȡ��
			{
				notifyError(request, ERR_NETWORK_CANCEL, null,null);
			}else if(times >= MAX_RETRY_COUNT){	//����ʧ��
				notifyError(request, ERR_NETWORK_DONT_CONNECT,
						null,null);
			}
		
		}
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static synchronized int getNextRequestID()
	{
		return mReqeustNum++;
	}
	
	/**
	 * 
	 * @param request
	 * @return	�����ResponseCodeֵ, ��Ҫ���Է���-1.
	 * @throws IOException
	 */
	private int doHttpReqeust(HttpRequest request) throws IOException{
		
		try
		{
			//ִ��ǰԤ����, ������Ҫ����Oauth���
//			request.beforeExec();
			trySend(request);
			//ִ�к���, ������Ҫ����Oauth���
//			boolean handled = request.afterExec(mRunningHttp);
			
//			if(handled){
//				request.beforeExec();
//				trySend(request);
//			}
			
			int reponseCode = mRunningHttp.getResponseCode();
			if(isCancel(request))
			{
				return reponseCode;
				
			//}else if (reponseCode == 200){
			}else{
				String type = mRunningHttp.getHeaderField("Content-Type");
				if (type != null
						&& type.indexOf("vnd.wap.wml") >= 0)
				{
					//�ƶ�ҳ��
					Log.d("HttpEngine", "Content-Type:" + type);
					return -1;//����
				}else{
					if (request.isStreamCallBack() && reponseCode == 200) 
					{
						InputStream in = null;
						try {
							in = mRunningHttp.openInputStream();
							notifyReceived(request, in, mRunningHttp.getContentLength(), mRunningHttp);
						}catch(IOException e){
							
						}
//						finally {
//							if (in != null) {
//								in.close();
//								in = null;
//							}
//						}
					} else {
						if(reponseCode !=  200){
							Log.e("response code", "" + reponseCode);
							notifyError(request, reponseCode, null, mRunningHttp);
						}
						byte[] data = readData(mRunningHttp, request);
						if(data != null && !isCancel(request)){
							
							if(reponseCode == 200){
//								Log.e("data", "" + new String(data));
								notifyReceived(request, data, mRunningHttp);	
							}else{
								notifyError(request, reponseCode, data, mRunningHttp);
							}
						}
					}
				}
			}
			
			return reponseCode;
		} finally 
		{
			closeRunningHttp();
		}
	}
	
	private void closeRunningHttp() {
		if(mRunningHttp != null){
			try {
				mRunningHttp.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mRunningHttp = null;
		}
	}
	
	/**
	 * ���Է������
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	private IHttp trySend(final HttpRequest request) throws IOException
	{
		String url = request.getUrl().trim();
		if (!url.startsWith("http://") && !url.startsWith("https://"))
		{
			url = "http://" + url;
		}
		
		mRunningHttp = PalHttp.createHttp(url, request.getMethod());
		mRunningHttp.setTimeout(NET_TIMEOUT / MAX_RETRY_COUNT);
		try
		{
			Hashtable header = request.getHeaderField();
			if (header != null && header.size() > 0)
			{
				Enumeration enu = header.keys();
				while (enu.hasMoreElements())
				{
					String key = (String) enu.nextElement();
					String value = (String) header.get(key);
					mRunningHttp.setRequestProperty(key, value);
				}
			}
			
			if(request.getMethod().equals(HttpRequest.METHOD_POST)){
				
				if(request.getPostData() != null){
					mRunningHttp.postByteArray(request.getPostData());
				}
				if(request.getMultiPartFile() != null){
					String file = request.getMultiPartFile();
					Hashtable t = request.getMultiPartParams();
					mRunningHttp.postMultiPart(file, t);
				}
			}
			
			mRunningHttp.setHttpParams(request.getHttpParams());
			mRunningHttp.setProcessCallBack(new ProcessCallBack() {
				
				public void onProcess(int curr, int max) {
					request.mCallBack.onProcess(request.getRequestID(), curr, max);
				}
			});
			mRunningHttp.execute();
			
		} catch (SecurityException e)
		{
			mRunningHttp.close();
			mRunningHttp = null;
			throw e;
		} catch (IOException e)
		{
			e.printStackTrace();
			mRunningHttp.close();
			mRunningHttp = null;
			Log.e("[Error]HttpThread", "HttpThread trySend IOException");
			throw e;
		}
		
		
		return mRunningHttp;
	}

	/**
	 * ��ȡResonpse���
	 * 
	 * @param http
	 * @param reqeust
	 * @return
	 * @throws IOException
	 */
	private byte[] readData(IHttp http,HttpRequest request) throws IOException
	{
		InputStream inStream = null;
		try
		{
			inStream = http.openInputStream();
			if(inStream==null){
				return null;
			}
			ByteArrayOutputStream buff = new ByteArrayOutputStream();
			
			if (http.getHeaderField("Content-Length") != null)
			{	
				int len = (int)http.getContentLength();
				if(request.getHttpCallBack() != null){
					request.getHttpCallBack().onDataLenReceived(request.getRequestID(), len, true);
				}
				readData(request, inStream, buff, len);
				return buff.toByteArray();
				
			} else
			{
				int numread = 0;
				long timeout = System.currentTimeMillis();
				byte[] tmp = new byte[NET_BUFFER];
				
				while (!isCancel(request))
				{
					numread = inStream.read(tmp, 0, tmp.length);
					long t = System.currentTimeMillis();
					if (numread < 0) {
						break;
					} else if (numread == 0) {
						if (t - timeout > 5000) {
							break;
						}
						
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						buff.write(tmp, 0, numread);
						timeout = t;
					}
				}
				
				return buff.toByteArray();
			}
			
		} finally{
			if(inStream != null){
				inStream.close();
				inStream = null;
			}
		}
	}

	private void readData(HttpRequest request, InputStream in, OutputStream out, int preferLength) throws IOException {
		int numread = 0;
		int count = 0;
		byte[] data = new byte[1024];
		while (!isCancel(request) && count < preferLength)
		{
			numread = in.read(data, 0, Math.min(data.length, preferLength - count));
			if(request.getHttpCallBack() != null){
				request.getHttpCallBack().onDataLenReceived(request.getRequestID(), numread, false);
			}
			
			if (numread == -1)
			{ // ��ݳ��� ��size ��С��ƥ��.
				throw new IOException();
			} else
			{
				out.write(data, 0, numread);
				count += numread;
			}
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * �����Ƿ��ж�
	 * 
	 * @param reqeust
	 * @return
	 */
	private boolean isCancel(HttpRequest request)
	{
		return request.isCancel();
	}
	
	/**
	 * http����.��ʽ��ݽ���.
	 * 
	 * @param request
	 * @param data
	 * @param size
	 */
	private void notifyReceived(HttpRequest request, byte[] data, IHttp http)
	{
		if(request.getHttpCallBack() != null)
		{
			request.getHttpCallBack().onReceived(request.getRequestID(), data, http);
		}
	}
	
	private void notifyReceived(HttpRequest request, InputStream stream, long contentLength, IHttp http)
	{
		if(request.getHttpCallBack() != null)
		{
			request.getHttpCallBack().onReceived(request.getRequestID(), stream, contentLength, http);
		}
	}

	/**
	 * http�������. �������Ϣ�ص���������
	 * 
	 * @param request
	 * @param err
	 */
	private void notifyError(HttpRequest request, int errCode, byte[] msg, IHttp http)
	{
		if(request.getHttpCallBack() != null)
		{
			request.getHttpCallBack().onError(request.getRequestID(), errCode, msg,http);
		}
	}
}
