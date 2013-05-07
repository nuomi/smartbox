package com.qipo.activity;


import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.demonzym.framework.activitymanager.ActivityBase;
import com.demonzym.framework.imagemanager.ImageManager;
import com.demonzym.framework.imagemanager.ImageManager.IImageDownloadListener;
import com.demonzym.framework.net.http.HttpListener.IHttpListener;
import com.demonzym.framework.net.http.HttpResponse;
import com.demonzym.framework.net.http.IHttp;
import com.demonzym.framework.util.Util;
import com.demonzym.smartbox.R;

import com.demonzym.smartbox.androidmediaplayer.AndroidTVActivity;
import com.demonzym.smartbox.api.Api;
import com.demonzym.smartbox.data.Foobar;
import com.demonzym.smartbox.data.MovieListData;
import com.demonzym.smartbox.data.XMLReader;
import com.demonzym.smartbox.protocal.ConstValues;


import com.qipo.bean.Channel;
import com.qipo.bean.TvList;
import com.qipo.bitmapcache.ImageCache;
import com.qipo.bitmapcache.ImageFetcher;
import com.qipo.parser.TvListXmlPullParser;


/**
 * ��Ӱ
 * @author Administrator
 *
 */
public class TvActivity extends ActivityBase{	

	private ImageFetcher mImageFetcher;
	private ImageCache.ImageCacheParams cacheParams = null;
	private static final String IMAGE_CACHE_DIR = "GuozitvIcon";
	private LayoutInflater mInflater;
	
	private int lastChooseId = 0;
	private int tagNum = 0;
	private boolean tagFirst = false;
	private boolean tagLast = false;
	private int first[] = new int[] { 0, 0, 0, 0, 0, 0 };
	private int last[] = new int[] { 3, 5, 5, 5, 5, 5 };
	
	
	private static TvList tvList;
	private ArrayList<Channel> list2 = new ArrayList<Channel>();
	private ArrayList<Channel> list3 = new ArrayList<Channel>();
	private ArrayList<Channel> list4 = new ArrayList<Channel>();
	private ArrayList<Channel> list5 = new ArrayList<Channel>();
	private ArrayList<Channel> list6 = new ArrayList<Channel>();
	private static final int yangshi = 3;
	private static final int weishi = 4;
	private static final int difang = 5;
	private static final int shuzi = 6;
	private static final int other = 33;
	
	private ProgressDialog progressDialog;
	private tvHandler hander;
	private static final int accessData = 0x11;
	private Intent intent = new Intent();
	
	private GridView mGrid;
	private TVAdapter mAdapter;
	private int width2 = 0;
	
	private boolean isLoading = false;
	
	private int mIndex = 1;
	
	//进入电影界面，默认的电影类型，服务端原本有一个全部，现在没了
	private String mData = "央视频道";	////xlh 后面Intent传过来的Action

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
				
		setContentView(R.layout.movie_grid);
		
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels;
		//width1 = width / 4;
		width2 = width / 6;

		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		cacheParams = new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(this, 0.25f); // Set memory cache to
															// 25% of mem class

		hander = new tvHandler();
		progressDialog = ProgressDialog.show(TvActivity.this, this
				.getResources().getString(R.string.progressDialog_title), this
				.getResources().getString(R.string.progressDialog_info));
		
