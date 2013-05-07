/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.demonzym.smartbox.androidmediaplayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.demonzym.framework.dialog.MyDialog;
import com.demonzym.framework.net.http.HttpResponse;
import com.demonzym.framework.net.http.IHttp;
import com.demonzym.framework.net.http.HttpListener.IHttpListener;
import com.demonzym.framework.util.Util;
import com.demonzym.smartbox.R;

import com.demonzym.smartbox.activity.SearchActivity.PopAdapter;
import com.demonzym.smartbox.activity.SearchActivity.PopAdapter.ViewHolder;
import com.demonzym.smartbox.androidmediaplayer.MediaController.IListListener;
import com.demonzym.smartbox.androidmediaplayer.MediaController.IScaleListener;
import com.demonzym.smartbox.api.Api;
import com.demonzym.smartbox.data.Foobar;
import com.demonzym.smartbox.data.TvUrlData;
import com.demonzym.smartbox.data.XMLReader;
import com.demonzym.smartbox.protocal.ConstValues;
import com.demonzym.framework.dialog.*;
import com.qipo.bean.Channel;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.preference.Preference.BaseSavedState;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AndroidTVActivity extends Activity implements OnCompletionListener,
		IScaleListener, IListListener, OnBufferingUpdateListener, OnErrorListener {

	private String mUrl;
	private VideoView mVideoView;
	private LinearLayout mTvLayout;
	// private VideoView mVideoView1;

	// private boolean whichplayer = true; // true mVideoView false mVideoView1

	private LinkedList<String> mFoobarList = new LinkedList<String>(); // һ�������ÿһ��

	private int index = 0;	
	private MediaController mMediaController;

	ArrayList<BaseSavedState> al = new ArrayList<BaseSavedState>();
	private ArrayList<Foobar> foobarlist = new ArrayList<Foobar>(); // ÿһ��

	private LayoutInflater mInflater;
	
	//关于直播的变量
	private boolean livetv;  //是否电视直播
	private int indexTv=0;
	String path=null;
	private ArrayList<Channel> list = new ArrayList<Channel>();
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// ���ó�ȫ��ģʽ
		// getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		// getWindow().getDecorView().setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
//		getWindow().getDecorView().setSystemUiVisibility(
//				View.SYSTEM_UI_FLAG_LOW_PROFILE);

		waitingSomething(R.string.wait_somethings);
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		livetv = getIntent().getBooleanExtra("livetv", false);		
		if(!livetv){
			mUrl = getIntent().getAction();

			Bundle bundle = getIntent().getBundleExtra("tvlist");
			if (bundle != null)
				foobarlist = (ArrayList<Foobar>) bundle.getSerializable("tvlist");
		}else
		{
			
			Bundle b=getIntent().getExtras();
			indexTv=b.getInt("Index");
			list = (ArrayList<Channel>) b.getSerializable("tvlist");
			Log.w("tvlist", list.size()+"");
		}
		
		
//		if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this,
//				"com.demonzym.smartbox.activity.TVActivity",
//				R.string.init_decoders, R.raw.libarm))
//			return;

		//waitingSomething(R.string.wait_somethings);

		setContentView(R.layout.android_videoview);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// ǿ��Ϊ����

		mVideoView = (VideoView) findViewById(R.id.surface_view);
		// mVideoView1 = (VideoView) findViewById(R.id.surface_view1);
		mTvLayout = (LinearLayout) findViewById(R.id.tv_layout);


		mVideoView.setOnBufferingUpdateListener(this);
		mVideoView.setOnErrorListener(this);
		//xlhadd
		mVideoView.setBoolean(livetv);
		mVideoView.setHandler(handler);
		mVideoView.setOnPreparedListener(new OnPreparedListener() {
			public void onPrepared(MediaPlayer mp) {
				mp.start();
				stopWaiting();				//xlhadd
//				mVideoView.setSubShown(true);
			}
		});
		
		mMediaController = new MediaController(this);
		//xlhadd
		mMediaController.setBoolean(livetv);
		mMediaController.setHandler(handler);
		//
		mVideoView.setOnCompletionListener(this);
		mMediaController.setOnScaleListener(this);
		mMediaController.setOnListListener(this);
		
		
		if (!livetv) {
			Api.getMovieFoobar(mUrl, new IHttpListener() {
				public void onSuccess(int requestId, String data, IHttp http) {
					//stopWaiting();

					TvUrlData mud = XMLReader.getTvUrlFromXml(data);	//Dom解析XML xlh
					if(mud == null){
						//Toast.makeText(AndroidTVActivity.this, R.string.data_error_try_again, Toast.LENGTH_SHORT).show();
						toastsomethings(R.string.data_error_try_again);
						return;
					}

					mFoobarList = mud.getFoobarList();

					index = 0;

					if (!mFoobarList.isEmpty()) {
						Log.e("tv url", mFoobarList.get(0));
						start();		//非直播启动播放
						// if(mFoobarList.size() > 1)
						// mVideoView1.setVideoURI(Uri.parse(mFoobarList.get(++index)));
					} else {
						Toast.makeText(AndroidTVActivity.this, R.string.error_for_init_data,
								Toast.LENGTH_SHORT).show();
						finish();
					}
				}

				public void onError(int requestId, int errorCode, String error,
						IHttp http) {

					stopWaiting();

					Toast.makeText(AndroidTVActivity.this, R.string.data_error_try_again, Toast.LENGTH_SHORT)
							.show();
					
					finish();
				}

				public void onSuccess(int requestId, HttpResponse hr) {
					// TODO Auto-generated method stub
					
				}
			});
		} 
		else {
			beginFirstPlay();

		}

	}
	public void beginFirstPlay(){		
		handler.sendEmptyMessage(ConstValues.TV_start1);		

	//	mVideoView.setMediaController(mMediaController);
		mVideoView.requestFocus();
		//mMediaController.show();
	}
	
	
	public void startTv(String path){
		
		if(Util.isStringEmpty(path))	//getTvNextUri()
			finish();
		mVideoView.setVideoURI(Uri.parse(path));

	}
	
	Handler handler=new Handler(){
		public void handleMessage(Message m){
			switch (m.what) {
			case ConstValues.TV_start1:
				handler.removeMessages(ConstValues.TV_start1); //xlhadd
				path=ConstValues.tv_server+list.get(indexTv).getUrl()+"&tn=1";
				startTv(path);
				handler.sendEmptyMessageDelayed(ConstValues.TV_start2, 10000);
				break;
			case ConstValues.TV_start2:
				handler.removeMessages(ConstValues.TV_start2); //xlhadd
				if(!mVideoView.isPlaying()){
					path=ConstValues.tv_server+list.get(indexTv).getUrl()+"&tn=2";
					startTv(path);
					handler.sendEmptyMessageDelayed(ConstValues.TV_start3, 10000);
				}

				break;
			case ConstValues.TV_start3:
				handler.removeMessages(ConstValues.TV_start3); //xlhadd
				if(!mVideoView.isPlaying()){
					path=ConstValues.tv_server+list.get(indexTv).getUrl()+"&tn=3";
					startTv(path);
					handler.sendEmptyMessageDelayed(ConstValues.TV_next, 10000);
				}			
				
				break;
			case ConstValues.TV_play_next:
				handler.removeMessages(ConstValues.TV_play_next); //xlhadd
				if(!AndroidTVActivity.this.isFinishing()){
					if(!mVideoView.isPlaying()){
						waitingSomething(R.string.tv_play_next);
						if(indexTv==list.size()){
							indexTv=0;			
						}else{
							indexTv++;							
						}
						handler.sendEmptyMessage(ConstValues.TV_start1);
						//stopWaiting();
					}					
				}
				break;
			case ConstValues.TV_next:
				handler.removeMessages(ConstValues.TV_next); //xlhadd
				if(!AndroidTVActivity.this.isFinishing()){					
						waitingSomething(R.string.tv_next);
						if(indexTv==list.size()-1){
							indexTv=0;
							
						}else{
							indexTv++;
							
						}
						handler.sendEmptyMessage(ConstValues.TV_start1);
						//stopWaiting();
										
				}
				break;
			case ConstValues.TV_last:
				handler.removeMessages(ConstValues.TV_last); //xlhadd
				if(!AndroidTVActivity.this.isFinishing()){					
					waitingSomething(R.string.tv_last);
					if(indexTv==0){
						indexTv=list.size()-1;		
						
					}else{
						indexTv--;
						
					}
					handler.sendEmptyMessage(ConstValues.TV_start1);
					//stopWaiting();
									
			}
				break;
				
			case ConstValues.TV_exit:
				handler.removeMessages(ConstValues.TV_exit);//xlhadd
				showDialog();
				break;

			default:
				break;
			}
			
		}
	};
	
	

	private void start() {
		if (index == mFoobarList.size())
			return;
		// if (whichplayer) {
		// Log.e("", "player");
		startPlayer();

	}
	
	private void startPlayer() {
		String url = "";

		if (index < mFoobarList.size()) {
			url = mFoobarList.get(index);
			if(Util.isStringEmpty(url)){
				toastsomethings(R.string.error_for_get_data);
				finish();
			}
				
			mVideoView.setVideoURI(Uri.parse(url));
		}
//		mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);
/*		mVideoView.setOnPreparedListener(new OnPreparedListener() {
			public void onPrepared(MediaPlayer mp) {
				mp.start();
//				mVideoView.setSubShown(true);
			}
		});*/
		mVideoView.setMediaController(mMediaController);
		mVideoView.requestFocus();
		
		//mMediaController.show();				
	}

	private int mLayout = VideoView.VIDEO_LAYOUT_ORIGIN;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
