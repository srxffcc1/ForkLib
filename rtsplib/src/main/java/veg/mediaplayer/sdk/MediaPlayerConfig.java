package veg.mediaplayer.sdk;

import java.util.ArrayList;

import veg.mediaplayer.sdk.MediaPlayer.PlayerModes;

public class MediaPlayerConfig {
    private static final String TAG = "MediaPlayerConfig";
    private int ConnectionTimeout = 60000;
    private int EnableInterruptOnClose = 1;
    private int aspectRatioMode = 1;
    private int aspectRatioMoveModeX = -1;
    private int aspectRatioMoveModeY = -1;
    private int aspectRatioZoomModePercent = 100;
    private float bogoMIPS = 0.0f;
    private int colorBackground = -16777216;
    private int connectionBufferingSize = 0;
    private int connectionBufferingTime = 1000;
    private int connectionBufferingType = 0;
    private int connectionDetectionTime = 5000;
    private int connectionNetworkProtocol = -1;
    private String connectionUrl = "";
    private int dataReceiveTimeout = 60000;
    private int decoderLatency = 0;
    private int decodingType = 0;
    private int enableABR = 0;
    private int enableAudio = 1;
    private int enableColorVideo = 1;
    private int ext_stream = 0;
    private int extra_data_filter = 0;
    private int fade_on_rate = 1;
    private int fade_on_seek = 1;
    private int fade_on_start = 1;
    private int ff_rate = 0;
    private int nm3u8_id = 0;
    private int numberOfCPUCores = 1;
    private int playbackSendPlayPauseToServer = 0;
    private int playerMode = PlayerModes.PP_MODE_ALL.val();
    private int record_flags = 0;
    private String record_path = "";
    private String record_prefix = "";
    private int record_split_size = 0;
    private int record_split_time = 0;
    private long record_trim_pos_end = -1;
    private long record_trim_pos_start = -1;
    private int rendererType = 1;
    private int selectAudio = 0;
    private int selectSubtitle = -1;
    private String sslKey = "";
    private String startCookies = "";
    private long startOffest = Long.MIN_VALUE;
    private String startPath = "";
    private int startPreroll = 0;
    public ArrayList<String> subtitlePaths = new ArrayList();
    private int synchroEnable = 1;
    private int synchroNeedDropFramesOnFF = 1;
    private int synchroNeedDropVideoFrames = 0;
    private int volume_boost = 0;
    private int volume_detect_max_samples = 0;
    private int vsyncEnable = 0;

    public MediaPlayerConfig() {
        resetToDefault();
    }

    public MediaPlayerConfig(String connectionUrl, int connectionNetworkProtocol, int connectionDetectionTime, int connectionBufferingType, int connectionBufferingTime, int connectionBufferingSize, int dataReceiveTimeout, int ConnectionTimeout, int EnableInterruptOnClose, int decodingType, int decoderLatency, int rendererType, int synchroEnable, int synchroNeedDropVideoFrames, int enableColorVideo, int aspectRatioMode, int aspectRatioZoomModePercent, int aspectRatioMoveModeX, int aspectRatioMoveModeY, int enableAudio, int colorBackground, int numberOfCPUCores, float bogoMIPS, String sslKey, long startOffest, int startPreroll, String startPath, String startCookies, String record_path, int record_flags, int record_split_time, int record_split_size, String record_prefix, int selectAudio, int selectSubtitle, int enableABR, int playbackSendPlayPauseToServer, int vsyncEnable, int extra_data_filter) {
        this.connectionUrl = connectionUrl;
        this.connectionNetworkProtocol = connectionNetworkProtocol;
        this.connectionDetectionTime = connectionDetectionTime;
        this.connectionBufferingType = connectionBufferingType;
        this.connectionBufferingTime = connectionBufferingTime;
        this.connectionBufferingSize = connectionBufferingSize;
        this.dataReceiveTimeout = dataReceiveTimeout;
        this.ConnectionTimeout = ConnectionTimeout;
        this.EnableInterruptOnClose = EnableInterruptOnClose;
        this.decodingType = decodingType;
        this.decoderLatency = decoderLatency;
        this.rendererType = rendererType;
        this.synchroEnable = synchroEnable;
        this.synchroNeedDropVideoFrames = synchroNeedDropVideoFrames;
        this.enableColorVideo = enableColorVideo;
        this.aspectRatioMode = aspectRatioMode;
        this.aspectRatioZoomModePercent = aspectRatioZoomModePercent;
        this.aspectRatioMoveModeX = aspectRatioMoveModeX;
        this.aspectRatioMoveModeY = aspectRatioMoveModeY;
        this.enableAudio = enableAudio;
        this.colorBackground = colorBackground;
        this.numberOfCPUCores = numberOfCPUCores;
        this.bogoMIPS = bogoMIPS;
        this.sslKey = sslKey;
        this.startOffest = startOffest;
        this.startPreroll = startPreroll;
        this.startPath = startPath;
        this.startCookies = startCookies;
        this.record_path = record_path;
        this.record_flags = record_flags;
        this.record_split_time = record_split_time;
        this.record_split_size = record_split_size;
        this.record_prefix = record_prefix;
        this.selectAudio = selectAudio;
        this.selectSubtitle = selectSubtitle;
        this.enableABR = enableABR;
        this.playbackSendPlayPauseToServer = playbackSendPlayPauseToServer;
        this.vsyncEnable = vsyncEnable;
        this.extra_data_filter = extra_data_filter;
    }

