package veg.mediaplayer.sdk;

import android.graphics.Color;
import android.os.Build.VERSION;
import android.view.SurfaceView;

import java.util.Iterator;

import veg.mediaplayer.sdk.M3U8.HLSStream;
import veg.mediaplayer.sdk.MediaPlayer.PlayerModes;

/* compiled from: MediaPlayer */
class MediaPlayerWorker implements Runnable {
    private static final String TAG = "MediaPlayerWorker";
    public volatile boolean finish = false;
    public boolean isPreview = false;
    private MediaPlayer owner = null;
    public long[] player_inst = new long[1];

    public MediaPlayerWorker(MediaPlayer owner, boolean isPreview) {
        this.player_inst[0] = 0;
        this.isPreview = isPreview;
        this.owner = owner;
    }

    public void run() {
        this.owner.waitStartOpenThread.notify("Notify start open thread... ");
        if (this.owner.mIS_WINDOW && !this.owner.mUseExternalSurface) {
            this.owner.waitSurfaceCreated.wait("Wait surface created... ");
        }
        if (this.owner.playerConfig.getConnectionUrl().contains(".m3u8")) {
            this.owner.abrGetHLSStreams();
            int attempts = 20;
            while (!this.finish && this.owner.abrHLSStreams == null && attempts > 0) {
                try {
                    synchronized (this.owner.waitGetHLSStreams) {
                        this.owner.waitGetHLSStreams.wait(100);
                    }
                    attempts--;
                } catch (InterruptedException e) {
                }
            }
            if (this.owner.playerConfig.getM3U8Id() < 0) {
                this.owner.playerConfig.setM3U8Id(0);
            }
            this.owner.abrCurrentPlayedStreamId = this.owner.playerConfig.getM3U8Id();
            if (this.owner.abrHLSStreams == null || this.owner.abrHLSStreams.size() <= this.owner.abrCurrentPlayedStreamId) {
                this.owner.playerConfig.setExtStream(0);
            } else {
                this.owner.playerConfig.setExtStream(((HLSStream) this.owner.abrHLSStreams.get(this.owner.abrCurrentPlayedStreamId)).ext_stream);
            }
        }
        long rc = this.owner.nativePlayerInit(this.player_inst, this.owner);
        if (this.player_inst[0] == 0 || this.finish) {
            this.owner.waitOpenSource.notify("Open source notify.. ");
            this.owner.waitOpenMediaCodec.notify("Open mediacodec notify.. ");
            this.owner.nativePlayerUninit(this.player_inst);
            this.player_inst[0] = 0;
            this.finish = false;
            this.owner.closeMediaCodec();
            this.owner.queueSurfaceCreate.clear();
            this.owner.mPlayerThread = null;
            this.owner.abrClose();
            return;
        }
        if (this.owner.Callback != null) {
            this.owner.nativePlayerSetCallback(this.player_inst, this.owner.Callback);
        }
        this.owner.nativePlayerSetOptions(this.player_inst, this.owner.playerConfig.getConnectionNetworkProtocol(), this.owner.playerConfig.getConnectionDetectionTime(), this.owner.playerConfig.getConnectionBufferingType(), this.owner.playerConfig.getConnectionBufferingTime(), this.owner.playerConfig.getConnectionBufferingSize(), this.owner.playerConfig.getConnectionTimeout(), this.owner.playerConfig.getInterruptOnClose(), this.owner.playerConfig.getExtraDataFilter(), this.owner.playerConfig.getDecodingType(), this.owner.playerConfig.getDecoderLatency(), this.owner.playerConfig.getRendererType(), this.owner.playerConfig.getSynchroEnable(), this.owner.playerConfig.getSynchroNeedDropVideoFrames(), this.owner.playerConfig.getDropOnFastPlayback(), this.owner.playerConfig.getEnableColorVideo(), this.owner.playerConfig.getEnableAspectRatio(), this.owner.playerConfig.getAspectRatioZoomModePercent(), this.owner.playerConfig.getAspectRatioMoveModeX(), this.owner.playerConfig.getAspectRatioMoveModeY(), this.owner.playerConfig.getNumberOfCPUCores(), (int) this.owner.playerConfig.getBogoMIPS(), Color.red(this.owner.playerConfig.getColorBackground()), Color.green(this.owner.playerConfig.getColorBackground()), Color.blue(this.owner.playerConfig.getColorBackground()), Color.alpha(this.owner.playerConfig.getColorBackground()), this.owner.playerConfig.getSslKey(), this.owner.playerConfig.getStartOffest(), this.owner.playerConfig.getStartPreroll(), this.owner.playerConfig.getStartPath(), this.owner.playerConfig.getStartCookies(), this.owner.playerConfig.getExtStream(), this.owner.playerConfig.getPlaybackSendPlayPauseToServer());
        this.owner.nativePlayerRecordSetOptions(this.player_inst, this.owner.playerConfig.getRecordPath(), this.owner.playerConfig.getRecordFlags(), this.owner.playerConfig.getRecordSplitTime(), this.owner.playerConfig.getRecordSplitSize(), this.owner.playerConfig.getRecordPrefix());
        this.owner.nativePlayerAudioSelect(this.player_inst, this.owner.playerConfig.getSelectedAudio());
        if (this.owner.playerConfig.getFFRate() != 0) {
            this.owner.nativePlayerSetFFRate(this.player_inst, this.owner.playerConfig.getFFRate());
        }
        if (this.owner.playerConfig.getVolumeDetectMaxSamples() != 0) {
            this.owner.nativePlayerStartVolumeDetect(this.player_inst, this.owner.playerConfig.getVolumeDetectMaxSamples());
        }
        if (this.owner.playerConfig.getVolumeBoost() != 0) {
            this.owner.nativePlayerSetVolumeBoost(this.player_inst, this.owner.playerConfig.getVolumeBoost());
        }
        if (this.owner.playerConfig.getMode() == PlayerModes.PP_MODE_RECORD.val()) {
            this.owner.nativePlayerSetRecordOnly(this.player_inst, 1);
            this.owner.nativePlayerRecordSetTrimPositions(this.player_inst, this.owner.playerConfig.getRecordTrimPosStart(), this.owner.playerConfig.getRecordTrimPosEnd());
        } else {
            if ((this.owner.playerConfig.getMode() & PlayerModes.PP_MODE_RECORD.val()) == 0) {
                this.owner.nativePlayerSetRecordOnly(this.player_inst, 2);
            }
            if (!this.owner.mIS_WINDOW && !this.owner.mUseExternalSurface && this.owner.mSurface == null && this.owner.playerConfig.getMode() == PlayerModes.PP_MODE_AUDIO.val()) {
                this.owner.nativePlayerSetAudioOnly(this.player_inst, 1);
            }
        }
        if (this.owner.playerConfig.getFadeOnStart() == 1) {
            this.owner.m_fade_time = System.nanoTime() + 200000000;
        }
        Iterator it = this.owner.playerConfig.subtitlePaths.iterator();
        while (it.hasNext()) {
            this.owner.SubtitleSourceAdd((String) it.next());
        }
        this.owner.nativePlayerSubtitleSelect(this.player_inst, this.owner.playerConfig.getSelectedSubtitle());
        this.owner.nativePlayerVsyncEnable(this.player_inst, this.owner.playerConfig.getVsyncEnable());
        this.owner.previousVsyncEnabe = this.owner.playerConfig.getVsyncEnable();
        if (this.owner.previousVsyncEnabe != 0) {
            this.owner.startChoreographer();
        }
        if (this.isPreview) {
            rc = (long) this.owner.nativePlayerOpenAsPreview(this.player_inst, this.owner.playerConfig.getConnectionUrl(), 0, this.owner.playerConfig.getDataReceiveTimeout());
        } else {
            rc = (long) this.owner.nativePlayerOpen(this.player_inst, this.owner.playerConfig.getConnectionUrl(), 0, this.owner.playerConfig.getDataReceiveTimeout());
        }
        if (rc != 0 || this.finish) {
            this.owner.previousVsyncEnabe = 0;
            this.owner.waitOpenSource.notify("Open source notify.. ");
            this.owner.waitOpenMediaCodec.notify("Open mediacodec notify.. ");
            this.owner.nativePlayerClose(this.player_inst);
            this.owner.nativePlayerUninit(this.player_inst);
            this.player_inst[0] = 0;
            this.finish = false;
            this.owner.closeMediaCodec();
            this.owner.queueSurfaceCreate.clear();
            this.owner.mPlayerThread = null;
            this.owner.abrClose();
            return;
        }
        while (!this.finish) {
            try {
                Thread.sleep(200);
                if (this.owner.previousVsyncEnabe != this.owner.playerConfig.getVsyncEnable()) {
                    this.owner.nativePlayerVsyncEnable(this.player_inst, this.owner.playerConfig.getVsyncEnable());
                    if (this.owner.previousVsyncEnabe == 0) {
                        this.owner.startChoreographer();
                    }
                    this.owner.previousVsyncEnabe = this.owner.playerConfig.getVsyncEnable();
                }
                if (this.finish || this.owner.nativePlayerIsPlaying(this.player_inst) != 0) {
                    break;
                }
                if (this.owner.abrSetPlayedStreamId != this.owner.abrCurrentPlayedStreamId) {
                    this.owner.abrChangeStream(this.owner.abrSetPlayedStreamId);
                }
                if (this.owner.playerConfig.getEnableABR() == 1) {
                    this.owner.abrCheckBitrateAndChangeStream();
                }
            } catch (InterruptedException e2) {
                this.finish = true;
            }
        }
        SurfaceView view = this.owner.getSurfaceView();
        if (view != null) {
            view.post(new Runnable() {
                public void run() {
                    if (VERSION.SDK_INT >= 11) {
                        MediaPlayerWorker.this.owner.jb_setalpha(0.0f);
                    }
                }
            });
        }
        this.owner.previousVsyncEnabe = 0;
        this.owner.abrClose();
        this.owner.waitOpenSource.notify("Open source notify.. ");
        this.owner.waitOpenMediaCodec.notify("Open mediacodec notify.. ");
        this.owner.nativePlayerClose(this.player_inst);
        this.owner.nativePlayerUninit(this.player_inst);
        this.player_inst[0] = 0;
        this.finish = false;
        this.owner.closeMediaCodec();
        this.owner.queueSurfaceCreate.clear();
        this.owner.mPlayerThread = null;
    }
}
