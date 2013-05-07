package com.demonzym.smartbox.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.demonzym.framework.activitymanager.ActivityBase;
import com.demonzym.framework.imagemanager.ImageManager;
import com.demonzym.framework.imagemanager.ImageManager.IImageDownloadListener;
import com.demonzym.framework.net.http.HttpResponse;
import com.demonzym.framework.net.http.IHttp;
import com.demonzym.framework.net.http.HttpListener.IHttpListener;
import com.demonzym.smartbox.R;
import com.demonzym.smartbox.activity.ComicGridActivity.ComicAdapter.ViewHolder;
import com.demonzym.smartbox.api.Api;
import com.demonzym.smartbox.data.Foobar;
import com.demonzym.smartbox.data.UserInfo;
import com.demonzym.smartbox.data.XMLReader;

/**
 * �û���Ϣ
 * @author Administrator
 *
 */
public class UserInfoActivity extends ActivityBase implements OnClickListener, OnItemClickListener{
	
	private TextView mReg, mLogin, mAbout, mYidenglu, mLogout;
	
	private GridView mFav;
	private ListView mHis;
	
	private LayoutInflater mInflater;
	private ArrayList<Foobar> mFavData = new ArrayList<Foobar>();
	private ArrayList<Foobar> mHistoryData = new ArrayList<Foobar>();
	
	FavAdapter mFavAdapter;
	HistoryAdapter mHistoryAdapter;
	private boolean isLoading = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.userinfo);
		
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mReg = (TextView) findViewById(R.id.reg);
		mLogin = (TextView) findViewById(R.id.login);
		mAbout = (TextView) findViewById(R.id.about);
		
		mYidenglu = (TextView) findViewById(R.id.yidenglu);
		mLogout = (TextView) findViewById(R.id.logout);
		
		keyListener kl = new keyListener();
		mReg.setOnKeyListener(kl);
		mLogin.setOnKeyListener(kl);
		mAbout.setOnKeyListener(kl);
		mYidenglu.setOnKeyListener(kl);
		mLogout.setOnKeyListener(kl);
		
		mFav = (GridView) findViewById(R.id.shoucang);
		mHis = (ListView) findViewById(R.id.history);
		
		mReg.setOnClickListener(this);
		mLogin.setOnClickListener(this);
		mAbout.setOnClickListener(this);
		mLogout.setOnClickListener(this);
		
		mFavData = (ArrayList<Foobar>) XMLReader.getFoobarsFromXml(SettingActivity.getFav(this));
		mFavAdapter = new FavAdapter();
		mFav.setAdapter(mFavAdapter);
		

		mHistoryData = (ArrayList<Foobar>) XMLReader.getFoobarsFromXml(SettingActivity.getHistory(this));
		mHistoryAdapter = new HistoryAdapter();
		mHis.setAdapter(mHistoryAdapter);
		
		mFav.setOnItemClickListener(this);
		mHis.setOnItemClickListener(this);
	}
	
	private class keyListener implements OnKeyListener{
		
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT
					&& event.getAction() == KeyEvent.ACTION_DOWN){
				focusParent(R.id.activity_userinfo);
				return true;
			}
			return false;
		}
	}

	public void onClick(View v) {
		switch(v.getId()){
		case R.id.reg:{
			Intent intent = new Intent(UserInfoActivity.this, RegActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.login:{
			Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.logout:{
			UserInfo.clear();
			onResume();
		}
			break;
		case R.id.about:
			break;
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(UserInfo.isOnline()){
			mReg.setVisibility(View.GONE);
			mLogin.setVisibility(View.GONE);
			mYidenglu.setVisibility(View.VISIBLE);
			mLogout.setVisibility(View.VISIBLE);
		}else{

			mReg.setVisibility(View.VISIBLE);
			mLogin.setVisibility(View.VISIBLE);
			mYidenglu.setVisibility(View.GONE);
			mLogout.setVisibility(View.GONE);
		}
		

		mFavData = (ArrayList<Foobar>) XMLReader.getFoobarsFromXml(SettingActivity.getFav(this));
		mFavAdapter = new FavAdapter();
		mFav.setAdapter(mFavAdapter);

		mHistoryData = (ArrayList<Foobar>) XMLReader.getFoobarsFromXml(SettingActivity.getHistory(this));
		mHistoryAdapter = new HistoryAdapter();
		mHis.setAdapter(mHistoryAdapter);
	}
	
	class FavAdapter extends BaseAdapter{

		public int getCount() {
			// TODO Auto-generated method stub
			return mFavData.size();
		}

		public Foobar getItem(int arg0) {
			// TODO Auto-generated method stub
			return mFavData.get(arg0);
		}

		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.tuijian_item, null);				
				holder = new ViewHolder();
				holder.iv = (ImageView) convertView.findViewById(R.id.img);
				holder.tv = (TextView) convertView.findViewById(R.id.name);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.tv.setText(getItem(position).getName());
			
			final ViewHolder vh = holder;
			ImageManager.getInstance().getImage(getItem(position).getImage(), new IImageDownloadListener() {
				
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
		
		class ViewHolder{
			public ImageView iv;
			public TextView tv;
		}
		
	}
	
	class HistoryAdapter extends BaseAdapter{

		public int getCount() {
			// TODO Auto-generated method stub
			return mHistoryData.size();
		}

		public Foobar getItem(int arg0) {
			// TODO Auto-generated method stub
			return mHistoryData.get(arg0);
		}

		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.tuijian_item, null);				
				holder = new ViewHolder();
				holder.iv = (ImageView) convertView.findViewById(R.id.img);
				holder.tv = (TextView) convertView.findViewById(R.id.name);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.tv.setText(getItem(position).getName());
			
			final ViewHolder vh = holder;
			ImageManager.getInstance().getImage(getItem(position).getImage(), new IImageDownloadListener() {
				
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
		
		class ViewHolder{
			public ImageView iv;
			public TextView tv;
		}
		
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(isLoading)
			return;
		isLoading = true;
		final Foobar foobar = (Foobar) arg0.getAdapter().getItem(arg2);
		final ProgressBar progress = (ProgressBar) arg1.findViewById(R.id.progressBar1);
		progress.setVisibility(View.VISIBLE);
		Api.getMovieData(foobar.getLink(), new IHttpListener() {
			
			public void onSuccess(int requestId, String data, IHttp http) {
				Intent intent = new Intent(UserInfoActivity.this,
						MovieDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("moviedata", XMLReader.getMovieDataFromXml(data));
				intent.putExtras(bundle);
				intent.setAction(foobar.getLink());
				startActivity(intent);
				progress.setVisibility(View.GONE);
				isLoading = false;
			}
			
			public void onError(int requestId, int errorCode, String error, IHttp http) {
				showError("error");
				progress.setVisibility(View.GONE);
				isLoading = false;
			}

			public void onSuccess(int requestId, HttpResponse hr) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
