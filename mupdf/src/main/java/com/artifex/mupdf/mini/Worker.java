package com.artifex.mupdf.mini;

import android.app.Activity;
import android.util.Log;
import java.util.concurrent.LinkedBlockingQueue;

public class Worker implements Runnable {
    protected Activity activity;
    protected boolean alive;
    protected LinkedBlockingQueue<Task> queue = new LinkedBlockingQueue();

    public static class Task implements Runnable {
        public void work() {
        }

        public void run() {
        }
    }

    public Worker(Activity act) {
        this.activity = act;
    }

    public void start() {
        this.alive = true;
        new Thread(this).start();
    }

    public void stop() {
        this.alive = false;
    }

    public void add(Task task) {
        try {
            this.queue.put(task);
        } catch (InterruptedException x) {
            Log.e("MuPDF Worker", x.getMessage());
        }
    }

    public void run() {
        while (this.alive) {
            try {
                Task task = (Task) this.queue.take();
                task.work();
                this.activity.runOnUiThread(task);
            } catch (Throwable x) {
                Log.e("MuPDF Worker", x.getMessage());
            }
        }
    }
}
