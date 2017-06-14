package com.artifex.mupdf.mini;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.widget.Scroller;
import com.artifex.mupdf.fitz.Link;
import com.artifex.mupdf.fitz.Rect;

public class PageView extends View implements OnGestureListener, OnScaleGestureListener {
    protected DocumentActivity actionListener;
    protected Bitmap bitmap;
    protected int bitmapH;
    protected int bitmapW;
    protected int canvasH;
    protected int canvasW;
    protected GestureDetector detector;
    protected boolean error;
    protected Paint errorPaint;
    protected Path errorPath;
    protected Paint linkPaint = new Paint();
    protected Link[] links;
    protected float maxScale = 2.0f;
    protected float minScale = 1.0f;
    protected ScaleGestureDetector scaleDetector;
    protected int scrollX;
    protected int scrollY;
    protected Scroller scroller;
    protected boolean showLinks;
    protected float viewScale = 1.0f;

    public PageView(Context ctx, AttributeSet atts) {
        super(ctx, atts);
        this.scroller = new Scroller(ctx);
        this.detector = new GestureDetector(ctx, this);
        this.scaleDetector = new ScaleGestureDetector(ctx, this);
        this.linkPaint.setARGB(32, 0, 0, 255);
        this.errorPaint = new Paint();
        this.errorPaint.setARGB(255, 255, 80, 80);
        this.errorPaint.setStrokeWidth(5.0f);
        this.errorPaint.setStyle(Style.STROKE);
        this.errorPath = new Path();
        this.errorPath.moveTo(-100.0f, -100.0f);
        this.errorPath.lineTo(100.0f, 100.0f);
        this.errorPath.moveTo(100.0f, -100.0f);
        this.errorPath.lineTo(-100.0f, 100.0f);
    }

    public void setActionListener(DocumentActivity l) {
        this.actionListener = l;
    }

    public void setError() {
        if (this.bitmap != null) {
            this.bitmap.recycle();
        }
        this.error = true;
        this.links = null;
        this.bitmap = null;
        invalidate();
    }

    public void setBitmap(Bitmap b, boolean wentBack, Link[] ls) {
        int i;
        int i2 = 0;
        if (this.bitmap != null) {
            this.bitmap.recycle();
        }
        this.error = false;
        this.links = ls;
        this.bitmap = b;
        this.bitmapW = (int) (((float) this.bitmap.getWidth()) * this.viewScale);
        this.bitmapH = (int) (((float) this.bitmap.getHeight()) * this.viewScale);
        this.scroller.forceFinished(true);
        if (wentBack) {
            i = this.bitmapW - this.canvasW;
        } else {
            i = 0;
        }
        this.scrollX = i;
        if (wentBack) {
            i2 = this.bitmapH - this.canvasH;
        }
        this.scrollY = i2;
        invalidate();
    }

    public void onSizeChanged(int w, int h, int ow, int oh) {
        this.canvasW = w;
        this.canvasH = h;
        this.actionListener.onPageViewSizeChanged(w, h);
    }

    public boolean onTouchEvent(MotionEvent event) {
        this.detector.onTouchEvent(event);
        this.scaleDetector.onTouchEvent(event);
        return true;
    }

    public boolean onDown(MotionEvent e) {
        this.scroller.forceFinished(true);
        return true;
    }

    public void onShowPress(MotionEvent e) {
    }

    public void onLongPress(MotionEvent e) {
        this.showLinks = !this.showLinks;
        invalidate();
    }

    public boolean onSingleTapUp(MotionEvent e) {
        Rect b;
        boolean foundLink = false;
        float x = e.getX();
        float y = e.getY();
        if (this.showLinks && this.links != null) {
            float mx = (x + (this.bitmapW <= this.canvasW ? (float) ((this.bitmapW - this.canvasW) / 2) : (float) this.scrollX)) / this.viewScale;
            float my = (y + (this.bitmapH <= this.canvasH ? (float) ((this.bitmapH - this.canvasH) / 2) : (float) this.scrollY)) / this.viewScale;
            Link[] linkArr = this.links;
            int length = linkArr.length;
            int i = 0;
            while (i < length) {
                Link link = linkArr[i];
                b = link.bounds;
                if (mx < b.x0 || mx > b.x1 || my < b.y0 || my > b.y1) {
                    i++;
                } else {
                    if (link.uri != null) {
                        this.actionListener.gotoURI(link.uri);
                    } else if (link.page >= 0) {
                        this.actionListener.gotoPage(link.page);
                    }
                    foundLink = true;
                }
            }
        }
        if (!foundLink) {
            float a = (float) (this.canvasW / 3);
            float bb = a * 2.0f;
            if (x <= a) {
                goBackward();
            }
            if (x >= bb) {
                goForward();
            }
            if (x > a && x < bb) {
                this.actionListener.toggleUI();
            }
        }
        invalidate();
        return true;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy) {
        if (this.bitmap != null) {
            this.scrollX += (int) dx;
            this.scrollY += (int) dy;
            this.scroller.forceFinished(true);
            invalidate();
        }
        return true;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float dx, float dy) {
        if (this.bitmap != null) {
            int maxY;
            int maxX = this.bitmapW > this.canvasW ? this.bitmapW - this.canvasW : 0;
            if (this.bitmapH > this.canvasH) {
                maxY = this.bitmapH - this.canvasH;
            } else {
                maxY = 0;
            }
            this.scroller.forceFinished(true);
            this.scroller.fling(this.scrollX, this.scrollY, (int) (-dx), (int) (-dy), 0, maxX, 0, maxY);
            invalidate();
        }
        return true;
    }

