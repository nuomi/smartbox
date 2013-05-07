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
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.demonzym.smartbox.api.Api;
import com.demonzym.smartbox.data.Foobar;
import com.demonzym.smartbox.data.MovieData;
import com.demonzym.smartbox.data.MovieListData;
import com.demonzym.smartbox.data.XMLReader;
import com.qipo.bitmapcache.ImageCache;
import com.qipo.bitmapcache.ImageFetcher;

/**
 * �Ƽ�
 * @author Administrator
 *
 */
public class RecommendActivity extends ActivityBase {
	
	//重磅推荐 电影好评榜前十
    //热播排行  连续剧”中的“最近更新”前10条、“电影”中“热播榜”的前5条、“综艺”中前5条记录
    //最新上线 “电影”中“最近更新”前6条、“连续剧”中“最近更新”前4条、“综艺”中前2条记录（总共12条）
	private ListView mTuijian, mZuixin;
	private GridView mPaihang;
	
	private ArrayList<Foobar> mZuixinData = new ArrayList<Foobar>();
	private ArrayList<Foobar> mTuijianData = new ArrayList<Foobar>();
	private ArrayList<Foobar> mPaihangData = new ArrayList<Foobar>();
	private TuijianAdapter mTuijianAdapter;
	private ZuixinAdapter mZuixinAdapter;
	private PaihangAdapter mPaihangAdapter;
	
	private LayoutInflater mInflater;
	
	private int mOkSum = 0;
	
	private boolean isLoading = false;
	
	private int mIndex = 1;
	
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
				
		setContentView(R.layout.recommend_layout);
		
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		DisplayMetrics metric = new DisplayMetrics();
		width = metric.widthPixels/6;
		cacheParams = new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(this, 0.25f); // Set memory cache to
															// 25% of mem class				
		findViews();
		
		iniData();
		
