package com.shark.pdfedit.widget;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;

/**
 * 自适应的选择框
 */
public class AutoCheckBox extends ImageView implements Checkable,IAutoCheck{
	private OnCheckedChangeListener mOnCheckedChangeListener;
	private OnWidgetCheckedChangeListener monWidgetCheckedChangeListener;
    private boolean mBroadcasting;
	public AutoCheckBox(Context context) {
		this(context, null);

		// TODO Auto-generated constructor stub
	}
	public AutoCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setScaleType(ScaleType.FIT_CENTER);
		// TODO Auto-generated constructor stub
	}

	public AutoCheckBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.setScaleType(ScaleType.FIT_CENTER);
	}

	@SuppressLint("NewApi")
	public AutoCheckBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		this.setScaleType(ScaleType.FIT_CENTER);
	}

	private boolean mChecked;
	private static final int[] CHECKED_STATE_SET = { android.R.attr.state_checked };
	@Override
	public int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
		if (isChecked()) {
			mergeDrawableStates(drawableState, CHECKED_STATE_SET);
		}
		return drawableState;
	}
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		AutoCheckBox.this.setClickable(true);
	}
	@Override
	public void setChecked(boolean checked) {
		if (mChecked != checked) {
			mChecked = checked;
			refreshDrawableState();
		}
		
		if (mBroadcasting) {
            return;
        }
		
		mBroadcasting = true;
		if(mOnCheckedChangeListener!=null){
			
			mOnCheckedChangeListener.onCheckedChanged(AutoCheckBox.this,mChecked);
		}
		if(monWidgetCheckedChangeListener !=null){
			monWidgetCheckedChangeListener.onCheckedChanged(AutoCheckBox.this,mChecked);
		}
		mBroadcasting = false;
	}
	public void setCheckedOnlyDraw(boolean checked) {
		if (mChecked != checked) {
			mChecked = checked;
			refreshDrawableState();
		}

		if (mBroadcasting) {
			return;
		}

		mBroadcasting = true;
//		if(mOnCheckedChangeListener!=null){
//
//			mOnCheckedChangeListener.onCheckedChanged(AutoCheckBox.this,mChecked);
//		}
//		if(monWidgetCheckedChangeListener !=null){
//			monWidgetCheckedChangeListener.onCheckedChanged(AutoCheckBox.this,mChecked);
//		}
		mBroadcasting = false;
	}
	@Override
	public boolean isChecked() {
		return mChecked;
	}
	@Override
	public void toggle() {
		setChecked(!mChecked);
		
	}
    @Override
    public boolean performClick() {
    	if(this.isEnabled()){
    		toggle();
    	}
        return true;
    }
    @Override
    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener){
    	
    	mOnCheckedChangeListener=onCheckedChangeListener;
    }
    public void setOnWidgetCheckedChangeListener(OnWidgetCheckedChangeListener monWidgetCheckedChangeListener){
    	
    	this.monWidgetCheckedChangeListener = monWidgetCheckedChangeListener;
    }

}