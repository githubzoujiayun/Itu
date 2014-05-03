package org.lance.itu.share;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.lance.itu.main.R;
import org.lance.itu.main.ShareActivity;
import org.lance.itu.util.NetService;
import org.lance.itu.util.Prefs;
import org.lance.itu.util.SharePrefs;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;

public class SinaSiteManager extends SiteManager {

	// 获取好友的游标
	int nextCursor = 0;
	// 好友的list
	List<String> friendsList = new ArrayList<String>();
	private Context mContext;
	private Handler mHandler;

	public SinaSiteManager(Context context, Handler handler) {
		this.mContext = context;
		this.mHandler = handler;
	}

	@Override
	public void login() {
		Weibo sinaWeibo = Weibo.getInstance(Prefs.getSinaKey(),
				Prefs.getSinaSecretUrl());
		sinaWeibo.authorize(mContext, new SinaAuthListener());
	}

	private class SinaAuthListener implements WeiboAuthListener {
		@Override
		public void onComplete(Bundle values) {
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			String uid = values.getString("uid");
			Oauth2AccessToken sinaAccessToken = new Oauth2AccessToken(token,
					expires_in);
			SharePrefs.putAccessToken(mContext, token, SharePrefs.PREFS_SINA);
			SharePrefs
					.putExpiresIn(mContext, expires_in, SharePrefs.PREFS_SINA);
			SharePrefs.putUserId(mContext, uid + "", SharePrefs.PREFS_SINA);
			if (sinaAccessToken.isSessionValid()) {
				String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new java.util.Date(sinaAccessToken
								.getExpiresTime()));
				// 根据uid获取用户的昵称
				if (mHandler != null) {
					mHandler.obtainMessage(ShareActivity.SHARE_SUCCESS)
							.sendToTarget();
				} else {
					Toast.makeText(mContext, "认证成功!", Toast.LENGTH_SHORT)
							.show();
				}
				getUserInfo(mContext);
			} else {
				if (mHandler != null) {
					mHandler.obtainMessage(ShareActivity.SHARE_FAILED)
							.sendToTarget();
				} else {
					Toast.makeText(mContext, "认证失败!", Toast.LENGTH_SHORT)
							.show();
				}
			}
		}

		public void onError(WeiboDialogError e) {
			Toast.makeText(mContext, "Auth error : " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

		public void onCancel() {
			Toast.makeText(mContext, "Auth cancel", Toast.LENGTH_LONG).show();
		}

		public void onWeiboException(WeiboException e) {
			Toast.makeText(mContext, "Auth exception : " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void getUserInfo(final Context context) {
		new Thread() {
			public void run() {
				try {
					String uid = SharePrefs.readUserId(context,
							SharePrefs.PREFS_SINA);
					String token = SharePrefs.readAccessToken(context,
							SharePrefs.PREFS_SINA);
					String url = "https://api.weibo.com/2/users/show.json?uid="
							+ uid + "&access_token=" + token;
					String json = NetService.getJsonInfo(url);
					JSONObject obj = new JSONObject(json);
					String name = obj.getString("name");
					String imageUrl = obj.getString("profile_image_url");
					SharePrefs.putName(context, name, SharePrefs.PREFS_SINA);
					SharePrefs.putHeadUrl(context, imageUrl,
							SharePrefs.PREFS_SINA);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	@Override
	// 执行分享 content不能为空否则会分享失败
	public void share(Context context, String content, String file, String lat,
			String lon) {
		String token = SharePrefs.readAccessToken(context,
				SharePrefs.PREFS_SINA);
		String time = SharePrefs.readExpiresIn(context, SharePrefs.PREFS_SINA);
		if (!TextUtils.isEmpty(token)) {
			Oauth2AccessToken sinaAccessTokenfroFriends = new Oauth2AccessToken(
					token, time);
			StatusesAPI statusesAPI = new StatusesAPI(sinaAccessTokenfroFriends);
			// 分享内容到微博
			statusesAPI.upload(content, file, lat, lon,
					new ResultRequestListener());
		}
	}

	@Override
	public void shareResult() {

	}

	/**
	 * 回调 用于反馈操作新浪微博成功与否的信息
	 */
	private class ResultRequestListener implements RequestListener {

		public void onComplete(String e) {
			if (mHandler != null) {
				mHandler.obtainMessage(ShareActivity.SHARE_SUCCESS)
						.sendToTarget();
			}
		}

		public void onError(WeiboException e) {
			if (mHandler != null) {
				Message mesg = mHandler
						.obtainMessage(ShareActivity.SHARE_FAILED);
				mesg.obj = "?:图片不存在;请先截图\n" + e.toString();
				mesg.sendToTarget();
			}
		}

		public void onIOException(IOException e) {
			if (mHandler != null) {
				Message mesg = mHandler
						.obtainMessage(ShareActivity.SHARE_FAILED);
				mesg.obj = "?:图片不存在;请先截图\n" + e.toString();
				mesg.sendToTarget();
			}
		}

	}

}