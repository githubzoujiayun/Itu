package org.lance.itu.main;

import org.lance.itu.anim.PhotoAnimations;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * �Ƴ�ͼƬ��
 * 
 * @author lance
 * 
 */
public class DeletePhotoActivity extends BaseActivity {
	private RelativeLayout mTitle;
	private Button mBack;
	private Button mDelete;
	private ImageView mDisplay;

	protected void onCreate(Bundle saved){
		super.onCreate(saved);
		setContentView(R.layout.deletephoto_activity);
		findViewById();
		setListener();
		// ��ȡͼƬ��·��
		String path = getIntent().getStringExtra("path");
		// ��ȡͼƬ
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		// ��ʾͼƬ
		mDisplay.setImageBitmap(bitmap);
	}

	private void findViewById() {
		mTitle = (RelativeLayout) findViewById(R.id.deletephoto_title);
		mBack = (Button) findViewById(R.id.deletephoto_back);
		mDelete = (Button) findViewById(R.id.deletephoto_delete);
		mDisplay = (ImageView) findViewById(R.id.deletephoto_display);
	}

	private void setListener() {
		mBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// �رյ�ǰ����,����������
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		mDelete.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// �رյ�ǰ����,����������
				setResult(RESULT_OK);
				finish();
			}
		});
		mDisplay.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// �жϱ������Ƿ���ʾ,��ʾ������,��������ʾ
				if (mTitle.isShown()) {
					PhotoAnimations.startTopOutAnimation(mTitle);
				} else {
					PhotoAnimations.startTopInAnimation(mTitle);
				}
			}
		});
	}

	public void onBackPressed() {
		// �رյ�ǰ����,����������
		setResult(RESULT_CANCELED);
		finish();
	}

}