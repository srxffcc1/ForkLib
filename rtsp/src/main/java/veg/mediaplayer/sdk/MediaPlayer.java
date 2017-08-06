package veg.mediaplayer.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback2;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import veg.mediaplayer.sdk.M3U8.HLSStream;
import veg.mediaplayer.sdk.SystemUtils.WaitNotify;

public class MediaPlayer extends FrameLayout implements Callback2, FrameCallback, OnTouchListener, SensorEventListener {
    private static int MAX_VIDEOSHOT_SIZE = 8294400;
    private static final String MEDIA_PLAYER_LIB_VERSION = "5.1.20170112";
    private static final String TAG = "MediaPlayer";
    private static int decoderSearch = 0;
    public MediaPlayerCallback Callback = null;
    public MediaPlayerCallbackSubtitle CallbackSubtitle = null;
    protected int abrCurrentPlayedStreamId = 0;
    private HLSThread abrGetHLSStreamsThread = null;
    protected List<HLSStream> abrHLSStreams = null;
    protected int abrPreviousPlayedStreamId = 0;
    protected int abrSetPlayedStreamId = 0;
    private FrameLayout barFrameLayout = null;
    private Charset charset = Charset.forName("UTF-8");
    private int count = 0;
    private CharsetDecoder decoder = this.charset.newDecoder();
    private CharsetEncoder encoder = this.charset.newEncoder();
    private float fps_estim = 0.0f;
    private int got_first_frame = 0;
    private int heightLayout = 0;
    private MediaCodec internalMediaDecoder = null;
    private MediaFormat internalMediaFormat = null;
    private int invalidating = 0;
    private boolean is_need_decrease = false;
    protected AudioTrack mAudioTrack;
    protected transient Context mContext;
    public boolean mHasFocus = true;
    protected float mHeight;
    public boolean mIS_WINDOW = true;
    protected boolean mIsExternalSurfaceTexture = false;
    public boolean mIsPaused = false;
    public boolean mIsSurfaceReady = false;
    protected Thread mPlayerThread = null;
    protected SensorManager mSensorManager;
    protected Surface mSurface = null;
    protected boolean mUseExternalSurface = false;
    private int mVideoHeight = 0;
    private int mVideoWidth = 0;
    protected float mWidth;
    protected long m_fade_time = 0;
    protected boolean m_internal_mute = false;
    private ByteBuffer mediaBuffer = null;
    private int mediaHeight = 0;
    private String mediaMime = "";
    private int mediaSize = 0;
    private int mediaWidth = 0;
    private ByteBuffer outbuff = null;
    private int pers_free_estim = 0;
    protected MediaPlayerConfig playerConfig = null;
    private int playerOrientation = 1;
    private MediaPlayerWorker playerWorker = null;
    private int prev_H = 0;
    private int prev_W = 0;
    protected int previousVsyncEnabe = 0;
    protected Queue<Integer> queueSurfaceCreate = new LinkedList();
    private long render_pos = 0;
    private Runnable runnableInformerHide = new Runnable() {
        public void run() {
            MediaPlayer.this.subtitleTextView.setText("");
            MediaPlayer.this.subtitleTextView.setVisibility(4);
        }
    };
    private Runnable runnableInformerHide_image = new Runnable() {
        public void run() {
            MediaPlayer.this.subtitleImageView.setVisibility(4);
            MediaPlayer.this.subtitleImageView.setImageBitmap(null);
        }
    };
    private int set_size_layout = 0;
    private VideoShot shotVideo = null;
    protected ImageView subtitleImageView = null;
    private TextView subtitleTextView = null;
    protected long time_cur = 0;
    private SurfaceView view = null;
    private boolean view_was_resized = false;
    protected WaitNotify waitGetHLSStreams = new WaitNotify();
    protected WaitNotify waitOpenMediaCodec = new WaitNotify();
    protected WaitNotify waitOpenSource = new WaitNotify();
    protected WaitNotify waitStartOpenThread = new WaitNotify();
    protected WaitNotify waitSurfaceCreated = new WaitNotify();
    private int widthLayout = 0;

    private class HLSThread extends Thread {
        public boolean HLS_thread_Cancel;
        public boolean HLS_thread_Working;

        private HLSThread() {
            this.HLS_thread_Working = false;
            this.HLS_thread_Cancel = false;
        }
    }

    public class BuffersState {
        private int audiodecoder_audiorenderer_filled;
        private int audiodecoder_audiorenderer_size;
        private int source_audiodecoder_filled;
        private int source_audiodecoder_size;
        private int source_videodecoder_filled;
        private int source_videodecoder_size;
        private int videodecoder_videorenderer_filled;
        private int videodecoder_videorenderer_size;

        private BuffersState() {
            this.source_videodecoder_filled = 0;
            this.source_videodecoder_size = 0;
            this.source_audiodecoder_filled = 0;
            this.source_audiodecoder_size = 0;
            this.videodecoder_videorenderer_filled = 0;
            this.videodecoder_videorenderer_size = 0;
            this.audiodecoder_audiorenderer_filled = 0;
            this.audiodecoder_audiorenderer_size = 0;
        }

        private BuffersState(int source_videodecoder_filled, int source_videodecoder_size, int videodecoder_videorenderer_filled, int videodecoder_videorenderer_size, int source_audiodecoder_filled, int source_audiodecoder_size, int audiodecoder_audiorenderer_filled, int audiodecoder_audiorenderer_size) {
            this.source_videodecoder_filled = 0;
            this.source_videodecoder_size = 0;
            this.source_audiodecoder_filled = 0;
            this.source_audiodecoder_size = 0;
            this.videodecoder_videorenderer_filled = 0;
            this.videodecoder_videorenderer_size = 0;
            this.audiodecoder_audiorenderer_filled = 0;
            this.audiodecoder_audiorenderer_size = 0;
            this.source_videodecoder_filled = source_videodecoder_filled;
            this.source_videodecoder_size = source_videodecoder_size;
            this.source_audiodecoder_filled = source_audiodecoder_filled;
            this.source_audiodecoder_size = source_audiodecoder_size;
            this.videodecoder_videorenderer_filled = videodecoder_videorenderer_filled;
            this.videodecoder_videorenderer_size = videodecoder_videorenderer_size;
            this.audiodecoder_audiorenderer_filled = audiodecoder_audiorenderer_filled;
            this.audiodecoder_audiorenderer_size = audiodecoder_audiorenderer_size;
        }

        public int getBufferSizeBetweenSourceAndVideoDecoder() {
            return this.source_videodecoder_size;
        }

        public int getBufferFilledSizeBetweenSourceAndVideoDecoder() {
            return this.source_videodecoder_filled;
        }

        public int getBufferSizeBetweenSourceAndAudioDecoder() {
            return this.source_audiodecoder_size;
        }

        public int getBufferFilledSizeBetweenSourceAndAudioDecoder() {
            return this.source_audiodecoder_filled;
        }

        public int getBufferSizeBetweenVideoDecoderAndVideoRenderer() {
            return this.videodecoder_videorenderer_size;
        }

        public int getBufferFilledSizeBetweenVideoDecoderAndVideoRenderer() {
            return this.videodecoder_videorenderer_filled;
        }

        public int getBufferSizeBetweenAudioDecoderAndAudioRenderer() {
            return this.audiodecoder_audiorenderer_size;
        }

        public int getBufferFilledSizeBetweenAudioDecoderAndAudioRenderer() {
            return this.audiodecoder_audiorenderer_filled;
        }
    }

    public interface MediaPlayerCallback {
        int OnReceiveData(ByteBuffer byteBuffer, int i, long j);

        int Status(int i);
    }

    public interface MediaPlayerCallbackSubtitle {
        int OnReceiveSubtitleString(String str, long j);
    }

    public enum PlayerModes {
        PP_MODE_ALL(-1),
        PP_MODE_VIDEO(1),
        PP_MODE_AUDIO(2),
        PP_MODE_SUBTITLE(4),
        PP_MODE_RECORD(8);
        
        private final int propname;

        private PlayerModes(int propname) {
            this.propname = propname;
        }

        public int val() {
            return this.propname;
        }
    }

    public enum PlayerNotifyCodes {
        PLP_TRIAL_VERSION(-999),
        PLP_BUILD_STARTING(1),
        PLP_BUILD_SUCCESSFUL(2),
        PLP_BUILD_FAILED(3),
        PLP_PLAY_STARTING(4),
        PLP_PLAY_SUCCESSFUL(5),
        PLP_PLAY_FAILED(6),
        PLP_CLOSE_STARTING(7),
        PLP_CLOSE_SUCCESSFUL(8),
        PLP_CLOSE_FAILED(9),
        PLP_ERROR(10),
        PLP_EOS(12),
        PLP_PLAY_PLAY(14),
        PLP_PLAY_PAUSE(15),
        PLP_PLAY_STOP(16),
        PLP_SEEK_COMPLETED(17),
        CP_CONNECT_STARTING(101),
        CP_CONNECT_SUCCESSFUL(102),
        CP_CONNECT_FAILED(103),
        CP_INTERRUPTED(104),
        CP_ERROR_DISCONNECTED(105),
        CP_STOPPED(106),
        CP_INIT_FAILED(107),
        CP_RECORD_STARTED(108),
        CP_RECORD_STOPPED(109),
        CP_RECORD_CLOSED(110),
        CP_BUFFER_FILLED(111),
        CP_ERROR_NODATA_TIMEOUT(112),
        CP_SOURCE_AUDIO_DISCONTINUITY(113),
        CP_SOURCE_VIDEO_DISCONTINUITY(114),
        CP_START_BUFFERING(115),
        CP_STOP_BUFFERING(116),
        VDP_STOPPED(201),
        VDP_INIT_FAILED(202),
        VDP_CRASH(206),
        VRP_STOPPED(300),
        VRP_INIT_FAILED(301),
        VRP_NEED_SURFACE(302),
        VRP_FIRSTFRAME(305),
        VRP_LASTFRAME(306),
        VRP_FFRAME_APAUSE(308),
        VRP_SURFACE_ACQUIRE(309),
        VRP_SURFACE_LOST(310),
        ADP_STOPPED(400),
        ADP_INIT_FAILED(401),
        ARP_STOPPED(500),
        ARP_INIT_FAILED(501),
        ARP_LASTFRAME(502),
        ARP_VOLUME_DETECTED(503),
        CRP_STOPPED(600),
        SDP_STOPPED(701),
        SDP_INIT_FAILED(702),
        SDP_LASTFRAME(703);
        
        private static Map<Integer, PlayerNotifyCodes> typesByValue = null;
        private final int value;

        static {
            typesByValue = new HashMap();
            PlayerNotifyCodes[] values = values();
            int length = values.length;
            int i = 0;
            while (i < length) {
                PlayerNotifyCodes type = values[i];
                typesByValue.put(Integer.valueOf(type.value), type);
                i++;
            }
        }

        private PlayerNotifyCodes(int value) {
            this.value = value;
        }

        public static PlayerNotifyCodes forValue(int value) {
            return (PlayerNotifyCodes) typesByValue.get(Integer.valueOf(value));
        }

        public static int forType(PlayerNotifyCodes type) {
            return type.value;
        }
    }

