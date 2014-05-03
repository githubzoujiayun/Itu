package org.lance.itu.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageUtil {
	
	/**
	 * 递归获取文件目录下的图片内容---文件名,路径 父文件名 父文件路径
	 * @param folder
	 */
	public static Map<String,List<Map<String,String>>> getFiles(File folder,
			Map<String,List<Map<String,String>>> album) {
		if(album==null){
			album=new HashMap<String, List<Map<String,String>>>();
		}
		File[] files = folder.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (file.isDirectory()) {
					getFiles(file,album);//递归获取图片文件
				}else if(ImageUtil.isImageFile(file.getName())){
					if (album.containsKey(folder.getAbsolutePath())) {//如果保存了该文件夹
						List<Map<String, String>> list = album.get(folder.getAbsolutePath());
						Map<String, String> map = new HashMap<String, String>();
						map.put("image_name", file.getName());
						map.put("image_path", file.getAbsolutePath());
						map.put("image_parent_name", folder.getName());
						map.put("image_parent_path", folder.getAbsolutePath());
						list.add(map);
					} else {
						List<Map<String, String>> list = new ArrayList<Map<String, String>>();
						Map<String, String> map = new HashMap<String, String>();
						map.put("image_name", file.getName());
						map.put("image_path", file.getAbsolutePath());
						map.put("image_parent_name", folder.getName());
						map.put("image_parent_path", folder.getAbsolutePath());
						list.add(map);
						album.put(folder.getAbsolutePath(), list);
					}
				}
			}
		}
		return album;
	}
	/** 判断是否为图片 */
	public static boolean isImageFile(String fName) {
		boolean re;
		String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();
		if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			re = true;
		} else {
			re = false;
		}
		return re;
	}
}
