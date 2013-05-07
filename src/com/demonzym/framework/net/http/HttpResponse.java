package com.demonzym.framework.net.http;

import java.io.InputStream;

public class HttpResponse {
	public HttpResponse(IHttp http2, byte[] data2) {
		http = http2;
		data = data2;
	}
	public HttpResponse(IHttp http2, InputStream is) {
		http = http2;
		inputstream = is;
	}
	public IHttp http;
	public byte[] data;
	public InputStream inputstream;
}