    public enum PlayerProperties {
        PP_PROPERTY_RENDERED_VIDEO_FRAMES(0),
        PP_PROPERTY_AUDIO_VOLUME_MEAN(1),
        PP_PROPERTY_AUDIO_VOLUME_MAX(2),
        PP_PROPERTY_PLP_LAST_ERROR(3),
        PP_PROPERTY_PLP_RESPONSE_TEXT(4),
        PP_PROPERTY_PLP_RESPONSE_CODE(5);
        
        private final int propname;

        private PlayerProperties(int propname) {
            this.propname = propname;
        }

        public int val() {
            return this.propname;
        }
    }

    public enum PlayerRecordFlags {
        PP_RECORD_NO_START(0),
        PP_RECORD_AUTO_START(1),
        PP_RECORD_SPLIT_BY_TIME(2),
        PP_RECORD_SPLIT_BY_SIZE(4),
        PP_RECORD_DISABLE_VIDEO(8),
        PP_RECORD_DISABLE_AUDIO(16);
        
        private final int value;

        private PlayerRecordFlags(int value) {
            this.value = value;
        }

        public static int forType(PlayerRecordFlags type) {
            return type.value;
        }
    }

    public enum PlayerRecordStat {
        PP_RECORD_STAT_LASTERROR(0),
        PP_RECORD_STAT_DURATION(1),
        PP_RECORD_STAT_SIZE(2),
        PP_RECORD_STAT_DURATION_TOTAL(3),
        PP_RECORD_STAT_SIZE_TOTAL(4);
        
        private final int value;

        private PlayerRecordStat(int value) {
            this.value = value;
        }

        public static int forType(PlayerRecordStat type) {
            return type.value;
        }
    }

    public enum PlayerState {
        Opening(0),
        Opened(1),
        Started(2),
        Paused(3),
        Stopped(4),
        Closing(5),
        Closed(6);
        
        private static Map<Integer, PlayerState> typesByValue = null;
        private final int value;

        static {
            typesByValue = new HashMap();
            PlayerState[] values = values();
            int length = values.length;
            int i=0;
            while (i < length) {
                PlayerState type = values[i];
                typesByValue.put(Integer.valueOf(type.value), type);
                i++;
            }
        }

        private PlayerState(int value) {
            this.value = value;
        }

        public static PlayerState forValue(int value) {
            return (PlayerState) typesByValue.get(Integer.valueOf(value));
        }

        public static int forType(PlayerState type) {
            return type.value;
        }
    }

    public class Position {
        private long current;
        private long duration;
        private long first;
        private long last;
        private int stream_type;

        private Position() {
            this.first = 0;
            this.current = 0;
            this.last = 0;
            this.duration = 0;
            this.stream_type = 0;
        }

        private Position(long first, long current, long last, long duration, int stream_type) {
            this.first = 0;
            this.current = 0;
            this.last = 0;
            this.duration = 0;
            this.stream_type = 0;
            this.first = first;
            this.current = current;
            this.last = last;
            this.duration = duration;
            this.stream_type = stream_type;
        }

        public long getFirst() {
            return this.first;
        }

        public long getCurrent() {
            return this.current;
        }

        public long getLast() {
            return this.last;
        }

        public long getDuration() {
            return this.duration;
        }

        public long getStreamType() {
            return (long) this.stream_type;
        }
    }

    public class VideoShot {
        private int height = 0;
        private ByteBuffer outbuff = null;
        private int width = 0;

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public ByteBuffer getData() {
            return this.outbuff;
        }

        public void setData(ByteBuffer buff) {
            this.outbuff = buff;
        }
    }

    public native int nativePlayerAudioGetCount(long[] jArr);

    public native int nativePlayerAudioGetSelected(long[] jArr);

    public native int nativePlayerAudioSelect(long[] jArr, int i);

    public native int nativePlayerBitrateOnSource(long[] jArr);

    public native int nativePlayerClose(long[] jArr);

    public native int nativePlayerDataDelayOnSource(long[] jArr);

    public native int nativePlayerDroppedFrames(long[] jArr);

    public native int nativePlayerGetAspectRatioSizes(long[] jArr, int i, int i2, int i3, int i4, int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4);

    public native int nativePlayerGetInternalBuffersState(long[] jArr, int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4, int[] iArr5, int[] iArr6, int[] iArr7, int[] iArr8);

    public native int nativePlayerGetLiveStreamPosition(long[] jArr, long[] jArr2, long[] jArr3, long[] jArr4, long[] jArr5, int[] iArr);

    public native int nativePlayerGetPropInt(long[] jArr, int i);

    public native String nativePlayerGetPropString(long[] jArr, int i);

    public native int nativePlayerGetRendererPosition(long[] jArr, long[] jArr2);

    public native int nativePlayerGetShot(long[] jArr, ByteBuffer byteBuffer, int[] iArr, int[] iArr2);

    public native int nativePlayerGetState(long[] jArr);

    public native String nativePlayerGetStreamInfo(long[] jArr);

    public native int nativePlayerGetStreamPosition(long[] jArr, long[] jArr2, long[] jArr3);

    public native int nativePlayerGetStreamPrebuffer(long[] jArr, long[] jArr2);

    public native int nativePlayerGetVideoSize(long[] jArr, int[] iArr, int[] iArr2);

    public native long nativePlayerInit(long[] jArr, MediaPlayer mediaPlayer);

    public native int nativePlayerInterrupt(long[] jArr);

    public native int nativePlayerIsHardwareDecoding(long[] jArr);

    public native int nativePlayerIsPlaying(long[] jArr);

    public native int nativePlayerOpen(long[] jArr, String str, int i, int i2);

    public native int nativePlayerOpenAsPreview(long[] jArr, String str, int i, int i2);

    public native int nativePlayerPause(long[] jArr);

    public native int nativePlayerPauseFlush(long[] jArr);

    public native int nativePlayerPlay(long[] jArr, int i);

    public native String nativePlayerRecordGetFileName(long[] jArr, int i);

    public native int nativePlayerRecordGetStat(long[] jArr, int i, long[] jArr2);

    public native int nativePlayerRecordSetOptions(long[] jArr, String str, int i, int i2, int i3, String str2);

    public native int nativePlayerRecordSetTrimPositions(long[] jArr, long j, long j2);

    public native int nativePlayerRecordStart(long[] jArr);

    public native int nativePlayerRecordStop(long[] jArr);

    public native int nativePlayerResize(long[] jArr, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11);

    public native int nativePlayerSetAudioOnly(long[] jArr, int i);

    public native int nativePlayerSetCallback(long[] jArr, MediaPlayerCallback mediaPlayerCallback);

    public native int nativePlayerSetExternalMediaCodec(long[] jArr, MediaFormat mediaFormat, MediaCodec mediaCodec);

    public native int nativePlayerSetFFRate(long[] jArr, int i);

    public native int nativePlayerSetLiveStreamPosition(long[] jArr, long j);

    public native int nativePlayerSetOptions(long[] jArr, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14, int i15, int i16, int i17, int i18, int i19, int i20, int i21, int i22, int i23, int i24, int i25, String str, long j, int i26, String str2, String str3, int i27, int i28);

    public native int nativePlayerSetRecordOnly(long[] jArr, int i);

    public native int nativePlayerSetStreamPosition(long[] jArr, long j, int i);

    public native int nativePlayerSetSurface(long[] jArr, Surface surface);

    public native int nativePlayerSetVolumeBoost(long[] jArr, int i);

    public native int nativePlayerStartVolumeDetect(long[] jArr, int i);

    public native int nativePlayerStatGetBitrate(long[] jArr);

    public native float nativePlayerStatGetFPS(long[] jArr);

    public native int nativePlayerStatGetPercFree(long[] jArr);

    public native int nativePlayerStop(long[] jArr);

    public native int nativePlayerSubtitleGetCount(long[] jArr);

    public native int nativePlayerSubtitleGetSelected(long[] jArr);

    public native int nativePlayerSubtitleSelect(long[] jArr, int i);

    public native int nativePlayerSubtitleSourceAdd(long[] jArr, String str);

    public native int nativePlayerSubtitleSourceRemove(long[] jArr, String str);

    public native int nativePlayerUninit(long[] jArr);

    public native int nativePlayerVsyncEnable(long[] jArr, int i);

    public native int nativePlayerVsyncSetCurrentTime(long[] jArr, long j);

    static {
        SystemUtils.loadLibs();
    }

    public MediaPlayer(Context context, boolean is_window) {
        super(context);
        Log.v(TAG, "VXG Media Player version:5.1.20170112");
        this.playerConfig = new MediaPlayerConfig();
        this.mIS_WINDOW = is_window;
        this.mUseExternalSurface = false;
        this.mIsExternalSurfaceTexture = false;
        if (is_window) {
            this.view = new SurfaceView(context);
            this.view.getHolder().addCallback(this);
        }
        if (is_window) {
            setFocusable(true);
            setFocusableInTouchMode(true);
            requestFocus();
            setOnTouchListener(this);
            setBackgroundColor(this.playerConfig.getColorBackground());
        }
        this.mSensorManager = (SensorManager) context.getSystemService("sensor");
        this.mWidth = 1.0f;
        this.mHeight = 1.0f;
        this.mContext = context;
        if (is_window) {
            this.subtitleTextView = new TextView(context);
            this.subtitleTextView.setLayoutParams(new LayoutParams(-2, -2, 81));
            this.subtitleTextView.setShadowLayer(1.5f, -1.0f, 1.0f, Color.parseColor("#ff444444"));
            this.subtitleTextView.setTextColor(Color.parseColor("#FFFFFFFF"));
            this.subtitleTextView.setTextSize(2, 48.0f);
            this.subtitleTextView.setTypeface(null, 1);
            this.subtitleImageView = new ImageView(context);
            this.subtitleImageView.setLayoutParams(new LayoutParams(-1, -1));
            this.barFrameLayout = new FrameLayout(context);
            this.barFrameLayout.addView(this.subtitleImageView);
            addView(this.subtitleTextView);
            LayoutParams params1 = new LayoutParams(-1, -1);
            this.queueSurfaceCreate.add(Integer.valueOf(1));
            addView(this.view, params1);
            addView(this.barFrameLayout, new LayoutParams(-1, -1));
            if (VERSION.SDK_INT >= 11) {
                jb_setalpha(0.0f);
            }
            this.view.setBackgroundResource(17170445);
        }
    }

    public MediaPlayer(Context context) {
        super(context);
        Log.v(TAG, "VXG Media Player version:5.1.20170112");
        this.playerConfig = new MediaPlayerConfig();
        this.mUseExternalSurface = false;
        this.mIsExternalSurfaceTexture = false;
        this.view = new SurfaceView(context);
        this.view.getHolder().addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        setOnTouchListener(this);
        setBackgroundColor(this.playerConfig.getColorBackground());
        this.mSensorManager = (SensorManager) context.getSystemService("sensor");
        this.mWidth = 1.0f;
        this.mHeight = 1.0f;
        this.mContext = context;
        this.subtitleTextView = new TextView(context);
        this.subtitleTextView.setLayoutParams(new LayoutParams(-2, -2, 81));
        this.subtitleTextView.setShadowLayer(1.5f, -1.0f, 1.0f, Color.parseColor("#ff444444"));
        this.subtitleTextView.setTextColor(Color.parseColor("#FFFFFFFF"));
        this.subtitleTextView.setTextSize(2, 48.0f);
        this.subtitleTextView.setTypeface(null, 1);
        this.subtitleImageView = new ImageView(context);
        this.subtitleImageView.setLayoutParams(new LayoutParams(-1, -1));
        this.barFrameLayout = new FrameLayout(context);
        this.barFrameLayout.addView(this.subtitleImageView);
        addView(this.subtitleTextView);
        LayoutParams params1 = new LayoutParams(-1, -1);
        this.queueSurfaceCreate.add(Integer.valueOf(1));
        addView(this.view, params1);
        addView(this.barFrameLayout, new LayoutParams(-1, -1));
        if (VERSION.SDK_INT >= 11) {
            jb_setalpha(0.0f);
        }
        this.view.setBackgroundResource(17170445);
    }

