package com.demonzym.smartbox.activity;


import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.demonzym.framework.activitymanager.ActivityBase;
import com.demonzym.framework.imagemanager.ImageManager;
import com.demonzym.framework.imagemanager.ImageManager.IImageDownloadListener;
import com.demonzym.framework.net.http.HttpListener.IHttpListener;
import com.demonzym.framework.net.http.HttpResponse;
import com.demonzym.framework.net.http.IHttp;
import com.demonzym.framework.util.Util;
import com.demonzym.smartbox.R;
import com.demonzym.smartbox.api.Api;
import com.demonzym.smartbox.data.Foobar;
import com.demonzym.smartbox.data.MovieListData;
import com.demonzym.smartbox.data.XMLReader;
import com.qipo.activity.TvActivity;
import com.qipo.bitmapcache.ImageCache;
import com.qipo.bitmapcache.ImageFetcher;

/**
 * ��Ӱ
 * @author Administrator
 *
 */
public class FilmGridActivity extends ActivityBase{	

	private ArrayList<Foobar> mFilmData = new ArrayList<Foobar>();
	private LayoutInflater mInflater;
	
	private ImageView mImageView;
	private GridView mGrid;
	private FilmAdapter mAdapter;
	
	private boolean isLoading = false;
	
	private int mIndex = 1;
	
	//进入电影界面，默认的电影类型，服务端原本有一个全部，现在没了
	private String mData = "动作";	////xlh 后面Intent传过来的Action
	
	//加载Icon图片的宽高和临时内存
	int width=0;
	int height=170;
	private ImageFetcher mImageFetcher;
	private ImageCache.ImageCacheParams cacheParams = null;
	private static final String IMAGE_CACHE_DIR = "GuozitvIcon";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
				
		setContentView(R.layout.movie_grid);

		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		DisplayMetrics metric = new DisplayMetrics();
		width = metric.widthPixels/6;
		cacheParams = new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(this, 0.25f); // Set memory cache to
															// 25% of mem class
				
		iniViews();
		
		iniIntent(getIntent());		//iniData initViews xlh
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
		
