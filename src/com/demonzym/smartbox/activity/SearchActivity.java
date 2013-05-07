package com.demonzym.smartbox.activity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
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
import com.demonzym.smartbox.activity.TvGridActivity.TvAdapter.ViewHolder;
import com.demonzym.smartbox.api.Api;
import com.demonzym.smartbox.data.Foobar;
import com.demonzym.smartbox.data.XMLReader;

/**
 * ����
 * @author Administrator
 *
 */
public class SearchActivity extends ActivityBase implements OnItemClickListener {

	private GridView mGridView;
	private ListView mListView;
//	private EditText mEdit;
	private TextView mText;
	private ProgressBar mProgressBar;

	private LinkedList<String> jianpan = new LinkedList<String>();

	int TYPE_UPPER = 0;
	int TYPE_LOWER = 1;
	int TYPE_FUHAO = 2;

	ArrayAdapter<String> adapter;
	private LayoutInflater mInflater;
	private ArrayList<Foobar> mData = new ArrayList<Foobar>();
	SearchAdapter mSearchAdapter;
	
	private int id;
	
	private LinearLayout main;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.search);
		
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mGridView = (GridView) findViewById(R.id.gridView1);
		mGridView.setOnItemClickListener(this);
		mListView = (ListView) findViewById(R.id.listView1);
//		mEdit = (EditText) findViewById(R.id.editText1);
		mText = (TextView) findViewById(R.id.textView1);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		//编辑框mEdit添加监听器
