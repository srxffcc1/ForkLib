package com.shark.pdfedit.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by King6rf on 2017/7/21.
 */

public class ZipTask {
    public static String ttfdir=Environment.getExternalStorageDirectory()+"/TTFS/";
    public static void checkTTF(Context context){
        if(new File(ttfdir+"/simfang.ttf").exists()&&
                new File(ttfdir+"/simfang2312.ttf").exists()&&
        new File(ttfdir+"/simhei.ttf").exists()&&
        new File(ttfdir+"/simkai2312.ttf").exists()&&
        new File(ttfdir+"/simzhongsong.ttf").exists()){
        }else{
            new AssetCopyTaskS(context).execute("Font",ttfdir);
        }
    }
}
