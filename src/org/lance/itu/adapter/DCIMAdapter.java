package org.lance.itu.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lance.itu.main.R;
import org.lance.itu.util.FileHandle;
import org.lance.itu.util.PhotoUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

public class DCIMAdapter extends BaseAdapter {

	private Context context;
	private List<Map<String,String>> mList;
	private List<String> mSelect = new ArrayList<String>();
	private int mScreenWidth;
	private LayoutParams params;
	
	public DCIMAdapter (Context context,List<Map<String,String>> mList,int width){
		this.context=context;
		this.mList=mList;
		mScreenWidth=width;
	}
	
	public List<String> getmSelect() {
		return mSelect;
	}

	public void setmSelect(List<String> mSelect) {
		this.mSelect = mSelect;
	}
	
	public void removeSelect(String position) {
		mSelect.remove(position);
		this.notifyDataSetChanged();
	}
	
	public void addSelect(String position) {
		mSelect.add(position);
		this.notifyDataSetChanged();
	}

	public void setmList(List<Map<String, String>> mList) {
		this.mList = mList;
		this.notifyDataSetChanged();
	}

	public int getCount() {
		return mList.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.album_activity_item, null);
			holder = new ViewHolder();
			holder.photo = (ImageView) convertView
					.findViewById(R.id.album_item_photo);
			// padding大小为40dip,计算px值
			int padding = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 40, context
							.getResources().getDisplayMetrics());
			// 设置显示的图片大小为屏幕宽度1/4大小
			params = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.width = (mScreenWidth - padding) / 4;
			params.height = (mScreenWidth - padding) / 4;
			holder.photo.setLayoutParams(params);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Bitmap bitmap=FileHandle.loadBitmapFromPath(mList.get(position).get("image_path"),params.width);
		if(bitmap!=null){
			holder.photo.setImageBitmap(bitmap);
		}else{
			holder.photo.setImageResource(R.drawable.miss_picture);
		}
		return convertView;
	}

	private class ViewHolder {
		ImageView photo;
	}
}
