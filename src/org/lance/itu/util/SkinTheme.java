package org.lance.itu.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Ƥ������
 * 
 * @author lance
 * 
 */
public class SkinTheme {

	public Context mThemeContext;
	private Context defaultContext;// ��ǰӦ�û���
	private static WeakReference<SkinTheme> skinTheme;
	// ���������õļ���---��Ҫ��java���������ջ����й�---��̨��Activity������ĳʱ������---��Դ����ʱ
	private final List<WeakReference<ThemeCallback>> mCallbacks = new ArrayList<WeakReference<ThemeCallback>>();

	private SkinTheme(Context context) {// ʹ��Ĭ���Ѿ����������
		initTheme(context, Prefs.getSkinThemePack(context));
	}

	private void initTheme(Context context, String theme) {
		this.defaultContext = context;
		this.mThemeContext = getThemeContext(context, theme);
	}

	// ����Ψһʵ��
	public static final SkinTheme getInstance(Context context) {
		if ((skinTheme == null) || (skinTheme.get() == null)) {
			skinTheme = new WeakReference(new SkinTheme(context));
		}
		return skinTheme.get();
	}

	/** ͨ��������ȡָ��Ӧ�õ������Ļ��� */
	public Context getThemeContext(Context context, String packName) {
		Context skinCtx = null;
		try {
			skinCtx = context.createPackageContext(packName, 0);
			Prefs.putSkinThemePack(context, packName);// ����ܻ�ȡ��Ӧ��������,���������
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			Prefs.putSkinThemePack(context, context.getPackageName());// ���浱ǰ�����
			return context;
		}
		return skinCtx;
	}

	// �ı�����
	public void changeTheme(Context context, String packName) {
		synchronized (this.mCallbacks) {
			initTheme(context, packName);
			for (int i = 0; i < mCallbacks.size(); i++) {
				WeakReference<ThemeCallback> refer = this.mCallbacks.get(i);
				if (refer != null) {
					ThemeCallback callback = refer.get();
					if (callback != null) {
						// ѭ�����ı�����ҳ�������
						callback.applyTheme();
					}
				}
			}
		}
	}

	public void registerSkinCallback(ThemeCallback callback) {
		mCallbacks.add(new WeakReference<ThemeCallback>(callback));
		callback.applyTheme();
	}

	/**
	 * ȡ��ע������ص�---activity����ʱ����ȡ������Ӧ��---����ʧȥ�������ڻ���
	 * 
	 * @param callback
	 */
	public void unRegisterSkinCallback(ThemeCallback callback) {
		ThemeCallback temp = null;
		for (int i = 0; i < mCallbacks.size(); i++) {
			temp = mCallbacks.get(i).get();
			if ((temp != null) && temp.equals(callback)) {
				mCallbacks.remove(i);
				return;
			}
		}
	}

	// ���ص�ǰʹ�õ����������ĵ���Դ
	public Resources getResources() {
		return mThemeContext.getResources();
	}

