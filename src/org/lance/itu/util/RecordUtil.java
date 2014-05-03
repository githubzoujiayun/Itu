package org.lance.itu.util;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;

/**
 * ¼��������
 * 
 * @author lance
 * 
 */
public class RecordUtil {
	private static final int SAMPLE_RATE_IN_HZ = 8000;
	private MediaRecorder recorder = new MediaRecorder();
	// ¼����·��
	private String mPath;

	public RecordUtil(String path) {
		mPath = path;
	}

	/**
	 * ��ʼ¼��
	 * 
	 * @throws IOException
	 */
	public void start() throws Exception {
		String state = android.os.Environment.getExternalStorageState();
		if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
			throw new IOException("SD Card is not mounted,It is  " + state
					+ ".");
		}
		File directory = new File(mPath).getParentFile();
		if (!directory.exists() && !directory.mkdirs()) {
			throw new IOException("Path to file could not be created");
		}
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setAudioSamplingRate(SAMPLE_RATE_IN_HZ);
		recorder.setOutputFile(mPath);
		recorder.prepare();
		recorder.start();
	}

	/** ����¼�� */
	public void stop() throws Exception {
		recorder.stop();
		recorder.release();
	}

	/** ��ȡ��� */
	public double getAmplitude() {
		if (recorder != null) {
			return (recorder.getMaxAmplitude());
		}
		return 0;
	}
}
