package com.demonzym.smartbox.activity;

import com.demonzym.framework.application.MyApplication;
import com.demonzym.framework.dialog.MyDialog;
import com.demonzym.framework.imagemanager.ImageManager;
import com.demonzym.framework.imagemanager.ImageManager.IImageDownloadListener;
import com.demonzym.framework.net.http.HttpResponse;
import com.demonzym.framework.net.http.IHttp;
import com.demonzym.framework.net.http.HttpListener.IHttpListener;
import com.demonzym.framework.util.Util;
import com.demonzym.smartbox.R;
import com.demonzym.smartbox.api.Api;
import com.demonzym.smartbox.data.UserInfo;
import com.demonzym.smartbox.protocal.ConstValues;
import com.demonzym.smartbox.update.UpdateManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ��ӭ
 * @author Administrator
 *
 */
public class WelcomeActivity extends Activity {
	
	private TextView mLogo;
	private UpdateManager up;
	public MyHandler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.welcome);
		

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 窗体全屏

//		getWindow().getDecorView().setSystemUiVisibility(
//				View.SYSTEM_UI_FLAG_LOW_PROFILE);
		
//		waitingSomething("�������ӷ�����������");
		
		mLogo = (TextView)findViewById(R.id.logo);
		
		mLogo.setBackgroundResource(R.drawable.welcome);
		
		handler = new MyHandler();
		Api.getHDP_URL();
		
		Api.getLiveTv();
		
		up = new UpdateManager(WelcomeActivity.this, handler);
		up.checkUpdate();				

		//iniData();

//		Intent intent = new Intent(WelcomeActivity.this, 
//				MainActivity.class);
//		startActivity(intent);
		
		final String account = SettingActivity.getAccount(this);
		final String pwd = SettingActivity.getPwd(this);
		if(!(Util.isStringEmpty(account) || Util.isStringEmpty(pwd))){
			Api.login(account, pwd, new IHttpListener() {					//进入程序是首界面登录申请资源
				
				public void onSuccess(int requestId, String data, IHttp http) {
					Log.e("login", data + "");
//					stopWaiting();
					int i1 = data.indexOf("error");
					if(i1 > -1){
						int i2 = data.indexOf("</error>");
						toastSomething("自动登录失败，" + data.substring(i1 + 6, i2));	//xlh
						return;
					}
					toastSomething(R.string.automatic_login_successful);
					UserInfo.username = account;
					UserInfo.pwd = pwd;
					SettingActivity.setAccount(WelcomeActivity.this, account);
					SettingActivity.setPwd(WelcomeActivity.this, pwd);
					String uid = data.substring(12, data.indexOf("</uid>"));
					UserInfo.uid = uid;
					SettingActivity.setFav(WelcomeActivity.this, data);
				}
				
				public void onError(int requestId, int errorCode, String error, IHttp http) {
					toastSomething(R.string.Logon_failed_try_again);
				}

				public void onSuccess(int requestId, HttpResponse hr) {
					// TODO Auto-generated method stub
					
				}
			});
		}
		
	}
	
	private synchronized void iniData() {

//		waitingSomething("正在获取数据，请稍等。。。");
		
		new Handler().postDelayed(new Runnable() {
			
			public void run() {
				boolean[] b;  //b1是否获取完成，b2是否连接错误
				b = Api.iniOk();
				Log.v("api b", ""+b);
				Log.v("api b[0]", ""+b[0]);
				Log.v("api b[1]", ""+b[1]);				
				if(!b[0]){
					Log.v("api b[0]", ""+b[0]);
					if(!b[1]){
						Log.v("api b[1]", ""+b[1]);
//						Toast.makeText(WelcomeActivity.this, " ", Toast.LENGTH_LONG).show();
//						stopWaiting();
						toastSomething(R.string.anomaly_for_network_link);
						MyApplication.getAppInstance().finishApp();		//退出应用
						finish();
						return;
					}
					iniData();
					return;
				}
				

				Intent intent = new Intent(WelcomeActivity.this, 
						MainActivity.class);						//转移到用户main界面
				startActivity(intent);

				finish();
			}
		}, 5000);		//200 xlh
		
	}
	
	class MyHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 100:
			{
				iniData();
			}
				break;

			default:
				break;
			}

		}
		
	}
	
	
	
//	private ProgressDialog mWaitingProgress;
//	protected void waitingSomething(String message){
//		waitingSomething(null, message);
//	}
//	protected void waitingSomething(String title, String message){
//		if(mWaitingProgress != null)
//			return;
//		
//		mWaitingProgress = MyDialog.showProgressDialog(this, title, message, true, true);
//		mWaitingProgress.setCancelable(false);
//	}
//	protected void stopWaiting(){
//		if(mWaitingProgress != null){
//			mWaitingProgress.dismiss();
//			mWaitingProgress = null;
//		}
//	}
	
	@Override
	public void finish() {

		TranslateAnimation a = new TranslateAnimation(0, -100, 0, 0);
		a.setDuration(1000);
		this.getWindow().getDecorView().startAnimation(a);
		
		super.finish();
	}
	
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			MyDialog.showAlertDialog(this, "确定退出？", "退出", 0, "取消", null
					, "确定", new OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							finish();
							MyApplication.getAppInstance().finishApp();	//退出应用
						
				}
			});
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void toastSomething(String message){
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	protected void toastSomething(int Id){
		toastSomething(getResources().getString(Id));
	}
}
