package com.shark.pdfedit.statich;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by King6rf on 2017/8/20.
 */

public class BookStatic {
    private int px;
    private int py;
    private Context mcontext;
    private static final BookStatic instance=new BookStatic();
    public static BookStatic getInstance(){
        return instance;
    }
    public void init(Context context) {
        mcontext = context;
        Point point = new Point();
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getRealSize(point);
        manager.getDefaultDisplay().getMetrics(dm);
//		double dpi = dm.densityDpi;
//		double x = Math.pow(point.x / dm.xdpi, 2);
//		double y = Math.pow(point.y / dm.ydpi, 2);
//		double screenInches = Math.sqrt(x + y);
        px = point.x;
        py = point.y;
    }
    public double getScreenWidth() {
        return px;
    }

    public double getScreenHeight() {
        return py;
    }
}