		iniIntent(getIntent());
		
			
		
	}
	
	private void iniIntent(Intent intent) {
		
		if (intent != null) {
			String action = getIntent().getAction();
			if (!Util.isStringEmpty(action))
				mData = action;
			Log.w("mData", action);
		}
		iniData();
		
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		
		iniIntent(intent);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();	
		if(mGrid!=null)
			mGrid.requestFocus();
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub		
		super.onDestroy();
		mImageFetcher.closeCache();
	}

	protected ArrayList<Channel> getTvList(int id){
		
		if(mData.equals("央视频道")){
			return list2;
			
		}else if(mData.equals("各省卫视")){
			return list3;
		}
		else if(mData.equals("地方节目")){
			return list4;
		}
		else if(mData.equals("综合频道")){
			return list5;
		}
		else/*其他*/{
			return list6;
		}

	}
	private void iniViews() {
		LayoutParams params;
		mGrid = (GridView) findViewById(R.id.gridView1);
		
		if(mData.equals("央视频道")){
			mAdapter = new TVAdapter(list2, R.layout.tv_item_layout, width2,
					170);
			params = new LayoutParams(list2.size() * width2,
					LayoutParams.FILL_PARENT);
			mGrid.setLayoutParams(params);
			mGrid.setColumnWidth(width2);
			mGrid.setStretchMode(GridView.NO_STRETCH);
		//	mGrid.setNumColumns(list2.size());
			mGrid.setAdapter(mAdapter);

		}else if(mData.equals("各省卫视")){
			mAdapter = new TVAdapter(list3, R.layout.tv_item_layout, width2,
					170);
			params = new LayoutParams(list3.size() * width2,
					LayoutParams.FILL_PARENT);
			mGrid.setLayoutParams(params);
			mGrid.setColumnWidth(width2);
			mGrid.setStretchMode(GridView.NO_STRETCH);
		//	mGrid.setNumColumns(list3.size());
			mGrid.setAdapter(mAdapter);
		}else if(mData.equals("地方节目")){

			mAdapter = new TVAdapter(list4, R.layout.tv_item_layout, width2,
					170);
			params = new LayoutParams(list4.size() * width2,
					LayoutParams.FILL_PARENT);
			mGrid.setLayoutParams(params);
			mGrid.setColumnWidth(width2);
			mGrid.setStretchMode(GridView.NO_STRETCH);
		//	mGrid.setNumColumns(list4.size());
			mGrid.setAdapter(mAdapter);
		}else if(mData.equals("综合频道")){

			mAdapter = new TVAdapter(list5, R.layout.tv_item_layout, width2,
					170);
			params = new LayoutParams(list5.size() * width2,
					LayoutParams.FILL_PARENT);
			mGrid.setLayoutParams(params);
			mGrid.setColumnWidth(width2);
			mGrid.setStretchMode(GridView.NO_STRETCH);
			//mGrid.setNumColumns(list5.size());
			mGrid.setAdapter(mAdapter);
		}else/*其他*/{

			mAdapter = new TVAdapter(list6, R.layout.tv_item_layout, width2,
					170);
			params = new LayoutParams(list6.size() * width2,
					LayoutParams.FILL_PARENT);
			mGrid.setLayoutParams(params);
			mGrid.setColumnWidth(width2);
			mGrid.setStretchMode(GridView.NO_STRETCH);
		//	mGrid.setNumColumns(list6.size());
			mGrid.setAdapter(mAdapter);		
		}
		
		mGrid.requestFocus();
		mGrid.setSelection(0);

		
		mGrid.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				
				Bundle b = new Bundle();
				b.putInt("Index", arg2);
				b.putSerializable("tvlist", getTvList(arg2));
				
				//intent.setAction();
				intent.putExtra("livetv", true);
				intent.setClass(getApplicationContext(),
						AndroidTVActivity.class);
				intent.putExtras(b);
				startActivity(intent);
				/*
				Bundle b = new Bundle();
				b.putSerializable("tvlist", tvList);
				
				b.putInt("type", getType(arg2));

//				if (arg2 == 10) {
//					intent.setClass(getApplicationContext(),
//							TvChannelMore.class);
//				} else 
				{
					b.putInt("position", arg2);
					intent.setClass(getApplicationContext(),
							TvLoadingViewActivity.class);
				}
				intent.putExtras(b);
				startActivity(intent);
			*/}
		});

		
		mGrid.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {

				if(arg1 == KeyEvent.KEYCODE_DPAD_LEFT
						&& arg2.getAction() == KeyEvent.ACTION_DOWN
						&& mGrid.getSelectedItemPosition() % 5 == 0){
					focusParent(R.id.activity_livetv);
					return true;
				}
				return false;
			
			}
		});
	
	}
	//mGrid.setAdapter(mAdapter);
