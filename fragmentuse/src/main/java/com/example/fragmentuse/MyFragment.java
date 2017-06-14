package com.example.fragmentuse;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2017/5/14.
 */

public class MyFragment extends Fragment {
    int mResultCode= Activity.RESULT_OK;
    int mRequestCode;
    Intent mResultData;
    public final void setResult(int resultCode, Intent data) {
        synchronized (this) {
            mResultCode = resultCode;
            mResultData = data;
        }
    }
    public final void setResult(int resultCode) {
        synchronized (this) {
            mResultCode = resultCode;
            mResultData = null;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment,container,false);;
        view.findViewById(R.id.fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyFragment.this.finish();
            }
        });
        return view;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            Class clazz = getActivity().getClass();
            Method method = clazz.getDeclaredMethod("onActivityResult",int.class,int.class, Intent.class);
            method.setAccessible(true);
            method.invoke(getActivity(),mRequestCode,mResultCode,mResultData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public final void finish(){
        getActivity().onBackPressed();
    }

}
