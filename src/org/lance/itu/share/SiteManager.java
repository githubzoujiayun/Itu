package org.lance.itu.share;

import android.content.Context;

public abstract class SiteManager {
	/** 登录 */
	public abstract void login();

	/** 发送分享内容 lat,lon为经纬度位置 */
	public abstract void share(Context context, String content, String file,
			String lat, String lon);

	/** 发送结果处理(包括异常返回错误等) */
	public abstract void shareResult();

	/** 保存用户信息 */
	public abstract void getUserInfo(final Context context);

	/** 根据登录结果刷新本地token */
	public boolean refreshLocalToken() {
		return true;
	}

	/** 根据获取的好友刷新本地好友数据库 */
	public boolean refreshLocalFriends() {
		return true;
	}

}
