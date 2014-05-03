package org.lance.itu.pak;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

/**
 * J2SE pakѹ���Ͷ�ȡ����---����ȡ���ļ�ϵͳ��
 * 
 * @author lance
 * 
 */
public class PakWriter {
	public final static String SIGNATURE = "012345";
	public final static String UUID = "0123456789";

	public PakWriter() {
	}

	/**
	 * �����ļ�����
	 * 
	 * @param filePath
	 *            �ļ�·��
	 * @return �ļ�����
	 */
	private long getFileSize(String filePath) {
		File file = new File(filePath);
		return file.length();
	}

	/**
	 * �����ļ���
	 * 
	 * @param filePath
	 *            �ļ�·��
	 * @return �ļ���
	 */

	private String getFileName(String filePath) {
		File file = new File(filePath);
		return file.getName();
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
	 * �����ļ�λ��
	 * 
	 * @param fileIndex
	 *            �ļ����
	 * @param lastFileOffset
	 *            ��һ���ļ�λ��
	 * @return���ļ���pak�ļ��е�λ��
	 */
	private long workOutNextOffset(long sourceFileSize, long lastFileOffset) {
		return lastFileOffset + sourceFileSize;
	}

	/**
	 * �����ļ�table
	 * 
	 * @param sourceFileName
	 *            Դ�ļ���
	 * @param sourceFileSize
	 *            Դ�ļ�����
	 * @param currentFileOffset
	 *            ��ǰ�ļ�λ��
	 * @return ���ɵ�PakFileTable����
	 */
	private PakTable generateFileTable(String sourceFileName,
			long sourceFileSize, long currentFileOffset) {
		PakTable ft = new PakTable();
		ft.setFileName(sourceFileName.toCharArray());
		ft.setFileSize(sourceFileSize);
		ft.setOffSet(currentFileOffset);
		return ft;
	}

	/**
	 * ��char�ַ�����д�뵽DataOutputStream��
	 * 
	 * @param toWriteCharArray
	 *            ��д���char����
	 * @param dos
	 *            DataOutputStream
	 * @throws Exception
	 */
	private void writeCharArray(char[] toWriteCharArray, DataOutputStream dos)
			throws Exception {
		for (int i = 0; i < toWriteCharArray.length; dos
				.writeChar(toWriteCharArray[i]), i++)
			;
	}

	/**
	 * ʹ���ļ�ͷ�е���������ݽ��м���
	 * 
	 * @param buff
	 *            �����ܵ�����
	 * @param buffLength
	 *            ���ݵĳ���
	 * @param header
	 *            �ļ�ͷ
	 */
	private void encryptBuff(byte[] buff, int buffLength, PakHeader header) {
		for (int i = 0; i < buffLength; i++) {
			switch (header.getCipherAction()) {
			case PakHeader.ADDITION_CIPHERACTION:
				buff[i] += header.getCipherValue();
				break;
			case PakHeader.SUBTRACT_CIHOERACTION:
				buff[i] -= header.getCipherValue();
				break;
			}
		}
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
	 * ����Pak�ļ�
	 * 
	 * @param sourceFilePath
	 *            Դ�ļ�·������
	 * @param destinateFilePath
	 *            Ŀ���ļ�·����Pak�ļ���
	 * @param cipherAction
	 *            ������Ϊ
	 * @param cipherValue
	 *            ����
	 * @throws Exception
	 */
	public void makePakFile(String[] sourceFilePath, String destinateFilePath,
			PakHeader header) throws Exception {
		PakTable[] fileTable = new PakTable[sourceFilePath.length];
		// �����ļ�λ����ʼ��
		long fileOffset = workOutOffsetStart(header);
		// ��������ļ�table
		for (int i = 0; i < sourceFilePath.length; i++) {
			String sourceFileName = getFileName(sourceFilePath[i]);
			long sourceFileSize = getFileSize(sourceFilePath[i]);
			PakTable ft = generateFileTable(sourceFileName, sourceFileSize,
					fileOffset);
			// ������һ���ļ�λ��
			fileOffset = workOutNextOffset(sourceFileSize, fileOffset);
			fileTable[i] = ft;
		}
		// д���ļ�ͷ
		File wFile = new File(destinateFilePath);
		FileOutputStream fos = new FileOutputStream(wFile);
		DataOutputStream dos = new DataOutputStream(fos);
		writeCharArray(header.getSignature(), dos);
		dos.writeFloat(header.getVersion());
		dos.writeLong(header.getNumFileTableEntries());
		dos.writeByte(header.getCipherAction());
		dos.writeByte(header.getCipherValue());
		writeCharArray(header.getUniqueID(), dos);
		dos.writeLong(header.getReserved());
		// д���ļ�table
		for (int i = 0; i < fileTable.length; i++) {
			writeCharArray(fileTable[i].getFileName(), dos);
			dos.writeLong(fileTable[i].getFileSize());
			dos.writeLong(fileTable[i].getOffSet());
		}
		// д���ļ�����
		for (int i = 0; i < fileTable.length; i++) {
			File ftFile = new File(sourceFilePath[i]);
			FileInputStream ftFis = new FileInputStream(ftFile);
			DataInputStream ftDis = new DataInputStream(ftFis);
			byte[] buff = new byte[256];
			int readLength = 0;
			while ((readLength = ftDis.read(buff)) != -1) {
				encryptBuff(buff, readLength, header);
				dos.write(buff, 0, readLength);
			}
			ftDis.close();
			ftFis.close();
		}
		dos.close();
	}

	/**
	 * ����pakѹ���ļ�
	 * 
	 * @param files
	 * @param destinateFilePath
	 * @param header
	 * @throws Exception
	 */
	public void makePakFile(ArrayList<File> files, String destinateFilePath,
			PakHeader header) throws Exception {
		PakTable[] fileTable = new PakTable[files.size()];
		// �����ļ�λ����ʼ��
		long fileOffset = workOutOffsetStart(header);
		// ��������ļ�table
		for (int i = 0; i < files.size(); i++) {
			File file = files.get(i);
			PakTable ft = generateFileTable(file.getName(), file.length(),
					fileOffset);
			// ������һ���ļ�λ��
			fileOffset = workOutNextOffset(file.length(), fileOffset);
			fileTable[i] = ft;
		}
		// д���ļ�ͷ
		File wFile = new File(destinateFilePath);
		FileOutputStream fos = new FileOutputStream(wFile);
		DataOutputStream dos = new DataOutputStream(fos);
		writeCharArray(header.getSignature(), dos);
		dos.writeFloat(header.getVersion());
		dos.writeLong(header.getNumFileTableEntries());
		dos.writeByte(header.getCipherAction());
		dos.writeByte(header.getCipherValue());
		writeCharArray(header.getUniqueID(), dos);
		dos.writeLong(header.getReserved());
		// д���ļ�table
		for (int i = 0; i < fileTable.length; i++) {
			writeCharArray(fileTable[i].getFileName(), dos);
			dos.writeLong(fileTable[i].getFileSize());
			dos.writeLong(fileTable[i].getOffSet());
		}
		// д���ļ�����
		for (int i = 0; i < fileTable.length; i++) {
			File ftFile = files.get(i);
			FileInputStream ftFis = new FileInputStream(ftFile);
			DataInputStream ftDis = new DataInputStream(ftFis);
			byte[] buff = new byte[256];
			int readLength = 0;
			while ((readLength = ftDis.read(buff)) != -1) {
				encryptBuff(buff, readLength, header);
				dos.write(buff, 0, readLength);
			}
			System.out.println("д��� "+i+" ���ļ�:"+ftFile.getName()+" ���");
			ftDis.close();
			ftFis.close();
		}
		dos.close();
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
			System.out.println("��ȡ���ݳ��Ȳ���ȷ");
			return null;
		} else {
			decryptBuff(fileBuff, readLength, header);
			return fileBuff;
		}
	}

	/**
	 * ��buffer�е�����д�뵽�ļ�
	 * 
	 * @param fileBuff
	 *            �����ļ����ݵ�buffer
	 * @param fileName
	 *            �ļ���
	 * @param extractDir
	 *            �ļ�����Ŀ¼
	 * @throws Exception
	 */
	private void writeFileFromByteBuffer(byte[] fileBuff, String fileName,
			String extractDir) throws Exception {
		String extractFilePath = extractDir + fileName;
		File wFile = new File(extractFilePath);
		FileOutputStream fos = new FileOutputStream(wFile);
		DataOutputStream dos = new DataOutputStream(fos);
		dos.write(fileBuff);
		dos.close();
		fos.close();
	}

	/**
	 * ��pak�ļ���ȡ��ָ�����ļ���byte���飬�����Ҫ�Ļ����Խ�byte����дΪ�ļ�
	 * 
	 * @param pakFilePath
	 *            pak�ļ�·������·��
	 * @param extractFileName
	 *            pak�ļ��н�Ҫ��ȡ�����ļ���
	 * @param writeFile
	 *            �Ƿ���Ҫ��byte����дΪ�ļ�
	 * @param extractDir
	 *            �����Ҫ�Ļ����Խ�byte����дΪ�ļ���extractDirΪȡ�����ݱ�д��Ŀ¼�ļ�
	 * @return byte����
	 * @throws Exception
	 */
	public byte[] extractFileFromAbsolutePath(String pakFilePath,
			String extractFileName, boolean writeFile, String extractDir)
			throws Exception {
		File rFile = new File(pakFilePath);
		FileInputStream fis = new FileInputStream(rFile);
		DataInputStream dis = new DataInputStream(fis);
		PakHeader header = readHeader(dis);
		PakTable[] fileTable = readFileTable(dis,
				(int) header.getNumFileTableEntries());
		boolean find = false;
		int fileIndex = 0;
		for (int i = 0; i < fileTable.length; i++) {
			String fileName = new String(fileTable[i].getFileName()).trim();
			if (fileName.equals(extractFileName)) {
				find = true;
				fileIndex = i;
				break;
			}
		}
		if (find == false) {
			System.out.println("û���ҵ�ָ�����ļ�");
			return null;
		} else {
			byte[] buff = readFileFromPak(dis, header, fileTable[fileIndex]);
			if (writeFile) {
				writeFileFromByteBuffer(buff, extractFileName, extractDir);
			} else {
				dis.close();
				fis.close();
			}
			return buff;
		}
	}

	/**
	 * ��ȡ�ļ������·����
	 * 
	 * @param pakFilePath
	 * @param extractFileName
	 * @param writeFile
	 * @param extractDir
	 * @return
	 * @throws Exception
	 */
	public byte[] extractFileFromRelativePath(String pakFilePath,
			String extractFileName, boolean writeFile, String extractDir)
			throws Exception {
		InputStream fis = this.getClass().getResourceAsStream(pakFilePath);
		DataInputStream dis = new DataInputStream(fis);
		PakHeader header = readHeader(dis);
		PakTable[] fileTable = readFileTable(dis,
				(int) header.getNumFileTableEntries());
		boolean find = false;
		int fileIndex = 0;
		for (int i = 0; i < fileTable.length; i++) {
			String fileName = new String(fileTable[i].getFileName()).trim();
			if (fileName.equals(extractFileName)) {
				find = true;
				fileIndex = i;
				break;
			}
		}
		if (find == false) {
			System.out.println("û���ҵ�ָ�����ļ�");
			return null;
		} else {
			byte[] buff = readFileFromPak(dis, header, fileTable[fileIndex]);
			if (writeFile) {
				writeFileFromByteBuffer(buff, extractFileName, extractDir);
			} else {
				dis.close();
				fis.close();
			}
			return buff;
		}
	}

	/**
	 * ��������ȡָ���ļ�������Դ
	 * 
	 * @param stream
	 * @param extractFileName
	 * @param writeFile
	 * @param extractDir
	 * @return
	 * @throws Exception
	 */
	public byte[] extractFileFromStream(InputStream stream,
			String extractFileName, boolean writeFile, String extractDir)
			throws Exception {
		DataInputStream dis = new DataInputStream(stream);
		PakHeader header = readHeader(dis);
		PakTable[] fileTable = readFileTable(dis,
				(int) header.getNumFileTableEntries());
		boolean find = false;
		int fileIndex = 0;
		for (int i = 0; i < fileTable.length; i++) {
			String fileName = new String(fileTable[i].getFileName()).trim();
			if (fileName.equals(extractFileName)) {
				find = true;
				fileIndex = i;
				break;
			}
		}
		if (find == false) {
			System.out.println("û���ҵ�ָ�����ļ�");
			return null;
		} else {
			byte[] buff = readFileFromPak(dis, header, fileTable[fileIndex]);
			if (writeFile) {
				writeFileFromByteBuffer(buff, extractFileName, extractDir);
			} else {
				dis.close();
				stream.close();
			}
			return buff;
		}
	}

	/**
	 * �Ӿ���·������ȡ��Դ
	 * 
	 * @param pakFilePath
	 * @return
	 * @throws Exception
	 */
	public Vector showPakFromAbsolutePath(String pakFilePath) throws Exception {
		FileInputStream fis = new FileInputStream(new File(pakFilePath));
		DataInputStream dis = new DataInputStream(fis);
		PakHeader header = readHeader(dis);
		PakTable[] fileTable = readFileTable(dis,
				(int) header.getNumFileTableEntries());
		Vector result = new Vector();
		result.add(header);
		result.add(fileTable);
		return result;
	}

	/**
	 * ��pak�ļ���ȡ��ָ����Pak�ļ�����Ϣ
	 * 
	 * @param pakFilePath
	 *            pak����Ŀ�е����·��
	 * @return װ���ļ�ͷ���ļ�table�����Vector
	 * @throws Exception
	 */
	public Vector showPakFromRelativePath(String pakFilePath) throws Exception {
		InputStream fis = this.getClass().getResourceAsStream(pakFilePath);
		DataInputStream dis = new DataInputStream(fis);
		PakHeader header = readHeader(dis);
		PakTable[] fileTable = readFileTable(dis,
				(int) header.getNumFileTableEntries());
		Vector result = new Vector();
		result.add(header);
		result.add(fileTable);
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
		result.add(header);
		result.add(fileTable);
		return result;
	}

	/**
	 * ����pak��ָ���ļ�
	 * 
	 * @param imagesDir
	 *            ͼƬĿ¼
	 * @param suffixName
	 *            ��׺��
	 * @param dstFileName
	 *            Ŀ���ļ�  ����·��---���ֱ��д�ļ����������ļ��ڵ�ǰ���̵�Ŀ¼��
	 *            demo:createPakToFile("D:/Apache/����/walll",".jpg","D:/Apache/����/walll/walll.pak");
	 */
	public static void createPakToFile(String imagesDir, String ext,
			String dstFileName) {
		try {
			PakWriter writer = new PakWriter();
			// �����ļ�ͷ
			char[] signature = new char[PakHeader.SIGNATURE_LENGTH];
			signature = new String(SIGNATURE).toCharArray();
			char[] uniqueID = new char[PakHeader.UNIQUEID_LENGTH];
			uniqueID = new String(UUID).toCharArray();
			PakHeader header = new PakHeader();
			header.setSignature(signature);

			header.setCipherAction((byte) PakHeader.ADDITION_CIPHERACTION);
			header.setCipherValue((byte) 0x0f);
			header.setUniqueID(uniqueID);
			header.setVersion(1.0f);
			header.setReserved(0L);
			ArrayList<File> frr = listFile(new File(imagesDir), ext);
			header.setNumFileTableEntries(frr.size());
			String extractFilePath = dstFileName;
			// ����ѹ���ļ�
			writer.makePakFile(frr, extractFilePath, header);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����ָ��Ŀ¼�µ������ļ�
	 * 
	 * @param dir
	 * @return
	 */
	public static ArrayList<File> listAllFile(File dir) {
		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isFile();
			}
		});
		return new ArrayList<File>(Arrays.asList(files));
	}

