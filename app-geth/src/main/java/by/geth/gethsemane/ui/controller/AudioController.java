package by.geth.gethsemane.ui.controller;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import by.geth.gethsemane.BuildConfig;
import by.geth.gethsemane.R;
import by.geth.gethsemane.service.AudioService;

public class AudioController implements ServiceConnection, View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private static final int UPDATE_TIME_DELAY_MS = 1000;

    private Activity mActivity;

    private View mMainContentView;
    private SlidingUpPanelLayout mSlidingUpLayout;
	private View mAudioContent;

    private AudioService.Binder mAudioService;
    private Handler mHandler = new Handler();
    private boolean mIsUpdateServiceStarted = false;

    private TextView mTitleView;
    private SeekBar mProgressBar;
    private ImageView mPauseResumeButton;
    private ImageView mStopButton;
    private TextView mProgressTextView;

    public AudioController(Activity activity) {
        mActivity = activity;
    }

    public void onCreate() {
        mMainContentView = mActivity.findViewById(R.id.main_content);
        mSlidingUpLayout = mActivity.findViewById(R.id.sliding_layout);
        mSlidingUpLayout.setTouchEnabled(false);
        mSlidingUpLayout.addPanelSlideListener(mPanelSlideListener);
		mAudioContent = mActivity.findViewById(R.id.audio_content);

        mTitleView = mActivity.findViewById(R.id.audio_title);
        mProgressBar = mActivity.findViewById(R.id.audio_progress_bar);
        mPauseResumeButton = mActivity.findViewById(R.id.audio_pause_resume);
        mStopButton = mActivity.findViewById(R.id.audio_stop);
        mProgressTextView = mActivity.findViewById(R.id.audio_progress_text);

        mPauseResumeButton.setOnClickListener(this);
        mStopButton.setOnClickListener(this);

        mProgressBar.setOnSeekBarChangeListener(this);

        mActivity.startService(new Intent(mActivity, AudioService.class));
    }

    public void onDestroy() {
        mSlidingUpLayout.removePanelSlideListener(mPanelSlideListener);
    }

    public void onStart() {
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mAudioBroadcastReceiver,
                AudioService.getFullIntentFilter());

        if (mAudioService != null) {
            updateUI();
            if (mAudioService.getState() == AudioService.State.PLAY)
                startUpdateService();
        }

        mActivity.bindService(new Intent(mActivity, AudioService.class), this, Service.BIND_AUTO_CREATE);
    }

    public void onStop() {
        stopUpdateService();
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mAudioBroadcastReceiver);
        mActivity.unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mAudioService = (AudioService.Binder) service;
        updateUI();
        if (mAudioService.getState() == AudioService.State.PLAY)
            startUpdateService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mAudioService = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.audio_pause_resume:
                if (mAudioService != null) {
                    switch (mAudioService.getState()) {
                        case PLAY:
                            mAudioService.pause();
                            break;
                        case PAUSE:
                            mAudioService.resume();
                            break;
                    }
                }
                break;
            case R.id.audio_stop:
                if (mAudioService != null)
                    mAudioService.stop();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mAudioService != null)
            mAudioService.seekTo(seekBar.getProgress());
    }

    public void playLive() {
        String uri = BuildConfig.URL_LIVE_AUDIO;
        String author = mActivity.getString(R.string.app_title);
        String title = mActivity.getString(R.string.live);
        if (mAudioService != null) {
            if (uri.equals(mAudioService.getUri())) {
                if (mAudioService.getState() == AudioService.State.STOP)
                    mAudioService.play(uri, title, author);
                else
                    mAudioService.stop();
            } else {
                mAudioService.stop();
                mAudioService.play(uri, title, author);
            }
        }
    }

    private void startUpdateService() {
        if (!mIsUpdateServiceStarted) {
            mHandler.postDelayed(mUpdateTask, UPDATE_TIME_DELAY_MS);
            mIsUpdateServiceStarted = true;
        }
    }

    private void stopUpdateService() {
        mHandler.removeCallbacks(mUpdateTask);
        mIsUpdateServiceStarted = false;
    }

    private void updateUI() {
        if (mAudioService != null) {
            AudioService.State state = mAudioService.getState();
            if (state == AudioService.State.STOP || state == AudioService.State.PREPARE) {
                closePanel();
            } else {
                String title = mActivity.getString(R.string.audio_controller_pattern_title,
                        mAudioService.getAuthor(), mAudioService.getTitle());
                mTitleView.setText(title);

                int currentPosition = mAudioService.getCurrentPosition();
                int duration = mAudioService.getDuration();

                mProgressBar.setMax(duration);
                mProgressBar.setProgress(currentPosition);

                String progress = mActivity.getString(R.string.audio_controller_pattern_progress,
                        toString(currentPosition), toString(duration));
                mProgressTextView.setText(progress);

                mPauseResumeButton.setSelected(state == AudioService.State.PLAY);
                openPanel();
            }
        }
    }

    private void openPanel() {
		if (mSlidingUpLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
			mMainContentView.setPadding(0, 0, 0, mAudioContent.getHeight());
		} else {
			mHandler.post(() -> mSlidingUpLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED));
		}
    }

    private void closePanel() {
        mHandler.post(() -> mSlidingUpLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED));
    }

    private String toString(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        int hours = minutes / 60;
        seconds -= minutes * 60;
        minutes -= hours * 60;
        String hstr = String.valueOf(hours);
        String mstr = minutes < 10 ? "0" + minutes : "" + minutes;
        String sstr = seconds < 10 ? "0" + seconds : "" + seconds;
        return hours == 0 ? (mstr+":"+sstr) : (hstr+":"+mstr+":"+sstr);
    }

    private SlidingUpPanelLayout.PanelSlideListener mPanelSlideListener =
            new SlidingUpPanelLayout.SimplePanelSlideListener() {

        @Override
        public void onPanelSlide(View panel, float slideOffset) {
            mMainContentView.setPadding(0, 0, 0, (int) (panel.getHeight() * slideOffset));
        }
    };

    private BroadcastReceiver mAudioBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mAudioService != null) {
                switch (mAudioService.getState()) {
                    case PLAY:
                        startUpdateService();
                        break;
                    case PAUSE:
                    case STOP:
                        stopUpdateService();
                        break;
                }
            }
            updateUI();
        }
    };

    private Runnable mUpdateTask = new Runnable() {
        @Override
        public void run() {
            updateUI();
            mHandler.postDelayed(this, UPDATE_TIME_DELAY_MS);
        }
    };
}
