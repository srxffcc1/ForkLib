/*
 * Copyright (c) 2015,KJFrameForAndroid Open Source Project,张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kymjs.common;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * 图片工具类
 * Created by kymjs on 15/9/8.
 */
public class ImageUtils {



    /**
     * 获得规定尺寸的图片
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap resoucreId2Bitmap(Resources res, int resId,
                                           int reqWidth, int reqHeight) {
        // BitmapFactory.Options options = new BitmapFactory.Options();
        // options.inJustDecodeBounds = true;
        // BitmapFactory.decodeResource(res, resId, options);
        // options = BitmapHelper.getCalculateOptionInSize(options, reqWidth,
        // reqHeight);
        // return BitmapFactory.decodeResource(res, resId, options);

        // 通过JNI的形式读取本地图片达到节省内存的目的
        InputStream is = res.openRawResource(resId);
        return inputStream2Bitmap(is, null, reqWidth, reqHeight);
    }

    /**
     * 获取一个指定大小的bitmap
     *
     * @param reqWidth
     *            目标宽度
     * @param reqHeight
     *            目标高度
     */
    public static Bitmap path2Bitmap(String pathName, int reqWidth,
                                     int reqHeight) {
        if (reqHeight == 0 || reqWidth == 0) {
            try {
                return BitmapFactory.decodeFile(pathName);
            } catch (OutOfMemoryError e) {
            }
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options = calculateInSampleSize2(options, reqWidth, reqHeight);
        return BitmapFactory.decodeFile(pathName, options);
    }

    /**
     * 获取一个指定大小的bitmap
     *
     * @param data
     *            Bitmap的byte数组
     * @param offset
     *            image从byte数组创建的起始位置
     * @param length
     *            the number of bytes, 从offset处开始的长度
     * @param reqWidth
     *            目标宽度
     * @param reqHeight
     *            目标高度
     */
    public static Bitmap bytes2Bitmap(byte[] data, int offset,
                                      int length, int reqWidth, int reqHeight) {
        if (reqHeight == 0 || reqWidth == 0) {
            try {
                return BitmapFactory.decodeByteArray(data, offset, length);
            } catch (OutOfMemoryError e) {
            }
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        BitmapFactory.decodeByteArray(data, offset, length, options);
        options = getCalculateOptionInSize(options, reqWidth, reqHeight);
        return BitmapFactory.decodeByteArray(data, offset, length, options);
    }

    /**
     * 获取一个指定大小的bitmap
     *
     * @param is
     *            从输入流中读取Bitmap
     * @param outPadding
     *            If not null, return the padding rect for the bitmap if it
     *            exists, otherwise set padding to [-1,-1,-1,-1]. If no bitmap
     *            is returned (null) then padding is unchanged.
     * @param reqWidth
     *            目标宽度
     * @param reqHeight
     *            目标高度
     */
    public static Bitmap inputStream2Bitmap(InputStream is, Rect outPadding,
                                            int reqWidth, int reqHeight) {
        Bitmap bmp = null;
        if (reqHeight == 0 || reqWidth == 0) {
            try {
                return BitmapFactory.decodeStream(is);
            } catch (OutOfMemoryError e) {
            }
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        BitmapFactory.decodeStream(is, outPadding, options);
        options = getCalculateOptionInSize(options, reqWidth, reqHeight);
        bmp = BitmapFactory.decodeStream(is, outPadding, options);
        return bmp;
    }


    /**
     * 获取一个指定大小的bitmap<br>
     * 实际调用的方法是bitmapFromByteArray(data, 0, data.length, w, h);
     *
     * @param is
     *            从输入流中读取Bitmap
     * @param reqWidth
     *            目标宽度
     * @param reqHeight
     *            目标高度
     */
    public static Bitmap inputStream2Bitmap(InputStream is, int reqWidth,
                                            int reqHeight) {
        if (reqHeight == 0 || reqWidth == 0) {
            try {
                return BitmapFactory.decodeStream(is);
            } catch (OutOfMemoryError e) {
            }
        }
        byte[] data = FileUtils.inStream2Byte(is);
        return bytes2Bitmap(data, 0, data.length, reqWidth, reqHeight);
    }

// public static Bitmap inputStream2Bitmap(File file, int reqWidth, int
// reqHeight) {
// try {
// return inputStream2Bitmap(new FileInputStream(file), reqWidth, reqHeight);
// } catch (FileNotFoundException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
// return null;
// }
    /**
     * 获得缩略图
     * @param context
     * @param path
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getThumbnailAll(Context context, String path, int width,
                                         int height, int voiceRid){
        int sacle=6;
        width=width/sacle;
        height=height/sacle;
        int result=0;
        String fileend="";
        File file=new File(path);
        if(file.isFile()){
            fileend=file.getName().split("\\.")[1].trim();
        }
        if (fileend.equalsIgnoreCase("mp3")||fileend.equalsIgnoreCase("wav")||fileend.equalsIgnoreCase("wma")||fileend.equalsIgnoreCase("ogg")) {
            result=1;
        }else if (fileend.equalsIgnoreCase("mp4")||fileend.equalsIgnoreCase("rmvb")||fileend.equalsIgnoreCase("mkv")||fileend.equalsIgnoreCase("flv")) {
            result=2;
        }else if (fileend.equalsIgnoreCase("jpg")||fileend.equalsIgnoreCase("bmp")||fileend.equalsIgnoreCase("jpeg")||fileend.equalsIgnoreCase("png")) {
            result=3;
        }

        int filetype=result;
        switch (filetype) {
            case 1:
                return decodeBitmapFromResource(context.getResources(), voiceRid, width, height);
            case 2:
                return getVideoThumbnail(path,width,height, MediaStore.Images.Thumbnails.MICRO_KIND);
            case 3:

                return getImageThumbnail(path,width,height);
            default:
                break;
        }
        return null;
    }
    /**
     * 获得图片缩略图
     * @param imagePath
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getImageThumbnail(String imagePath, int width,
                                           int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 获取视频的缩略图 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     *
     * @param videoPath
     *            视频的路径
     * @param width
     *            指定输出视频缩略图的宽度
     * @param height
     *            指定输出视频缩略图的高度度
     * @param kind
     *            参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width,
                                           int height, int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);

        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }



    /**
     * 图片压缩处理（使用Options的方法） <br>
     * <b>说明</b> 使用方法：
     * 首先你要将Options的inJustDecodeBounds属性设置为true，BitmapFactory.decode一次图片 。
     * 然后将Options连同期望的宽度和高度一起传递到到本方法中。
     * 之后再使用本方法的返回值做参数调用BitmapFactory.decode创建图片。 <br>
     * <b>说明</b> BitmapFactory创建bitmap会尝试为已经构建的bitmap分配内存
     * ，这时就会很容易导致OOM出现。为此每一种创建方法都提供了一个可选的Options参数
     * ，将这个参数的inJustDecodeBounds属性设置为true就可以让解析方法禁止为bitmap分配内存
     * ，返回值也不再是一个Bitmap对象， 而是null。虽然Bitmap是null了，但是Options的outWidth、
     * outHeight和outMimeType属性都会被赋值。
     *
     * @param reqWidth
     *            目标宽度,这里的宽高只是阀值，实际显示的图片将小于等于这个值
     * @param reqHeight
     *            目标高度,这里的宽高只是阀值，实际显示的图片将小于等于这个值
     */
    public static BitmapFactory.Options getCalculateOptionInSize(
            final BitmapFactory.Options options, final int reqWidth,
            final int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        // 设置压缩比例
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        return options;
    }

    public static Bitmap decodeBitmapFromResource(Resources res,
                                                  int resId, int imageViewWidth, int imageViewHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options = calculateInSampleSize2(options, imageViewWidth,
                imageViewHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static BitmapFactory.Options calculateInSampleSize2(
            BitmapFactory.Options options, int imageViewWidth,
            int imageViewHeight) {

        // Raw height and width of image
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        // double imageScaleSize = maxMemory/imageMemory;
        // double imageScaleSize = 0.15;
        // final int reqWidth = (int)(imageViewWidth * imageScaleSize);
        // final int reqHeight = (int)(imageViewHeight * imageScaleSize);

        int reqWidth = (int) (imageViewWidth);
        int reqHeight = (int) (imageViewHeight);
        if(imageWidth>imageHeight){
            imageWidth=(imageHeight/reqHeight)*reqWidth;
        }else{
            imageHeight=(imageWidth/reqWidth)*reqHeight;
        }
        int inSampleSize = 1;

        if (imageHeight > reqHeight || imageWidth > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) imageHeight
                    / (float) imageViewHeight);
            final int widthRatio = Math.round((float) imageWidth
                    / (float) imageViewWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return options;
    }

//public static double getMemoryScaleSize(ImageView imageview, int memoryScale) {
//	double imageMemory = (imageview.getWidth() * imageview.getHeight()) * 32 / 1024;
//	double maxMemory = (Runtime.getRuntime().maxMemory() / 1024 / memoryScale);
//	double imageScaleSize = maxMemory / imageMemory;
//	return imageScaleSize;
//}
    /**
     * 压缩图片
     *
     * @param filePath 源图片地址
     * @param width    想要的宽度
     * @param height   想要的高度
     * @param isAdjust 是否自动调整尺寸, true图片就不会拉伸，false严格按照你的尺寸压缩
     * @return Bitmap
     */
    public static File scaleBitmapFile(Context cxt, String filePath, int width, int height,
                                       boolean isAdjust) {

        Bitmap bitmap = scaleBitmap(BitmapFactory.decodeFile(filePath), width, height, isAdjust);

        File file = new File(getRandomFileName(cxt.getCacheDir().getPath()));

        BufferedOutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /***
     * 获取一个随机图片文件名，含路径
     *
     * @param filePath
     * @return
     */
    public static String getRandomFileName(String filePath) {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
                Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return filePath + "/" + key + ".jpeg";
    }

    /**
     * 压缩图片
     *
     * @param bitmap   源图片
     * @param width    想要的宽度
     * @param height   想要的高度
     * @param isAdjust 是否自动调整尺寸, true图片就不会拉伸，false严格按照你的尺寸压缩
     * @return Bitmap
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, int width, int height, boolean isAdjust) {
        // 如果想要的宽度和高度都比源图片小，就不压缩了，直接返回原图
        if (bitmap.getWidth() < width && bitmap.getHeight() < height) {
            return bitmap;
        }
        if (width == 0 && height == 0) {
            width = bitmap.getWidth();
            height = bitmap.getHeight();
        }

        // 根据想要的尺寸精确计算压缩比例, 方法详解：public BigDecimal divide(BigDecimal divisor, int scale, int 
        // roundingMode);
        // scale表示要保留的小数位, roundingMode表示如何处理多余的小数位，BigDecimal.ROUND_DOWN表示自动舍弃
        float sx = new BigDecimal(width).divide(new BigDecimal(bitmap.getWidth()), 4, BigDecimal
                .ROUND_DOWN).floatValue();
        float sy = new BigDecimal(height).divide(new BigDecimal(bitmap.getHeight()), 4,
                BigDecimal.ROUND_DOWN).floatValue();
        if (isAdjust) {// 如果想自动调整比例，不至于图片会拉伸
            sx = (sx < sy ? sx : sy);
            sy = sx;// 哪个比例小一点，就用哪个比例
        }
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy);// 调用api中的方法进行压缩，就大功告成了
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,
                true);
    }
}
