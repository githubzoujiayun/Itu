package org.lance.itu.main;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.lance.itu.util.Prefs;
import org.lance.itu.util.RecordUtil;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * ¼������
 * 
 * @author lance
 * 
 */
public class VoiceActivity extends BaseActivity {
	private LinearLayout mParent;
	private Button mCancel;
	private Button mDetermine;
	private LinearLayout mDisplayVoiceLayout;
	private ImageView mDisplayVoicePlay;
	private ProgressBar mDisplayVoiceProgressBar;
	private TextView mDisplayVoiceTime;
	private Button mRecord;
	private RelativeLayout mRecordLayout;
	private ImageView mRecordVolume;
	private ImageView mRecordLight_1;
	private ImageView mRecordLight_2;
	private ImageView mRecordLight_3;
	private TextView mRecordTime;
	private ProgressBar mRecordProgressBar;

	private Animation mRecordLight_1_Animation;
	private Animation mRecordLight_2_Animation;
	private Animation mRecordLight_3_Animation;

	private MediaPlayer mMediaPlayer;
	private RecordUtil mRecordUtil;
	private static final int MAX_TIME = 60;// �¼��ʱ��
	private static final int MIN_TIME = 2;// ���¼��ʱ��

	private static final int RECORD_NO = 0; // ����¼��
	private static final int RECORD_ING = 1; // ����¼��
	private static final int RECORD_ED = 2; // ���¼��
	private int mRecord_State = 0; // ¼����״̬
	private float mRecord_Time;// ¼����ʱ��
	private double mRecord_Volume;// ��˷��ȡ������ֵ
	private boolean mPlayState; // ����״̬
	private int mPlayCurrentPosition;// ��ǰ���ŵ�ʱ��
	private String mRecordPath;// ¼���Ĵ洢����
	private int mMAXVolume;// ��������߶�
	private int mMINVolume;// ��С�����߶�
	
	private EditText shareTitle;
	private EditText shareContent;
	private Button shareVoice;

