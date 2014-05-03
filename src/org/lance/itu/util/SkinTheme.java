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
 * 皮肤主题
 * 
 * @author lance
 * 
 */
public class SkinTheme {

	public Context mThemeContext;
	private Context defaultContext;// 当前应用环境
	private static WeakReference<SkinTheme> skinTheme;
	// 保存弱引用的集合---主要与java的垃圾回收机制有关---后台的Activity可能在某时被回收---资源紧张时
	private final List<WeakReference<ThemeCallback>> mCallbacks = new ArrayList<WeakReference<ThemeCallback>>();

	private SkinTheme(Context context) {// 使用默认已经保存的主题
		initTheme(context, Prefs.getSkinThemePack(context));
	}

	private void initTheme(Context context, String theme) {
		this.defaultContext = context;
		this.mThemeContext = getThemeContext(context, theme);
	}

	// 返回唯一实例
	public static final SkinTheme getInstance(Context context) {
		if ((skinTheme == null) || (skinTheme.get() == null)) {
			skinTheme = new WeakReference(new SkinTheme(context));
		}
		return skinTheme.get();
	}

	/** 通过包名获取指定应用的上下文环境 */
	public Context getThemeContext(Context context, String packName) {
		Context skinCtx = null;
		try {
			skinCtx = context.createPackageContext(packName, 0);
			Prefs.putSkinThemePack(context, packName);// 如果能获取到应用上下文,保存主题包
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			Prefs.putSkinThemePack(context, context.getPackageName());// 保存当前主题包
			return context;
		}
		return skinCtx;
	}

	// 改变主题
	public void changeTheme(Context context, String packName) {
		synchronized (this.mCallbacks) {
			initTheme(context, packName);
			for (int i = 0; i < mCallbacks.size(); i++) {
				WeakReference<ThemeCallback> refer = this.mCallbacks.get(i);
				if (refer != null) {
					ThemeCallback callback = refer.get();
					if (callback != null) {
						// 循环来改变所有页面的主题
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
	 * 取消注册主题回调---activity销毁时可以取消主题应用---让其失去引用利于回收
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

	// 返回当前使用的主题上下文的资源
	public Resources getResources() {
		return mThemeContext.getResources();
	}

	// 获取主题包中的资源id
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

	// 返回图片
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

	// 返回颜色值
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

	// 返回多重颜色列表
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

	// 返回尺寸
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

	// 返回字符串
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

	// 返回字符数组
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

	// 发返回整形
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

	// 返回布局解析器
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

	// 返回动画解析器
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

	/** 为ImageView设置源位图 */
	public void setImageViewSrc(ImageView image, int resid) {
		image.setImageDrawable(getDrawable(resid));
	}

	/** 为ListView设置分隔条 */
	public void setListViewDivider(Activity act, int layId, int resid) {
		ListView listView = (ListView) act.findViewById(layId);
		if (listView != null) {
			setListViewDivider(listView, resid);
		}
	}

	/** 设置分隔条 */
	public void setListViewDivider(ListView listView, int resid) {
		listView.setDivider(getDrawable(resid));
	}

	/** 为ListView设置背景选择器 */
	public void setListViewSelector(Activity act, int layId, int resid) {
		ListView listView = (ListView) act.findViewById(layId);
		if (listView != null) {
			setListViewSelector(listView, resid);
		}
	}

	/** 设置背景选择器 */
	public void setListViewSelector(ListView listView, int resid) {
		listView.setSelector(getDrawable(resid));
	}

	/** 设置字体颜色 */
	public void setTextViewColor(Activity act, int layId, int resid) {
		TextView textView = (TextView) act.findViewById(layId);
		if (textView == null) {
			setTextViewColor(textView, resid);
		}
	}

	/** 设置字体颜色 */
	public void setTextViewColor(TextView textView, int resid) {
		textView.setTextColor(getColor(resid));
	}

	/** 设置背景--使用drawable对象 */
	public void setViewBackgroudDrawable(Activity act, int layId, int resid) {
		View view = act.findViewById(layId);
		if (view != null) {
			setViewBackgroudDrawable(view, resid);
		}
	}

	/** 设置背景 */
	public void setViewBackgroudDrawable(View view, int resid) {
		view.setBackgroundDrawable(getDrawable(resid));
	}

	/** 为控件设置背景色 */
	public void setViewIdBackgroudColor(Activity act, int layId, int resid) {
		View view = act.findViewById(layId);
		if (view != null) {
			setViewBackgroudColor(view, resid);
		}
	}

	/** 为控件设置背景色 */
	public void setViewBackgroudColor(View view, int resid) {
		view.setBackgroundColor(getColor(resid));
	}

	/** 为窗口设置背景---窗口只支持图片--不支持颜色 */
	public void setWindowBackgroud(Activity act, int resid) {
		act.getWindow().setBackgroundDrawable(getDrawable(resid));
	}

	public static interface ThemeCallback {
		public void applyTheme();
	}
}
