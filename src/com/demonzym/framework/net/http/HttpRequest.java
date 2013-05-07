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
	
	/* ���¶���post��ʽ������ͬ�� ���ݺ��ļ� */
	byte[] mPostByteArray;	
	String mMultiPartFile;
	
	private String mPath;   //�����ļ�ʱ�õ�
	
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
	 * ���ͷ�ֶ�
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
	 * ���http params
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
	 * ����ʱ�õ�
	 * @param path
	 */
	public void setPath(String path){
		mPath = path;
	}
	public String getPath(){
		return mPath;
	}
	
	//����ϴ�ͼƬ���ϴ�ͷ�������һ��������
	private Hashtable/* <String, String> */mMultiPartParams;
	public void setMultiPartParams(String key, String value) {
		if (mMultiPartParams == null)
			mMultiPartParams = new Hashtable/* <String, String> */();

		mMultiPartParams.put(key, value);
	}
	public Hashtable getMultiPartParams() {
		return mMultiPartParams;
	}
		
	
	//before��after  ����oath��
	public void beforeExec(){		
	}
	
	/**
	 * 
	 * @return true ��ʾ�ѱ�����
	 */
	public boolean afterExec(IHttp http){
		return false;
	}
}
