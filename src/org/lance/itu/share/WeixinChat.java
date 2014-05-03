package org.lance.itu.share;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.lance.itu.util.Prefs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

/**
 * 分享到微信相关功能
 * 
 * @author lance
 */
public class WeixinChat {
	/** WXWebpageObject对象中的webpageurl字段最大长度为10KB */
	public static final int WEBPAGEURL_MAX_LENGTH = 5000;
	/** 目标缩略图的宽和高 */
	private static final int THUMB_SIZE = 150;
	/** 发送消息时缩略图文件最大为32KB */
	private static final float THUMB_FILE_MAX_SIZE = 32 * 1000;
	/** WXTextObject中text字段长度需大于0且不超过10KB,一个字符两个字节，为了保险 */
	private static final int TEXT_MAX_LENGTH = 5000;
	/** WXMediaMessage对象中的title字段长度不超过512bytes */
	private static final int MEDIA_TITLE_MAX_LENGTH = 250;
	/** WXMediaMessage对象中的description字段长度不超过1KB */
	private static final int DESCRIPTION_MAX_LENGTH = 500;
	/** WXImageObject对象中imageData大小不得超过10MB */
	private static final long IMAGE_FILE_MAX_LENGTH = 10 * 1024 * 1024;

	private IWXAPI mApi = null;
	private Context mContext = null;
	private Handler mHandler = null;
	final int Version = 0x21020001;//

	/**
	 * <默认构造函数>
	 * 
	 * @param paramContext
	 *            上下文
	 * @param paramHandler
	 *            handler
	 */
	public WeixinChat(Context paramContext, Handler paramHandler) {
		this.mContext = paramContext;
		this.mHandler = paramHandler;
		initWXAPI();
	}

	/**
	 * 初始化WXAPI,创建WXAPI，并向微信注册应用
	 */
	public void initWXAPI() {
		this.mApi = WXAPIFactory.createWXAPI(this.mContext,
				Prefs.getWeixinId(), true);
		this.mApi.registerApp(Prefs.getWeixinId());
	}

	/** 向微信注册应用，创建对象之后调用 */
	public void register() {
		if (null == this.mApi) {
			this.mApi = WXAPIFactory.createWXAPI(this.mContext, null);
			this.mApi.registerApp(Prefs.getWeixinId());
		}
	}

	/**
	 * 向微信注销应用，界面销毁时调用
	 */
	public void unregister() {
		if (null != this.mApi) {
			this.mApi.unregisterApp();
		}
	}

	/**
	 * 判断是否安装了微信
	 * 
	 * @return
	 */
	public boolean ISWXAppInstalled() {
		return mApi.isWXAppInstalled();
	}

	/**
	 * 判断微信版本是否支持发送到朋友圈
	 * 
	 * @return
	 */
	public boolean ISShareFirends() {
		return mApi.getWXAppSupportAPI() >= Version ? true : false;
	}