		if(!Util.hasSD()){
			//toastSomething(R.string.insert_SDcard);
		}

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		mTuijian.requestFocus();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mImageFetcher.closeCache();
	}

	private void findViews() {
		mTuijian = (ListView) findViewById(R.id.tuijian);
		mZuixin = (ListView) findViewById(R.id.zuixin);
		mPaihang = (GridView) findViewById(R.id.paihang);
		
		mTuijianAdapter = new TuijianAdapter(width,height);
		mZuixinAdapter = new ZuixinAdapter(width,height);
		mPaihangAdapter = new PaihangAdapter(width,height);
		
		mTuijian.setAdapter(mTuijianAdapter);
		mZuixin.setAdapter(mZuixinAdapter);
		mPaihang.setAdapter(mPaihangAdapter);
		
		mTuijian.setOnItemClickListener(new ItemClickListener());
		mZuixin.setOnItemClickListener(new ItemClickListener());
		mPaihang.setOnItemClickListener(new ItemClickListener());
		
		mTuijian.setOnKeyListener(new OnKeyListener() {
			
			public boolean onKey(View v, int keyCode, KeyEvent event) {	//xlh
				if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT
						&& event.getAction() == KeyEvent.ACTION_DOWN){
					focusParent(R.id.activity_recommend);
					return true;
				}
				return false;
			}
		});
	}

	private synchronized void iniData() {

		waitingSomething(R.string.wait_for_init_data);	//xlh
		
//		new Handler().postDelayed(new Runnable() {			//xlh
//			
//			public void run() {
//				boolean[] b;  //b1�Ƿ��ȡ��ɣ�b2�Ƿ����Ӵ���
//				b = Api.iniOk();
//				if(!b[0]){
//					if(!b[1]){
//						showError("��������ʧ��");
//						return;
//					}
//					iniData();
//					return;
//				}

				getTuijian();
				getZuixin();
				getPaihang();
//			}
//		}, 200);
	}


	private void getPaihang() {
		Api.getListInTv(Api.mTvList.get(0).getName(), new IHttpListener() {
			
			public void onSuccess(int requestId, String data, IHttp http) {
				MovieListData mld = XMLReader.getMovieListFromXml(data);
				if(mld == null){
					showError("RecommendActiviy getPaihangInTv error!");
					return;
				}
				for(int i = 0; i < 10; i++){
					mPaihangData.add(mld.mFoobarList.get(i));
				}
				mPaihangAdapter.notifyDataSetChanged();
				
				mOkSum++;
				if(mOkSum == 7){
					stopWaiting();
					mOkSum = 0;
				}
			}
			
			public void onError(int requestId, int errorCode, String error, IHttp http) {

				showError(error);
			}

			public void onSuccess(int requestId, HttpResponse hr) {
				// TODO Auto-generated method stub
				
			}
		}, String.valueOf(mIndex));
		
		Api.getListInMovie(Api.mMovieList.get(0).getName(), new IHttpListener() {
			
			public void onSuccess(int requestId, String data, IHttp http) {
				MovieListData mld = XMLReader.getMovieListFromXml(data);
				if(mld == null){
					showError();
					return;
				}
				for(int i = 0; i < 5; i++){
					mPaihangData.add(mld.mFoobarList.get(i));
				}
				mPaihangAdapter.notifyDataSetChanged();
				
				mOkSum++;
				if(mOkSum == 7){
					stopWaiting();
					mOkSum = 0;
				}
			}
			
			public void onError(int requestId, int errorCode, String error, IHttp http) {

				showError(error);
			}

			public void onSuccess(int requestId, HttpResponse hr) {
				// TODO Auto-generated method stub
				
			}
		}, String.valueOf(mIndex));
		
		Api.getListInZy(Api.mZyList.get(0).getName(), new IHttpListener() {
			
			public void onSuccess(int requestId, String data, IHttp http) {
				MovieListData mld = XMLReader.getMovieListFromXml(data);
				if(mld == null){
					showError();
					return;
				}
				for(int i = 0; i < 5; i++){
					mZuixinData.add(mld.mFoobarList.get(i));
				}
				mZuixinAdapter.notifyDataSetChanged();
				
				mOkSum++;
				if(mOkSum == 7){
					stopWaiting();
					mOkSum = 0;
				}
			}
			
			public void onError(int requestId, int errorCode, String error, IHttp http) {

				showError(error);
			}

			public void onSuccess(int requestId, HttpResponse hr) {
				// TODO Auto-generated method stub
				
			}
		}, String.valueOf(mIndex));
	}
	
	private void getZuixin() {
		if(Api.mMovieList.size()<=0)
		{
			return;
		}
		Api.getListInMovie(Api.mMovieList.get(0).getName(), new IHttpListener() {
			
			public void onSuccess(int requestId, String data, IHttp http) {
				MovieListData mld = XMLReader.getMovieListFromXml(data);
				if(mld == null){
					showError();
					return;
				}
				
				mOkSum++;
				if(mOkSum == 7){
					stopWaiting();
					mOkSum = 0;
				}
				
				if(mld.mFoobarList.size() == 0)
					return;
				for(int i = 0; i < 6; i++){
					mZuixinData.add(mld.mFoobarList.get(i));
				}
				mZuixinAdapter.notifyDataSetChanged();
				
			}
			
			public void onError(int requestId, int errorCode, String error, IHttp http) {

				showError(error);
			}

			public void onSuccess(int requestId, HttpResponse hr) {
				// TODO Auto-generated method stub
				
			}
		}, String.valueOf(mIndex));
		
		Api.getListInTv(Api.mTvList.get(0).getName(), new IHttpListener() {
			
			public void onSuccess(int requestId, String data, IHttp http) {
				MovieListData mld = XMLReader.getMovieListFromXml(data);
				if(mld == null){
					showError();
					return;
				}
				for(int i = 0; i < 4; i++){
					mZuixinData.add(mld.mFoobarList.get(i));
				}
				mZuixinAdapter.notifyDataSetChanged();
				
				mOkSum++;
				if(mOkSum == 7){
					stopWaiting();
					mOkSum = 0;
				}
			}
			
			public void onError(int requestId, int errorCode, String error, IHttp http) {

				showError(error);
			}

			public void onSuccess(int requestId, HttpResponse hr) {
				// TODO Auto-generated method stub
				
			}
		}, String.valueOf(mIndex));
		
		Api.getListInZy(Api.mZyList.get(0).getName(), new IHttpListener() {
			
			public void onSuccess(int requestId, String data, IHttp http) {
				MovieListData mld = XMLReader.getMovieListFromXml(data);
				if(mld == null){
					showError();
					return;
				}
				for(int i = 0; i < 4; i++){
					mZuixinData.add(mld.mFoobarList.get(i));
				}
				mZuixinAdapter.notifyDataSetChanged();
				
				mOkSum++;
				if(mOkSum == 7){
					stopWaiting();
					mOkSum = 0;
				}
			}
			
			public void onError(int requestId, int errorCode, String error, IHttp http) {

				showError(error);
			}

			public void onSuccess(int requestId, HttpResponse hr) {
				// TODO Auto-generated method stub
				
			}
		}, String.valueOf(mIndex));
	}
	
	private void getTuijian() {
		
		Log.v("daming.zou***movelist**", ""+Api.mMovieList.size());
		if(Api.mMovieList.size()<=0)
		{
			return;
		}
		Api.getListInMovie(Api.mMovieList.get(0).getName(), new IHttpListener() {
			
			public void onSuccess(int requestId, String data, IHttp http) {
				MovieListData mld = XMLReader.getMovieListFromXml(data);
				if(mld == null){
					showError();
					return;
				}
				for(int i = 0; i < 10; i++){
					mTuijianData.add(mld.mFoobarList.get(i));
				}
				mTuijianAdapter.notifyDataSetChanged();
				
				mTuijian.requestFocus();
				
				mOkSum++;
				if(mOkSum == 7){
					stopWaiting();
					mOkSum = 0;
				}
			}
			
			public void onError(int requestId, int errorCode, String error, IHttp http) {

				showError(error);
			}

			public void onSuccess(int requestId, HttpResponse hr) {
				// TODO Auto-generated method stub
				
			}
		}, String.valueOf(mIndex));
	}
	
	public void initFetcher() {
		mImageFetcher = new ImageFetcher(RecommendActivity.this, width, height);
		mImageFetcher.setExitTasksEarly(false);

		mImageFetcher.flushCache();
		// imageSize
		mImageFetcher.addImageCache(cacheParams);
		mImageFetcher.setImageFadeIn(false);
		
		mImageFetcher.setLoadingImage(R.drawable.basic_img);
	}
	
	class TuijianAdapter extends BaseAdapter{		
		private int width=0;
		private int height=0;
		
		public TuijianAdapter(int width,int height){
			this.width=width;
			this.height=height;
			
			initFetcher();
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return mTuijianData.size();
		}

		public Foobar getItem(int arg0) {
			// TODO Auto-generated method stub
			return mTuijianData.get(arg0);
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
				holder.iv.setScaleType(ScaleType.CENTER_CROP);
				holder.iv.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 200));	//加载视频元素图片
				holder.tv = (TextView) convertView.findViewById(R.id.name);		//视频元素名字（电影名）
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.tv.setText(getItem(position).getName());
			
			mImageFetcher.loadImage(getItem(position).getImage(), holder.iv);
