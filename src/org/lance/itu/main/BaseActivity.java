package org.lance.itu.main;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

public class BaseActivity extends Activity {

	protected int mScreenWidth;
	protected int mScreenHeight;
	
	private static ThreadPoolExecutor threadExecutor;
	static {
		threadExecutor = new ThreadPoolExecutor(5, 10, 30L, TimeUnit.SECONDS,
				new SynchronousQueue<Runnable>(),
				new ThreadPoolExecutor.CallerRunsPolicy());
	}
	
	protected void onCreate(Bundle saved){
		super.onCreate(saved);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
		
	}
	
	public void execTask(Thread run) {
		threadExecutor.execute(run);
	}
	
	protected String getBaiduMapKey(){
		try {
			ApplicationInfo appInfo = getPackageManager()
			        .getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
			return appInfo.metaData.getString("baidu_key");
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private static Toast shortToast;
	private static Toast longToast;

	public void showHint(String str) {
		if (shortToast == null) {
			shortToast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
		} else {
			shortToast.setText(str);
		}
		shortToast.show();
	}

	public void showHintLong(String str) {
		if (longToast == null) {
			longToast = Toast.makeText(this, str, Toast.LENGTH_LONG);
		} else {
			longToast.setText(str);
		}
		longToast.show();
	}

	public void showHint(int strId) {
		if (shortToast == null) {
			shortToast = Toast.makeText(this, strId, Toast.LENGTH_SHORT);
		} else {
			shortToast.setText(strId);
		}
		shortToast.show();
	}

	public void showHintLong(int strId) {
		if (longToast == null) {
			longToast = Toast.makeText(this, strId, Toast.LENGTH_LONG);
		} else {
			longToast.setText(strId);
		}
		longToast.show();
	}
	
	public void back(View view){
		finish();
	}
}
