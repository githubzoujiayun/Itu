package org.lance.itu.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lance.itu.adapter.AlbumAdapter;
import org.lance.itu.util.ImageUtil;
import org.lance.itu.util.Prefs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

/**
 * 用户手机文件夹具体图片显示类
 * 
 * @author lance
 * 
 */
public class AlbumActivity extends BaseActivity {
	private Button mBack;
	private Button mAll;
	private GridView mDisplay;
	private TextView mCount;
	private Button mDetermine;
	private AlbumAdapter mAdapter;
	private List<Map<String, String>> mList = new ArrayList<Map<String, String>>();

	public void onCreate(Bundle saved){
		super.onCreate(saved);
		setContentView(R.layout.album_activity);
		findViewById();
		setListener();
		final String path = getIntent().getStringExtra("path");
		new Thread() {
			public void run() {
				Map<String, List<Map<String, String>>> album = ImageUtil
						.getFiles(new File(path), null);
				Set<String> keys = album.keySet();
				for (String key : keys) {
					mList=album.get(key);
					mAdapter = new AlbumAdapter(AlbumActivity.this, mList,mScreenWidth);
					initCount(mAdapter.getmSelect().size());
					mDisplay.setAdapter(mAdapter);
					break;
				}
			}
		}.start();
	}

	private void findViewById() {
		mBack = (Button) findViewById(R.id.album_back);
		mAll = (Button) findViewById(R.id.album_all);
		mDisplay = (GridView) findViewById(R.id.album_display);
		mCount = (TextView) findViewById(R.id.album_count);
		mDetermine = (Button) findViewById(R.id.album_determine);
	}

	private void setListener() {
		mBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		mAll.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mAdapter.getmSelect().size() == mList.size()) {
					mAdapter.getmSelect().clear();
				} else if (mAdapter.getmSelect().size() > 0) {
					for (int i = 0; i < mList.size(); i++) {
						if (mAdapter.getmSelect().contains(String.valueOf(i))) {
							continue;
						}
						mAdapter.getmSelect().add(String.valueOf(i));
					}
				} else {
					for (int i = 0; i < mList.size(); i++) {
						mAdapter.getmSelect().add(String.valueOf(i));
					}
				}
				if (mAdapter.getmSelect().size() == mList.size()) {
					mAll.setText("反选");
				} else {
					mAll.setText("全选");
				}
				initCount(mAdapter.getmSelect().size());
				mAdapter.notifyDataSetChanged();
			}
		});
		mDisplay.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if (mAdapter.getmSelect().contains(String.valueOf(position))) {
					mAdapter.removeSelect(String.valueOf(position));
				} else {
					mAdapter.addSelect(String.valueOf(position));
				}
				if (mAdapter.getmSelect().size() == mList.size()) {
					mAll.setText("反选");
				} else {
					mAll.setText("全选");
				}
				initCount(mAdapter.getmSelect().size());
				mAdapter.notifyDataSetChanged();
			}
		});
		mDetermine.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(AlbumActivity.this,ImageFilterActivity.class);
				// 传递当前选中的图片的地址
				intent.putExtra("path",mList.get(Integer.parseInt(mAdapter.getmSelect().get(
								0))).get("image_path"));
				startActivity(intent);
				finish();
			}
		});
	}

	/**
	 * 更新界面显示数量内容
	 * 
	 * @param count
	 *            选中的图片数量
	 */
	private void initCount(int count) {
		// 更新选择的图片数量,如果数量大于0,设置确定按钮为可用,反之不可用并修改字体颜色
		if (count > 0) {
			mCount.setText("已选择" + count + "张");
			mCount.setTextColor(0xFFFFFFFF);
			mDetermine.setTextColor(0xFFFFFFFF);
			mDetermine.setEnabled(true);
			mDetermine.setClickable(true);
		} else {
			mCount.setText("已选择0张");
			mCount.setTextColor(0xFF999999);
			mDetermine.setTextColor(0xFF999999);
			mDetermine.setEnabled(false);
			mDetermine.setClickable(false);
		}
	}

}