    public MediaPlayerConfig(MediaPlayerConfig src) {
        this.connectionUrl = src.connectionUrl;
        this.connectionNetworkProtocol = src.connectionNetworkProtocol;
        this.connectionDetectionTime = src.connectionDetectionTime;
        this.connectionBufferingType = src.connectionBufferingType;
        this.connectionBufferingTime = src.connectionBufferingTime;
        this.connectionBufferingSize = src.connectionBufferingSize;
        this.dataReceiveTimeout = src.dataReceiveTimeout;
        this.ConnectionTimeout = src.ConnectionTimeout;
        this.EnableInterruptOnClose = src.EnableInterruptOnClose;
        this.decodingType = src.decodingType;
        this.decoderLatency = src.decoderLatency;
        this.rendererType = src.rendererType;
        this.synchroEnable = src.synchroEnable;
        this.synchroNeedDropVideoFrames = src.synchroNeedDropVideoFrames;
        this.enableColorVideo = src.enableColorVideo;
        this.aspectRatioMode = src.aspectRatioMode;
        this.aspectRatioZoomModePercent = src.aspectRatioZoomModePercent;
        this.aspectRatioMoveModeX = src.aspectRatioMoveModeX;
        this.aspectRatioMoveModeY = src.aspectRatioMoveModeY;
        this.enableAudio = src.enableAudio;
        this.colorBackground = src.colorBackground;
        this.numberOfCPUCores = src.numberOfCPUCores;
        this.bogoMIPS = src.bogoMIPS;
        this.sslKey = src.sslKey;
        this.startOffest = src.startOffest;
        this.startPreroll = src.startPreroll;
        this.startPath = src.startPath;
        this.startCookies = src.startCookies;
        this.ext_stream = src.ext_stream;
        this.nm3u8_id = src.nm3u8_id;
        this.record_path = src.record_path;
        this.record_flags = src.record_flags;
        this.record_split_time = src.record_split_time;
        this.record_split_size = src.record_split_size;
        this.record_prefix = src.record_prefix;
        this.record_trim_pos_start = src.record_trim_pos_start;
        this.record_trim_pos_end = src.record_trim_pos_end;
        this.selectAudio = src.selectAudio;
        this.selectSubtitle = src.selectSubtitle;
        this.subtitlePaths = src.subtitlePaths;
        this.ff_rate = src.ff_rate;
        this.volume_detect_max_samples = src.volume_detect_max_samples;
        this.volume_boost = src.volume_boost;
        this.playerMode = src.playerMode;
        this.enableABR = src.enableABR;
        this.vsyncEnable = src.vsyncEnable;
        this.extra_data_filter = src.extra_data_filter;
    }

