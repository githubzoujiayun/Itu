package org.lance.itu.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.lance.itu.pak.PakReader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * ��ȡͼƬ�ļ�����
 * 
 * @author lance
 * 
 */
public class FileHandle {
	private final static String TAG = "FileHandle";
	//���������Ƭ�ĳߴ�
	private static final int BITMAP_MAX_WIDTH = 1280;// �����
	private static final int BITMAP_MAX_HEIGHT = 800;// ���߶�

	/** �����ƶ����������� */
	public static Context createContext(Context context) {
		Context ctx = null;
		try {
			ctx = context.createPackageContext(context.getPackageName()
					+ "_images", 0);
		} catch (NameNotFoundException e) {
			// e.printStackTrace();
		}
		return ctx;
	}

	/**
	 * ��assets�г�ȡͼƬ����
	 * 
	 * @param context
	 * @param fileName
	 * @param height
	 * @return
	 */
	public static Bitmap loadWalllPakBitmap(Context context, String fileName) {
		PakReader reader = new PakReader();
		InputStream is = null;
		Bitmap bitmap = null;
		try {
			String temp = "walll/walll.pak";
			is = context.getAssets().open(temp);
			if (is == null) {
				return null;
			}
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			byte[] brr = reader.extractResourceFromStream(is, fileName);
			bitmap = BitmapFactory.decodeByteArray(brr, 0, brr.length, options);
			options = prepareOptions(options);
			options.inJustDecodeBounds = false;
			options.inPurgeable = true;
			bitmap = BitmapFactory.decodeByteArray(brr, 0, brr.length, options);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	/** ����ͼƬ���ű��� */
	private static Bitmap optionAssetBitmap(Context context, String dir,
			String fileName, int dimen) {
		InputStream is = null;
		Bitmap bitmap = null;
		try {
			String temp = dir + fileName;
			is = context.getAssets().open(temp);
			if (is == null) {
				return null;
			}
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			bitmap = BitmapFactory.decodeStream(is, null, options);
			options = prepareOptions(options);
			if (dimen <= 1) {
				options.inSampleSize = 1;
			} else {
				int factor = options.outWidth / dimen;
				options.inSampleSize = factor < 1 ? 1 : factor;
			}
			options.inJustDecodeBounds = false;
			options.inPurgeable = true;
			bitmap = BitmapFactory.decodeStream(is, null, options);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	/* ����AssetĿ¼�µ�ͼƬ dirΪasset�µ�Ŀ¼ */
	private static Bitmap optionAssetBitmap(Context context, String dir,
			String fileName) {
		return optionAssetBitmap(context, dir, fileName, 1);
	}

	// Ԥ����
	private static BitmapFactory.Options prepareOptions(
			BitmapFactory.Options options) {
		float scaleWidthFactor = options.outWidth / BITMAP_MAX_WIDTH;
		float scaleHeightFactor = options.outWidth / BITMAP_MAX_HEIGHT;
		float factor = scaleWidthFactor > scaleHeightFactor ? scaleWidthFactor
				: scaleHeightFactor;
		options.inSampleSize = (int) (factor + 1);
		return options;
	}

	/**
	 * ����ָ��·����ͼƬ
	 * @param path
	 * @param dimen Ϊ��ʾ�ĳߴ�
	 * @return
	 */
	public static Bitmap loadBitmapFromPath(String path, int dimen) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		options = prepareOptions(options);
		if (options.outWidth <= dimen) {
			options.inSampleSize = 1;
		} else if(dimen==1){
			
		} else {
			int factor = options.outWidth / dimen;
			options.inSampleSize = factor + 1;
		}
		options.inJustDecodeBounds = false;
		options.inPurgeable = true;
		bitmap = BitmapFactory.decodeFile(path, options);
		return bitmap;
	}
	
	/** ѹ��ͼƬ */
	public static Bitmap comp(Bitmap image,int width,int height) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {
			baos.reset();
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		int be = 1;
		if (w > h && w > width) {
			be = (int) (newOpts.outWidth / width);
		} else if (w < h && h > height) {
			be = (int) (newOpts.outHeight / height);
		}
		if (be <= 0){
			be = 1;
		}
		newOpts.inSampleSize = be;
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap,BITMAP_MAX_WIDTH*BITMAP_MAX_HEIGHT);
	}
	
	/**
	 * ѹ��ָ��λͼ������
	 * @param image
	 * @return
	 */
	public static Bitmap compressImage(Bitmap image,int size) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		while (baos.toByteArray().length > size) {
			baos.reset();
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);
			options -= 10;
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		return BitmapFactory.decodeStream(isBm, null, null);
	}

	/** ָ����߼���ͼƬ */
	public static Bitmap loadBitmapFromPath(String path, int width, int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// ��ȡ���ͼƬ�Ŀ�͸ߣ�ע��˴���bitmapΪnull---��ʱ��ʹ��
		bitmap = BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false; // ��Ϊ false
		// �������ű�
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		// ���¶���ͼƬ����ȡ���ź��bitmap��ע�����Ҫ��options.inJustDecodeBounds ��Ϊ false
		bitmap = BitmapFactory.decodeFile(path, options);
		// ����ThumbnailUtils����������ͼ������Ҫָ��Ҫ�����ĸ�Bitmap����
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	// �Ӿ���·���м���λͼ
	public static Bitmap loadBitmapFromPath(String path) {
		return loadBitmapFromPath(path, 1);
	}

	/**
	 * ͨ����Դ����ͼƬ
	 * 
	 * @param context
	 * @param resId
	 * @param height
	 * @return
	 */
	public static Bitmap loadResBitmap(Context context, int resId) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				resId, options);
		options = prepareOptions(options);
		options.inJustDecodeBounds = false;
		options.inPurgeable = true;
		bitmap = BitmapFactory.decodeResource(context.getResources(), resId,
				options);
		return bitmap;
	}

