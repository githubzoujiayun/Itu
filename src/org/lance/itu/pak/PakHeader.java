package org.lance.itu.pak;

/**
 * Pak文件头： 结构： 签名：6字节 char数组 版本号：32位float 文件table数量：32位整数 密码行为：8位字节 密码：8位字节
 * 文件唯一ID：10字节 char数组 保留位：32位整数(4字节
 * 
 * @author lance
 * 
 */
public class PakHeader {
	// 定义文件唯一ID长度
	public static final int UNIQUEID_LENGTH = 10;
	// 定义文件签名长度
	public static final int SIGNATURE_LENGTH = 6;
	// 定义加法运算
	public static final int ADDITION_CIPHERACTION = 0;
	// 定义减法运算
	public static final int SUBTRACT_CIHOERACTION = 1;
	// 文件签名
	private char[] signature = new char[SIGNATURE_LENGTH];
	// 版本号
	private float version = 0f;
	// 文件table数量
	private long numFileTableEntries = 0;
	// 密码使用方法：在原数据上进行加法还是减法
	private byte cipherAction = ADDITION_CIPHERACTION;
	// 密码值
	private byte cipherValue = 0x00;
	// 唯一ID
	private char[] uniqueID = new char[UNIQUEID_LENGTH];
	// 保留的4字节
	private long reserved = 0;

	public PakHeader() {
	}

	/**
	 * 
	 * @param signature
	 *            签名
	 * @param version
	 *            版本号
	 * @param numFileTableEntries
	 *            文件数量
	 * @param cipherAction
	 *            密码使用方法
	 * @param cipherValue
	 *            密码值
	 * @param uniqueID
	 *            唯一ID
	 * @param reserved
	 *            保留2字节
	 */
	public PakHeader(char[] signature, float version, long numFileTableEntries,
			byte cipherAction, byte cipherValue, char[] uniqueID, long reserved) {
		for (int i = 0; i < SIGNATURE_LENGTH; this.signature[i] = signature[i], i++)
			;
		this.version = version;
		this.cipherAction = cipherAction;
		this.numFileTableEntries = numFileTableEntries;
		this.cipherValue = cipherValue;
		for (int i = 0; i < UNIQUEID_LENGTH; this.uniqueID[i] = uniqueID[i], i++)
			;
		this.reserved = reserved;
	}

	public byte getCipherValue() {
		return cipherValue;
	}

	public void setCipherValue(byte cipherValue) {
		this.cipherValue = cipherValue;
	}

	public long getNumFileTableEntries() {
		return numFileTableEntries;
	}

	public void setNumFileTableEntries(long numFileTableEntries) {
		this.numFileTableEntries = numFileTableEntries;
	}

	public long getReserved() {
		return reserved;
	}

	public void setReserved(long reserved) {
		this.reserved = reserved;
	}

	public char[] getUniqueID() {
		return uniqueID;
	}

	public void setUniqueID(char[] uniqueID) {
		for (int i = 0; i < UNIQUEID_LENGTH; this.uniqueID[i] = uniqueID[i], i++)
			;
	}

	public float getVersion() {
		return version;
	}

	public void setVersion(float version) {
		this.version = version;
	}

	public byte getCipherAction() {
		return cipherAction;
	}

	public void setCipherAction(byte cipherAction) {
		this.cipherAction = cipherAction;
	}

	public char[] getSignature() {
		return signature;
	}

	public void setSignature(char[] signature) {
		for (int i = 0; i < SIGNATURE_LENGTH; this.signature[i] = signature[i], i++)
			;
	}

	/**
	 * @return 返回PakHeader的大小
	 */
	public static int size() {
		return SIGNATURE_LENGTH + 4 + 4 + 1 + 1 + UNIQUEID_LENGTH + 4;
	}

	public String toString() {
		String result = "";
		result += "\tsignName:" + new String(this.signature).trim()
				+ "\tversionCode:" + this.version + "\tfileNum:"
				+ this.numFileTableEntries + "\tpwd xingwei:"
				+ this.cipherAction + "\tpwd:" + this.cipherValue + "\tUUID:"
				+ new String(this.uniqueID).trim() + "\tbao liu wei:"
				+ this.reserved;
		return result;
	}
}