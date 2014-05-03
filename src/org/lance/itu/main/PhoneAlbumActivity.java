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
 * �ֻ�ͼƬ�ļ���---��ȡ�ֻ��е�����ͼƬ��ʾ�ڸý���
 * 
 * @author lance
 * 
 */
public class PhoneAlbumActivity extends BaseActivity implements
		OnItemClickListener {
	private Button mCancel;
	private ListView mDisplay;
	// ���ﱣ���ļ�ϵͳ�ļ��еþ���·�����ļ�
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
		progressDialog.setMessage("���Ժ�...");
		progressDialog.show();
		
		findViewById();
		setListener();
		// ��ȡ�ֻ����ͼƬ����
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
				// �رյ�ǰ����
				finish();
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// �����ļ��е�ַ��ǰ�ļ��е�������ʾ��
		Intent intent = new Intent();
		intent.setClass(PhoneAlbumActivity.this, AlbumActivity.class);
		String path = (String) album.keySet().toArray()[position];// ���ﴫ���ļ���·����ȥ
		intent.putExtra("path", path);
		startActivity(intent);
		finish();
	}

}
