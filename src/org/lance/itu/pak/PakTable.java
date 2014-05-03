package org.lance.itu.pak;

/**
 * Pak�ļ�table�� �ļ�table�ṹ�� �ļ�����30�ֽ�char���� �ļ���С��32λ���� �ļ���pak�ļ��е�λ�ƣ�32λ����
 * 
 * @author lance
 */
public class PakTable {
	public static final int FILENAME_LENGTH = 30;
	// �ļ���
	private char[] fileName = new char[FILENAME_LENGTH];
	// �ļ���С
	private long fileSize = 0L;
	// �ļ���pak�ļ��е�λ��
	private long offSet = 0L;

	public PakTable() {
	}

	/**
	 * ���췽��
	 * 
	 * @param fileName
	 *            �ļ���
	 * @param fileSize
	 *            �ļ���С
	 * @param offSet
	 *            �ļ���Pak�ļ��е�λ��
	 */
	public PakTable(char[] fileName, long fileSize, long offSet) {
		for (int i = 0; i < FILENAME_LENGTH; this.fileName[i] = fileName[i], i++)
			;
		this.fileSize = fileSize;
		this.offSet = offSet;
	}

	public char[] getFileName() {
		return fileName;
	}

	public void setFileName(char[] fileName) {
		for (int i = 0; i < fileName.length; this.fileName[i] = fileName[i], i++)
			;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public long getOffSet() {
		return offSet;
	}

	public void setOffSet(long offSet) {
		this.offSet = offSet;
	}

	/**
	 * �����ļ�Table�Ĵ�С
	 * 
	 * @return �����ļ�Table�Ĵ�С
	 */
	public static int size() {
		return FILENAME_LENGTH + 4 + 4;
	}

	public String toString() {
		return "\tfileName:" + new String(this.fileName).trim()
				+ "\tfileLength:" + this.fileSize + "\tfileOffset:"
				+ this.offSet;
	}
}