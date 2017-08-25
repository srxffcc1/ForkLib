package com.shark.pdfedit.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.widget.Toast;

import java.io.File;

public class PdfPrintHelp {
    /**
     * @param pdfpath   pdf路劲
     * @param activity
     * @param result startactivity的resquestcode
     */
    private static  final boolean isplugin = false;//插件不稳定

    public static void print(final String pdfpath, final Activity activity, int resquestcode) {
        if (isplugin) {
        //不推荐那种形式了
        } else {
            Uri fileuri = Uri.fromFile(new File(pdfpath));
            if (Util_Apk.appIsInstalled13(activity, "com.dynamixsoftware.printershare")) {
                File file = new File(fileuri.toString());
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                i.setPackage("com.dynamixsoftware.printershare");
                i.setDataAndType(fileuri, "application/pdf");
                activity.startActivity(i);
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Toast.makeText(activity, "安装打印插件", Toast.LENGTH_SHORT).show();
                        Util_Apk.appInstall13(activity, "com.dynamixsoftware.printershare","PrinterShare_Crack.app");
                        Looper.loop();
                    }
                }).start();

            }
        }

    }
}
