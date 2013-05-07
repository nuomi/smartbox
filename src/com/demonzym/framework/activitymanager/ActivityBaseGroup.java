package com.demonzym.framework.activitymanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.InputMethodService;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.wheel.test.OnWheelClickedListener;
import android.wheel.test.WheelView;
import android.wheel.test.adapter.ArrayWheelAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.demonzym.smartbox.R;
import com.demonzym.smartbox.activity.FilmGridActivity;

import com.demonzym.smartbox.api.Api;
import com.demonzym.smartbox.data.Foobar;
import com.demonzym.smartbox.data.TvChannel;
import com.demonzym.smartbox.data.TvClass;
import com.qipo.bean.TvList;
import com.qipo.parser.TvListXmlPullParser;
import com.qipo.activity.TvActivity;


/** �̳и��ಢʵ����������󷽷����� */
//����tab��һ��group
public abstract class ActivityBaseGroup extends ActivityGroup {

	private final String TAG = "ActivityBaseGroup";
	
	/** ԴIntent */
	protected Intent fromIntent;

	/** ����ģ����ת��Ŀ��Intent */
	protected Intent targetIntent = new Intent();

	/** ����������Activity�Ĳ��� */
	private LinearLayout container = null;

	/** ѡ� */
	private RadioGroup radioGroup = null;

	/** ѡ�б�ǩ��ID */
	private int radioGroupCheckId;

	/** �л���ǩ�����ı�־λ */
	private boolean changedFlag;

	/** ѡ������б�ǩ */
	private RadioButton radioButtons[] = null;

	/** ѡ����б�ǩ��ID */
	private int[] radioButtonIds = null;

	/** ѡ����б�ǩ��ͼ��ID */
	private int[] radioButtonImageIds;

//	private int[] radioFocusBgId = new int[] {
//			R.drawable.nav_recommend_selector, R.drawable.nav_movie_selector,
//			R.drawable.nav_tv_selector, R.drawable.nav_zy_selector,
//			R.drawable.nav_cartoon_selector,
//			R.drawable.nav_livetv_focus, R.drawable.nav_search_selector,
//			R.drawable.nav_userinfo_selector,};
	private int[] radioFocusId = new int[] { R.drawable.nav_recommend_focus,
			R.drawable.nav_movie_focus, R.drawable.nav_tv_focus,
			R.drawable.nav_art_focus, R.drawable.nav_cartoon_focus,
			R.drawable.nav_livetv_focus,
			R.drawable.nav_search_focus,
			R.drawable.nav_user_focus, };

	/** ѡ����б�ǩ������ */
	private String[] radioButtonTexts;

	/** ��ǩID��Ӧ�ĳ�ʼActivity���� */
	private Map<Integer, Class<? extends Activity>> classes = new HashMap<Integer, Class<? extends Activity>>();

	/** ��ǩID��Ӧ�ĵ�ǰActivity���� */
	public Map<Integer, Class<? extends Activity>> currentClasses = new HashMap<Integer, Class<? extends Activity>>();;

	/**
	 * ��������ʵ�ֵ��趨���ֵķ�����Activity�Ĳ��ֵ�id����Ϊactivity_group_container��
	 * ѡ���id����Ϊactivity_group_radioGroup
	 */
	protected abstract int getLayoutResourceId();

	/** ����������Ҫʵ�ֵĻ�ȡѡ����б�ǩ��ID�ķ��� */
	protected abstract int[] getRadioButtonIds();

	/** ����������Ҫʵ�ֵĻ�ȡѡ����б�ǩ��ͼ��ķ��� */
	protected abstract int[] getRadioButtonImageIds();

	/** ����������Ҫʵ�ֵĻ�ȡѡ����б�ǩ�����ֵķ��� */
	protected abstract String[] getRadioButtonTexts();

	/** ����������Ҫʵ�ֵĻ�ȡѡ����б�ǩID��Ӧ�ĳ�ʼActivity�ķ��� */
	public abstract Class<? extends Activity>[] getClasses();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// ���ó�ȫ��ģʽ
//		getWindow().getDecorView().setSystemUiVisibility(
//				View.SYSTEM_UI_FLAG_LOW_PROFILE);

