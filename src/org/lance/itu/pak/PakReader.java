package org.lance.itu.pak;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Vector;

/**
 * J2ME Pak�ļ���ȡ������ ���ܣ���Pak�ļ���ȡ��pngͼƬ������byte���飨������������Image���� ���Լ��ٰ�װ���Ŀռ��С
 * 
 * @author lance
 */
public class PakReader {
	public PakReader() {
	}

	/**
	 * �����ļ�λ�Ƶ���ʼ��
	 * 
	 * @return �ļ�λ�Ƶ���ʼ��
	 */
	private long workOutOffsetStart(PakHeader header) {
		// ������ļ�ͷ+�ļ�table�ĳ���
		return PakHeader.size() + header.getNumFileTableEntries()
				* PakTable.size();
	}

	/**
	 * ��DataInputStream��ȡchar����
	 * 
	 * @param dis
	 *            DataInputStream
	 * @param readLength
	 *            ��ȡ����
	 * @return char����
	 * @throws Exception
	 */
	private char[] readCharArray(DataInputStream dis, int readLength)
			throws Exception {
		char[] readCharArray = new char[readLength];
		for (int i = 0; i < readLength; i++) {
			readCharArray[i] = dis.readChar();
		}
		return readCharArray;
	}

	/**
	 * ��PAK�ļ��ж�ȡ�ļ�ͷ
	 * 
	 * @param dis
	 *            DataInputStream
	 * @return PakHeader
	 * @throws Exception
	 */
	private PakHeader readHeader(DataInputStream dis) throws Exception {
		PakHeader header = new PakHeader();
		char[] signature = readCharArray(dis, PakHeader.SIGNATURE_LENGTH);
		header.setSignature(signature);
		header.setVersion(dis.readFloat());
		header.setNumFileTableEntries(dis.readLong());
		header.setCipherAction(dis.readByte());
		header.setCipherValue(dis.readByte());
		char[] uniqueID = readCharArray(dis, PakHeader.UNIQUEID_LENGTH);
		header.setUniqueID(uniqueID);
		header.setReserved(dis.readLong());
		return header;
	}

	/**
	 * ��ȡ���е��ļ�table
	 * 
	 * @param dis
	 *            DataInputStream
	 * @param fileTableNumber
	 *            �ļ�������
	 * @return �ļ�table����
	 * @throws Exception
	 */
	private PakTable[] readFileTable(DataInputStream dis, int fileTableNumber)
			throws Exception {
		PakTable[] fileTable = new PakTable[fileTableNumber];
		for (int i = 0; i < fileTableNumber; i++) {
			PakTable ft = new PakTable();
			ft.setFileName(readCharArray(dis, PakTable.FILENAME_LENGTH));
			ft.setFileSize(dis.readLong());
			ft.setOffSet(dis.readLong());
			fileTable[i] = ft;
		}
		return fileTable;
	}

	/**
	 * ��pak�ļ���ȡ�ļ���byte����
	 * 
	 * @param dis
	 *            DataInputStream
	 * @param fileTable
	 *            PakFileTable
	 * @return byte����
	 * @throws Exception
	 */
	private byte[] readFileFromPak(DataInputStream dis, PakHeader header,
			PakTable fileTable) throws Exception {
		dis.skip(fileTable.getOffSet() - workOutOffsetStart(header));
		//
		int fileLength = (int) fileTable.getFileSize();
		byte[] fileBuff = new byte[fileLength];
		int readLength = dis.read(fileBuff, 0, fileLength);
		if (readLength < fileLength) {
			return null;
		} else {
			decryptBuff(fileBuff, readLength, header);
		}
		return fileBuff;
	}

	/**
	 * ʹ���ļ�ͷ�е���������ݽ��н���
	 * 
	 * @param buff
	 *            �����ܵ�����
	 * @param buffLength
	 *            ���ݵĳ���
	 * @param header
	 *            �ļ�ͷ
	 */
	private void decryptBuff(byte[] buff, int buffLength, PakHeader header) {
		for (int i = 0; i < buffLength; i++) {
			switch (header.getCipherAction()) {
			case PakHeader.ADDITION_CIPHERACTION:
				buff[i] -= header.getCipherValue();
				break;
			case PakHeader.SUBTRACT_CIHOERACTION:
				buff[i] += header.getCipherValue();
				break;
			}
		}
	}

	/**
	 * ��pak�ļ���ȡ��ָ����Pak�ļ�����Ϣ ȡ��ʱ��һ��Ԫ��Ϊpak�ļ�ͷ �ڶ���Ԫ��Ϊ�ļ�PakTable����
	 * 
	 * @param pakResourcePath
	 *            ��pak�ļ���Դ·��---��Ŀ�ļ��е����·��
	 * @return װ���ļ�ͷ���ļ�table�����Vector
	 * @throws Exception
	 */
	public Vector showPakFromRelativePath(String pakRelativePath)
			throws Exception {
		InputStream is = this.getClass().getResourceAsStream(pakRelativePath);
		DataInputStream dis = new DataInputStream(is);
		PakHeader header = readHeader(dis);
		PakTable[] fileTable = readFileTable(dis,
				(int) header.getNumFileTableEntries());
		Vector result = new Vector();
		result.addElement(header);
		result.addElement(fileTable);
		return result;
	}

