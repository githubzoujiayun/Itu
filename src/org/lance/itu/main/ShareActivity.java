package org.lance.itu.main;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import org.lance.itu.share.QzoneSiteManager;
import org.lance.itu.share.SinaSiteManager;
import org.lance.itu.share.SiteManager;
import org.lance.itu.share.TencentSiteManager;
import org.lance.itu.share.WeixinChat;
import org.lance.itu.util.Constants;
import org.lance.itu.util.Prefs;
import org.lance.itu.util.SharePrefs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.webview.OAuthV2AuthorizeWebView;

/**
 * 分享页面
 * 
 * @author lance
 * 
 */
public class ShareActivity extends BaseActivity implements OnClickListener {

	public static final int SHARE_SUCCESS = 0x1;// 分享成功
	public static final int SHARE_FAILED = 0x2;// 分享失败
	public static final int ACCOUNT_ICON = 0x3;

	public static final int TENCENT_REQUEST_CODE = 0x10;
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHARE_SUCCESS:
				showHint("分享成功!");
				finish();
				break;
			case SHARE_FAILED:
				showHint("分享失败!" + (String) msg.obj);
				break;
			case TencentSiteManager.MSG_USER_INFO:// 这里已经获取到了用户信息需要显示头像
				showUserInfo(shareType);
				break;
			case ACCOUNT_ICON:
				Bitmap bmp = (Bitmap) msg.obj;
				accountIcon.setImageBitmap(bmp);
				break;
			}
		}
	};
	private int shareType;
	// 需要分享的图片
	private ImageView sharePic;
	
	private Button share;
	private EditText shareTitle;
	private EditText shareContent;
	private ImageView accountIcon;
	private TextView accountName;
	private TextView pageTitle;
	// 切换账户按钮
	private TextView switchAccount;

	protected void onCreate(Bundle saved) {
		super.onCreate(saved);
		setContentView(R.layout.do_share_page);
		((TextView) findViewById(R.id.top_bar_title)).setText(getTitle());
		sharePic = (ImageView) findViewById(R.id.share_image);
		Bitmap bmp = BitmapFactory.decodeFile(Prefs.getRecentlyOperaFile(this));
		if (bmp != null) {
			sharePic.setImageBitmap(bmp);
		} else {// 没有截图使用默认的图标
			showHint("文件不存在~");
			sharePic.setImageResource(R.drawable.app_icon);
			try {
				String fileName = Environment.getExternalStorageDirectory()
						.getPath() + "/app_icon.png";
				File file = new File(fileName);
				if (!file.exists()) {
					file.createNewFile();
				}
				FileOutputStream out = new FileOutputStream(file);
				Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.app_icon);
				bitmap.compress(CompressFormat.PNG, 100, out);
				Prefs.putRecentlyOperaFile(this, fileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		share = (Button) findViewById(R.id.share_button);
		shareTitle = (EditText) findViewById(R.id.share_title);
		shareContent = (EditText) findViewById(R.id.share_content);
		accountIcon = (ImageView) findViewById(R.id.account_icon);
		accountName = (TextView) findViewById(R.id.account_name);
		switchAccount = (TextView) findViewById(R.id.btn_right);
		pageTitle = (TextView) findViewById(R.id.page_title);
		share.setOnClickListener(this);
		switchAccount.setOnClickListener(this);
		shareType = getIntent().getIntExtra(Constants.CHAIN_SHARE_TYPE, 0);
		checkIcon(shareType);
	}
	
	private void checkIcon(int shareType){
		switch(shareType){
		case Constants.SHARE_SINA:
			accountIcon.setImageResource(R.drawable.sina_weibo_normal);
			break;
		case Constants.SHARE_TENCENT:
			accountIcon.setImageResource(R.drawable.tencent_weibo_normal);
			break;
		case Constants.SHARE_QZONE:
			accountIcon.setImageResource(R.drawable.qzone_normal);
			switchAccount.setVisibility(View.INVISIBLE);
			break;
		case Constants.SHARE_FRIEND:
			accountIcon.setImageResource(R.drawable.wechat_friend_normal);
			switchAccount.setVisibility(View.INVISIBLE);
			break;
		case Constants.SHARE_EMAIL:
			accountIcon.setImageResource(R.drawable.email_share_normal);
			switchAccount.setVisibility(View.INVISIBLE);
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		showUserInfo(shareType);
	}

	/** 显示账户信息 */
	private void showUserInfo(int shareType) {
		String account = "";
		String head = "";
		switch (shareType) {
		case Constants.SHARE_SINA:
			account = SharePrefs.readName(ShareActivity.this,
					SharePrefs.PREFS_SINA);
			head = SharePrefs.readHeadUrl(ShareActivity.this,
					SharePrefs.PREFS_SINA);
			break;
		case Constants.SHARE_TENCENT:
			account = SharePrefs.readName(ShareActivity.this,
					SharePrefs.PREFS_TENCENT);
			head = SharePrefs.readHeadUrl(ShareActivity.this,
					SharePrefs.PREFS_TENCENT);
			break;
		case Constants.SHARE_QZONE:
			account = SharePrefs.readName(ShareActivity.this,
					SharePrefs.PREFS_QZONE);
			head = SharePrefs.readHeadUrl(ShareActivity.this,
					SharePrefs.PREFS_QZONE);
			break;
		}
		accountName.setText(account);
		final String iconurl = head;
		execTask(new Thread() {//获取账户图片
			public void run() {
				try {
					Bitmap bmp = BitmapFactory.decodeStream(new URL(iconurl)
							.openStream());
					if (bmp != null) {
						Message msg = handler.obtainMessage(ACCOUNT_ICON);
						msg.obj = bmp;
						msg.sendToTarget();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case Constants.TENCENT_REQUEST_CODE:
			if (resultCode == OAuthV2AuthorizeWebView.RESULT_CODE) {
				OAuthV2 oAuth = (OAuthV2) data.getExtras().getSerializable(
						"oauth");
				System.out.println("auth:" + oAuth.getStatus());
				if (oAuth.getStatus() == 0) {
					SharePrefs.putAccessToken(this, oAuth.getAccessToken(),
							SharePrefs.PREFS_TENCENT);
					SharePrefs.putOpenId(this, oAuth.getOpenid(),
							SharePrefs.PREFS_TENCENT);
					SharePrefs.putUserId(this, oAuth.getClientId(),
							SharePrefs.PREFS_TENCENT);
					showHint("认证成功!");
					TencentSiteManager.getUserInfo(this, handler);
				} else {
					showHint("认证失败!");
				}
			} else {
				showHint("认证失败!");
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_right:
			System.out.println("切换分享账户~");
			switch (shareType) {
			case Constants.SHARE_SINA:
				// 这一步不调用会崩溃
				CookieSyncManager.createInstance(getApplicationContext());
				CookieManager.getInstance().removeAllCookie();
				new SinaSiteManager(this, null).login();
				break;
			case Constants.SHARE_TENCENT:
				new TencentSiteManager(this, null).login();
				break;
			case Constants.SHARE_QZONE:
				//new QzoneSiteManager(this, null).login();
				break;
			}
			break;
		case R.id.share_button:
			System.out.println("执行分享~");
			String titleTemp = shareTitle.getText().toString().trim();
			String contentTemp = shareContent.getText().toString().trim();
			titleTemp += "来自艾图客户端";
			contentTemp += "详情请关注应用市场艾图";

			final String title = titleTemp;
			final String content = contentTemp;
			switch (shareType) {
			case Constants.SHARE_SINA:// ok 新浪微博
				execTask(new Thread() {
					@Override
					public void run() {
						SinaSiteManager sinaSiteManager = new SinaSiteManager(
								ShareActivity.this, handler);
						sinaSiteManager.share(ShareActivity.this, title,
								Prefs.getRecentlyOperaFile(ShareActivity.this),
								null, null);
					}
				});
				break;
			case Constants.SHARE_TENCENT:// ok 腾讯微博
				execTask(new Thread() {
					@Override
					public void run() {
						TencentSiteManager tencentSiteManager = new TencentSiteManager(
								ShareActivity.this, handler);
						tencentSiteManager.share(ShareActivity.this, title,
								Prefs.getRecentlyOperaFile(ShareActivity.this),
								null, null);
					}
				});
				break;
			case Constants.SHARE_QZONE:// QQ互联
				final SiteManager qzone = new QzoneSiteManager(this, handler);
				qzone.share(ShareActivity.this, title,
						Prefs.getRecentlyOperaFile(ShareActivity.this), null, null);
				break;
			case Constants.SHARE_FRIEND:// ok 发送到微信
				WeixinChat wehcat = new WeixinChat(ShareActivity.this,
						new Handler());
				wehcat.register();
				wehcat.sendToFriends(ShareActivity.this, title, content,
						Prefs.getRecentlyOperaFile(ShareActivity.this));
				break;
			case Constants.SHARE_EMAIL:// ok 邮件
				emailShare(title, content, null,
						Prefs.getRecentlyOperaFile(ShareActivity.this));
				break;
			}
			break;
		}
	}

	private void emailShare(String emailTitle, String emailContent,
			String[] emailReciver, String filePath) {
		Intent email = new Intent(android.content.Intent.ACTION_SEND);
		// 附件
		File file = new File(filePath);
		// 邮件发送类型：带附件的邮件
		email.setType("application/octet-stream");
		// 设置邮件地址 邮件接收者（数组，可以是多位接收者）
		email.putExtra(android.content.Intent.EXTRA_EMAIL, emailReciver);
		// 设置邮件标题
		email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailTitle);
		// 设置发送的内容
		email.putExtra(android.content.Intent.EXTRA_TEXT, emailContent);
		// 附件
		email.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		// 调用系统的邮件系统
		startActivity(Intent.createChooser(email, "请选择邮件程序"));
	}
}