    public int getMode() {
        return this.playerMode;
    }

    public void setMode(int playerMode) {
        this.playerMode = playerMode;
    }

    public void setMode(PlayerModes playerMode) {
        this.playerMode = playerMode.val();
    }

    public int getColorBackground() {
        return this.colorBackground;
    }

    public void setColorBackground(int colorBackground) {
        this.colorBackground = colorBackground;
    }

    public int getEnableAspectRatio() {
        return this.aspectRatioMode;
    }

    public void setEnableAspectRatio(int aspectRatioMode) {
        this.aspectRatioMode = aspectRatioMode;
    }

    public int getAspectRatioMode() {
        return this.aspectRatioMode;
    }

    public void setAspectRatioMode(int aspectRatioMode) {
        this.aspectRatioMode = aspectRatioMode;
    }

    public int getAspectRatioZoomModePercent() {
        return this.aspectRatioZoomModePercent;
    }

    public void setAspectRatioZoomModePercent(int aspectRatioZoomModePercent) {
        this.aspectRatioZoomModePercent = aspectRatioZoomModePercent;
    }

    public int getAspectRatioMoveModeX() {
        return this.aspectRatioMoveModeX;
    }

    public void setAspectRatioMoveModeX(int aspectRatioMoveModeX) {
        this.aspectRatioMoveModeX = aspectRatioMoveModeX;
    }

    public int getAspectRatioMoveModeY() {
        return this.aspectRatioMoveModeY;
    }

    public void setAspectRatioMoveModeY(int aspectRatioMoveModeY) {
        this.aspectRatioMoveModeY = aspectRatioMoveModeY;
    }

    public long getStartOffest() {
        return this.startOffest;
    }

    public void setStartOffest(long startOffest) {
        this.startOffest = startOffest;
    }

    public int getEnableAudio() {
        return this.enableAudio;
    }

    public void setEnableAudio(int enableAudio) {
        this.enableAudio = enableAudio;
    }

    public String getSslKey() {
        return this.sslKey;
    }

    public void setSslKey(String sslKey) {
        this.sslKey = sslKey;
    }

    public int getExtStream() {
        return this.ext_stream;
    }

    public void setExtStream(int ext_stream) {
        this.ext_stream = ext_stream;
    }

    public int getM3U8Id() {
        return this.nm3u8_id;
    }

    public void setM3U8Id(int id) {
        this.nm3u8_id = id;
    }

    public int getSelectedAudio() {
        return this.selectAudio;
    }

    public void setSelectedAudio(int selectedA) {
        this.selectAudio = selectedA;
    }

    public int getSelectedSubtitle() {
        return this.selectSubtitle;
    }

    public void setSelectedSubtitle(int selectedS) {
        this.selectSubtitle = selectedS;
    }

