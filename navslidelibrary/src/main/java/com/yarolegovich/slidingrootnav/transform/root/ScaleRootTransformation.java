package com.yarolegovich.slidingrootnav.transform.root;

import android.view.View;

import com.yarolegovich.slidingrootnav.util.SideNavUtils;

/**
 * Created by yarolegovich on 25.03.2017.
 */

public class ScaleRootTransformation implements RootTransformation {

    private static final float START_SCALE = 1f;

    private final float endScale;

    public ScaleRootTransformation(float endScale) {
        this.endScale = endScale;
    }

    @Override
    public void transform(float dragProgress, View rootView) {
        float scale = SideNavUtils.evaluate(dragProgress, START_SCALE, endScale);

        rootView.setPivotX(0);
        rootView.setScaleX(scale);
        rootView.setScaleY(scale);
    }
}
