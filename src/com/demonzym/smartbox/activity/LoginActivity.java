package com.demonzym.smartbox.activity;

import java.util.LinkedList;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.demonzym.framework.activitymanager.ActivityBase;
import com.demonzym.framework.net.http.HttpListener.IHttpListener;
import com.demonzym.framework.net.http.HttpResponse;
import com.demonzym.framework.net.http.IHttp;
import com.demonzym.framework.util.Util;
import com.demonzym.smartbox.R;
import com.demonzym.smartbox.api.Api;
import com.demonzym.smartbox.data.UserInfo;

/**
 * * 登录，登录成功后自动写入settingpreference，下次自动登录
 * @author Administrator
 *
 */
public class LoginActivity extends ActivityBase implements OnItemClickListener {
	private GridView mGridView;
	private EditText mAccount, mPwd;
	private TextView mLogin, mQuit;

	private LinkedList<String> jianpan = new LinkedList<String>();

	int TYPE_UPPER = 0;
	int TYPE_LOWER = 1;
	int TYPE_FUHAO = 2;

	ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);

		mGridView = (GridView) findViewById(R.id.gridView1);
		mGridView.setOnItemClickListener(this);

		mAccount = (EditText) findViewById(R.id.account);
		mPwd = (EditText) findViewById(R.id.pwd);
		mAccount.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus)
					focusindex = 0;
			}
		});
		mPwd.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus)
					focusindex = 1;
			}
		});

		mLogin = (TextView) findViewById(R.id.login);
		mQuit = (TextView) findViewById(R.id.quit);

		iniJianPan(TYPE_UPPER);

		mGridView.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
						&& event.getAction() == KeyEvent.ACTION_DOWN
						&& mGridView.getSelectedItemPosition() % 6 == 0) {
					focusParent(R.id.activity_userinfo);
					return true;
				}
				return false;
			}
		});

		mQuit.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				goback();
			}
		});

		mLogin.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				final String account = mAccount.getEditableText().toString();
				final String pwd = mPwd.getEditableText().toString();

				if (Util.isStringEmpty(account) || Util.isStringEmpty(pwd)) {
					toastSomething(R.string.account_password_can_not_be_empty);
					return;
				}
				Api.login(account, pwd, new IHttpListener() {

					public void onSuccess(int requestId, String data, IHttp http) {
						stopWaiting();
						int i1 = data.indexOf("error");
						if (i1 > -1) {
							int i2 = data.indexOf("</error>");
							toastSomething(data.substring(i1 + 6, i2));
							return;
						}
						toastSomething(R.string.login_success);
						UserInfo.username = account;
						UserInfo.pwd = pwd;
						SettingActivity.setAccount(LoginActivity.this, account);
						SettingActivity.setPwd(LoginActivity.this, pwd);
						String uid = data.substring(12, data.indexOf("</uid>"));
						UserInfo.uid = uid;
						SettingActivity.setFav(LoginActivity.this, data);
						goback();
					}

					public void onError(int requestId, int errorCode,
							String error, IHttp http) {
						stopWaiting();
						toastSomething(R.string.Logon_failed_try_again);

					}

					public void onSuccess(int requestId, HttpResponse hr) {
						// TODO Auto-generated method stub
						
					}
				});
				waitingSomething(R.string.wait_for_Logining);
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		mGridView.requestFocus();

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

		jianpan.add("@163.com");
		jianpan.add("@126.com");
		jianpan.add("@qq.com");
		jianpan.add("@gmail.com");

        jianpan.add("符号");
        jianpan.add("删除");
        jianpan.add("空格");
        jianpan.add("清空");

		adapter = new ArrayAdapter<String>(this, R.layout.search_grid,
				R.id.textView1, jianpan);
		mGridView.setAdapter(adapter);
	}

	int focusindex = 0;

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		EditText mEdit = null;
		switch (focusindex) {
		case 0:
			mEdit = mAccount;
			break;
		case 1:
			mEdit = mPwd;
			break;
		}
		String item = (String) (arg0.getItemAtPosition(arg2));
		 if (item.equals("小写")) {
	            iniJianPan(TYPE_LOWER);
	        } else if (item.equals("大写")) {
	            iniJianPan(TYPE_UPPER);
	        } else if (item.equals("符号")) {
	            iniJianPan(TYPE_FUHAO);
	        } else if (item.equals("删除")) {
	            int start = mEdit.getEditableText().length() - 1;
	            if (start < 0)
	                start = 0;
	            mEdit.getEditableText().delete(start,
	                    mEdit.getEditableText().length());
	        } else if (item.equals("空格")) {
	            mEdit.append("");
	        } else if (item.equals("清空")) {
	            mEdit.setText("");
	        } else {
	            mEdit.append(item);
	        }

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		finish();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		// super.finish();
		goback();
	}
}
