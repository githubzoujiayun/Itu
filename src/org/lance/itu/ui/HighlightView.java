package org.lance.itu.ui;

import org.lance.itu.main.R;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * This class is used by CropImage to display a highlighted cropping rectangle
 * overlayed with the image. There are two coordinate spaces in use. One is
 * image, another is screen. computeLayout() uses mMatrix to map from image
 * space to screen space.
 * @author lance
 *
 */
public class HighlightView {

	@SuppressWarnings("unused")
	private static final String TAG = "HighlightView";
	View mContext; // The View displaying the image.
	//边缘线的颜色
	private int outlineColor=Color.RED;
	//拖动和到达最大时的颜色
	private int downColor=Color.YELLOW;
	//是否为任意裁剪模式
	private boolean isFree=true;

	public static final int GROW_NONE = (1 << 0);
	public static final int GROW_LEFT_EDGE = (1 << 1);
	public static final int GROW_RIGHT_EDGE = (1 << 2);
	public static final int GROW_TOP_EDGE = (1 << 3);
	public static final int GROW_BOTTOM_EDGE = (1 << 4);
	public static final int MOVE = (1 << 5);

	public HighlightView(View ctx) {
		mContext = ctx;
	}

	private void init() {
		android.content.res.Resources resources = mContext.getResources();
		mResizeDrawableDiagonal = resources
				.getDrawable(R.drawable.picture_cut_button_normal);
	}

	public boolean mIsFocused;
	boolean mHidden;

	public boolean hasFocus() {
		return mIsFocused;
	}

	public void setFocus(boolean f) {
		mIsFocused = f;
	}

	public void setHidden(boolean hidden) {
		mHidden = hidden;
	}
	
	public void setFree(boolean free){
		isFree=free;
	}
	
	//绘制高亮线
	public void draw(Canvas canvas) {
		if (mHidden) {
			return;
		}
		canvas.save();
		Path path = new Path();
		if (!hasFocus()) {
			canvas.drawRect(mDrawRect, mOutlinePaint);
		} else {
			Rect viewDrawingRect = new Rect();
			mContext.getDrawingRect(viewDrawingRect);
			if (mCircle) {//环形时
				float width = mDrawRect.width();
				float height = mDrawRect.height();
				path.addCircle(mDrawRect.left + (width / 2), mDrawRect.top
						+ (height / 2), width / 2, Path.Direction.CW);
				mOutlinePaint.setColor(0xFFEF04D6);
			} else {//矩形时的颜色
				path.addRect(new RectF(mDrawRect), Path.Direction.CW);
			}
			Region region = new Region();
			region.set(viewDrawingRect);
			region.op(mDrawRect, Region.Op.DIFFERENCE);
			RegionIterator iter = new RegionIterator(region);
			Rect r = new Rect();
			while (iter.next(r)) {
				canvas.drawRect(r, hasFocus() ? mFocusPaint : mNoFocusPaint);
			}
			canvas.restore();
			canvas.drawPath(path, mOutlinePaint);

			if (mMode == ModifyMode.Grow) {
				if (mCircle) {
					int width = mResizeDrawableDiagonal.getIntrinsicWidth();
					int height = mResizeDrawableDiagonal.getIntrinsicHeight();

					int d = (int) Math.round(Math.cos(/* 45deg */Math.PI / 4D)
							* (mDrawRect.width() / 2D));
					int x = mDrawRect.left + (mDrawRect.width() / 2) + d
							- width / 2;
					int y = mDrawRect.top + (mDrawRect.height() / 2) - d
							- height / 2;
					mResizeDrawableDiagonal.setBounds(x, y, x
							+ mResizeDrawableDiagonal.getIntrinsicWidth(), y
							+ mResizeDrawableDiagonal.getIntrinsicHeight());
					mResizeDrawableDiagonal.draw(canvas);
				} else {
				}
			}
			if (mCircle) {

			} else {
				int left = mDrawRect.left;
				int right = mDrawRect.right;
				int top = mDrawRect.top;
				int bottom = mDrawRect.bottom;

				int widthWidth = mResizeDrawableDiagonal.getIntrinsicWidth() / 2;
				int widthHeight = mResizeDrawableDiagonal.getIntrinsicHeight() / 2;

				mResizeDrawableDiagonal.setBounds(left - widthWidth, top
						- widthHeight, left + widthWidth, top + widthHeight);
				mResizeDrawableDiagonal.draw(canvas);
				mResizeDrawableDiagonal.setBounds(right - widthWidth, top
						- widthHeight, right + widthWidth, top + widthHeight);
				mResizeDrawableDiagonal.draw(canvas);
				mResizeDrawableDiagonal.setBounds(left - widthWidth, bottom
						- widthHeight, left + widthWidth, bottom + widthHeight);
				mResizeDrawableDiagonal.draw(canvas);
				mResizeDrawableDiagonal
						.setBounds(right - widthWidth, bottom - widthHeight,
								right + widthWidth, bottom + widthHeight);
				mResizeDrawableDiagonal.draw(canvas);
			}
		}
	}

