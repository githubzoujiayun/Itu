package org.lance.itu.main;

import java.io.IOException;

import org.lance.itu.util.FileHandle;
import org.lance.itu.util.PhotoUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * 图片边框类
 * 
 * @author lance
 * 
 */
public class ImageFilterFrameActivity extends BaseActivity implements
		OnClickListener {
	private Button mCancel;
	private Button mDetermine;
	private ImageView mDisplay;
	private ImageButton mFrame_1;
	private ImageButton mFrame_2;
	private ImageButton mFrame_3;
	private ImageButton mFrame_4;
	private ImageButton mFrame_5;
	private ImageButton mFrame_6;
	private ImageButton mFrame_7;
	private ImageButton mFrame_8;
	private ImageButton mFrame_9;
	private ImageButton mFrame_10;

	private String mPath;// 图片地址
	private Bitmap mOldBitmap;// 旧图片
	private Bitmap mNewBitmap;// 新图片
	private int mFrameId = 0;// 边框编号

	public void onCreate(Bundle saved){
		super.onCreate(saved);
		setContentView(R.layout.imagefilter_frame_activity);
		findViewById();
		setListener();
		mPath = getIntent().getStringExtra("path");
		mOldBitmap = FileHandle.loadBitmapFromPath(mPath);
		mDisplay.setImageBitmap(mOldBitmap);
	}

	private void findViewById() {
		mCancel = (Button) findViewById(R.id.imagefilter_frame_cancel);
		mDetermine = (Button) findViewById(R.id.imagefilter_frame_determine);
		mDisplay = (ImageView) findViewById(R.id.imagefilter_frame_display);
		mFrame_1 = (ImageButton) findViewById(R.id.imagefilter_frame_frame1);
		mFrame_2 = (ImageButton) findViewById(R.id.imagefilter_frame_frame2);
		mFrame_3 = (ImageButton) findViewById(R.id.imagefilter_frame_frame3);
		mFrame_4 = (ImageButton) findViewById(R.id.imagefilter_frame_frame4);
		mFrame_5 = (ImageButton) findViewById(R.id.imagefilter_frame_frame5);
		mFrame_6 = (ImageButton) findViewById(R.id.imagefilter_frame_frame6);
		mFrame_7 = (ImageButton) findViewById(R.id.imagefilter_frame_frame7);
		mFrame_8 = (ImageButton) findViewById(R.id.imagefilter_frame_frame8);
		mFrame_9 = (ImageButton) findViewById(R.id.imagefilter_frame_frame9);
		mFrame_10 = (ImageButton) findViewById(R.id.imagefilter_frame_frame10);
	}

	private void setListener() {
		mCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 返回对话框
				backDialog();
			}
		});
		mDetermine.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 如果id为0,代表没做任何操作,则无需返回值,否则则保存当前修改的图片并返回地址
				if (mFrameId == 0) {
					setResult(RESULT_CANCELED);
					finish();
				} else {
					mPath = PhotoUtil.saveToLocal(mNewBitmap);
					Intent intent = new Intent();
					intent.putExtra("path", mPath);
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		});
		mFrame_1.setOnClickListener(this);
		mFrame_2.setOnClickListener(this);
		mFrame_3.setOnClickListener(this);
		mFrame_4.setOnClickListener(this);
		mFrame_5.setOnClickListener(this);
		mFrame_6.setOnClickListener(this);
		mFrame_7.setOnClickListener(this);
		mFrame_8.setOnClickListener(this);
		mFrame_9.setOnClickListener(this);
		mFrame_10.setOnClickListener(this);
	}

	/**
	 * 返回对话框
	 */
	private void backDialog() {
		AlertDialog.Builder builder = new Builder(ImageFilterFrameActivity.this);
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
		// 返回对话框
		backDialog();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imagefilter_frame_frame1:
			mFrameId = 1;
			mNewBitmap = PhotoUtil.combinateFrame(
					ImageFilterFrameActivity.this, mOldBitmap, "frame1");
			break;
		case R.id.imagefilter_frame_frame2:
			mFrameId = 2;
			mNewBitmap = PhotoUtil.combinateFrame(
					ImageFilterFrameActivity.this, mOldBitmap, "frame2");
			break;
		case R.id.imagefilter_frame_frame3:
			mFrameId = 3;
			mNewBitmap = PhotoUtil.combinateFrame(
					ImageFilterFrameActivity.this, mOldBitmap, "frame3");
			break;
		case R.id.imagefilter_frame_frame4:
			mFrameId = 4;
			mNewBitmap = PhotoUtil.combinateFrame(
					ImageFilterFrameActivity.this, mOldBitmap, "frame4");
			break;
		case R.id.imagefilter_frame_frame5:
			mFrameId = 5;
			try {
				mNewBitmap = PhotoUtil.addBigFrame(
						mOldBitmap,
						BitmapFactory.decodeStream(getAssets().open(
								"frames/frame5/mist.png")));
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case R.id.imagefilter_frame_frame6:
			mFrameId = 6;
			try {
				mNewBitmap = PhotoUtil.addBigFrame(
						mOldBitmap,
						BitmapFactory.decodeStream(getAssets().open(
								"frames/frame6/love.png")));
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case R.id.imagefilter_frame_frame7:
			mFrameId = 7;
			mNewBitmap = PhotoUtil.combinateFrame(
					ImageFilterFrameActivity.this, mOldBitmap, "frame7");
			break;
		case R.id.imagefilter_frame_frame8:
			mFrameId = 8;
			try {
				mNewBitmap = PhotoUtil.addBigFrame(
						mOldBitmap,
						BitmapFactory.decodeStream(getAssets().open(
								"frames/frame8/transparent.png")));
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case R.id.imagefilter_frame_frame9:
			mFrameId = 9;
			try {
				mNewBitmap = PhotoUtil.addBigFrame(
						mOldBitmap,
						BitmapFactory.decodeStream(getAssets().open(
								"frames/frame9/black.png")));
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case R.id.imagefilter_frame_frame10:
			mFrameId = 10;
			mNewBitmap = PhotoUtil.combinateFrame(
					ImageFilterFrameActivity.this, mOldBitmap, "frame10");
			break;
		}
		if(mNewBitmap!=null){
			mDisplay.setImageBitmap(mNewBitmap);
		}
	}

}
