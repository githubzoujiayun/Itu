package org.lance.itu.filebrowse;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.lance.itu.main.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/** Activity������ **/
public class FileActivityHelper {

	/** ��ȡһ���ļ����µ������ļ� **/
	public static ArrayList<FileInfo> getFiles(Activity activity, String path) {
		File f = null;
		File[] files = null;
		try { // ��ȡ�ļ�
			f = new File(path);
			files = f.listFiles();
			if (files == null) {
				Toast.makeText(activity,"�ļ����ܱ���:"+ path,
						Toast.LENGTH_SHORT).show();
				return null;
			}
		} catch (Exception ex) {
			Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_SHORT).show();
		}

		ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();
		// ��ȡ�ļ��б�
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			FileInfo fileInfo = new FileInfo();
			fileInfo.setFileName(file.getName());
			fileInfo.setDirectory(file.isDirectory());
			fileInfo.setFilePath(file.getPath());
			fileInfo.setFileSize(file.length());
			fileList.add(fileInfo);
		}

		// ����
		Collections.sort(fileList, new FileComparator());

		return fileList;
	}

	/** �������ļ��� **/
	public static void createDir(final Activity activity, final String path, final Handler hander) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.file_create, null);
		final EditText text = (EditText) layout.findViewById(R.id.file_name);
		builder.setView(layout);
		builder.setPositiveButton("ȷ��", new OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i) {
				String newName = text.getText().toString().trim();
				if (newName.length() == 0) {
					Toast.makeText(activity, "�ļ�������Ϊ��", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				String fullFileName = FileUtil.combinPath(path, newName);
				File newFile = new File(fullFileName);
				if (newFile.exists()) {
					Toast.makeText(activity, "�ļ����Ѿ�����", Toast.LENGTH_SHORT).show();
				} else {
					if (newFile.mkdir()) {
						hander.sendEmptyMessage(0); // �ɹ�
					} else {
						Toast.makeText(activity, "�ļ�����ʧ��~", Toast.LENGTH_SHORT)
								.show();
					}
				}
			}
		}).setNegativeButton("ȡ��", null);
		AlertDialog alertDialog = builder.create();
		alertDialog.setTitle("�����ļ���");
		alertDialog.show();
	}

	/** �������ļ� **/
	public static void renameFile(final Activity activity, final File f, final Handler hander) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.file_rename, null);
		final EditText text = (EditText) layout.findViewById(R.id.file_name);
		text.setText(f.getName());
		builder.setView(layout);
		builder.setPositiveButton("ȷ��", new OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i) {
				String path = f.getParentFile().getPath();
				String newName = text.getText().toString().trim();
				if (newName.equalsIgnoreCase(f.getName())) {
					return;
				}
				if (newName.length() == 0) {
					Toast.makeText(activity, "�ļ�������Ϊ��", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				String fullFileName = FileUtil.combinPath(path, newName);

				File newFile = new File(fullFileName);
				if (newFile.exists()) {
					Toast.makeText(activity,"�ļ����Ѿ�����", Toast.LENGTH_SHORT).show();
				} else {
					if (f.renameTo(newFile)) {
						hander.sendEmptyMessage(0); // �ɹ�
					} else {
						Toast.makeText(activity, "������ʧ��!", Toast.LENGTH_SHORT)
								.show();
					}
				}
			}
		}).setNegativeButton("ȡ��", null);
		AlertDialog alertDialog = builder.create();
		alertDialog.setTitle("�ļ�������");
		alertDialog.show();
	}

	/** �鿴�ļ����� **/
	public static void viewFileInfo(Activity activity, File f) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.file_info, null);
		FileInfo info = FileUtil.getFileInfo(f);

		((TextView) layout.findViewById(R.id.file_name)).setText(f.getName());
		((TextView) layout.findViewById(R.id.file_lastmodified)).setText(new Date(f.lastModified())
				.toLocaleString());
		((TextView) layout.findViewById(R.id.file_size))
				.setText(FileUtil.formetFileSize(info.getFileSize()));
		if (f.isDirectory()) {
			((TextView) layout.findViewById(R.id.file_contents)).setText("Folder "
					+ info.getFolderCount() + ", File " + info.getFileCount());
		}

		builder.setView(layout);
		builder.setPositiveButton("ȷ��", new OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i) {
				dialoginterface.cancel();
			}
		});
		AlertDialog alertDialog = builder.create();
		alertDialog.setTitle("�ļ���Ϣ");
		alertDialog.show();
	}
}
