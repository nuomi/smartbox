package com.demonzym.smartbox.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.demonzym.framework.activitymanager.ActivityBase;
import com.demonzym.framework.imagemanager.ImageManager;
import com.demonzym.framework.imagemanager.ImageManager.IImageDownloadListener;
import com.demonzym.framework.net.http.HttpEngine;
import com.demonzym.framework.net.http.HttpListener;
import com.demonzym.framework.net.http.HttpListener.IHttpListener;
import com.demonzym.framework.net.http.HttpRequest;
import com.demonzym.framework.net.http.HttpResponse;
import com.demonzym.framework.net.http.IHttp;
import com.demonzym.framework.util.Util;
import com.demonzym.smartbox.R;
import com.demonzym.smartbox.androidmediaplayer.AndroidTVActivity;
import com.demonzym.smartbox.api.Api;
import com.demonzym.smartbox.data.TvChannel;
import com.demonzym.smartbox.data.TvClass;
import com.demonzym.smartbox.data.TvEpgData;
import com.demonzym.smartbox.data.TvLink;

/**
 * ����
 * @author Administrator
 *
 */
public class LiveTvActivity extends ActivityBase {

	private GridView mTvLiveGrid; 
	private TextView mText;
	private LiveTvAdapter mAdapter;	

	private LayoutInflater mInflater;

	Set<Entry<TvClass, ArrayList<TvChannel>>> set;
	Iterator<Entry<TvClass, ArrayList<TvChannel>>> iterator;

	ArrayList<TvChannel> channelList = new ArrayList<TvChannel>();
	
