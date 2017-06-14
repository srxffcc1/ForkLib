package com.example.nav;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nineoldandroids.view.ViewHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

       final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerClosed(View v) {

            }

            @Override
            public void onDrawerOpened(View v) {

            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // 主体窗口
                View mainFrame = drawer.getChildAt(0);

                // 这个就是隐藏起来的边侧滑菜单栏
                View leftMenu = drawerView;

                addQQStyleSlide(mainFrame, leftMenu, slideOffset);
            }

            @Override
            public void onDrawerStateChanged(int arg0) {

            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    //此处将控制NavigationView侧滑出的高度、宽度已经重心位置（居中？靠上？靠下？）
    private void setNavigationViewSize(NavigationView nv, float w_percent, float h_percent) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        //宽度默认是MATCH_PARENT，
        //NavigationView的宽度
        int width = (int) (displayMetrics.widthPixels * w_percent);

        //NavigationView的高度
        int height = (int) (displayMetrics.heightPixels * h_percent);

        //高度默认是MATCH_PARENT，如果不打算打满屏幕高度：DrawerLayout.LayoutParams.MATCH_PARENT，
        // 那么比如可以设置成屏幕高度的80%(即0.8f)
        DrawerLayout.LayoutParams params = new DrawerLayout.LayoutParams(width, height);

        //主要要设置center，否则侧滑出来的菜单栏将从下往上绘制相应高度和宽度而不是居中
        params.gravity = Gravity.START | Gravity.CENTER_VERTICAL;

        nv.setLayoutParams(params);
    }

    // 实现边侧滑的核心代码
    private void addQQStyleSlide(View mainFrame, View leftMenu, float slideOffset) {
        //GAP的值决定左边侧滑出来的宽度和右边的主界面之间在侧滑过程以及侧滑结束后的间距。
        //如果不设置此值或者设置为0，则将恢复成Android系统默认的样式，即侧滑出来的界面和主界面之间紧密贴在一起。
        int GAP = 0;

        float leftScale = 0.5f + 0.5f * slideOffset;
        float rightScale = 1 - 0.2f * slideOffset;

        ViewHelper.setScaleX(leftMenu, leftScale);
        ViewHelper.setScaleY(leftMenu, leftScale);
        ViewHelper.setAlpha(leftMenu, 0.5f + 0.5f * slideOffset);
        ViewHelper.setTranslationX(mainFrame, (leftMenu.getMeasuredWidth() + GAP) * slideOffset);
        ViewHelper.setPivotX(mainFrame, 0);
        ViewHelper.setPivotY(mainFrame, mainFrame.getMeasuredHeight() / 2);
        mainFrame.invalidate();
        ViewHelper.setScaleX(mainFrame, rightScale);
        ViewHelper.setScaleY(mainFrame, rightScale);

        // 该处主要是为了使背景的颜色渐变过渡。
        // 如果失效，则可能是因为Android DrawerLayout的NavigationView绘制背景的图层互相之间遮盖导致。
        //此处不关乎重点实现，作为代码在未来的复用，仍然保留，当然也可以删掉！
//        getWindow().getDecorView().getBackground().setColorFilter(evaluate(slideOffset, Color.BLACK, Color.TRANSPARENT),
//                PorterDuff.Mode.SRC_OVER);
    }

    private Integer evaluate(float fraction, Object startValue, Integer endValue) {
        int startInt = (Integer) startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;
        int endInt = (Integer) endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;
        return (int) ((startA + (int) (fraction * (endA - startA))) << 24)
                | (int) ((startR + (int) (fraction * (endR - startR))) << 16)
                | (int) ((startG + (int) (fraction * (endG - startG))) << 8)
                | (int) ((startB + (int) (fraction * (endB - startB))));
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