		setContentView(getLayoutResourceId());
		// ��ȡԴIntent
		fromIntent = getIntent();
		// �趨ԭʼ���
		setData();
		// ��ʼ���ؼ�
		initWidgetStatic();
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		
		Log.e(TAG, "onKeyUp");
		
		return super.onKeyUp(keyCode, event);
	}

	/** �趨���Դ�ķ��� */
	protected void setData() {
		radioButtonIds = getRadioButtonIds();
		radioButtonImageIds = getRadioButtonImageIds();
		radioButtonTexts = getRadioButtonTexts();
		for (int i = 0; i < radioButtonIds.length; i++) {
			classes.put(radioButtonIds[i], getClasses()[i]);
			currentClasses.put(radioButtonIds[i], getClasses()[i]);
		}
	}

	/** ��ʼ���ؼ� */
	protected void initWidgetStatic() {
		container = (LinearLayout) findViewById(R.id.activity_group_container);
		radioGroup = (RadioGroup) findViewById(R.id.activity_group_radioGroup);
		radioButtons = new RadioButton[radioButtonIds.length];
		for (int i = 0; i < radioButtons.length; i++) {
			radioButtons[i] = (RadioButton) findViewById(radioButtonIds[i]);
			if (radioButtonImageIds != null) {
				radioButtons[i].setText(radioButtonTexts[i]);
				Drawable drawable = getResources().getDrawable(
						radioButtonImageIds[i]);
//				radioButtons[i].setCompoundDrawablesWithIntrinsicBounds(
//						drawable, null, null, null);
				((View)radioButtons[i]).setBackgroundDrawable(drawable);
			}
			radioButtons[i].setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// ���Ե���
					// popWindow(POPWINDOW_TYPE_FILM);
					if (!changedFlag) {
						targetIntent.setClass(ActivityBaseGroup.this,
								classes.get(radioGroupCheckId));
						launchActivity(targetIntent);
					}
					changedFlag = false;
				}
			});
			final int j = i;
			radioButtons[i]
					.setOnFocusChangeListener(new OnFocusChangeListener() {

						public void onFocusChange(View v, boolean hasFocus) {
							if (hasFocus) {
//								((RadioButton) v)
//										.setBackgroundResource(radioFocusBgId[j]);
								Drawable drawable = getResources().getDrawable(
										radioFocusId[j]);
//								((RadioButton) v)
//										.setCompoundDrawablesWithIntrinsicBounds(
//												drawable, null, null, null);
								v.setBackgroundDrawable(drawable);
								
								v.startAnimation(AnimationUtils.loadAnimation(
										ActivityBaseGroup.this,
										R.anim.focus_anim));
//								v.setAnimation();
							} else {
								Drawable drawable = getResources().getDrawable(
										radioButtonImageIds[j]);
//								((RadioButton) v)
//										.setCompoundDrawablesWithIntrinsicBounds(
//												drawable, null, null, null);

								v.setBackgroundDrawable(drawable);
								v.clearAnimation();
							}
						}
					});
		}

		radioButtons[1].setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					popWindow(POPWINDOW_TYPE_FILM);
					return true;
				}
				return false;
			}
		});
		radioButtons[2].setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					popWindow(POPWINDOW_TYPE_TV);
					return true;
				}
				return false;
			}
		});
		radioButtons[3].setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					popWindow(POPWINDOW_TYPE_ZY);
					return true;
				}
				return false;
			}
		});
		radioButtons[4].setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					popWindow(POPWINDOW_TYPE_CARTOON);
					return true;
				}
				return false;
			}
		});
		radioButtons[5].setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					popWindow(POPWINDOW_TYPE_LIVETV);
					return true;
				}
				return false;
			}
		});

		// ��ѡ��趨����
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(RadioGroup group, int checkedId) {
//				currentClasses.put(radioGroupCheckId, ActivityBaseGroup.this
//						.getCurrentActivity().getClass());
				setTargetIntent(checkedId);
				launchActivity(targetIntent);
				radioGroupCheckId = checkedId;
				changedFlag = true;
			}
		});

		// ��ʼ������		
		radioGroupCheckId = getCheckedRadioButtonId();
		setTargetIntent(radioGroupCheckId);
		launchNewActivity(targetIntent);
	}

	/** �趨Ŀ��Intent�ķ��� */
	protected void setTargetIntent(int checkedId) {
		targetIntent.setClass(ActivityBaseGroup.this,
				currentClasses.get(checkedId));
		targetIntent.setAction("");
	}

	/** ActivityGroup�����µ���Activity�ķ���(�����µ�) */
	public void launchNewActivity(Intent intent) {
		container.removeAllViews();
		container
				.addView(getLocalActivityManager().startActivity(
						intent.getComponent().getShortClassName()
								+ getCheckedRadioButtonId(),
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
						.getDecorView());
	}

	/** ActivityGroup�����µ���Activity�ķ���(�����µ�) */
	public void launchNewActivityForResult(ActivityBase requestSubActivity,
			Intent intent, int requestCode) {
		intent.putExtra("requestCode", requestCode);
		container.removeAllViews();
		container
				.addView(getLocalActivityManager().startActivity(
						intent.getComponent().getShortClassName()
								+ getCheckedRadioButtonId(),
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
						.getDecorView());
		((ActivityBase) getCurrentActivity())
				.setRequestSubActivity(requestSubActivity);
	}

	/** ActivityGroup������Activity�ķ���(�ȿ���û�У��������ԭ���ģ����򴴽��µ�) */
	public void launchActivity(Intent intent) {
		container.removeAllViews();
		container.addView(getLocalActivityManager().startActivity(
				intent.getComponent().getShortClassName()
						+ getCheckedRadioButtonId(),
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
				.getDecorView());
	}

	/** ����������ڻ�ȡ��ǰActivityGroup��ѡ����µĵ�ѡ��ť��ID */
	public int getCheckedRadioButtonId() {
		return radioGroup.getCheckedRadioButtonId();
	}

	public void checkRadio(int id) {
		radioGroup.check(id);
		radioGroup.requestFocus();
	}

	private final int POPWINDOW_TYPE_FILM = 0;
	private final int POPWINDOW_TYPE_CARTOON = 1;
	private final int POPWINDOW_TYPE_TV = 2;
	private final int POPWINDOW_TYPE_ZY = 3;
	private final int POPWINDOW_TYPE_LIVETV = 4;
	
	private int POPWINDOW_TYPE = -1;
	private PopupWindow mPopWindow;

	private void dismissWindow() {
		if (mPopWindow != null) {
			mPopWindow.dismiss();
			mPopWindow = null;
		}
	}

	private void popWindow(int type) {
		if (mPopWindow != null && mPopWindow.isShowing()) {
			mPopWindow.dismiss();
			mPopWindow = null;
		}
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = inflater.inflate(R.layout.pop_search, null);
		view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.FILL_PARENT));
		WheelView wheel1 = (WheelView) view.findViewById(R.id.wheel1);

		switch (type) {
		case POPWINDOW_TYPE_FILM: {
			int sum = Api.mMovieList.size();
			String[] data = new String[sum];
			for(int i = 0; i < sum; i++){
				data[i] = Api.mMovieList.get(i).getName();
			}
			final ArrayWheelAdapter<String> adapter1 = new ArrayWheelAdapter<String>(
					this, data);			
			wheel1.setVisibleItems(10);
			wheel1.setViewAdapter(adapter1);
			wheel1.setCurrentItem(4);
			wheel1.setOnKeyListener(new OnKeyListener() {

				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
							&& event.getAction() == KeyEvent.ACTION_DOWN) {
						dismissWindow();
						return true;
					}
					if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
							&& event.getAction() == KeyEvent.ACTION_DOWN) {
						dismissWindow();
						container.requestFocus();		//焦点选中框转移
//						for(int i = 0; i < ((ViewGroup)container.getChildAt(0)).getChildCount(); i++){
//							if(container.getChildAt(i).isFocusable()){
//								container.getChildAt(i).requestFocus();
//								break;
//							}
//						}
						return true;
					}
					return false;
				}
			});
			wheel1.requestFocus();
			wheel1.addClickingListener(new OnWheelClickedListener() {

				public void onItemClicked(WheelView wheel, int itemIndex) {

					setTargetIntent(radioButtonIds[1]);
					targetIntent.setAction((String) adapter1.getItemText(itemIndex));
					launchActivity(targetIntent);
					
//					Intent intent = new Intent(ActivityBaseGroup.this,
//							FilmGridActivity.class);
//					intent.setAction((String) adapter1.getItemText(itemIndex));
//					launchNewActivity(intent);
					radioButtons[1].setChecked(true);
					radioButtons[1].requestFocus();
					dismissWindow();
				}
			});
		}
			break;
		case POPWINDOW_TYPE_CARTOON: {
			int sum = Api.mCartoonList.size();
			String[] data = new String[sum];
			for(int i = 0; i < sum; i++){
				data[i] = Api.mCartoonList.get(i).getName();
			}
			final ArrayWheelAdapter<String> adapter1 = new ArrayWheelAdapter<String>(
					this, data);
			wheel1.setVisibleItems(10);
			wheel1.setViewAdapter(adapter1);
			wheel1.setCurrentItem(5);
			wheel1.setOnKeyListener(new OnKeyListener() {

				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
							&& event.getAction() == KeyEvent.ACTION_DOWN) {
						dismissWindow();
						return true;
					}
					if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
							&& event.getAction() == KeyEvent.ACTION_DOWN) {
						dismissWindow();
						container.requestFocus();
//						for(int i = 0; i < container.getChildCount(); i++){
//							if(container.getChildAt(i).isFocusable()){
//								container.getChildAt(i).requestFocus();
//								break;
//							}
//						}
						return true;
					}
					return false;
				}
			});
			wheel1.requestFocus();
			wheel1.addClickingListener(new OnWheelClickedListener() {

				public void onItemClicked(WheelView wheel, int itemIndex) {

					setTargetIntent(radioButtonIds[4]);
					targetIntent.setAction((String) adapter1.getItemText(itemIndex));
					launchActivity(targetIntent);
					
//					Intent intent = new Intent(ActivityBaseGroup.this,
//							FilmGridActivity.class);
//					intent.setAction((String) adapter1.getItemText(itemIndex));
//					launchNewActivity(intent);
					radioButtons[4].setChecked(true);
					radioButtons[4].requestFocus();
					dismissWindow();
				}
			});
		}
			break;
		case POPWINDOW_TYPE_TV: {
			int sum = Api.mTvList.size();
			String[] data = new String[sum];
			for(int i = 0; i < sum; i++){
				data[i] = Api.mTvList.get(i).getName();
			}
			final ArrayWheelAdapter<String> adapter1 = new ArrayWheelAdapter<String>(
					this, data);			
			wheel1.setVisibleItems(10);
			wheel1.setViewAdapter(adapter1);
			wheel1.setCurrentItem(5);
			wheel1.setOnKeyListener(new OnKeyListener() {

				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
							&& event.getAction() == KeyEvent.ACTION_DOWN) {
						dismissWindow();
						return true;
					}
					if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
							&& event.getAction() == KeyEvent.ACTION_DOWN) {
						dismissWindow();
						container.requestFocus();
//						for(int i = 0; i < container.getChildCount(); i++){
//							if(container.getChildAt(i).isFocusable()){
//								container.getChildAt(i).requestFocus();
//								break;
//							}
//						}
						return true;
					}
					return false;
				}
			});
			wheel1.requestFocus();
			wheel1.addClickingListener(new OnWheelClickedListener() {

				public void onItemClicked(WheelView wheel, int itemIndex) {

					setTargetIntent(radioButtonIds[2]);
					targetIntent.setAction((String) adapter1.getItemText(itemIndex));
					launchActivity(targetIntent);
					
//					Intent intent = new Intent(ActivityBaseGroup.this,
//							FilmGridActivity.class);
//					intent.setAction((String) adapter1.getItemText(itemIndex));
//					launchNewActivity(intent);
					radioButtons[2].setChecked(true);
					radioButtons[2].requestFocus();
					dismissWindow();
				}
			});
		}
			break;
		case POPWINDOW_TYPE_ZY: {
			int sum = Api.mZyList.size();
			String[] data = new String[sum];
			for(int i = 0; i < sum; i++){
				data[i] = Api.mZyList.get(i).getName();
			}
			final ArrayWheelAdapter<String> adapter1 = new ArrayWheelAdapter<String>(
					this, data);
			wheel1.setVisibleItems(10);
			wheel1.setViewAdapter(adapter1);
			wheel1.setCurrentItem(5);
			wheel1.setOnKeyListener(new OnKeyListener() {

				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
							&& event.getAction() == KeyEvent.ACTION_DOWN) {
						dismissWindow();
						return true;
					}
					if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
							&& event.getAction() == KeyEvent.ACTION_DOWN) {
						dismissWindow();
						container.requestFocus();
//						for(int i = 0; i < container.getChildCount(); i++){
//							if(container.getChildAt(i).isFocusable()){
//								container.getChildAt(i).requestFocus();
//								break;
//							}
//						}
						return true;
					}
					return false;
				}
			});
			wheel1.requestFocus();
			wheel1.addClickingListener(new OnWheelClickedListener() {

				public void onItemClicked(WheelView wheel, int itemIndex) {

					setTargetIntent(radioButtonIds[3]);
					targetIntent.setAction((String) adapter1.getItemText(itemIndex));
					launchActivity(targetIntent);
					
//					Intent intent = new Intent(ActivityBaseGroup.this,
//							FilmGridActivity.class);
//					intent.setAction((String) adapter1.getItemText(itemIndex));
//					launchNewActivity(intent);
					radioButtons[3].setChecked(true);
					radioButtons[3].requestFocus();
					dismissWindow();
				}
			});
		}
			break;			//xlh
		case POPWINDOW_TYPE_LIVETV: {			
			
			String[] data = new String[]{getResources().getString(R.string.yangshi),getResources().getString(R.string.gesheng),
					getResources().getString(R.string.difang),getResources().getString(R.string.zonghe),getResources().getString(R.string.qita)};
			final ArrayWheelAdapter<String> adapter1 = new ArrayWheelAdapter<String>(
					this, data);
			wheel1.setVisibleItems(10);
			wheel1.setViewAdapter(adapter1);
			wheel1.setCurrentItem(5);
			wheel1.setOnKeyListener(new OnKeyListener() {

				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
							&& event.getAction() == KeyEvent.ACTION_DOWN) {
						dismissWindow();
						return true;
					}
					if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
							&& event.getAction() == KeyEvent.ACTION_DOWN) {
						dismissWindow();
						container.requestFocus();
						return true;
					}
					return false;
				}
			});
			wheel1.requestFocus();
			wheel1.addClickingListener(new OnWheelClickedListener() {					//xlh

				public void onItemClicked(WheelView wheel, int itemIndex) {

					setTargetIntent(radioButtonIds[5]);
				//	targetIntent.setClass(getApplicationContext(), TvActivity.class);
					targetIntent.setAction((String) adapter1.getItemText(itemIndex));
					Log.w("Action",(String) adapter1.getItemText(itemIndex));
					launchActivity(targetIntent);
					radioButtons[5].setChecked(true);
					radioButtons[5].requestFocus();
					dismissWindow();
				}
			});
		}
			break;
		default:
			break;
		}

		mPopWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
				LayoutParams.FILL_PARENT);
		mPopWindow.setAnimationStyle(R.anim.focus_anim);
		mPopWindow.setTouchable(true);
		mPopWindow.setFocusable(true);
		mPopWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
		mPopWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopWindow.showAtLocation(getWindow().getDecorView(), Gravity.TOP
				| Gravity.LEFT, radioGroup.getWidth() / 2, 0);
	}

	@Override
	public void onBackPressed() {
		getCurrentActivity().onBackPressed();
	}
}
