package com.demonzym.smartbox.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference.BaseSavedState;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.demonzym.framework.activitymanager.ActivityBase;
import com.demonzym.framework.imagemanager.ImageManager;
import com.demonzym.framework.imagemanager.ImageManager.IImageDownloadListener;
import com.demonzym.framework.net.http.IHttp;
import com.demonzym.framework.net.http.HttpListener.IHttpListener;
import com.demonzym.framework.util.Util;
import com.demonzym.smartbox.R;
import com.demonzym.smartbox.androidmediaplayer.AndroidTVActivity.PopAdapter;
import com.demonzym.smartbox.androidmediaplayer.AndroidTVActivity.PopAdapter.ViewHolder;
import com.demonzym.smartbox.androidmediaplayer.AndroidTVActivity;
import com.demonzym.smartbox.api.Api;
import com.demonzym.smartbox.data.Foobar;
import com.demonzym.smartbox.data.MovieData;
import com.demonzym.smartbox.data.TvUrlData;
import com.demonzym.smartbox.data.XMLReader;
import com.qipo.bitmapcache.ImageCache;
import com.qipo.bitmapcache.ImageFetcher;

/**
 * ӰƬ����
 * @author Administrator
 *
 */
public class MovieDetailActivity extends ActivityBase {
	
	private final String TAG = "MovieDetailActivity";
	
	private String name, actor, director, date, leibie, desc, img;
	private ListView jierudian, xihuan;
	
	private ArrayList<Foobar> foobarlist = new ArrayList<Foobar>();
	private ArrayList<Foobar> xihuanlist = new ArrayList<Foobar>();
	
	public HashMap<String, ArrayList<Foobar>> sitemap =
			new HashMap<String, ArrayList<Foobar>>();
	
	private TextView shoucang, pingfen, mingzi, zhuyan, daoyan, jieshao;
	private ImageView image;
	private ScrollView jieshaoView;

	private LayoutInflater mInflater;
	private XihuanAdapter mXihuanAdapter;
	private JierudianAdapter mJierudianAdapter;
	
	private HashMap<String, Integer> mUrlNameMap = new HashMap<String, Integer>();
	
	private MovieData mMovieData;
	private String link;
	boolean shoucangtag=false;
	
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
		
		setContentView(R.layout.movie_detail);
		
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		DisplayMetrics metric = new DisplayMetrics();
		width = metric.widthPixels/6;
		cacheParams = new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(this, 0.25f); // Set memory cache to
		
		iniData();														// 25% of mem class
		findViews();		
			
		fillData();				
		refreshFavBtn();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		jierudian.requestFocus();
	}

	public void initFetcher() {
		mImageFetcher = new ImageFetcher(MovieDetailActivity.this, width, height);
		mImageFetcher.setExitTasksEarly(false);

		mImageFetcher.flushCache();
		// imageSize
		mImageFetcher.addImageCache(cacheParams);
		mImageFetcher.setImageFadeIn(false);
		
		mImageFetcher.setLoadingImage(R.drawable.basic_img);
	}
	
	private void fillData() {
		mingzi.setText(name);
		zhuyan.setText(String.format(getResources().getString(R.string.zhuyan), actor));
		daoyan.setText(String.format(getResources().getString(R.string.daoyan), director));
		jieshao.setText(desc);
		
		xihuan.setAdapter(mXihuanAdapter);
		if(foobarlist.size() != 0){
			jierudian.setAdapter(mJierudianAdapter);	//电影的播放源播放入口
		}
		else{
			String[] sitearray = new String[sitemap.size()];	//综艺，剧集播放入口
			Set<String> set = sitemap.keySet();
			Iterator<String> iterator = set.iterator();
			int ii = 0;
			while(iterator.hasNext()){
				sitearray[ii] = iterator.next();
				Log.w("视频列表名", sitearray[ii]);
				ii++;
			}
			SiteAdapter sa = new SiteAdapter(sitearray);	//播放源名称 xlh
			jierudian.setAdapter(sa);
			jierudian.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					String sitename = (String) arg0.getAdapter().getItem(arg2);
					popList(sitemap.get(sitename));	//点击播放源弹出播放列表 xlh
				}
			});
		}
		
		//xlh icon
		initFetcher();
		//由于某些盒子没有划分内存给应用使用，所以加载Icon图片的方式由下载到本地再加载改成加载到临时缓存显示
		mImageFetcher.loadImage(img, image);
		
