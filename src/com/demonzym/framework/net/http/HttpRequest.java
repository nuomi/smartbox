package com.demonzym.framework.net.http;

import java.util.Hashtable;


public class HttpRequest {
	
	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";
	
	public String mMethod;

	int mRequestID;
	boolean mIsCancel = false;

	private Hashtable mHeader;
	private Hashtable mHttpParams;	

	boolean mStreamCallBack;
	
	public String mUrl;
	public HttpCallBack mCallBack;
	
	/* 以下二种post方式，不能同存 数据和文件 */
	byte[] mPostByteArray;	
	String mMultiPartFile;
	
	private String mPath;   //下载文件时用到
	
	public HttpRequest(String url){
		this(url, METHOD_GET);
	}
	
	public HttpRequest(String url, byte[] postData){		
		this(url, METHOD_POST);
		mPostByteArray = postData;
	}
	
	public HttpRequest(String url, String method){
		mMethod = method;
		mUrl = url;
		mRequestID = HttpEngine.getNextRequestID();
		mIsCancel = false;
	}
	
	public String getMethod()
	{
		return mMethod;
	}
	
	public void setUrl(String url){
		mUrl = url;
	}
	
	public String getUrl()
	{
		return mUrl;	
	}
	
	/**
	 * 添加头字段
	 * @param key
	 * @param value
	 */
	public void addHeaderField(String key,String value)
	{
		if(mHeader == null){
			mHeader = new Hashtable();
		}
		mHeader.put(key, value);
	}
	
	/**
	 * set Http Header Field
	 * @param table
	 */
	public void setHeaderField(Hashtable table)
	{
		if(table != null){
			mHeader = table;
		}
	}
	
	
	public Hashtable getHeaderField()
	{
		return mHeader;
	}

	/**
	 * 添加http params
	 * @param name
	 * @param value
	 */
	public void addHttpParams(String name, Object value){
		if(mHttpParams == null)
			mHttpParams = new Hashtable();
		
		mHttpParams.put(name, value);
	}
	
	public Hashtable getHttpParams(){
		return mHttpParams;
	}

	public int getRequestID()
	{
		return mRequestID;
	}
	public void doCancel(){
		mIsCancel = true;	
	}
	
	public boolean isCancel()
	{
		return mIsCancel;
	}
	
	public void setStreamCallBack(boolean streamCallBack) {
		mStreamCallBack = streamCallBack;
	}
	
	public boolean isStreamCallBack() {
		return mStreamCallBack;
	}
	
	public byte[] getPostData()
	{
		return mPostByteArray;
	}
	
	public String getMultiPartFile()
	{
		return mMultiPartFile;
	}
	
	public byte[] getMultiPartData() {
		return mPostByteArray;
	}
	
	public void setHttpCallBack(HttpCallBack callback)
	{
		mCallBack = callback;
	}
	
	public HttpCallBack getHttpCallBack()
	{
		return mCallBack;
	}
	
	/**
	 * 下载时用到
	 * @param path
	 */
	public void setPath(String path){
		mPath = path;
	}
	public String getPath(){
		return mPath;
	}
	
	//解决上传图片和上传头像参数不一样的问题
	private Hashtable/* <String, String> */mMultiPartParams;
	public void setMultiPartParams(String key, String value) {
		if (mMultiPartParams == null)
			mMultiPartParams = new Hashtable/* <String, String> */();

		mMultiPartParams.put(key, value);
	}
	public Hashtable getMultiPartParams() {
		return mMultiPartParams;
	}
		
	
	//before和after  用于oath等
	public void beforeExec(){		
	}
	
	/**
	 * 
	 * @return true 表示已被处理
	 */
	public boolean afterExec(IHttp http){
		return false;
	}
}
