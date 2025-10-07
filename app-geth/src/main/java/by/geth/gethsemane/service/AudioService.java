package by.geth.gethsemane.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;

import by.geth.gethsemane.app.AppNotificationManager;
import by.geth.gethsemane.data.base.AudioItem;

public class AudioService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    public enum State {STOP, PREPARE, PLAY, PAUSE}

    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_STATE_CHANGED = "ACTION_STATE_CHANGED";

    public static final String EXTRA_URI = "EXTRA_URI";

    public static IntentFilter getFullIntentFilter() {
        return new IntentFilter(ACTION_STATE_CHANGED);
    }

    private AudioManager audioManager;
    private AudioFocusRequest audioFocusRequest;
    private MediaPlayer mPlayer;
    private State mState = State.STOP;
    private String mUri;
    private String mTitle;
    private String mAuthor;

    private boolean mPlaybackDelayed = false;
    private boolean mResumeOnFocusGain = false;

    @Override
    public void onCreate() {
        super.onCreate();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case ACTION_PAUSE:
                        pause();
                        break;
                    case ACTION_PLAY:
                        play();
                        break;
                    case ACTION_STOP:
                        release();
                        break;
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        release();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        requestAudioFocusAndPlay();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        release();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        release();
        return true;
    }

    private void play() {
        mPlayer.start();
        setState(State.PLAY);

        startForeground(AppNotificationManager.ID_AUDIO,
            AppNotificationManager.getInstance().getAudioNotification(mAuthor, mTitle, true));
    }

    private void pause() {
        mPlayer.pause();
        setState(State.PAUSE);

        startForeground(AppNotificationManager.ID_AUDIO,
            AppNotificationManager.getInstance().getAudioNotification(mAuthor, mTitle, false));
    }

    private void requestAudioFocusAndPlay() {
        int requestResult;
        if (Build.VERSION.SDK_INT < 26) {
            requestResult = audioManager.requestAudioFocus(onAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        } else {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
            audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setWillPauseWhenDucked(true)
                .setOnAudioFocusChangeListener(onAudioFocusChangeListener)
                .build();
            requestResult = audioManager.requestAudioFocus(audioFocusRequest);
        }

        switch (requestResult) {
            case AudioManager.AUDIOFOCUS_REQUEST_FAILED: {
                mPlaybackDelayed = false;
                release();
                break;
            }
            case AudioManager.AUDIOFOCUS_REQUEST_GRANTED: {
                mPlaybackDelayed = false;
                play();
                break;
            }
            case AudioManager.AUDIOFOCUS_REQUEST_DELAYED: {
                mPlaybackDelayed = true;
                break;
            }
        }
    }

    private void abandonAudioFocus() {
        if (Build.VERSION.SDK_INT < 26) {
            audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        } else {
            if (audioFocusRequest != null) {
                audioManager.abandonAudioFocusRequest(audioFocusRequest);
            }
        }
        mPlaybackDelayed = false;
        mResumeOnFocusGain = false;
    }

    private void release() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        abandonAudioFocus();
        setState(State.STOP);
        stopForeground(true);
        mUri = mTitle = mAuthor = null;
    }

    private void setState(State state) {
        mState = state;
        notifyStateChanged();
    }

    private void notifyStateChanged() {
        Intent intent = new Intent(ACTION_STATE_CHANGED);
        intent.putExtra(EXTRA_URI, mUri);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = focusChange -> {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (mPlaybackDelayed || mResumeOnFocusGain) {
                    mPlaybackDelayed = false;
                    mResumeOnFocusGain = false;
                    play();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                mPlaybackDelayed = false;
                mResumeOnFocusGain = false;
                pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                mPlaybackDelayed = false;
                mResumeOnFocusGain = mPlayer.isPlaying();
                pause();
                break;
        }
    };

    public class Binder extends android.os.Binder {

        public void play(AudioItem item) {
            String uri = item.getLocalPath() == null ? item.getRemoteUrl() : item.getLocalPath();
            String title = item.getTitle();
            String author = item.getAuthor();
            play(uri, title, author);
        }

        public void play(String uri, String title, String author) {
            mUri = uri;
            mTitle = title;
            mAuthor = author;

            try {
                mPlayer = new MediaPlayer();
                mPlayer.setOnPreparedListener(AudioService.this);
                mPlayer.setOnCompletionListener(AudioService.this);
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mPlayer.setDataSource(uri);
                mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                mPlayer.prepareAsync();

                setState(State.PREPARE);
            } catch (IOException e) {
                e.printStackTrace();

                mPlayer.release();
                mPlayer = null;
            }
        }

        public void stop() {
            release();
        }

        public void pause() {
            AudioService.this.pause();
        }

        public void resume() {
            AudioService.this.play();
        }

        public void seekTo(int msec) {
            if (mPlayer != null) {
                mPlayer.seekTo(msec);
                notifyStateChanged();
            }
        }

        public String getUri() {
            return mUri;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getAuthor() {
            return mAuthor;
        }

        public int getCurrentPosition() {
            if (getState() == State.PLAY || getState() == State.PAUSE)
                return mPlayer.getCurrentPosition();
            else
                return 0;
        }

        public int getDuration() {
            if (getState() == State.PLAY || getState() == State.PAUSE)
                return mPlayer.getDuration();
            else
                return 0;
        }

        public State getState() {
            return mState;
        }
    }
}
