package vlc.video;


import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.vlcm.vlcvideo.R;

import vlc.VlcVideoView;
import vlc.util.VLCInstance;


/**
 */
public class VideoFragment extends Fragment implements View.OnClickListener {
    // String path = "http://img1.peiyinxiu.com/2014121211339c64b7fb09742e2c.mp4";
    public static   String path = "http://yoyo-v-out.oss-cn-hangzhou.aliyuncs.com/012e47d2eb1e49a7bd1a27e6c0f37d9f/act-m3u8-segment/022a79ae-9bc7-7e1c-3926-e9c87b360f81.mp4";
    // String path = "rtsp://video.fjtu.com.cn/vs01/flws/flws_01.rm";
    //String path = "rtsp://139.199.159.71:554/easypusher_rtsp.sdp";
    //  @Bind(R.id.player)
    VlcVideoView vlcVideoView;
    //  @Bind(R.id.info)
    TextView logInfo;

    String tag = "VlcVideoFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (VLCInstance.testCompatibleCPU(this.getActivity())) {
            Log.i(tag, "支持 x86  armv7");
        } else {
            Log.i(tag, "什么破手机  不支持");
        }
        View view = inflater.inflate(R.layout.video_modulefragment, container, false);
        //为啥这么多人用不会用butterknife 要我注掉
        // ButterKnife.bind(this, view);
        vlcVideoView = (VlcVideoView) view.findViewById(R.id.modulevlcplayer);
        logInfo = (TextView) view.findViewById(R.id.moduleinfo);
        view.findViewById(R.id.changevideo).setOnClickListener(this);
//        Media media = new Media(VLCInstance.get(getContext()), Uri.parse(path));
//        media.setHWDecoderEnabled(false, false);
//        vlcVideoView.setMedia(media);

        vlcVideoView.setMediaListenerEvent(new MediaControl(vlcVideoView, logInfo));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(tag, "---------   start   ----------------");
                vlcVideoView.startPlay(path);
            }
        }, 1000);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        vlcVideoView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vlcVideoView.onDestory();
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    public boolean isFullscreen;

    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.changevideo) {
            isFullscreen = !isFullscreen;
            if (isFullscreen) {
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            } else {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

        }
    }
}
