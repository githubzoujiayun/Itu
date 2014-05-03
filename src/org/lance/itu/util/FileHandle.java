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
 * 读取图片文件工具
 * 
 * @author lance
 * 
 */
public class FileHandle {
	private final static String TAG = "FileHandle";
	//设置最大照片的尺寸
	private static final int BITMAP_MAX_WIDTH = 1280;// 最大宽度
	private static final int BITMAP_MAX_HEIGHT = 800;// 最大高度

	/** 创建制定包名的容器 */
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
	 * 从assets中抽取图片数据
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

	/** 加载图片缩放比例 */
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

	/* 加载Asset目录下的图片 dir为asset下的目录 */
	private static Bitmap optionAssetBitmap(Context context, String dir,
			String fileName) {
		return optionAssetBitmap(context, dir, fileName, 1);
	}

	// 预处理
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
	 * 加载指定路径的图片
	 * @param path
	 * @param dimen 为显示的尺寸
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
	
	/** 压缩图片 */
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
	 * 压缩指定位图的质量
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

	/** 指定宽高加载图片 */
	public static Bitmap loadBitmapFromPath(String path, int width, int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null---暂时不使用
		bitmap = BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
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
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeFile(path, options);
		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	// 从绝对路径中加载位图
	public static Bitmap loadBitmapFromPath(String path) {
		return loadBitmapFromPath(path, 1);
	}

	/**
	 * 通过资源加载图片
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

	/** 从raw中提取资源 */
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

	/** 使用相对路径载入墙纸大图文件 */
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

	/** 从sd卡安装不需要权限 */
	public static void installApk(Context context, String path) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(path)),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/** 从沙箱缓存目录安装apk需要修改文件权限 */
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
	 * 解压缩文件
	 * 
	 * @param zipFilePath
	 *            zip压缩文件路径
	 * @param destDirPath
	 *            指定解压缩目录
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
				// 会把目录作为一个file读出一次，所以只建立目录就可以，之下的文件还会被迭代到。
				if (entry.isDirectory()) {
					new File(destDirPath + entry.getName()).mkdirs();
					continue;
				}
				bis = new BufferedInputStream(zipFile.getInputStream(entry));
				File file = new File(destDirPath + entry.getName());
				// 加入这个的原因是zipfile读取文件是随机读取的，这就造成可能先读取一个文件
				// 而这个文件所在的目录还没有出现过，所以要建出目录来。
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
	 * 压缩指定目录文件成zip文件
	 * 
	 * @param zipDirPath
	 *            压缩目录路径
	 * @param destZipPath
	 *            输出zip文件路径
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
	 * 获取全部位图文件名
	 * 
	 * @param dir
	 *            目录
	 * @param ext
	 *            扩展名
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
	 * 获取指定目录和指定后缀名的所有文件
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
	 * 获取U盘路径 优派配置--->设备:VSD220 商标:ViewSonic 三星配置--->设备:p4notewifiww 商标:samsung
	 * 
	 * @return 根据设备品牌返回U盘路径---各个品牌U盘路径不一样
	 */
	private static String getUSBPath() {
		// String device=android.os.Build.DEVICE;
		String brand = android.os.Build.BRAND;
		if (brand.equals("ViewSonic")) {// 这里要处理两个USB接口
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

	/** 这里将asset文件通过流读取到cache目录然后安装 */
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