	/**
	 * ��pak�ļ���ȡ��ָ����Pak�ļ�����Ϣ ȡ��ʱ��һ��Ԫ��Ϊpak�ļ�ͷ �ڶ���Ԫ��Ϊ�ļ�PakTable����
	 * 
	 * @param absolutePath
	 *            pak�ļ�����·��
	 * @return
	 * @throws Exception
	 */
	public Vector showPakFromAbsolutePath(String pakAbsolutePath)
			throws Exception {
		InputStream is = new FileInputStream(pakAbsolutePath);
		DataInputStream dis = new DataInputStream(is);
		PakHeader header = readHeader(dis);
		PakTable[] fileTable = readFileTable(dis,
				(int) header.getNumFileTableEntries());
		Vector result = new Vector();
		result.addElement(header);
		result.addElement(fileTable);
		return result;
	}

	/**
	 * ��ָ�������л�ȡ����
	 * 
	 * @param stream
	 * @return
	 * @throws Exception
	 */
	public Vector showPakFromStream(InputStream stream) throws Exception {
		DataInputStream dis = new DataInputStream(stream);
		PakHeader header = readHeader(dis);
		PakTable[] fileTable = readFileTable(dis,
				(int) header.getNumFileTableEntries());
		Vector result = new Vector();
		result.addElement(header);
		result.addElement(fileTable);
		return result;
	}
	
	/**
	 * ��pak�ļ���ȡ��ָ�����ļ���byte����
	 * 
	 * @param pakResourceURL
	 *            ��pak�ļ�����Ŀ�еĵ����·��
	 * @param extractResourceName
	 *            pak�ļ��н�Ҫ��ȡ�����ļ���
	 * @return byte����
	 * @throws Exception
	 */
	public byte[] extractResourceFromRelativePath(String pakResourcePath,
			String extractResourceName) throws Exception {
		InputStream is = this.getClass().getResourceAsStream(pakResourcePath);
		DataInputStream dis = new DataInputStream(is);
		PakHeader header = readHeader(dis);
		PakTable[] fileTable = readFileTable(dis,
				(int) header.getNumFileTableEntries());
		boolean find = false;
		int fileIndex = 0;
		for (int i = 0; i < fileTable.length; i++) {
			String fileName = new String(fileTable[i].getFileName()).trim();
			if (fileName.equals(extractResourceName)) {
				find = true;
				fileIndex = i;
				break;
			}
		}
		if (find == false) {
			return null;
		} else {
			byte[] buff = readFileFromPak(dis, header, fileTable[fileIndex]);
			return buff;
		}
	}

	/**
	 * ��pak����ȡָ���ļ���byte����
	 * 
	 * @param absoluteURL
	 *            pak�ļ�����·��
	 * @param resName
	 *            ��Դ��
	 * @return
	 * @throws Exception
	 */
	public byte[] extractResourceFromAbsolutePath(String absolutePath,
			String resName) throws Exception {
		InputStream is = new FileInputStream(absolutePath);
		DataInputStream dis = new DataInputStream(is);
		PakHeader header = readHeader(dis);
		PakTable[] fileTable = readFileTable(dis,
				(int) header.getNumFileTableEntries());
		boolean find = false;
		int fileIndex = 0;
		for (int i = 0; i < fileTable.length; i++) {
			String fileName = new String(fileTable[i].getFileName()).trim();
			if (fileName.equals(resName)) {
				find = true;
				fileIndex = i;
				break;
			}
		}
		if (find == false) {
			return null;
		} else {
			byte[] buff = readFileFromPak(dis, header, fileTable[fileIndex]);
			return buff;
		}
	}

	/**
	 * ��ָ����������ȡָ���ļ����ĵ���Դ
	 * 
	 * @param stream
	 *            ������
	 * @param resName
	 * @return
	 * @throws Exception
	 */
	public byte[] extractResourceFromStream(InputStream stream, String resName)
			throws Exception {
		DataInputStream dis = new DataInputStream(stream);
		PakHeader header = readHeader(dis);
		PakTable[] fileTable = readFileTable(dis,
				(int) header.getNumFileTableEntries());
		boolean find = false;
		int fileIndex = 0;
		for (int i = 0; i < fileTable.length; i++) {
			String fileName = new String(fileTable[i].getFileName()).trim();
			if (fileName.equals(resName)) {
				find = true;
				fileIndex = i;
				break;
			}
		}
		if (find == false) {
			return null;
		} else {
			byte[] buff = readFileFromPak(dis, header, fileTable[fileIndex]);
			return buff;
		}
	}

}