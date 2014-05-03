package org.lance.itu.main;

import org.lance.itu.util.ActivityForResultUtil;
import org.lance.itu.util.FileHandle;
import org.lance.itu.util.Prefs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * 图片编辑类
 * 
 * @author lance
 * 
 */
public class ImageFilterActivity extends BaseActivity implements OnClickListener {
	private Button mCancel;
	private Button mFinish;
	private ImageButton mBack;
	private ImageButton mForward;
	private ImageView mDisplay;
	private Button mCut;
	private Button mEffect;
	private Button mFace;
	private Button mFrame;

	private String mOldPath;
	private Bitmap mOldBitmap;
	private String mNewPath;
	private Bitmap mNewBitmap;
	private boolean mIsOld = true;

	public void onCreate(Bundle saved){
		super.onCreate(saved);
		setContentView(R.layout.imagefilter_activity);
		findViewById();
		setListener();
		mBack.setImageResource(R.drawable.image_action_back_arrow_normal);
		mForward.setImageResource(R.drawable.image_action_forward_arrow_normal);
		mBack.setEnabled(false);
		mForward.setEnabled(false);
		mNewPath = mOldPath = getIntent().getStringExtra("path");
		mOldBitmap=mNewBitmap=FileHandle.loadBitmapFromPath(mNewPath);
		mDisplay.setImageBitmap(mOldBitmap);
	}

	private void findViewById() {
		mCancel = (Button) findViewById(R.id.imagefilter_cancel);
		mFinish = (Button) findViewById(R.id.imagefilter_finish);
		mBack = (ImageButton) findViewById(R.id.imagefilter_back);
		mForward = (ImageButton) findViewById(R.id.imagefilter_forward);
		mDisplay = (ImageView) findViewById(R.id.imagefilter_display);
		mCut = (Button) findViewById(R.id.imagefilter_cut);
		mEffect = (Button) findViewById(R.id.imagefilter_effect);
		mFace = (Button) findViewById(R.id.imagefilter_face);
		mFrame = (Button) findViewById(R.id.imagefilter_frame);
	}

	private void setListener() {
		mCancel.setOnClickListener(this);
		mFinish.setOnClickListener(this);
		mBack.setOnClickListener(this);
		mForward.setOnClickListener(this);
		mCut.setOnClickListener(this);
		mEffect.setOnClickListener(this);
		mFace.setOnClickListener(this);
		mFrame.setOnClickListener(this);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (mIsOld) {
				mNewPath = data.getStringExtra("path");
				mNewBitmap=FileHandle.loadBitmapFromPath(mNewPath);
			} else {
				mOldPath = mNewPath;
				mOldBitmap = mNewBitmap;
				mNewPath = data.getStringExtra("path");
				mNewBitmap=FileHandle.loadBitmapFromPath(mNewPath);
			}
			Prefs.putRecentlyOperaFile(this, mNewPath);
			mIsOld = false;
			mBack.setImageResource(R.drawable.image_filter_action_back_arrow);
			mForward.setImageResource(R.drawable.image_action_forward_arrow_normal);
			mBack.setEnabled(true);
			mForward.setEnabled(false);
			mDisplay.setImageBitmap(mNewBitmap);
		}
	}

	public void finish(){
		super.finish();
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.imagefilter_cancel:
			finish();
			break;
		case R.id.imagefilter_finish:
			finish();
			break;
		case R.id.imagefilter_back://显示未修改的图片
			mIsOld = true;
			mBack.setImageResource(R.drawable.image_action_back_arrow_normal);
			mForward.setImageResource(R.drawable.image_filter_action_forward_arrow);
			mBack.setEnabled(false);
			mForward.setEnabled(true);
			mDisplay.setImageBitmap(mOldBitmap);
			break;
		case R.id.imagefilter_forward://显示修改后的图片
			mIsOld = false;
			mBack.setImageResource(R.drawable.image_filter_action_back_arrow);
			mForward.setImageResource(R.drawable.image_action_forward_arrow_normal);
			mBack.setEnabled(true);
			mForward.setEnabled(false);
			mDisplay.setImageBitmap(mNewBitmap);
			break;
		case R.id.imagefilter_cut:{//裁剪
			Intent intent = new Intent();
			intent.setClass(ImageFilterActivity.this,
					ImageFilterCropActivity.class);
			if (mIsOld) {
				intent.putExtra("path", mOldPath);
			} else {
				intent.putExtra("path", mNewPath);
			}
			startActivityForResult(intent,
					ActivityForResultUtil.REQUESTCODE_IMAGEFILTER_CROP);
		}
			break;
		case R.id.imagefilter_effect:{//添加特效
			Intent intent = new Intent();
			intent.setClass(ImageFilterActivity.this,
					ImageFilterEffectActivity.class);
			if (mIsOld) {
				intent.putExtra("path", mOldPath);
			} else {
				intent.putExtra("path", mNewPath);
			}
			startActivityForResult(intent,
					ActivityForResultUtil.REQUESTCODE_IMAGEFILTER_EFFECT);
		}
			break;
		case R.id.imagefilter_face:{//添加表情
			Intent intent = new Intent();
			intent.setClass(ImageFilterActivity.this,
					ImageFilterFaceActivity.class);
			if (mIsOld) {
				intent.putExtra("path", mOldPath);
			} else {
				intent.putExtra("path", mNewPath);
			}
			startActivityForResult(intent,
					ActivityForResultUtil.REQUESTCODE_IMAGEFILTER_FACE);
		}
			break;
		case R.id.imagefilter_frame:{//添加边框
			Intent intent = new Intent();
			intent.setClass(ImageFilterActivity.this,
					ImageFilterFrameActivity.class);
			if (mIsOld) {
				intent.putExtra("path", mOldPath);
			} else {
				intent.putExtra("path", mNewPath);
			}
			startActivityForResult(intent,
					ActivityForResultUtil.REQUESTCODE_IMAGEFILTER_FRAME);
			break;
		}
		}
	}

}