		mGrid.requestFocus();
		
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mImageFetcher.closeCache();
	}

	private void iniViews() {
		
		mImageView = (ImageView) findViewById(R.id.imageView1);
		mImageView.setImageResource(R.drawable.movie);
		mGrid = (GridView) findViewById(R.id.gridView1);
		mAdapter = new FilmAdapter(width,height);
		mGrid.setAdapter(mAdapter);
		mGrid.setOnItemClickListener(new ItemClickListener());
		
		mGrid.setOnKeyListener(new OnKeyListener() {
			
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN 
						&& event.getAction() == KeyEvent.ACTION_DOWN
						&& (mAdapter.getCount() % 5 == 0 ? mGrid.getSelectedItemPosition() > (mAdapter.getCount() - 6)
						: mGrid.getSelectedItemPosition() > mAdapter.getCount() / 5 * 5)
					){
					nextPage();
					return true;
				}
				if(keyCode == KeyEvent.KEYCODE_DPAD_UP
						&& event.getAction() == KeyEvent.ACTION_DOWN
						&& mGrid.getSelectedItemPosition() < 5){
					previousPage();
					return true;
				}
				if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT
						&& event.getAction() == KeyEvent.ACTION_DOWN
						&& mGrid.getSelectedItemPosition() % 5 == 0){
					focusParent(R.id.activity_film);	//焦点转到父焦点（RadioButton）
					return true;
				}
				return false;
			}
		});
	}

	private void iniData() {
		waitingSomething(R.string.wait_for_init_data);	//xlh
		
		Api.getListInMovie(mData, new IHttpListener() {
			
			public void onSuccess(int requestId, String data, IHttp http) {
				Log.w("onSuccess","onSuccess");
				stopWaiting();
				
				mFilmData.clear();
				MovieListData mld = XMLReader.getMovieListFromXml(data);
				if(mld == null){
					showError();
					return;
				}
				for(int i = 0; i < mld.mFoobarList.size(); i++){
					mFilmData.add(mld.mFoobarList.get(i));
				}
				mAdapter.notifyDataSetChanged();
				
				mGrid.requestFocus();
			}
			
			public void onError(int requestId, int errorCode, String error, IHttp http) {

				showError(error);
			}

			public void onSuccess(int requestId, HttpResponse hr) {
				// TODO Auto-generated method stub
				
			}
		}, String.valueOf(mIndex));	// "" xlh
	}
	
	private void previousPage(){
		mIndex--;
		if(mIndex < 1){
			mIndex = 1;
			toastSomething(R.string.is_the_first_page);
			return;
		}
		waitingSomething(R.string.get_on_a_page);
		Api.getListInMovie(mData, new IHttpListener() {
			
			public void onSuccess(int requestId, String data, IHttp http) {
				stopWaiting();
				
				MovieListData mld = XMLReader.getMovieListFromXml(data);
				if(mld == null){
					showError();
					return;
				}
				mFilmData.clear();
				for(int i = 0; i < mld.mFoobarList.size(); i++){
					mFilmData.add(mld.mFoobarList.get(i));
				}
				mAdapter.notifyDataSetChanged();
				mGrid.setSelection(0);
				stopWaiting();
			}
			
			public void onError(int requestId, int errorCode, String error, IHttp http) {

				showError(error);
			}

			public void onSuccess(int requestId, HttpResponse hr) {
				// TODO Auto-generated method stub
				
			}
		}, "" + String.valueOf(mIndex));
	}
	
	private void nextPage(){
		mIndex++;
		waitingSomething(R.string.get_on_a_page);
		Api.getListInMovie(mData, new IHttpListener() {
			
			public void onSuccess(int requestId, String data, IHttp http) {
				stopWaiting();
				
				MovieListData mld = XMLReader.getMovieListFromXml(data);
				if(mld == null){
					showError();
					return;
				}
				mFilmData.clear();
				for(int i = 0; i < mld.mFoobarList.size(); i++){
					mFilmData.add(mld.mFoobarList.get(i));
				}
				mAdapter.notifyDataSetChanged();
				mGrid.setSelection(0);
				stopWaiting();
			}
			
			public void onError(int requestId, int errorCode, String error, IHttp http) {

				showError(error);
			}

			public void onSuccess(int requestId, HttpResponse hr) {
				// TODO Auto-generated method stub
				
			}
		}, "" + String.valueOf(mIndex));
	}
	
	class FilmAdapter extends BaseAdapter{
		private int width=0;
		private int height=0;

		public FilmAdapter(int width,int height){
			this.width=width;
			this.height=height;
			initFetcher();
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return mFilmData.size();
		}

		public Foobar getItem(int arg0) {
			// TODO Auto-generated method stub
			return mFilmData.get(arg0);
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
			
			//由于某些盒子没有划分内存给应用使用，所以加载Icon图片的方式由下载到本地再加载改成加载到临时缓存显示
			mImageFetcher.loadImage(getItem(position).getImage(), holder.iv);			
		/*	final ViewHolder vh = holder;			
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
			});			*/
			
			
			return convertView;
		}
		
		class ViewHolder{
			public ImageView iv;
			public TextView tv;
		}
		public void initFetcher() {
			mImageFetcher = new ImageFetcher(FilmGridActivity.this, width, height);
			mImageFetcher.setExitTasksEarly(false);

			mImageFetcher.flushCache();
			// imageSize
			mImageFetcher.addImageCache(cacheParams);
			mImageFetcher.setImageFadeIn(false);
			
			mImageFetcher.setLoadingImage(R.drawable.basic_img);
		}
		
		
	}
	
	private class ItemClickListener implements OnItemClickListener{

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if(isLoading)
				return;
			isLoading = true;
			final Foobar foobar = (Foobar) arg0.getAdapter().getItem(arg2);
			final ProgressBar progress = (ProgressBar) arg1.findViewById(R.id.progressBar1);
			progress.setVisibility(View.VISIBLE);
			Api.getMovieData(foobar.getUrl(), new IHttpListener() {
				
				public void onSuccess(int requestId, String data, IHttp http) {
					Intent intent = new Intent(FilmGridActivity.this,
							MovieDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("moviedata", XMLReader.getMovieDataFromXml(data));
					intent.putExtras(bundle);
					intent.setAction(foobar.getUrl());
					startActivity(intent);
					progress.setVisibility(View.GONE);
					isLoading = false;
				}
				
				public void onError(int requestId, int errorCode, String error, IHttp http) {
					showError(error);
					progress.setVisibility(View.GONE);
					isLoading = false;
				}

				public void onSuccess(int requestId, HttpResponse hr) {
					// TODO Auto-generated method stub
					
				}
			});
		}
		
	}
}