	// ��ȡ������е���Դid
	public int getThemeId(int resid) {
		try {
			String resFullName = defaultContext.getResources().getResourceName(
					resid);
			String[] srr = resFullName.split(":")[1].split("/");
			String type = srr[0];
			String resName = srr[1];
			int themeid = getResources().getIdentifier(resName, type,
					Prefs.getSkinThemePack(defaultContext));
			return themeid;
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		return resid;
	}

	// ����ͼƬ
	public Drawable getDrawable(int resid) {
		try {
			if (!defaultContext.equals(mThemeContext)) {
				return getResources().getDrawable(getThemeId(resid));
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		return defaultContext.getResources().getDrawable(resid);
	}

	// ������ɫֵ
	public int getColor(int resid) {
		try {
			if (!defaultContext.equals(mThemeContext)) {
				return getResources().getColor(getThemeId(resid));
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		return defaultContext.getResources().getColor(resid);
	}

	// ���ض�����ɫ�б�
	public ColorStateList getColorStateList(int resid) {
		try {
			if (!defaultContext.equals(mThemeContext)) {
				return getResources().getColorStateList(getThemeId(resid));
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		return defaultContext.getResources().getColorStateList(resid);
	}

	// ���سߴ�
	public float getDimension(int resid) {
		try {
			if (!defaultContext.equals(mThemeContext)) {
				return getResources().getDimension(getThemeId(resid));
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		return defaultContext.getResources().getDimension(resid);
	}

	// �����ַ���
	public String getString(int resid) {
		try {
			if (!defaultContext.equals(mThemeContext)) {
				return getResources().getString(getThemeId(resid));
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		return defaultContext.getResources().getString(resid);
	}

	// �����ַ�����
	public String[] getStringArray(int resid) {
		try {
			if (!defaultContext.equals(mThemeContext)) {
				return getResources().getStringArray(getThemeId(resid));
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		return defaultContext.getResources().getStringArray(resid);
	}

	// ����������
	public float getInteger(int resid) {
		try {
			if (!defaultContext.equals(mThemeContext)) {
				return getResources().getInteger(getThemeId(resid));
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		return defaultContext.getResources().getInteger(resid);
	}

	public int[] getIntArray(int resid) {
		try {
			if (!defaultContext.equals(mThemeContext)) {
				return getResources().getIntArray(getThemeId(resid));
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		return defaultContext.getResources().getIntArray(resid);
	}

	public boolean getBoolean(int resid) {
		try {
			if (!defaultContext.equals(mThemeContext)) {
				return getResources().getBoolean(getThemeId(resid));
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		return defaultContext.getResources().getBoolean(resid);
	}

	// ���ز��ֽ�����
	public XmlResourceParser getLayoutParser(int resid) {
		try {
			if (!defaultContext.equals(mThemeContext)) {
				return getResources().getLayout(getThemeId(resid));
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		return defaultContext.getResources().getLayout(resid);
	}

	// ���ض���������
	public XmlResourceParser getAnimationParser(int resid) {
		try {
			if (!defaultContext.equals(mThemeContext)) {
				return getResources().getAnimation(getThemeId(resid));
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		return defaultContext.getResources().getAnimation(resid);
	}

	/** ΪImageView����Դλͼ */
	public void setImageViewSrc(ImageView image, int resid) {
		image.setImageDrawable(getDrawable(resid));
	}

	/** ΪListView���÷ָ��� */
	public void setListViewDivider(Activity act, int layId, int resid) {
		ListView listView = (ListView) act.findViewById(layId);
		if (listView != null) {
			setListViewDivider(listView, resid);
		}
	}

	/** ���÷ָ��� */
	public void setListViewDivider(ListView listView, int resid) {
		listView.setDivider(getDrawable(resid));
	}

	/** ΪListView���ñ���ѡ���� */
	public void setListViewSelector(Activity act, int layId, int resid) {
		ListView listView = (ListView) act.findViewById(layId);
		if (listView != null) {
			setListViewSelector(listView, resid);
		}
	}

	/** ���ñ���ѡ���� */
	public void setListViewSelector(ListView listView, int resid) {
		listView.setSelector(getDrawable(resid));
	}

	/** ����������ɫ */
	public void setTextViewColor(Activity act, int layId, int resid) {
		TextView textView = (TextView) act.findViewById(layId);
		if (textView == null) {
			setTextViewColor(textView, resid);
		}
	}

	/** ����������ɫ */
	public void setTextViewColor(TextView textView, int resid) {
		textView.setTextColor(getColor(resid));
	}

	/** ���ñ���--ʹ��drawable���� */
	public void setViewBackgroudDrawable(Activity act, int layId, int resid) {
		View view = act.findViewById(layId);
		if (view != null) {
			setViewBackgroudDrawable(view, resid);
		}
	}

	/** ���ñ��� */
	public void setViewBackgroudDrawable(View view, int resid) {
		view.setBackgroundDrawable(getDrawable(resid));
	}

	/** Ϊ�ؼ����ñ���ɫ */
	public void setViewIdBackgroudColor(Activity act, int layId, int resid) {
		View view = act.findViewById(layId);
		if (view != null) {
			setViewBackgroudColor(view, resid);
		}
	}

	/** Ϊ�ؼ����ñ���ɫ */
	public void setViewBackgroudColor(View view, int resid) {
		view.setBackgroundColor(getColor(resid));
	}

	/** Ϊ�������ñ���---����ֻ֧��ͼƬ--��֧����ɫ */
	public void setWindowBackgroud(Activity act, int resid) {
		act.getWindow().setBackgroundDrawable(getDrawable(resid));
	}

	public static interface ThemeCallback {
		public void applyTheme();
	}
}
