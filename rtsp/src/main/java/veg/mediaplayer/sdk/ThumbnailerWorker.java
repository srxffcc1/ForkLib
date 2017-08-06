package veg.mediaplayer.sdk;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.Buffer;

import veg.mediaplayer.sdk.Thumbnailer.ThumbnailerState;

/* compiled from: Thumbnailer */
class ThumbnailerWorker implements Runnable {
    private String TAG = "Thread:";
    public volatile boolean finish = false;
    private Thumbnailer owner = null;
    public int thumbnailer_inst = 0;

    public ThumbnailerWorker(Thumbnailer owner, String TAG) {
        this.owner = owner;
        this.TAG += TAG;
    }

    public void run() {
        if (this.owner.state == ThumbnailerState.Closing) {
            this.finish = false;
            this.owner.state = ThumbnailerState.Closed;
            this.owner.waitOpen.notify("Open notify...");
            if (this.owner.outbuff != null) {
                this.owner.outbuff.clear();
                destroyBuffer(this.owner.outbuff);
                this.owner.outbuff = null;
                return;
            }
            return;
        }
        this.owner.state = ThumbnailerState.Opening;
        this.thumbnailer_inst = this.owner.nativeThumbnailerInit(this.owner);
        if (this.thumbnailer_inst == 0 || this.finish) {
            this.owner.nativeThumbnailerUninit(this.thumbnailer_inst);
            this.thumbnailer_inst = 0;
            this.finish = false;
            this.owner.state = ThumbnailerState.Closed;
            this.owner.waitOpen.notify("Open notify...");
            if (this.owner.outbuff != null) {
                this.owner.outbuff.clear();
                destroyBuffer(this.owner.outbuff);
                this.owner.outbuff = null;
            }
        } else if (this.owner.nativeThumbnailerOpen(this.thumbnailer_inst, this.owner.thumbnailerConfig.getConnectionUrl(), this.owner.thumbnailerConfig.getOutWidth(), this.owner.thumbnailerConfig.getOutHeight()) != 0 || this.finish) {
            this.owner.nativeThumbnailerClose(this.thumbnailer_inst);
            this.owner.nativeThumbnailerUninit(this.thumbnailer_inst);
            this.thumbnailer_inst = 0;
            this.finish = false;
            this.owner.state = ThumbnailerState.Closed;
            this.owner.waitOpen.notify("waitOpen notify...");
            if (this.owner.outbuff != null) {
                this.owner.outbuff.clear();
                destroyBuffer(this.owner.outbuff);
                this.owner.outbuff = null;
            }
        } else {
            this.owner.state = ThumbnailerState.Opened;
            this.owner.waitOpen.notify("waitOpen notify...");
            this.owner.waitStartClose.wait("waitStartClose wait.. ");
            this.owner.state = ThumbnailerState.Closing;
            this.owner.nativeThumbnailerClose(this.thumbnailer_inst);
            this.owner.nativeThumbnailerUninit(this.thumbnailer_inst);
            this.thumbnailer_inst = 0;
            this.finish = false;
            if (this.owner.outbuff != null) {
                synchronized (this.owner.outbuff) {
                    this.owner.outbuff.clear();
                    destroyBuffer(this.owner.outbuff);
                    this.owner.outbuff = null;
                }
            }
            this.owner.state = ThumbnailerState.Closed;
        }
    }

    public void destroyBuffer(Buffer buffer) {
        if (buffer.isDirect()) {
            try {
                if (!buffer.getClass().getName().equals("java.nio.DirectByteBuffer")) {
                    Field attField = buffer.getClass().getDeclaredField("att");
                    attField.setAccessible(true);
                    buffer = (Buffer) attField.get(buffer);
                }
                Method cleanerMethod = buffer.getClass().getMethod("cleaner", new Class[0]);
                cleanerMethod.setAccessible(true);
                Object cleaner = cleanerMethod.invoke(buffer, new Object[0]);
                Method cleanMethod = cleaner.getClass().getMethod("clean", new Class[0]);
                cleanMethod.setAccessible(true);
                cleanMethod.invoke(cleaner, new Object[0]);
            } catch (Exception e) {
            }
        }
    }
}
