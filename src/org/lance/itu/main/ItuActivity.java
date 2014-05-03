package org.lance.itu.main;

import static org.lance.itu.util.Constants.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.lance.itu.adapter.DCIMAdapter;
import org.lance.itu.adapter.SettingAdapter;
import org.lance.itu.anim.UgcAnimations;
import org.lance.itu.share.SinaSiteManager;
import org.lance.itu.share.TencentSiteManager;
import org.lance.itu.util.ActivityForResultUtil;
import org.lance.itu.util.Constants;
import org.lance.itu.util.ImageUtil;
import org.lance.itu.util.PhotoUtil;
import org.lance.itu.util.Prefs;
import org.lance.itu.util.SharePrefs;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.webview.OAuthV2AuthorizeWebView;

/**
 * 主页
 * @author lance
 *
 */
public class ItuActivity extends BaseActivity implements OnClickListener{

	private String cameraPath = "";// 拍照时保存照片的路径

	private View ituMainView;
	private Button browseBtn;//浏览文件
	private Button imageAllBtn;
	private Button handleBtn;
	private GridView displayGrid;
	private DCIMAdapter dcimAdapter;
	private List<Map<String,String>> mList=new ArrayList<Map<String,String>>();
	
	// 菜单按钮
	private View mUgcView;
	private RelativeLayout mUgcLayout;

	private ImageView mUgc;// 菜单按钮
	private ImageView mUgcBg;// 背景
	private ImageView mUgcVoice;// 语音
	private ImageView mUgcPhoto;// 照片
	private ImageView mUgcRecord;// 消息记录
	//private ImageView mUgcLbs;// 签到
	private boolean mUgcIsShowing = false;
	
	private int popup_shareh=-160;
	private int popup_snsh=-320;

