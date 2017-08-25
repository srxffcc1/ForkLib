package com.shark.pdfedit.utils;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.kymjs.common.ViewUtils;

import java.util.List;

/**
 * Created by King6rf on 2017/8/25.
 */

public class TextColorUtil {
    public static void fixTextColor(View view){
        List<View> viewlist= ViewUtils.getAllChildViews(view);
        for (int i = 0; i < viewlist.size(); i++) {
            View viewtmp=viewlist.get(i);
            if(viewtmp instanceof TextView){
                TextView textview= (TextView) viewtmp;
                textview.setTextColor(Color.parseColor("#000000"));
            }
        }
    }
}
