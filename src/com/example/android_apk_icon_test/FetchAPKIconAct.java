package com.example.android_apk_icon_test;

import java.io.File;
import java.io.FileDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParsePosition;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

public class FetchAPKIconAct extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//String path=Environment.getExternalStorageDirectory().getPath();
		String path="/data/sdcard/";
		//showUninstallAPKIcon(path+"test2.apk");
		getUnistallApkIcon(this,path+ "test2.apk");
	}

	private void getUnistallApkIcon(Context context, String archiveFilePath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(archiveFilePath,
				PackageManager.GET_ACTIVITIES);
		if (info != null) {
			ApplicationInfo appInfo = info.applicationInfo;
			Drawable inco = pm.getApplicationIcon(appInfo);
			ImageView image = (ImageView) findViewById(R.id.apkIconByTradition);
			image.setVisibility(View.VISIBLE);
			image.setImageDrawable(inco);
		}

	}

	
	private void showUninstallAPKIcon(String apkPath) {
		String PATH_PackageParser = "android.content.pm.PackageParser";
		String PATH_AssetManager = "android.content.res.AssetManager";
		try {
			//apk包的文件路径
			//这是一个Package 解释器，是隐藏的
			//构造函数的参数只有一个，apk文件的路径
			//PackageParser packagerParser = new PackageParser(apkPath);
			Class pkgParserCls =Class.forName(PATH_PackageParser);
			Class[] typeArgs =new Class[1]; 
			typeArgs[0] =String.class;
			Constructor pkgParserCt =pkgParserCls.getConstructor(typeArgs);
			Object[] valueArgs =new Object[1];
			valueArgs[0] =apkPath; 
			Object pkgParser =pkgParserCt.newInstance(valueArgs);
			Log.d("ANDROID_LAB", "pkgParser:"+ pkgParser.toString());
			//这个是与显示相关的，里面涉及到一些像素显示等等，我们使用默认的情况
			DisplayMetrics metrics =	new DisplayMetrics();
			metrics.setToDefaults();
			//PackageParser.Package mPkgInfo = packageparser.parserPackage( new File(apkPath), apkPath,metrics,0);
			typeArgs =new Class[4];
			typeArgs[0]= File.class;
			typeArgs[1]= String.class;
			typeArgs[2]= DisplayMetrics.class;
			typeArgs[3]= Integer.TYPE;
			Method pkgParser_parsePackageMtd =pkgParserCls.getDeclaredMethod("parsePackage", typeArgs);
			valueArgs=new Object[4];
			valueArgs[0]=new File(apkPath);
			valueArgs[1]=apkPath;
			valueArgs[2]=metrics;
			valueArgs[3]=0;
			Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser, valueArgs);
			//应用程序信息包，这个公开的，不过有些函数，变量没有公开
			//ApplicationInfo info = mPkgInfo.applicationInfo;
			Field appInfoFld =pkgParserPkg.getClass().getDeclaredField("applicationInfo");
			ApplicationInfo info = (ApplicationInfo) appInfoFld.get(pkgParserPkg);
			//uid 输出为 “-1”，原因是未安装，系统未分配其Uid。
			Log.d("ANDROID_LAB", "pkg:"+info.packageName+"uid=" +info.uid);
			
			Class assetMagCls =Class.forName(PATH_AssetManager);
			Constructor assetMagCt = assetMagCls.getConstructor((Class[]) null);
			Object assetMag =assetMagCt.newInstance((Object[]) null);
			typeArgs=new Class[1];
			typeArgs[0]=String.class;
			Method assetMag_addAssetPathMtd =assetMagCls.getDeclaredMethod("addAssetPath", typeArgs);
			valueArgs = new Object[1];
			valueArgs[0] =apkPath;
			assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
			Resources res =getResources();
			typeArgs=new Class[3];
			typeArgs[0]=assetMag.getClass();
			typeArgs[1]=res.getDisplayMetrics().getClass();
			typeArgs[2]=res.getConfiguration().getClass();
			Constructor resct =Resources.class.getConstructor(typeArgs);
			valueArgs =new Object[3];
			valueArgs[0]=assetMag;
			valueArgs[1]=res.getDisplayMetrics();
			valueArgs[2]=res.getConfiguration();
			res =(Resources)resct.newInstance(valueArgs);
			CharSequence label=null;
			if(info.labelRes !=0){
				label =res.getText(info.labelRes);
			}
			Log.d("ANDROID_LAB", "label="+label);
			//这里就是读取一个apk程序的图标
			if(info.icon !=0){
				Drawable icon =res.getDrawable(info.icon);
				ImageView image =(ImageView) findViewById(R.id.apkIconBySodino);
				image.setVisibility(View.VISIBLE);
				image.setImageDrawable(icon);
				
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
