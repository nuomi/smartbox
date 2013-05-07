/*
 * Copyright (C) 2011 VOV IO (http://vov.io/)
 */

package com.demonzym.smartbox.androidmediaplayer;

import io.vov.utils.StringUtils;
import io.vov.vitamio.widget.OutlineTextView;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.demonzym.smartbox.R;
import com.demonzym.smartbox.protocal.ConstValues;

/**
 * A view containing controls for a MediaPlayer. Typically contains the buttons
 * like "Play/Pause" and a progress slider. It takes care of synchronizing the
 * controls with the state of the MediaPlayer.
 * <p>
 * The way to use this class is to a) instantiate it programatically or b)
 * create it in your xml layout.
 * 
 * a) The MediaController will create a default set of controls and put them in
 * a window floating above your application. Specifically, the controls will
 * float above the view specified with setAnchorView(). By default, the window
 * will disappear if left idle for three seconds and reappear when the user
 * touches the anchor view. To customize the MediaController's style, layout and
 * controls you should extend MediaController and override the {#link
 * {@link #makeControllerView()} method.
 * 
 * b) The MediaController is a FrameLayout, you can put it in your layout xml
 * and get it through {@link #findViewById(int)}.
 * 
 * NOTES: In each way, if you want customize the MediaController, the SeekBar's
 * id must be mediacontroller_progress, the Play/Pause's must be
 * mediacontroller_pause, current time's must be mediacontroller_time_current,
 * total time's must be mediacontroller_time_total, file name's must be
 * mediacontroller_file_name. And your resources must have a pause_button
 * drawable and a play_button drawable.
 * <p>
 * Functions like show() and hide() have no effect when MediaController is
 * created in an xml layout.
 */
public class MediaController extends FrameLayout {
	
	private LinearLayout playerView;
	
	private MediaPlayerControl mPlayer;
	private Context mContext;
	private PopupWindow mWindow;
	private int mAnimStyle;
	private View mAnchor;
	private View mRoot;
	private ProgressBar mProgress;
	private TextView mEndTime, mCurrentTime;
	private TextView mFileName;
	private OutlineTextView mInfoView;
	private String mTitle;
	private long mDuration;
	private boolean mShowing;
	private boolean mDragging;
	private boolean mInstantSeeking = true;
	private static final int sDefaultTimeout = 10000;
	private static final int FADE_OUT = 1;
	private static final int SHOW_PROGRESS = 2;
	private boolean mFromXml = false;
	private ImageButton mPauseButton;
	private ImageButton mScaleButton;
	private ImageButton mLeftButton;
	private ImageButton mRightButton;
	private ImageButton mSetButton;
	private ImageButton mBigPauseButton;
	private int mScaleType = VideoView.VIDEO_LAYOUT_ORIGIN;

	private AudioManager mAM;

	public MediaController(Context context, AttributeSet attrs) {
		super(context, attrs);
		mRoot = this;
		mFromXml = true;
		initController(context);
	}

	public MediaController(Context context) {
		super(context);
		if (!mFromXml && initController(context))
			initFloatingWindow();
	}

	private boolean initController(Context context) {
		mContext = context;
		mAM = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		
		
		
		return true;
	}

	@Override
	public void onFinishInflate() {
		if (mRoot != null)
			initControllerView(mRoot);
	}

	private void initFloatingWindow() {
		mWindow = new PopupWindow(mContext);
		mWindow.setFocusable(true);
		mWindow.setBackgroundDrawable(null);
		mWindow.setOutsideTouchable(true);
		mAnimStyle = android.R.style.Animation_Toast;
	}

	/**
	 * Set the view that acts as the anchor for the control view. This can for
	 * example be a VideoView, or your Activity's main view.
	 * 
	 * @param view The view to which to anchor the controller when it is visible.
	 */
	public void setAnchorView(View view) {
		mAnchor = view;
		if (!mFromXml) {
			removeAllViews();
			mRoot = makeControllerView();
			mWindow.setContentView(mRoot);
			mWindow.setWidth(LayoutParams.MATCH_PARENT);
			mWindow.setHeight(LayoutParams.WRAP_CONTENT);
		}
		initControllerView(mRoot);
	}

