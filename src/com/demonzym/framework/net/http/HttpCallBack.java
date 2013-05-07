package com.demonzym.framework.net.http;

import java.io.InputStream;

public interface HttpCallBack
{
	
	public void onError(int requestId, int errCode, byte[] errStr,IHttp http);
	
	public void onReceived(int requestId, byte[] data, IHttp http);
	
	public void onReceived(int requestId, InputStream stream, long contentLength, IHttp http);
	
	public void onDataLenReceived(int requestId,int len ,boolean isTotalLen);
	
	public void onProcess(int requestId, int curr, int max);
}