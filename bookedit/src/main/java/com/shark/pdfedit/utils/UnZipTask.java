package com.shark.pdfedit.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.util.List;

/**
 * Created by King6rf on 2017/7/11.
 */

public  class UnZipTask extends AsyncTask<ZipFile, Long, Long> {
    private String dir = "";
    private boolean isSucceeded;
    private Context mainActivity;

    public UnZipTask(Context context, String dir, int numberFiles) {
        this.dir = dir;
        this.mainActivity = context;
    }

    protected Long doInBackground(ZipFile... zipFiles) {
        try {
            for (ZipFile zipFile : zipFiles) {
                List fileHeaderList = zipFile.getFileHeaders();
                int number = fileHeaderList.size();
                for (int i = 0; i < number; i++) {
                    zipFile.extractFile((net.lingala.zip4j.model.FileHeader) fileHeaderList.get(i), this.dir);
                    long tmpi=i;
                    publishProgress(tmpi);
                    if (isCancelled()) {
                        break;
                    }
                }
            }
            this.isSucceeded = true;
        } catch (ZipException e) {
            e.printStackTrace();
            Log.e("unzip", e.toString());
            this.isSucceeded = false;
        }
        return 100l;
    }

    protected void onCancelled() {
        super.onCancelled();
    }

    protected void onPreExecute() {
        super.onPreExecute();

    }

    protected void onPostExecute(Long result) {
        super.onPostExecute(result);
        HandlerHelp.instance().sendEmptyMessage(12362);
    }

    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        long jindu=values[0];
        int getjindu= (int) jindu;
        Message message = Message.obtain();
        message.what=12361;
        message.obj=getjindu;
        HandlerHelp.instance().sendMessage(message);
    }
}
