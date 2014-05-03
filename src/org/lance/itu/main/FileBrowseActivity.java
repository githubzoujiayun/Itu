package org.lance.itu.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lance.itu.adapter.FileAdapter;
import org.lance.itu.filebrowse.FileActivityHelper;
import org.lance.itu.filebrowse.FileInfo;
import org.lance.itu.filebrowse.FileUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;

/**
 * �ļ������ͼ
 * @author lance
 *
 */
public class FileBrowseActivity extends ListActivity {
	private TextView _filePath;
	private List<FileInfo> _files;
	private String _rootPath = FileUtil.getSDPath();
	private String _currentPath = _rootPath;
	private final String TAG = "Main";
	private final int MENU_RENAME = Menu.FIRST;
	private final int MENU_COPY = Menu.FIRST + 3;
	private final int MENU_MOVE = Menu.FIRST + 4;
	private final int MENU_DELETE = Menu.FIRST + 5;
	private final int MENU_INFO = Menu.FIRST + 6;
	
	private int command=0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browse_main);
		_filePath = (TextView) findViewById(R.id.file_path);
		// ��ȡ��ǰĿ¼���ļ��б�
		viewFiles(_currentPath);
		// ע�������Ĳ˵�
		registerForContextMenu(getListView());
		command=getIntent().getIntExtra("COMMAND", 1);
		setResult(-1);
		
	}

	/** �����Ĳ˵� **/
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterView.AdapterContextMenuInfo info = null;

		try {
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		} catch (ClassCastException e) {
			Log.e(TAG, "bad menuInfo", e);
			return;
		}

		FileInfo f = _files.get(info.position);
		menu.setHeaderTitle(f.getFileName());
		menu.add(0, MENU_RENAME, 1, "������");
		menu.add(0, MENU_COPY, 2, "�����ļ�");
		menu.add(0, MENU_MOVE, 3, "�ƶ��ļ�");
		menu.add(0, MENU_DELETE, 4, "ɾ���ļ�");
		menu.add(0, MENU_INFO, 5, "�ļ�����");
	}

	/** �����Ĳ˵��¼����� **/
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		FileInfo fileInfo = _files.get(info.position);
		File f = new File(fileInfo.getFilePath());
		switch (item.getItemId()) {
		case MENU_RENAME:
			FileActivityHelper.renameFile(FileBrowseActivity.this, f, renameFileHandler);
			return true;
		case MENU_COPY:
			pasteFile(f.getPath(), "COPY");
			return true;
		case MENU_MOVE:
			pasteFile(f.getPath(), "MOVE");
			return true;
		case MENU_DELETE:
			FileUtil.deleteFile(f);
			viewFiles(_currentPath);
			return true;
		case MENU_INFO:
			FileActivityHelper.viewFileInfo(FileBrowseActivity.this, f);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	/** �б�����¼����� **/
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		FileInfo f = _files.get(position);
		if (f.isDirectory()) {
			viewFiles(f.getFilePath());
		} else {
			//openFile(f.Path);//�������¶������ļ�����Ӧ�¼�
			String path=f.getFilePath();
			Intent data=new Intent();
			data.putExtra("PATH",path);
			setResult(command, data);
			finish();
		}
	}

	/** �ض��巵�ؼ��¼� **/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// ����back����
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			File f = new File(_currentPath);
			String parentPath = f.getParent();
			if (parentPath != null) {
				viewFiles(parentPath);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/** ��ȡ��PasteFile���ݹ�����·�� **/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (Activity.RESULT_OK == resultCode) {
			Bundle bundle = data.getExtras();
			if (bundle != null && bundle.containsKey("CURRENTPATH")) {
				viewFiles(bundle.getString("CURRENTPATH"));
			}
		}
	}

	/** �����˵� **/
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.browse_menu, menu);
		return true;
	}

	/** �˵��¼� **/
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mainmenu_home:
			viewFiles(_rootPath);
			break;
		case R.id.mainmenu_refresh:
			viewFiles(_currentPath);
			break;
		case R.id.mainmenu_createdir:
			FileActivityHelper.createDir(FileBrowseActivity.this, _currentPath, createDirHandler);
			break;
		case R.id.mainmenu_exit:
			exit();
			break;
		default:
			break;
		}
		return true;
	}

	/** ��ȡ��Ŀ¼�������ļ� **/
	private void viewFiles(String filePath) {
		ArrayList<FileInfo> tmp = FileActivityHelper.getFiles(FileBrowseActivity.this, filePath);
		if (tmp != null) {
			if (_files != null) {
				_files.clear();
			}
			_files = tmp;
			_currentPath = filePath;
			_filePath.setText(filePath);
			setListAdapter(new FileAdapter(this, _files));
		}
	}

	/** �����¼����� **/
	/**
	 * private OnItemLongClickListener _onItemLongClickListener = new
	 * OnItemLongClickListener() {
	 * 
	 * @Override public boolean onItemLongClick(AdapterView<?> parent, View
	 *           view, int position, long id) { Log.e(TAG, "position:" +
	 *           position); return true; } };
	 **/

	/** ���ļ� **/
	private void openFile(String path) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);

		File f = new File(path);
		String type = FileUtil.getMIMEType(f.getName());
		intent.setDataAndType(Uri.fromFile(f), type);
		startActivity(intent);
	}

	/** �������ص�ί�� **/
	private final Handler renameFileHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0)
				viewFiles(_currentPath);
		}
	};

	/** �����ļ��лص�ί�� **/
	private final Handler createDirHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0)
				viewFiles(_currentPath);
		}
	};

	/** ճ���ļ� **/
	private void pasteFile(String path, String action) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("CURRENTPASTEFILEPATH", path);
		bundle.putString("ACTION", action);
		intent.putExtras(bundle);
		intent.setClass(FileBrowseActivity.this, FilePasteActivity.class);
		// ��һ��Activity���ȴ����
		startActivityForResult(intent, 0);
	}

	/** �˳����� **/
	private void exit() {
		new AlertDialog.Builder(FileBrowseActivity.this).setMessage("��ȷ��Ҫ�˳���?").setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						FileBrowseActivity.this.finish();
						android.os.Process.killProcess(android.os.Process.myPid());
						System.exit(0);
					}
				}).setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).show();
	}
}
