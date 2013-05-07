package com.demonzym.smartbox.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.demonzym.framework.activitymanager.ActivityBaseGroup;
import com.demonzym.framework.dialog.MyDialog;
import com.demonzym.smartbox.R;

import com.qipo.activity.TvActivity;


/**
 * ����
 * @author Administrator
 *
 */
public class MainActivity extends ActivityBaseGroup{

	protected int getLayoutResourceId() {
		return R.layout.activity_group_layout;
	}

	protected int[] getRadioButtonIds() {
		return new int[] {
				R.id.activity_recommend,
				R.id.activity_film,
				R.id.activity_tv,
				R.id.activity_zy,
				R.id.activity_cartoon,
				R.id.activity_livetv,
				R.id.activity_search,
				R.id.activity_userinfo,
				};
	}

	protected int[] getRadioButtonImageIds() {
		return new int[] {
				R.drawable.recommend_selector,
				R.drawable.movie_selector,
				R.drawable.tv_selector,
				R.drawable.art_selector,
				R.drawable.cartoon_selector,
				R.drawable.livetv_selector,
				R.drawable.search_selector,
				R.drawable.userinfo_selector,
				};
	} 

	protected String[] getRadioButtonTexts() {
		return new String[]{
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				""
				};
//		return null;
	}

	public Class<? extends Activity>[] getClasses() {
		Class<? extends Activity>[] classes = new Class[] {
//				UserInfoActivity.class,
				RecommendActivity.class,
				FilmGridActivity.class,
				TvGridActivity.class,
				ComicGridActivity.class,
				CartoonGridActivity.class,
				TvActivity.class,//LiveTvActivity.class,
				SearchActivity.class,
				UserInfoActivity.class,
				};
		return classes;
	}

}