//		mEdit.addTextChangedListener(textWatcher);
		mText.addTextChangedListener(textWatcher);
		
		mSearchAdapter = new SearchAdapter();
		mListView.setAdapter(mSearchAdapter);
		mListView.setOnItemClickListener(new ItemClickListener());
		mListView.setVisibility(View.GONE);
		
		iniJianPan(TYPE_UPPER);
		
		main = (LinearLayout) findViewById(R.id.main);
		
		mGridView.setOnKeyListener(new OnKeyListener() {
			
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT
						&& event.getAction() == KeyEvent.ACTION_DOWN
						&& mGridView.getSelectedItemPosition() % 6 == 0){
					focusParent(R.id.activity_search);
					return true;
				}
				return false;
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
//		mEdit.requestFocus();		//默认焦点在编辑框
		mGridView.requestFocus();	//默认焦点在键盘的A
		mGridView.setSelection(0);
	}

    private void iniJianPan(int type) {
        jianpan.clear();
        if (type == TYPE_UPPER) {
            // 65-90大写
            for (int i = 65; i < 91; i++) {
                jianpan.add(String.valueOf((char) i));
            }
            for (int i = 48; i < 58; i++) {
                jianpan.add(String.valueOf((char) i));
            }
            jianpan.add("小写");
        } else if (type == TYPE_LOWER) {
            // 97-122大写
            for (int i = 97; i < 123; i++) {
                jianpan.add(String.valueOf((char) i));
            }
            for (int i = 48; i < 58; i++) {
                jianpan.add(String.valueOf((char) i));
            }
            jianpan.add("大写");
        } else if (type == TYPE_FUHAO) {
            // 33-47 60-64 91-94
            for (int i = 33; i < 48; i++) {
                jianpan.add(String.valueOf((char) i));
            }
            for (int i = 60; i < 65; i++) {
                jianpan.add(String.valueOf((char) i));
            }
            for (int i = 91; i < 95; i++) {
                jianpan.add(String.valueOf((char) i));
            }
            for (int i = 48; i < 58; i++) {
                jianpan.add(String.valueOf((char) i));
            }
            jianpan.add("大写");
        }
        jianpan.add("符号");
        jianpan.add("删除");
        jianpan.add("空格");
        jianpan.add("清空");
        
 //吧键盘加载到GridView
        /** 1.创建数组适配器（四个参数） 
         * this:环境，需要访问资源 
         * R.layout.item1：确定在那个布局资源中显示 
         * R.id.tv_data：确定显示在布局资源的的那个组件中 
         * getData()：提供显示的数据源 
         */ 
        adapter = new ArrayAdapter<String>(this,
                R.layout.search_grid,
                R.id.textView1, jianpan);
        mGridView.setAdapter(adapter);
    }
    
	//键盘按键监听
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        String item = (String) (arg0.getItemAtPosition(arg2));
        if (item.equals("小写")) {
            iniJianPan(TYPE_LOWER);
        } else if (item.equals("大写")) {
            iniJianPan(TYPE_UPPER);
        } else if (item.equals("符号")) {
            iniJianPan(TYPE_FUHAO);
        } else if (item.equals("删除")) {	
//        	int index = mEdit.getSelectionStart();  //删除光标前面的字符
//        	Editable editable = mEdit.getText();  
//        	editable.delete(index-1, index);  
            int start = mText.getEditableText().length() - 1;
            if(start < 0)
                start = 0;
            mText.getEditableText().delete(
                    start ,
                    mText.getEditableText().length());
        } else if (item.equals("空格")) {	
//        	int index = mEdit.getSelectionStart();  //在光标前面添加空格
//        	Editable editable = mEdit.getText();  
//        	editable.insert(index, " ");
        	mText.append(" ");
        } else if (item.equals("清空")) {
        	mText.setText("");
        } else {	//在光标前面添加字符
//        	int index = mEdit.getSelectionStart();  
//        	Editable editable = mEdit.getText();  
//        	editable.insert(index, item);  
            mText.append(item);
        }
 
    }

	
	TextWatcher textWatcher = new TextWatcher() {
		
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}
		
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		public void afterTextChanged(Editable s) {
			HttpRequest hr = (HttpRequest) HttpListener.getInstance().mListenerMap.get(id);
			if(hr != null){
				hr.doCancel();
				mProgressBar.setVisibility(View.GONE);
			}
			//xlh
			Api.getSearchList(s.toString(), new IHttpListener() {
				
				public void onSuccess(int requestId, String data, IHttp http) {
					mProgressBar.setVisibility(View.GONE);
					mData.clear();
					mData = (ArrayList<Foobar>) XMLReader.getFoobarsFromXml_search(data);
					
					if(mData == null || mData.size() == 0){
						toastSomething(R.string.search_result_is_empty);
						mListView.setVisibility(View.GONE);
					}else{
						mListView.setVisibility(View.VISIBLE);
					}
					
					mSearchAdapter.notifyDataSetChanged();
				}
				
				public void onError(int requestId, int errorCode, String error, IHttp http) {
					
				}

				public void onSuccess(int requestId, HttpResponse hr) {
					// TODO Auto-generated method stub
					
				}
			});

			mProgressBar.setVisibility(View.VISIBLE);
		}
	};

	class SearchAdapter extends BaseAdapter{

		public int getCount() {
			// TODO Auto-generated method stub
			return mData.size();
		}

		public Foobar getItem(int arg0) {
			// TODO Auto-generated method stub
			return mData.get(arg0);
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
			holder.iv.setVisibility(View.GONE);
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
	
	public class PopAdapter extends BaseAdapter{
		
		List<Foobar> data;
		
		public void setData(List<Foobar> foobarlist){
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
			holder.iv.setVisibility(View.GONE);
			return convertView;
		}
		
		public class ViewHolder{
			public ImageView iv;
			public TextView tv;
		}
		
	}
	
	PopupWindow pop;
	private class ItemClickListener implements OnItemClickListener{

		private boolean isLoading = false;

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if(isLoading )
				return;
			isLoading = true;
			waitingSomething(R.string.wait_for_init_data);
			final Foobar foobar = (Foobar) arg0.getAdapter().getItem(arg2);
			Api.getMovieData(foobar.getLink(), new IHttpListener() {
				
				public void onSuccess(int requestId, String data, IHttp http) {
					Intent intent = new Intent(SearchActivity.this,
							MovieDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("moviedata", XMLReader.getMovieDataFromXml(data));
					intent.putExtras(bundle);
					intent.setAction(foobar.getLink());
					startActivity(intent);
					stopWaiting();
					isLoading = false;
				}
				
				public void onError(int requestId, int errorCode, String error, IHttp http) {
					showError(error);
					stopWaiting();
					isLoading = false;
				}

				public void onSuccess(int requestId, HttpResponse hr) {
					// TODO Auto-generated method stub
					
				}
			});
//			Api.getMovieFoobar(foobar.getUrl(), new IHttpListener() {
//				
//				public void onSuccess(int requestId, String data, IHttp http) {
//					stopWaiting();
//					isLoading = false;
//					List<Foobar> foobarlist = XMLReader.getFoobarsFromXml(data);
//					if(foobarlist.size() == 0){
//						toastSomething("ӰƬ��Ϣ����");
//						return;
//					}else{
//						ListView listview = new ListView(SearchActivity.this);
//						PopAdapter sa = new PopAdapter();
//						sa.setData(foobarlist);
//						listview.setAdapter(sa);
//						pop = new PopupWindow(listview, LayoutParams.WRAP_CONTENT,
//								LayoutParams.FILL_PARENT);
//						pop.setTouchable(true);
//						pop.setFocusable(true);
//						pop.setBackgroundDrawable(getResources().getDrawable(R.drawable.float1_bg));
//						listview.requestFocus();
//						pop.setAnimationStyle(R.style.AnimationFade);
//						pop.showAtLocation(main, Gravity.CENTER, 0, 0);
//						listview.setOnItemClickListener(new OnItemClickListener() {
//
//							public void onItemClick(AdapterView<?> arg0,
//									View arg1, int arg2, long arg3) {
//								if(pop != null){
//									pop.dismiss();
//									pop = null;
//								}
//								waitingSomething("���ڻ�ȡ��ݣ����Եȡ�����");
//								Api.getMovieData(((Foobar) arg0.getItemAtPosition(arg2)).getUrl(), new IHttpListener() {
//									
//									public void onSuccess(int requestId, String data, IHttp http) {
//										Intent intent = new Intent(SearchActivity.this,
//												MovieDetailActivity.class);
//										Bundle bundle = new Bundle();
//										bundle.putSerializable("moviedata", XMLReader.getMovieDataFromXml(data));
//										intent.putExtras(bundle);
//										intent.setAction(foobar.getUrl());
//										startActivity(intent);
//										stopWaiting();
//										isLoading = false;
//									}
//									
//									public void onError(int requestId, int errorCode, String error, IHttp http) {
//										showError(error);
//										stopWaiting();
//										isLoading = false;
//									}
//								});
//							}
//						});
//						
//					}
//				}
//				
//				public void onError(int requestId, int errorCode, String error, IHttp http) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
			
		}
		
	}
}
