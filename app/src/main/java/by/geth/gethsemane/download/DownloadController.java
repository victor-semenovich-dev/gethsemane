package by.geth.gethsemane.download;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.LongSparseArray;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.activeandroid.query.Update;

import java.io.File;
import java.util.ArrayList;

import by.geth.gethsemane.BuildConfig;
import by.geth.gethsemane.data.Event;
import by.geth.gethsemane.data.Photo;
import by.geth.gethsemane.data.Sermon;
import by.geth.gethsemane.data.Song;
import by.geth.gethsemane.data.Witness;
import by.geth.gethsemane.data.Worship;
import by.geth.gethsemane.data.base.AudioItem;
import by.geth.gethsemane.service.AudioService;
import by.geth.gethsemane.util.FileUtils;

public class DownloadController implements ServiceConnection {

    public enum ItemType {EVENT, WORSHIP, SERMON, WITNESS, SONG, PHOTO}

    public static final String ACTION_ITEM_DOWNLOAD_STARTED = "ACTION_ITEM_DOWNLOAD_STARTED";
    public static final String ACTION_ITEM_DOWNLOAD_FINISHED = "ACTION_ITEM_DOWNLOAD_FINISHED";
    public static final String ACTION_ITEM_DOWNLOAD_CANCELLED = "ACTION_ITEM_DOWNLOAD_CANCELLED";
    public static final String ACTION_ITEM_DOWNLOAD_ERROR = "ACTION_ITEM_DOWNLOAD_ERROR";
    public static final String ACTION_ITEM_DELETED = "ACTION_ITEM_DELETED";
    public static final String EXTRA_ITEM_TYPE = "EXTRA_ITEM_TYPE";
    public static final String EXTRA_ITEM_ID = "EXTRA_ITEM_ID";
    public static final String EXTRA_FILE_PATH = "EXTRA_FILE_PATH";

