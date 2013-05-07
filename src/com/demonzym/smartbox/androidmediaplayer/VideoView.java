/*
 * Copyright (C) 2011 VOV IO (http://vov.io/)
 */

package com.demonzym.smartbox.androidmediaplayer;

import io.vov.utils.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.demonzym.smartbox.R;
import com.demonzym.smartbox.protocal.ConstValues;

/**
 * Displays a video file. The VideoView class can load images from various
 * sources (such as resources or content providers), takes care of computing its
 * measurement from the video so that it can be used in any layout manager, and
 * provides various display options such as scaling and tinting.
 * 
 * VideoView also provide many wrapper methods for
 * {@link io.vov.vitamio.MediaPlayer}, such as {@link #getVideoWidth()},
 * {@link #setSubShown(boolean)}
 */
public class VideoView extends SurfaceView implements MediaController.MediaPlayerControl {
	private Uri mUri;
	private long mDuration;

	private static final int STATE_ERROR = -1;
	private static final int STATE_IDLE = 0;
	private static final int STATE_PREPARING = 1;
	private static final int STATE_PREPARED = 2;
	private static final int STATE_PLAYING = 3;
	private static final int STATE_PAUSED = 4;
	private static final int STATE_PLAYBACK_COMPLETED = 5;
	private static final int STATE_SUSPEND = 6;
	private static final int STATE_RESUME = 7;
	private static final int STATE_SUSPEND_UNSUPPORTED = 8;

	private int mCurrentState = STATE_IDLE;
	private int mTargetState = STATE_IDLE;

	private float mAspectRatio = 0;
	private int mVideoLayout = VIDEO_LAYOUT_SCALE;
	public static final int VIDEO_LAYOUT_ORIGIN = 0;
	public static final int VIDEO_LAYOUT_SCALE = 1;
	public static final int VIDEO_LAYOUT_STRETCH = 2;
	public static final int VIDEO_LAYOUT_ZOOM = 3;

	private SurfaceHolder mSurfaceHolder = null;
	private MediaPlayer mMediaPlayer = null;
	private int mVideoWidth;
	private int mVideoHeight;
//	private float mVideoAspectRatio;
	private int mSurfaceWidth;
	private int mSurfaceHeight;
	private MediaController mMediaController;
	private OnCompletionListener mOnCompletionListener;
	private OnPreparedListener mOnPreparedListener;
	private OnErrorListener mOnErrorListener;
	private OnSeekCompleteListener mOnSeekCompleteListener;
//	private OnSubtitleUpdateListener mOnSubtitleUpdateListener;
	private OnInfoListener mOnInfoListener;
	private OnBufferingUpdateListener mOnBufferingUpdateListener;
	private int mCurrentBufferPercentage;
	private long mSeekWhenPrepared;
	private boolean mCanPause = true;
	private boolean mCanSeekBack = true;
	private boolean mCanSeekForward = true;
	private Context mContext;

	public VideoView(Context context) {
		super(context);
		initVideoView(context);
	}