//		ImageManager.getInstance().getImage(img, new IImageDownloadListener() {
//			
//			public void onGetImageError() {
//			}
//			
//			public void onGetImage(Bitmap bitmap, String localPath) {
//				image.setImageBitmap(bitmap);
//			}
//			
//			public void onDownloading(int cur, int max) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
	}

	private void findViews() {
		shoucang = (TextView) findViewById(R.id.fav);
		shoucang.requestFocus();
		shoucang.setOnKeyListener(new OnKeyListener() {
			
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT
						&& event.getAction() == KeyEvent.ACTION_DOWN){
					focusParent(R.id.activity_recommend);
					return true;
				}
				return false;
			}
		});
		
		shoucang.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Foobar foobar = new Foobar();
				foobar.setId(mMovieData.getId());
				foobar.setName(mMovieData.getName());
				foobar.setImage(mMovieData.getImg());
				foobar.setLink(link);				
				if(!Api.hasFav(mMovieData.getId(), MovieDetailActivity.this)){	//xlh_add
					Api.addFav(foobar, MovieDetailActivity.this);
					toastSomething(R.string.Has_collected);	
				}else{
					Api.remFav(foobar, MovieDetailActivity.this);
					toastSomething(R.string.cancel_collected);
				}
					refreshFavBtn();			