	public void setMode(ModifyMode mode) {
		mOutlinePaint.setColor(outlineColor);
		if (mode != mMode) {
			mMode = mode;
			mContext.invalidate();
		}
	}

	//确定触摸的那一边
	//Determines which edges are hit by touching at (x, y).
	public int getHit(float x, float y) {
		Rect r = computeLayout();
		final float hysteresis = 20F;
		int retval = GROW_NONE;

		if (mCircle) {
			float distX = x - r.centerX();
			float distY = y - r.centerY();
			int distanceFromCenter = (int) Math.sqrt(distX * distX + distY
					* distY);
			int radius = mDrawRect.width() / 2;
			int delta = distanceFromCenter - radius;
			if (Math.abs(delta) <= hysteresis) {
				if (Math.abs(distY) > Math.abs(distX)) {
					if (distY < 0) {
						retval = GROW_TOP_EDGE;
					} else {
						retval = GROW_BOTTOM_EDGE;
					}
				} else {
					if (distX < 0) {
						retval = GROW_LEFT_EDGE;
					} else {
						retval = GROW_RIGHT_EDGE;
					}
				}
			} else if (distanceFromCenter < radius) {
				retval = MOVE;
			} else {
				retval = GROW_NONE;
			}
		} else {
			// verticalCheck makes sure the position is between the top and
			// the bottom edge (with some tolerance). Similar for horizCheck.
			boolean verticalCheck = (y >= r.top - hysteresis)
					&& (y < r.bottom + hysteresis);
			boolean horizCheck = (x >= r.left - hysteresis)
					&& (x < r.right + hysteresis);

			// Check whether the position is near some edge(s).
			if ((Math.abs(r.left - x) < hysteresis) && verticalCheck) {
				retval |= GROW_LEFT_EDGE;
			}
			if ((Math.abs(r.right - x) < hysteresis) && verticalCheck) {
				retval |= GROW_RIGHT_EDGE;
			}
			if ((Math.abs(r.top - y) < hysteresis) && horizCheck) {
				retval |= GROW_TOP_EDGE;
			}
			if ((Math.abs(r.bottom - y) < hysteresis) && horizCheck) {
				retval |= GROW_BOTTOM_EDGE;
			}

			// Not near any edge but inside the rectangle: move.
			if (retval == GROW_NONE && r.contains((int) x, (int) y)) {
				retval = MOVE;
			}
		}
		return retval;
	}

	// Handles motion (dx, dy) in screen space. 3  5  9  17  32为移动状态
	// The "edge" parameter specifies which edges the user is dragging.
	public void handleMotion(int edge, float dx, float dy) {
		mOutlinePaint.setColor(outlineColor);
		Rect r = computeLayout();
		if (edge == GROW_NONE) {
			return;
		} else if (edge == MOVE) {
			mOutlinePaint.setColor(downColor);
			// Convert to image space before sending to moveBy().
			moveBy(dx * (mCropRect.width() / r.width()),
					dy * (mCropRect.height() / r.height()));
		} else {
			if (((GROW_LEFT_EDGE | GROW_RIGHT_EDGE) & edge) == 0) {
				dx = 0;
			}

			if (((GROW_TOP_EDGE | GROW_BOTTOM_EDGE) & edge) == 0) {
				dy = 0;
			}

			// Convert to image space before sending to growBy().
			float xDelta = dx * (mCropRect.width() / r.width());
			float yDelta = dy * (mCropRect.height() / r.height());
			growBy((((edge & GROW_LEFT_EDGE) != 0) ? -1 : 1) * xDelta,
					(((edge & GROW_TOP_EDGE) != 0) ? -1 : 1) * yDelta);
		}
	}

	// Grows the cropping rectange by (dx, dy) in image space.
	void moveBy(float dx, float dy) {
		Rect invalRect = new Rect(mDrawRect);

		mCropRect.offset(dx, dy);

		// Put the cropping rectangle inside image rectangle.
		mCropRect.offset(Math.max(0, mImageRect.left - mCropRect.left),
				Math.max(0, mImageRect.top - mCropRect.top));

		mCropRect.offset(Math.min(0, mImageRect.right - mCropRect.right),
				Math.min(0, mImageRect.bottom - mCropRect.bottom));

		mDrawRect = computeLayout();
		invalRect.union(mDrawRect);
		invalRect.inset(-10, -10);
		// mContext.invalidate(invalRect);
		mContext.invalidate();
	}

