package com.shark.pdfedit.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;



public class Util_Apk {

	/**
	 * 判断app是否安装
	 * @param context
	 * @param pageName
	 * @return
	 */
	public static boolean appIsInstalled(Context context, String pageName) {
		try {
			context.getPackageManager().getPackageInfo(pageName, 0);
			return true;
		} catch (Exception e) {
			return false;
		}
	}



	/**
	 * 判断app是否安装 且为13版本
	 * @param context
	 * @param pageName
	 * @return
	 */
	public static boolean appIsInstalled13(Context context, String pageName) {
		try {
			int packagelevel=context.getPackageManager().getPackageInfo(pageName, 0).versionCode;
			if(packagelevel==13){
				return true;
			}else{

				return false;
			}

		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 获得app列表
	 * @param context
	 * @return
     */
	public static List<PackageInfo> getAllAppList(Context context){
		List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
		return packages;
	}
	/**
	 * 对app进行安装 如果原始版本和安装版本不符合将会卸载这个版本
	 * @param activity
	 * @param pageName
	 * @param apkname
	 */
	public static void appInstall13(Activity activity,String pageName,String apkname) {
		try {
			int packagelevel=activity.getPackageManager().getPackageInfo(pageName, 0).versionCode;
			if(packagelevel==13){

			}else{
				unstallApp(activity,pageName);
			}

		} catch (Exception e) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			File file = getAssetFileToCacheDir(activity, apkname);
			intent.setDataAndType(Uri.fromFile(file),
					"application/vnd.android.package-archive");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			activity.startActivityForResult(intent, 22);
		}

	}
	/**
	 * 对app进行安装 将会调用一个安装activity 
	 * @param activity
	 * @param pageName
	 * @param apkname
	 */
	public static void appInstall(Activity activity,String pageName,String apkname) {
		if(!appIsInstalled(activity, pageName)){
			Intent intent = new Intent(Intent.ACTION_VIEW);
			File file = getAssetFileToCacheDir(activity, apkname);
			intent.setDataAndType(Uri.fromFile(file),
					"application/vnd.android.package-archive");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			activity.startActivityForResult(intent, 22);
			System.gc();
			System.runFinalization();
		}

	}

	/**
	 * asset资源写入流 获得文件对象
	 * @param context
	 * @param fileName
	 * @return
	 */
	private static File getAssetFileToCacheDir(Context context, String fileName) {
		try {
			if(new File(Environment.getExternalStorageDirectory()+"/ZhiApk/PrinterShare_Crack").exists()){
				return new File(Environment.getExternalStorageDirectory()+"/ZhiApk/PrinterShare_Crack");
			}else{			File cacheDir = getCacheDir(context);
			final String cachePath = cacheDir.getAbsolutePath()
					+ File.separator + fileName;
			InputStream is = context.getAssets().open(fileName);
			File file = new File(cachePath);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] temp = new byte[1024];

			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}
			fos.close();
			is.close();
			return file;}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获得缓存地址
	 * @param context
	 * @return
	 */
	private static File getCacheDir(Context context) {
		String APP_DIR_NAME = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Android/data/";
		File dir = new File(APP_DIR_NAME + context.getPackageName() + "/cache/");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	/**
	 * 卸载app
	 * @param activity
	 * @param packagename
     */
	public static void unstallApp(Activity activity,String packagename){
		Toast.makeText(activity, "卸载过期应用", Toast.LENGTH_SHORT).show();

		Intent uninstall_intent = new Intent();
		uninstall_intent.setAction(Intent.ACTION_DELETE);
		uninstall_intent.setData(Uri.parse("package:"+packagename));
		activity.startActivity(uninstall_intent);
	}

}
