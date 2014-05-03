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
 * ����΢����ع���
 * 
 * @author lance
 */
public class WeixinChat {
	/** WXWebpageObject�����е�webpageurl�ֶ���󳤶�Ϊ10KB */
	public static final int WEBPAGEURL_MAX_LENGTH = 5000;
	/** Ŀ������ͼ�Ŀ�͸� */
	private static final int THUMB_SIZE = 150;
	/** ������Ϣʱ����ͼ�ļ����Ϊ32KB */
	private static final float THUMB_FILE_MAX_SIZE = 32 * 1000;
	/** WXTextObject��text�ֶγ��������0�Ҳ�����10KB,һ���ַ������ֽڣ�Ϊ�˱��� */
	private static final int TEXT_MAX_LENGTH = 5000;
	/** WXMediaMessage�����е�title�ֶγ��Ȳ�����512bytes */
	private static final int MEDIA_TITLE_MAX_LENGTH = 250;
	/** WXMediaMessage�����е�description�ֶγ��Ȳ�����1KB */
	private static final int DESCRIPTION_MAX_LENGTH = 500;
	/** WXImageObject������imageData��С���ó���10MB */
	private static final long IMAGE_FILE_MAX_LENGTH = 10 * 1024 * 1024;

	private IWXAPI mApi = null;
	private Context mContext = null;
	private Handler mHandler = null;
	final int Version = 0x21020001;//

	/**
	 * <Ĭ�Ϲ��캯��>
	 * 
	 * @param paramContext
	 *            ������
	 * @param paramHandler
	 *            handler
	 */
	public WeixinChat(Context paramContext, Handler paramHandler) {
		this.mContext = paramContext;
		this.mHandler = paramHandler;
		initWXAPI();
	}

	/**
	 * ��ʼ��WXAPI,����WXAPI������΢��ע��Ӧ��
	 */
	public void initWXAPI() {
		this.mApi = WXAPIFactory.createWXAPI(this.mContext,
				Prefs.getWeixinId(), true);
		this.mApi.registerApp(Prefs.getWeixinId());
	}

	/** ��΢��ע��Ӧ�ã���������֮����� */
	public void register() {
		if (null == this.mApi) {
			this.mApi = WXAPIFactory.createWXAPI(this.mContext, null);
			this.mApi.registerApp(Prefs.getWeixinId());
		}
	}

	/**
	 * ��΢��ע��Ӧ�ã���������ʱ����
	 */
	public void unregister() {
		if (null != this.mApi) {
			this.mApi.unregisterApp();
		}
	}

	/**
	 * �ж��Ƿ�װ��΢��
	 * 
	 * @return
	 */
	public boolean ISWXAppInstalled() {
		return mApi.isWXAppInstalled();
	}

	/**
	 * �ж�΢�Ű汾�Ƿ�֧�ַ��͵�����Ȧ
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
			localReq.scene = SendMessageToWX.Req.WXSceneTimeline;// ��������Ȧ
			// localReq.scene = SendMessageToWX.Req.WXSceneSession;// ���������

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
			Toast.makeText(context, "δ��װ΢��", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * ��΢�ŷ��ʹ��ı���Ϣ
	 * 
	 * @param content
	 *            ��Ϣ����
	 */
	public void sendTextToWeChat(String content) {
		String text = content;
		if (TextUtils.isEmpty(text)) {
			text = "Ӧ�÷���";
		}
		// ��ʼ��һ��WXTextObject����
		WXTextObject textObj = new WXTextObject();
		// WXTextObject��text�ֶγ��������0�Ҳ�����10KB
		// ���жϣ����������Ҫ��ȡ
		if (TEXT_MAX_LENGTH < text.length()) {
			text = text.substring(0, TEXT_MAX_LENGTH);
		}
		textObj.text = text;

		// ��WXTextObject�����ʼ��һ��WXMediaMessage����
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		// �����ı����͵���Ϣʱ��title�ֶβ�������
		msg.title = "Will be ignored";
		// WXMediaMessage��description�ֶδ�С������1KB
		if (DESCRIPTION_MAX_LENGTH < text.length()) {
			text = text.substring(0, DESCRIPTION_MAX_LENGTH);
		}
		msg.description = text;
		// ����һ��Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		// transaction�ֶ�����Ψһ��ʶһ������
		req.transaction = buildTransaction("text");
		req.message = msg;
		// ����api�ӿڷ������ݵ�΢��
		mApi.sendReq(req);
	}

