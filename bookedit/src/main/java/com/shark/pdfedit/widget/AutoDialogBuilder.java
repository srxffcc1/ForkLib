package com.shark.pdfedit.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.shark.pdfedit.R;


public class AutoDialogBuilder {
	private View view;
	private Activity context;
	private ViewGroup.LayoutParams params;
	private Dialog dialog;

	/**
	 *
	 * @param context
	 * @param view 提供两个id为buttontrue 和 buttonfalse 的按钮
     * @param params
     */
	public AutoDialogBuilder(Activity context,View view, ViewGroup.LayoutParams params) {
		this.view = view;
		this.params = params;
		this.context=context;
	}
	

	public Dialog show(){

		dialog=new Dialog(context, R.style.Theme_Transparent);
		dialog.setContentView(view, params);
		Window window = dialog.getWindow();
		// 设置显示动画
		window.setWindowAnimations(R.style.main_menu_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		window.setGravity(Gravity.CENTER_VERTICAL);
		wl.x = 0;
		wl.y = 0;
		// 以下这两句是为了保证按钮可以水平满屏

		// 设置显示位置
		dialog.onWindowAttributesChanged(wl);
		dialog.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0)
	            {
	             return true;
	            }
	            else
	            {
	             return false;
	            }
			}
		});
		dialog.show();
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}
	public Dialog getdialog(){
		dialog=new Dialog(context, R.style.Theme_Transparent);
		dialog.setContentView(view, params);
		Window window = dialog.getWindow();
		// 设置显示动画
		window.setWindowAnimations(R.style.main_menu_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		window.setGravity(Gravity.CENTER_VERTICAL);
		wl.x = 0;
		wl.y = 0;
		// 以下这两句是为了保证按钮可以水平满屏

		// 设置显示位置
		dialog.onWindowAttributesChanged(wl);
//		dialog.setOnKeyListener(new OnKeyListener() {
//			
//			@Override
//			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//				// TODO Auto-generated method stub
//				if (keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0)
//	            {
//	             return true;
//	            }
//	            else
//	            {
//	             return false;
//	            }
//			}
//		});
//		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}
	public AutoDialogBuilder setPositiveButton(View text, final OnClickListener listener) {
		text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(listener!=null){
					listener.onClick(v);
				}
				dialog.dismiss();

			}
		});
		return this;
	}
	public AutoDialogBuilder setNegativeButton(View text, final OnClickListener listener) {
		text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(listener!=null){
					listener.onClick(v);
				}
				dialog.dismiss();

			}
		});
		return this;
	}

	public AutoDialogBuilder setPositiveButton(final OnClickListener listener) {
		view.findViewById(R.id.buttontrue).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(listener!=null){
					listener.onClick(v);
				}
				dialog.dismiss();
				
			}
		});
		return this;
	}

	public AutoDialogBuilder setNegativeButton(final OnClickListener listener) {
		view.findViewById(R.id.buttonfalse).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(listener!=null){
					listener.onClick(v);
				}
				
				dialog.dismiss();
				
			}
		});
		return this;
	}

}
