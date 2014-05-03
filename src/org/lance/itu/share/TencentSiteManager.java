package org.lance.itu.share;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.lance.itu.main.ShareActivity;
import org.lance.itu.util.ActivityForResultUtil;
import org.lance.itu.util.NetService;
import org.lance.itu.util.Prefs;
import org.lance.itu.util.SharePrefs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.oauthv2.OAuthV2Client;
import com.tencent.weibo.webview.OAuthV2AuthorizeWebView;
import com.weibo.sdk.android.WeiboException;

public class TencentSiteManager extends SiteManager {
	private OAuthV2 oAuth;
	private Context mContext;

	// ���ѵ�list
	List<String> friendsList = new ArrayList<String>();
	private Handler mHandler;
	public static final String SEND_WEIBO_SUCESS_RET = "0";

	public TencentSiteManager(Context context, Handler handler) {
		this.mContext = context;
		this.mHandler = handler;
	}

	@Override
	public void login() {
		oAuth = new OAuthV2(Prefs.getTencentSecretUrl());
		oAuth.setClientId(Prefs.getTencentKey());
		oAuth.setClientSecret(Prefs.getTencentSecret());
		// �ر�OAuthV2Client�е�Ĭ�Ͽ�����QHttpClient��
		OAuthV2Client.getQHttpClient().shutdownConnection();
		Intent intent = new Intent(mContext, OAuthV2AuthorizeWebView.class);
		intent.putExtra("oauth", oAuth);// ����Ҳ������WebView������Ȩ����û�м����ص�,ֻ�����ٺ󷵻ؽ��
		((Activity) mContext).startActivityForResult(intent,
				ActivityForResultUtil.TENCENT_REQUEST_CODE);// TODO
	}

	public void onError(WeiboException arg0) {
		arg0.printStackTrace();
	}

	public void onIOException(IOException arg0) {
		arg0.printStackTrace();
	}

	@Override// content������ֵ�����ʧ��
	public void share(Context context, String content, String file, String lat,
			String lon) {
		String token = SharePrefs.readAccessToken(context,
				SharePrefs.PREFS_TENCENT);
		String openid = SharePrefs
				.readOpenId(context, SharePrefs.PREFS_TENCENT);
		OAuthV2 oAuth1 = new OAuthV2(Prefs.getTencentSecretUrl());
		oAuth1.setClientId(Prefs.getTencentKey());
		oAuth1.setClientSecret(Prefs.getTencentSecret());
		oAuth1.setAccessToken(token);
		oAuth1.setOpenid(openid);

		TAPI tAPI = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);

		String response;
		try {
			//{"data":{"id":"382779082960358","time":1394257875},"errcode":0,"imgurl":"http:\/\/t1.qpic.cn\/mblogpic\/13062e06d6b4ae2b2b1e","msg":"ok","ret":0,"seqid":5988291971034465345}
			response = tAPI.addPic(oAuth1, "json", content, "127.0.0.1", file);
			JSONObject jsonObject;
			jsonObject = new JSONObject(response);
			String retcode = jsonObject.getString("ret");
			System.out.println("resp:" + response);
			if (retcode.equals(SEND_WEIBO_SUCESS_RET)) {
				if (mHandler != null) {
					mHandler.obtainMessage(ShareActivity.SHARE_SUCCESS)
							.sendToTarget();
				}
			} else {
				String errcode = jsonObject.getString("errcode");
				String msg = jsonObject.getString("msg");
				if (mHandler != null) {
					Message mesg = mHandler
							.obtainMessage(ShareActivity.SHARE_FAILED);
					mesg.obj = "tencent weibo" + msg + " errcode:" + errcode;
					mesg.sendToTarget();
				}
			}
			tAPI.shutdownConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void shareResult() {

	}

	@Override
	public void getUserInfo(Context context) {
		
	}
	
	public static final int MSG_USER_INFO=0x111;
	public static void getUserInfo(final Context context,final Handler handler) {
		new Thread(){
			public void run(){
				try {
					String openid=SharePrefs.readOpenId(context, SharePrefs.PREFS_TENCENT);
					String token=SharePrefs.readAccessToken(context, SharePrefs.PREFS_TENCENT);
					String appKey=Prefs.getTencentKey();
					String url="https://open.t.qq.com/api/user/info?oauth_consumer_key="+appKey+"&access_token="+token+"&openid="+openid+"&clientip=127.0.0.1&oauth_version=2.a&scope=all";
					String json=NetService.getJsonInfo(url);
					JSONObject obj=new JSONObject(json);
					JSONObject dataObj=obj.getJSONObject("data");
					String headUrl=dataObj.getString("head");
					String name=dataObj.getString("name");
					SharePrefs.putName(context, name, SharePrefs.PREFS_TENCENT);
					SharePrefs.putHeadUrl(context, headUrl, SharePrefs.PREFS_TENCENT);
					System.out.println(SharePrefs.readName(context, SharePrefs.PREFS_TENCENT));
					System.out.println(SharePrefs.readHeadUrl(context, SharePrefs.PREFS_TENCENT));
					if(handler!=null){
						handler.obtainMessage(MSG_USER_INFO).sendToTarget();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}
