package org.lance.itu.ui;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * ��תλͼ����
 * @author lance
 *
 */
public class RotateBitmap {
    public static final String TAG = "RotateBitmap";
    private Bitmap mBitmap;
    private int mRotation;

    public RotateBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        mRotation = 0;
    }

    public RotateBitmap(Bitmap bitmap, int rotation) {
        mBitmap = bitmap;
        mRotation = rotation % 360;
    }

    public void setRotation(int rotation) {
        mRotation = rotation;
    }

    public int getRotation() {
        return mRotation;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public Matrix getRotateMatrix() {
        Matrix matrix = new Matrix();
        if (mRotation != 0) {
        	//����������ת����Դ,�����ڱ߽������ת�󽫱��ı�,��˦�ֵ�ֱ���ھɵĺ��µĿ��/�߶ȡ�
            int cx = mBitmap.getWidth() / 2;
            int cy = mBitmap.getHeight() / 2;
            matrix.preTranslate(-cx, -cy);
            matrix.postRotate(mRotation);//������ת����0,0Ϊ����
            matrix.postTranslate(getWidth() / 2, getHeight() / 2);//������ת��ͼƬ�ƶ�����ͼ����
        }
        return matrix;
    }
    //�����Ƿ�ı�
    public boolean isOrientationChanged() {
        return (mRotation / 90) % 2 != 0;
    }

    public int getHeight() {
        if (isOrientationChanged()) {
            return mBitmap.getWidth();
        } else {
            return mBitmap.getHeight();
        }
    }

    public int getWidth() {
        if (isOrientationChanged()) {
            return mBitmap.getHeight();
        } else {
            return mBitmap.getWidth();
        }
    }

    public void recycle() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }
}