	// Grows the cropping rectange by (dx, dy) in image space.
	//扩展矩形框
	private void growBy(float dx, float dy) {
		if (mMaintainAspectRatio) {
			if(isFree){
				dx = dx * mInitialAspectRatio;
				dy = dy * mInitialAspectRatio;
			}else{
				if (dx != 0) {
					dy = dx / mInitialAspectRatio;
				} else if (dy != 0) {
					dx = dy * mInitialAspectRatio;
				}
			}
		}

		// Don't let the cropping rectangle grow too fast.避免矩形太快
		// Grow at most half of the difference between the image rectangle and
		// the cropping rectangle.
		RectF rf = new RectF(mCropRect);
		if (dx > 0F && rf.width() + 2 * dx > mImageRect.width()) {
			float adjustment = (mImageRect.width() - rf.width()) / 2F;
			dx = adjustment;
			if (mMaintainAspectRatio) {
				dy = dx / mInitialAspectRatio;
			}
		}
		if (dy > 0F && rf.height() + 2 * dy > mImageRect.height()) {
			float adjustment = (mImageRect.height() - rf.height()) / 2F;
			dy = adjustment;
			if (mMaintainAspectRatio) {
				dx = dy * mInitialAspectRatio;
			}
		}

		rf.inset(-dx, -dy);

		// Don't let the cropping rectangle shrink too fast.
		final float widthCap = 25F;
		if (rf.width() < widthCap) {
			return;
		}
		float heightCap = mMaintainAspectRatio ? (widthCap / mInitialAspectRatio)
				: widthCap;
		if (rf.height() < heightCap) {
			return;
		}

		// Put the cropping rectangle inside the image rectangle.
		//System.out.println(rf.left+"-"+mImageRect.left+"   "+rf.right+"-"+mImageRect.right);
		//System.out.println(rf.top+"-"+mImageRect.top+"   "+rf.bottom+"-"+mImageRect.bottom);
		//System.out.println("==============");
		if (rf.left < mImageRect.left) {
			rf.offset(mImageRect.left - rf.left, 0F);
		} else if (rf.right > mImageRect.right) {
			rf.offset(-(rf.right - mImageRect.right), 0);
		}
		if (rf.top < mImageRect.top) {
			rf.offset(0F, mImageRect.top - rf.top);
		} else if (rf.bottom > mImageRect.bottom) {
			rf.offset(0F, -(rf.bottom - mImageRect.bottom));
		}
		if(rf.left<mImageRect.left){
			rf.left=mImageRect.left;
		}
		if(rf.right>mImageRect.right){
			rf.right=mImageRect.right;
		}
		if(rf.top<mImageRect.top){
			rf.top=mImageRect.top;
		}
		if(rf.bottom>mImageRect.bottom){
			rf.bottom=mImageRect.bottom;
		}
		if(rf.left == mImageRect.left&&rf.right == mImageRect.right&&
				rf.top == mImageRect.top&&rf.bottom == mImageRect.bottom){
			mOutlinePaint.setColor(downColor);
		}
		mCropRect.set(rf);
		mDrawRect = computeLayout();
		mContext.invalidate();
	}

	// Returns the cropping rectangle in image space.
	public Rect getCropRect() {
		return new Rect((int) mCropRect.left, (int) mCropRect.top,
				(int) mCropRect.right, (int) mCropRect.bottom);
	}

	// Maps the cropping rectangle from image space to screen space.
	private Rect computeLayout() {
		RectF r = new RectF(mCropRect.left, mCropRect.top, mCropRect.right,
				mCropRect.bottom);
		mMatrix.mapRect(r);
		return new Rect(Math.round(r.left), Math.round(r.top),
				Math.round(r.right), Math.round(r.bottom));
	}

	public void invalidate() {
		mDrawRect = computeLayout();
	}

	public void setup(Matrix m, Rect imageRect, RectF cropRect, boolean circle,
			boolean maintainAspectRatio) {
		if (circle) {
			maintainAspectRatio = true;
		}
		mMatrix = new Matrix(m);

		mCropRect = cropRect;
		mImageRect = new RectF(imageRect);
		mMaintainAspectRatio = maintainAspectRatio;
		mCircle = circle;

		mInitialAspectRatio = mCropRect.width() / mCropRect.height();
		mDrawRect = computeLayout();

		mFocusPaint.setARGB(125, 50, 50, 50);
		mNoFocusPaint.setARGB(125, 50, 50, 50);
		mOutlinePaint.setColor(outlineColor);
		mOutlinePaint.setStrokeWidth(3F);
		mOutlinePaint.setStyle(Paint.Style.STROKE);
		mOutlinePaint.setAntiAlias(true);

		mMode = ModifyMode.None;
		init();
	}

	public enum ModifyMode {
		None, Move, Grow
	}

	private ModifyMode mMode = ModifyMode.None;

	public Rect mDrawRect; // in screen space
	public RectF mImageRect; // in image space
	public RectF mCropRect; // in image space
	public Matrix mMatrix;

	private boolean mMaintainAspectRatio = false;
	private float mInitialAspectRatio;
	private boolean mCircle = false;

	//对角的图钉
	private Drawable mResizeDrawableDiagonal;

	private final Paint mFocusPaint = new Paint();
	private final Paint mNoFocusPaint = new Paint();
	private final Paint mOutlinePaint = new Paint();
}
