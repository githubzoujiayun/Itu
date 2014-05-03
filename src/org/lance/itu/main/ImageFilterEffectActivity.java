package org.lance.itu.main;

import org.lance.itu.util.FileHandle;
import org.lance.itu.util.PhotoUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * 图片特效类
 * 
 * @author lance
 * 
 */
public class ImageFilterEffectActivity extends BaseActivity implements
		OnClickListener {
	private Button mCancel;
	private Button mDetermine;
	private ImageView mDisplay;
	/** 原图 */
	private ImageButton mEffect1;
	/** Lomo */
	private ImageButton mEffect2;
	/** 冰冻 */
	private ImageButton mEffect3;
	/** 熔铸 */
	private ImageButton mEffect4;
	/** 光晕 */
	private ImageButton mEffect5;
	/** 单纯 */
	private ImageButton mEffect6;
	/** 底片 */
	private ImageButton mEffect7;
	/** 暖意 */
	private ImageButton mEffect8;
	/** 旧时光 */
	private ImageButton mEffect9;
	/** 暖意 */
	private ImageButton mEffect10;
	/** 锐化 */
	private ImageButton mEffect11;
	/** 模糊 */
	private ImageButton mEffect12;

	private String mPath;// 图片地址
	private Bitmap mOldBitmap;// 旧图片
	private Bitmap mNewBitmap;// 新图片
	private boolean isEffect;// 是否修改

	public void onCreate(Bundle saved) {
		super.onCreate(saved);
		setContentView(R.layout.imagefilter_effect_activity);
		findViewById();
		setListener();
		// 获取图片地址、图片并显示图片
		mPath = getIntent().getStringExtra("path");
		mOldBitmap = FileHandle.loadBitmapFromPath(mPath);
		mDisplay.setImageBitmap(mOldBitmap);
	}

	private void findViewById() {
		mCancel = (Button) findViewById(R.id.imagefilter_effect_cancel);
		mDetermine = (Button) findViewById(R.id.imagefilter_effect_determine);
		mDisplay = (ImageView) findViewById(R.id.imagefilter_effect_display);
		mEffect1 = (ImageButton) findViewById(R.id.imagefilter_effect_effect1);
		mEffect2 = (ImageButton) findViewById(R.id.imagefilter_effect_effect2);
		mEffect3 = (ImageButton) findViewById(R.id.imagefilter_effect_effect3);
		mEffect4 = (ImageButton) findViewById(R.id.imagefilter_effect_effect4);
		mEffect5 = (ImageButton) findViewById(R.id.imagefilter_effect_effect5);
		mEffect6 = (ImageButton) findViewById(R.id.imagefilter_effect_effect6);
		mEffect7 = (ImageButton) findViewById(R.id.imagefilter_effect_effect7);
		mEffect8 = (ImageButton) findViewById(R.id.imagefilter_effect_effect8);
		mEffect9 = (ImageButton) findViewById(R.id.imagefilter_effect_effect9);
		mEffect10 = (ImageButton) findViewById(R.id.imagefilter_effect_effect10);
		mEffect11 = (ImageButton) findViewById(R.id.imagefilter_effect_effect11);
		mEffect12 = (ImageButton) findViewById(R.id.imagefilter_effect_effect12);
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
				// 如果是1代表原图,不做任何操作返回,否则则保存图片到本地并返回地址
				if (!isEffect) {
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
		mEffect1.setOnClickListener(this);
		mEffect2.setOnClickListener(this);
		mEffect3.setOnClickListener(this);
		mEffect4.setOnClickListener(this);
		mEffect5.setOnClickListener(this);
		mEffect6.setOnClickListener(this);
		mEffect7.setOnClickListener(this);
		mEffect8.setOnClickListener(this);
		mEffect9.setOnClickListener(this);
		mEffect10.setOnClickListener(this);
		mEffect11.setOnClickListener(this);
		mEffect12.setOnClickListener(this);
	}

	/**
	 * 返回对话框
	 */
	private void backDialog() {
		AlertDialog.Builder builder = new Builder(
				ImageFilterEffectActivity.this);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imagefilter_effect_effect1:
			mNewBitmap = mOldBitmap;
			break;
		case R.id.imagefilter_effect_effect2:
			mNewBitmap = PhotoUtil.lomoFilter(mOldBitmap);
			break;
		case R.id.imagefilter_effect_effect3:
			mNewBitmap = PhotoUtil.frozenFilter(mOldBitmap);
			break;
		case R.id.imagefilter_effect_effect4:
			mNewBitmap = PhotoUtil.castingFilter(mOldBitmap);
			break;
		case R.id.imagefilter_effect_effect5:
			mNewBitmap = PhotoUtil.haloFilter(mOldBitmap,
					mOldBitmap.getWidth() / 2, mOldBitmap.getHeight() / 2, 10f);
			break;
		case R.id.imagefilter_effect_effect6:
			mNewBitmap = PhotoUtil.blackAndWhiteBitmap(mOldBitmap);
			break;
		case R.id.imagefilter_effect_effect7:
			mNewBitmap = PhotoUtil.negativeFilter(mOldBitmap);
			break;
		case R.id.imagefilter_effect_effect8:
			mNewBitmap = PhotoUtil.reliefFilter(mOldBitmap);
			break;
		case R.id.imagefilter_effect_effect9:
			mNewBitmap = PhotoUtil.oldTimeFilter(mOldBitmap);
			break;
		case R.id.imagefilter_effect_effect10:
			mNewBitmap = PhotoUtil.warmthFilter(mOldBitmap,
					mOldBitmap.getWidth() / 2, mOldBitmap.getHeight() / 2);
			break;
		case R.id.imagefilter_effect_effect11:
			mNewBitmap = PhotoUtil.sharpenFilter(mOldBitmap);
			break;
		case R.id.imagefilter_effect_effect12:
			mNewBitmap = PhotoUtil.blurImageAmeliorateFilter(mOldBitmap);
			break;
		}
		isEffect = true;
		mDisplay.setImageBitmap(mNewBitmap);
	}

}