//					toastSomething(R.string.cancel_collected);
//					refreshFavBtn();				
			}
		});
		pingfen = (TextView) findViewById(R.id.score);
		pingfen.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				toastSomething(R.string.scoring_functions_are_not_enabled);
			}
		});
		mingzi = (TextView) findViewById(R.id.name);
		zhuyan = (TextView) findViewById(R.id.actor);
		daoyan = (TextView) findViewById(R.id.director);
		jieshao = (TextView) findViewById(R.id.desc);
		jieshaoView=(ScrollView)findViewById(R.id.scrollView1);
		jieshaoView.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT
						&& event.getAction() == KeyEvent.ACTION_DOWN){
					focusParent(R.id.activity_recommend);
					return true;
				}
				return false;
			}
		});
		
		
		image = (ImageView) findViewById(R.id.img);
		
		jierudian = (ListView) findViewById(R.id.jierudian);
		xihuan = (ListView) findViewById(R.id.listView1);
		
		jierudian.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int p,
					long arg3) {
				
				Foobar foobar = new Foobar();
				foobar.setId(mMovieData.getId());
				foobar.setName(mMovieData.getName());
				foobar.setImage(mMovieData.getImg());
				foobar.setLink(link);
				Api.addHistory(foobar, MovieDetailActivity.this);

				Intent intent =new Intent(MovieDetailActivity.this, AndroidTVActivity.class);//TVActivity.startTVActivity(MovieDetailActivity.this);
				intent.setAction(mJierudianAdapter.getItem(p).getUrl());
				Bundle bundle = new Bundle();
				bundle.putSerializable("tvlist", foobarlist);
				intent.putExtra("tvlist", bundle);
				startActivity(intent);
			}
		});
	}
	
	private void refreshFavBtn(){
		if(Api.hasFav(mMovieData.getId(), this)){
			shoucang.setBackgroundResource(R.drawable.favorites_highlight_selector);
		}else{
			shoucang.setBackgroundResource(R.drawable.favorites_selector);
		}
	}


	private void iniData() {
		Bundle b = getIntent().getExtras();
		mMovieData = (MovieData) b.getSerializable("moviedata");
		if(mMovieData == null){
			toastSomething(R.string.data_error_try_again);
			goback();
			return;
		}
		link = getIntent().getAction();
		name = mMovieData.getName();
		actor = mMovieData.getActor();
		director = mMovieData.getDirector();
		img = mMovieData.getImg();
		desc = mMovieData.getDesc();
		
		foobarlist = mMovieData.mFoobarList;
		sitemap = mMovieData.mMap;
		
		mXihuanAdapter = new XihuanAdapter();
		mJierudianAdapter = new JierudianAdapter();
		
		//pptv��sohu���ſᣬ���ӣ���������6������
//		mUrlNameMap.put("����", R.drawable.letv_selector);
//		mUrlNameMap.put("leshi", R.drawable.letv_selector);
//		mUrlNameMap.put("tudou", R.drawable.tudou_selector);
//		mUrlNameMap.put("����", R.drawable.tudou_selector);
//		mUrlNameMap.put("pptv", R.drawable.tudou_selector);
//		mUrlNameMap.put("sohu", R.drawable.tudou_selector);
//		mUrlNameMap.put("�ſ�", R.drawable.tudou_selector);
//		mUrlNameMap.put("��6", R.drawable.tudou_selector);
//		mUrlNameMap.put("����", R.drawable.tudou_selector);
//		mUrlNameMap.put("qiyi", R.drawable.tudou_selector);
	}
	
	class JierudianAdapter extends BaseAdapter{
		public int getCount() {
			return foobarlist.size();
		}

		public Foobar getItem(int arg0) {
			return foobarlist.get(arg0);
		}

		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.tvurl_item, null);				
				holder = new ViewHolder();
				holder.tv = (TextView) convertView.findViewById(R.id.url);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.tv.setText(getItem(position).getName());
			holder.tv.setBackgroundResource(getResFromName(getItem(position).getName()));
						
			return convertView;
		}

		
		class ViewHolder{
			public TextView tv;
		}
	}
	
	class SiteAdapter extends BaseAdapter{
		private String[] mSite;
		public SiteAdapter(String[] site){
			mSite = site;
		}
		public int getCount() {
			return mSite.length;
		}

		public String getItem(int arg0) {
			return mSite[arg0];
		}

		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.tvurl_item, null);				
				holder = new ViewHolder();
				holder.tv = (TextView) convertView.findViewById(R.id.url);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.tv.setText(getItem(position));
			holder.tv.setBackgroundResource(getResFromName(getItem(position)));
						
			return convertView;
		}

		
		class ViewHolder{
			public TextView tv;
		}
	}
	
	//��ͬý���Ӧ��ͬ�İ�ť����
	//һ��ʼ��ݻ�û�а��ղ�ͬ��ý����࣬�����Ӿ�Ҳ��û����ÿһ����ͬý���Ӧ�İ�ť��
	private int getResFromName(String name){
		if(Util.isStringEmpty(name)){
			return R.drawable.channel_selector;
		}
		String[] n = name.split("[-]");
//		Log.e("name", "" + n[0]);
		if(mUrlNameMap.containsKey(name))
			return mUrlNameMap.get(n[0].toLowerCase());
		else
			return R.drawable.channel_selector;
	}
	
	class XihuanAdapter extends BaseAdapter{
		public int getCount() {
			// TODO Auto-generated method stub
			return xihuanlist.size();
		}

		public Foobar getItem(int arg0) {
			// TODO Auto-generated method stub
			return xihuanlist.get(arg0);
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
	
	@Override
	public void onBackPressed() {
		
//		Log.e(TAG, "onBackPressed");
		
		goback();
		
	}
	
	PopupWindow pop;
	public void popList(final ArrayList<Foobar> fl) {
		Log.v("播放列表名称3", fl+"fl");
		ListView listview = new ListView(this);
		PopAdapter sa = new PopAdapter();
		sa.setData(fl);
		Foobar foobar=null;
		for(int i=0;i<fl.size();i++)
			foobar=fl.get(i);
			Log.w("播放列表名称3",foobar+"foobar");
			Log.v("播放列表名称3", foobar==null?null:foobar.getName()+"foobarname");
			
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
				Foobar foobar = new Foobar();
				foobar.setId(mMovieData.getId());
				foobar.setName(mMovieData.getName());
				foobar.setImage(mMovieData.getImg());
				foobar.setLink(link);
				Api.addHistory(foobar, MovieDetailActivity.this);

				Intent intent = new Intent(MovieDetailActivity.this,AndroidTVActivity.class);		//xlh
				intent.setAction(fl.get(arg2).getUrl());
				
				Log.w("播放列表名称3", fl.get(arg2).getName());	//xlhadd
				
				Bundle bundle = new Bundle();
				bundle.putSerializable("tvlist", fl);
				intent.putExtra("tvlist", bundle);
				startActivity(intent);
			}
		});
	}

	class PopAdapter extends BaseAdapter {

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
			
			Log.w("播放列表名称2", getItem(position).getName());//xlhadd
			
			holder.iv.setVisibility(View.GONE);
			return convertView;
		}

		class ViewHolder {
			public ImageView iv;
			public TextView tv;
		}

	}
}
