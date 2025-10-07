package by.geth.gethsemane.service;

import android.text.Html;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Map;

import by.geth.gethsemane.BuildConfig;
import by.geth.gethsemane.api.Server;
import by.geth.gethsemane.app.AppNotificationManager;
import by.geth.gethsemane.app.AppPreferences;
import by.geth.gethsemane.util.FileUtils;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    public static final String TAG = FirebaseMessagingService.class.getSimpleName();

    private static final String TOPICS_PREFIX = "/topics/";

    public static final String LIVE_TOPIC = "live";
    public static final String LIVE_TYPE = "type";
    public static final String LIVE_TYPE_AUDIO = "audio";
    public static final String LIVE_TYPE_VIDEO = "video";
    public static final String LIVE_STATUS = "status";
    public static final String LIVE_STATUS_START = "start";
    public static final String LIVE_STATUS_STOP = "stop";
    public static final String LIVE_KEY = "key";

    public static final String SCHEDULE_TOPIC = "schedule";

    public static final String NEWS_TOPIC = "news";
    public static final String NEWS_DEV_TOPIC = "news_dev";
    public static final String NEWS_TITLE = "title";
    public static final String NEWS_DESCRIPTION = "text";
    public static final String NEWS_ID = "id";
    public static final String NEWS_IMAGE = "image";

    public static final String WORSHIP_NOTES_TOPIC = "worship-notes";
    public static final String WORSHIP_NOTES_DEV_TOPIC = "worship-notes_dev";
    public static final String TO_SEE_CHRIST_TOPIC = "to-see-christ";
    public static final String ARTICLE_TITLE = "title";
    public static final String ARTICLE_DESCRIPTION = "text";
    public static final String ARTICLE_ID = "id";
    public static final String ARTICLE_IMAGE = "image";

    public static final String PHOTOS_TOPIC = "photos";
    public static final String PHOTOS_DEV_TOPIC = "photos_dev";
    public static final String PHOTOS_DATA_IDS = "photos";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (BuildConfig.DEBUG) {
            FileUtils.writeFcmLog(remoteMessage);
        }
        Log.d(TAG, "message received from " + remoteMessage.getFrom());
        if (remoteMessage.getFrom() != null) {
            switch (remoteMessage.getFrom()) {
                case TOPICS_PREFIX + LIVE_TOPIC:
                    onLiveMessageReceived(remoteMessage.getData());
                    break;
                case TOPICS_PREFIX + SCHEDULE_TOPIC:
                    onScheduleMessageReceived();
                    break;
                case TOPICS_PREFIX + NEWS_TOPIC:
                case TOPICS_PREFIX + NEWS_DEV_TOPIC:
                    onNewsMessageReceived(remoteMessage.getData());
                    break;
                case TOPICS_PREFIX + WORSHIP_NOTES_TOPIC:
                case TOPICS_PREFIX + WORSHIP_NOTES_DEV_TOPIC:
                    onWorshipNotesMessageReceived(remoteMessage.getData());
                    break;
                case TOPICS_PREFIX + TO_SEE_CHRIST_TOPIC:
                    onToSeeChristMessageReceived(remoteMessage.getData());
                    break;
                case TOPICS_PREFIX + PHOTOS_TOPIC:
                case TOPICS_PREFIX + PHOTOS_DEV_TOPIC:
                    onPhotosMessageReceived(remoteMessage.getData());
                    break;
            }
        }
    }

    private void onPhotosMessageReceived(Map<String, String> data) {
        Log.d(TAG, "onPhotosMessageReceived: " + data);
        if (AppPreferences.getInstance().isPhotosNotifEnabled()) {
            String idsJson = data.get(PHOTOS_DATA_IDS);
            try {
                JSONArray jsonArray = new JSONArray(idsJson);
                AppNotificationManager.getInstance().notifyPhotos(jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void onNewsMessageReceived(Map<String, String> data) {
        Log.d(TAG, "onNewsMessageReceived: " + data);
        Server.INSTANCE.getNewsList(1, 20, false);
        if (AppPreferences.getInstance().isNewsNotifEnabled()) {
            String title = data.get(NEWS_TITLE);
            String descriptionHtml = data.get(NEWS_DESCRIPTION);
            String description;
            if (descriptionHtml == null)
                description = null;
            else
                description = Html.fromHtml(descriptionHtml).toString();
            String image = data.get(NEWS_IMAGE);
            try {
                long id = Long.parseLong(data.get(NEWS_ID));
                AppNotificationManager.getInstance().notifyNews(title, description, image, id);
            } catch (Throwable e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private void onWorshipNotesMessageReceived(Map<String, String> data) {
        Log.d(TAG, "onWorshipNotesMessageReceived: " + data);
        Server.INSTANCE.getWorshipNotesList(1, 20, false);
        if (AppPreferences.getInstance().isWorshipNotesNotifEnabled()) {
            String title = data.get(ARTICLE_TITLE);
            String descriptionHtml = data.get(ARTICLE_DESCRIPTION);
            String description;
            if (descriptionHtml == null)
                description = null;
            else
                description = Html.fromHtml(descriptionHtml).toString();
            String image = data.get(ARTICLE_IMAGE);
            try {
                long id = Long.parseLong(data.get(ARTICLE_ID));
                AppNotificationManager.getInstance().notifyWorshipNotes(title, description, image, id);
            } catch (Throwable e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private void onToSeeChristMessageReceived(Map<String, String> data) {
        Log.d(TAG, "onToSeeChristMessageReceived: " + data);
        Server.INSTANCE.getToSeeChristList(1, 20, false);

        String title = data.get(ARTICLE_TITLE);
        String descriptionHtml = data.get(ARTICLE_DESCRIPTION);
        String description;
        if (descriptionHtml == null)
            description = null;
        else
            description = Html.fromHtml(descriptionHtml).toString();
        String image = data.get(ARTICLE_IMAGE);
        try {
            long id = Long.parseLong(data.get(ARTICLE_ID));
            AppNotificationManager.getInstance().notifyToSeeChrist(title, description, image, id);
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    private void onScheduleMessageReceived() {
        Log.d(TAG, "onScheduleMessageReceived");
        WorkRequest request = new OneTimeWorkRequest.Builder(ScheduleUpdateWorker.class)
                .setConstraints(new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build();
        WorkManager.getInstance(getApplicationContext()).enqueue(request);
    }

    private void onLiveMessageReceived(Map<String, String> data) {
        Log.d(TAG, "onLiveMessageReceived: " + data);
        String type = data.get(LIVE_TYPE);
        String status = data.get(LIVE_STATUS);
        if (LIVE_TYPE_VIDEO.equals(type) && status != null) {
            switch (status) {
                case LIVE_STATUS_START:
                    AppPreferences.getInstance().setLiveStarted(true);
                    break;
                case LIVE_STATUS_STOP:
                    AppNotificationManager.getInstance().cancelLiveStarted();
                    AppPreferences.getInstance().setLiveStarted(false);
                    break;
            }
        }
    }
}