	/**
	 * ���ݱ���·����΢�ŷ���ͼƬ
	 * 
	 * @param filePath
	 *            ͼƬ·��
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
		// ����ֵ�п�
		Bitmap bmp = getThumbnailFromLocalImageWithWidthAndHeight(path,
				THUMB_SIZE, THUMB_SIZE, 0);
		if (null != bmp) {
			byte[] bmpData = bmpToByteArray(bmp, true);
			msg.thumbData = bmpData;
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			// transaction�ֶ�����Ψһ��ʶһ������
			req.transaction = buildTransaction("img");
			req.message = msg;
			mApi.sendReq(req);
		}
		// �����ȡͼƬ����ͼʧ�ܣ�ȡ��������Ϣ����ʾ�û�
		else {
			// mHandler.sendEmptyMessage(ShareActivity.WECHAT_SEND_FAIL_IMAGE_NOT_THUMB);
		}
	}

	/**
	 * ��΢�ź��ѷ�����Ƶ����Ƶ��Դ�����ص�ַ
	 * 
	 * @param title
	 *            ��Ϣ���⣬���Ȳ�����512Bytes
	 * @param description
	 *            ��Ϣ��������С��Ҫ����1KB
	 * @param webUrl
	 *            ��ҳ����ַ�����ȴ���0�Ҳ�����10KB
	 * @param localFilePath
	 *            ����������Ƶ������·������һ����Ƶ����ͼ���������Ƶ�ò��������壬����Ϊnull
	 * @param isMusic
	 *            ������Ƿ��������ļ���trueΪ��Ƶ�ļ���falseΪ��Ƶ�ļ�
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
			// ���������ļ�ʱ������ͼΪ�̶�ͼƬ
			// thumb = BitmapFactory.decodeResource(mContext.getResources(),
			// R.drawable.voice_icon);
			if (null != thumb) {
				thumbData = bmpToByteArray(thumb, true);
			}
		} else {
			// ������Ƶ������ͼ
			thumb = getVideoThumbFromLocalPath(localFilePath);
			if (null != thumb) {
				thumbData = bmpToByteArray(thumb, true);
			}
		}

		if (null != thumbData) {
			// thumbData��С��������32KB
			msg.thumbData = thumbData;
		}

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("video");
		req.message = msg;
		mApi.sendReq(req);
	}

	/**
	 * ����һ��Ψһ��־������ַ���
	 * 
	 * @param type
	 *            ��������ͣ�text��image��webpage��
	 * @return
	 */
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	/**
	 * ��bitmap����ת����byte����
	 * 
	 * @param paramBitmap
	 *            ��Ҫת����bitmap����
	 * @param paramBoolean
	 *            ת��֮���Ƿ���Ҫrecycle
	 * @return ���ض�ӦͼƬ��byte����
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
	 * ���ݱ���ͼƬ·����ȡ��ͼƬ������ͼ(��СС��32KB)
	 * 
	 * @param localImagePath
	 *            ����ͼƬ��·��
	 * @param width
	 *            ����ͼ�Ŀ�
	 * @param height
	 *            ����ͼ�ĸ�
	 * @param addedScaling
	 *            ������Լӵ����ű���
	 * @return bitmap ָ����ߵ�����ͼ
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
			// ���ػ�ȡͼƬ�Ŀ��
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
						// ��֤���ŵı���Ϊ2��N�η�
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
			// ����õ�������ͼ�ļ�����������ƣ���Ҫ��һ������
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
			// ����VideoOperator.getThumbnailForVideo���������õ�������ͼΪMINI_KIND�ͣ�ͼƬ���ܳ�������
			thumb = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE,
					true);
			bitmap.recycle();
			// begin bugID:2707
			// ��ת����byte����ʱ����ǿ��recycle�����thumb�����п���û������С���ƣ�����ֱ�ӷ��ظ�thumb����
			byte[] thumbData = bmpToByteArray(thumb, false);
			// ����õ�������ͼ�ļ�����������ƣ���Ҫ��һ������
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