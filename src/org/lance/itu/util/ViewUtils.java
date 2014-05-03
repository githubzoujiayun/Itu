package org.lance.itu.util;

import org.lance.itu.main.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;

/**
 * 动态创建view工具
 * 
 * @author lance
 * 
 */
public class ViewUtils {

	public static int getScreenWidth(Activity act) {
		Display dis = act.getWindowManager().getDefaultDisplay();
		return dis.getWidth();
	}

	public static int getScreenHeight(Activity act) {
		Display dis = act.getWindowManager().getDefaultDisplay();
		return dis.getHeight();
	}

	public static ImageView createImageView(Context context, int id, Bitmap src,int size,int margin) {
		ImageView image = new ImageView(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size,
				size);
		params.setMargins(margin, margin, margin, margin);
		image.setId(id);
		image.setLayoutParams(params);
		image.setScaleType(ScaleType.CENTER_CROP);
		image.setImageBitmap(src);
		return image;
	}
	
	/**
	 * 初始化一个popup窗口
	 * @param inflater
	 * @param layoutId
	 * @param isOutside
	 * @return
	 */
	public static PopupWindow initPopup(LayoutInflater inflater,int layoutId,int animStyle,boolean isOutside){
		View contentView = inflater.inflate(layoutId, null);
		PopupWindow popup = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT,true);
		popup.setAnimationStyle(animStyle);
		popup.setFocusable(true);
		popup.setTouchable(true);
		popup.setOutsideTouchable(isOutside);
		ColorDrawable color = new ColorDrawable(Color.TRANSPARENT);
		popup.setBackgroundDrawable(color);
		return popup;
	}
	
	/**
	 * 通过内容视图创建popup
	 * @param contentView
	 * @param isOutside
	 * @return
	 */
	public static PopupWindow initPopup(View contentView,int animStyle,boolean isOutside){
		PopupWindow popup = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT,true);
		popup.setAnimationStyle(animStyle);
		popup.setFocusable(true);
		popup.setTouchable(true);
		popup.setOutsideTouchable(isOutside);
		ColorDrawable color = new ColorDrawable(Color.TRANSPARENT);
		popup.setBackgroundDrawable(color);
		return popup;
	}
	/**
	 * 初始化指定宽度的弹出框
	 * @param contentView
	 * @param width
	 * @param animStyle
	 * @param isOutside
	 * @return
	 */
	public static PopupWindow initPopup(View contentView,int width,int animStyle,boolean isOutside){
		PopupWindow popup = new PopupWindow(contentView, width,
				LayoutParams.WRAP_CONTENT,true);
		popup.setAnimationStyle(animStyle);
		popup.setFocusable(true);
		popup.setTouchable(true);
		popup.setOutsideTouchable(isOutside);
		ColorDrawable color = new ColorDrawable(Color.TRANSPARENT);
		popup.setBackgroundDrawable(color);
		return popup;
	}

	public Button createButton(Context context, String str) {
		Button text = new Button(context);
		text.setText(str);
		text.setGravity(Gravity.CENTER);
		text.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		return text;
	}

}
