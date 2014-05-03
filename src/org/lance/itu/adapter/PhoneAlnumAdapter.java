package org.lance.itu.adapter;

import java.util.List;
import java.util.Map;

import org.lance.itu.main.R;
import org.lance.itu.util.PhotoUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/** 手机相册 */
public class PhoneAlnumAdapter extends BaseAdapter {

	private Context context;
	private Map<String, List<Map<String, String>>> album;

	public PhoneAlnumAdapter(Context context,
			Map<String, List<Map<String, String>>> album) {
		this.context = context;
		this.album = album;
	}

	public int getCount() {
		return album.size();
	}

	public Object getItem(int position) {
		return album.keySet().toArray()[position];
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.phone_album_activity_item, null);
			holder = new ViewHolder();
			holder.photo = (ImageView) convertView
					.findViewById(R.id.phonealbum_item_photo);
			holder.name = (TextView) convertView
					.findViewById(R.id.phonealbum_item_name);
			holder.count = (TextView) convertView
					.findViewById(R.id.phonealbum_item_count);
			holder.dirpath = (TextView) convertView
					.findViewById(R.id.phonealbum_item_dirpath);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		List<Map<String, String>> list = album
				.get(album.keySet().toArray()[position]);
		Bitmap bitmap=PhotoUtil.getImageThumbnail(list.get(0)
				.get("image_path"), 70, 70);
		if(bitmap!=null){
			holder.photo.setImageBitmap(bitmap);
		}else{
			holder.photo.setImageResource(R.drawable.miss_picture);
		}
		
		holder.name.setText(list.get(0).get("image_parent_name"));
		holder.count.setText("(" + list.size() + ")");
		holder.dirpath.setText(list.get(0).get("image_parent_path"));
		return convertView;
	}

	private class ViewHolder {
		ImageView photo;
		TextView name;
		TextView count;
		TextView dirpath;// 文件夹路径
	}
}
