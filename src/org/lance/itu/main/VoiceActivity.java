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
 * 录音界面
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
	private static final int MAX_TIME = 60;// 最长录音时间
	private static final int MIN_TIME = 2;// 最短录音时间

	private static final int RECORD_NO = 0; // 不在录音
	private static final int RECORD_ING = 1; // 正在录音
	private static final int RECORD_ED = 2; // 完成录音
	private int mRecord_State = 0; // 录音的状态
	private float mRecord_Time;// 录音的时间
	private double mRecord_Volume;// 麦克风获取的音量值
	private boolean mPlayState; // 播放状态
	private int mPlayCurrentPosition;// 当前播放的时间
	private String mRecordPath;// 录音的存储名称
	private int mMAXVolume;// 最大音量高度
	private int mMINVolume;// 最小音量高度
	
	private EditText shareTitle;
	private EditText shareContent;
	private Button shareVoice;

	protected void onCreate(Bundle saved) {
		super.onCreate(saved);
		setContentView(R.layout.voice_activity);
		findViewById();
		setListener();
		// 设置当前的最小声音和最大声音值
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
				showHint("保存成功!");
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
								// 初始化录音时间
								mRecord_Time = 0;
								while (mRecord_State == RECORD_ING) {
									// 大于最大录音时间则停止录音
									if (mRecord_Time >= MAX_TIME) {
										mRecordHandler.sendEmptyMessage(0);
									} else {
										try {
											// 每隔200毫秒就获取声音音量并更新界面显示
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
				// 停止录音
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
						// 如果录音时间小于最短时间
						if (mRecord_Time <= MIN_TIME) {
							showHint("录音时间过短");
							mRecord_State = RECORD_NO;
							mRecord_Time = 0;
							mRecordTime.setText("0″");
							mRecordProgressBar.setProgress(0);
							ViewGroup.LayoutParams params = mRecordVolume
									.getLayoutParams();
							params.height = 0;
							mRecordVolume.setLayoutParams(params);
						} else {
							// 录音成功,则显示录音成功后的界面
							mRecordLayout.setVisibility(View.GONE);
							mRecord.setVisibility(View.GONE);
							mDisplayVoiceLayout.setVisibility(View.VISIBLE);
							mDisplayVoicePlay
									.setImageResource(R.drawable.globle_player_btn_play);
							mDisplayVoiceProgressBar.setMax((int) mRecord_Time);
							mDisplayVoiceProgressBar.setProgress(0);
							mDisplayVoiceTime.setText((int) mRecord_Time + "″");
						}
					}
					break;
				}
				return false;
			}
		});
		mDisplayVoicePlay.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 播放录音
				if (!mPlayState) {
					mMediaPlayer = new MediaPlayer();
					try {
						// 添加录音的路径
						mMediaPlayer.setDataSource(mRecordPath);
						// 准备
						mMediaPlayer.prepare();
						// 播放
						mMediaPlayer.start();
						// 根据时间修改界面
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
						// 修改播放状态
						mPlayState = true;
						// 修改播放图标
						mDisplayVoicePlay
								.setImageResource(R.drawable.globle_player_btn_stop);

						mMediaPlayer
								.setOnCompletionListener(new OnCompletionListener() {
									// 播放结束后调用
									public void onCompletion(MediaPlayer mp) {
										// 停止播放
										mMediaPlayer.stop();
										// 修改播放状态
										mPlayState = false;
										// 修改播放图标
										mDisplayVoicePlay
												.setImageResource(R.drawable.globle_player_btn_play);
										// 初始化播放数据
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
						// 根据播放状态修改显示内容
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
	 * 用来控制动画效果
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
	 * 用来控制录音
	 */
	Handler mRecordHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (mRecord_State == RECORD_ING) {
					// 停止动画效果
					stopRecordLightAnimation();
					// 修改录音状态
					mRecord_State = RECORD_ED;
					try {
						// 停止录音
						mRecordUtil.stop();
						// 初始化录音音量
						mRecord_Volume = 0;
					} catch (Exception e) {
						e.printStackTrace();
					}
					// 根据录音修改界面显示内容
					mRecordLayout.setVisibility(View.GONE);
					mRecord.setVisibility(View.GONE);
					mDisplayVoiceLayout.setVisibility(View.VISIBLE);
					mDisplayVoicePlay
							.setImageResource(R.drawable.globle_player_btn_play);
					mDisplayVoiceProgressBar.setMax((int) mRecord_Time);
					mDisplayVoiceProgressBar.setProgress(0);
					mDisplayVoiceTime.setText((int) mRecord_Time + "″");
				}
				break;

			case 1:
				// 根据录音时间显示进度条
				mRecordProgressBar.setProgress((int) mRecord_Time);
				// 显示录音时间
				mRecordTime.setText((int) mRecord_Time + "″");
				// 根据录音声音大小显示效果
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
	 * 开始动画效果
	 */
	private void startRecordLightAnimation() {
		mRecordLightHandler.sendEmptyMessageDelayed(0, 0);
		mRecordLightHandler.sendEmptyMessageDelayed(1, 1000);
		mRecordLightHandler.sendEmptyMessageDelayed(2, 2000);
	}

	/**
	 * 停止动画效果
	 */
	private void stopRecordLightAnimation() {
		mRecordLightHandler.sendEmptyMessage(3);
	}

}
