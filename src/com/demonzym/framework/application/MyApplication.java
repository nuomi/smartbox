package com.demonzym.framework.application;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.demonzym.smartbox.api.Api;

/**
 * 
 * com.demonzym.framework包下，是一些我封装的常用的东西，这个包里的东西就都不注释了，应该都看得懂的
 * 
 * 用于全局的application，方便一些获取不到context的像service里面的类通过静态方法获取到程序的context
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
		
		//下两句注释开放的话能捕获全局异常
//		CrashHandler crashHandler = CrashHandler.getInstance();  
        // 注册crashHandler  
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
