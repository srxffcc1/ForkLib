package com.yarolegovich.slidingrootnav.transform.menu;

import android.view.View;

import java.util.List;

/**
 * Created by yarolegovich on 25.03.2017.
 */

public class CompositeMenuTransformation implements MenuTransformation {

    private List<MenuTransformation> transformations;

    public CompositeMenuTransformation(List<MenuTransformation> transformations) {
        this.transformations = transformations;
    }

    @Override
    public void transform(float dragProgress, View rootView) {
        for (MenuTransformation t : transformations) {
            t.transform(dragProgress, rootView);
        }
    }
}
