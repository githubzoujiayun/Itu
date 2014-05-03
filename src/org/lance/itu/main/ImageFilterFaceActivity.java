package org.lance.itu.main;

import java.io.IOException;
import java.util.LinkedList;

import org.lance.itu.ui.FaceImage;
import org.lance.itu.ui.FaceImageView;
import org.lance.itu.util.FileHandle;
import org.lance.itu.util.PhotoUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * ͼƬ������
 * 
 * @author lance
 * 
 */
public class ImageFilterFaceActivity extends BaseActivity implements OnClickListener {
	private Button mCancel;
	private Button mDetermine;
	private RelativeLayout mDisplayLayout;
	private FaceImageView mDisplay;
	private ImageButton mFace1;
	private ImageButton mFace2;
	private ImageButton mFace3;
	private ImageButton mFace4;
	private ImageButton mFace5;
	private ImageButton mFace6;
	private ImageButton mFace7;
	private ImageButton mFace8;
	private ImageButton mFace9;
	private ImageButton mFace10;
	private ImageButton mFace11;
	private ImageButton mFace12;
	private ImageButton mFace13;

	private String mPath;// ͼƬ�ĵ�ַ
	private Bitmap mOldBitmap;// ��ͼƬ
	private Bitmap mNewBitmap;// ��ͼƬ
	private Bitmap mFaceBitmap; // ����ͼƬ
	private boolean mIsMeasured;// �Ƿ��Ѿ������С
	private float mMaxWidth;// ͼƬ�����
	private float mMaxHeight;// ͼƬ���߶�

	public void onCreate(Bundle saved){
		super.onCreate(saved);
		setContentView(R.layout.imagefilter_face_activity);
		findViewById();
		setListener();
		// ��ȡRelativeLayout�ĸ߶ȺͿ��
		ViewTreeObserver vto = mDisplayLayout.getViewTreeObserver();
		vto.addOnPreDrawListener(new OnPreDrawListener() {

			public boolean onPreDraw() {
				if (mIsMeasured == false) {
					mMaxWidth = mDisplayLayout.getMeasuredWidth();
					mMaxHeight = mDisplayLayout.getMeasuredHeight();
					mPath = getIntent().getStringExtra("path");
					FileHandle.loadBitmapFromPath(mPath);
					mOldBitmap = zoom(FileHandle.loadBitmapFromPath(mPath));
					mDisplay.setImageBitmap(mOldBitmap);
					mIsMeasured = true;
				}
				return true;
			}
		});
	}

	private void findViewById() {
		mCancel = (Button) findViewById(R.id.imagefilter_face_cancel);
		mDetermine = (Button) findViewById(R.id.imagefilter_face_determine);
		mDisplayLayout = (RelativeLayout) findViewById(R.id.imagefilter_face_display_layout);
		mDisplay = (FaceImageView) findViewById(R.id.imagefilter_face_display);
		mFace1 = (ImageButton) findViewById(R.id.imagefilter_face_face1);
		mFace2 = (ImageButton) findViewById(R.id.imagefilter_face_face2);
		mFace3 = (ImageButton) findViewById(R.id.imagefilter_face_face3);
		mFace4 = (ImageButton) findViewById(R.id.imagefilter_face_face4);
		mFace5 = (ImageButton) findViewById(R.id.imagefilter_face_face5);
		mFace6 = (ImageButton) findViewById(R.id.imagefilter_face_face6);
		mFace7 = (ImageButton) findViewById(R.id.imagefilter_face_face7);
		mFace8 = (ImageButton) findViewById(R.id.imagefilter_face_face8);
		mFace9 = (ImageButton) findViewById(R.id.imagefilter_face_face9);
		mFace10 = (ImageButton) findViewById(R.id.imagefilter_face_face10);
		mFace11 = (ImageButton) findViewById(R.id.imagefilter_face_face11);
		mFace12 = (ImageButton) findViewById(R.id.imagefilter_face_face12);
		mFace13 = (ImageButton) findViewById(R.id.imagefilter_face_face13);
	}

