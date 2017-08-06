package com.yarolegovich.slidingrootnav.transform.root;

import android.view.View;

import java.util.List;

/**
 * Created by yarolegovich on 25.03.2017.
 */

public class CompositeRootTransformation implements RootTransformation {

    private List<RootTransformation> transformations;

    public CompositeRootTransformation(List<RootTransformation> transformations) {
        this.transformations = transformations;
    }

    @Override
    public void transform(float dragProgress, View rootView) {
        for (RootTransformation t : transformations) {
            t.transform(dragProgress, rootView);
        }
    }
}
