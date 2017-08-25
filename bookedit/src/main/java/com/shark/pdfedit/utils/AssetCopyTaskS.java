package com.shark.pdfedit.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Message;
import android.util.Log;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/7/9.
 */

public class AssetCopyTaskS extends AsyncTask<String, Long, Long> {
    Context context;

    public AssetCopyTaskS(Context context) {
        this.context = context;
    }

    @Override
    protected Long doInBackground(String... params) {
        String startwith = params[0];
        String outstringparent = params[1];
        InputStream is = null;
        FileOutputStream fos = null;
        if (!new File(outstringparent).exists()) {
            new File(outstringparent).mkdirs();
        }
        try {
            String[] alllist = context.getAssets().list("");
            for (int i = 0; i < alllist.length; i++) {
                System.out.println("分配");
                System.out.println(alllist[i]+":"+startwith);
                if (alllist[i].startsWith(startwith)) {
                    copyAssetToPath(alllist[i],outstringparent);
                    System.gc();// gc
                    System.runFinalization();// gc
                }
            }
            return 100l;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 100l;
        } finally {

        }
    }

    private void copyAssetToPath(String instring, String outstring) throws IOException {
        InputStream is;
        FileOutputStream fos;
        is = context.getAssets().open(
                instring);
//            is=new FileInputStream(startwith);
        fos = new FileOutputStream(outstring+"/"+instring);
        long total = is.available();
        int copyedSize = 0;
//            int needsize=total>512*1024*1024?4096:1024;
        byte[] buffer = new byte[1024];
        long byteCount = 0;
        int len = 0;
        long sum = 0;
        while (sum < total) {//这里是下载文件是否能全部下载完的关键！
//                Log.v("FileCopyTask","进入");
            len = is.read(buffer);

//                Log.v("SRX","每次读取:"+len);
            fos.write(buffer, 0, len);
            sum += len;
//            Log.v("SRX","进度"+sum+",全部:"+total);
            publishProgress(sum,total);

//                Log.v("SRX","剩余:"+(total-sum));
        }
        is.close();
        fos.flush();// 刷新缓冲区
        fos.close();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.v("AssetCopyTask","任务开始");

    }

    @Override
    protected void onPostExecute(Long integer) {
        super.onPostExecute(integer);
        try {
            Log.v("AssetCopyTask","任务完成");
            ZipFile zipFile=new ZipFile(Environment.getExternalStorageDirectory()+"/TTFS"+"/Font.zip");
            int zipnumber=zipFile.getFileHeaders().size();
            new UnZipTask(context, Environment.getExternalStorageDirectory()+"/TTFS/", zipnumber).execute(new ZipFile[]{zipFile});
            HandlerHelp.instance().sendEmptyMessage(12359);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }
    long old=0;
    long now=0;
    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
//        Log.v("AssetCopyTask","进度"+values[0]+",全部:"+values[1]);
        now= System.currentTimeMillis();
        if(old==0||now-old>177){
            long jindu = values[0];
            long tot = values[1];
            int getjindu = (int) (jindu * 1000 / tot);
            Log.v("SRX","阐出进度"+getjindu);
            Message message = Message.obtain();
            message.what = 12358;
            message.obj = getjindu;
            HandlerHelp.instance().sendMessage(message);
            Message message2= Message.obtain();
            message2.what=12360;
            message2.obj=getjindu;
            HandlerHelp.instance().sendMessage(message2);
            old=now;

        }


    }
}