//		if (mVideoView != null)
//			mVideoView.setVideoLayout(mLayout, 0);
		super.onConfigurationChanged(newConfig);
	}

	private ProgressDialog mWaitingProgress;

	protected void waitingSomething(String message) {
		waitingSomething(null, message);
	}
	protected void waitingSomething(int Id) {
		waitingSomething(null, getResources().getString(Id));
	}

	protected void waitingSomething(String title, String message) {
		if (mWaitingProgress != null)
			return;

		mWaitingProgress = MyDialog.showProgressDialog(this, title, message,
				true, true);
//		mWaitingProgress.setCancelable(false);
	}

	protected void stopWaiting() {
		if (mWaitingProgress != null) {
			mWaitingProgress.dismiss();
			mWaitingProgress = null;
		}
	}

//	public void onEnd() {
//		// if(mFoobarList != null && mFoobarList.size() > index){
//
//		index++;
//		start();
//		// }
//
//	}

	public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
		// Log.e("onBufferingUpdate", " " + arg1);		//xlhadd 解决点播控制条不自动隐藏
		/*if (arg1 < 99)
			mMediaController.show();*/
	}

	public void onScale(int type) {
		mLayout = type;
//		mVideoView.setVideoLayout(type, 0);
		mMediaController.setScaleButtonFocus();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		getWindow().getDecorView().setSystemUiVisibility(
//				View.SYSTEM_UI_FLAG_LOW_PROFILE);
		return super.onTouchEvent(event);
	}
	
	PopupWindow pop;

	public void onList() {
		if(livetv)
			return;
		ListView listview = new ListView(this);
		PopAdapter sa = new PopAdapter();
		sa.setData(foobarlist);
		listview.setAdapter(sa);
		listview.setBackgroundResource(R.drawable.video_list_bg);
		pop = new PopupWindow(listview, LayoutParams.WRAP_CONTENT,
				LayoutParams.FILL_PARENT);
		pop.setTouchable(true);
		pop.setFocusable(true);
		pop.setBackgroundDrawable(new BitmapDrawable());
		listview.requestFocus();
		pop.showAtLocation(getWindow().getDecorView(), Gravity.RIGHT | Gravity.CENTER_VERTICAL,
				0, 0);
		listview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (pop != null) {
					pop.dismiss();
					pop = null;
				}
				waitingSomething("正在获取数据，请稍等...");
				Api.getMovieFoobar(foobarlist.get(arg2).getUrl(),
						new IHttpListener() {

							public void onSuccess(int requestId, String data,
									IHttp http) {

								stopWaiting();

								TvUrlData mud = XMLReader.getTvUrlFromXml(data);
								if(mud == null){
									//Toast.makeText(AndroidTVActivity.this, "数据错误，请重试！", Toast.LENGTH_SHORT).show();
									toastsomethings(R.string.data_error_try_again);
									return;
								}

								mFoobarList = mud.getFoobarList();

								index = 0;

								if (!mFoobarList.isEmpty()) {
									Log.e("tv url", mFoobarList.get(0));
									start();
									// if(mFoobarList.size() > 1)
									// mVideoView1.setVideoURI(Uri.parse(mFoobarList.get(++index)));
								} else {
									toastsomethings(R.string.data_error_try_again);
								}
							}

							public void onError(int requestId, int errorCode,
									String error, IHttp http) {

								stopWaiting();

								Toast.makeText(AndroidTVActivity.this, "error",
										Toast.LENGTH_SHORT).show();
							}

							public void onSuccess(int requestId, HttpResponse hr) {
								// TODO Auto-generated method stub
								
							}
						});
			}
		});
	}

	public class PopAdapter extends BaseAdapter {

		List<Foobar> data;

		public void setData(List<Foobar> foobarlist) {
			data = foobarlist;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		public Foobar getItem(int arg0) {
			// TODO Auto-generated method stub
			return data.get(arg0);
		}

		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.tuijian_item, null);
				holder = new ViewHolder();
				holder.iv = (ImageView) convertView.findViewById(R.id.img);
				holder.tv = (TextView) convertView.findViewById(R.id.name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.tv.setText(getItem(position).getName());
			holder.iv.setVisibility(View.GONE);
			return convertView;
		}

		public class ViewHolder {
			public ImageView iv;
			public TextView tv;
		}

	}

	public static final int PLAYER_ERROR = 1; 
	public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
		if(arg1 == 1 && arg2 == -5){
			setResult(PLAYER_ERROR);
			//Toast.makeText(this, "不支持当前视频格式，请尝试其他线路", Toast.LENGTH_SHORT).show();
			toastsomethings(R.string.Current_video_format_is_not_supported);
			finish();
		}
		return true;
	}
	
	@Override
	public void onBackPressed() {
		showDialog();
	}
	public void showDialog(){
		MyDialog.showAlertDialog(this, "你确定要退出？", "", 0, "取消", null, "确定", new OnClickListener() {			
			public void onClick(DialogInterface arg0, int arg1) {
				finish();
			}
		});
	}

	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub

		index++;
		start();
	}
	public void toastsomethings(int Id){
		Toast.makeText(this, getResources().getString(Id), Toast.LENGTH_SHORT).show();
	}

/*	Dialog dialog = null;
	private void showDialog() {
		MyAlertDialog.Builder customBuilder = new MyAlertDialog.Builder(
				AndroidTVActivity.this);
		customBuilder
				.setTitle(
						AndroidTVActivity.this.getResources().getString(
								R.string.dialog_message_exit))
				.setNegativeButton(new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).setPositiveButton(new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mVideoView.onStop();
						finish();
						//insertHistory();
					}
				});
		dialog = customBuilder.create();		
		dialog.show();
	}	*/
	
}
