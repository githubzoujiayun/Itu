package org.lance.itu.anim;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

/**
 * 3d��������תԭ��---ʹ��һ���������Χ��Ŀ�����㣻 Ȼ�󽫹��̼�¼��Matrix�����У� ֮��ֱ�Ӳ������Matrix������ʵ��3d��Ч
 * 
 * @author lance
 * 
 */
public class Rotate3DAnimation extends Animation {
	// Ĭ����ʼ��ת�Ƕ�---from
	public static final float PRE_FROMDEGREE = 0f;
	// Ĭ����ʼ��ת�Ƕ�---to
	public static final float PRE_TODEGREE = 90f;
	// Ĭ�Ͻ��淭ת�Ƕ�---from
	public static final float NEXT_FROMDEGREE = -90f;
	// Ĭ�Ͻ��淭ת�Ƕ�---to
	public static final float NEXT_TODEGREE = 0f;
	// Ĭ�Ϸ�תʱ��
	public static final long DURATION = 500L;
	// Ĭ�����
	public static final float DEPTHZ = 500f;
	// Ĭ�ϱ��ַ�ת���״̬
	public static final boolean FILLAFTER = true;
	// Ĭ�ϼ��ٲ�ֵ��
	public static final Interpolator INTERPOLATOR = new AccelerateInterpolator();
	// Ĭ�ϼ��ٲ�ֵ��
	public static final Interpolator NEXTINTERPOLATOR = new DecelerateInterpolator();
	// �������
	private Camera mCamera;
	// X��������
	private float mCenterX;
	// Y��������
	private float mCenterY;
	// ����ת�����
	private float mDepthZ;
	// ��ʼ�Ƕ�
	private float mFromDegrees;
	// �����Ƕ�
	private float mToDegrees;
	// �Ƿ�������ʾ����
	private boolean mReverse;
	// ��תģʽ---Ĭ����Y����ת
	private Model model = Model.ROTATE_Y;

	public static enum Model {
		ROTATE_X, ROTATE_Y
	}

	public Rotate3DAnimation(float fromDegrees, float toDegrees, float centerX,
			float centerY, float depthz, boolean bool, Model model,
			OnSwapListener swapListener) {
		init(fromDegrees, toDegrees, centerX, centerY, depthz, bool,
				swapListener);
		this.model = model;
	}

	public Rotate3DAnimation(float fromDegrees, float toDegrees, float centerX,
			float centerY, float depthz, boolean bool,
			OnSwapListener swapListener) {
		init(fromDegrees, toDegrees, centerX, centerY, depthz, bool,
				swapListener);
	}

	private void init(float fromDegrees, float toDegrees, float centerX,
			float centerY, float depthz, boolean bool,
			OnSwapListener swapListener) {
		this.mFromDegrees = fromDegrees;
		this.mToDegrees = toDegrees;
		this.mCenterX = centerX;
		this.mCenterY = centerY;
		this.mDepthZ = depthz;
		this.mReverse = bool;
	}

