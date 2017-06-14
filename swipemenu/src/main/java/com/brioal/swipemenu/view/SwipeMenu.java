package com.brioal.swipemenu.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.brioal.swipemenu.R;
import com.brioal.swipemenu.interfaces.onSwipeProgressListener;
import com.brioal.swipemenu.util.SizeUtil;

/**
 * 侧滑菜单的实现
 * Created by Brioal on 2016/7/31.
 */

public class SwipeMenu extends ViewGroup {

    private float xIntercept = 0;
    private float yIntercept = 0;
    private float xLast = 0;
    private float yLast = 0;
    private Scroller mScroller;
    private int mScreenWidth;
    private int mScreenHeight;
    private View mMenuView;
    private View mContentView;
    private int mType;
    private int mDragWipeOffset; //侧边拖动的偏移值
    private int mMenuOffset; //菜单距右边的距离
    private boolean isMeasured = false; //是否已经测量过
    private boolean isMenuShowing = false; //是否已经显示了菜单

    private int mTransInt = 1; //移动选项 1.固定不动 2.跟随移动 3.视差移动
    private int mScaleInt = 2;//缩放选项 1.无缩放动画 2.缩放动画
    private int mScaleInt2 = 2;//缩放选项 1.无缩放动画 2.缩放动画 content的缩放
    private int mAlphaInt = 1;//透明选项 1.无透明效果 2.透明动画
    private int mRotateInt = 1;//旋转选项 1.无旋转动画 2.中心旋转 3.左3D旋转 4.右3D旋转

    private float mStartScale; //起始缩放值 0~0.8
    private float mStartScale2; //起始缩放值 0~0.8
    private float mStartAlpha; //起始透明度 0~0.8
    private int mStart3DAngle; //最小旋转角度 ,只对3D有用 0~80
    private boolean isFullScreenSwipe = true; //是否全屏滑动
    private ImageView mBackImageView; //设置动态模糊时候的背景
    private View statusView = null;
    private boolean isTranslate = false; //是否透明
    private boolean isTranslated = false; //是否已经设置过透明
    private onSwipeProgressListener mListener; //滑动监听
    private boolean canscroll = true;

    public SwipeMenu(Context context) {
        this(context, null);
    }

    public SwipeMenu(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        initObtainStyledAttr(context, attrs);
        init(context);

    }

