package org.lance.itu.adapter;

import java.util.List;

import org.lance.itu.main.R;
import org.lance.itu.util.FileHandle;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingAdapter extends BaseAdapter {
	private Context context;
	private List<String> data;
	
	public SettingAdapter (Context context,List<String> data){
		this.context=context;
		this.data=data;
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.setting_option_list_item, null);
			holder.menu = (TextView) convertView
					.findViewById(R.id.setting_option_item_button);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.menu.setText(data.get(position));
		return convertView;
	}

	private class ViewHolder {
		TextView menu;
	}
}
