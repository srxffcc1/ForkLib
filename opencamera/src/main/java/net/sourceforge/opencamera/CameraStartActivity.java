package net.sourceforge.opencamera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2017/8/3.
 * 中间类 用来快速进入相机界面
 */

public class CameraStartActivity extends Activity {
    int request=2001;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opsplash_layout);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivityForResult(new Intent(CameraStartActivity.this,OpMainActivity.class),request);
            }
        },100);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setResult(RESULT_OK,data);
        finish();
    }
}
