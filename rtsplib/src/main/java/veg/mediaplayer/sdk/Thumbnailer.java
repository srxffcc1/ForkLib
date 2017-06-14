package veg.mediaplayer.sdk;

import android.content.Context;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import veg.mediaplayer.sdk.SystemUtils.WaitNotify;

public class Thumbnailer {
    private final String TAG = ("Thumbnailer(" + hashCode() + ")");
    protected transient Context context;
    private int heightLayout = 0;
    protected float mHeight;
    private int mVideoHeight = 0;
    private int mVideoWidth = 0;
    protected float mWidth;
    protected ByteBuffer outbuff = null;
    protected ThumbnailerState state = ThumbnailerState.Closed;
    private ThumbnailFrame thumbFrame = null;
    protected ThumbnailerConfig thumbnailerConfig = null;
    protected Thread thumbnailerThread = null;
    protected ThumbnailerWorker thumbnailerWorker = null;
    protected WaitNotify waitOpen = new WaitNotify();
    protected WaitNotify waitStartClose = new WaitNotify();
    private int widthLayout = 0;

    public class ThumbnailFrame {
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

    public enum ThumbnailerState {
        Opening(0),
        Opened(1),
        Closing(2),
        Closed(3);
        
        private static Map<Integer, ThumbnailerState> typesByValue = null;
        private final int value;

        static {
            typesByValue = new HashMap();
            ThumbnailerState[] values = values();
            int length = values.length;
            int i = 0;
            while (i < length) {
                ThumbnailerState type = values[i];
                typesByValue.put(Integer.valueOf(type.value), type);
                i++;
            }
        }

        private ThumbnailerState(int value) {
            this.value = value;
        }

        public static ThumbnailerState forValue(int value) {
            return (ThumbnailerState) typesByValue.get(Integer.valueOf(value));
        }

        public static int forType(ThumbnailerState type) {
            return type.value;
        }
    }

    public native int nativeThumbnailerClose(int i);

    public native int nativeThumbnailerGetFrame(int i, ByteBuffer byteBuffer, int[] iArr, int[] iArr2);

    public native String nativeThumbnailerGetInfo(int i);

    public native int nativeThumbnailerInit(Thumbnailer thumbnailer);

    public native int nativeThumbnailerInterrupt(int i);

    public native int nativeThumbnailerOpen(int i, String str, int i2, int i3);

    public native int nativeThumbnailerUninit(int i);

    static {
        SystemUtils.loadLibs();
    }

    public Thumbnailer(Context context) {
        this.context = context;
        this.thumbnailerConfig = new ThumbnailerConfig();
    }

    public Object Open(String ConnectionUrl) {
        if (this.thumbnailerThread != null) {
            Close();
        }
        if (this.thumbnailerThread == null) {
            this.thumbnailerWorker = new ThumbnailerWorker(this, this.TAG);
            this.thumbnailerWorker.finish = false;
            this.thumbnailerConfig.setConnectionUrl(ConnectionUrl);
            if (!(this.thumbnailerConfig.getConnectionUrl() == null || this.thumbnailerConfig.getConnectionUrl().isEmpty() || this.thumbnailerConfig.getConnectionUrl().indexOf("mms://") != 0)) {
                this.thumbnailerConfig.setConnectionUrl(this.thumbnailerConfig.getConnectionUrl().replace("mms://", "mmsh://"));
            }
            this.thumbnailerConfig.setNumberOfCPUCores(1);
            this.state = ThumbnailerState.Opening;
            this.thumbnailerThread = new Thread(this.thumbnailerWorker, "ThumbnailerThread");
            this.thumbnailerThread.start();
        }
        return this.waitOpen.getObject();
    }

