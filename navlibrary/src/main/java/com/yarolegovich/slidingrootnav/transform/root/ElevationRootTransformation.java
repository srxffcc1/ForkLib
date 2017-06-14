package com.yarolegovich.slidingrootnav.transform.root;

import android.view.View;

/**
 * Created by yarolegovich on 25.03.2017.
 */

public class ElevationRootTransformation implements RootTransformation {

    private static final float START_ELEVATION = 0f;

    private final float endElevation;

    public ElevationRootTransformation(float endElevation) {
        this.endElevation = endElevation;
    }

    @Override
    public void transform(float dragProgress, View rootView) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            float elevation = SideNavUtils.evaluate(dragProgress, START_ELEVATION, endElevation);
//            rootView.setElevation(elevation);
//        }
    }
}