    public MediaPlayer(Context context, AttributeSet attr) {
        super(context, attr);
        Log.v(TAG, "VXG Media Player version:5.1.20170112");
        this.playerConfig = new MediaPlayerConfig();
        this.mUseExternalSurface = false;
        this.mIsExternalSurfaceTexture = false;
        this.view = new SurfaceView(context, attr);
        this.view.getHolder().addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        setOnTouchListener(this);
        setBackgroundColor(this.playerConfig.getColorBackground());
        this.mSensorManager = (SensorManager) context.getSystemService("sensor");
        this.mWidth = 1.0f;
        this.mHeight = 1.0f;
        this.mContext = context;
        this.subtitleTextView = new TextView(context);
        this.subtitleTextView.setLayoutParams(new LayoutParams(-2, -2, 81));
        this.subtitleTextView.setShadowLayer(1.5f, -1.0f, 1.0f, Color.parseColor("#ff444444"));
        this.subtitleTextView.setTextColor(Color.parseColor("#FFFFFFFF"));
        this.subtitleTextView.setTextSize(2, 18.0f);
        this.subtitleTextView.setTypeface(null, 1);
        this.subtitleImageView = new ImageView(context);
        this.subtitleImageView.setLayoutParams(new LayoutParams(-1, -1));
        addView(this.subtitleTextView);
        LayoutParams params1 = new LayoutParams(-1, -1);
        this.queueSurfaceCreate.add(Integer.valueOf(1));
        addView(this.view, params1);
        this.barFrameLayout = new FrameLayout(context);
        this.barFrameLayout.addView(this.subtitleImageView);
        addView(this.barFrameLayout, new LayoutParams(-1, -1));
        if (VERSION.SDK_INT >= 11) {
            jb_setalpha(0.0f);
        }
        this.view.setBackgroundResource(17170445);
    }

    public MediaPlayerConfig getConfig() {
        return this.playerConfig;
    }

    public void setPlayerMode(int mode) {
        this.playerConfig.setMode(mode);
    }

