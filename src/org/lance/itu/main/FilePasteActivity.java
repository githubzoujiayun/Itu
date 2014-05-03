package org.lance.itu.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lance.itu.adapter.FileAdapter;
import org.lance.itu.filebrowse.FileActivityHelper;
import org.lance.itu.filebrowse.FileInfo;
import org.lance.itu.filebrowse.FileUtil;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/** ճ���ļ� **/
public class FilePasteActivity extends ListActivity {
	private TextView _filePath;
	private List<FileInfo> _files;
	private String _rootPath = FileUtil.getSDPath();
	private String _currentPath = _rootPath;
	private final String TAG = "PasteFile";
	private String _currentPasteFilePath = "";
	private String _action = "";
	private ProgressDialog progressDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_paste);

		// ��ȡ��Intent���ݹ����Ĳ���
		Bundle bundle = getIntent().getExtras();
		_currentPasteFilePath = bundle.getString("CURRENTPASTEFILEPATH");
		_action = bundle.getString("ACTION");

		_filePath = (TextView) findViewById(R.id.file_path);

		// ���¼�
		((Button) findViewById(R.id.file_createdir)).setOnClickListener(fun_CreateDir);
		((Button) findViewById(R.id.paste)).setOnClickListener(fun_Paste);
		((Button) findViewById(R.id.cancel)).setOnClickListener(fun_Cancel);

		// Ĭ�ϻ�ȡ��Ŀ¼���ļ��б�
		viewFiles(_rootPath);
	}

	/** �б�����¼����� **/
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		FileInfo f = _files.get(position);

		if (f.isDirectory()) {
			viewFiles(f.getFilePath());
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
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/** ��ȡ��Ŀ¼�������ļ� **/
	private void viewFiles(String filePath) {
		ArrayList<FileInfo> tmp = FileActivityHelper.getFiles(FilePasteActivity.this, filePath);
		if (tmp != null) {
			// �������
			if (_files != null) {
				_files.clear();
			}

			_files = tmp;
			// ���õ�ǰĿ¼
			_currentPath = filePath;
			_filePath.setText(filePath);
			// ������
			setListAdapter(new FileAdapter(this, _files));
		}
	}

	/** �����ļ��лص�ί�� **/
	private final Handler createDirHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0)
				viewFiles(_currentPath);
		}
	};

	private Button.OnClickListener fun_CreateDir = new Button.OnClickListener() {
		public void onClick(View v) {
			FileActivityHelper.createDir(FilePasteActivity.this, _currentPath, createDirHandler);
		}
	};

	/**
	 * ��Handler������UI
	 */
	private final Handler progressHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// �ر�ProgressDialog
			progressDialog.dismiss();

			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("CURRENTPATH", _currentPath);
			intent.putExtras(bundle);
			setResult(Activity.RESULT_OK, intent);

			finish();
		}
	};

	private Button.OnClickListener fun_Paste = new Button.OnClickListener() {
		public void onClick(View v) {

			final File src = new File(_currentPasteFilePath);
			if (!src.exists()) {
				Toast.makeText(getApplicationContext(), "�ļ�������!", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			String newPath = FileUtil.combinPath(_currentPath, src.getName());
			final File tar = new File(newPath);
			if (tar.exists()) {
				Toast.makeText(getApplicationContext(),"�ļ��Ѿ�����!", Toast.LENGTH_SHORT)
						.show();
				return;
			}

			progressDialog = ProgressDialog.show(FilePasteActivity.this, "", "Please wait...", true, false);

			new Thread() {
				@Override
				public void run() {
					if ("MOVE".equals(_action)) { // �ƶ��ļ�
						try {
							FileUtil.moveFile(src, tar);
						} catch (Exception ex) {
							Log.e(TAG, "�ƶ��ļ�ʧ��!", ex);
							Toast.makeText(getApplicationContext(), ex.getMessage(),
									Toast.LENGTH_SHORT).show();
						}
					} else { // �����ļ�
						try {
							FileUtil.copyFile(src, tar);
						} catch (Exception ex) {
							Log.e(TAG, "�����ļ�ʧ��!", ex);
							Toast.makeText(getApplicationContext(), ex.getMessage(),
									Toast.LENGTH_SHORT).show();
						}
					}

					progressHandler.sendEmptyMessage(0);
				}
			}.start();
		}
	};

	private Button.OnClickListener fun_Cancel = new Button.OnClickListener() {
		public void onClick(View v) {
			setResult(Activity.RESULT_CANCELED);
			finish();
		}
	};
}