	public VideoView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public VideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initVideoView(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
		int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

	/**
	 * Set the display options
	 * 
	 * @param layout <ul>
	 * <li>{@link #VIDEO_LAYOUT_ORIGIN}
	 * <li>{@link #VIDEO_LAYOUT_SCALE}
	 * <li>{@link #VIDEO_LAYOUT_STRETCH}
	 * <li>{@link #VIDEO_LAYOUT_ZOOM}
	 * </ul>
	 * @param aspectRatio video aspect ratio, will audo detect if 0.
	 */
//	public void setVideoLayout(int layout, float aspectRatio) {
//		LayoutParams lp = getLayoutParams();
//		DisplayMetrics disp = mContext.getResources().getDisplayMetrics();
//		int windowWidth = disp.widthPixels, windowHeight = disp.heightPixels;
//		float windowRatio = windowWidth / (float) windowHeight;
//		float videoRatio = aspectRatio <= 0.01f ? mVideoAspectRatio : aspectRatio;
//		mSurfaceHeight = mVideoHeight;
//		mSurfaceWidth = mVideoWidth;
//		if (VIDEO_LAYOUT_ORIGIN == layout && mSurfaceWidth < windowWidth && mSurfaceHeight < windowHeight) {
//			lp.width = (int) (mSurfaceHeight * videoRatio);
//			lp.height = mSurfaceHeight;
//		} else if (layout == VIDEO_LAYOUT_ZOOM) {
//			lp.width = windowRatio > videoRatio ? windowWidth : (int) (videoRatio * windowHeight);
//			lp.height = windowRatio < videoRatio ? windowHeight : (int) (windowWidth / videoRatio);
//		} else {
//			boolean full = layout == VIDEO_LAYOUT_STRETCH;
//			lp.width = (full || windowRatio < videoRatio) ? windowWidth : (int) (videoRatio * windowHeight);
//			lp.height = (full || windowRatio > videoRatio) ? windowHeight : (int) (windowWidth / videoRatio);
//		}
//		setLayoutParams(lp);
//		getHolder().setFixedSize(mSurfaceWidth, mSurfaceHeight);
////		Log.d("VIDEO: %dx%dx%f, Surface: %dx%d, LP: %dx%d, Window: %dx%dx%f", mVideoWidth, mVideoHeight, mVideoAspectRatio, mSurfaceWidth, mSurfaceHeight, lp.width, lp.height, windowWidth, windowHeight, windowRatio);
//		mVideoLayout = layout;
//		mAspectRatio = aspectRatio;
//	}

	private void initVideoView(Context ctx) {
		mContext = ctx;
		mVideoWidth = 0;
		mVideoHeight = 0;
		getHolder().addCallback(mSHCallback);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
		mCurrentState = STATE_IDLE;
		mTargetState = STATE_IDLE;
		if (ctx instanceof Activity)
			((Activity) ctx).setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	public boolean isValid() {
		return (mSurfaceHolder != null && mSurfaceHolder.getSurface().isValid());
	}

	public void setVideoPath(String path) {
		setVideoURI(Uri.parse(path));
	}

	public void setVideoURI(Uri uri) {
		mUri = uri;
		mSeekWhenPrepared = 0;
		openVideo();
		requestLayout();
		invalidate();
	}
	
	public Uri getVideoUri(){
		return mUri;
	}

	public void stopPlayback() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
			mCurrentState = STATE_IDLE;
			mTargetState = STATE_IDLE;
		}
	}
    public void onStop()
    {
    	if(mMediaPlayer!=null)
    	{
    		mMediaPlayer.pause();
    		mMediaPlayer.stop();
    	}
    }

	private void openVideo() {
		if (mUri == null || mSurfaceHolder == null)
			return;

		Intent i = new Intent("com.android.music.musicservicecommand");
		i.putExtra("command", "pause");
		mContext.sendBroadcast(i);

		release(false);
		try {
			mDuration = -1;
			mCurrentBufferPercentage = 0;
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setOnPreparedListener(mPreparedListener);
//			mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
			mMediaPlayer.setOnCompletionListener(mCompletionListener);
			mMediaPlayer.setOnErrorListener(mErrorListener);
			mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
			mMediaPlayer.setOnInfoListener(mInfoListener);
			mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
//			mMediaPlayer.setOnSubtitleUpdateListener(mSubtitleUpdateListener);
			mMediaPlayer.setDataSource(mContext, mUri);
			mMediaPlayer.setDisplay(mSurfaceHolder);
			mMediaPlayer.setScreenOnWhilePlaying(true);
			mMediaPlayer.prepareAsync();
			mCurrentState = STATE_PREPARING;
			attachMediaController();
		} catch (IOException ex) {
//			Log.e("Unable to open content: " + mUri, ex);
			mCurrentState = STATE_ERROR;
			mTargetState = STATE_ERROR;
			mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
			return;
		} catch (IllegalArgumentException ex) {
//			Log.e("Unable to open content: " + mUri, ex);
			mCurrentState = STATE_ERROR;
			mTargetState = STATE_ERROR;
			mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
			return;
		}
	}