	private void setListener() {
		mCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				backDialog();
			}
		});
		mDetermine.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mFaceBitmap == null) {
					setResult(RESULT_CANCELED);
					finish();
				} else {
					mNewBitmap = Bitmap.createBitmap(mOldBitmap.getWidth(),
							mOldBitmap.getHeight(), Config.ARGB_8888);
					LinkedList<FaceImage> mFaceImages = mDisplay.getFaceImages();
					Canvas canvas = new Canvas(mNewBitmap);//����һ������
					canvas.drawBitmap(mOldBitmap, 0, 0, null);
					for (int i = mFaceImages.size(); i > 0; i--) {
						mFaceImages.get(i - 1).draw(canvas);//ѭ���ڻ����ϻ���ͼ��
					}
					canvas.save(Canvas.ALL_SAVE_FLAG);
					canvas.restore();
					mPath = PhotoUtil.saveToLocalPNG(mNewBitmap);
					Intent intent = new Intent();
					intent.putExtra("path", mPath);
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		});
		mFace1.setOnClickListener(this);
		mFace2.setOnClickListener(this);
		mFace3.setOnClickListener(this);
		mFace4.setOnClickListener(this);
		mFace5.setOnClickListener(this);
		mFace6.setOnClickListener(this);
		mFace7.setOnClickListener(this);
		mFace8.setOnClickListener(this);
		mFace9.setOnClickListener(this);
		mFace10.setOnClickListener(this);
		mFace11.setOnClickListener(this);
		mFace12.setOnClickListener(this);
		mFace13.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.imagefilter_face_face1:
				mFaceBitmap = decodeAssets("accessories/new_year_1.png");
			break;
		case R.id.imagefilter_face_face2:
				mFaceBitmap = decodeAssets("accessories/new_year_2.png");
			break;
		case R.id.imagefilter_face_face3:
				mFaceBitmap = decodeAssets("accessories/new_year_3.png");
			break;
		case R.id.imagefilter_face_face4:
				mFaceBitmap = decodeAssets("accessories/new_year_4.png");
			break;
		case R.id.imagefilter_face_face5:
				mFaceBitmap = decodeAssets("accessories/image_face_forbite.png");
			break;
		case R.id.imagefilter_face_face6:
				mFaceBitmap = decodeAssets("accessories/image_face_rabbit.png");
			break;
		case R.id.imagefilter_face_face7:
				mFaceBitmap = decodeAssets("accessories/image_face1.png");
			break;
		case R.id.imagefilter_face_face8:
				mFaceBitmap = decodeAssets("accessories/image_face2.png");
			break;
		case R.id.imagefilter_face_face9:
				mFaceBitmap = decodeAssets("accessories/image_face3.png");
			break;
		case R.id.imagefilter_face_face10:
				mFaceBitmap = decodeAssets("accessories/image_face4.png");
			break;
		case R.id.imagefilter_face_face11:
				mFaceBitmap = decodeAssets("accessories/image_face9.png");
			break;
		case R.id.imagefilter_face_face12:
				mFaceBitmap = decodeAssets("accessories/image_face10.png");
			break;
		case R.id.imagefilter_face_face13:
				mFaceBitmap = decodeAssets("accessories/image_face11.png");
			break;
		}
		mDisplay.addFace(mFaceBitmap);
		mDisplay.invalidate();
	}

	public Bitmap decodeAssets(String path){
		try {
			Bitmap dest = BitmapFactory.decodeStream(getAssets().open(path));
			return dest;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * ����ͼƬ
	 * 
	 * @param bitmap
	 *            ��Ҫ���ŵ�ͼƬ
	 * @return ���ź��ͼƬ
	 */
	public Bitmap zoom(Bitmap bitmap) {
		float width = bitmap.getWidth();
		float height = bitmap.getHeight();
		// ��ȡ40dip��pxֵ
		int padding = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 40, getResources()
						.getDisplayMetrics());
		float maxWidth = mMaxWidth - padding;
		float maxHeight = mMaxHeight - padding;
		// �ж������Ȼ�߶ȳ������ֵ,������,���򷵻�ԭͼƬ
		if (width > maxWidth || height > maxHeight) {
			float scale = getScale(width, height, maxWidth, maxHeight);
			int bitmapWidth = (int) (width * scale);
			int bitmapHeight = (int) (height * scale);
			Bitmap zoomBitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth,
					bitmapHeight, true);
			return zoomBitmap;
		} else {
			return bitmap;
		}

	}

	/** �������ű��� */
	private float getScale(float width, float height, float maxWidth,
			float maxHeight) {
		float scaleWidth = maxWidth / width;
		float scaleHeight = maxHeight / height;
		return Math.min(scaleWidth, scaleHeight);
	}

	/**
	 * ���ضԻ���
	 */
	private void backDialog() {
		AlertDialog.Builder builder = new Builder(ImageFilterFaceActivity.this);
		builder.setTitle("");
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage("��ȷ��Ҫȡ���༭��?");
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}

	public void onBackPressed() {
		// ���ضԻ���
		backDialog();
	}

}