	@Override
	protected void onCreate(Bundle saved) {
		super.onCreate(saved);
		ituMainView=LayoutInflater.from(this).inflate(R.layout.itu_main, null);
		setContentView(ituMainView);
		if(saved!=null){
			cameraPath=saved.getString("cameraPath");
		}
		imageAllBtn=(Button)findViewById(R.id.itu_image_all);
		handleBtn=(Button)findViewById(R.id.itu_handle);
		displayGrid=(GridView)findViewById(R.id.itu_grid);
		browseBtn=(Button) findViewById(R.id.itu_browse);
		mUgcView = (View) findViewById(R.id.desktop_ugc);
		mUgcLayout = (RelativeLayout) findViewById(R.id.ugc_layout);
		mUgc = (ImageView) findViewById(R.id.ugc);
		mUgcBg = (ImageView) findViewById(R.id.ugc_bg);

		mUgcVoice = (ImageView) findViewById(R.id.ugc_voice);
		mUgcPhoto = (ImageView) findViewById(R.id.ugc_photo);
		mUgcRecord = (ImageView) findViewById(R.id.ugc_share);
		//mUgcLbs = (ImageView) findViewById(R.id.ugc_lbs);

		browseBtn.setOnClickListener(this);
		imageAllBtn.setOnClickListener(this);
		handleBtn.setOnClickListener(this);
		registerForContextMenu(displayGrid);
		Map<String,List<Map<String,String>>> maps=ImageUtil.getFiles(new File(Prefs.getDCIMPath()), null);
		if(maps.size()>0){
			Set<String> keys = maps.keySet();
			Iterator<String> iter=keys.iterator();
			mList=maps.get(iter.next());
			dcimAdapter=new DCIMAdapter(this, mList, mScreenWidth);
			displayGrid.setAdapter(dcimAdapter);
		}else{
			browseBtn.setVisibility(View.VISIBLE);
			showHint("相册里没有照片!");
		}
		displayGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent intent = new Intent(ItuActivity.this, ImageFilterActivity.class);
				String path=mList.get(position).get("image_path");
				intent.putExtra("path", path);
				startActivity(intent);
			}
		});
		mUgcView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				// 判断是否已经显示,显示则关闭并隐藏
				if (mUgcIsShowing) {
					mUgcIsShowing = false;
					UgcAnimations.startCloseAnimation(mUgcLayout, mUgcBg, mUgc,
							500);
					return true;
				}
				return false;
			}
		});
		mUgc.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 判断是否显示,已经显示则隐藏,否则则显示
				mUgcIsShowing = !mUgcIsShowing;
				if (mUgcIsShowing) {
					UgcAnimations.startOpenAnimation(mUgcLayout, mUgcBg, mUgc,
							500);
				} else {
					UgcAnimations.startCloseAnimation(mUgcLayout, mUgcBg, mUgc,
							500);
				}
			}
		});
		// Path 语音按钮监听
		mUgcVoice.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Animation anim = UgcAnimations.clickAnimation(500);
				anim.setAnimationListener(new AnimationListener() {

					public void onAnimationStart(Animation animation) {

					}

					public void onAnimationRepeat(Animation animation) {

					}

					public void onAnimationEnd(Animation animation) {
						startActivity(new Intent(ItuActivity.this,
								VoiceActivity.class));
						closeUgc();
					}
				});
				mUgcVoice.startAnimation(anim);
			}
		});
		// Path 拍照按钮监听
		mUgcPhoto.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Animation anim = UgcAnimations.clickAnimation(500);
				anim.setAnimationListener(new AnimationListener() {

					public void onAnimationStart(Animation animation) {

					}

					public void onAnimationRepeat(Animation animation) {

					}

					public void onAnimationEnd(Animation animation) {
						sendToCamera();
						closeUgc();
					}
				});
				mUgcPhoto.startAnimation(anim);
			}
		});
		// Path 分享按钮监听
		mUgcRecord.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Animation anim = UgcAnimations.clickAnimation(500);
				anim.setAnimationListener(new AnimationListener() {

					public void onAnimationStart(Animation animation) {

					}

					public void onAnimationRepeat(Animation animation) {

					}

					public void onAnimationEnd(Animation animation) {
						if("".equals(Prefs.getRecentlyOperaFile(ItuActivity.this))){
							showHint("没有最近操作的文件!");
						}else{
							settingPopup.showAsDropDown(ituMainView, 0, popup_shareh);
						}
						closeUgc();
					}
				});
				mUgcRecord.startAnimation(anim);
			}
		});
		// Path 签到按钮监听
