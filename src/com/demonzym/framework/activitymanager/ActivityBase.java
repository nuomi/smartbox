package com.demonzym.framework.activitymanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.demonzym.framework.application.MyApplication;
import com.demonzym.framework.dialog.MyDialog;
import com.demonzym.smartbox.R;
import com.demonzym.smartbox.activity.CartoonGridActivity;
import com.demonzym.smartbox.activity.ComicGridActivity;
import com.demonzym.smartbox.activity.FilmGridActivity;
import com.demonzym.smartbox.activity.LiveTvActivity;
import com.demonzym.smartbox.activity.MovieDetailActivity;
import com.demonzym.smartbox.activity.RecommendActivity;
import com.demonzym.smartbox.activity.SearchActivity;
import com.demonzym.smartbox.activity.TvGridActivity;
import com.demonzym.smartbox.activity.UserInfoActivity;
import com.qipo.activity.TvActivity;

/** �̳и��༴��ʵ����Activity�Ĺ��� */
//��װ��һЩͨ�õķ���
public abstract class ActivityBase extends Activity{
	
	private final String TAG = "ActivityBase";
	
	public static Handler mHandler = new Handler();
		
	private ActivityBase requestSubActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// ���ó�ȫ��ģʽ
//		getWindow().getDecorView().setSystemUiVisibility(
//				View.SYSTEM_UI_FLAG_LOW_PROFILE);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		

//		getWindow().getDecorView().setSystemUiVisibility(
//				View.SYSTEM_UI_FLAG_LOW_PROFILE);
	}
	
	public ActivityBase getRequestSubActivity() {
		return requestSubActivity;
	}

	public void setRequestSubActivity(ActivityBase requestSubActivity) {
		this.requestSubActivity = requestSubActivity;
	}

	private Class getTargetClass(Intent intent){
		Class clazz = null;
		try {
			if(intent.getComponent() != null)
			clazz = Class.forName(intent.getComponent().getClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return clazz;
	}
	
	// ��д��startActivity()������
	// ������ø÷���ʱ����Ŀ��Activity�Ƿ�����Activity����ò�ͬ�ķ���
	@Override
	public void startActivity(Intent intent) {
		if( getTargetClass(intent) != null && ActivityBase.class.isAssignableFrom(getTargetClass(intent)) ){
			if(this.getParent() instanceof ActivityBaseGroup){
				intent.putExtra("fromSubActivity", getClass().getName());
				((ActivityBaseGroup)this.getParent()).launchNewActivity(intent);
			}
		}else{
			super.startActivity(intent);
		}
	}

	// ��д��startActivityForResult()������
	// ������ø÷���ʱ����Ŀ��Activity�Ƿ�����Activity����ò�ͬ�ķ���
	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		if( getTargetClass(intent) != null && ActivityBase.class.isAssignableFrom(getTargetClass(intent)) ){
			if(this.getParent() instanceof ActivityBaseGroup){
				intent.putExtra("fromSubActivity", getClass().getName());
				((ActivityBaseGroup) this.getParent())
						.launchNewActivityForResult(this, intent, requestCode);
			}
		}else{
			super.startActivityForResult(intent, requestCode);
		}
	}
		
	/** ���ô˷�����������һ������ */
	public void goback() {
		Class clazz = null;
		try {
			clazz = Class.forName(getIntent().getStringExtra("fromSubActivity"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Intent intent = new Intent(this,clazz);
		((ActivityBaseGroup)this.getParent()).launchActivity(intent);
	}
	
	/** 调用此方法来返回上一个界面并返回数据 */
	public void gobackWithResult(int resultCode, Intent data) {
		Class clazz = null;
		try {
			clazz = Class.forName(getIntent().getStringExtra("fromSubActivity"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		data.setClass(this, clazz);
		if( requestSubActivity != null){
			requestSubActivity.onActivityResult(
					data.getIntExtra("requestCode", 0), resultCode, data);
		}
		((ActivityBaseGroup)this.getParent()).launchActivity(data);	
	}
	
	private ProgressDialog mWaitingProgress;
	protected void waitingSomething(String message){
		waitingSomething(null, message);
	}
	protected void waitingSomething(int id){
		waitingSomething(null, getResources().getString(id));	//xlh
	}
	protected void waitingSomething(String title, String message){
		if(mWaitingProgress != null)
			return;
		
		mWaitingProgress = MyDialog.showProgressDialog(this, title, message, true, true);
//		mWaitingProgress.setCancelable(false);
	}
	protected void stopWaiting(){
		if(mWaitingProgress != null){
			mWaitingProgress.dismiss();
			mWaitingProgress = null;
		}
	}
	
	protected void toastSomething(String message){
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	protected void toastSomething(int Id){
		toastSomething(getResources().getString(Id));
	}
	
	protected void showError(String msg){
		stopWaiting();
		toastSomething(msg);
	}
	protected void showError(){
		stopWaiting();
		toastSomething(getResources().getString(R.string.data_error_try_again));
	}
	
	protected void focusParent(int id){
		getParent().findViewById(id).requestFocus();
	}
	
	//其他界面返回会返回到RecommendActivity界面，再返回就退出应用
	@Override
	public void onBackPressed() {
		
//		Log.e(TAG, "onBackPressed");
		
		if(this instanceof MovieDetailActivity){
			goback();
			return;
		}
		
		if(this instanceof CartoonGridActivity
				|| this instanceof ComicGridActivity
				|| this instanceof FilmGridActivity
				|| this instanceof SearchActivity
				|| this instanceof TvActivity
				|| this instanceof TvGridActivity
				|| this instanceof UserInfoActivity){
			((ActivityBaseGroup)getParent()).checkRadio(R.id.activity_recommend);
			return;
		}
		
		if(this instanceof RecommendActivity){
			MyDialog.showAlertDialog(this, "确定退出？", "退出", 0, "取消", null
					, "确定", new OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							finish();
							MyApplication.getAppInstance().finishApp();	//退出应用
						
				}
			});
			return;
		}

//		goback();
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		
//		Log.e(TAG, "onKeyUp");
		
		return super.onKeyUp(keyCode, event);
	}
}
