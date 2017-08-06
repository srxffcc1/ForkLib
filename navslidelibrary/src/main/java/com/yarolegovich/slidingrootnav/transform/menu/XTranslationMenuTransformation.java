package com.yarolegovich.slidingrootnav.transform.menu;

import android.view.View;

/**
 * Created by yarolegovich on 25.03.2017.
 */

public class XTranslationMenuTransformation implements MenuTransformation {

    private static final float START_TRANSLATION = 0f;

    private final float endTranslation;

    public XTranslationMenuTransformation(float endTranslation) {
        this.endTranslation = endTranslation;
    }

    @Override
    public void transform(float dragProgress, View rootView) {
//        float translation = SideNavUtils.evaluate(dragProgress, START_TRANSLATION, endTranslation);

        rootView.setTranslationX(dragProgress);
    }
}