//			final ViewHolder vh = holder;
//			ImageManager.getInstance().getImage(getItem(position).getImage(), new IImageDownloadListener() {
//				
//				public void onGetImageError() {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				public void onGetImage(Bitmap bitmap, String localPath) {
//					vh.iv.setImageBitmap(bitmap);		//下载图片，在ImageView设置图片
//				}
//				
//				public void onDownloading(int cur, int max) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
			return convertView;
		}
		
		class ViewHolder{
			public ImageView iv;
			public TextView tv;
		}
		
	}
	
	class ZuixinAdapter extends BaseAdapter{
		private int width=0;
		private int height=0;

		public ZuixinAdapter(int width, int height) {
			this.width=width;
			this.height=height;
			initFetcher();
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return mZuixinData.size();
		}

		public Foobar getItem(int arg0) {
			// TODO Auto-generated method stub
			return mZuixinData.get(arg0);
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
			mImageFetcher.loadImage(getItem(position).getImage(), holder.iv);
			
//			final ViewHolder vh = holder;
//			ImageManager.getInstance().getImage(getItem(position).getImage(), new IImageDownloadListener() {
//				
//				public void onGetImageError() {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				public void onGetImage(Bitmap bitmap, String localPath) {
//					vh.iv.setImageBitmap(bitmap);
//				}
//				
//				public void onDownloading(int cur, int max) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
			return convertView;
		}

		
		class ViewHolder{
			public ImageView iv;
			public TextView tv;
		}
	}
	
	class PaihangAdapter extends BaseAdapter{
		private int width=0;
		private int height=0;

		public PaihangAdapter(int width, int height) {
			this.width=width;
			this.height=height;
			initFetcher();
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return mPaihangData.size();
		}

		public Foobar getItem(int arg0) {
			// TODO Auto-generated method stub
			return mPaihangData.get(arg0);
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
			
			mImageFetcher.loadImage(getItem(position).getImage(), holder.iv);
/*			final ViewHolder vh = holder;
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
			});*/
			return convertView;
		}
		
		class ViewHolder{
			public ImageView iv;
			public TextView tv;
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
					Intent intent = new Intent(RecommendActivity.this,
							MovieDetailActivity.class);
					Bundle bundle = new Bundle();
					MovieData moviedata;
					moviedata =XMLReader.getMovieDataFromXml(data);
					if(moviedata!=null){
						bundle.putSerializable("moviedata",moviedata);
						intent.putExtras(bundle);
						intent.setAction(foobar.getUrl());
						startActivity(intent);
						progress.setVisibility(View.GONE);
						isLoading = false;
					}else{
						Toast.makeText(RecommendActivity.this, R.string.error_for_get_data, Toast.LENGTH_SHORT).show();
					}

				}
				
				public void onError(int requestId, int errorCode, String error, IHttp http) {
					toastSomething(error);
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