	public void setMediaController(MediaController controller) {
		if (mMediaController != null)
			mMediaController.hide();
		mMediaController = controller;
		attachMediaController();
	}

	private void attachMediaController() {
		if (mMediaPlayer != null && mMediaController != null) {
			mMediaController.setMediaPlayer(this);
			View anchorView = this.getParent() instanceof View ? (View) this.getParent() : this;
			mMediaController.setAnchorView(anchorView);
			mMediaController.setEnabled(isInPlaybackState());

			if (mUri != null) {
				List<String> paths = mUri.getPathSegments();
				String name = paths == null || paths.isEmpty() ? "null" : paths.get(paths.size() - 1);
				mMediaController.setFileName("%0");
			}
		}
	}

//	OnVideoSizeChangedListener mSizeChangedListener = new OnVideoSizeChangedListener() {
//		public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
////			Log.d("onVideoSizeChanged: (%dx%d)", width, height);
//			mVideoWidth = mp.getVideoWidth();
//			mVideoHeight = mp.getVideoHeight();
//			mVideoAspectRatio = mp.getVideoAspectRatio();
//			if (mVideoWidth != 0 && mVideoHeight != 0)
//				setVideoLayout(mVideoLayout, mAspectRatio);
//		}
//	};

	OnPreparedListener mPreparedListener = new OnPreparedListener() {
		public void onPrepared(MediaPlayer mp) {
//			Log.d("onPrepared");
			mCurrentState = STATE_PREPARED;
			mTargetState = STATE_PLAYING;

			if (mOnPreparedListener != null)
				mOnPreparedListener.onPrepared(mMediaPlayer);
			if (mMediaController != null)
				mMediaController.setEnabled(true);
			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();
//			mVideoAspectRatio = mp.getVideoAspectRatio();

			long seekToPosition = mSeekWhenPrepared;

			if (seekToPosition != 0)
				seekTo(seekToPosition);
			if (mVideoWidth != 0 && mVideoHeight != 0) {
//				setVideoLayout(mVideoLayout, mAspectRatio);
				if (mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
					if (mTargetState == STATE_PLAYING) {
						start();
						if (mMediaController != null)
							mMediaController.show();
					} else if (!isPlaying() && (seekToPosition != 0 || getCurrentPosition() > 0)) {
						if (mMediaController != null)
							mMediaController.show(0);
					}
				}
			} else if (mTargetState == STATE_PLAYING) {
				start();
			}
		}
	};

	private OnCompletionListener mCompletionListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mp) {
//			Log.d("onCompletion");
			mCurrentState = STATE_PLAYBACK_COMPLETED;
			mTargetState = STATE_PLAYBACK_COMPLETED;
			if (mMediaController != null)
				mMediaController.hide();
			if (mOnCompletionListener != null)
				mOnCompletionListener.onCompletion(mMediaPlayer);
		}
	};

	private OnErrorListener mErrorListener = new OnErrorListener() {
		public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
//			Log.d("Error: %d, %d", framework_err, impl_err);
			mCurrentState = STATE_ERROR;
			mTargetState = STATE_ERROR;
			if (mMediaController != null)
				mMediaController.hide();

			if (mOnErrorListener != null) {
				if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err))
					return true;
			}

			if (getWindowToken() != null) {
				int message = framework_err == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK ? R.string.VideoView_error_text_invalid_progressive_playback : R.string.VideoView_error_text_unknown;

				new AlertDialog.Builder(mContext).setTitle(R.string.VideoView_error_title).setMessage(message).setPositiveButton(R.string.VideoView_error_button, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (mOnCompletionListener != null)
							mOnCompletionListener.onCompletion(mMediaPlayer);
					}
				}).setCancelable(false).show();
			}
			return true;
		}
	};

	//xlh 控制条变化是出发调用
	private OnBufferingUpdateListener mBufferingUpdateListener = new OnBufferingUpdateListener() {
		public void onBufferingUpdate(MediaPlayer mp, int percent) {
			mCurrentBufferPercentage = percent;
			if(mMediaController != null)
				mMediaController.setBufferPercent(-1);			//百分比的percent值
			if (mOnBufferingUpdateListener != null)
				mOnBufferingUpdateListener.onBufferingUpdate(mp, percent);
		}
	};

	private OnInfoListener mInfoListener = new OnInfoListener() {
		public boolean onInfo(MediaPlayer mp, int what, int extra) {
//			Log.d("onInfo: (%d, %d)", what, extra);
			if (mOnInfoListener != null) {
				mOnInfoListener.onInfo(mp, what, extra);
			} else if (mMediaPlayer != null) {
//				if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START)
//					mMediaPlayer.pause();
//				else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END)
//					mMediaPlayer.start();
			}

			return true;
		}
	};

	private OnSeekCompleteListener mSeekCompleteListener = new OnSeekCompleteListener() {
		public void onSeekComplete(MediaPlayer mp) {
//			Log.d("onSeekComplete");
			if (mOnSeekCompleteListener != null)
				mOnSeekCompleteListener.onSeekComplete(mp);
		}
	};