	/**
	 * ����ָ��Ŀ¼��ָ����׺�����ļ�
	 * 
	 * @param dir
	 * @param ext
	 * @return
	 */
	public static ArrayList<File> listFile(File dir, final String ext) {
		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				String name = pathname.getName();
				boolean bool = name.endsWith(ext.toLowerCase())
						| name.endsWith(ext.toUpperCase());
				return pathname.isFile() && bool;
			}
		});
		return new ArrayList<File>(Arrays.asList(files));
	}

	/**
	 * ���ļ�ϵͳ����·������ȡ��Դ
	 * extractFromAbsoluteToDir("d:/apache/walll.pak","d:/temp/");
	 * @param relaPath
	 * @param restoreDir ָ����Ŀ¼���һ��Ҫ��ӷָ���---�������ȡ�������ϼ�Ŀ¼
	 */
	public static void extractFromAbsoluteToDir(String absoPath,
			String restoreDir) {
		try {
			PakWriter writer = new PakWriter();
			// �����ļ�ͷ
			char[] signature = new char[PakHeader.SIGNATURE_LENGTH];
			signature = new String(SIGNATURE).toCharArray();
			char[] uniqueID = new char[PakHeader.UNIQUEID_LENGTH];
			uniqueID = new String(UUID).toCharArray();
			PakHeader header = new PakHeader();
			header.setSignature(signature);

			header.setCipherAction((byte) PakHeader.ADDITION_CIPHERACTION);
			header.setCipherValue((byte) 0x0f);
			header.setUniqueID(uniqueID);
			header.setVersion(1.0f);
			header.setReserved(0L);
			Vector pakInfo = writer.showPakFromAbsolutePath(absoPath);
			header = (PakHeader) pakInfo.elementAt(0);
			PakTable[] fileTable = (PakTable[]) pakInfo.elementAt(1);
			/*
			 * for (int i = 0; i < fileTable.length; i++) {
			 * System.out.println("�ļ�table[" + i + "]:" + fileTable[i]); }
			 */
			String restoreFileName = null;
			byte[] fileBuff = null;
			for (int i = 0; i < fileTable.length; i++) {
				restoreFileName = new String(fileTable[i].getFileName()).trim();
				System.out.println("��Pak�ļ���ȡ���� "+i+" ��" + "�ļ�--->" + restoreFileName);
				fileBuff = writer.extractFileFromAbsolutePath(absoPath,
						restoreFileName, true, restoreDir);
				/*
				 * System.out.println("��Pak�ļ���ȡ��" + restoreFileName + "�ļ�������" +
				 * restoreDir + "Ŀ¼");
				 */
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡ�ļ���ָ��Ŀ¼
	 * 
	 * @param extractFilePath
	 *            �ļ�·��
	 * @param restoreDir
	 *            �ָ�Ŀ¼
	 */
	public static void extractFromRelativeToDir(String extractFilePath,
			String restoreDir) {
		try {
			PakWriter writer = new PakWriter();
			// �����ļ�ͷ
			char[] signature = new char[PakHeader.SIGNATURE_LENGTH];
			signature = new String(SIGNATURE).toCharArray();
			char[] uniqueID = new char[PakHeader.UNIQUEID_LENGTH];
			uniqueID = new String(UUID).toCharArray();
			PakHeader header = new PakHeader();
			header.setSignature(signature);

			header.setCipherAction((byte) PakHeader.ADDITION_CIPHERACTION);
			header.setCipherValue((byte) 0x0f);
			header.setUniqueID(uniqueID);
			header.setVersion(1.0f);
			header.setReserved(0L);
			Vector pakInfo = writer.showPakFromRelativePath(extractFilePath);
			header = (PakHeader) pakInfo.elementAt(0);
			PakTable[] fileTable = (PakTable[]) pakInfo.elementAt(1);
			/*
			 * for (int i = 0; i < fileTable.length; i++) {
			 * System.out.println("�ļ�table[" + i + "]:" + fileTable[i]); }
			 */
			String restoreFileName = null;
			byte[] fileBuff = null;
			for (int i = 0; i < fileTable.length; i++) {
				restoreFileName = new String(fileTable[i].getFileName()).trim();
				/* System.out.println("��Pak�ļ���ȡ��" + restoreFileName + "�ļ�..."); */
				fileBuff = writer.extractFileFromRelativePath(extractFilePath,
						restoreFileName, true, restoreDir);
				/*
				 * System.out.println("��Pak�ļ���ȡ��" + restoreFileName + "�ļ�������" +
				 * restoreDir + "Ŀ¼");
				 */
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		//String str="D:/Apache/����/walll";
		//createPakToFile("D:/Apache/����/walll",".jpg","D:/Apache/����/walll/walll.pak");
		
		extractFromAbsoluteToDir("d:/apache/walll.pak","d:/temp/");
	}
}