    public boolean onScaleBegin(ScaleGestureDetector det) {
        return true;
    }

    public boolean onScale(ScaleGestureDetector det) {
        if (this.bitmap != null) {
            float focusX = det.getFocusX();
            float focusY = det.getFocusY();
            float pageFocusX = (((float) this.scrollX) + focusX) / this.viewScale;
            float pageFocusY = (((float) this.scrollY) + focusY) / this.viewScale;
            this.viewScale *= det.getScaleFactor();
            if (this.viewScale < this.minScale) {
                this.viewScale = this.minScale;
            }
            if (this.viewScale > this.maxScale) {
                this.viewScale = this.maxScale;
            }
            this.bitmapW = (int) (((float) this.bitmap.getWidth()) * this.viewScale);
            this.bitmapH = (int) (((float) this.bitmap.getHeight()) * this.viewScale);
            this.scrollX = (int) ((this.viewScale * pageFocusX) - focusX);
            this.scrollY = (int) ((this.viewScale * pageFocusY) - focusY);
            this.scroller.forceFinished(true);
            invalidate();
        }
        return true;
    }

    public void onScaleEnd(ScaleGestureDetector det) {
    }

    public void goBackward() {
        this.scroller.forceFinished(true);
        if (this.scrollY > 0) {
            this.scroller.startScroll(this.scrollX, this.scrollY, 0, ((-this.canvasH) * 9) / 10, 250);
        } else if (this.scrollX <= 0) {
            this.actionListener.goBackward();
            return;
        } else {
            this.scroller.startScroll(this.scrollX, this.scrollY, ((-this.canvasW) * 9) / 10, (this.bitmapH - this.canvasH) - this.scrollY, 500);
        }
        invalidate();
    }

    public void goForward() {
        this.scroller.forceFinished(true);
        if (this.scrollY + this.canvasH < this.bitmapH) {
            this.scroller.startScroll(this.scrollX, this.scrollY, 0, (this.canvasH * 9) / 10, 250);
        } else if (this.scrollX + this.canvasW >= this.bitmapW) {
            this.actionListener.goForward();
            return;
        } else {
            this.scroller.startScroll(this.scrollX, this.scrollY, (this.canvasW * 9) / 10, -this.scrollY, 500);
        }
        invalidate();
    }

    public void onDraw(Canvas canvas) {
        if (this.bitmap != null) {
            int x;
            int y;
            if (this.scroller.computeScrollOffset()) {
                this.scrollX = this.scroller.getCurrX();
                this.scrollY = this.scroller.getCurrY();
                invalidate();
            }
            if (this.bitmapW <= this.canvasW) {
                this.scrollX = 0;
                x = (this.canvasW - this.bitmapW) / 2;
            } else {
                if (this.scrollX < 0) {
                    this.scrollX = 0;
                }
                if (this.scrollX > this.bitmapW - this.canvasW) {
                    this.scrollX = this.bitmapW - this.canvasW;
                }
                x = -this.scrollX;
            }
            if (this.bitmapH <= this.canvasH) {
                this.scrollY = 0;
                y = (this.canvasH - this.bitmapH) / 2;
            } else {
                if (this.scrollY < 0) {
                    this.scrollY = 0;
                }
                if (this.scrollY > this.bitmapH - this.canvasH) {
                    this.scrollY = this.bitmapH - this.canvasH;
                }
                y = -this.scrollY;
            }
            canvas.translate((float) x, (float) y);
            canvas.scale(this.viewScale, this.viewScale);
            canvas.drawBitmap(this.bitmap, 0.0f, 0.0f, null);
            if (this.showLinks && this.links != null) {
                for (Link link : this.links) {
                    Rect b = link.bounds;
                    canvas.drawRect(b.x0, b.y0, b.x1, b.y1, this.linkPaint);
                }
            }
        } else if (this.error) {
            canvas.translate((float) (this.canvasW / 2), (float) (this.canvasH / 2));
            canvas.drawPath(this.errorPath, this.errorPaint);
        }
    }
}
