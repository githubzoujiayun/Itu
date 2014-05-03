package org.lance.itu.main;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lance.itu.adapter.PhoneAlnumAdapter;
import org.lance.itu.util.ImageUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

/**
 * 手机图片文件类---获取手机中的所有图片显示在该界面
 * 
 * @author lance
 * 
 */
public class PhoneAlbumActivity extends BaseActivity implements
		OnItemClickListener {
	private Button mCancel;
	private ListView mDisplay;
	// 这里保存文件系统文件夹得绝对路径和文件
	private Map<String, List<Map<String, String>>> album = new HashMap<String, List<Map<String, String>>>();

	private ProgressDialog progressDialog;
	private final int MSG_FINISHED=1;
	Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case MSG_FINISHED:
				progressDialog.dismiss();
				break;
			}
		}
	};
	protected void onCreate(Bundle saved){
		super.onCreate(saved);
		setContentView(R.layout.phone_album_activity);
		progressDialog=new ProgressDialog(this);
		progressDialog.setTitle("");
		progressDialog.setMessage("请稍后...");
		progressDialog.show();
		
		findViewById();
		setListener();
		// 获取手机里的图片内容
		new Thread(new Runnable() {
			public void run() {
				File sdRootFolder = new File("/mnt/sdcard/");
				album = ImageUtil.getFiles(sdRootFolder, null);
				runOnUiThread(new Runnable() {
					public void run() {
						mDisplay.setAdapter(new PhoneAlnumAdapter(
								PhoneAlbumActivity.this, album));
						handler.obtainMessage(MSG_FINISHED).sendToTarget();
					}
				});
			}
		}).start();
	}

	private void findViewById() {
		mCancel = (Button) findViewById(R.id.phonealbum_cancel);
		mDisplay = (ListView) findViewById(R.id.phonealbum_display);
	}

	private void setListener() {
		mDisplay.setOnItemClickListener(this);
		mCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 关闭当前界面
				finish();
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// 传递文件夹地址当前文件夹到内容显示类
		Intent intent = new Intent();
		intent.setClass(PhoneAlbumActivity.this, AlbumActivity.class);
		String path = (String) album.keySet().toArray()[position];// 这里传递文件夹路径过去
		intent.putExtra("path", path);
		startActivity(intent);
		finish();
	}

}