    public String getConnectionUrl() {
        return this.connectionUrl;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public int getDecodingType() {
        return this.decodingType;
    }

    public void setDecodingType(int decodingType) {
        this.decodingType = decodingType;
    }

    public int getDecoderLatency() {
        return this.decoderLatency;
    }

    public void setDecoderLatency(int decoderLatency) {
        this.decoderLatency = decoderLatency;
    }

    public int getRendererType() {
        return this.rendererType;
    }

    public void setRendererType(int rendererType) {
        this.rendererType = rendererType;
    }

    public int getSynchroEnable() {
        return this.synchroEnable;
    }

    public void setSynchroEnable(int synchroEnable) {
        this.synchroEnable = synchroEnable;
    }

    public int getSynchroNeedDropVideoFrames() {
        return this.synchroNeedDropVideoFrames;
    }

    public void setSynchroNeedDropVideoFrames(int synchroNeedDropVideoFrames) {
        this.synchroNeedDropVideoFrames = synchroNeedDropVideoFrames;
    }

    public int getDropOnFastPlayback() {
        return this.synchroNeedDropFramesOnFF;
    }

    public void setDropOnFastPlayback(int synchroNeedDropFramesOnFF) {
        this.synchroNeedDropFramesOnFF = synchroNeedDropFramesOnFF;
    }

    public int getEnableColorVideo() {
        return this.enableColorVideo;
    }

    public void setEnableColorVideo(int enableColorVideo) {
        this.enableColorVideo = enableColorVideo;
    }

    public int getNumberOfCPUCores() {
        return this.numberOfCPUCores;
    }

    public void setNumberOfCPUCores(int numberOfCPUCores) {
        this.numberOfCPUCores = numberOfCPUCores;
    }

    public int getConnectionNetworkProtocol() {
        return this.connectionNetworkProtocol;
    }

    public void setConnectionNetworkProtocol(int connectionNetworkProtocol) {
        this.connectionNetworkProtocol = connectionNetworkProtocol;
    }

    public int getConnectionDetectionTime() {
        return this.connectionDetectionTime;
    }

    public void setConnectionDetectionTime(int connectionDetectionTime) {
        this.connectionDetectionTime = connectionDetectionTime;
    }

    public int getConnectionBufferingType() {
        return this.connectionBufferingType;
    }

    public void setConnectionBufferingType(int connectionBufferingType) {
        this.connectionBufferingType = connectionBufferingType;
    }

    public int getConnectionBufferingTime() {
        return this.connectionBufferingTime;
    }

    public void setConnectionBufferingTime(int connectionBufferingTime) {
        this.connectionBufferingTime = connectionBufferingTime;
    }

    public int getConnectionBufferingSize() {
        return this.connectionBufferingSize;
    }

    public void setConnectionBufferingSize(int connectionBufferingSize) {
        this.connectionBufferingSize = connectionBufferingSize;
    }

    public int getDataReceiveTimeout() {
        return this.dataReceiveTimeout;
    }

    public void setDataReceiveTimeout(int dataReceiveTimeout) {
        this.dataReceiveTimeout = dataReceiveTimeout;
    }

    public int getConnectionTimeout() {
        return this.ConnectionTimeout;
    }

    public void setConnectionTimeout(int ConnectionTimeout) {
        this.ConnectionTimeout = ConnectionTimeout;
    }

    public int getInterruptOnClose() {
        return this.EnableInterruptOnClose;
    }

    public void setInterruptOnClose(int EnableInterruptOnClose) {
        this.EnableInterruptOnClose = EnableInterruptOnClose;
    }

    public int getExtraDataFilter() {
        return this.extra_data_filter;
    }

    public void setExtraDataFilter(int extra_data_filter) {
        this.extra_data_filter = extra_data_filter;
    }

    public float getBogoMIPS() {
        return this.bogoMIPS;
    }

    public void setBogoMIPS(float bogoMIPS) {
        this.bogoMIPS = bogoMIPS;
    }

    public int getStartPreroll() {
        return this.startPreroll;
    }

    public void setStartPreroll(int startPreroll) {
        this.startPreroll = startPreroll;
    }

    public String getStartPath() {
        return this.startPath;
    }

    public void setStartPath(String startPath) {
        this.startPath = startPath;
    }

    public String getStartCookies() {
        return this.startCookies;
    }

    public void setStartCookies(String cookies) {
        this.startCookies = cookies;
    }

    public int getFadeOnStart() {
        return this.fade_on_start;
    }

    public void setFadeOnStart(int fade_on_start) {
        this.fade_on_start = fade_on_start;
    }

    public int getFadeOnSeek() {
        return this.fade_on_seek;
    }

    public void setFadeOnSeek(int fade_on_seek) {
        this.fade_on_seek = fade_on_seek;
    }

    public int getFFRate() {
        return this.ff_rate;
    }

    public void setFFRate(int rate) {
        this.ff_rate = rate;
    }

    public int getVolumeDetectMaxSamples() {
        return this.volume_detect_max_samples;
    }

    public void setVolumeDetectMaxSamples(int volume_detect_max_samples) {
        this.volume_detect_max_samples = volume_detect_max_samples;
    }

    public int getVolumeBoost() {
        return this.volume_boost;
    }

    public void setVolumeBoost(int volume_boost) {
        this.volume_boost = volume_boost;
    }

    public int getFadeOnChangeFFSpeed() {
        return this.fade_on_rate;
    }

    public void setFadeOnChangeFFSpeed(int fade_on_rate) {
        this.fade_on_rate = fade_on_rate;
    }

    public String getRecordPath() {
        return this.record_path;
    }

    public void setRecordPath(String s) {
        this.record_path = s;
    }

    public int getRecordFlags() {
        return this.record_flags;
    }

    public void setRecordFlags(int i) {
        this.record_flags = i;
    }

    public int getRecordSplitTime() {
        return this.record_split_time;
    }

    public void setRecordSplitTime(int i) {
        this.record_split_time = i;
    }

    public int getRecordSplitSize() {
        return this.record_split_size;
    }

    public void setRecordSplitSize(int i) {
        this.record_split_size = i;
    }

    public String getRecordPrefix() {
        return this.record_prefix;
    }

    public void setRecordPrefix(String s) {
        this.record_prefix = s;
    }

    public long getRecordTrimPosStart() {
        return this.record_trim_pos_start;
    }

    public void setRecordTrimPosStart(long i) {
        this.record_trim_pos_start = i;
    }

    public long getRecordTrimPosEnd() {
        return this.record_trim_pos_end;
    }

    public void setRecordTrimPosEnd(long i) {
        this.record_trim_pos_end = i;
    }

    public int getEnableABR() {
        return this.enableABR;
    }

    public void setEnableABR(int enableABR) {
        this.enableABR = enableABR;
    }

    public int getPlaybackSendPlayPauseToServer() {
        return this.playbackSendPlayPauseToServer;
    }

    public void setPlaybackSendPlayPauseToServer(int playbackSendPlayPauseToServer) {
        this.playbackSendPlayPauseToServer = playbackSendPlayPauseToServer;
    }

    public int getVsyncEnable() {
        return this.vsyncEnable;
    }

    public void setVsyncEnable(int vsyncEnable) {
        this.vsyncEnable = vsyncEnable;
    }

    public void resetToDefault() {
        this.connectionUrl = "";
        this.connectionNetworkProtocol = -1;
        this.connectionDetectionTime = 5000;
        this.connectionBufferingType = 0;
        this.connectionBufferingTime = 3000;
        this.connectionBufferingSize = 0;
        this.dataReceiveTimeout = 30000;
        this.ConnectionTimeout = 60000;
        this.EnableInterruptOnClose = 1;
        this.decodingType = 0;
        this.decoderLatency = 0;
        this.rendererType = 1;
        this.synchroEnable = 1;
        this.synchroNeedDropVideoFrames = 0;
        this.synchroNeedDropFramesOnFF = 1;
        this.enableColorVideo = 1;
        this.aspectRatioMode = 1;
        this.aspectRatioZoomModePercent = 100;
        this.aspectRatioMoveModeX = -1;
        this.aspectRatioMoveModeY = -1;
        this.enableAudio = 1;
        this.colorBackground = -16777216;
        this.numberOfCPUCores = 0;
        this.bogoMIPS = 0.0f;
        this.sslKey = "";
        this.ext_stream = 0;
        this.nm3u8_id = 0;
        this.startOffest = Long.MIN_VALUE;
        this.startPreroll = 0;
        this.startPath = "";
        this.startCookies = "";
        this.ff_rate = 0;
        this.volume_detect_max_samples = 0;
        this.volume_boost = 0;
        this.fade_on_start = 1;
        this.fade_on_seek = 1;
        this.fade_on_rate = 1;
        this.record_path = "";
        this.record_flags = 0;
        this.record_split_time = 0;
        this.record_split_size = 0;
        this.record_prefix = "";
        this.record_trim_pos_start = -1;
        this.record_trim_pos_end = -1;
        this.selectAudio = 0;
        this.selectSubtitle = -1;
        this.subtitlePaths.clear();
        this.playerMode = PlayerModes.PP_MODE_ALL.val();
        this.enableABR = 0;
        this.vsyncEnable = 0;
        this.extra_data_filter = 0;
    }

    public void print() {
    }
}
