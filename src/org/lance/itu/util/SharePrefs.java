package org.lance.itu.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * ���������صĲ���
 * @author lance
 *
 */
public class SharePrefs {
	
	public static final String PREFS_SINA="prefs_sina";
	public static final String PREFS_TENCENT="prefs_tencent";
	public static final String PREFS_QZONE="prefs_qzone";
	//public static final String PREFS_WEIXIN="prefs_weixin";
	//�����ǲ����ļ���ϸ�ֲ���
	private static final String PREFS_TOKEN="prefs_token";
	private static final String PREFS_NAME="prefs_name";
	private static final String PREFS_HEAD_URL="prefs_head_url";
	private static final String PREFS_USER_ID="prefs_user_id";
	private static final String PREFS_EXPIRES_IN="prefs_expires_in";
	private static final String PREFS_OPEN_ID="prefs_open_id";
	/**
	 * ����token
	 * @param context
	 * @param token
	 * @param prefName
	 */
	public static void putAccessToken(Context context, String token,
			String prefName) {
		SharedPreferences pref = context.getSharedPreferences(prefName,
				Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(PREFS_TOKEN, token);
		editor.commit();
	}

	/**
	 * �����ǳ�
	 * 
	 * @param context
	 * @param name
	 * @param prefName
	 */
	public static void putName(Context context, String name, String prefName) {
		SharedPreferences pref = context.getSharedPreferences(prefName,
				Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(PREFS_NAME, name);
		editor.commit();
	}

	/** ����ͷ���url��ַ */
	public static void putHeadUrl(Context context, String headUrl, String prefName) {
		SharedPreferences pref = context.getSharedPreferences(prefName,
				Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(PREFS_HEAD_URL, headUrl);
		editor.commit();
	}
	
	/**
	 * �����û�id
	 * 
	 * @param context
	 * @param userId
	 * @param prefName
	 */
	public static void putUserId(Context context, String userId, String prefName) {
		SharedPreferences pref = context.getSharedPreferences(prefName,
				Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(PREFS_USER_ID, userId);
		editor.commit();
	}

	/**
	 * ������Ч��
	 * 
	 * @param context
	 * @param token
	 * @param prefName
	 */
	public static void putExpiresIn(Context context, String expiresIn,
			String prefName) {
		SharedPreferences pref = context.getSharedPreferences(prefName,
				Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(PREFS_EXPIRES_IN, expiresIn);
		editor.commit();
	}

	/**
	 * ����openId
	 * 
	 * @param context
	 * @param token
	 * @param prefName
	 */
	public static void putOpenId(Context context, String openId,
			String prefName) {
		SharedPreferences pref = context.getSharedPreferences(prefName,
				Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(PREFS_OPEN_ID, openId);
		editor.commit();
	}

	/**
	 * ��ղ����ļ�
	 * 
	 * @param context
	 * @param prefName ָ�������ļ���
	 */
	public static void clear(Context context, String prefName) {
		SharedPreferences pref = context.getSharedPreferences(prefName,
				Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}

	/**
	 * ��ȡaccessstoken
	 * 
	 * @param context
	 * @param prefName
	 * @return String
	 */
	public static String readAccessToken(Context context, String prefName) {
		SharedPreferences pref = context.getSharedPreferences(prefName,
				Context.MODE_PRIVATE);
		return pref.getString(PREFS_TOKEN, "");
	}

	/**
	 * ��ȡname
	 * 
	 * @param context
	 * @param prefName
	 * @return String
	 */
	public static String readName(Context context, String prefName) {
		SharedPreferences pref = context.getSharedPreferences(prefName,
				Context.MODE_PRIVATE);
		return pref.getString(PREFS_NAME, "");
	}
	
	/** ��ȡͷ���url��ַ */
	public static String readHeadUrl(Context context, String prefName) {
		SharedPreferences pref = context.getSharedPreferences(prefName,
				Context.MODE_PRIVATE);
		return pref.getString(PREFS_HEAD_URL, "");
	}
	/**
	 * ��ȡuserId
	 * 
	 * @param context
	 * @param prefName
	 * @return Long
	 */
	public static String readUserId(Context context, String prefName) {
		SharedPreferences pref = context.getSharedPreferences(prefName,
				Context.MODE_PRIVATE);
		return pref.getString(PREFS_USER_ID,"");
	}

	/**
	 * ��ȡexpireIn
	 * 
	 * @param context
	 * @param prefName
	 * @return String
	 */
	public static String readExpiresIn(Context context, String prefName) {
		SharedPreferences pref = context.getSharedPreferences(prefName,
				Context.MODE_PRIVATE);
		return pref.getString(PREFS_EXPIRES_IN, "");
	}

	/**
	 * ��ȡopenId
	 * 
	 * @param context
	 * @param prefName
	 * @return String
	 */
	public static String readOpenId(Context context, String prefName) {
		SharedPreferences pref = context.getSharedPreferences(prefName,
				Context.MODE_PRIVATE);
		return pref.getString(PREFS_OPEN_ID, "");
	}
	
}