	public void sendToFriends(Context context, String title, String content,
			String path) {
		if (mApi.openWXApp()) {
			WXWebpageObject obj = new WXWebpageObject();
			obj.webpageUrl = "http://www.wallinter.com";
			WXMediaMessage localWXMediaMessage = new WXMediaMessage(obj);

			localWXMediaMessage.title = title;
			localWXMediaMessage.description = content;
			Bitmap bmp = BitmapFactory.decodeFile(path);
			if (bmp != null) {
				Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE,
						THUMB_SIZE, true);
				localWXMediaMessage.thumbData = bmpToByteArray(thumbBmp, true);
				bmp.recycle();
			}

			SendMessageToWX.Req localReq = new SendMessageToWX.Req();
			localReq.transaction = buildTransaction("img");
			localReq.message = localWXMediaMessage;
			localReq.scene = SendMessageToWX.Req.WXSceneTimeline;// 分享到朋友圈
			// localReq.scene = SendMessageToWX.Req.WXSceneSession;// 分享给好友

			/*
			 * WXImageObject imgObj = new WXImageObject();
			 * imgObj.setImagePath(path);
			 * 
			 * WXMediaMessage msg = new WXMediaMessage(); msg.mediaObject =
			 * imgObj;
			 * 
			 * Bitmap bmp = BitmapFactory.decodeFile(path); Bitmap thumbBmp =
			 * Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
			 * bmp.recycle(); msg.thumbData = bmpToByteArray(thumbBmp, true);
			 * 
			 * SendMessageToWX.Req req = new SendMessageToWX.Req();
			 * req.transaction = buildTransaction("img"); req.message = msg;
			 * req.scene = SendMessageToWX.Req.WXSceneTimeline ;
			 * mApi.sendReq(req);
			 */
			mApi.sendReq(localReq);
		} else {
			Toast.makeText(context, "未安装微信", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 向微信发送纯文本消息
	 * 
	 * @param content
	 *            消息内容
	 */
	public void sendTextToWeChat(String content) {
		String text = content;
		if (TextUtils.isEmpty(text)) {
			text = "应用分享";
		}
		// 初始化一个WXTextObject对象
		WXTextObject textObj = new WXTextObject();
		// WXTextObject的text字段长度需大于0且不超过10KB
		// 加判断，如果超长需要截取
		if (TEXT_MAX_LENGTH < text.length()) {
			text = text.substring(0, TEXT_MAX_LENGTH);
		}
		textObj.text = text;

		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		// 发送文本类型的消息时，title字段不起作用
		msg.title = "Will be ignored";
		// WXMediaMessage的description字段大小不超过1KB
		if (DESCRIPTION_MAX_LENGTH < text.length()) {
			text = text.substring(0, DESCRIPTION_MAX_LENGTH);
		}
		msg.description = text;
		// 构造一个Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		// transaction字段用于唯一标识一个请求
		req.transaction = buildTransaction("text");
		req.message = msg;
		// 调用api接口发送数据到微信
		mApi.sendReq(req);
	}

	/**
	 * 根据本地路径向微信发送图片
	 * 
	 * @param filePath
	 *            图片路径
	 */
	public void sendLocalImageToWeChat(String filePath) {
		String path = filePath;
		File file = new File(path);
		if (!file.exists()) {
			// mHandler.sendEmptyMessage(ShareActivity.WECHAT_SEND_FAIL_NOT_IMAGE);
			return;
		}
		if (IMAGE_FILE_MAX_LENGTH < file.length()) {
			// mHandler.sendEmptyMessage(ShareActivity.WECHAT_SEND_FAIL_IMAGE_TOO_BIG);
			return;
		}
		WXImageObject imgObj = new WXImageObject();
		imgObj.setImagePath(path);

		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imgObj;
		// 返回值判空
		Bitmap bmp = getThumbnailFromLocalImageWithWidthAndHeight(path,
				THUMB_SIZE, THUMB_SIZE, 0);
		if (null != bmp) {
			byte[] bmpData = bmpToByteArray(bmp, true);
			msg.thumbData = bmpData;
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			// transaction字段用于唯一标识一个请求
			req.transaction = buildTransaction("img");
			req.message = msg;
			mApi.sendReq(req);
		}
		// 如果获取图片缩略图失败，取消发送消息并提示用户
		else {
			// mHandler.sendEmptyMessage(ShareActivity.WECHAT_SEND_FAIL_IMAGE_NOT_THUMB);
		}
	}

	/**
	 * 向微信好友发送视频或音频资源的下载地址
	 * 
	 * @param title
	 *            消息标题，长度不超过512Bytes
	 * @param description
	 *            消息描述，大小不要超过1KB
	 * @param webUrl
	 *            网页的网址，长度大于0且不超过10KB
	 * @param localFilePath
	 *            如果分享的视频，根据路径生成一个视频缩略图；如果是音频该参数无意义，可设为null
	 * @param isMusic
	 *            分享的是否是音乐文件；true为音频文件，false为视频文件
	 */
	public void sendWebUrlToWeChat(String title, String description,
			String webUrl, String localFilePath, boolean isMusic) {
		String msgTitle = title;
		String msgDescription = description;
		if (MEDIA_TITLE_MAX_LENGTH < msgTitle.length()) {
			msgTitle = msgTitle.substring(0, MEDIA_TITLE_MAX_LENGTH);
		}
		if (DESCRIPTION_MAX_LENGTH < msgDescription.length()) {
			msgDescription = msgDescription
					.substring(0, DESCRIPTION_MAX_LENGTH);
		}
		WXWebpageObject web = new WXWebpageObject();
		web.webpageUrl = webUrl;

		WXMediaMessage msg = new WXMediaMessage(web);
		msg.title = msgTitle;
		msg.description = msgDescription;
		Bitmap thumb = null;
		byte[] thumbData = null;
		if (isMusic) {
			// 分享音乐文件时，缩略图为固定图片
			// thumb = BitmapFactory.decodeResource(mContext.getResources(),
			// R.drawable.voice_icon);
			if (null != thumb) {
				thumbData = bmpToByteArray(thumb, true);
			}
		} else {
			// 设置视频的缩略图
			thumb = getVideoThumbFromLocalPath(localFilePath);
			if (null != thumb) {
				thumbData = bmpToByteArray(thumb, true);
			}
		}

		if (null != thumbData) {
			// thumbData大小不过超过32KB
			msg.thumbData = thumbData;
		}

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("video");
		req.message = msg;
		mApi.sendReq(req);
	}

	/**
	 * 生成一个唯一标志请求的字符串
	 * 
	 * @param type
	 *            请求的类型，text、image、webpage等
	 * @return
	 */
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	/**
	 * 将bitmap对象转换成byte数组
	 * 
	 * @param paramBitmap
	 *            需要转换的bitmap对象
	 * @param paramBoolean
	 *            转换之后是否需要recycle
	 * @return 返回对应图片的byte数组
	 */
	private byte[] bmpToByteArray(Bitmap paramBitmap, boolean paramBoolean) {
		if (null != paramBitmap) {
			ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
			paramBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
					localByteArrayOutputStream);
			if (paramBoolean) {
				paramBitmap.recycle();
			}
			byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
			try {
				localByteArrayOutputStream.close();
				return arrayOfByte;
			} catch (Exception localException) {
				localException.printStackTrace();
			}

			return null;
		}
		return null;
	}

	/**
	 * 根据本地图片路径获取该图片的缩略图(大小小于32KB)
	 * 
	 * @param localImagePath
	 *            本地图片的路径
	 * @param width
	 *            缩略图的宽
	 * @param height
	 *            缩略图的高
	 * @param addedScaling
	 *            额外可以加的缩放比例
	 * @return bitmap 指定宽高的缩略图
	 */
	private Bitmap getThumbnailFromLocalImageWithWidthAndHeight(
			String localImagePath, int width, int height, int addedScaling) {
		File imgFile = new File(localImagePath);
		if (TextUtils.isEmpty(localImagePath) || !imgFile.isFile()) {
			return null;
		}
		Bitmap temBitmap = null;

		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			options.inSampleSize = 1;
			// 加载获取图片的宽高
			BitmapFactory.decodeFile(localImagePath, options);
			if (options.outWidth > 0 && options.outHeight > 0) {
				int widthFactor = (options.outWidth + width - 1) / width;
				int heightFactor = (options.outHeight + height - 1) / height;

				widthFactor = Math.max(widthFactor, heightFactor);
				widthFactor = Math.max(widthFactor, 1);

				if (widthFactor > 1) {
					if ((widthFactor & (widthFactor - 1)) != 0) {
						while ((widthFactor & (widthFactor - 1)) != 0) {
							widthFactor &= widthFactor - 1;
						}
						// 保证缩放的倍数为2的N次方
						widthFactor <<= 1;
					}
				}
				options.inSampleSize = widthFactor;
				options.inJustDecodeBounds = false;
				temBitmap = BitmapFactory.decodeFile(localImagePath, options);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		if (null != temBitmap) {
			byte[] tempData = bmpToByteArray(temBitmap, false);
			// 如果得到的缩略图文件超过最大限制，需要进一步处理
			if (tempData.length > THUMB_FILE_MAX_SIZE) {
				temBitmap.recycle();
				int sampleSize = (int) ((tempData.length / THUMB_FILE_MAX_SIZE) + 1);
				BitmapFactory.Options outOptions = new BitmapFactory.Options();
				outOptions.inSampleSize = sampleSize;
				temBitmap = BitmapFactory.decodeByteArray(tempData, 0,
						tempData.length, outOptions);
			}
			tempData = null;
		}
		return temBitmap;
	}

	private Bitmap getVideoThumbFromLocalPath(String localFilePath) {
		Bitmap bitmap = null;
		Bitmap thumb = null;
		// bitmap = VideoOperator.getThumbnailForVideo(localFilePath,
		// mContext.getContentResolver());
		if (null != bitmap) {
			// 由于VideoOperator.getThumbnailForVideo（）方法得到的缩略图为MINI_KIND型，图片可能超出限制
			thumb = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE,
					true);
			bitmap.recycle();
			// begin bugID:2707
			// 在转换成byte数组时不能强制recycle。这个thumb对象有可能没超过大小限制，可能直接返回该thumb对象
			byte[] thumbData = bmpToByteArray(thumb, false);
			// 如果得到的缩略图文件超过最大限制，需要进一步处理
			if (thumbData.length > THUMB_FILE_MAX_SIZE) {
				thumb.recycle();
				// end of BugID:2707
				int sampleSize = (int) (thumbData.length / THUMB_FILE_MAX_SIZE) + 1;
				BitmapFactory.Options outOptions = new BitmapFactory.Options();
				outOptions.inSampleSize = sampleSize;
				thumb = BitmapFactory.decodeByteArray(thumbData, 0,
						thumbData.length, outOptions);
			}
			thumbData = null;
		}
		return thumb;
	}
}