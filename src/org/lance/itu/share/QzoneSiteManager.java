package org.lance.itu.share;

import java.util.ArrayList;

import org.json.JSONObject;
import org.lance.itu.util.NetService;
import org.lance.itu.util.Prefs;
import org.lance.itu.util.SharePrefs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class QzoneSiteManager extends SiteManager {
	public static final String Scope = "all";

	private Context mContext;
	private Handler mHandler;
	public Tencent mTencent;

	private int shareType = QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT;

	public QzoneSiteManager(Context context, Handler handler) {
		mContext = context;
		this.mHandler = handler;
		mTencent = Tencent.createInstance(Prefs.getQzoneId(), context);
		//mQQAuth = QQAuth.createInstance(Prefs.getQzoneId(), context);
	}

	@Override
	public void share(Context context, String content, String file, String lat,
			String lon) {
		final Bundle params = new Bundle();
		params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, shareType);
		params.putString(QQShare.SHARE_TO_QQ_TITLE, "应用分享");
		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,
				"http://www.wallinter.com");
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY, content);
		// 支持传多个imageUrl
		ArrayList<String> imageUrls = new ArrayList<String>();
		imageUrls.add(file);
		params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
		doShareToQzone(params);
	}

	private void doShareToQzone(final Bundle params) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				mTencent.shareToQzone((Activity) mContext, params,
						new IUiListener() {

							@Override
							public void onCancel() {
								System.out.println("onCancel: ");
							}

							@Override
							public void onError(UiError e) {
								System.out.println("onError: " + e.errorMessage
										+ "e");
							}

							@Override
							public void onComplete(Object response) {
								System.out.println("onComplete: "
										+ response.toString());
							}

						});
			}
		}).start();
	}

	@Override
	public void shareResult() {

	}

	@Override// 获取用户信息
	public void getUserInfo(final Context context) {
		new Thread() {
			public void run() {
				try {
					String token = SharePrefs.readAccessToken(context,
							SharePrefs.PREFS_QZONE);
					String openid = SharePrefs.readOpenId(context,
							SharePrefs.PREFS_QZONE);
					String url = "https://graph.qq.com/user/get_user_info?access_token="
							+ token
							+ "&oauth_consumer_key="
							+ Prefs.getQzoneId()
							+ "&openid="
							+ openid
							+ "&format=json";
					String json = NetService.getJsonInfo(url);
					JSONObject obj = new JSONObject(json);
					String name = obj.getString("nickname");
					String imageUrl = obj.getString("figureurl_1");
					SharePrefs.putName(context, name, SharePrefs.PREFS_QZONE);
					SharePrefs.putHeadUrl(context, imageUrl,
							SharePrefs.PREFS_QZONE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	@Override
	public void login() {
		
	}
}