    public static IntentFilter getFullIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_ITEM_DOWNLOAD_STARTED);
        intentFilter.addAction(ACTION_ITEM_DOWNLOAD_FINISHED);
        intentFilter.addAction(ACTION_ITEM_DOWNLOAD_CANCELLED);
        intentFilter.addAction(ACTION_ITEM_DOWNLOAD_ERROR);
        intentFilter.addAction(ACTION_ITEM_DELETED);
        return intentFilter;
    }

    @SuppressLint("StaticFieldLeak")
    private static DownloadController sInstance;

    public static void init(Context context) {
        sInstance = new DownloadController(context);
    }

    public static DownloadController getInstance() {
        return sInstance;
    }

    LongSparseArray<Event> eventIdSparseArray = new LongSparseArray<>();
    LongSparseArray<Worship> worshipIdSparseArray = new LongSparseArray<>();
    LongSparseArray<Sermon> sermonIdSparseArray = new LongSparseArray<>();
    LongSparseArray<Witness> witnessIdSparseArray = new LongSparseArray<>();
    LongSparseArray<Song> songIdSparseArray = new LongSparseArray<>();
    LongSparseArray<Photo> photoIdSparseArray = new LongSparseArray<>();
    AudioItem autoPlayItem;

    private Context mContext;
    private LocalBroadcastManager mBroadcastManager;
    private AudioService.Binder mAudioService;
    private DownloadManager mDownloadManager;

    private DownloadController(Context context) {
        mContext = context;
        mBroadcastManager = LocalBroadcastManager.getInstance(context);
        mContext.bindService(new Intent(mContext, AudioService.class), this, Service.BIND_AUTO_CREATE);
        mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
        if (service instanceof AudioService.Binder) {
            mAudioService = (AudioService.Binder) service;
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mAudioService = null;
    }

    void autoPlay() {
        if (mAudioService != null) {
            mAudioService.play(autoPlayItem);
        }
        autoPlayItem = null;
    }

    public void download(AudioItem item) {
        download(item, false);
    }

    public void download(AudioItem item, boolean isAutoPlay) {
        if (isAutoPlay) {
            autoPlayItem = item;
        }
        if (item instanceof Event) {
            download((Event) item);
        } else if (item instanceof Worship) {
            download((Worship) item);
        } else if (item instanceof Sermon) {
            download((Sermon) item);
        } else if (item instanceof Witness) {
            download((Witness) item);
        } else if (item instanceof Song) {
            download((Song) item);
        } else if (item instanceof Photo) {
            download((Photo) item);
        }
    }

    public void delete(AudioItem item) {
        if (item instanceof Event) {
            delete((Event) item);
        } else if (item instanceof Worship) {
            delete((Worship) item);
        } else if (item instanceof Sermon) {
            delete((Sermon) item);
        } else if (item instanceof Witness) {
            delete((Witness) item);
        } else if (item instanceof Song) {
            delete((Song) item);
        }
    }

    private void download(Event event) {
        Uri sourceUri = Uri.parse(event.getRemoteUrl());
        Uri destinationUri = FileUtils.getDownloadDestinationUri(event);
        if (eventIdSparseArray.indexOfValue(event) < 0) {
            if (destinationUri != null) {
                DownloadManager.Request request = new DownloadManager.Request(sourceUri);
                request.setTitle(event.getTitle()).setDestinationUri(destinationUri);
                long downloadId = mDownloadManager.enqueue(request);
                eventIdSparseArray.append(downloadId, event);
                notifyItemDownloadStarted(ItemType.EVENT, event.externalId());
                notifyItemDownloadStarted(ItemType.WORSHIP, event.externalId());
            }
        }
    }

    private void download(Worship worship) {
        Uri sourceUri = Uri.parse(worship.getAudioUri());
        Uri destinationUri = FileUtils.getDownloadDestinationUri(worship);
        if (worshipIdSparseArray.indexOfValue(worship) < 0) {
            if (destinationUri != null) {
                DownloadManager.Request request = new DownloadManager.Request(sourceUri);
                request.setTitle(worship.getTitle()).setDestinationUri(destinationUri);
                long downloadId = mDownloadManager.enqueue(request);
                worshipIdSparseArray.append(downloadId, worship);
                notifyItemDownloadStarted(ItemType.EVENT, worship.getExternalId());
                notifyItemDownloadStarted(ItemType.WORSHIP, worship.getExternalId());
            }
        }
    }

    private void download(Sermon sermon) {
        Uri sourceUri = Uri.parse(sermon.getAudioUri());
        Uri destinationUri = FileUtils.getDownloadDestinationUri(sermon);
        if (sermonIdSparseArray.indexOfValue(sermon) < 0) {
            if (destinationUri != null) {
                DownloadManager.Request request = new DownloadManager.Request(sourceUri);
                request.setTitle(sermon.getTitle()).setDestinationUri(destinationUri);
                long downloadId = mDownloadManager.enqueue(request);
                sermonIdSparseArray.append(downloadId, sermon);
                notifyItemDownloadStarted(ItemType.SERMON, sermon.getExternalId());
            }
        }
    }

    private void download(Witness witness) {
        Uri sourceUri = Uri.parse(witness.getAudioUri());
        Uri destinationUri = FileUtils.getDownloadDestinationUri(witness);
        if (witnessIdSparseArray.indexOfValue(witness) < 0) {
            if (destinationUri != null) {
                DownloadManager.Request request = new DownloadManager.Request(sourceUri);
                request.setTitle(witness.getTitle()).setDestinationUri(destinationUri);
                long downloadId = mDownloadManager.enqueue(request);
                witnessIdSparseArray.append(downloadId, witness);
                notifyItemDownloadStarted(ItemType.WITNESS, witness.getExternalId());
            }
        }
    }

    private void download(Song song) {
        Uri sourceUri = Uri.parse(song.getAudioUri());
        Uri destinationUri = FileUtils.getDownloadDestinationUri(song);
        if (songIdSparseArray.indexOfValue(song) < 0) {
            if (destinationUri != null) {
                DownloadManager.Request request = new DownloadManager.Request(sourceUri);
                request.setTitle(song.getTitle()).setDestinationUri(destinationUri);
                long downloadId = mDownloadManager.enqueue(request);
                songIdSparseArray.append(downloadId, song);
                notifyItemDownloadStarted(ItemType.SONG, song.externalId());
            }
        }
    }

    private void download(Photo photo) {
        Uri sourceUri = Uri.parse(photo.getPhotoUrl());
        Uri destinationUri = FileUtils.getDownloadDestinationUri(photo);
        if (photoIdSparseArray.indexOfValue(photo) < 0) {
            if (destinationUri != null) {
                DownloadManager.Request request = new DownloadManager.Request(sourceUri);
                request.setDestinationUri(destinationUri);
                long downloadId = mDownloadManager.enqueue(request);
                photoIdSparseArray.append(downloadId, photo);
                notifyItemDownloadStarted(ItemType.PHOTO, photo.getBackendId());
            }
        }
    }

    private void delete(Event event) {
        if (!TextUtils.isEmpty(event.getAudioLocalUri())) {
            if (mAudioService != null && mAudioService.getState() != AudioService.State.STOP &&
                    event.getAudioLocalUri().equals(mAudioService.getUri())) {
                mAudioService.stop();
            }

            Uri uri = Uri.parse(event.getAudioLocalUri());
            File file = new File(uri.getPath());
            if (!file.exists() || file.delete()) {
                new Update(Worship.class)
                        .set(Worship.COLUMN_AUDIO_LOCAL + " = NULL")
                        .where(Worship.COLUMN_ID + " = ?", event.externalId())
                        .execute();
                event.setAudioLocalUri(null);
                event.save();
                if (BuildConfig.DB_DUMP_ENABLED) {
                    FileUtils.dumbDBFile(mContext);
                }
                notifyItemDeleted(ItemType.EVENT, event.externalId());
            }
        }
    }

    private void delete(Worship worship) {
        if (!TextUtils.isEmpty(worship.getAudioLocalUri())) {
            if (mAudioService != null && mAudioService.getState() != AudioService.State.STOP &&
                    worship.getAudioLocalUri().equals(mAudioService.getUri())) {
                mAudioService.stop();
            }

            Uri uri = Uri.parse(worship.getAudioLocalUri());
            File file = new File(uri.getPath());
            if (!file.exists() || file.delete()) {
                worship.setAudioLocalUri(null);
                worship.save();
                new Update(Event.class)
                        .set(Event.COLUMN_AUDIO_LOCAL + " = NULL")
                        .where(Event.COLUMN_ID + " = ?", worship.getExternalId())
                        .execute();
                if (BuildConfig.DB_DUMP_ENABLED) {
                    FileUtils.dumbDBFile(mContext);
                }
                notifyItemDeleted(ItemType.WORSHIP, worship.getExternalId());
            }
        }
    }

    private void delete(Sermon sermon) {
        if (!TextUtils.isEmpty(sermon.getAudioLocalUri())) {
            if (mAudioService != null && mAudioService.getState() != AudioService.State.STOP &&
                    sermon.getAudioLocalUri().equals(mAudioService.getUri())) {
                mAudioService.stop();
            }

            Uri uri = Uri.parse(sermon.getAudioLocalUri());
            File file = new File(uri.getPath());
            if (!file.exists() || file.delete()) {
                sermon.setAudioLocalUri(null);
                sermon.save();
                if (BuildConfig.DB_DUMP_ENABLED) {
                    FileUtils.dumbDBFile(mContext);
                }
                notifyItemDeleted(ItemType.SERMON, sermon.getExternalId());
            }
        }
    }

    private void delete(Witness witness) {
        if (!TextUtils.isEmpty(witness.getAudioLocalUri())) {
            if (mAudioService != null && mAudioService.getState() != AudioService.State.STOP &&
                    witness.getAudioLocalUri().equals(mAudioService.getUri())) {
                mAudioService.stop();
            }

            Uri uri = Uri.parse(witness.getAudioLocalUri());
            File file = new File(uri.getPath());
            if (!file.exists() || file.delete()) {
                witness.setAudioLocalUri(null);
                witness.save();
                if (BuildConfig.DB_DUMP_ENABLED) {
                    FileUtils.dumbDBFile(mContext);
                }
                notifyItemDeleted(ItemType.WITNESS, witness.getExternalId());
            }
        }
    }

    private void delete(Song song) {
        if (!TextUtils.isEmpty(song.getAudioLocalUri())) {
            if (mAudioService != null && mAudioService.getState() != AudioService.State.STOP &&
                    song.getAudioLocalUri().equals(mAudioService.getUri())) {
                mAudioService.stop();
            }

            Uri uri = Uri.parse(song.getAudioLocalUri());
            File file = new File(uri.getPath());
            if (!file.exists() || file.delete()) {
                song.setAudioLocalUri(null);
                song.save();
                if (BuildConfig.DB_DUMP_ENABLED) {
                    FileUtils.dumbDBFile(mContext);
                }
                notifyItemDeleted(ItemType.SONG, song.externalId());
            }
        }
    }

    public boolean isDownloadInProgress(AudioItem item) {
        return getQueue().contains(item.getRemoteUrl());
    }

    private ArrayList<String> getQueue() {
        ArrayList<String> queue = new ArrayList<>();
        for (int i = 0; i < eventIdSparseArray.size(); i++) {
            queue.add(eventIdSparseArray.valueAt(i).getRemoteUrl());
        }
        for (int i = 0; i < worshipIdSparseArray.size(); i++) {
            queue.add(worshipIdSparseArray.valueAt(i).getAudioUri());
        }
        for (int i = 0; i < sermonIdSparseArray.size(); i++) {
            queue.add(sermonIdSparseArray.valueAt(i).getAudioUri());
        }
        for (int i = 0; i < witnessIdSparseArray.size(); i++) {
            queue.add(witnessIdSparseArray.valueAt(i).getAudioUri());
        }
        for (int i = 0; i < songIdSparseArray.size(); i++) {
            queue.add(songIdSparseArray.valueAt(i).getAudioUri());
        }
        for (int i = 0; i < photoIdSparseArray.size(); i++) {
            queue.add(photoIdSparseArray.valueAt(i).getPhotoUrl());
        }
        return queue;
    }

    private void notifyItemDownloadStarted(ItemType itemType, long itemId) {
        Intent intent = new Intent(ACTION_ITEM_DOWNLOAD_STARTED);
        intent.putExtra(EXTRA_ITEM_TYPE, itemType);
        intent.putExtra(EXTRA_ITEM_ID, itemId);
        mBroadcastManager.sendBroadcast(intent);
    }

    private void notifyItemDeleted(ItemType itemType, long itemId) {
        Intent intent = new Intent(ACTION_ITEM_DELETED);
        intent.putExtra(EXTRA_ITEM_TYPE, itemType);
        intent.putExtra(EXTRA_ITEM_ID, itemId);
        mBroadcastManager.sendBroadcast(intent);
    }
}