	private HashMap<View, String> epg_map = new HashMap<View, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.livetv);

		mText = (TextView) findViewById(R.id.textView1);
		mTvLiveGrid = (GridView) findViewById(R.id.gridView1);
		//父类布局，下面添加了子布局
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mAdapter = new LiveTvAdapter();
		mTvLiveGrid.setAdapter(mAdapter);
		mTvLiveGrid.setOnKeyListener(new OnKeyListener() {
			
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT
						&& event.getAction() == KeyEvent.ACTION_DOWN
						&& mTvLiveGrid.getSelectedItemPosition() % 4 == 0){
					focusParent(R.id.activity_livetv);
					return true;
				}
				return false;
			}
		});
		mTvLiveGrid.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, final View arg1,
					int arg2, long arg3) {
				TvChannel tc = (TvChannel) (arg0.getItemAtPosition(arg2));
				String ss = epg_map.get(arg1);
				mText.setText(ss);
				if(Util.isStringEmpty(ss)){			//xlh
					HttpRequest hr = new HttpRequest("http://v.guozitv.com/txt/epg/"
							+ Util.getCurrentTime() + "/" + tc.epg + ".txt");
					HttpListener.getInstance().addHttpListener(hr.getRequestID(), new IHttpListener() {
						
						public void onSuccess(int requestId, HttpResponse hr) {
							try {
								epg_map.put(arg1, TvEpgData.Array2String(TvEpgData.parseTvData(new String(hr.data, "GB2312"))));
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							String s = epg_map.get(arg1);
							mText.setText(s);
						}
						
						public void onError(int requestId, int errorCode, String error, IHttp http) {
							epg_map.put(arg1, error);
						}

						public void onSuccess(int requestId, String data,
								IHttp http) {
							// TODO Auto-generated method stub
							
						}
					});
					hr.setHttpCallBack(HttpListener.getInstance());					
					HttpEngine.getInstance().addRequest(hr);
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		mTvLiveGrid.setOnItemClickListener(new OnItemClickListener() {	//xlh

			public void onItemClick(AdapterView<?> arg0, final View arg1, int arg2,
					long arg3) {
				mCurrentTvChannel = (TvChannel) arg0.getItemAtPosition(arg2);
				if(mCurrentTvChannel.tvlink_list != null && mCurrentTvChannel.tvlink_list.size() > 0){
				TvLink tl = mCurrentTvChannel.tvlink_list.get(0);
				Intent intent = new Intent(LiveTvActivity.this,AndroidTVActivity.class);//TVActivity.startTVActivity(LiveTvActivity.this);
				intent.setAction(tl.link);
				intent.putExtra("livetv", true);
				startActivityForResult(intent, 0);
				mCurrentIndex = 0;
				SettingActivity.addTvChannel(LiveTvActivity.this, mCurrentTvChannel);
				}
				
				
//				TvChannel tc = (TvChannel) (arg0.getItemAtPosition(arg2));
//				String ss = epg_map.get(arg1);
//				if(Util.isStringEmpty(ss)){
//					HttpRequest hr = new HttpRequest("http://v.guozitv.com/txt/epg/"
//							+ Util.getCurrentTime() + "/" + tc.epg + ".txt");
//					HttpListener.getInstance().addHttpListener(hr.getRequestID(), new IHttpListener() {
//						
//						public void onSuccess(int requestId, HttpResponse hr) {
//							try {
//								epg_map.put(arg1, TvEpgData.Array2String(TvEpgData.parseTvData(new String(hr.data, "GB2312"))));
//							} catch (UnsupportedEncodingException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//							String s = epg_map.get(arg1);
//							mText.setText(s);
//						}
//						
//						public void onError(int requestId, int errorCode, String error, IHttp http) {
//							epg_map.put(arg1, error);
//						}
//
//						public void onSuccess(int requestId, String data,
//								IHttp http) {
//							// TODO Auto-generated method stub
//							
//						}
//					});
//					hr.setHttpCallBack(HttpListener.getInstance());					
//					HttpEngine.getInstance().addRequest(hr);
//				}else
//					mText.setText(ss);
			}
		});

		iniIntent(getIntent());
	}
	
	private TvChannel mCurrentTvChannel;
	private int mCurrentIndex;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 0 && resultCode == 1/*TVActivity.PLAYER_ERROR*/){
			if(mCurrentTvChannel != null){
				if(mCurrentTvChannel.tvlink_list != null && mCurrentTvChannel.tvlink_list.size() > 0){
					if(mCurrentTvChannel.tvlink_list.size() <= ++mCurrentIndex){
						toastSomething(R.string.no_more_lines);
						return;
					}
					toastSomething(R.string.wait_for_automatic_switching_circuit);
					new Handler().postDelayed(new Runnable() {
						
						public void run() {
							TvLink tl = mCurrentTvChannel.tvlink_list.get(mCurrentIndex);
							Intent intent = new Intent(LiveTvActivity.this, AndroidTVActivity.class);
							intent.setAction(tl.link);
							intent.putExtra("livetv", true);
							startActivityForResult(intent, 0);
						}
					}, 500);
					SettingActivity.addTvChannel(LiveTvActivity.this, mCurrentTvChannel);
					}
			}
		}
	}

	private void iniIntent(Intent intent) {
		waitingSomething("正在获取数据，请稍等。。。");
		new Handler().postDelayed(new Runnable() {
			
			public void run() {
				stopWaiting();
				mTvLiveGrid.requestFocus();
			}
		}, 300);
		set = Api.mTvLiveDataMap.entrySet();
		iterator = set.iterator();
		channelList.clear();
		if (intent != null) {
			String name = intent.getAction();
			if (!Util.isStringEmpty(name)) {
				if(name.equals("常看频道")){
					channelList = SettingActivity.getTvChannelList(this);
					mAdapter = new LiveTvAdapter();
					mTvLiveGrid.setAdapter(mAdapter);
					mTvLiveGrid.requestFocus();
					return;
				}
				while (iterator.hasNext()) {
					Entry<TvClass, ArrayList<TvChannel>> entry = iterator.next();
					if (entry.getKey().classname.equals(name)) {
						channelList = entry.getValue();
						mAdapter = new LiveTvAdapter();
						mTvLiveGrid.setAdapter(mAdapter);
						mTvLiveGrid.requestFocus();
						return;
					}
				}
			}
			while (iterator.hasNext()) {
				channelList.addAll(iterator.next().getValue());
			}
			mAdapter = new LiveTvAdapter();
			mTvLiveGrid.setAdapter(mAdapter);
			mTvLiveGrid.requestFocus();
			mTvLiveGrid.setSelection(0);
			return;
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);

		iniIntent(intent);
	}

	class LiveTvAdapter extends BaseAdapter {

		public int getCount() {
			// TODO Auto-generated method stub
			return channelList.size();
		}

		public TvChannel getItem(int arg0) {
			// TODO Auto-generated method stub
			return channelList.get(arg0);
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

			holder.tv.setText(getItem(position).name);

			final ViewHolder vh = holder;
			ImageManager.getInstance().getImage(getItem(position).img,
					new IImageDownloadListener() {

						public void onGetImageError() {
							// TODO Auto-generated method stub

						}

						public void onGetImage(Bitmap bitmap, String localPath) {
							vh.iv.setImageBitmap(bitmap);
						}

						public void onDownloading(int cur, int max) {
							// TODO Auto-generated method stub

						}
					});
			return convertView;
		}

		class ViewHolder {
			public ImageView iv;
			public TextView tv;
		}

	}

	// public static final class ComparatorValues implements
	// Comparator<TvClass>{
	//
	// public int compare(TvClass object1, TvClass object2) {
	// return Integer.parseInt(object1.id)
	// - Integer.parseInt(object2.id);
	// }
	//
	// }
}