	// 3d����ǰ�ĳ�ʼ������
	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		// �������
		super.initialize(width, height, parentWidth, parentHeight);
		// ��ʼ������ͷ
		mCamera = new Camera();
	}

	// �ڳ�ʼ���������ú󱻵���
	@Override
	protected void applyTransformation(float time, Transformation tran) {
		// time��һ��ϵͳ����ʱ�䣻ʱ������С�����ϵͳĬ�ϵ���תʱ�䶼�ܶ�
		Matrix matrix = tran.getMatrix();
		// �µ�ת��
		float newDegrees = mFromDegrees + time * (mToDegrees - mFromDegrees);
		mCamera.save();// ��תǰ���浱ǰ����
		if (!this.mReverse) {
			mCamera.translate(0F, 0F, this.mDepthZ * (1F - time));
		} else {
			mCamera.translate(0F, 0F, time * this.mDepthZ);
		}
		if (model == Model.ROTATE_X) {
			// ����X����ת�¼���ĽǶ�
			mCamera.rotateX(newDegrees);
		} else {
			// ����Y����ת�¼���ĽǶ�
			mCamera.rotateY(newDegrees);
		}
		// ���ݾ�����󵽵ײ㣻��õײ�matrix��ʵ��
		mCamera.getMatrix(matrix);
		// ������ָ�ԭ״
		mCamera.restore();
		// ����Matrix����׼��ƽ��
		matrix.preTranslate(-mCenterX, -mCenterY);
		// ����ƽ��
		matrix.postTranslate(mCenterX, mCenterY);
	}

	/**
	 * ����Ĭ�ϵ���ʼ��ת�Ķ���
	 * 
	 * @param container
	 * @return
	 */
	public static Rotate3DAnimation getDefaultPreAnim(ViewGroup container,
			Model model, final OnSwapListener swapListener) {
		Rotate3DAnimation d3 = new Rotate3DAnimation(PRE_FROMDEGREE,
				PRE_TODEGREE, container.getWidth() / 2f,
				container.getHeight() / 2f, DEPTHZ, true, model, swapListener);
		d3.setAnimationListener(new NextView(container, false, model,
				swapListener));
		d3.setDuration(DURATION);
		d3.setFillAfter(FILLAFTER);
		d3.setInterpolator(INTERPOLATOR);
		return d3;
	}

	/**
	 * ������ʼ��ת�Ķ���
	 * 
	 * @param container
	 * @return
	 */
	public static Rotate3DAnimation getPreAnim(ViewGroup container,
			Model model, final OnSwapListener swapListener) {
		Rotate3DAnimation d3 = new Rotate3DAnimation(PRE_FROMDEGREE,
				-PRE_TODEGREE, container.getWidth() / 2f,
				container.getHeight() / 2f, DEPTHZ, true, model, swapListener);
		d3.setAnimationListener(new NextView(container, true, model,
				swapListener));
		d3.setDuration(DURATION);
		d3.setFillAfter(FILLAFTER);
		d3.setInterpolator(INTERPOLATOR);
		return d3;
	}

	/**
	 * ����Ĭ�ϵĽ�����ת����
	 * 
	 * @param container
	 * @return
	 */
	public static Rotate3DAnimation getDefaultNextAnim(ViewGroup container,
			Model model, final OnSwapListener swapListener) {
		Rotate3DAnimation d3 = new Rotate3DAnimation(NEXT_FROMDEGREE,
				NEXT_TODEGREE, container.getWidth() / 2f,
				container.getHeight() / 2f, DEPTHZ, false, model, swapListener);
		d3.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				swapListener.onSwapFinished(true);
			}
		});
		d3.setDuration(DURATION);
		d3.setFillAfter(FILLAFTER);
		d3.setInterpolator(NEXTINTERPOLATOR);
		return d3;
	}

	/**
	 * ���ؽ�����ת����
	 * 
	 * @param container
	 * @return
	 */
	public static Rotate3DAnimation getNextAnim(ViewGroup container,
			Model model, final OnSwapListener swapListener) {
		Rotate3DAnimation d3 = new Rotate3DAnimation(-NEXT_FROMDEGREE,
				NEXT_TODEGREE, container.getWidth() / 2f,
				container.getHeight() / 2f, DEPTHZ, false, model, swapListener);
		d3.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				swapListener.onSwapFinished(false);
			}
		});
		d3.setDuration(DURATION);
		d3.setFillAfter(FILLAFTER);
		d3.setInterpolator(NEXTINTERPOLATOR);
		return d3;
	}

	// ִ����ת�ļ�����
	private static class NextView implements Animation.AnimationListener {
		private ViewGroup container;
		private boolean flag;// �ж�ʹ����һ�ֶ���
		private Model swapModel;
		private OnSwapListener swapListener;

		public NextView(ViewGroup container, boolean flag, Model model,
				OnSwapListener swapListener) {
			this.container = container;
			this.flag = flag;
			this.swapModel = model;
			this.swapListener = swapListener;
		}

		public void onAnimationEnd(Animation animation) {
			swapListener.onSwapFinished(false);
			container.post(new SwapView(container, flag, swapModel,
					swapListener));
		}

		public void onAnimationStart(Animation animation) {
		}

		public void onAnimationRepeat(Animation animation) {
		}
	}

	private static class SwapView implements Runnable {
		private ViewGroup container;
		private boolean flag;
		private Model swapModel;
		private OnSwapListener swapListener;

		public SwapView(ViewGroup container, boolean flag, Model model,
				OnSwapListener swapListener) {
			this.container = container;
			this.flag = flag;
			this.swapModel = model;
			this.swapListener = swapListener;
		}

		// �������Ĳ�����SOURCE--->��ʾԭͼ �������Ĳ�����EFFECT---->��ʾЧ��ͼ
		public void run() {
			if (swapListener != null) {
				swapListener.onSwap(flag);
			}
			Rotate3DAnimation d3 = null;
			if (flag) {
				d3 = Rotate3DAnimation.getNextAnim(container, swapModel,
						swapListener);
			} else {
				d3 = Rotate3DAnimation.getDefaultNextAnim(container, swapModel,
						swapListener);
			}
			container.startAnimation(d3);
		}
	}

	// �����������
	public interface OnSwapListener {
		/** �Ƿ�ת */
		public void onSwap(boolean flag);

		/** ��ת���� true �����Ƿ��� */
		public void onSwapFinished(boolean isNext);
	}

}