//		mUgcLbs.setOnClickListener(new OnClickListener() {
//
//			public void onClick(View v) {
//				Animation anim = UgcAnimations.clickAnimation(500);
//				anim.setAnimationListener(new AnimationListener() {
//
//					public void onAnimationStart(Animation animation) {
//
//					}
//
//					public void onAnimationRepeat(Animation animation) {
//
//					}
//
//					public void onAnimationEnd(Animation animation) {
//						//startActivity(new Intent(ItuActivity.this,LocationActivity.class));
//						closeUgc();
//					}
//				});
//				mUgcLbs.startAnimation(anim);
//			}
//		});
		inflateSettingView();
		inflateShareView();
	}
	
	private View settingView;
	private ListView settingList;
	private SettingAdapter setAdapter;
	private PopupWindow settingPopup;
	
	private void inflateSettingView() {
		settingView = LayoutInflater.from(this).inflate(R.layout.setting_option_layout,
				null);
		settingList=(ListView)settingView.findViewById(R.id.setting_list);
		settingList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if(position==0){
					shareImage();
				}else if(position==1){
					sharePopup.showAsDropDown(ituMainView, 0, popup_snsh);
					//startActivity(new Intent(ItuActivity.this,ShareActivity.class));
				}
				settingPopup.dismiss();
			}
		});
		List<String> data=new ArrayList<String>();
		data.add("应用分享");
		data.add("社交分享");
		setAdapter=new SettingAdapter(this, data);
		settingList.setAdapter(setAdapter);
		settingPopup = new PopupWindow(settingView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		settingPopup.setAnimationStyle(R.style.setting_animation);
		settingPopup.setFocusable(true);
		settingPopup.setTouchable(true);
		settingPopup.setOutsideTouchable(true);
		settingPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	}
	
	private View shareView;
	private ListView shareList;
	private SettingAdapter shareAdapter;
	private PopupWindow sharePopup;
	private void inflateShareView() {
		shareView = LayoutInflater.from(this).inflate(R.layout.setting_option_layout,
				null);
		shareList=(ListView)shareView.findViewById(R.id.setting_list);
		shareList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if(position==0){//新浪微博
					System.out.println("token--->"+SharePrefs.readAccessToken(
							ItuActivity.this, SharePrefs.PREFS_SINA));
					if (TextUtils.isEmpty(SharePrefs.readAccessToken(
							ItuActivity.this, SharePrefs.PREFS_SINA))) {
						new SinaSiteManager(ItuActivity.this, new Handler(){
							@Override
							public void handleMessage(Message msg) {
								switch(msg.what){
								case ShareActivity.SHARE_SUCCESS:
									sendShare(SHARE_SINA);
									break;
								}
							}
						}).login();
					} else {
						sendShare(SHARE_SINA);
					}
				}else if(position==1){// ok 腾讯微博
					System.out.println("token--->"+SharePrefs.readAccessToken(
							ItuActivity.this, SharePrefs.PREFS_TENCENT));
					if (TextUtils.isEmpty(SharePrefs.readAccessToken(
							ItuActivity.this, SharePrefs.PREFS_TENCENT))) {
						new TencentSiteManager(ItuActivity.this, null).login();
					} else {
						sendShare(SHARE_TENCENT);
					}
				}else if(position==2){//QQ空间
//					System.out.println("token--->"+SharePrefs.readAccessToken(
//							ItuActivity.this, SharePrefs.PREFS_QZONE));
//					if (TextUtils.isEmpty(SharePrefs.readAccessToken(
//							ItuActivity.this, SharePrefs.PREFS_QZONE))) {
//						new QzoneSiteManager(ItuActivity.this, new Handler(){
//
//							@Override
//							public void handleMessage(Message msg) {
//								switch(msg.what){
//								case ShareActivity.SHARE_SUCCESS:
//									sendShare(SHARE_QZONE);
//									break;
//								}
//							}
//							
//						}).login();
//					} else {
//					}
					sendShare(SHARE_QZONE);
				}else if(position==3){//微信朋友
					sendShare(SHARE_FRIEND);
				}else if(position==4){
					sendShare(SHARE_EMAIL);
				}
				sharePopup.dismiss();
			}
		});
		List<String> data=new ArrayList<String>();
		data.add("新浪微博");
		data.add("腾讯微博");
		data.add("QQ空间");
		data.add("微信");
		data.add("邮件");
		setAdapter=new SettingAdapter(this, data);
		shareList.setAdapter(setAdapter);
		sharePopup = new PopupWindow(shareView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		sharePopup.setAnimationStyle(R.style.share_animation);
		sharePopup.setFocusable(true);
		sharePopup.setTouchable(true);
		sharePopup.setOutsideTouchable(true);
		sharePopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	}

	/** 上下文菜单 **/
	private final int MENU_DELETE=1;
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterView.AdapterContextMenuInfo info = null;

		try {
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		} catch (ClassCastException e) {
			return;
		}

		menu.setHeaderTitle("删除图片!");
		menu.add(0, MENU_DELETE, 1, "删除");
	}

	/** 上下文菜单事件处理 **/
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case MENU_DELETE:
			if(mList.size()>0){
				Map<String,String> imageInf=mList.get(info.position);
				String path=imageInf.get("image_path");
				System.out.println("路径--->"+path);
				File file=new File(path);
				if(file.exists()){
					if(file.delete()){
						mList.remove(info.position);
						dcimAdapter.setmList(mList);
					}else{
						showHint("删除失败!");
					}
				}else{
					showHint("文件不存在!");
				}
			}
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void shareImage(){
		Intent intent=new Intent(Intent.ACTION_SEND);   
		intent.setType("image/*");   
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享");   
		intent.putExtra(Intent.EXTRA_TEXT, "终于可以了!!!");    
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
		intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(Prefs.getRecentlyOperaFile(this)))); 
		startActivity(Intent.createChooser(intent, getTitle()));  
	}
	
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putString("cameraPath", cameraPath);
		super.onSaveInstanceState(savedInstanceState);
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ActivityForResultUtil.TENCENT_REQUEST_CODE:
			if (resultCode == OAuthV2AuthorizeWebView.RESULT_CODE) {
				OAuthV2 oAuth = (OAuthV2) data.getExtras().getSerializable("oauth");
				if (oAuth.getStatus() == 0) {
					SharePrefs.putAccessToken(this, oAuth.getAccessToken(), SharePrefs.PREFS_TENCENT);
					SharePrefs.putOpenId(this, oAuth.getOpenid(), SharePrefs.PREFS_TENCENT);
					SharePrefs.putExpiresIn(this, oAuth.getExpiresIn(), SharePrefs.PREFS_TENCENT);
					showHint("认证成功!");
					TencentSiteManager.getUserInfo(this,null);
					sendShare(SHARE_TENCENT);
				} else {
					showHint("认证失败!");
				}
			}else{
				showHint("认证失败!");
			}
			break;
		case ActivityForResultUtil.REQUESTCODE_UPLOADPHOTO_CAMERA:
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					Toast.makeText(this, "SD不可用", Toast.LENGTH_SHORT).show();
					return;
				}
				if ("".equals(cameraPath)) {
					return;
				}
				Intent intent = new Intent(this, ImageFilterActivity.class);
				String path = PhotoUtil.saveToLocal(PhotoUtil.createBitmap(
						cameraPath, mScreenWidth, mScreenHeight));
				intent.putExtra("path", path);
				startActivity(intent);
			} else {
				Toast.makeText(this, "取消保存~", Toast.LENGTH_SHORT).show();
			}
			break;
		case ActivityForResultUtil.REQUESTCODE_UPLOADPHOTO_BROWSE:{
			if(data!=null){
				cameraPath=data.getStringExtra("PATH");
				if(ImageUtil.isImageFile(cameraPath)){
					Intent intent = new Intent(this, ImageFilterActivity.class);
					intent.putExtra("path", cameraPath);
					startActivity(intent);
				}else{
					showHint("您选择的不是图片文件");
				}
			}
		}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void closeUgc() {
		mUgcIsShowing = false;
		UgcAnimations.startCloseAnimation(mUgcLayout, mUgcBg, mUgc, 500);
	}

	private void sendShare(int shareType) {
		Intent intent = new Intent();
		intent.setClass(this, ShareActivity.class);
		intent.putExtra(Constants.CHAIN_SHARE_TYPE, shareType);
		startActivity(intent);
	}

	private void sendToCamera(){
		// 这里拍照后返回该照片跳转到照片编辑页面
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File dir = new File(Prefs.getDCIMPath());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String path = Prefs.getDCIMPath()
				+ UUID.randomUUID().toString()+".jpg";
		cameraPath = path;
		File file = new File(path);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {

			}
		}
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(file));
		startActivityForResult(intent, ActivityForResultUtil.REQUESTCODE_UPLOADPHOTO_CAMERA);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.itu_handle:
			Intent intent = new Intent(ItuActivity.this, AlbumActivity.class);
			intent.putExtra("path", Prefs.getImageFilterPath());
			startActivity(intent);
			break;
		case R.id.itu_image_all:
			startActivity(new Intent(ItuActivity.this,
					PhoneAlbumActivity.class));
			break;
		case R.id.itu_browse:
			startActivityForResult(new Intent(this,FileBrowseActivity.class), 
					ActivityForResultUtil.REQUESTCODE_UPLOADPHOTO_BROWSE);
			break;
		}
	}
}