	/**
	 * Create the view that holds the widgets that control playback. Derived
	 * classes can override this to create their own.
	 * 
	 * @return The controller view.
	 */
	protected View makeControllerView() {
		return ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.mediacontroller, this);
	}


	long starttime = 0;
	int progressCache = 0;
	private void initControllerView(View v) {
		

		playerView = (LinearLayout) v.findViewById(R.id.mediacontroller_player);
		playerView.setFocusable(true);
		playerView.setFocusableInTouchMode(true);
		playerView.setVisibility(View.VISIBLE);
		playerView.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				if (arg1 == KeyEvent.KEYCODE_BACK) {
					Log.i("playerView", "playVIew back");
					playerView.setVisibility(View.INVISIBLE);
					return true;
				}
				return false;
			}
		});
		//xlhadd
		mBigPauseButton=(ImageButton)v.findViewById(R.id.ibt_pause_middle);		
			mBigPauseButton.setVisibility(GONE);
		
		
		mPauseButton = (ImageButton) v.findViewById(R.id.mediacontroller_play_pause);
		if (mPauseButton != null) {
			mPauseButton.requestFocus();
			mPauseButton.setOnClickListener(mPauseListener);
		}
		
		mScaleButton = (ImageButton) v.findViewById(R.id.mediacontroller_screen);
		mScaleButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				mScaleType++;
				if(mScaleType > VideoView.VIDEO_LAYOUT_ZOOM)
					mScaleType = 0;
				if(mScaleListener != null)
					mScaleListener.onScale(mScaleType);
				switch(mScaleType){
				case 0:
					mScaleButton.setImageResource(R.drawable.mediacontroller_0_button);
					break;
				case 1:
					mScaleButton.setImageResource(R.drawable.mediacontroller_1_button);
					break;
				case 2:
					mScaleButton.setImageResource(R.drawable.mediacontroller_4_button);
					break;
				case 3:
					mScaleButton.setImageResource(R.drawable.mediacontroller_unfold_button);
					break;
				}
				
			}
		});

		mProgress = (ProgressBar) v.findViewById(R.id.mediacontroller_seekbar);
		if (mProgress != null) {
			if (mProgress instanceof SeekBar) {
				SeekBar seeker = (SeekBar) mProgress;
				seeker.setOnSeekBarChangeListener(mSeekListener);
				seeker.setThumbOffset(1);
			}
			mProgress.setMax(1000);
		}

		mEndTime = (TextView) v.findViewById(R.id.mediacontroller_time_total);
		mCurrentTime = (TextView) v.findViewById(R.id.mediacontroller_time_current);
		mFileName = (TextView) v.findViewById(R.id.mediacontroller_file_name);
		if (mFileName != null)
			mFileName.setText(mTitle);
		
		mLeftButton = (ImageButton) v.findViewById(R.id.mediacontroller_prew);
		mLeftButton.setOnKeyListener(new OnKeyListener() {
			
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode != KeyEvent.KEYCODE_DPAD_CENTER)
					return false;

				mPlayer.pause();
				
				switch(event.getAction()){
				case KeyEvent.ACTION_DOWN:
					starttime = System.currentTimeMillis();
					progressCache = mProgress.getSecondaryProgress();
					if(progressCache == 0)
						progressCache = mProgress.getProgress();
					int scale = 3;
					progressCache -= scale * 3;
					mProgress.setSecondaryProgress(progressCache);
					break;
				case KeyEvent.ACTION_UP:
					long newposition = (mDuration * mProgress.getSecondaryProgress()) / 1000;
					String time = StringUtils.generateTime(newposition);
					if (mInstantSeeking)
						mPlayer.seekTo(newposition);
					if (mInfoView != null)
						mInfoView.setText(time);
					if (mCurrentTime != null)
						mCurrentTime.setText(time);

					mPlayer.start();
					
					break;
				}
				
				
				return false;
			}
		});
		mLeftButton.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				long now = 0;
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:

					mPlayer.pause();
					
					starttime = System.currentTimeMillis();
					progressCache = mProgress.getProgress();
					break;
				case MotionEvent.ACTION_MOVE:
					now = System.currentTimeMillis();
					int scale = (int) ((now - starttime) / 1500);
					if(scale > 3)
						scale = 3;
					progressCache -= scale * 3;
					mProgress.setSecondaryProgress(progressCache);
					break;
				case MotionEvent.ACTION_UP:
					long newposition = (mDuration * mProgress.getSecondaryProgress()) / 1000;
					String time = StringUtils.generateTime(newposition);
					if (mInstantSeeking)
						mPlayer.seekTo(newposition);
					if (mInfoView != null)
						mInfoView.setText(time);
					if (mCurrentTime != null)
						mCurrentTime.setText(time);

					mPlayer.start();
					
					break;
				}
				
				
				return false;
			}
		});
		mRightButton = (ImageButton) v.findViewById(R.id.mediacontroller_next);
		mRightButton.setOnKeyListener(new OnKeyListener() {
			
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode != KeyEvent.KEYCODE_DPAD_CENTER)
					return false;
				Log.e("keyaction", "" + event.getAction());
				switch(event.getAction()){
				case KeyEvent.ACTION_DOWN:
					
					mPlayer.pause();
					
					starttime = System.currentTimeMillis();
					progressCache = mProgress.getSecondaryProgress();
					if(progressCache == 0)
						progressCache = mProgress.getProgress();
					
					int scale  = 3;
					progressCache += scale * 3;
					mProgress.setSecondaryProgress(progressCache);
					break;
				case KeyEvent.ACTION_UP:
					long newposition = (mDuration * mProgress.getSecondaryProgress()) / 1000;
					String time = StringUtils.generateTime(newposition);
					if (mInstantSeeking)
						mPlayer.seekTo(newposition);
					if (mInfoView != null)
						mInfoView.setText(time);
					if (mCurrentTime != null)
						mCurrentTime.setText(time);
					mProgress.setSecondaryProgress(0);

					mPlayer.start();
					
					break;
				}
				
				
				return false;
			}
		});
		mRightButton.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				long now = 0;
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:

					mPlayer.pause();
					
					starttime = System.currentTimeMillis();
					progressCache = mProgress.getProgress();
					break;
				case MotionEvent.ACTION_MOVE:
					now = System.currentTimeMillis();
					int scale = (int) ((now - starttime) / 3000);
					if(scale > 3)
						scale = 3;
					progressCache += scale * 3;
					mProgress.setSecondaryProgress(progressCache);
					break;
				case MotionEvent.ACTION_UP:
					long newposition = (mDuration * mProgress.getSecondaryProgress()) / 1000;
					String time = StringUtils.generateTime(newposition);
					if (mInstantSeeking)
						mPlayer.seekTo(newposition);
					if (mInfoView != null)
						mInfoView.setText(time);
					if (mCurrentTime != null)
						mCurrentTime.setText(time);
					mProgress.setSecondaryProgress(0);

					mPlayer.start();
					
					break;
				}
				
				
				return false;
			}
		});
		
		mSetButton = (ImageButton) v.findViewById(R.id.mediacontroller_set);
		mSetButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if(mListListener != null){
					mListListener.onList();
				}
			}
		});
		
	}			
	
	public void setBufferPercent(int percent){	//xlh percent
		if (mFileName != null&&percent>=0){
				mFileName.setText(percent+"%...");
		}
	}

	public void setMediaPlayer(MediaPlayerControl player) {
		mPlayer = player;
		updatePausePlay();
	}

	/**
	 * Control the action when the seekbar dragged by user
	 * 
	 * @param seekWhenDragging True the media will seek periodically
	 */
	public void setInstantSeeking(boolean seekWhenDragging) {
		mInstantSeeking = seekWhenDragging;
	}

	public void show() {
		show(sDefaultTimeout);
	}

	/**
	 * Set the content of the file_name TextView
	 * 
	 * @param name
	 */
	public void setFileName(String name) {
		mTitle = name;
		if (mFileName != null)
			mFileName.setText(mTitle);
	}

	/**
	 * Set the View to hold some information when interact with the
	 * MediaController
	 * 
	 * @param v
	 */
	public void setInfoView(OutlineTextView v) {
		mInfoView = v;
	}

	private void disableUnsupportedButtons() {
		try {
			if (mPauseButton != null && !mPlayer.canPause())
				mPauseButton.setEnabled(false);
		} catch (IncompatibleClassChangeError ex) {
		}
	}

	/**
	 * <p>
	 * Change the animation style resource for this controller.
	 * </p>
	 * 
	 * <p>
	 * If the controller is showing, calling this method will take effect only the
	 * next time the controller is shown.
	 * </p>
	 * 
	 * @param animationStyle animation style to use when the controller appears
	 * and disappears. Set to -1 for the default animation, 0 for no animation, or
	 * a resource identifier for an explicit animation.
	 * 
	 */
	public void setAnimationStyle(int animationStyle) {
		mAnimStyle = animationStyle;
	}

	/**
	 * Show the controller on screen. It will go away automatically after
	 * 'timeout' milliseconds of inactivity.
	 * 
	 * @param timeout The timeout in milliseconds. Use 0 to show the controller
	 * until hide() is called.
	 */
	public void show(int timeout) {
		if (!mShowing && mAnchor != null && mAnchor.getWindowToken() != null) {
			if (mPauseButton != null)
				mPauseButton.requestFocus();
			disableUnsupportedButtons();

			if (mFromXml) {
				setVisibility(View.VISIBLE);
			} else {
				int[] location = new int[2];
				
				if (playerView.getVisibility() != View.VISIBLE) {
					//xlhadd
					playerView.setVisibility(View.VISIBLE);
					mPauseButton.requestFocus();										
					playerView.setFocusable(true);
					playerView.setFocusableInTouchMode(true);
					playerView.requestLayout();
					
				}

				mAnchor.getLocationOnScreen(location);
				Rect anchorRect = new Rect(location[0], location[1], location[0] + mAnchor.getWidth(), location[1] + mAnchor.getHeight());

				mWindow.setAnimationStyle(mAnimStyle);
				mWindow.showAtLocation(mAnchor, Gravity.NO_GRAVITY, anchorRect.left, anchorRect.bottom);
			}
			mShowing = true;
			if (mShownListener != null)
				mShownListener.onShown();
		}
		updatePausePlay();
		mHandler.sendEmptyMessage(SHOW_PROGRESS);

		if (timeout != 0) {
			mHandler.removeMessages(FADE_OUT);
			mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT), timeout);
		}
	}

	public boolean isShowing() {
		return mShowing;
	}

	public void hide() {
		if (mAnchor == null)
			return;
		
		//xlhadd
		if (mShowing) {
			if (playerView.getVisibility() == View.VISIBLE) {
				playerView.setVisibility(View.INVISIBLE);
			}/*else{
				playerView.setVisibility(View.VISIBLE);
				mPauseButton.requestFocus();
			}*/
			
			try {
				mHandler.removeMessages(SHOW_PROGRESS);
				if (mFromXml)
					setVisibility(View.GONE);
				else
					mWindow.dismiss();
			} catch (IllegalArgumentException ex) {
				Log.d("media", "MediaController already removed");
			}
			mShowing = false;
			if (mHiddenListener != null)
				mHiddenListener.onHidden();
		}
	}

	public interface OnShownListener {
		public void onShown();
	}

	private OnShownListener mShownListener;

	public void setOnShownListener(OnShownListener l) {
		mShownListener = l;
	}

	public interface OnHiddenListener {
		public void onHidden();
	}

	private OnHiddenListener mHiddenListener;

	public void setOnHiddenListener(OnHiddenListener l) {
		mHiddenListener = l;
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			long pos;
			switch (msg.what) {
			case FADE_OUT:
				hide();
				break;
			case SHOW_PROGRESS:
				pos = setProgress();
				if (!mDragging && mShowing) {
					msg = obtainMessage(SHOW_PROGRESS);
					sendMessageDelayed(msg, 1000 - (pos % 1000));
					updatePausePlay();
				}
				break;
			}
		}
	};
	
	public interface IScaleListener{
		public void onScale(int type);
	}
	private IScaleListener mScaleListener;
	public void setOnScaleListener(IScaleListener i){
		mScaleListener = i;
	}

	public interface IListListener{
		public void onList();
	}
	private IListListener mListListener;
	public void setOnListListener(IListListener i){
		mListListener = i;
	}

	private long setProgress() {
		if (mPlayer == null || mDragging)
			return 0;

		long position = mPlayer.getCurrentPosition();
		long duration = mPlayer.getDuration();
		
		if (mProgress != null) {
			if (duration > 0) {
				long pos = 1000L * position / duration;
				mProgress.setProgress((int) pos);
			}
			int percent = mPlayer.getBufferPercentage();
			setBufferPercent((int)(position*100 / duration));
//			mProgress.setSecondaryProgress(percent * 10);
		}

		mDuration = duration;

		if (mEndTime != null)
			mEndTime.setText(StringUtils.generateTime(mDuration));
		if (mCurrentTime != null)
			mCurrentTime.setText(StringUtils.generateTime(position));

		return position;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		show(sDefaultTimeout);
		return true;
	}

	@Override
	public boolean onTrackballEvent(MotionEvent ev) {
		show(sDefaultTimeout);
		return false;
	}

	//xlhadd
	public Handler handler;
	public boolean livetv;
	public void setHandler(Handler handler){
		this.handler=handler;
	}
	public void setBoolean(boolean livetv){
		this.livetv=livetv;
	}
	//
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		
		
		int keyCode = event.getKeyCode();
		
		if (event.getRepeatCount() == 0 && (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE)) {
			doPauseResume();
			show(sDefaultTimeout);
			if (mPauseButton != null)
				mPauseButton.requestFocus();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
			if (mPlayer.isPlaying()) {
				mPlayer.pause();
				updatePausePlay();
			}
			return true;
		} else if (/*keyCode == KeyEvent.KEYCODE_BACK ||*/ keyCode == KeyEvent.KEYCODE_MENU) {
			hide();
			
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_BACK){
			if(event.getAction()==KeyEvent.ACTION_DOWN){
			if (playerView.getVisibility() == View.VISIBLE) {
				playerView.setVisibility(View.INVISIBLE);
			}else{
				handler.sendEmptyMessage(ConstValues.TV_exit);
			}
			}
		} else if((keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
				|| (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
				|| (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
				|| (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
				|| (keyCode == KeyEvent.KEYCODE_DPAD_UP)){
			if (playerView.getVisibility() == View.INVISIBLE) {
				playerView.setVisibility(View.VISIBLE);
				mPauseButton.requestFocus();
			}
		}else {			
				toggleMediaControlsVisiblity();	//xlhadd			
			//show(sDefaultTimeout);
		}
		
		return super.dispatchKeyEvent(event);

	}
	
/*	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK
				&& keyCode != KeyEvent.KEYCODE_MENU
				&& keyCode != KeyEvent.KEYCODE_CALL
				&& keyCode != KeyEvent.KEYCODE_VOLUME_DOWN
				&& keyCode != KeyEvent.KEYCODE_VOLUME_UP
				&& keyCode != KeyEvent.KEYCODE_ENDCALL;

		if (isKeyCodeSupported) {
			show(sDefaultTimeout);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_DOWN) {
			
			if (playerView.getVisibility() == View.VISIBLE) {
				
				playerView.setVisibility(View.INVISIBLE);
			}else {
				//handler.;
			}
			return true;
		}
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}*/

	public void toggleMediaControlsVisiblity() {
		if (isShowing()) {
			hide();
		} else {
			show();
		}
	}	
	
	private View.OnClickListener mPauseListener = new View.OnClickListener() {
		public void onClick(View v) {
			doPauseResume();
			show(sDefaultTimeout);
		}
	};

	private void updatePausePlay() {
		if (mRoot == null || mPauseButton == null)
			return;

		if (mPlayer.isPlaying()){
			mPauseButton.setImageResource(R.drawable.mediacontroller_pause_button);	
			mBigPauseButton.setVisibility(View.GONE);
		}
		else{
			mPauseButton.setImageResource(R.drawable.mediacontroller_play_button);
			mBigPauseButton.setVisibility(View.VISIBLE);
		}
		
	}

	private void doPauseResume() {
		if (mPlayer.isPlaying())
			mPlayer.pause();
		else
			mPlayer.start();
		updatePausePlay();
	}

	private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
		public void onStartTrackingTouch(SeekBar bar) {
			mDragging = true;
			show(3600000);
			mHandler.removeMessages(SHOW_PROGRESS);
			if (mInstantSeeking)
				mAM.setStreamMute(AudioManager.STREAM_MUSIC, true);
			if (mInfoView != null) {
				mInfoView.setText("");
				mInfoView.setVisibility(View.VISIBLE);
			}
		}

		public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
			if (!fromuser)
				return;
			long newposition = (mDuration * progress) / 1000;
			String time = StringUtils.generateTime(newposition);
			if (mInstantSeeking)
				mPlayer.seekTo(newposition);
			if (mInfoView != null)
				mInfoView.setText(time);
			if (mCurrentTime != null)
				mCurrentTime.setText(time);
		}

		public void onStopTrackingTouch(SeekBar bar) {
			if (!mInstantSeeking)
				mPlayer.seekTo((mDuration * bar.getProgress()) / 1000);
			if (mInfoView != null) {
				mInfoView.setText("");
				mInfoView.setVisibility(View.GONE);
			}
			show(sDefaultTimeout);
			mHandler.removeMessages(SHOW_PROGRESS);
			mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
			mDragging = false;
			mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
		}
	};

	@Override
	public void setEnabled(boolean enabled) {
		if (mPauseButton != null)
			mPauseButton.setEnabled(enabled);
		if (mProgress != null)
			mProgress.setEnabled(enabled);
		disableUnsupportedButtons();
		super.setEnabled(enabled);
	}

	public interface MediaPlayerControl {
		void start();

		void pause();

		long getDuration();

		long getCurrentPosition();

		void seekTo(long pos);

		boolean isPlaying();

		int getBufferPercentage();

		boolean canPause();

		boolean canSeekBackward();

		boolean canSeekForward();
	}
	
	public void setScaleButtonFocus(){
		mScaleButton.requestFocus();
	}
	
	public void ShowBigPauseButton() {
		
		if(mBigPauseButton.getVisibility()==View.GONE)
		mBigPauseButton.setVisibility(View.VISIBLE);
/*		show(sDefaultTimeout);
		mPlayer.pause();
		updatePausePlay();
		mHandler.removeMessages(FADE_OUT);
		mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT),
				sDefaultTimeout);*/
	}

	public void HideBigPauseButton() {
	
/*		mPlayer.start();
		updatePausePlay();*/
		if(mBigPauseButton.getVisibility()==View.VISIBLE)
		mBigPauseButton.setVisibility(View.GONE);
	}

}
