package org.lance.itu.filebrowse;

import java.util.Comparator;


/** ���� **/
public class FileComparator implements Comparator<FileInfo> {

	public int compare(FileInfo file1, FileInfo file2) {
		// �ļ�������ǰ��
		if (file1.isDirectory() && !file2.isDirectory()) {
			return -1000;
		} else if (!file1.isDirectory() && file2.isDirectory()) {
			return 1000;
		}
		// ��ͬ���Ͱ���������
		return file1.getFileName().compareTo(file2.getFileName());
	}
}