/*		mGrid[].setOnItemClickListener(new ItemClickListener(){});
		
		mGrid.setOnKeyListener(new OnKeyListener() {});
	}*/

	private void iniData() {
		tvThread thread = new tvThread();
		thread.start();		
	}
	class tvThread extends Thread {
		public void run() {
			accessData();
			Message m = new Message();
			m.what = accessData;
			hander.sendMessage(m);
		}
	}
	class tvHandler extends Handler {
		public void handleMessage(Message msg) {
			Log.e("tag", "TV_accessMessage");
			switch (msg.what) {
			case accessData:
/*				initActivityView();
				initListener();*/
				iniViews();				
				progressDialog.dismiss();
				break;
			default:
			}
		}
	}
	
	private void accessData() {
		ArrayList<Channel> channelList;
/*		recommendList = TvListXmlPullParser
				.getTvList(Constant.url_tv_recommend);
		channelList = recommendList.getChannels();
		if (channelList != null) {
			for (int i = 0; i < channelList.size(); i++) {
				Channel element = channelList.get(i);
				list1.add(element);
			}
		}*/

		tvList = TvListXmlPullParser.getTvList(ConstValues.url_tv_new);	//xlhadd
		channelList = tvList.getChannels();
		if (channelList != null) {
			for (int i = 0; i < channelList.size(); i++) {
				Channel element = channelList.get(i);

				switch (Integer.valueOf(element.getType())) {
				case yangshi:
				/*	if (list2.size() < 10) {
						list2.add(element);
					} else if (list2.size() == 10) {
						element.setUrl("more");
						list2.add(element);
					}*/
					list2.add(element);
					String s=list2.size()+" ";
					Log.w("央视频道列表大小1",s);
					break;
				case weishi:
		/*			if (list3.size() < 10) {
						list3.add(element);
					} else if (list3.size() == 10) {
						element.setUrl("more");
						list3.add(element);
					}*/
					list3.add(element);
					break;
				case difang:
/*					if (list4.size() < 10) {
						list4.add(element);
					} else if (list4.size() == 10) {
						element.setUrl("more");
						list4.add(element);
					}*/
					list4.add(element);
					break;
				case shuzi:
/*					if (list5.size() < 10) {
						list5.add(element);
					} else if (list5.size() == 10) {
						element.setUrl("more");
						list5.add(element);
					}*/
					list5.add(element);
					break;
				case other:
/*					if (list6.size() < 10) {
						list6.add(element);
					} else if (list6.size() == 10) {
						element.setUrl("more");
						list6.add(element);
					}*/
					list6.add(element);
					break;
				default:
				}
			}
		} else {
			Toast.makeText(TvActivity.this,
					this.getResources().getString(R.string.network_error),
					Toast.LENGTH_LONG).show();
		}
		
	}
	

	public class TVAdapter extends BaseAdapter {
		private List<Channel> list = null;
		private Integer grid_layout = null;
		private int width = 0;
		private int height = 0;

		public TVAdapter(ArrayList<Channel> list1, Integer grid_layout,
				int width, int height) {
			this.list = list1;
			this.grid_layout = grid_layout;
			this.width = width;
			this.height = height;

			initFetcher();
		}

		public void initFetcher() {
			mImageFetcher = new ImageFetcher(TvActivity.this, width, height);
			mImageFetcher.setExitTasksEarly(false);

			mImageFetcher.flushCache();
			// imageSize
			mImageFetcher.addImageCache(cacheParams);
			mImageFetcher.setImageFadeIn(false);
			
			mImageFetcher.setLoadingImage(R.drawable.basic_img);
		}

		public int getCount() {
			return list.size();
		}

		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			Channel element = list.get(position);

			if (convertView == null) {
				convertView = getLayoutInflater().inflate(grid_layout, null);
			}

			ImageView iv = (ImageView) convertView.findViewById(R.id.img);
/*			ImageView tuijian = (ImageView) convertView
					.findViewById(R.id.tuijian);*/

/*			if (R.layout.title_grid_layout == grid_layout) {
				if (element.getUrl().toString().equals("more")) {
					iv.setImageResource(R.drawable.more);
				} else {
					mImageFetcher.loadImage(recommendList.getServer()
							+ element.getHimg().toString(), iv);
					tuijian.setImageResource(R.drawable.tuijian);
				}
			} else*/ {
/*				if (element.getUrl().toString().equals("more")) {
					iv.setImageResource(R.drawable.more);
				} else */{
					mImageFetcher.loadImage(tvList.getServer()
							+ element.getIcon().toString(), iv);
				}
			}
			return convertView;
		}
	}
	

	
}