	/** ��raw����ȡ��Դ */
	public static Bitmap loadWalllPakBitmap(Context context, int rawId,
			String fileName) {
		PakReader reader = new PakReader();
		Bitmap bitmap = null;
		InputStream in = context.getResources().openRawResource(rawId);
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			byte[] brr = reader.extractResourceFromStream(in, fileName);
			bitmap = BitmapFactory.decodeByteArray(brr, 0, brr.length, options);
			options = prepareOptions(options);
			options.inJustDecodeBounds = false;
			options.inPurgeable = true;
			bitmap = BitmapFactory.decodeByteArray(brr, 0, brr.length, options);
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				reader = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	/** ʹ�����·������ǽֽ��ͼ�ļ� */
	public static Bitmap loadWalllPakBitmap(String fileName) {
		PakReader reader = new PakReader();
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			byte[] data = reader.extractResourceFromRelativePath("/walll.pak",
					fileName);
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
					options);
			options = prepareOptions(options);
			options.inJustDecodeBounds = false;
			options.inPurgeable = true;
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
					options);
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			reader = null;
		}
		return bitmap;
	}

	/** ��sd����װ����ҪȨ�� */
	public static void installApk(Context context, String path) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(path)),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/** ��ɳ�仺��Ŀ¼��װapk��Ҫ�޸��ļ�Ȩ�� */
	public static void installApkFromCache(Context context, String cachePath) {
		try {
			String command = "chmod 777 " + cachePath;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.parse("file://" + cachePath),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * ��ѹ���ļ�
	 * 
	 * @param zipFilePath
	 *            zipѹ���ļ�·��
	 * @param destDirPath
	 *            ָ����ѹ��Ŀ¼
	 * @return
	 */
	public static int unZipFile(String zipFilePath, String destDirPath) {
		int buffer = 2048;
		ZipFile zipFile = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		if (!destDirPath.endsWith("/")) {
			destDirPath += "/";
		}
		try {
			zipFile = new ZipFile(zipFilePath);
			Enumeration emu = zipFile.entries();
			int i = 0;
			while (emu.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) emu.nextElement();
				// ���Ŀ¼��Ϊһ��file����һ�Σ�����ֻ����Ŀ¼�Ϳ��ԣ�֮�µ��ļ����ᱻ��������
				if (entry.isDirectory()) {
					new File(destDirPath + entry.getName()).mkdirs();
					continue;
				}
				bis = new BufferedInputStream(zipFile.getInputStream(entry));
				File file = new File(destDirPath + entry.getName());
				// ���������ԭ����zipfile��ȡ�ļ��������ȡ�ģ������ɿ����ȶ�ȡһ���ļ�
				// ������ļ����ڵ�Ŀ¼��û�г��ֹ�������Ҫ����Ŀ¼����
				File parent = file.getParentFile();
				if (parent != null && (!parent.exists())) {
					parent.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(file);
				bos = new BufferedOutputStream(fos, buffer);
				int count;
				byte data[] = new byte[buffer];
				while ((count = bis.read(data, 0, buffer)) != -1) {
					bos.write(data, 0, count);
				}
				bos.flush();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		} finally {
			try {
				if (zipFile != null)
					zipFile.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (bis != null)
					bis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (bos != null)
					bos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 1;
	}

	/**
	 * ѹ��ָ��Ŀ¼�ļ���zip�ļ�
	 * 
	 * @param zipDirPath
	 *            ѹ��Ŀ¼·��
	 * @param destZipPath
	 *            ���zip�ļ�·��
	 * @return
	 */
	public static int zipFile(String zipDirPath, String destZipPath) {
		int buffer = 2048;
		BufferedInputStream origin = null;
		ZipOutputStream out = null;
		try {
			FileOutputStream dest = new FileOutputStream(destZipPath);
			out = new ZipOutputStream(new BufferedOutputStream(dest));
			byte data[] = new byte[buffer];
			File f = new File(zipDirPath);
			File files[] = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				FileInputStream fi = new FileInputStream(files[i]);
				origin = new BufferedInputStream(fi, buffer);
				ZipEntry entry = new ZipEntry(files[i].getName());
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, buffer)) != -1) {
					out.write(data, 0, count);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		} finally {
			try {
				if (origin != null)
					origin.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 1;
	}

	/**
	 * ��ȡȫ��λͼ�ļ���
	 * 
	 * @param dir
	 *            Ŀ¼
	 * @param ext
	 *            ��չ��
	 */
	public static ArrayList<String> listImageNames(File dir, String ext) {
		ArrayList<String> fileNames = new ArrayList<String>();
		ArrayList<File> files = listFile(dir, ext);
		for (int i = 0; i < files.size(); i++) {
			fileNames.add(files.get(i).getAbsolutePath());
		}
		return fileNames;
	}

	/**
	 * ��ȡָ��Ŀ¼��ָ����׺���������ļ�
	 * 
	 * @param dir
	 * @param ext
	 * @return
	 */
	public static ArrayList<File> listFile(File dir, final String ext) {
		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				String name = pathname.getName();
				boolean bool = name.endsWith(ext.toLowerCase())
						| name.endsWith(ext.toUpperCase());
				return pathname.isFile() && bool;
			}
		});
		return new ArrayList<File>(Arrays.asList(files));
	}

	/**
	 * ��ȡU��·�� ��������--->�豸:VSD220 �̱�:ViewSonic ��������--->�豸:p4notewifiww �̱�:samsung
	 * 
	 * @return �����豸Ʒ�Ʒ���U��·��---����Ʒ��U��·����һ��
	 */
	private static String getUSBPath() {
		// String device=android.os.Build.DEVICE;
		String brand = android.os.Build.BRAND;
		if (brand.equals("ViewSonic")) {// ����Ҫ��������USB�ӿ�
			String usbPath = "/mnt/ext_usb1";
			File dir = new File(usbPath);
			if (dir.listFiles() != null) {
				return usbPath;
			} else {
				return "/mnt/ext_usb2";
			}
		} else if (brand.equals("samsung")) {
			return "/mnt/UsbDriveA";
		}
		return null;
	}

	/** ���ｫasset�ļ�ͨ������ȡ��cacheĿ¼Ȼ��װ */
	public static boolean retrieveApkFromAssets(Context context,
			String fileName, String path) {
		boolean bRet = false;
		try {
			InputStream is = context.getAssets().open(fileName);
			File file = new File(path);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);

			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}

			fos.close();
			is.close();

			bRet = true;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return bRet;
	}

	public String getRealPathFromURI(Activity act, Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = act.managedQuery(contentUri, proj, null, null, null);
		cursor.moveToFirst();
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		return cursor.getString(column_index);
	}
}
