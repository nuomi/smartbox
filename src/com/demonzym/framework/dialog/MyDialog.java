package com.demonzym.framework.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.demonzym.framework.activitymanager.ActivityBase;
import com.demonzym.smartbox.R;


public class MyDialog {
	
	public static Context getValidParent(Context context){
		if(context instanceof ActivityBase){
			Activity newActivity = (Activity)context;
			Activity parent = newActivity;
			while(parent != null && parent.getParent() != null){
				parent = parent.getParent();
			}
			
			return parent;
		}
		
		return context;
	}
	
	static PopupWindow pop;
	/**
	 * ��ʾ�а�ť�ĶԻ���
	 * @param context
	 * @param msg
	 * @param title
	 * @param icon
	 * @param left
	 * @param leftclick
	 * @param right
	 * @param rightclick
	 */
	public static void showAlertDialog(Context context,
			String msg, String title,int icon,
			String left, OnClickListener leftclick,
			String right, OnClickListener rightclick
			){
		newAlertDiag(context)
		.setIcon(icon)
		.setMessage(msg)
		.setTitle(title)
		.setNegativeButton(left, leftclick)
		.setPositiveButton(right, rightclick)
		.show();
		
//		if(pop != null){
//			pop.dismiss();
//			pop = null;
//		}
//		LayoutInflater inflater = (LayoutInflater) context.
//				getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View mainview = inflater.inflate(R.layout.dialog, null);
//		TextView titletx, messagetx, lefttx, righttx;
//		titletx = (TextView) mainview.findViewById(R.id.title);
//		messagetx = (TextView) mainview.findViewById(R.id.message);
//		lefttx = (TextView) mainview.findViewById(R.id.left);
//		righttx = (TextView) mainview.findViewById(R.id.right);
//		titletx.setText(title);
//		messagetx.setText(msg);
//		lefttx.setText(left);
//		righttx.setText(right);
//		lefttx.setOnClickListener(leftclick);
//		righttx.setOnClickListener(rightclick);
//		pop = new PopupWindow(mainview, 300,
//				200);
//		pop.showAtLocation(((Activity)context).getWindow().getDecorView(),
//				Gravity.CENTER, 0, 0);
//		pop.setTouchable(true);
//		pop.setFocusable(true);
//		pop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
//		pop.setBackgroundDrawable(new BitmapDrawable());
//		lefttx.requestFocus();
	}
	
	/**
	 * ��һ�������
	 * @param context
	 * @param title
	 * @param iconId
	 * @param left
	 * @param right
	 */
	public static void showEditDialog(Context context, String title,
			int iconId, String left, OnClickListener leftclick, String right,
			OnClickListener rightclick) {
		newAlertDiag(context).setTitle(title)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(new EditText(context))
				.setPositiveButton(left, leftclick)
				.setNegativeButton(right, rightclick)
				.show();
	}
	
	/**
	 * ��ѡ��
	 * @param context
	 */
	public static void showMultiChoiceDialog(Context context, String title, int iconId,
			CharSequence[] items, boolean[] checkedItems,
			OnMultiChoiceClickListener listener, String left,
			OnClickListener leftclick, String right, OnClickListener rightclick) {

		newAlertDiag(context).setTitle(title).setIcon(iconId)
				.setMultiChoiceItems(items, checkedItems, listener)
				.setPositiveButton(left, leftclick)
				.setNegativeButton(right, rightclick)
				.show();

	}
	
	/**
	 * ��ѡ���
	 * @param context
	 * @param title
	 * @param iconId
	 * @param items
	 * @param checkedItem
	 * @param listener
	 * @param left
	 * @param leftclick
	 */
	public static void showSingleChoiceDialog(Context context, String title, int iconId,
			CharSequence[] items, int checkedItem, OnClickListener listener,
			String left, OnClickListener leftclick) {
		newAlertDiag(context).setTitle(title).setIcon(iconId)
				.setSingleChoiceItems(items, 0, listener)
				.setNegativeButton(left, leftclick).show();

	}
	
	/**
	 * ���б��
	 * @param context
	 * @param title
	 * @param iconId
	 * @param items
	 * @param checkedItem
	 * @param listener
	 * @param left
	 * @param leftclick
	 */
	public static void showListDialog(Context context, String title,
			int iconId, CharSequence[] items, int checkedItem,
			OnClickListener listener, String left, OnClickListener leftclick) {
		newAlertDiag(context).setTitle(title).setIcon(iconId)
				.setItems(items, listener).setNegativeButton(left, leftclick)
				.show();
	}
	
	/**
	 * ���Զ��岼�ֵ�
	 * @param context
	 * @param title
	 * @param iconId
	 * @param parent
	 * @param layout
	 * @param left
	 * @param leftclick
	 * @param right
	 * @param rightclick
	 */
	public static void showCustomDialog(Context context, String title,
			int iconId, ViewGroup parent, int layout, String left,
			OnClickListener leftclick, String right, OnClickListener rightclick) {
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		View view = inflater.inflate(layout, parent);
		newAlertDiag(context).setTitle(title).setIcon(iconId).setView(view)
				.setPositiveButton(left, leftclick)
				.setNegativeButton(right, rightclick).show();
	}
	
	public static Builder newAlertDiag(Context context){
		Context parent = getValidParent(context);		
		return new AlertDialog.Builder(parent);
	}
	
	public static ProgressDialog newProgressDialog(Context context){
		Context parent = getValidParent(context);
		return new ProgressDialog(parent);
	}
	
	public static ProgressDialog showProgressDialog(Context context,
			CharSequence title, CharSequence message) {
		Context parent = getValidParent(context);
		return ProgressDialog.show(parent, title, message, false);
	}

	public static ProgressDialog showProgressDialog(Context context, CharSequence title,
			CharSequence message, boolean indeterminate) {
		Context parent = getValidParent(context);
		return ProgressDialog.show(parent, title, message, indeterminate,
				false, null);
	}

	public static ProgressDialog showProgressDialog(Context context, CharSequence title,
			CharSequence message, boolean indeterminate, boolean cancelable) {
		Context parent = getValidParent(context);
		return ProgressDialog.show(parent, title, message, indeterminate,
				cancelable, null);
	}

	public static ProgressDialog showProgressDialog(Context context, CharSequence title,
			CharSequence message, boolean indeterminate, boolean cancelable,
			OnCancelListener cancelListener) {
		Context parent = getValidParent(context);
		return ProgressDialog.show(parent, title, message, indeterminate,
				cancelable, cancelListener);
	}
	
	
	
}