    public Object Open(ThumbnailerConfig config) {
        if (this.thumbnailerThread != null) {
            Close();
        }
        if (this.thumbnailerThread == null) {
            if (config != null) {
                this.thumbnailerConfig = new ThumbnailerConfig(config);
            }
            this.thumbnailerWorker = new ThumbnailerWorker(this, this.TAG);
            this.thumbnailerWorker.finish = false;
            if (!(this.thumbnailerConfig.getConnectionUrl() == null || this.thumbnailerConfig.getConnectionUrl().isEmpty() || this.thumbnailerConfig.getConnectionUrl().indexOf("mms://") != 0)) {
                this.thumbnailerConfig.setConnectionUrl(this.thumbnailerConfig.getConnectionUrl().replace("mms://", "mmsh://"));
            }
            this.state = ThumbnailerState.Opening;
            this.thumbnailerThread = new Thread(this.thumbnailerWorker, "ThumbnailerThread");
            this.thumbnailerThread.start();
        }
        return this.waitOpen.getObject();
    }

    public String getInfo() {
        if (this.thumbnailerWorker == null || this.thumbnailerWorker.thumbnailer_inst == 0 || getState() != ThumbnailerState.Opened) {
            return "";
        }
        return nativeThumbnailerGetInfo(this.thumbnailerWorker.thumbnailer_inst);
    }

    public ThumbnailFrame getFrame() {
        if (this.thumbnailerWorker == null || this.thumbnailerWorker.thumbnailer_inst == 0 || getState() != ThumbnailerState.Opened) {
            return null;
        }
        int req_alloc_size = (this.thumbnailerConfig.getOutWidth() * this.thumbnailerConfig.getOutHeight()) * 4;
        if (req_alloc_size < 4) {
            return null;
        }
        if (this.outbuff == null) {
            this.outbuff = ByteBuffer.allocateDirect(req_alloc_size);
            this.outbuff.order(ByteOrder.nativeOrder());
        }
        int[] src_w = new int[]{0};
        int[] src_h = new int[]{0};
        int size = nativeThumbnailerGetFrame(this.thumbnailerWorker.thumbnailer_inst, this.outbuff, src_w, src_h);
        if (size > 0) {
            if (this.thumbFrame == null) {
                this.thumbFrame = new ThumbnailFrame();
            }
            this.thumbFrame.setWidth(src_w[0]);
            this.thumbFrame.setHeight(src_h[0]);
            if (this.outbuff != null) {
                synchronized (this.outbuff) {
                    if (this.outbuff != null) {
                        this.outbuff.limit(size);
                    }
                    if (this.outbuff != null) {
                        this.thumbFrame.setData(this.outbuff.slice());
                    }
                }
            }
        }
        if (size > 0) {
            return this.thumbFrame;
        }
        return null;
    }

    public ThumbnailerState getState() {
        return this.state;
    }

    public void Close() {
        if (this.state == ThumbnailerState.Closed) {
            this.thumbnailerThread = null;
            this.thumbnailerWorker = null;
            this.thumbFrame = null;
            return;
        }
        this.state = ThumbnailerState.Closing;
        if (this.thumbnailerThread == null || this.thumbnailerWorker == null || this.state == ThumbnailerState.Closed) {
            this.waitStartClose.notify("waitStartClose notify.. ");
            this.thumbnailerThread = null;
            this.thumbnailerWorker = null;
            this.thumbFrame = null;
            this.state = ThumbnailerState.Closed;
        } else if (this.thumbnailerThread.isAlive() || this.thumbnailerWorker == null || this.thumbnailerWorker.thumbnailer_inst != 0) {
            if (!(this.thumbnailerWorker == null || this.thumbnailerWorker.thumbnailer_inst == 0)) {
                nativeThumbnailerInterrupt(this.thumbnailerWorker.thumbnailer_inst);
            }
            this.waitStartClose.notify("waitStartClose notify.. ");
            this.thumbnailerWorker.finish = true;
            if (!(this.state == ThumbnailerState.Closed || this.state == ThumbnailerState.Closing)) {
                nativeThumbnailerInterrupt(this.thumbnailerWorker.thumbnailer_inst);
            }
            try {
                this.thumbnailerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.thumbnailerThread = null;
            this.thumbnailerWorker = null;
            this.thumbFrame = null;
            this.state = ThumbnailerState.Closed;
        } else {
            this.waitStartClose.notify("waitStartClose notify.. ");
            this.thumbnailerThread = null;
            this.thumbnailerWorker = null;
            this.thumbFrame = null;
            this.state = ThumbnailerState.Closed;
        }
    }
}