//	private OnSubtitleUpdateListener mSubtitleUpdateListener = new OnSubtitleUpdateListener() {
//		public void onSubtitleUpdate(byte[] pixels, int width, int height) {
////			Log.i("onSubtitleUpdate: bitmap subtitle, %dx%d", width, height);
//			if (mOnSubtitleUpdateListener != null)
//				mOnSubtitleUpdateListener.onSubtitleUpdate(pixels, width, height);
//		}
//
//		public void onSubtitleUpdate(String text) {
////			Log.i("onSubtitleUpdate: %s", text);
//			if (mOnSubtitleUpdateListener != null)
//				mOnSubtitleUpdateListener.onSubtitleUpdate(text);
//		}
//	};

	public void setOnPreparedListener(OnPreparedListener l) {
		mOnPreparedListener = l;
	}

	public void setOnCompletionListener(OnCompletionListener l) {
		mOnCompletionListener = l;
	}

	public void setOnErrorListener(OnErrorListener l) {
		mOnErrorListener = l;
	}

	public void setOnBufferingUpdateListener(OnBufferingUpdateListener l) {
		mOnBufferingUpdateListener = l;
	}

	public void setOnSeekCompleteListener(OnSeekCompleteListener l) {
		mOnSeekCompleteListener = l;
	}

