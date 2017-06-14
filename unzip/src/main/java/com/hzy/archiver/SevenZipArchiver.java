package com.hzy.archiver;

import com.hzy.lib7z.Un7Zip;

import java.io.File;

/**
 * Created by Administrator on 2016/8/26.
 */
public class SevenZipArchiver extends BaseArchiver {
    @Override
    public void doArchiver(File[] files, String destpath) {

    }

    @Override
    public void doUnArchiver(String srcfile, String unrarPath, String password, final IArchiverListener listener) {
        if (listener != null)
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onStartArchiver();
                }
            });

        Un7Zip.extract7z(srcfile,unrarPath);

        if (listener != null)
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onEndArchiver();
                }
            });


    }
}
