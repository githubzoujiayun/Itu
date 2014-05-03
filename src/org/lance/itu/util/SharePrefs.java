package org.lance.itu.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 保存分享相关的参数
 * @author lance
 *
 */
public class SharePrefs {
	
	public static final String PREFS_SINA="prefs_sina";
	public static final String PREFS_TENCENT="prefs_tencent";
	public static final String PREFS_QZONE="prefs_qzone";
	//public static final String PREFS_WEIXIN="prefs_weixin";
	//这里是参数文件的细分参数
	private static final String PREFS_TOKEN="prefs_token";
	private static final String PREFS_NAME="prefs_name";
	private static final String PREFS_HEAD_URL="prefs_head_url";
	private static final String PREFS_USER_ID="prefs_user_id";
	private static final String PREFS_EXPIRES_IN="prefs_expires_in";
	private static final String PREFS_OPEN_ID="prefs_open_id";
	/**
	 * 保存token
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
	 * 保存昵称
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

	/** 保存头像的url地址 */
	public static void putHeadUrl(Context context, String headUrl, String prefName) {
		SharedPreferences pref = context.getSharedPreferences(prefName,
				Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(PREFS_HEAD_URL, headUrl);
		editor.commit();
	}
	
	/**
	 * 保存用户id
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
	 * 保存有效期
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
	 * 保存openId
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
	 * 清空参数文件
	 * 
	 * @param context
	 * @param prefName 指定参数文件名
	 */
	public static void clear(Context context, String prefName) {
		SharedPreferences pref = context.getSharedPreferences(prefName,
				Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}

	/**
	 * 读取accessstoken
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
	 * 读取name
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
	
	/** 获取头像的url地址 */
	public static String readHeadUrl(Context context, String prefName) {
		SharedPreferences pref = context.getSharedPreferences(prefName,
				Context.MODE_PRIVATE);
		return pref.getString(PREFS_HEAD_URL, "");
	}
	/**
	 * 读取userId
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
	 * 读取expireIn
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
	 * 读取openId
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