//	public void setOnSubtitleUpdateListener(OnSubtitleUpdateListener l) {
//		mOnSubtitleUpdateListener = l;
//	}

	public void setOnInfoListener(OnInfoListener l) {
		mOnInfoListener = l;
	}

	SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
			mSurfaceWidth = w;
			mSurfaceHeight = h;
			boolean isValidState = (mTargetState == STATE_PLAYING);
			boolean hasValidSize = (mVideoWidth == w && mVideoHeight == h);
			if (mMediaPlayer != null && isValidState && hasValidSize) {
				if (mSeekWhenPrepared != 0)
					seekTo(mSeekWhenPrepared);
				start();
				if (mMediaController != null) {
					if (mMediaController.isShowing())
						mMediaController.hide();
					mMediaController.show();
				}
			}
		}

		public void surfaceCreated(SurfaceHolder holder) {
			mSurfaceHolder = holder;
			if (mMediaPlayer != null && mCurrentState == STATE_SUSPEND && mTargetState == STATE_RESUME) {
				mMediaPlayer.setDisplay(mSurfaceHolder);
				resume();
			} else {
				openVideo();
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			mSurfaceHolder = null;
			if (mMediaController != null)
				mMediaController.hide();
			if (mCurrentState != STATE_SUSPEND)
				release(true);
		}
	};

	private void release(boolean cleartargetstate) {
		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
			mCurrentState = STATE_IDLE;
			if (cleartargetstate)
				mTargetState = STATE_IDLE;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (isInPlaybackState() && mMediaController != null)
			mMediaController.toggleMediaControlsVisiblity();
		return false;
	}

	@Override
	public boolean onTrackballEvent(MotionEvent ev) {
		if (isInPlaybackState() && mMediaController != null)
			mMediaController.toggleMediaControlsVisiblity();
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
/*		boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK 
				&& keyCode != KeyEvent.KEYCODE_VOLUME_UP 
				&& keyCode != KeyEvent.KEYCODE_VOLUME_DOWN 
				&& keyCode != KeyEvent.KEYCODE_MENU 
				&& keyCode != KeyEvent.KEYCODE_CALL 
				&& keyCode != KeyEvent.KEYCODE_ENDCALL;*/
		boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK 
                && keyCode != KeyEvent.KEYCODE_MENU 
                && keyCode != KeyEvent.KEYCODE_CALL 
                && keyCode != KeyEvent.KEYCODE_VOLUME_UP
                && keyCode != KeyEvent.KEYCODE_VOLUME_DOWN 
                && keyCode != KeyEvent.KEYCODE_MEDIA_PAUSE
                && keyCode != KeyEvent.KEYCODE_MEDIA_PLAY
                && keyCode != KeyEvent.KEYCODE_MEDIA_PREVIOUS
		        && keyCode != KeyEvent.KEYCODE_MEDIA_NEXT
		        && keyCode != KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                && keyCode != KeyEvent.KEYCODE_ENDCALL;
		//注释掉这段直播就不会有控制条
		if (isInPlaybackState() && isKeyCodeSupported && mMediaController != null) {
			if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE) {
				if (mMediaPlayer.isPlaying()) {
					pause();
					mMediaController.show();
				} else {
					start();
					mMediaController.hide();
				}		
			} else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP && mMediaPlayer.isPlaying()) {
				pause();
				mMediaController.show();
			} else {
				mMediaController.toggleMediaControlsVisiblity();
			}
		}
			/*mMediaController.toggleMediaControlsVisiblity();
			}else if((event.getKeyCode()==KeyEvent.KEYCODE_MEDIA_PAUSE)&&(mMediaController != null)){
				pause();
				mMediaController.ShowBigPauseButton();
				return true;				
			}
			else if((event.getKeyCode()==KeyEvent.KEYCODE_MEDIA_PLAY)&&(mMediaController != null)){
				
				start();
				mMediaController.HideBigPauseButton();
				return true;
			}else if((event.getKeyCode()==KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)&&(mMediaController != null)){
				
				if(mMediaPlayer.isPlaying()){
					mMediaController.ShowBigPauseButton();
				}else {
				   mMediaController.HideBigPauseButton();
				}
				return true;
			}*/		
		if(livetv){
		if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN&&event.getAction()==KeyEvent.ACTION_DOWN){
			handler.sendEmptyMessage(ConstValues.TV_next);
			
		}else if(keyCode == KeyEvent.KEYCODE_DPAD_UP&&event.getAction()==KeyEvent.ACTION_DOWN){
			handler.sendEmptyMessage(ConstValues.TV_last);
			
		}else if(keyCode==KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_DOWN){
			handler.sendEmptyMessage(ConstValues.TV_exit);
		}else{
			return false;
		}
		}
		
		return super.onKeyDown(keyCode, event);
	}


	public void start() {
		if (isInPlaybackState()) {
			mMediaPlayer.start();
			mCurrentState = STATE_PLAYING;
		}
		mTargetState = STATE_PLAYING;
	}

	public void pause() {
		if (isInPlaybackState()) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.pause();
				mCurrentState = STATE_PAUSED;
			}
		}
		mTargetState = STATE_PAUSED;
	}

	public void suspend() {
		if (isInPlaybackState()) {
			release(false);
			mCurrentState = STATE_SUSPEND_UNSUPPORTED;
//			Log.d("Unable to suspend video. Release MediaPlayer.");
		}
	}

	public void resume() {
		if (mSurfaceHolder == null && mCurrentState == STATE_SUSPEND) {
			mTargetState = STATE_RESUME;
		} else if (mCurrentState == STATE_SUSPEND_UNSUPPORTED) {
			openVideo();
		}
	}

	public long getDuration() {
		if (isInPlaybackState()) {
			if (mDuration > 0)
				return mDuration;
			mDuration = mMediaPlayer.getDuration();
			return mDuration;
		}
		mDuration = -1;
		return mDuration;
	}

	public long getCurrentPosition() {
		if (isInPlaybackState())
			return mMediaPlayer.getCurrentPosition();
		return 0;
	}

	public void seekTo(long msec) {
		if (isInPlaybackState()) {
			mMediaPlayer.seekTo((int) msec);
			mSeekWhenPrepared = 0;
		} else {
			mSeekWhenPrepared = msec;
		}
	}

	public boolean isPlaying() {
		return isInPlaybackState() && mMediaPlayer.isPlaying();
	}

	public int getBufferPercentage() {
		if (mMediaPlayer != null)
			return mCurrentBufferPercentage;
		return 0;
	}

	public void setVolume(float leftVolume, float rightVolume) {
		if (mMediaPlayer != null)
			mMediaPlayer.setVolume(leftVolume, rightVolume);
	}

	public int getVideoWidth() {
		return mVideoWidth;
	}

	public int getVideoHeight() {
		return mVideoHeight;
	}

