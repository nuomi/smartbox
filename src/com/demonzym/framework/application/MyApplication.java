package com.demonzym.framework.application;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.demonzym.smartbox.api.Api;

/**
 * 
 * com.demonzym.framework���£���һЩ�ҷ�װ�ĳ��õĶ������������Ķ����Ͷ���ע���ˣ�Ӧ�ö����ö���
 * 
 * ����ȫ�ֵ�application������һЩ��ȡ����context����service�������ͨ����̬������ȡ�������context
 * @author Administrator
 *
 */
public class MyApplication extends Application{
private final String TAG = "MyApplication";
	
	private static MyApplication sApp;
	
	static public MyApplication getAppInstance(){
		if(sApp == null)
			throw new NullPointerException("app not create or be terminated!");
		return sApp;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();		
		 
		sApp = this;
		appIni();
		
		//������ע�Ϳ��ŵĻ��ܲ���ȫ���쳣
//		CrashHandler crashHandler = CrashHandler.getInstance();  
        // ע��crashHandler  
//        crashHandler.init(getApplicationContext());  
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	
	public Context getAppContext(){
		return getApplicationContext();
	}
	
	private void appIni(){
//		Api.getHDP_URL();
	}
	
	public void finishApp(){
		new Handler().postDelayed(new Runnable() {			
			public void run() {
				KillApplication.killApplicationByPackageName(MyApplication.this, getPackageName());
			}
		}, 1000);

	}
}
