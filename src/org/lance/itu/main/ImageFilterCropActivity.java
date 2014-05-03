package org.lance.itu.main;

import org.lance.itu.ui.CropImage;
import org.lance.itu.ui.CropImageView;
import org.lance.itu.util.PhotoUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * 图片剪切类
 * 
 * @author lance
 * 
 */
public class ImageFilterCropActivity extends BaseActivity {
	private Button mCancel;
	private Button mDetermine;
	private CropImageView mDisplay;
	private ProgressBar mProgressBar;
	private Button mLeft;
	private Button mRight;

	public static final int SHOW_PROGRESS = 0;
	public static final int REMOVE_PROGRESS = 1;

	private String mPath;// 修改的图片地址
	private Bitmap mBitmap;// 修改的图片
	private CropImage mCropImage; // 裁剪工具类

	public void onCreate(Bundle saved){
		super.onCreate(saved);
		setContentView(R.layout.imagefilter_crop_activity);
		findViewById();
		setListener();
		
		mPath = getIntent().getStringExtra("path");
		mBitmap = PhotoUtil.createBitmap(mPath, mScreenWidth, mScreenHeight);
		if (mBitmap == null) {
			Toast.makeText(ImageFilterCropActivity.this, "没有找到图片", 0).show();
			setResult(RESULT_CANCELED);
			finish();
		} else {
			resetImageView(mBitmap);
		}
	}

	private void findViewById() {
		mCancel = (Button) findViewById(R.id.imagefilter_crop_cancel);
		mDetermine = (Button) findViewById(R.id.imagefilter_crop_determine);
		mDisplay = (CropImageView) findViewById(R.id.imagefilter_crop_display);
		mProgressBar = (ProgressBar) findViewById(R.id.imagefilter_crop_progressbar);
		mLeft = (Button) findViewById(R.id.imagefilter_crop_left);
		mRight = (Button) findViewById(R.id.imagefilter_crop_right);
	}

	private void setListener() {
		mCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 显示返回对话框
				backDialog();
			}
		});
		mDetermine.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 保存修改的图片到本地,并返回图片地址
				mPath = PhotoUtil.saveToLocal(mCropImage.cropAndSave());
				Intent intent = new Intent();
				intent.putExtra("path", mPath);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		mLeft.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 左旋转
				mCropImage.startRotate(270.f);
			}
		});
		mRight.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 有旋转
				mCropImage.startRotate(90.f);
			}
		});
	}

	/**
	 * 初始化图片显示
	 * 
	 * @param b
	 */
	private void resetImageView(Bitmap src) {
		mDisplay.clear();
		mDisplay.setImageBitmap(src);
		mDisplay.setImageBitmapResetBase(src, true);
		mCropImage = new CropImage(this, mDisplay, handler);
		mCropImage.crop(src);
	}

	/**
	 * 控制进度条
	 */
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_PROGRESS:
				mProgressBar.setVisibility(View.VISIBLE);
				break;
			case REMOVE_PROGRESS:
				handler.removeMessages(SHOW_PROGRESS);
				mProgressBar.setVisibility(View.INVISIBLE);
				break;
			}
		}
	};

	/**
	 * 返回对话框
	 */
	private void backDialog() {
		AlertDialog.Builder builder = new Builder(ImageFilterCropActivity.this);
		builder.setTitle("");
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage("你确定要取消编辑吗?");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}

	public void onBackPressed() {
		backDialog();
	}

}