    public int getPropInt(PlayerProperties propname) {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return -1;
        }
        return nativePlayerGetPropInt(this.playerWorker.player_inst, propname.val());
    }

    public String getPropString(PlayerProperties propname) {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return null;
        }
        return nativePlayerGetPropString(this.playerWorker.player_inst, propname.val());
    }

    public float GetStatFPS() {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return -1.0f;
        }
        return nativePlayerStatGetFPS(this.playerWorker.player_inst);
    }

    public int GetStatBitrate() {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return -1;
        }
        return nativePlayerStatGetBitrate(this.playerWorker.player_inst);
    }

    public int GetDataDelayOnSource() {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return -1;
        }
        return nativePlayerDataDelayOnSource(this.playerWorker.player_inst);
    }

    public int GetDataBitrateOnSource() {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return -1;
        }
        return nativePlayerBitrateOnSource(this.playerWorker.player_inst);
    }

    public int GetStatPercFree() {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return -1;
        }
        return nativePlayerStatGetPercFree(this.playerWorker.player_inst);
    }

    public boolean IsHardwareDecoding() {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0 || nativePlayerIsHardwareDecoding(this.playerWorker.player_inst) <= 0) {
            return false;
        }
        return true;
    }

    public void backgroundColor(int clr) {
        this.playerConfig.setColorBackground(clr);
        setBackgroundColor(clr);
    }

    public int getAspectRatioMoveModeAvailableDirections() {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0 || this.mVideoWidth <= 0 || this.mVideoHeight <= 0) {
            return 0;
        }
        int[] src_x = new int[]{0};
        int[] src_y = new int[]{0};
        int[] src_w = new int[]{getInternalWidth()};
        int[] src_h = new int[]{getInternalHeight()};
        nativePlayerGetAspectRatioSizes(this.playerWorker.player_inst, this.playerConfig.getEnableAspectRatio(), this.playerConfig.getAspectRatioZoomModePercent(), this.playerConfig.getAspectRatioMoveModeX(), this.playerConfig.getAspectRatioMoveModeY(), src_x, src_y, src_w, src_h);
        if (src_w[0] <= 0 || src_h[0] <= 0) {
            return 0;
        }
        int[] v_w = new int[1];
        int[] v_h = new int[]{getInternalWidth()};
        v_h[0] = getInternalHeight();
        int moveDirection = 0;
        if (src_x[0] < 0) {
            moveDirection = 0 | 1;
        }
        if (src_x[0] + src_w[0] > v_w[0]) {
            moveDirection |= 2;
        }
        if (src_y[0] < 0) {
            moveDirection |= 8;
        }
        if (src_y[0] + src_h[0] > v_h[0]) {
            return moveDirection | 4;
        }
        return moveDirection;
    }

    public int UpdateView(boolean isAspectRatioEnabled) {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return -1;
        }
        this.playerConfig.setEnableAspectRatio(isAspectRatioEnabled ? 1 : 0);
        int rc = nativePlayerResize(this.playerWorker.player_inst, this.playerConfig.getEnableAspectRatio(), this.playerConfig.getAspectRatioZoomModePercent(), this.playerConfig.getAspectRatioMoveModeX(), this.playerConfig.getAspectRatioMoveModeY(), (int) this.mWidth, (int) this.mHeight, 0, Color.red(this.playerConfig.getColorBackground()), Color.green(this.playerConfig.getColorBackground()), Color.blue(this.playerConfig.getColorBackground()), Color.alpha(this.playerConfig.getColorBackground()));
        updateSizesAndAspects();
        return rc;
    }

    public int UpdateView() {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return -1;
        }
        int rc = nativePlayerResize(this.playerWorker.player_inst, this.playerConfig.getEnableAspectRatio(), this.playerConfig.getAspectRatioZoomModePercent(), this.playerConfig.getAspectRatioMoveModeX(), this.playerConfig.getAspectRatioMoveModeY(), (int) this.mWidth, (int) this.mHeight, 0, Color.red(this.playerConfig.getColorBackground()), Color.green(this.playerConfig.getColorBackground()), Color.blue(this.playerConfig.getColorBackground()), Color.alpha(this.playerConfig.getColorBackground()));
        if (rc >= 0) {
            updateSizesAndAspects();
        }
        return rc;
    }

    public SurfaceView getSurfaceView() {
        return this.view;
    }

    public int getVideoWidth() {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return 0;
        }
        int[] v_w = new int[]{0};
        int rc = nativePlayerGetVideoSize(this.playerWorker.player_inst, v_w, new int[]{0});
        return v_w[0];
    }

    public int getVideoHeight() {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return 0;
        }
        int[] v_h = new int[]{0};
        int rc = nativePlayerGetVideoSize(this.playerWorker.player_inst, new int[]{0}, v_h);
        return v_h[0];
    }

    public VideoShot getVideoShot(int desiredWidth, int desiredHeight) {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return null;
        }
        if (this.shotVideo == null) {
            this.shotVideo = new VideoShot();
        }
        boolean isDesired = desiredWidth > 0 && desiredHeight > 0;
        int desiredSize = (desiredWidth * desiredHeight) * 4;
        int req_alloc_size = MAX_VIDEOSHOT_SIZE;
        int[] v_w = new int[]{0};
        int[] v_h = new int[]{0};
        if (nativePlayerGetVideoSize(this.playerWorker.player_inst, v_w, v_h) == 0) {
            req_alloc_size = (v_w[0] * v_h[0]) * 4;
        }
        if (isDesired && desiredSize < req_alloc_size) {
            req_alloc_size = desiredSize;
        }
        if (this.outbuff == null) {
            this.outbuff = ByteBuffer.allocateDirect(req_alloc_size);
            this.outbuff.order(ByteOrder.nativeOrder());
        } else if (req_alloc_size != this.outbuff.capacity()) {
            this.outbuff = ByteBuffer.allocateDirect(req_alloc_size);
            this.outbuff.order(ByteOrder.nativeOrder());
        }
        int[] src_w = new int[]{desiredWidth};
        int[] src_h = new int[]{desiredHeight};
        int size = nativePlayerGetShot(this.playerWorker.player_inst, this.outbuff, src_w, src_h);
        if (size <= 0) {
            return null;
        }
        this.shotVideo.setWidth(src_w[0]);
        this.shotVideo.setHeight(src_h[0]);
        this.outbuff.limit(size);
        this.shotVideo.setData(this.outbuff.slice());
        return this.shotVideo;
    }

    public String getStreamInfo() {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return "";
        }
        return nativePlayerGetStreamInfo(this.playerWorker.player_inst);
    }

    public static String getVersion() {
        return MEDIA_PLAYER_LIB_VERSION;
    }

    public long getStreamDuration() {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return 0;
        }
        long[] duration = new long[]{0};
        nativePlayerGetStreamPosition(this.playerWorker.player_inst, new long[]{0}, duration);
        return duration[0];
    }

    public int setStreamPosition(long lTime) {
        int rc = -1;
        if (!(this.playerWorker == null || this.playerWorker.player_inst[0] == 0 || lTime < 0)) {
            this.m_internal_mute = true;
            if (this.playerConfig.getFadeOnSeek() == 1) {
                this.m_fade_time = System.nanoTime() + 300000000;
            }
            rc = nativePlayerSetStreamPosition(this.playerWorker.player_inst, lTime, -1);
            if (this.subtitleTextView != null) {
                this.subtitleTextView.postDelayed(this.runnableInformerHide, 0);
            }
            if (this.subtitleImageView != null) {
                this.subtitleImageView.postDelayed(this.runnableInformerHide_image, 0);
            }
            this.m_internal_mute = false;
        }
        return rc;
    }

    public long getStreamPosition() {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return 0;
        }
        long[] position = new long[]{0};
        nativePlayerGetStreamPosition(this.playerWorker.player_inst, position, new long[]{0});
        return position[0];
    }

    public long getStreamPrebufferTime() {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return 0;
        }
        long[] position = new long[]{0};
        nativePlayerGetStreamPrebuffer(this.playerWorker.player_inst, position);
        return position[0];
    }

    public long getRenderPosition() {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return 0;
        }
        long[] position = new long[]{0};
        nativePlayerGetRendererPosition(this.playerWorker.player_inst, position);
        return position[0];
    }

    public int GetDroppedFrame() {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return 0;
        }
        return nativePlayerDroppedFrames(this.playerWorker.player_inst);
    }

    public void setLiveStreamPath(String path) {
        this.playerConfig.setStartPath(path);
    }

    public void setStartLiveStreamPosition(long offset) {
        this.playerConfig.setStartOffest(offset);
    }

    public int setLiveStreamPosition(long lTime) {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return -1;
        }
        if (this.subtitleTextView != null) {
            this.subtitleTextView.postDelayed(this.runnableInformerHide, 0);
        }
        if (this.subtitleImageView != null) {
            this.subtitleImageView.postDelayed(this.runnableInformerHide_image, 0);
        }
        this.m_internal_mute = true;
        if (this.playerConfig.getFadeOnSeek() == 1) {
            this.m_fade_time = System.nanoTime() + 600000000;
        }
        int rc = nativePlayerSetLiveStreamPosition(this.playerWorker.player_inst, lTime);
        this.m_internal_mute = false;
        return rc;
    }

    public Position getLiveStreamPosition() {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return null;
        }
        long[] first = new long[]{0};
        long[] current = new long[]{0};
        long[] last = new long[]{0};
        long[] duration = new long[]{0};
        int[] stream_type = new int[]{0};
        nativePlayerGetLiveStreamPosition(this.playerWorker.player_inst, first, current, last, duration, stream_type);
        return new Position(first[0], current[0], last[0], duration[0], stream_type[0]);
    }

    public void setFFRate(int rate) {
        getConfig().setFFRate(rate);
        if (this.playerWorker != null && this.playerWorker.player_inst[0] != 0) {
            this.m_internal_mute = true;
            if (this.playerConfig.getFadeOnChangeFFSpeed() == 1) {
                this.m_fade_time = System.nanoTime() + 600000000;
            }
            if (rate > 3000) {
                this.m_fade_time = Long.MAX_VALUE;
            }
            nativePlayerSetFFRate(this.playerWorker.player_inst, rate);
            this.m_internal_mute = false;
        }
    }

    public void startVolumeDetect(int vd_max_samples) {
        getConfig().setVolumeDetectMaxSamples(vd_max_samples);
        if (this.playerWorker != null && this.playerWorker.player_inst[0] != 0) {
            nativePlayerStartVolumeDetect(this.playerWorker.player_inst, vd_max_samples);
        }
    }

    public void setVolumeBoost(int volume_boost) {
        getConfig().setVolumeBoost(volume_boost);
        if (this.playerWorker != null && this.playerWorker.player_inst[0] != 0) {
            nativePlayerSetVolumeBoost(this.playerWorker.player_inst, volume_boost);
        }
    }

    public void toggleMute(boolean mute) {
        this.playerConfig.setEnableAudio(mute ? 0 : 1);
    }

    public void setKey(String key) {
        this.playerConfig.setSslKey(key);
    }

    public void setOnSubtitleListener(MediaPlayerCallbackSubtitle callbackSubtitle) {
        this.CallbackSubtitle = callbackSubtitle;
    }

    public BuffersState getInternalBuffersState() {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return null;
        }
        int[] source_videodecoder_filled = new int[]{0};
        int[] source_videodecoder_size = new int[]{0};
        int[] source_audiodecoder_filled = new int[]{0};
        int[] source_audiodecoder_size = new int[]{0};
        int[] videodecoder_videorenderer_filled = new int[]{0};
        int[] videodecoder_videorenderer_size = new int[]{0};
        int[] audiodecoder_audiorenderer_filled = new int[]{0};
        int[] audiodecoder_audiorenderer_size = new int[]{0};
        if (nativePlayerGetInternalBuffersState(this.playerWorker.player_inst, source_videodecoder_filled, source_videodecoder_size, videodecoder_videorenderer_filled, videodecoder_videorenderer_size, source_audiodecoder_filled, source_audiodecoder_size, audiodecoder_audiorenderer_filled, audiodecoder_audiorenderer_size) < 0) {
            return null;
        }
        return new BuffersState(source_videodecoder_filled[0], source_videodecoder_size[0], videodecoder_videorenderer_filled[0], videodecoder_videorenderer_size[0], source_audiodecoder_filled[0], source_audiodecoder_size[0], audiodecoder_audiorenderer_filled[0], audiodecoder_audiorenderer_size[0]);
    }

    public synchronized void Open(String ConnectionUrl, int DataReceiveTimeout, MediaPlayerCallback callback) {
        if (this.mPlayerThread == null) {
            this.playerWorker = new MediaPlayerWorker(this, false);
            this.playerConfig.setConnectionUrl(ConnectionUrl);
            if (!(this.playerConfig.getConnectionUrl() == null || this.playerConfig.getConnectionUrl().isEmpty() || this.playerConfig.getConnectionUrl().indexOf("mms://") != 0)) {
                this.playerConfig.setConnectionUrl(this.playerConfig.getConnectionUrl().replace("mms://", "mmsh://"));
            }
            this.playerOrientation = getResources().getConfiguration().orientation;
            this.playerConfig.setDecodingType(0);
            this.playerConfig.setRendererType(1);
            this.playerConfig.setSynchroEnable(1);
            this.playerConfig.setSynchroNeedDropVideoFrames(0);
            this.playerConfig.setEnableColorVideo(1);
            this.playerConfig.setEnableAspectRatio(1);
            this.playerConfig.setNumberOfCPUCores(1);
            this.playerConfig.setConnectionNetworkProtocol(-1);
            this.playerConfig.setConnectionDetectionTime(5000);
            this.playerConfig.setConnectionBufferingType(0);
            this.playerConfig.setConnectionBufferingTime(3000);
            this.playerConfig.setConnectionBufferingSize(0);
            this.Callback = callback;
            this.playerConfig.setDataReceiveTimeout(DataReceiveTimeout);
            if (this.mIS_WINDOW) {
                clearLayout();
                addView(this.subtitleTextView);
                this.barFrameLayout.addView(this.subtitleImageView, new LayoutParams(-2, -2));
                LayoutParams params1 = new LayoutParams(-1, -1);
                this.queueSurfaceCreate.add(Integer.valueOf(2));
                addView(this.view, params1);
                addView(this.barFrameLayout, new LayoutParams(-1, -1));
                if (VERSION.SDK_INT >= 11) {
                    jb_setalpha(0.0f);
                }
                this.view.setBackgroundResource(17170445);
            }
            this.mUseExternalSurface = false;
            this.mPlayerThread = new Thread(this.playerWorker, "MediaPlayerThread");
            this.mPlayerThread.start();
            this.waitStartOpenThread.wait("Wait start open thread... ");
        }
    }

    public synchronized void Open(String ConnectionUrl, int ConnectionNetworkProtocol, int ConnectionDetectionTime, int ConnectionBufferingTime, int DecodingType, int RendererType, int SynchroEnable, int SynchroNeedDropVideoFrames, int EnableColorVideo, int aspectRatioMode, int DataReceiveTimeout, int NumberOfCPUCores, MediaPlayerCallback callback) {
        if (this.mPlayerThread == null) {
            boolean z;
            this.playerWorker = new MediaPlayerWorker(this, false);
            this.playerConfig.setConnectionUrl(ConnectionUrl);
            if (!(this.playerConfig.getConnectionUrl() == null || this.playerConfig.getConnectionUrl().isEmpty() || this.playerConfig.getConnectionUrl().indexOf("mms://") != 0)) {
                this.playerConfig.setConnectionUrl(this.playerConfig.getConnectionUrl().replace("mms://", "mmsh://"));
            }
            this.playerConfig.setNumberOfCPUCores(NumberOfCPUCores);
            if (NumberOfCPUCores <= 0) {
                this.playerConfig.setNumberOfCPUCores(SystemUtils.getNumCores());
            }
            this.playerConfig.setBogoMIPS(SystemUtils.getCPUBogoMIPS());
            this.playerConfig.setDecodingType(DecodingType);
            if (DecodingType > 0 && VERSION.SDK_INT < 16) {
                this.playerConfig.setDecodingType(0);
            }
            this.playerOrientation = getResources().getConfiguration().orientation;
            this.playerConfig.setRendererType(RendererType);
            this.playerConfig.setSynchroEnable(SynchroEnable);
            this.playerConfig.setSynchroNeedDropVideoFrames(SynchroNeedDropVideoFrames);
            this.playerConfig.setEnableColorVideo(EnableColorVideo);
            this.playerConfig.setEnableAspectRatio(aspectRatioMode);
            this.playerConfig.setDataReceiveTimeout(DataReceiveTimeout);
            this.playerConfig.setConnectionNetworkProtocol(ConnectionNetworkProtocol);
            this.playerConfig.setConnectionDetectionTime(ConnectionDetectionTime);
            this.playerConfig.setConnectionBufferingTime(ConnectionBufferingTime);
            this.Callback = callback;
            if (this.playerConfig.getDecodingType() == 0) {
                z = false;
            } else {
                z = this.mUseExternalSurface;
            }
            this.mUseExternalSurface = z;
            if (this.mIS_WINDOW) {
                clearLayout();
                LayoutParams params1 = new LayoutParams(-1, -1);
                this.queueSurfaceCreate.add(Integer.valueOf(2));
                addView(this.view, params1);
                addView(this.subtitleTextView);
                this.barFrameLayout.addView(this.subtitleImageView, new LayoutParams(-1, -1));
                addView(this.barFrameLayout, new LayoutParams(-1, -1));
                if (VERSION.SDK_INT >= 11) {
                    jb_setalpha(0.0f);
                }
                this.view.setBackgroundResource(17170445);
            }
            this.mPlayerThread = new Thread(this.playerWorker, "MediaPlayerThread");
            this.mPlayerThread.start();
            this.waitStartOpenThread.wait("Wait start open thread... ");
            if (!this.mIS_WINDOW && this.mUseExternalSurface && this.mIsExternalSurfaceTexture && this.mSurface != null) {
                this.waitOpenSource.wait("Wait open source... ");
                if (getState() != PlayerState.Closed) {
                    openMediaCodec(this.mSurface, this.mediaMime, this.mediaWidth, this.mediaHeight, this.mediaBuffer);
                }
            }
        }
    }

    public synchronized void Open(MediaPlayerConfig config, MediaPlayerCallback callback) {
        boolean z = false;
        synchronized (this) {
            if (this.mPlayerThread == null) {
                if (config != null) {
                    this.playerConfig = new MediaPlayerConfig(config);
                }
                this.playerWorker = new MediaPlayerWorker(this, false);
                if (VERSION.SDK_INT < 16) {
                    this.playerConfig.setDecodingType(0);
                }
                this.playerOrientation = getResources().getConfiguration().orientation;
                if (this.playerConfig.getNumberOfCPUCores() <= 0) {
                    this.playerConfig.setNumberOfCPUCores(SystemUtils.getNumCores());
                }
                this.Callback = callback;
                if (this.mIS_WINDOW && !this.mUseExternalSurface && this.mSurface == null) {
                    if (Looper.myLooper() != Looper.getMainLooper()) {
                        final WaitNotify sync = new WaitNotify();
                        this.subtitleImageView.post(new Runnable() {
                            public void run() {
                                MediaPlayer.this.clearLayout();
                                LayoutParams params1 = new LayoutParams(-1, -1);
                                MediaPlayer.this.queueSurfaceCreate.add(Integer.valueOf(2));
                                MediaPlayer.this.addView(MediaPlayer.this.view, params1);
                                MediaPlayer.this.addView(MediaPlayer.this.subtitleTextView);
                                MediaPlayer.this.barFrameLayout.addView(MediaPlayer.this.subtitleImageView, new LayoutParams(-1, -1));
                                MediaPlayer.this.addView(MediaPlayer.this.barFrameLayout, new LayoutParams(-1, -1));
                                if (VERSION.SDK_INT >= 11) {
                                    MediaPlayer.this.jb_setalpha(0.0f);
                                }
                                MediaPlayer.this.view.setBackgroundResource(17170445);
                                sync.notify("notify UI layout create.");
                            }
                        });
                        sync.wait("wait UI layout create..");
                    } else {
                        clearLayout();
                        LayoutParams params1 = new LayoutParams(-1, -1);
                        this.queueSurfaceCreate.add(Integer.valueOf(2));
                        addView(this.view, params1);
                        addView(this.subtitleTextView);
                        this.barFrameLayout.addView(this.subtitleImageView, new LayoutParams(-1, -1));
                        addView(this.barFrameLayout, new LayoutParams(-1, -1));
                        if (VERSION.SDK_INT >= 11) {
                            jb_setalpha(0.0f);
                        }
                        this.view.setBackgroundResource(17170445);
                    }
                }
                if (this.playerConfig.getDecodingType() != 0) {
                    z = this.mUseExternalSurface;
                }
                this.mUseExternalSurface = z;
                this.mPlayerThread = new Thread(this.playerWorker, "MediaPlayerThread");
                this.mPlayerThread.start();
                this.waitStartOpenThread.wait("Wait start open thread... ");
                if (!this.mIS_WINDOW && this.mUseExternalSurface && this.mIsExternalSurfaceTexture && this.mSurface != null) {
                    this.waitOpenSource.wait("Wait open source... ");
                    if (getState() != PlayerState.Closed) {
                        openMediaCodec(this.mSurface, this.mediaMime, this.mediaWidth, this.mediaHeight, this.mediaBuffer);
                    }
                }
            }
        }
    }

    public synchronized void OpenAsPreview(String ConnectionUrl, int DataReceiveTimeout, MediaPlayerCallback callback) {
        if (this.mPlayerThread == null) {
            this.playerWorker = new MediaPlayerWorker(this, true);
            this.playerConfig.setConnectionUrl(ConnectionUrl);
            if (!(this.playerConfig.getConnectionUrl() == null || this.playerConfig.getConnectionUrl().isEmpty() || this.playerConfig.getConnectionUrl().indexOf("mms://") != 0)) {
                this.playerConfig.setConnectionUrl(this.playerConfig.getConnectionUrl().replace("mms://", "mmsh://"));
            }
            this.playerOrientation = getResources().getConfiguration().orientation;
            this.playerConfig.setNumberOfCPUCores(1);
            this.playerConfig.setDecodingType(0);
            this.playerConfig.setRendererType(1);
            this.playerConfig.setSynchroEnable(0);
            this.playerConfig.setSynchroNeedDropVideoFrames(0);
            this.playerConfig.setEnableColorVideo(1);
            this.playerConfig.setEnableAspectRatio(1);
            this.playerConfig.setConnectionNetworkProtocol(-1);
            this.playerConfig.setConnectionDetectionTime(5000);
            this.playerConfig.setConnectionBufferingType(0);
            this.playerConfig.setConnectionBufferingTime(3000);
            this.playerConfig.setConnectionBufferingSize(0);
            this.Callback = callback;
            this.playerConfig.setDataReceiveTimeout(DataReceiveTimeout);
            clearLayout();
            LayoutParams params1 = new LayoutParams(-1, -1);
            this.queueSurfaceCreate.add(Integer.valueOf(2));
            addView(this.view, params1);
            addView(this.subtitleTextView);
            this.barFrameLayout.addView(this.subtitleImageView, new LayoutParams(-1, -1));
            addView(this.barFrameLayout, new LayoutParams(-1, -1));
            this.mPlayerThread = new Thread(this.playerWorker, "MediaPlayerThread");
            this.mPlayerThread.start();
            this.waitStartOpenThread.wait("Wait start open thread... ");
        }
    }

    public PlayerState getState() {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return PlayerState.Closed;
        }
        PlayerState statePlayer = PlayerState.forValue(nativePlayerGetState(this.playerWorker.player_inst));
        if (statePlayer == null) {
            return PlayerState.Closed;
        }
        return statePlayer;
    }

    public void Play(int flags) {
        if (this.playerWorker != null && this.playerWorker.player_inst[0] != 0) {
            this.m_internal_mute = true;
            this.m_fade_time = System.nanoTime() + 705032704;
            nativePlayerPlay(this.playerWorker.player_inst, 1);
            this.m_internal_mute = false;
        }
    }

    public void Play() {
        if (this.playerWorker != null && this.playerWorker.player_inst[0] != 0) {
            nativePlayerPlay(this.playerWorker.player_inst, 0);
        }
    }

    public void Stop() {
        if (this.playerWorker != null && this.playerWorker.player_inst[0] != 0) {
            nativePlayerStop(this.playerWorker.player_inst);
        }
    }

    public void Pause() {
        if (this.playerWorker != null && this.playerWorker.player_inst[0] != 0) {
            nativePlayerPause(this.playerWorker.player_inst);
        }
    }

    public void PauseFlush() {
        if (this.playerWorker != null && this.playerWorker.player_inst[0] != 0) {
            nativePlayerPauseFlush(this.playerWorker.player_inst);
        }
    }

    public void RecordSetup(String record_path, int record_flags, int record_split_time, int record_split_size, String record_prefix) {
        if (this.playerConfig != null) {
            this.playerConfig.setRecordPath(record_path);
            this.playerConfig.setRecordFlags(record_flags);
            this.playerConfig.setRecordSplitTime(record_split_time);
            this.playerConfig.setRecordSplitSize(record_split_size);
            this.playerConfig.setRecordPrefix(record_prefix);
            if (this.playerWorker != null && this.playerWorker.player_inst[0] != 0) {
                nativePlayerRecordSetOptions(this.playerWorker.player_inst, record_path, record_flags, record_split_time, record_split_size, record_prefix);
            }
        }
    }

    public void RecordStart() {
        if (this.playerWorker != null && this.playerWorker.player_inst[0] != 0) {
            nativePlayerRecordStart(this.playerWorker.player_inst);
        }
    }

    public void RecordStop() {
        if (this.playerWorker != null && this.playerWorker.player_inst[0] != 0) {
            nativePlayerRecordStop(this.playerWorker.player_inst);
        }
    }

    public String RecordGetFileName(int param) {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return null;
        }
        return nativePlayerRecordGetFileName(this.playerWorker.player_inst, param);
    }

    public long RecordGetStat(int propname) {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return 0;
        }
        long[] value = new long[]{0};
        nativePlayerRecordGetStat(this.playerWorker.player_inst, propname, value);
        return value[0];
    }

    public int AudioGetCount() {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return 0;
        }
        return nativePlayerAudioGetCount(this.playerWorker.player_inst);
    }

    public int AudioSelect(int stream_i) {
        getConfig().setSelectedAudio(stream_i);
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return 0;
        }
        return nativePlayerAudioSelect(this.playerWorker.player_inst, stream_i);
    }

    public int AudioGetSelected() {
        int ret = getConfig().getSelectedAudio();
        return (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) ? ret : nativePlayerAudioGetSelected(this.playerWorker.player_inst);
    }

    public int SubtitleGetCount() {
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return 0;
        }
        return nativePlayerSubtitleGetCount(this.playerWorker.player_inst);
    }

    public int SubtitleSelect(int stream_i) {
        getConfig().setSelectedSubtitle(stream_i);
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return 0;
        }
        return nativePlayerSubtitleSelect(this.playerWorker.player_inst, stream_i);
    }

    public int SubtitleGetSelected() {
        int ret = getConfig().getSelectedSubtitle();
        return (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) ? ret : nativePlayerSubtitleGetSelected(this.playerWorker.player_inst);
    }

    public int SubtitleSourceAdd(String path2) {
        if (!getConfig().subtitlePaths.contains(path2)) {
            getConfig().subtitlePaths.add(path2);
        }
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return 0;
        }
        return nativePlayerSubtitleSourceAdd(this.playerWorker.player_inst, path2);
    }

    public int SubtitleSourceRemove(String path2) {
        getConfig().subtitlePaths.remove(path2);
        if (this.playerWorker == null || this.playerWorker.player_inst[0] == 0) {
            return 0;
        }
        return nativePlayerSubtitleSourceRemove(this.playerWorker.player_inst, path2);
    }

    public void setSurfaceAndRunMediaCodecInOpenThreadContext(Surface surface, boolean isTexture) {
        boolean z = true;
        this.mUseExternalSurface = false;
        this.mIsExternalSurfaceTexture = false;
        if (VERSION.SDK_INT >= 11 && !this.mIS_WINDOW && getState() == PlayerState.Closed) {
            boolean z2;
            this.mSurface = surface;
            if (this.mSurface != null) {
                z2 = true;
            } else {
                z2 = false;
            }
            this.mUseExternalSurface = z2;
            if (!(this.mUseExternalSurface && isTexture)) {
                z = false;
            }
            this.mIsExternalSurfaceTexture = z;
        }
    }

    public void setSurface(Surface surface) {
        this.mSurface = surface;
    }

    public void setSurface(Surface surface, int newWidth, int newHeight) {
        this.mWidth = (float) newWidth;
        this.mHeight = (float) newHeight;
        this.mSurface = surface;
    }

    public synchronized void Close() {
        this.queueSurfaceCreate.clear();
        this.previousVsyncEnabe = 0;
        if (this.mPlayerThread == null || this.playerWorker == null) {
            clearLayout();
        } else if (this.mPlayerThread.isAlive() || this.playerWorker == null || this.playerWorker.player_inst[0] != 0) {
            this.waitSurfaceCreated.notify("Surface created notify.. ");
            this.waitOpenSource.notify("Open source notify.. ");
            this.waitOpenMediaCodec.notify("Open mediacodec notify.. ");
            if (!(this.playerWorker == null || this.playerWorker.player_inst[0] == 0)) {
                nativePlayerInterrupt(this.playerWorker.player_inst);
            }
            this.waitSurfaceCreated.notify("Surface created notify.. ");
            this.waitOpenSource.notify("Open source notify.. ");
            this.waitOpenMediaCodec.notify("Open mediacodec notify.. ");
            this.playerWorker.finish = true;
            do {
                try {
                    Thread.sleep(100);
                    if (this.playerWorker.player_inst[0] == 0) {
                        break;
                    }
                } catch (Exception e) {
                }
            } while (this.playerWorker.finish);
            this.mPlayerThread = null;
            this.playerWorker = null;
            if (this.mIS_WINDOW && !this.mUseExternalSurface) {
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    final WaitNotify syncVisible = new WaitNotify();
                    this.subtitleImageView.post(new Runnable() {
                        public void run() {
                            MediaPlayer.this.subtitleImageView.setVisibility(4);
                            MediaPlayer.this.subtitleTextView.setVisibility(4);
                            MediaPlayer.this.subtitleTextView.setText("");
                            MediaPlayer.this.clearLayout();
                            syncVisible.notify("notify UI set visible.");
                        }
                    });
                    syncVisible.wait("wait UI set visible...");
                } else {
                    this.subtitleImageView.setVisibility(4);
                    this.subtitleTextView.setVisibility(4);
                    this.subtitleTextView.setText("");
                    clearLayout();
                }
            }
            this.mUseExternalSurface = false;
            this.mIsExternalSurfaceTexture = false;
            this.mSurface = null;
            closeMediaCodec();
        } else {
            clearLayout();
            this.mPlayerThread = null;
            this.playerWorker = null;
        }
    }

    protected void clearLayout() {
        if (this.mIS_WINDOW && !this.mUseExternalSurface) {
            removeView(this.subtitleTextView);
            this.barFrameLayout.removeView(this.subtitleImageView);
            removeView(this.barFrameLayout);
            removeView(this.view);
        }
    }

    public void doFrame(long frameTimeNanos) {
        if (this.previousVsyncEnabe != 0 && this.playerWorker != null && this.playerWorker.player_inst[0] != 0) {
            Choreographer.getInstance().postFrameCallback(this);
            nativePlayerVsyncSetCurrentTime(this.playerWorker.player_inst, frameTimeNanos);
        }
    }

    public void onPause() {
        handlePause();
    }

    public void onResume() {
        handleResume();
    }

    public void onStart() {
    }

    public void onStop() {
        Close();
    }

    public void onDestroy() {
        Close();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        this.mHasFocus = hasFocus;
        if (hasFocus) {
            handleResume();
        }
    }

    public void onLowMemory() {
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == 25 || keyCode == 24) {
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    public void handlePause() {
        if (!this.mIsPaused && this.mIsSurfaceReady) {
            this.mIsPaused = true;
        }
    }

    public void handleResume() {
        if (this.mIsPaused && this.mIsSurfaceReady && this.mHasFocus) {
            this.mIsPaused = false;
        }
    }

    protected boolean onUnhandledMessage(int command, Object param) {
        return false;
    }

    public static boolean createGLContext(int majorVersion, int minorVersion, int[] attribs) {
        return initEGL(majorVersion, minorVersion, attribs);
    }

    public static void deleteGLContext() {
    }

    public static void flipBuffers() {
    }

    public static boolean setActivityTitle(String title) {
        return true;
    }

    public static boolean sendMessage(int command, int param) {
        return true;
    }

    protected int getInternalWidth() {
        if (this.mIS_WINDOW) {
            return getWidth();
        }
        return (int) this.mWidth;
    }

    protected int getInternalHeight() {
        if (this.mIS_WINDOW) {
            return getHeight();
        }
        return (int) this.mHeight;
    }

    protected void DrawReadyFrame() {
    }

    protected void startChoreographer() {
        final MediaPlayer mthis = this;
        new Handler(this.mContext.getMainLooper()).post(new Runnable() {
            public void run() {
                Choreographer.getInstance().postFrameCallback(mthis);
            }
        });
    }

    protected Surface GetReadySurface() {
        if (this.view != null) {
        }
        if (this.queueSurfaceCreate.size() > 1) {
            return null;
        }
        if (!(this.mSurface == null || this.mSurface.isValid())) {
            this.mSurface = null;
        }
        return this.mSurface;
    }

    protected void updateSizesAndAspects() {
        if (this.playerWorker != null && this.playerWorker.player_inst[0] != 0 && this.mVideoWidth > 0 && this.mVideoHeight > 0) {
            int[] src_x = new int[]{0};
            int[] src_y = new int[]{0};
            int[] src_w = new int[]{getInternalWidth()};
            int[] src_h = new int[]{getInternalHeight()};
            nativePlayerGetAspectRatioSizes(this.playerWorker.player_inst, this.playerConfig.getEnableAspectRatio(), this.playerConfig.getAspectRatioZoomModePercent(), this.playerConfig.getAspectRatioMoveModeX(), this.playerConfig.getAspectRatioMoveModeY(), src_x, src_y, src_w, src_h);
            if (src_w[0] > 0 && src_h[0] > 0) {
                setVideoSize(src_x[0], src_y[0], src_w[0], src_h[0]);
            }
        }
    }

    protected String getPath() {
        if (this.mContext.getPackageName().isEmpty()) {
            return "";
        }
        return this.mContext.getPackageName().replace('.', '/');
    }

    protected void notifyFirstVideoFrame() {
        this.got_first_frame = 1;
        if (this.view != null) {
            this.view.post(new Runnable() {
                public void run() {
                    if (MediaPlayer.this.IsHardwareDecoding()) {
                        MediaPlayer.this.UpdateView();
                    }
                    if (VERSION.SDK_INT >= 11) {
                        MediaPlayer.this.jb_setalpha(1.0f);
                    }
                }
            });
        }
    }

    protected int notifyStartDecoderSearch() {
        if (decoderSearch != 0) {
            return 1;
        }
        decoderSearch = 1;
        return 0;
    }

    protected int notifyStopDecoderSearch() {
        decoderSearch = 0;
        return 0;
    }

    @SuppressLint({"NewApi"})
    void jb_setalpha(float value) {
        this.view.setAlpha(value);
    }

    public static boolean initEGL(int majorVersion, int minorVersion, int[] attribs) {
        return false;
    }

    public static boolean createEGLContext() {
        return false;
    }

    public static boolean createEGLSurface() throws Exception {
        return false;
    }

    public static void flipEGL() {
    }

    public static int audioInit(int sampleRate, boolean is16Bit, boolean isStereo, int desiredFrames) {
        return 0;
    }

    public static void audioWriteShortBuffer(short[] buffer) {
    }

    public static void audioWriteByteBuffer(byte[] buffer) {
    }

    public static void audioQuit() {
    }

    protected int audioTrackInit(int samplerate, int format, int channels_num, int desiredFrames) {
        if (samplerate <= 0) {
            return -1;
        }
        int i;
        int i2;
        boolean isStereo = channels_num > 1;
        boolean is16Bit = format > 8;
        int channelConfig = isStereo ? 3 : 2;
        int audioFormat = is16Bit ? 2 : 3;
        if (isStereo) {
            i = 2;
        } else {
            i = 1;
        }
        if (is16Bit) {
            i2 = 2;
        } else {
            i2 = 1;
        }
        int frameSize = i * i2;
        try {
            desiredFrames = Math.max(desiredFrames, ((AudioTrack.getMinBufferSize(samplerate, channelConfig, audioFormat) + frameSize) - 1) / frameSize);
            int buf_time = ((desiredFrames * frameSize) * 1000) / (samplerate * frameSize);
            if (this.mAudioTrack != null) {
                return buf_time;
            }
            this.mAudioTrack = new AudioTrack(3, samplerate, channelConfig, audioFormat, desiredFrames * frameSize, 1);
            if (this.mAudioTrack.getState() != 1) {
                this.mAudioTrack = null;
                return -1;
            }
            this.mAudioTrack.play();
            return buf_time;
        } catch (IllegalArgumentException e) {
            this.mAudioTrack = null;
            return -1;
        }
    }

    protected void audioTrackWriteShortBuffer(short[] buffer) {
        this.mAudioTrack.write(buffer, 0, buffer.length);
    }

    protected void audioTrackWriteByteBuffer(ByteBuffer buffer, int size) {
        boolean is_mute = true;
        byte[] chunk = new byte[size];
        if (!this.m_internal_mute && this.playerConfig.getEnableAudio() == 1 && getState() == PlayerState.Started) {
            is_mute = false;
        }
        if (!is_mute && (this.m_fade_time == 0 || System.nanoTime() - this.m_fade_time >= 0)) {
            buffer.get(chunk);
        }
        buffer.clear();
        this.mAudioTrack.write(chunk, 0, chunk.length);
    }

    protected void audioTrackQuit() {
        if (this.mAudioTrack != null) {
            this.mAudioTrack.stop();
            this.mAudioTrack.release();
            this.mAudioTrack = null;
        }
    }

    protected void notifySourceMetadataReady(String mime, int width, int height, ByteBuffer buffer, int size) {
        this.mediaMime = "";
        this.mediaWidth = 0;
        this.mediaHeight = 0;
        this.mediaBuffer = null;
        this.mediaSize = 0;
        if (this.mUseExternalSurface && this.mSurface != null && !this.mIS_WINDOW) {
            if (this.playerWorker == null || !this.playerWorker.isPreview) {
                this.mediaMime = mime;
                this.mediaWidth = width;
                this.mediaHeight = height;
                this.mediaBuffer = buffer;
                this.mediaSize = size;
                if (this.mIsExternalSurfaceTexture) {
                    this.waitOpenSource.notify("Open source notify.. ");
                    this.waitOpenMediaCodec.wait("Wait open media codec... ");
                    return;
                }
                openMediaCodec(this.mSurface, this.mediaMime, this.mediaWidth, this.mediaHeight, this.mediaBuffer);
            }
        }
    }

    @SuppressLint({"NewApi"})
    protected boolean openMediaCodec(Surface surface, String mime, int videoWidth, int videoHeight, ByteBuffer extradata) {
        if (!this.mUseExternalSurface || this.mSurface == null || this.mIS_WINDOW) {
            return false;
        }
        try {
            this.internalMediaFormat = MediaFormat.createVideoFormat(mime, videoWidth, videoHeight);
            if (extradata != null) {
                ByteBuffer buf0 = ByteBuffer.allocate(extradata.capacity());
                extradata.position(0);
                buf0.put(extradata);
                buf0.position(0);
                String out = "";
                for (int k = 0; k < buf0.capacity(); k++) {
                    out = out + "," + buf0.get();
                }
                buf0.position(0);
                this.internalMediaFormat.setByteBuffer("csd-0", buf0);
                buf0.position(0);
            }
            this.internalMediaDecoder = MediaCodec.createDecoderByType(mime);
            if (this.internalMediaDecoder == null) {
                this.waitOpenMediaCodec.notify("Open mediacodec notify.. ");
                return false;
            }
            this.internalMediaDecoder.configure(this.internalMediaFormat, surface, null, 0);
            if (!(this.playerWorker.player_inst[0] == 0 || this.internalMediaFormat == null || this.internalMediaDecoder == null)) {
                nativePlayerSetExternalMediaCodec(this.playerWorker.player_inst, this.internalMediaFormat, this.internalMediaDecoder);
            }
            this.waitOpenMediaCodec.notify("Open mediacodec notify.. ");
            return true;
        } catch (Exception e) {
            this.internalMediaFormat = null;
            this.internalMediaDecoder = null;
        }
        return Boolean.parseBoolean(null);
    }

    @SuppressLint({"NewApi"})
    protected boolean reopenMediaCodec(Surface surface) {
        if (!this.mUseExternalSurface || this.mSurface == null || this.mIS_WINDOW) {
            return false;
        }
        try {
            Pause();
            this.internalMediaDecoder.stop();
            this.internalMediaDecoder.configure(this.internalMediaFormat, surface, null, 0);
            this.internalMediaDecoder.start();
            Play();
        } catch (Exception e) {
            this.internalMediaFormat = null;
            this.internalMediaDecoder = null;
        }
        return true;
    }

    @SuppressLint({"NewApi"})
    protected boolean closeMediaCodec() {
        try {
            if (this.internalMediaDecoder != null) {
                this.internalMediaDecoder.stop();
                this.internalMediaDecoder.release();
            }
            this.internalMediaFormat = null;
        } catch (Exception e) {
            this.internalMediaFormat = null;
            this.internalMediaDecoder = null;
        }
        return true;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        this.mIsSurfaceReady = true;
        holder.setFixedSize(getInternalWidth(), getInternalHeight());
        this.mSurface = holder.getSurface();
        this.queueSurfaceCreate.poll();
        this.waitSurfaceCreated.notify("Surface created notify.. ");
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        handlePause();
        this.mIsSurfaceReady = false;
        this.mSurface = null;
        this.mUseExternalSurface = false;
        this.mIsExternalSurfaceTexture = false;
    }

    public void surfaceRedrawNeeded(SurfaceHolder holder) {
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.mWidth = (float) width;
        this.mHeight = (float) height;
        this.mVideoWidth = width;
        this.mVideoHeight = height;
        if (getState() != PlayerState.Opening) {
            int sdlFormat = 353701890;
            switch (format) {
                case 1:
                    sdlFormat = 373694468;
                    break;
                case 2:
                    sdlFormat = 371595268;
                    break;
                case 3:
                    sdlFormat = 370546692;
                    break;
                case 4:
                    sdlFormat = 353701890;
                    break;
                case 6:
                    sdlFormat = 356782082;
                    break;
                case 7:
                    sdlFormat = 356651010;
                    break;
                case 11:
                    sdlFormat = 336660481;
                    break;
            }
            this.mIsSurfaceReady = true;
            if (!(this.playerWorker == null || this.playerWorker.player_inst[0] == 0)) {
                nativePlayerResize(this.playerWorker.player_inst, this.playerConfig.getEnableAspectRatio(), this.playerConfig.getAspectRatioZoomModePercent(), this.playerConfig.getAspectRatioMoveModeX(), this.playerConfig.getAspectRatioMoveModeY(), width, height, sdlFormat, Color.red(this.playerConfig.getColorBackground()), Color.green(this.playerConfig.getColorBackground()), Color.blue(this.playerConfig.getColorBackground()), Color.alpha(this.playerConfig.getColorBackground()));
            }
            if (!IsHardwareDecoding()) {
                return;
            }
            if (this.set_size_layout == 1) {
                this.set_size_layout = 0;
            } else if (this.set_size_layout == 2) {
                holder.setSizeFromLayout();
                this.view.requestLayout();
                this.set_size_layout = 0;
            } else {
                holder.setFixedSize((int) this.mWidth, (int) this.mHeight);
            }
        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (this.playerConfig.getDecodingType() > 0 && IsHardwareDecoding() && this.playerWorker != null && this.playerWorker.player_inst[0] != 0 && w > 0 && h > 0) {
            int[] src_x = new int[]{0};
            int[] src_y = new int[]{0};
            int[] src_w = new int[]{w};
            int[] src_h = new int[]{h};
            nativePlayerGetAspectRatioSizes(this.playerWorker.player_inst, this.playerConfig.getEnableAspectRatio(), this.playerConfig.getAspectRatioZoomModePercent(), this.playerConfig.getAspectRatioMoveModeX(), this.playerConfig.getAspectRatioMoveModeY(), src_x, src_y, src_w, src_h);
            if (src_w[0] > 0 && src_h[0] > 0) {
                setVideoSize(src_x[0], src_y[0], src_w[0], src_h[0]);
            }
        } else if (!(IsHardwareDecoding() || this.playerWorker == null || this.playerWorker.player_inst[0] == 0 || w <= 0 || h <= 0)) {
            setVideoSize(0, 0, w, h);
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void setSubLayerSize(int x, int y, int w, int h) {
        if ((getState() == PlayerState.Started || getState() == PlayerState.Paused || this.got_first_frame == 1) && this.barFrameLayout != null) {
            LayoutParams params = (LayoutParams) this.barFrameLayout.getLayoutParams();
            if (params != null) {
                if (w - x == getInternalWidth() && h - y == getInternalHeight()) {
                    params.leftMargin = 0;
                    params.topMargin = 0;
                    params.rightMargin = 0;
                    params.bottomMargin = 0;
                    params.width = getInternalWidth();
                    params.height = getInternalHeight();
                } else if (this.playerConfig.getEnableAspectRatio() == 5) {
                    params.leftMargin = x;
                    params.topMargin = (getInternalHeight() - y) - h;
                    params.rightMargin = w > getInternalWidth() ? ((-w) - params.leftMargin) + getInternalWidth() : (getInternalWidth() - w) - params.leftMargin;
                    params.bottomMargin = h > getInternalHeight() ? ((-h) - params.topMargin) + getInternalHeight() : (getInternalHeight() - h) - params.topMargin;
                    params.width = w;
                    params.height = h;
                } else {
                    params.leftMargin = x;
                    params.topMargin = (getInternalHeight() - y) - h;
                    params.rightMargin = w > getInternalWidth() ? ((-w) - params.leftMargin) + getInternalWidth() : (getInternalWidth() - w) - params.leftMargin;
                    params.bottomMargin = h > getInternalHeight() ? ((-h) - params.topMargin) + getInternalHeight() : (getInternalHeight() - h) - params.topMargin;
                    params.width = -1;
                    params.height = -1;
                }
                this.barFrameLayout.post(new Runnable() {
                    public void run() {
                        MediaPlayer.this.barFrameLayout.requestLayout();
                    }
                });
            }
        }
    }

    private void setVideoSize(int x, int y, int w, int h) {
        if ((getState() == PlayerState.Started || getState() == PlayerState.Paused || this.got_first_frame == 1) && this.view != null) {
            setSubLayerSize(x, y, w, h);
            if (this.playerConfig.getDecodingType() <= 0 || !IsHardwareDecoding()) {
                if (!(this.prev_W == getInternalWidth() && this.prev_H == getInternalHeight())) {
                    this.view.post(new Runnable() {
                        public void run() {
                            MediaPlayer.this.set_size_layout = 1;
                            MediaPlayer.this.view.getHolder().setSizeFromLayout();
                        }
                    });
                }
                this.invalidating = 1;
                this.view.post(new Runnable() {
                    public void run() {
                        if (MediaPlayer.this.invalidating == 1) {
                            MediaPlayer.this.view.invalidate();
                            MediaPlayer.this.invalidating = 0;
                        }
                    }
                });
                this.prev_W = getInternalWidth();
                this.prev_H = getInternalHeight();
                return;
            }
            LayoutParams params = (LayoutParams) this.view.getLayoutParams();
            if (params != null) {
                int r1;
                if (w - x == getInternalWidth() && h - y == getInternalHeight()) {
                    params.leftMargin = 0;
                    params.topMargin = 0;
                    params.rightMargin = 0;
                    params.bottomMargin = 0;
                    params.width = -1;
                    params.height = -1;
                } else if (this.playerConfig.getEnableAspectRatio() == 5) {
                    params.leftMargin = x;
                    params.topMargin = (getInternalHeight() - y) - h;
                    params.rightMargin = w > getInternalWidth() ? ((-w) - params.leftMargin) + getInternalWidth() : (getInternalWidth() - w) - params.leftMargin;
                    if (h > getInternalHeight()) {
                        r1 = ((-h) - params.topMargin) + getInternalHeight();
                    } else {
                        r1 = (getInternalHeight() - h) - params.topMargin;
                    }
                    params.bottomMargin = r1;
                    params.width = w;
                    params.height = h;
                } else {
                    params.leftMargin = x;
                    params.topMargin = (getInternalHeight() - y) - h;
                    params.rightMargin = w > getInternalWidth() ? ((-w) - params.leftMargin) + getInternalWidth() : (getInternalWidth() - w) - params.leftMargin;
                    if (h > getInternalHeight()) {
                        r1 = ((-h) - params.topMargin) + getInternalHeight();
                    } else {
                        r1 = (getInternalHeight() - h) - params.topMargin;
                    }
                    params.bottomMargin = r1;
                    params.width = -1;
                    params.height = -1;
                }
                if (!(((this.playerConfig.getEnableAspectRatio() == 4 || this.playerConfig.getEnableAspectRatio() == 5) && this.prev_W == getInternalWidth() && this.prev_H == getInternalHeight()) || this.view == null)) {
                    this.view.post(new Runnable() {
                        public void run() {
                            MediaPlayer.this.set_size_layout = 1;
                            MediaPlayer.this.view.getHolder().setSizeFromLayout();
                        }
                    });
                }
                this.view.post(new Runnable() {
                    public void run() {
                        MediaPlayer.this.view.requestLayout();
                    }
                });
                this.prev_W = getInternalWidth();
                this.prev_H = getInternalHeight();
            }
        }
    }

    public void onDraw(Canvas canvas) {
    }

    public boolean onTouch(View v, MotionEvent event) {
        int touchDevId = event.getDeviceId();
        int pointerCount = event.getPointerCount();
        int actionPointerIndex = (event.getAction() & 65280) >> 8;
        int pointerFingerId = event.getPointerId(actionPointerIndex);
        int action = event.getAction() & 255;
        float x = event.getX(actionPointerIndex) / this.mWidth;
        float y = event.getY(actionPointerIndex) / this.mHeight;
        float p = event.getPressure(actionPointerIndex);
        if (action == 2 && pointerCount > 1) {
            for (int i = 0; i < pointerCount; i++) {
                pointerFingerId = event.getPointerId(i);
                x = event.getX(i) / this.mWidth;
                y = event.getY(i) / this.mHeight;
                p = event.getPressure(i);
            }
        }
        return false;
    }

    public void enableSensor(int sensortype, boolean enabled) {
        if (enabled) {
            this.mSensorManager.registerListener(this, this.mSensorManager.getDefaultSensor(sensortype), 2, null);
        } else {
            this.mSensorManager.unregisterListener(this, this.mSensorManager.getDefaultSensor(sensortype));
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
    }

    private ByteBuffer str_to_bb(String msg) {
        try {
            return this.encoder.encode(CharBuffer.wrap(msg));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String bb_to_str(ByteBuffer buffer) {
        String data = "";
        try {
            int old_position = buffer.position();
            data = this.decoder.decode(buffer).toString();
            buffer.position(old_position);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    protected int OnReceiveSubtitle(ByteBuffer buffer, int size, long end_time, int format, int w, int h, int x, int y, int v_w, int v_h) {
        if ((format == 1 || format == 2) && size > 0) {
            ByteBuffer bb = ByteBuffer.allocate(size);
            bb.put(buffer);
            bb.rewind();
            final String s = bb_to_str(bb);
            final boolean single_line = format == 1;
            if (this.CallbackSubtitle != null && this.CallbackSubtitle.OnReceiveSubtitleString(s, end_time) == 0) {
                return 0;
            }
            this.subtitleTextView.removeCallbacks(this.runnableInformerHide);
            this.subtitleTextView.postDelayed(this.runnableInformerHide, end_time);
            this.subtitleTextView.post(new Runnable() {
                public void run() {
                    MediaPlayer.this.subtitleTextView.setVisibility(0);
                    MediaPlayer.this.subtitleTextView.setSingleLine(single_line);
                    MediaPlayer.this.subtitleTextView.setText(s);
                }
            });
        } else if (format == 16 && w > 0 && h > 0 && v_w > 0 && v_h > 0) {
            if (w > v_w) {
                v_w = w;
            }
            final Bitmap bm = Bitmap.createBitmap(v_w, v_h, Config.ARGB_8888);
            int[] result = new int[(w * h)];
            buffer.rewind();
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            while (buffer.remaining() > 0) {
                result[buffer.position() / 4] = buffer.getInt();
            }
            if (x >= 0 && y >= 0) {
                if (x + w > v_w) {
                    x = v_w - w;
                }
                try {
                    bm.setPixels(result, 0, w, x, y, w, h);
                } catch (IllegalStateException e) {
                    Log.e(TAG, "OnReceiveSubtitle IllegalStateException1 " + e);
                } catch (IllegalArgumentException e2) {
                    Log.e(TAG, "OnReceiveSubtitle IllegalArgumentException2 " + e2);
                } catch (ArrayIndexOutOfBoundsException e3) {
                    Log.e(TAG, "OnReceiveSubtitle IllegalArgumentException3 " + e3);
                }
            }
            this.subtitleImageView.removeCallbacks(this.runnableInformerHide_image);
            this.subtitleImageView.postDelayed(this.runnableInformerHide_image, end_time);
            this.subtitleImageView.post(new Runnable() {
                public void run() {
                    MediaPlayer.this.subtitleImageView.setImageBitmap(bm);
                    MediaPlayer.this.subtitleImageView.setVisibility(0);
                }
            });
        }
        return 0;
    }

    protected void abrClose() {
        if (this.abrGetHLSStreamsThread != null) {
            synchronized (this.abrGetHLSStreamsThread) {
                this.abrGetHLSStreamsThread.interrupt();
                this.abrGetHLSStreamsThread = null;
            }
        }
        if (this.abrHLSStreams != null) {
            this.abrHLSStreams.clear();
            this.abrHLSStreams = null;
        }
        this.time_cur = 0;
        this.fps_estim = 0.0f;
        this.pers_free_estim = 0;
        this.count = 0;
        this.render_pos = 0;
        this.is_need_decrease = false;
        this.abrCurrentPlayedStreamId = 0;
        this.abrPreviousPlayedStreamId = 0;
    }

    protected synchronized void abrGetHLSStreams() {
        if (this.abrHLSStreams == null && this.abrGetHLSStreamsThread == null) {
            this.abrGetHLSStreamsThread = new HLSThread() {
                public void run() {
                    M3U8 m3u8 = new M3U8();
                    m3u8.getDataAndParse(MediaPlayer.this.playerConfig.getConnectionUrl());
                    MediaPlayer.this.abrHLSStreams = m3u8.getChannelList();
                    if (MediaPlayer.this.abrHLSStreams != null) {
                        for (int i = 0; i < MediaPlayer.this.abrHLSStreams.size(); i++) {
                            Log.i(MediaPlayer.TAG, "abrGetHLSStreams URL:" + ((HLSStream) MediaPlayer.this.abrHLSStreams.get(i)).URL + " ID:" + ((HLSStream) MediaPlayer.this.abrHLSStreams.get(i)).ID + " BANDWIDTH:" + ((HLSStream) MediaPlayer.this.abrHLSStreams.get(i)).BANDWIDTH + " CODECS:" + ((HLSStream) MediaPlayer.this.abrHLSStreams.get(i)).CODECS + " RESOLUTION:" + ((HLSStream) MediaPlayer.this.abrHLSStreams.get(i)).RESOLUTION + " ");
                        }
                    }
                    MediaPlayer.this.waitGetHLSStreams.notify("abrGetHLSStreams list ready.");
                    MediaPlayer.this.abrGetHLSStreamsThread = null;
                }
            };
            synchronized (this.abrGetHLSStreamsThread) {
                this.abrGetHLSStreamsThread.start();
            }
        }
    }

    public List<HLSStream> abrGetListStreams() {
        return this.abrHLSStreams;
    }

    public int adrGetCurrentId() {
        return this.abrCurrentPlayedStreamId;
    }

    public void abrSetCurrentId(int id) {
        this.abrSetPlayedStreamId = id;
    }

    protected void abrChangeStream(int abrNewStreamId) {
        this.abrSetPlayedStreamId = abrNewStreamId;
        this.abrCurrentPlayedStreamId = abrNewStreamId;
        if (this.abrHLSStreams != null && this.abrHLSStreams.size() > 0 && this.playerWorker != null && this.playerWorker.player_inst[0] != 0) {
            int i;
            long currentPosition = getRenderPosition();
            Position pos = getLiveStreamPosition();
            nativePlayerClose(this.playerWorker.player_inst);
            this.playerConfig.setStartOffest(pos.getLast() - currentPosition);
            MediaPlayerConfig mediaPlayerConfig = this.playerConfig;
            if (this.abrHLSStreams.size() <= abrNewStreamId) {
                i = 0;
            } else {
                i = ((HLSStream) this.abrHLSStreams.get(abrNewStreamId)).ext_stream;
            }
            mediaPlayerConfig.setExtStream(i);
            nativePlayerSetOptions(this.playerWorker.player_inst, this.playerConfig.getConnectionNetworkProtocol(), this.playerConfig.getConnectionDetectionTime(), this.playerConfig.getConnectionBufferingType(), this.playerConfig.getConnectionBufferingTime(), this.playerConfig.getConnectionBufferingSize(), this.playerConfig.getConnectionTimeout(), this.playerConfig.getInterruptOnClose(), this.playerConfig.getExtraDataFilter(), this.playerConfig.getDecodingType(), this.playerConfig.getDecoderLatency(), this.playerConfig.getRendererType(), this.playerConfig.getSynchroEnable(), this.playerConfig.getSynchroNeedDropVideoFrames(), this.playerConfig.getDropOnFastPlayback(), this.playerConfig.getEnableColorVideo(), this.playerConfig.getEnableAspectRatio(), this.playerConfig.getAspectRatioZoomModePercent(), this.playerConfig.getAspectRatioMoveModeX(), this.playerConfig.getAspectRatioMoveModeY(), this.playerConfig.getNumberOfCPUCores(), (int) this.playerConfig.getBogoMIPS(), Color.red(this.playerConfig.getColorBackground()), Color.green(this.playerConfig.getColorBackground()), Color.blue(this.playerConfig.getColorBackground()), Color.alpha(this.playerConfig.getColorBackground()), this.playerConfig.getSslKey(), this.playerConfig.getStartOffest(), this.playerConfig.getStartPreroll(), this.playerConfig.getStartPath(), this.playerConfig.getStartCookies(), this.playerConfig.getExtStream(), this.playerConfig.getPlaybackSendPlayPauseToServer());
            int rc = nativePlayerOpen(this.playerWorker.player_inst, this.playerConfig.getConnectionUrl(), 0, this.playerConfig.getDataReceiveTimeout());
            if (this.playerWorker.finish) {
                if (this.Callback != null) {
                    nativePlayerSetCallback(this.playerWorker.player_inst, this.Callback);
                }
                this.waitOpenSource.notify("Open source notify.. ");
                this.waitOpenMediaCodec.notify("Open mediacodec notify.. ");
                nativePlayerClose(this.playerWorker.player_inst);
                return;
            }
            if (rc != 0) {
                nativePlayerClose(this.playerWorker.player_inst);
                this.playerConfig.setStartOffest(pos.getLast() - currentPosition);
                this.playerConfig.setExtStream(this.abrCurrentPlayedStreamId);
                nativePlayerSetOptions(this.playerWorker.player_inst, this.playerConfig.getConnectionNetworkProtocol(), this.playerConfig.getConnectionDetectionTime(), this.playerConfig.getConnectionBufferingType(), this.playerConfig.getConnectionBufferingTime(), this.playerConfig.getConnectionBufferingSize(), this.playerConfig.getConnectionTimeout(), this.playerConfig.getInterruptOnClose(), this.playerConfig.getExtraDataFilter(), this.playerConfig.getDecodingType(), this.playerConfig.getDecoderLatency(), this.playerConfig.getRendererType(), this.playerConfig.getSynchroEnable(), this.playerConfig.getSynchroNeedDropVideoFrames(), this.playerConfig.getDropOnFastPlayback(), this.playerConfig.getEnableColorVideo(), this.playerConfig.getEnableAspectRatio(), this.playerConfig.getAspectRatioZoomModePercent(), this.playerConfig.getAspectRatioMoveModeX(), this.playerConfig.getAspectRatioMoveModeY(), this.playerConfig.getNumberOfCPUCores(), (int) this.playerConfig.getBogoMIPS(), Color.red(this.playerConfig.getColorBackground()), Color.green(this.playerConfig.getColorBackground()), Color.blue(this.playerConfig.getColorBackground()), Color.alpha(this.playerConfig.getColorBackground()), this.playerConfig.getSslKey(), this.playerConfig.getStartOffest(), this.playerConfig.getStartPreroll(), this.playerConfig.getStartPath(), this.playerConfig.getStartCookies(), this.playerConfig.getExtStream(), this.playerConfig.getPlaybackSendPlayPauseToServer());
                rc = nativePlayerOpen(this.playerWorker.player_inst, this.playerConfig.getConnectionUrl(), 0, this.playerConfig.getDataReceiveTimeout());
            }
            if (rc != 0 || this.playerWorker.finish) {
                if (this.Callback != null) {
                    nativePlayerSetCallback(this.playerWorker.player_inst, this.Callback);
                }
                this.waitOpenSource.notify("Open source notify.. ");
                this.waitOpenMediaCodec.notify("Open mediacodec notify.. ");
                nativePlayerClose(this.playerWorker.player_inst);
                return;
            }
            UpdateView();
            if (this.Callback != null) {
                nativePlayerSetCallback(this.playerWorker.player_inst, this.Callback);
            }
            this.abrPreviousPlayedStreamId = this.abrCurrentPlayedStreamId;
            this.abrCurrentPlayedStreamId = abrNewStreamId;
            this.time_cur = System.currentTimeMillis();
        }
    }

    protected void abrCheckBitrateAndChangeStream() {
        if (this.abrHLSStreams == null) {
            abrGetHLSStreams();
        } else if (this.abrHLSStreams.size() > 0) {
            if (this.time_cur == 0) {
                this.time_cur = System.currentTimeMillis();
            }
            float fps = GetStatFPS();
            int pers_free = GetStatPercFree();
            this.render_pos = getRenderPosition();
            if (fps < 0.0f || getState() != PlayerState.Started) {
                this.count = 0;
                this.fps_estim = 0.0f;
                this.pers_free_estim = 0;
                this.time_cur = System.currentTimeMillis();
            } else {
                this.count++;
                this.fps_estim += fps;
                this.pers_free_estim += pers_free;
                if (System.currentTimeMillis() - this.time_cur > 5000) {
                    this.time_cur = System.currentTimeMillis();
                    this.fps_estim /= (float) this.count;
                    this.pers_free_estim /= this.count;
                    if (this.fps_estim < 5.0f || this.pers_free_estim >= 99) {
                        this.is_need_decrease = true;
                    }
                    this.count = 0;
                    this.fps_estim = 0.0f;
                    this.pers_free_estim = 0;
                }
            }
            if ((this.count <= 0 || this.count % 5 != 0) && this.is_need_decrease) {
            }
            if (getState() == PlayerState.Started && this.abrCurrentPlayedStreamId >= 0 && this.is_need_decrease && this.abrHLSStreams != null && this.abrHLSStreams.size() > this.abrCurrentPlayedStreamId + 1) {
                this.time_cur = System.currentTimeMillis();
                int nhls = this.abrCurrentPlayedStreamId + 1;
                if (nhls < this.abrHLSStreams.size()) {
                    abrChangeStream(nhls);
                    this.is_need_decrease = false;
                }
            }
        }
    }
}