    @SuppressLint("NewApi")
    public SwipeMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initObtainStyledAttr(context, attrs);
        init(context);


    }

    @SuppressLint("NewApi")
    public SwipeMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
        initObtainStyledAttr(context, attrs);
        init(context);

    }

    //设置动画效果代码
    public void setStyleCode(int type) {
        try {
            mType = type;
            char[] ints = (mType + "").toCharArray();
            mTransInt = ints[0] - '0';
            mScaleInt = ints[1] - '0';
            mScaleInt2= ints[2] - '0';
            mAlphaInt = ints[3] - '0';
            mRotateInt = ints[4] - '0';

            mMenuView = getChildAt(0);
            if (mMenuView != null) {
                mMenuView.setScaleX(1);
                mMenuView.setScaleY(1);
                mMenuView.setTranslationX(0);
                mMenuView.setRotationX(0);
                mMenuView.setRotationY(0);
                mMenuView.setRotationX(0);
                mMenuView.setAlpha(1);
            }
        } catch (Exception e) {
            Log.e("SwipeMenu", "动画代码设置出错,请检查范围");
        }

    }

    public boolean isCanscroll() {
        return canscroll;
    }

    public void setCanscroll(boolean canscroll) {
        this.canscroll = canscroll;
    }

    //设置拉出菜单距离右边界的距离
    public void setMenuOffset(int menuOffset) {
        this.mMenuOffset = menuOffset;
    }

    //设置触发滑动的范围,为0则是全屏
    public void setDragWipeOffset(int dragWipeOffset) {
        this.mDragWipeOffset = dragWipeOffset;
    }

    //设置起始缩放
    public void setStartScale(float minScale) {
        mStartScale = minScale;
    }

    //设置起始透明度
    public void setStartAlpha(float startAlpha) {
        mStartAlpha = startAlpha;
    }

    //设置起始3D旋转角度
    public void setStart3DAngle(int start3DAngle) {
        mStart3DAngle = start3DAngle;
    }

    //设置滑动监听
    public void setOnMenuShowingListener(onSwipeProgressListener listener) {
        mListener = listener;
    }

    //改变全局整体颜色
    public void changeAllColor(int color) throws Exception {
        if (statusView != null) {
            setBackgroundColor(color);
            statusView.setBackgroundColor(color);
        } else {
            throw new Exception("you must call the setBackImage method first");
        }
    }

    //是否显示菜单
    public boolean isMenuShowing() {
        if (getScrollX() <= 0) {
            isMenuShowing = true;
        } else {
            isMenuShowing = false;
        }
        return isMenuShowing;
    }

    //显示菜单
    public void showMenu() {
        mScroller.startScroll(getScrollX(), 0, 0 - getScrollX(), 0);
        invalidate();
    }

    //显示内容
    public void hideMenu() {
        mScroller.startScroll(getScrollX(), 0, mScreenWidth - mMenuOffset - getScrollX(), 0);
        invalidate();
    }

    //从资源文件获取数据
    private void initObtainStyledAttr(Context context, AttributeSet attrs) {

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SwipeMenu);
        mType = array.getInteger(R.styleable.SwipeMenu_sm_type, 12211);
        mDragWipeOffset = (int) array.getDimension(R.styleable.SwipeMenu_sm_dragoffset, SizeUtil.Dp2Px(context, 100));
        mMenuOffset = (int) array.getDimension(R.styleable.SwipeMenu_sm_menuoffset, SizeUtil.Dp2Px(context, 100));
        mStartScale = array.getFloat(R.styleable.SwipeMenu_sm_startscale, 0.8f);
        mStartScale2 = array.getFloat(R.styleable.SwipeMenu_sm_startscale2, 0.8f);
        mStartAlpha = array.getFloat(R.styleable.SwipeMenu_sm_startalpha, 0.2f);
        mStart3DAngle = array.getInteger(R.styleable.SwipeMenu_sm_start3dangle, 60);
        setStyleCode(mType);
        array.recycle();
    }

    private View setColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 生成一个状态栏大小的矩形
            // 添加 statusView 到布局中
            View statusView = createStatusBarView(activity, color);
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            if (decorView.getChildAt(decorView.getChildCount() - 1).getId() != Integer.valueOf(1)) {
                decorView.addView(statusView, decorView.getChildCount());
            }
            // 设置根布局的参数
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
            return statusView;
        }
        return null;
    }

    private View createStatusBarView(Activity activity, int color) {
        // 绘制一个和状态栏一样高的矩形
        View statusBarView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                getStatusBarHeight(activity));
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(color);
        statusBarView.setId(Integer.valueOf(1));
        return statusBarView;
    }

    private int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int height = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        height = context.getResources().getDimensionPixelSize(resourceId);
        return height;
    }

    //添加背景图获取屏幕宽高
    private void init(Context context) {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
        mScroller = new Scroller(context);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch(action){
            case MotionEvent.ACTION_DOWN:
                super.dispatchTouchEvent(ev);
                return true;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        boolean intercept = false;

        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                intercept = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float xDelta = x - xIntercept;
                float yDelta = y - yIntercept;
                if (mDragWipeOffset == 0 && Math.abs(xDelta) > 20) { //全屏滑动
                    intercept = true;
                    break;
                }
                if (!isMenuShowing()) {
                    if (x >= SizeUtil.Dp2Px(getContext(), mDragWipeOffset)) {
                        return false;
                    }
                }
                if (x + getScrollX() < mScreenWidth + mDragWipeOffset) {
                    if (Math.abs(xDelta) > Math.abs(yDelta) && Math.abs(xDelta) > 20) { //X滑动主导

                        intercept = true;
                    } else {
                        intercept = false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                intercept = false;
                break;
        }
        xLast = x;
        yLast = y;
        xIntercept = x;
        yIntercept = y;
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float xDelta = x - xLast;
                float offset = xDelta;
                if (canscroll) {
                    touchMove_deal(offset);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (canscroll) {
                    touchUp_deal();
                }

                break;
        }
        xLast = x;
        yLast = y;
        return false;
    }

    private void dealScroll() {
        //最小缩放值
        float progress = 1 - getScrollX() * 1.0f / (mScreenWidth - mMenuOffset);
        //移动动画处理
        switch (mTransInt) {
            case 1:
                mMenuView.setTranslationX(getScrollX());
                break;
            case 2:
                break;
            case 3:
                mMenuView.setTranslationX(getScrollX() * progress);
                break;
        }
        //状态栏跟随内容区域滑动
        if (isTranslate) {
            statusView.setTranslationX(progress * (mScreenWidth - mMenuOffset));
        }
        //缩放动画处理
        if (mScaleInt2 == 2) {
            mContentView.setPivotX(0);
            mContentView.setPivotY(mContentView.getHeight() / 2);
            mContentView.setScaleX((mStartScale2) + (1 - progress) * (1 - mStartScale2));
            mContentView.setScaleY((mStartScale2) + (1 - progress) * (1 - mStartScale2));
        }
        //缩放动画处理
        if (mScaleInt == 2) {
            mMenuView.setAlpha(0.0f + 1.0f * (progress));
            mMenuView.setPivotX(0);
            mMenuView.setScaleX(progress * (1 - mStartScale) + (mStartScale));
            mMenuView.setScaleY(progress * (1 - mStartScale) + (mStartScale));
        }
        //透明动画处理
        if (mAlphaInt == 2) {
            mMenuView.setAlpha(mStartAlpha + (1 - mStartAlpha) * progress);
        }
        //旋转动画处理
        switch (mRotateInt) {
            case 2: //中心
                mMenuView.setPivotX(mMenuView.getWidth() / 2);
                mMenuView.setPivotY(mMenuView.getHeight() / 2);
                mMenuView.setRotation(progress * 360);

                break;
            case 3: //3D左侧翻转
                mMenuView.setPivotX(0);
                mMenuView.setPivotY(mMenuView.getHeight() / 2);
                mMenuView.setRotationY(progress * -(90 - mStart3DAngle) + (90 - mStart3DAngle));
                break;
            case 4: //x翻转
                mMenuView.setPivotX(0);
                mMenuView.setPivotY(0);
                mMenuView.setRotation(progress * -90 + 90);
                break;
            case 5://左上角
                mMenuView.setPivotX(mMenuView.getWidth() / 2);
                mMenuView.setPivotY(mMenuView.getHeight() / 2);
                mMenuView.setRotationX(progress * -(45) + 45);
                break;
            case 6://左下角
                mMenuView.setPivotX(0);
                mMenuView.setPivotY(mMenuView.getHeight());
                mMenuView.setRotation(progress * +90 + -90);
                break;
        }
        //渐变状态栏
        if (mListener != null) {//进度监听
            mListener.onProgressChange(progress);
        }
        //设置动态模糊
        if (mBackImageView != null) {
            mBackImageView.setAlpha(progress);
        }
    }

    //滑动处理
    private void touchMove_deal(float offset) {
        if (getScrollX() - offset <= 0) {
            offset = 0;
        } else if (getScrollX() + mScreenWidth - offset > mScreenWidth * 2 - mMenuOffset) {
            offset = 0;
        }
        scrollBy((int) (-offset), 0); //跟随拖动
        dealScroll();

    }

    //抬起处理
    private void touchUp_deal() {
        if (getScrollX() < (mScreenWidth - mMenuOffset) / 2) {
            //滑动菜单
            showMenu();
        } else {
            //滑动到内容
            hideMenu();
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            dealScroll();
            postInvalidate();
        }
        super.computeScroll();
    }

    int actionstatus = -1;

    public void checkActionBar(Context activity) {
        try {
            this.actionstatus = ((Activity) activity).getActionBar().isShowing() ? 1 : 0;
        } catch (Exception e) {
            try {
                this.actionstatus = ((AppCompatActivity) activity).getSupportActionBar().isShowing() ? 1 : 0;
            } catch (Exception e1) {
                this.actionstatus = 0;
            }

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {//解决例如fragment不显示的bug
        if (!isMeasured) {
            mMenuView = getChildAt(0);
            mContentView = getChildAt(1);
            mMenuView.getLayoutParams().width = mScreenWidth - mMenuOffset;
            mMenuView.getLayoutParams().height = mScreenHeight - getStatusBarHeight(getContext()) - getActionBarHeight();
            mContentView.getLayoutParams().width = mScreenWidth;
            mContentView.getLayoutParams().height = mScreenHeight - getStatusBarHeight(getContext()) - getActionBarHeight();
            isMeasured = true;
        }
        measureChild(mMenuView, widthMeasureSpec, heightMeasureSpec);
        measureChild(mContentView, widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mScreenWidth * 2 - mMenuOffset, mScreenHeight);
    }

    private int getActionBarHeight() {
        if (actionstatus == -1) {
            try {
                checkActionBar((Activity) getContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int actionBarHeight = 0;
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        System.out.println();
        final TypedValue tv = new TypedValue();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if ((getContext()).getTheme()
                    .resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(
                        tv.data, getResources().getDisplayMetrics());
            }
        } else {
            // 使用android.support.v7.appcompat包做actionbar兼容的情况
            if ((getContext()).getTheme()
                    .resolveAttribute(
                            android.support.v7.appcompat.R.attr.actionBarSize,
                            tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(
                        tv.data, getResources().getDisplayMetrics());
            }
        }
        return (actionstatus == 1) ? actionBarHeight : 0;
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {//解决例如fragment不显示的bug
        mMenuView.layout(0, 0, mScreenWidth - mMenuOffset, mScreenHeight - getStatusBarHeight(getContext()) - getActionBarHeight());
        mContentView.layout(mScreenWidth - mMenuOffset, 0, mScreenWidth - mMenuOffset + mScreenWidth, mScreenHeight - getStatusBarHeight(getContext()) - getActionBarHeight());
        if (b) {
            mContentView.setClickable(true);
            mMenuView.setClickable(true);
            mMenuView.setBackgroundColor(Color.TRANSPARENT);
            scrollTo(mScreenWidth - mMenuOffset, 0);
        }

    }

}
