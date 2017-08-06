package com.buglyhelp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by King6rf on 2017/3/22.
 */

public class CrashHelp {
    private static Context mcontext;
    private static Class<? extends Activity> mbugclass;
    private static final CrashHelp crashHelp = new CrashHelp();

    public static boolean isApkDebugable() {
        try {
            ApplicationInfo info = mcontext.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {

        }
        return false;
    }

    public static CrashHelp instance(Context context, Class<Activity> bugclass, final String buglyid) {
        mcontext = context;
//        if(bugclass==null){
//            mbugclass = BugActivity.class;
//        }else{
//            mbugclass = bugclass;
//        }

        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context.getApplicationContext());
        strategy.setCrashHandleCallback(new CrashReport.CrashHandleCallback() {
            public Map<String, String> onCrashHandleStart(int crashType, String errorType,
                                                          String errorMessage, String errorStack) {
                Log.v("SRX","crash1");
                LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                map.put("Key", "Value");
                if (isApkDebugable()) {
//                    Intent crasha = new Intent(mcontext, mbugclass).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    crasha.putExtra("crash", crashType + "\n" + errorType + "\n" + errorMessage + "\n" + errorStack);
//                    mcontext.startActivity(crasha);
                }


                return null;
            }

            @Override
            public byte[] onCrashHandleStart2GetExtraDatas(int crashType, String errorType,
                                                           String errorMessage, String errorStack) {
                Log.v("SRX","crash2");
                if (isApkDebugable() && crashType != 0) {
//                    Intent crasha = new Intent(mcontext, mbugclass).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    crasha.putExtra("crash", crashType + "\n" + errorType + "\n" + errorMessage + "\n" + errorStack);
//                    mcontext.startActivity(crasha);
                }
                try {
                    return "Extra data.".getBytes("UTF-8");
                } catch (Exception e) {
                    return null;
                }
            }

        });
        CrashReport.initCrashReport(mcontext.getApplicationContext(), buglyid, isApkDebugable(), strategy);
        return crashHelp;
    }

}
