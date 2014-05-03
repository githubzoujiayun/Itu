package org.lance.itu.util;

import java.io.InputStream;
import java.util.Properties;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;

/**
 * ��ȡָ������ǽֽ��Ӧ��������Ϣ
 * 
 * @author lance
 * 
 */
public class Prefs {
	private static final String PREFER_NAME = "itu";

	private static Properties env = new Properties();
	static {
		try {
			// ���·�����������
			InputStream is = Prefs.class.getResourceAsStream("/itu.properties");
			env.load(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ���浱ǰƤ�������---��ɾ�����ָ�Ϊ�Դ�����
	private static final String SKIN_THEME_PACK = "skin_theme_pack";
	//��ȡ����������ļ�
	private static final String RECENTLY_OPERA_FILE="recently_opera_file";

	/** ��ȡsd����·�� */
	public static String getSDRoot() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	/** ��ȡ���Ŀ¼ */
	public static String getDCIMPath() {
		return getSDRoot() + "/DCIM/Camera/";
	}

	/** ��ȡͼƬ����Ŀ¼ */
	public static String getImageFilterPath() {
		return getSDRoot() + "/itu/Images/";
	}
	
	/** ��ȡ¼��Ŀ¼Ŀ¼ */
	public static String getRecordPath() {
		return getSDRoot() + "/itu/Records/";
	}

	/** ��������������ļ�·�� */
	public static boolean putRecentlyOperaFile(Context context, String fileName) {
		try {
			SharedPreferences prefer = context.getSharedPreferences(
					PREFER_NAME, Context.MODE_PRIVATE);
			Editor editor = prefer.edit();
			editor.putString(RECENTLY_OPERA_FILE, fileName);
			editor.commit();
			prefer = null;
			editor = null;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/** ��ȡ����������ļ�·�� */
	public static String getRecentlyOperaFile(Context context) {
		try {
			SharedPreferences prefer = context.getSharedPreferences(
					PREFER_NAME, Context.MODE_PRIVATE);
			String result = prefer.getString(RECENTLY_OPERA_FILE, "");
			prefer = null;
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/** ��ȡ��ǰ���������� */
	public static String getSkinThemePack(Context context) {
		try {
			SharedPreferences prefer = context.getSharedPreferences(
					PREFER_NAME, Context.MODE_PRIVATE);
			String result = prefer.getString(SKIN_THEME_PACK,
					context.getPackageName());
			prefer = null;
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return context.getPackageName();
	}

	/** �������õ������ */
	public static boolean putSkinThemePack(Context context, String packName) {
		try {
			SharedPreferences prefer = context.getSharedPreferences(
					PREFER_NAME, Context.MODE_PRIVATE);
			Editor editor = prefer.edit();
			editor.putString(SKIN_THEME_PACK, packName);
			editor.commit();
			prefer = null;
			editor = null;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static String getBaiduMapKey(){
		return env.getProperty("baidumap_key");
	}

	// TODO ����
	private static final String SINA_KEY = "sina_key";
	private static final String SINA_SECRET_URL = "sina_secret_url";

	private static final String TENCENT_KEY = "tencent_key";
	private static final String TENCENT_SECRET = "tencent_secret";
	private static final String TENCENT_SECRET_URL = "tencent_secret_url";

	private static final String QZONE_ID = "qzone_id";

	private static final String WEIXIN_ID = "weixin_id";

	public static String getSinaKey() {
		return env.getProperty(SINA_KEY);
	}

	public static String getSinaSecretUrl() {
		return env.getProperty(SINA_SECRET_URL);
	}

	public static String getTencentKey() {
		return env.getProperty(TENCENT_KEY);
	}

	public static String getTencentSecret() {
		return env.getProperty(TENCENT_SECRET);
	}

	public static String getTencentSecretUrl() {
		return env.getProperty(TENCENT_SECRET_URL);
	}

	public static String getQzoneId() {
		return env.getProperty(QZONE_ID);
	}

	public static String getWeixinId() {
		return env.getProperty(WEIXIN_ID);
	}
}