	protected void onCreate(Bundle saved) {
		super.onCreate(saved);
		setContentView(R.layout.voice_activity);
		findViewById();
		setListener();
		// ���õ�ǰ����С�������������ֵ
		mMINVolume = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4.5f, getResources()
						.getDisplayMetrics());
		mMAXVolume = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 65f, getResources()
						.getDisplayMetrics());
	}

	private void findViewById() {
		mParent = (LinearLayout) findViewById(R.id.voice_parent);
		mCancel = (Button) findViewById(R.id.voice_cancel);
		mDetermine = (Button) findViewById(R.id.voice_determine);
		
		mDisplayVoiceLayout = (LinearLayout) findViewById(R.id.voice_display_voice_layout);
		mDisplayVoicePlay = (ImageView) findViewById(R.id.voice_display_voice_play);
		mDisplayVoiceProgressBar = (ProgressBar) findViewById(R.id.voice_display_voice_progressbar);
		mDisplayVoiceTime = (TextView) findViewById(R.id.voice_display_voice_time);
		mRecord = (Button) findViewById(R.id.voice_record_btn);
		mRecordLayout = (RelativeLayout) findViewById(R.id.voice_record_layout);
		mRecordVolume = (ImageView) findViewById(R.id.voice_recording_volume);
		mRecordLight_1 = (ImageView) findViewById(R.id.voice_recordinglight_1);
		mRecordLight_2 = (ImageView) findViewById(R.id.voice_recordinglight_2);
		mRecordLight_3 = (ImageView) findViewById(R.id.voice_recordinglight_3);
		mRecordTime = (TextView) findViewById(R.id.voice_record_time);
		mRecordProgressBar = (ProgressBar) findViewById(R.id.voice_record_progressbar);
		
		shareTitle=(EditText)findViewById(R.id.share_title);
		shareContent=(EditText)findViewById(R.id.share_content);
		shareVoice=(Button)findViewById(R.id.share_button);
	}

	private void setListener() {
		mCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				File file=new File(mRecordPath);
				if(file.exists()){
					file.delete();
				}
				finish();
			}
		});
		mDetermine.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showHint("����ɹ�!");
				finish();
			}
		});
		shareVoice.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		mRecord.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (mRecord_State != RECORD_ING) {
						startRecordLightAnimation();
						mRecord_State = RECORD_ING;
						mRecordPath = Prefs.getRecordPath() + UUID.randomUUID().toString()
								+ ".amr";
						mRecordUtil = new RecordUtil(mRecordPath);
						try {
							mRecordUtil.start();
						} catch (Exception e) {
							e.printStackTrace();
							return true;
						}
						new Thread(new Runnable() {

							public void run() {
								// ��ʼ��¼��ʱ��
								mRecord_Time = 0;
								while (mRecord_State == RECORD_ING) {
									// �������¼��ʱ����ֹͣ¼��
									if (mRecord_Time >= MAX_TIME) {
										mRecordHandler.sendEmptyMessage(0);
									} else {
										try {
											// ÿ��200����ͻ�ȡ�������������½�����ʾ
											Thread.sleep(200);
											mRecord_Time += 0.2;
											if (mRecord_State == RECORD_ING) {
												mRecord_Volume = mRecordUtil
														.getAmplitude();
												mRecordHandler
														.sendEmptyMessage(1);
											}
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}
								}
							}
						}).start();
					}
					break;
				// ֹͣ¼��
				case MotionEvent.ACTION_UP:
					if (mRecord_State == RECORD_ING) {
						stopRecordLightAnimation();
						mRecord_State = RECORD_ED;
						try {
							mRecordUtil.stop();
							mRecord_Volume = 0;
						} catch (Exception e) {
							e.printStackTrace();
							return true;
						}
						// ���¼��ʱ��С�����ʱ��
						if (mRecord_Time <= MIN_TIME) {
							showHint("¼��ʱ�����");
							mRecord_State = RECORD_NO;
							mRecord_Time = 0;
							mRecordTime.setText("0��");
							mRecordProgressBar.setProgress(0);
							ViewGroup.LayoutParams params = mRecordVolume
									.getLayoutParams();
							params.height = 0;
							mRecordVolume.setLayoutParams(params);
						} else {
							// ¼���ɹ�,����ʾ¼���ɹ���Ľ���
							mRecordLayout.setVisibility(View.GONE);
							mRecord.setVisibility(View.GONE);
							mDisplayVoiceLayout.setVisibility(View.VISIBLE);
							mDisplayVoicePlay
									.setImageResource(R.drawable.globle_player_btn_play);
							mDisplayVoiceProgressBar.setMax((int) mRecord_Time);
							mDisplayVoiceProgressBar.setProgress(0);
							mDisplayVoiceTime.setText((int) mRecord_Time + "��");
						}
					}
					break;
				}
				return false;
			}
		});
		mDisplayVoicePlay.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// ����¼��
				if (!mPlayState) {
					mMediaPlayer = new MediaPlayer();
					try {
						// ���¼����·��
						mMediaPlayer.setDataSource(mRecordPath);
						// ׼��
						mMediaPlayer.prepare();
						// ����
						mMediaPlayer.start();
						// ����ʱ���޸Ľ���
						new Thread(new Runnable() {

							public void run() {

								mDisplayVoiceProgressBar
										.setMax((int) mRecord_Time);
								mPlayCurrentPosition = 0;
								while (mMediaPlayer.isPlaying()) {
									mPlayCurrentPosition = mMediaPlayer
											.getCurrentPosition() / 1000;
									mDisplayVoiceProgressBar
											.setProgress(mPlayCurrentPosition);
								}
							}
						}).start();
						// �޸Ĳ���״̬
						mPlayState = true;
						// �޸Ĳ���ͼ��
						mDisplayVoicePlay
								.setImageResource(R.drawable.globle_player_btn_stop);

						mMediaPlayer
								.setOnCompletionListener(new OnCompletionListener() {
									// ���Ž��������
									public void onCompletion(MediaPlayer mp) {
										// ֹͣ����
										mMediaPlayer.stop();
										// �޸Ĳ���״̬
										mPlayState = false;
										// �޸Ĳ���ͼ��
										mDisplayVoicePlay
												.setImageResource(R.drawable.globle_player_btn_play);
										// ��ʼ����������
										mPlayCurrentPosition = 0;
										mDisplayVoiceProgressBar
												.setProgress(mPlayCurrentPosition);
									}
								});

					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					if (mMediaPlayer != null) {
						// ���ݲ���״̬�޸���ʾ����
						if (mMediaPlayer.isPlaying()) {
							mPlayState = false;
							mMediaPlayer.stop();
							mDisplayVoicePlay
									.setImageResource(R.drawable.globle_player_btn_play);
							mPlayCurrentPosition = 0;
							mDisplayVoiceProgressBar
									.setProgress(mPlayCurrentPosition);
						} else {
							mPlayState = false;
							mDisplayVoicePlay
									.setImageResource(R.drawable.globle_player_btn_play);
							mPlayCurrentPosition = 0;
							mDisplayVoiceProgressBar
									.setProgress(mPlayCurrentPosition);
						}
					}
				}
			}
		});
	}

	/**
	 * �������ƶ���Ч��
	 */
	Handler mRecordLightHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (mRecord_State == RECORD_ING) {
					mRecordLight_1.setVisibility(View.VISIBLE);
					mRecordLight_1_Animation = AnimationUtils.loadAnimation(
							VoiceActivity.this, R.anim.voice_anim);
					mRecordLight_1.setAnimation(mRecordLight_1_Animation);
					mRecordLight_1_Animation.startNow();
				}
				break;

			case 1:
				if (mRecord_State == RECORD_ING) {
					mRecordLight_2.setVisibility(View.VISIBLE);
					mRecordLight_2_Animation = AnimationUtils.loadAnimation(
							VoiceActivity.this, R.anim.voice_anim);
					mRecordLight_2.setAnimation(mRecordLight_2_Animation);
					mRecordLight_2_Animation.startNow();
				}
				break;
			case 2:
				if (mRecord_State == RECORD_ING) {
					mRecordLight_3.setVisibility(View.VISIBLE);
					mRecordLight_3_Animation = AnimationUtils.loadAnimation(
							VoiceActivity.this, R.anim.voice_anim);
					mRecordLight_3.setAnimation(mRecordLight_3_Animation);
					mRecordLight_3_Animation.startNow();
				}
				break;
			case 3:
				if (mRecordLight_1_Animation != null) {
					mRecordLight_1.clearAnimation();
					mRecordLight_1_Animation.cancel();
					mRecordLight_1.setVisibility(View.GONE);

				}
				if (mRecordLight_2_Animation != null) {
					mRecordLight_2.clearAnimation();
					mRecordLight_2_Animation.cancel();
					mRecordLight_2.setVisibility(View.GONE);
				}
				if (mRecordLight_3_Animation != null) {
					mRecordLight_3.clearAnimation();
					mRecordLight_3_Animation.cancel();
					mRecordLight_3.setVisibility(View.GONE);
				}

				break;
			}
		}
	};
	/**
	 * ��������¼��
	 */
	Handler mRecordHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (mRecord_State == RECORD_ING) {
					// ֹͣ����Ч��
					stopRecordLightAnimation();
					// �޸�¼��״̬
					mRecord_State = RECORD_ED;
					try {
						// ֹͣ¼��
						mRecordUtil.stop();
						// ��ʼ��¼������
						mRecord_Volume = 0;
					} catch (Exception e) {
						e.printStackTrace();
					}
					// ����¼���޸Ľ�����ʾ����
					mRecordLayout.setVisibility(View.GONE);
					mRecord.setVisibility(View.GONE);
					mDisplayVoiceLayout.setVisibility(View.VISIBLE);
					mDisplayVoicePlay
							.setImageResource(R.drawable.globle_player_btn_play);
					mDisplayVoiceProgressBar.setMax((int) mRecord_Time);
					mDisplayVoiceProgressBar.setProgress(0);
					mDisplayVoiceTime.setText((int) mRecord_Time + "��");
				}
				break;

			case 1:
				// ����¼��ʱ����ʾ������
				mRecordProgressBar.setProgress((int) mRecord_Time);
				// ��ʾ¼��ʱ��
				mRecordTime.setText((int) mRecord_Time + "��");
				// ����¼��������С��ʾЧ��
				ViewGroup.LayoutParams params = mRecordVolume.getLayoutParams();
				if (mRecord_Volume < 200.0) {
					params.height = mMINVolume;
				} else if (mRecord_Volume > 200.0 && mRecord_Volume < 400) {
					params.height = mMINVolume * 2;
				} else if (mRecord_Volume > 400.0 && mRecord_Volume < 800) {
					params.height = mMINVolume * 3;
				} else if (mRecord_Volume > 800.0 && mRecord_Volume < 1600) {
					params.height = mMINVolume * 4;
				} else if (mRecord_Volume > 1600.0 && mRecord_Volume < 3200) {
					params.height = mMINVolume * 5;
				} else if (mRecord_Volume > 3200.0 && mRecord_Volume < 5000) {
					params.height = mMINVolume * 6;
				} else if (mRecord_Volume > 5000.0 && mRecord_Volume < 7000) {
					params.height = mMINVolume * 7;
				} else if (mRecord_Volume > 7000.0 && mRecord_Volume < 10000.0) {
					params.height = mMINVolume * 8;
				} else if (mRecord_Volume > 10000.0 && mRecord_Volume < 14000.0) {
					params.height = mMINVolume * 9;
				} else if (mRecord_Volume > 14000.0 && mRecord_Volume < 17000.0) {
					params.height = mMINVolume * 10;
				} else if (mRecord_Volume > 17000.0 && mRecord_Volume < 20000.0) {
					params.height = mMINVolume * 11;
				} else if (mRecord_Volume > 20000.0 && mRecord_Volume < 24000.0) {
					params.height = mMINVolume * 12;
				} else if (mRecord_Volume > 24000.0 && mRecord_Volume < 28000.0) {
					params.height = mMINVolume * 13;
				} else if (mRecord_Volume > 28000.0) {
					params.height = mMAXVolume;
				}
				mRecordVolume.setLayoutParams(params);
				break;
			}
		}

	};

	/**
	 * ��ʼ����Ч��
	 */
	private void startRecordLightAnimation() {
		mRecordLightHandler.sendEmptyMessageDelayed(0, 0);
		mRecordLightHandler.sendEmptyMessageDelayed(1, 1000);
		mRecordLightHandler.sendEmptyMessageDelayed(2, 2000);
	}

	/**
	 * ֹͣ����Ч��
	 */
	private void stopRecordLightAnimation() {
		mRecordLightHandler.sendEmptyMessage(3);
	}

}