//	public float getVideoAspectRatio() {
//		return mVideoAspectRatio;
//	}

//	public void setVideoQuality(int quality) {
//		if (mMediaPlayer != null)
//			mMediaPlayer.setVideoQuality(quality);
//	}

//	public void setBufferSize(int bufSize) {
//		if (mMediaPlayer != null)
//			mMediaPlayer.setBufferSize(bufSize);
//	}

//	public boolean isBuffering() {
//		if (mMediaPlayer != null)
//			return mMediaPlayer.isBuffering();
//		return false;
//	}

//	public void setMetaEncoding(String encoding) {
//		if (mMediaPlayer != null)
//			mMediaPlayer.setMetaEncoding(encoding);
//	}

//	public String getMetaEncoding() {
//		if (mMediaPlayer != null)
//			return mMediaPlayer.getMetaEncoding();
//		return null;
//	}

//	public HashMap<String, Integer> getAudioTrackMap(String encoding) {
//		if (mMediaPlayer != null)
//			return mMediaPlayer.getAudioTrackMap(encoding);
//		return null;
//	}

//	public int getAudioTrack() {
//		if (mMediaPlayer != null)
//			return mMediaPlayer.getAudioTrack();
//		return -1;
//	}

//	public void setAudioTrack(int audioIndex) {
//		if (mMediaPlayer != null)
//			mMediaPlayer.setAudioTrack(audioIndex);
//	}

//	public void setSubShown(boolean shown) {
//		if (mMediaPlayer != null)
//			mMediaPlayer.setSubShown(shown);
//	}

//	public void setSubEncoding(String encoding) {
//		if (mMediaPlayer != null)
//			mMediaPlayer.setSubEncoding(encoding);
//	}

//	public int getSubLocation() {
//		if (mMediaPlayer != null)
//			return mMediaPlayer.getSubLocation();
//		return -1;
//	}

//	public void setSubPath(String subPath) {
//		if (mMediaPlayer != null)
//			mMediaPlayer.setSubPath(subPath);
//	}

//	public String getSubPath() {
//		if (mMediaPlayer != null)
//			return mMediaPlayer.getSubPath();
//		return null;
//	}

//	public void setSubTrack(int trackId) {
//		if (mMediaPlayer != null)
//			mMediaPlayer.setSubTrack(trackId);
//	}

//	public int getSubTrack() {
//		if (mMediaPlayer != null)
//			return mMediaPlayer.getSubTrack();
//		return -1;
//	}

//	public HashMap<String, Integer> getSubTrackMap(String encoding) {
//		if (mMediaPlayer != null)
//			return mMediaPlayer.getSubTrackMap(encoding);
//		return null;
//	}

	protected boolean isInPlaybackState() {
		return (mMediaPlayer != null && mCurrentState != STATE_ERROR && mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
	}

	public boolean canPause() {
		return mCanPause;
	}

	public boolean canSeekBackward() {
		return mCanSeekBack;
	}

	public boolean canSeekForward() {
		return mCanSeekForward;
	}
}
