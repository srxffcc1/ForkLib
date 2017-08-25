package com.shark.pdfedit.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kymjs.common.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 包装类 给每个控件设置点击
 */
public class PassLinearLayout extends LinearLayout {
	List<View> childlist = new ArrayList<View>();

	public PassLinearLayout(Context context) {
		super(context);
	}

	public PassLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PassLinearLayout(Context context,  AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@SuppressLint("NewApi")
	public PassLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		this.setClickable(true);
		childlist = ViewUtils.getAllChildViews(PassLinearLayout.this);
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		super.addView(child, index, params);

	}

	@Override
	public boolean performClick() {
		super.performClick();
		for (int i = 0; i < childlist.size(); i++) {
			if (childlist.get(i) instanceof AutoCheckBox) {
				childlist.get(i).performClick();
				
			}
		}
		return false;

	}
	

	public boolean isChecked() {
		boolean flag = false;
		for (int i = 0; i < childlist.size(); i++) {
			if (childlist.get(i) instanceof AutoCheckBox) {
				flag = ((AutoCheckBox) childlist.get(i)).isChecked();
			}
		}
		return flag;
	}
	public String getText() {
		String result = "";
		for (int i = 0; i < childlist.size(); i++) {
			if (childlist.get(i) instanceof TextView) {
				if (((TextView) childlist.get(i)).getText() != null
						&& !((TextView) childlist.get(i)).getText().equals("")) {
					result = ((TextView) childlist.get(i)).getText().toString();
				}
			}
		}
		return result;
	}
}
