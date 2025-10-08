package by.geth.gethsemane.service;

import static by.geth.gethsemane.util.DateUtilsKt.keepDateOnly;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import by.geth.gethsemane.BuildConfig;
import by.geth.gethsemane.app.App;
import by.geth.gethsemane.app.AppPreferences;
import by.geth.gethsemane.app.AppTaskManager;
import by.geth.gethsemane.data.Album;
import by.geth.gethsemane.data.Author;
import by.geth.gethsemane.data.Category;
import by.geth.gethsemane.data.CategoryAlbum;
import by.geth.gethsemane.data.Event;
import by.geth.gethsemane.data.Photo;
import by.geth.gethsemane.data.Sermon;
import by.geth.gethsemane.data.Song;
import by.geth.gethsemane.data.Witness;
import by.geth.gethsemane.data.Worship;
import by.geth.gethsemane.util.DBUtils;
import by.geth.gethsemane.util.FileUtils;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class ApiService extends IntentService {

    public static final int HTTP_CODE_NOT_FOUND = 404;

    private static final String TAG = ApiService.class.getSimpleName();

    public static final String ACTION_REQUEST_STARTED = "ACTION_REQUEST_STARTED";
    public static final String ACTION_REQUEST_SUCCESSFUL = "ACTION_REQUEST_SUCCESSFUL";
    public static final String ACTION_REQUEST_ERROR = "ACTION_REQUEST_ERROR";

    public static final String EXTRA_RESPONSE_CODE = "EXTRA_RESPONSE_CODE";
    public static final String EXTRA_REQUEST_TYPE = "EXTRA_REQUEST_TYPE";
    public static final String EXTRA_ID = "EXTRA_ID";
    public static final String EXTRA_IDS = "EXTRA_IDS";
    public static final String EXTRA_FROM = "EXTRA_FROM";
    public static final String EXTRA_SCHEDULE_EVENTS_ADDED = "EXTRA_SCHEDULE_EVENTS_ADDED";
    public static final String EXTRA_SCHEDULE_EVENTS_DELETED = "EXTRA_SCHEDULE_EVENTS_DELETED";
    public static final String EXTRA_SCHEDULE_EVENTS_MODIFIED_OLD = "EXTRA_SCHEDULE_EVENTS_MODIFIED_OLD";
    public static final String EXTRA_SCHEDULE_EVENTS_MODIFIED_NEW = "EXTRA_SCHEDULE_EVENTS_MODIFIED_NEW";
    public static final String EXTRA_ERROR_MESSAGE = "EXTRA_ERROR_MESSAGE";
    public static final String EXTRA_SHOW_NOTIFICATION = "EXTRA_SHOW_NOTIFICATION";
    public static final String EXTRA_BODY = "EXTRA_BODY";
    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final String EXTRA_CATEGORY_ID = "EXTRA_CATEGORY_ID";
    public static final String EXTRA_ALBUM_ID = "EXTRA_ALBUM_ID";
    public static final String EXTRA_IS_SILENT = "EXTRA_IS_SILENT";
    public static final String EXTRA_PAGE = "EXTRA_PAGE";
    public static final String EXTRA_PER_PAGE = "EXTRA_PER_PAGE";

    public static final String REQUEST_GET_AUTHOR_LIST = "REQUEST_GET_AUTHOR_LIST";
    public static final String REQUEST_GET_AUTHOR = "REQUEST_GET_AUTHOR";
    public static final String REQUEST_GET_WORSHIP = "REQUEST_GET_WORSHIP";
    public static final String REQUEST_GET_EVENTS = "REQUEST_GET_EVENTS";
    public static final String REQUEST_GET_SERMONS_LIST = "REQUEST_GET_SERMONS_LIST";
    public static final String REQUEST_GET_SERMON = "REQUEST_GET_SERMON";
    public static final String REQUEST_GET_WITNESSES_LIST = "REQUEST_GET_WITNESSES_LIST";
    public static final String REQUEST_GET_WITNESS = "REQUEST_GET_WITNESS";
    public static final String REQUEST_GET_CATEGORY_LIST = "REQUEST_GET_CATEGORY_LIST";
    public static final String REQUEST_GET_ALBUM_LIST = "REQUEST_GET_ALBUM_LIST";
    public static final String REQUEST_GET_ALBUM = "REQUEST_GET_ALBUM";
    public static final String REQUEST_GET_PHOTO_LIST = "REQUEST_GET_PHOTO_LIST";
    public static final String REQUEST_GET_RECENT_PHOTO_LIST = "REQUEST_GET_RECENT_PHOTO_LIST";

    public static IntentFilter getFullIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REQUEST_STARTED);
        filter.addAction(ACTION_REQUEST_SUCCESSFUL);
        filter.addAction(ACTION_REQUEST_ERROR);
        return filter;
    }

    public static void getAuthorList(Context context) {
        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(EXTRA_REQUEST_TYPE, REQUEST_GET_AUTHOR_LIST);
        processRequestState(context, intent, ACTION_REQUEST_STARTED);
        context.startService(intent);
    }

    public static void getAuthor(Context context, long id) {
        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(EXTRA_REQUEST_TYPE, REQUEST_GET_AUTHOR);
        intent.putExtra(EXTRA_ID, id);
        processRequestState(context, intent, ACTION_REQUEST_STARTED);
        context.startService(intent);
    }

    public static void getWorship(Context context, long id) {
        getWorship(context, id, false);
    }

    public static void getWorship(Context context, long id, boolean isSilent) {
        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(EXTRA_REQUEST_TYPE, REQUEST_GET_WORSHIP);
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_IS_SILENT, isSilent);
        processRequestState(context, intent, ACTION_REQUEST_STARTED, isSilent);
        context.startService(intent);
    }

    public static void getEvents(Context context) {
        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(EXTRA_REQUEST_TYPE, REQUEST_GET_EVENTS);
        processRequestState(context, intent, ACTION_REQUEST_STARTED);
        context.startService(intent);
    }

    public static void getEvents(Context context, String from) {
        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(EXTRA_REQUEST_TYPE, REQUEST_GET_EVENTS);
        intent.putExtra(EXTRA_FROM, from);
        processRequestState(context, intent, ACTION_REQUEST_STARTED);
        context.startService(intent);
    }

    public static void getSermonsList(Context context) {
        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(EXTRA_REQUEST_TYPE, REQUEST_GET_SERMONS_LIST);
        processRequestState(context, intent, ACTION_REQUEST_STARTED);
        context.startService(intent);
    }

    public static void getSermonsList(Context context, String from) {
        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(EXTRA_REQUEST_TYPE, REQUEST_GET_SERMONS_LIST);
        intent.putExtra(EXTRA_FROM, from);
        processRequestState(context, intent, ACTION_REQUEST_STARTED);
        context.startService(intent);
    }

    public static void getSermon(Context context, long id) {
        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(EXTRA_REQUEST_TYPE, REQUEST_GET_SERMON);
        intent.putExtra(EXTRA_ID, id);
        processRequestState(context, intent, ACTION_REQUEST_STARTED);
        context.startService(intent);
    }

    public static void getWitnessesList(Context context) {
        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(EXTRA_REQUEST_TYPE, REQUEST_GET_WITNESSES_LIST);
        processRequestState(context, intent, ACTION_REQUEST_STARTED);
        context.startService(intent);
    }

    public static void getWitnessesList(Context context, String from) {
        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(EXTRA_REQUEST_TYPE, REQUEST_GET_WITNESSES_LIST);
        intent.putExtra(EXTRA_FROM, from);
        processRequestState(context, intent, ACTION_REQUEST_STARTED);
        context.startService(intent);
    }

    public static void getWitness(Context context, long id) {
        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(EXTRA_REQUEST_TYPE, REQUEST_GET_WITNESS);
        intent.putExtra(EXTRA_ID, id);
        processRequestState(context, intent, ACTION_REQUEST_STARTED);
        context.startService(intent);
    }

    public static void getCategoryList(Context context, String type, boolean isSilent) {
        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(EXTRA_REQUEST_TYPE, REQUEST_GET_CATEGORY_LIST);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_IS_SILENT, isSilent);
        processRequestState(context, intent, ACTION_REQUEST_STARTED, isSilent);
        context.startService(intent);
    }

    public static void getAlbumList(Context context, long categoryId, boolean isSilent) {
        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(EXTRA_REQUEST_TYPE, REQUEST_GET_ALBUM_LIST);
        intent.putExtra(EXTRA_CATEGORY_ID, categoryId);
        intent.putExtra(EXTRA_IS_SILENT, isSilent);
        processRequestState(context, intent, ACTION_REQUEST_STARTED, isSilent);
        context.startService(intent);
    }

    public static void getAlbum(Context context, long albumId) {
        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(EXTRA_REQUEST_TYPE, REQUEST_GET_ALBUM);
        intent.putExtra(EXTRA_ALBUM_ID, albumId);
        processRequestState(context, intent, ACTION_REQUEST_STARTED);
        context.startService(intent);
    }

    public static void getPhotoList(Context context, long albumId, boolean isSilent) {
        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(EXTRA_REQUEST_TYPE, REQUEST_GET_PHOTO_LIST);
        intent.putExtra(EXTRA_ALBUM_ID, albumId);
        intent.putExtra(EXTRA_IS_SILENT, isSilent);
        processRequestState(context, intent, ACTION_REQUEST_STARTED, isSilent);
        context.startService(intent);
    }

    public static void getPhotoList(Context context, String ids) {
        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(EXTRA_REQUEST_TYPE, REQUEST_GET_PHOTO_LIST);
        intent.putExtra(EXTRA_IDS, ids);
        processRequestState(context, intent, ACTION_REQUEST_STARTED);
        context.startService(intent);
    }

    public static void getRecentPhotoList(Context context, int page, int perPage) {
        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(EXTRA_REQUEST_TYPE, REQUEST_GET_RECENT_PHOTO_LIST);
        intent.putExtra(EXTRA_PAGE, page);
        intent.putExtra(EXTRA_PER_PAGE, perPage);
        processRequestState(context, intent, ACTION_REQUEST_STARTED);
        context.startService(intent);
    }
    
    public ApiService() {
        super(ApiService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String requestType = intent.getStringExtra(EXTRA_REQUEST_TYPE);
            Log.d(TAG, "handle " + requestType);
            switch (requestType) {
                case REQUEST_GET_AUTHOR_LIST:
                    getAuthorList(intent);
                    break;
                case REQUEST_GET_AUTHOR:
                    getAuthor(intent);
                    break;
                case REQUEST_GET_WORSHIP:
                    getWorship(intent);
                    break;
                case REQUEST_GET_EVENTS:
                    getEvents(intent);
                    break;
                case REQUEST_GET_SERMONS_LIST:
                    getSermonsList(intent);
                    break;
                case REQUEST_GET_SERMON:
                    getSermon(intent);
                    break;
                case REQUEST_GET_WITNESSES_LIST:
                    getWitnessesList(intent);
                    break;
                case REQUEST_GET_WITNESS:
                    getWitness(intent);
                    break;
                case REQUEST_GET_CATEGORY_LIST:
                    getCategoryList(intent);
                    break;
                case REQUEST_GET_ALBUM_LIST:
                    getAlbumList(intent);
                    break;
                case REQUEST_GET_ALBUM:
                    getAlbum(intent);
                    break;
                case REQUEST_GET_PHOTO_LIST:
                    getPhotoList(intent);
                    break;
                case REQUEST_GET_RECENT_PHOTO_LIST:
                    getRecentPhotoList(intent);
                    break;
            }
        } catch (Exception e) {
            processRequestState(this, intent, ACTION_REQUEST_ERROR);
            e.printStackTrace();
        }
    }

    private void getAuthorList(Intent intent) {
        try {
            GethApi api = App.getGethAPI();
            Response<List<Author>> response = api.getAuthorList().execute();
            if (response.isSuccessful()) {
                ActiveAndroid.beginTransaction();
                List<Author> authorList = response.body();
                new Delete().from(Author.class).execute();
                for (Author author : authorList)
                    author.save();
                ActiveAndroid.setTransactionSuccessful();
                ActiveAndroid.endTransaction();
                processRequestState(this, intent, ACTION_REQUEST_SUCCESSFUL);
                if (BuildConfig.DB_DUMP_ENABLED)
                    FileUtils.dumbDBFile(this);
            } else {
                processRequestState(this, intent, ACTION_REQUEST_ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
            processRequestState(this, intent, ACTION_REQUEST_ERROR);
        }
    }

    private void getAuthor(Intent intent) {
        try {
            GethApi api = App.getGethAPI();
            long id = intent.getLongExtra(EXTRA_ID, 0);
            Response<List<Author>> response = api.getAuthor(id).execute();
            if (response.isSuccessful()) {
                List<Author> authorList = response.body();
                if (!authorList.isEmpty()) {
                    authorList.get(0).save();
                    intent.putExtra(EXTRA_BODY, authorList.get(0));
                    processRequestState(this, intent, ACTION_REQUEST_SUCCESSFUL);
                    if (BuildConfig.DB_DUMP_ENABLED) {
                        FileUtils.dumbDBFile(this);
                    }
                } else {
                    processRequestState(this, intent, ACTION_REQUEST_ERROR);
                }
            } else {
                processRequestState(this, intent, ACTION_REQUEST_ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
            processRequestState(this, intent, ACTION_REQUEST_ERROR);
        }
    }
    
    private void getWorship(Intent intent) {
        try {
            GethApi api = App.getGethAPI();
            long id = intent.getLongExtra(EXTRA_ID, 0);
            Response<Worship> response;
            response = api.getWorship(id).execute();
            intent.putExtra(EXTRA_RESPONSE_CODE, response.code());
            if (response.isSuccessful()) {
                Worship worship = response.body();
                Worship oldWorship = DBUtils.getWorship(id);
                apply(worship);

                if (oldWorship != null) {
                    // check deleted sermons
                    for (Sermon oldSermon: oldWorship.getSermonList()) {
                        boolean isDeleted = true;
                        for (Sermon sermon : worship.getSermonList()) {
                            if (oldSermon.getExternalId() == sermon.getExternalId()) {
                                isDeleted = false;
                                break;
                            }
                        }
                        if (isDeleted) {
                            if (oldSermon.getLocalPath() != null) {
                                File file = new File(oldSermon.getLocalPath());
                                if (file.exists()) {
                                    file.delete();
                                }
                            }
                            new Delete().from(Sermon.class)
                                    .where(Sermon.COLUMN_ID + " = ?", oldSermon.getExternalId())
                                    .execute();
                        }
                    }

                    // check deleted witnesses
                    for (Witness oldWitness: oldWorship.getWitnessList()) {
                        boolean isDeleted = true;
                        for (Witness witness : worship.getWitnessList()) {
                            if (oldWitness.getExternalId() == witness.getExternalId()) {
                                isDeleted = false;
                                break;
                            }
                        }
                        if (isDeleted) {
                            if (oldWitness.getLocalPath() != null) {
                                File file = new File(oldWitness.getLocalPath());
                                if (file.exists()) {
                                    file.delete();
                                }
                            }
                            new Delete().from(Witness.class)
                                    .where(Witness.COLUMN_ID + " = ?", oldWitness.getExternalId())
                                    .execute();
                        }
                    }

                    // check deleted songs
                    for (Song oldSong: oldWorship.getSongList()) {
                        boolean isDeleted = true;
                        for (Song song : worship.getSongList()) {
                            if (oldSong.externalId() == song.externalId()) {
                                isDeleted = false;
                                break;
                            }
                        }
                        if (isDeleted) {
                            if (oldSong.getLocalPath() != null) {
                                File file = new File(oldSong.getLocalPath());
                                if (file.exists()) {
                                    file.delete();
                                }
                            }
                            new Delete().from(Song.class)
                                    .where(Song.COLUMN_ID + " = ?", oldSong.externalId())
                                    .execute();
                        }
                    }
                }

                for (Sermon sermon : worship.getSermonList()) {
                    sermon.setWorshipId(worship.getExternalId());
                    sermon.setDate(keepDateOnly(worship.getDate()));
                    apply(sermon);
                }
                for (Witness witness : worship.getWitnessList()) {
                    witness.setWorshipId(worship.getExternalId());
                    witness.setDate(keepDateOnly(worship.getDate()));
                    apply(witness);
                }
                for (Song song : worship.getSongList()) {
                    song.setWorshipID(worship.getExternalId());
                    song.setDate(worship.getDate());
                    apply(song);
                }
                new Update(Photo.class)
                        .set(Photo.COLUMN_EVENT_ID + " = 0")
                        .where(Photo.COLUMN_EVENT_ID + " = ?", worship.getExternalId())
                        .execute();
                for (Photo photo : worship.getPhotoList()) {
                    photo.setEventId(worship.getExternalId());
                    apply(photo);
                }
                processRequestState(this, intent, ACTION_REQUEST_SUCCESSFUL);
                if (BuildConfig.DB_DUMP_ENABLED)
                    FileUtils.dumbDBFile(this);
            } else {
                String errorBody = response.errorBody().string();
                try {
                    JSONObject jsonObject = new JSONObject(errorBody);
                    String errorMessage = jsonObject.getString("message");
                    intent.putExtra(EXTRA_ERROR_MESSAGE, errorMessage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (response.code() == HTTP_CODE_NOT_FOUND) {
                    new Delete().from(Event.class).where(Event.COLUMN_ID + " = ?", id).execute();
                    new Delete().from(Worship.class).where(Worship.COLUMN_ID + " = ?", id).execute();
                }
                processRequestState(this, intent, ACTION_REQUEST_ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
            processRequestState(this, intent, ACTION_REQUEST_ERROR);
        }
    }

    private void apply(Worship worship) {
        if (new Select().from(Worship.class).where(Worship.COLUMN_ID + " = ?",
                worship.getExternalId()).executeSingle() == null) {
            Event event = new Select().from(Event.class)
                    .where(Event.COLUMN_ID + " = ?", worship.getExternalId())
                    .executeSingle();
            if (event != null) {
                worship.setAudioLocalUri(event.getAudioLocalUri());
            }
            worship.save();
        } else {
            String description = worship.getDescription() == null ? "" : worship.getDescription();
            new Update(Worship.class).set(
                    Worship.COLUMN_DATE + " = ?, " +
                    Worship.COLUMN_TITLE + " = ?, " +
                    Worship.COLUMN_DESCRIPTION + " = ?, " +
                    Worship.COLUMN_VIDEO_REMOTE + " = ?, " +
                    Worship.COLUMN_POSTER_REMOTE + " = ?",

                    worship.getDate().getTime(),
                    worship.getTitle(),
                    description,
                    worship.getVideoUri(),
                    worship.getPosterUri()
                )
                .where(Worship.COLUMN_ID + " = ?", worship.getExternalId())
                .execute();
        }
    }

    private void apply(Sermon sermon) {
        if (new Select().from(Sermon.class).where(Sermon.COLUMN_ID + " = ?",
                sermon.getExternalId()).executeSingle() == null) {
            sermon.save();
        } else {
            new Update(Sermon.class).set(
                    Sermon.COLUMN_TITLE + " = ?, " +
                    Sermon.COLUMN_AUTHOR_ID + " = ?, " +
                    Sermon.COLUMN_DATE + " = ?, " +
                    Sermon.COLUMN_AUDIO_REMOTE + " = ?",

                    sermon.getTitle(),
                    sermon.getAuthorId(),
                    sermon.getDate().getTime(),
                    sermon.getAudioUri()
                )
                .where(Sermon.COLUMN_ID + " = ?", sermon.getExternalId())
                .execute();
            if (sermon.getWorshipId() > 0) {
                new Update(Sermon.class)
                        .set(Sermon.COLUMN_WORSHIP_ID + " = ?", sermon.getWorshipId())
                        .where(Sermon.COLUMN_ID + " = ?", sermon.getExternalId())
                        .execute();
            }
            if (!TextUtils.isEmpty(sermon.getContent())) {
                new Update(Sermon.class)
                        .set(Sermon.COLUMN_CONTENT + " = ?", sermon.getContent())
                        .where(Sermon.COLUMN_ID + " = ?", sermon.getExternalId())
                        .execute();
            }
        }
    }

    private void apply(Witness witness) {
        if (new Select().from(Witness.class).where(Witness.COLUMN_ID + " = ?",
                witness.getExternalId()).executeSingle() == null) {
            witness.save();
        } else {
            new Update(Witness.class).set(
                    Witness.COLUMN_TITLE + " = ?, " +
                    Witness.COLUMN_AUTHOR_ID + " = ?, " +
                    Witness.COLUMN_DATE + " = ?, " +
                    Witness.COLUMN_AUDIO_REMOTE + " = ?",

                    witness.getTitle(),
                    witness.getAuthorId(),
                    witness.getDate().getTime(),
                    witness.getAudioUri()
            )
                    .where(Witness.COLUMN_ID + " = ?", witness.getExternalId())
                    .execute();
            if (witness.getWorshipId() > 0) {
                new Update(Witness.class)
                        .set(Witness.COLUMN_WORSHIP_ID + " = ?", witness.getWorshipId())
                        .where(Witness.COLUMN_ID + " = ?", witness.getExternalId())
                        .execute();
            }
            if (!TextUtils.isEmpty(witness.getContent())) {
                new Update(Witness.class)
                        .set(Witness.COLUMN_CONTENT + " = ?", witness.getContent())
                        .where(Witness.COLUMN_ID + " = ?", witness.getExternalId())
                        .execute();
            }
        }
    }

    private void apply(Song song) {
        if (new Select().from(Song.class).where(Song.COLUMN_ID + " = ?",
                song.externalId()).executeSingle() == null) {
            song.save();
        } else {
            new Update(Song.class).set(
                    Song.COLUMN_WORSHIP_ID + " = ?, " +
                    Song.COLUMN_GROUP_ID + " = ?, " +
                    Song.COLUMN_TITLE + " = ?, " +
                    Song.COLUMN_AUDIO_REMOTE + " = ?",

                    song.getWorshipID(),
                    song.getGroupID(),
                    song.getTitle(),
                    song.getAudioUri()
                )
                .where(Song.COLUMN_ID + " = ?", song.externalId())
                .execute();
        }
    }

    /**
     * for the worship request only
     */
    private void apply(Photo photo) {
        if (new Select().from(Photo.class).where(Photo.COLUMN_BACKEND_ID + " = ?", photo.getBackendId()).exists()) {
            new Update(Photo.class).set(
                    Photo.COLUMN_TITLE + " = ?, " +
                    Photo.COLUMN_PREVIEW_URL + " = ?, " +
                    Photo.COLUMN_PHOTO_URL + " = ?, " +
                    Photo.COLUMN_DATE + " = ?, " +
                    Photo.COLUMN_EVENT_ID + " = ?",

                    photo.getTitle() == null ? "" : photo.getTitle(),
                    photo.getPreviewUrl(),
                    photo.getPhotoUrl(),
                    photo.getDateString(),
                    photo.getEventId()
                )
                .where(Photo.COLUMN_BACKEND_ID + " = ?", photo.getBackendId())
                .execute();
        } else {
            photo.save();
        }
    }

    private void getEvents(Intent intent) {
        String from = intent.getStringExtra(EXTRA_FROM);
        GethApi api = App.getGethAPI();
        try {
            Response<List<Event>> response;
            Date dateFrom;
            boolean isSchedule = false;
            if (from == null) {
                response = api.getEvents().execute();
                dateFrom = Calendar.getInstance().getTime();
                isSchedule = true;
            } else {
                response = api.getEvents(from).execute();
                dateFrom = convertToDate(from);
            }

            if (response.isSuccessful()) {
                List<Event> newEventsList = response.body();

                List<Event> oldEventsList = new Select()
                        .from(Event.class)
                        .where(Event.COLUMN_DATE + " >= ?", dateFrom.getTime())
                        .execute();
                if (isSchedule) {
                    ArrayList<Event> scheduleAddedEvents = new ArrayList<>();
                    ArrayList<Event> scheduleDeletedEvents = new ArrayList<>();
                    ArrayList<Event> scheduleModifiedOldEvents = new ArrayList<>();
                    ArrayList<Event> scheduleModifiedNewEvents = new ArrayList<>();

                    for (Event newEvent : newEventsList) {
                        boolean isAdded = true;
                        for (Event oldEvent : oldEventsList) {
                            if (newEvent.externalId() == oldEvent.externalId()) {
                                isAdded = false;
                                break;
                            }
                        }
                        if (isAdded)
                            scheduleAddedEvents.add(newEvent);
                    }

                    for (Event oldEvent : oldEventsList) {
                        boolean isDeleted = true;
                        for (Event newEvent : newEventsList) {
                            if (newEvent.externalId() == oldEvent.externalId()) {
                                isDeleted = false;
                                if (!Event.isScheduleEventsEqual(oldEvent, newEvent)) {
                                    scheduleModifiedOldEvents.add(oldEvent);
                                    scheduleModifiedNewEvents.add(newEvent);
                                }
                                break;
                            }
                        }
                        if (isDeleted)
                            scheduleDeletedEvents.add(oldEvent);
                    }

                    intent.putParcelableArrayListExtra(EXTRA_SCHEDULE_EVENTS_ADDED, scheduleAddedEvents);
                    intent.putParcelableArrayListExtra(EXTRA_SCHEDULE_EVENTS_DELETED, scheduleDeletedEvents);
                    intent.putParcelableArrayListExtra(EXTRA_SCHEDULE_EVENTS_MODIFIED_OLD, scheduleModifiedOldEvents);
                    intent.putParcelableArrayListExtra(EXTRA_SCHEDULE_EVENTS_MODIFIED_NEW, scheduleModifiedNewEvents);
                }

                ActiveAndroid.beginTransaction();
                for (Event newEvent : newEventsList) {
                    if (isSchedule || !from.equals("2019-01-01")) {
                        if (new Select()
                                .from(Event.class)
                                .where(Event.COLUMN_ID + " = ?", newEvent.externalId())
                                .exists()) {
                            new Update(Event.class)
                                    .set(
                                            Event.COLUMN_CATEGORY_ID + " = ?, " +
                                                    Event.COLUMN_DATE + " = ?, " +
                                                    Event.COLUMN_TITLE + " = ?, " +
                                                    Event.COLUMN_SHORT_DESC + " = ?, " +
                                                    Event.COLUMN_NOTE + " = ?, " +
                                                    Event.COLUMN_IS_DRAFT + " = ?, " +
                                                    Event.COLUMN_IS_ARCHIVE + " = ?, " +
                                                    Event.COLUMN_AUDIO_REMOTE + " = ?, " +
                                                    Event.COLUMN_GROUP_ID + " = ?, " +
                                                    Event.COLUMN_VIDEO_REMOTE + " = ?",

                                            newEvent.getCategoryId(),
                                            newEvent.getDate().getTime(),
                                            newEvent.getTitle(),
                                            newEvent.getShortDesc() == null ? "" : newEvent.getShortDesc(),
                                            newEvent.getNote() == null ? "" : newEvent.getNote(),
                                            newEvent.isDraft() ? 1 : 0,
                                            newEvent.isArchive() ? 1 : 0,
                                            newEvent.getAudioFileName() == null ? "" : newEvent.getAudioFileName(),
                                            newEvent.getMusicGroupId(),
                                            newEvent.getVideoFileName() == null ? "" : newEvent.getVideoFileName()
                                    )
                                    .where(Event.COLUMN_ID + " = ?", newEvent.externalId())
                                    .execute();
                        } else {
                            Worship worship = new Select().from(Worship.class)
                                    .where(Worship.COLUMN_ID + " = ?", newEvent.externalId())
                                    .executeSingle();
                            if (worship != null) {
                                newEvent.setAudioLocalUri(worship.getAudioLocalUri());
                            }
                            newEvent.save();
                        }
                    } else {
                        if (newEvent.getDate().compareTo(new Date()) > 0 || newEvent.getCategoryId() == Event.ID_CATEGORY_WORSHIP)
                            newEvent.save();
                    }
                }
                for (Event oldEvent : oldEventsList) {
                    boolean isDeleted = true;
                    for (Event newEvent : newEventsList) {
                        if (oldEvent.externalId() == newEvent.externalId()) {
                            isDeleted = false;
                            break;
                        }
                    }
                    if (isDeleted) {
                        oldEvent.delete();
                    }
                }
                new Delete().from(Event.class).where(Event.COLUMN_DATE + " < " + System.currentTimeMillis() + " AND " + Event.COLUMN_CATEGORY_ID + " != " + Event.ID_CATEGORY_WORSHIP).execute();
                new Delete().from(Event.class).where(Event.COLUMN_CATEGORY_ID + " = " + Event.ID_CATEGORY_WORSHIP + " AND " + Event.COLUMN_IS_ARCHIVE + " = 1");
                ActiveAndroid.setTransactionSuccessful();
                ActiveAndroid.endTransaction();

                if (BuildConfig.DB_DUMP_ENABLED)
                    FileUtils.dumbDBFile(this);

                if (isSchedule)
                    AppPreferences.getInstance().onScheduleUpdated();
                else
                    AppPreferences.getInstance().onWorshipsUpdated();

                processRequestState(this, intent, ACTION_REQUEST_SUCCESSFUL);
            } else {
                processRequestState(this, intent, ACTION_REQUEST_ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
            processRequestState(this, intent, ACTION_REQUEST_ERROR);
        }
    }

    /**
     * Convert to date string like "yyyy", "yyyy-MM", "yyyy-MM-dd"
     */
    @SuppressWarnings("EmptyCatchBlock")
    private Date convertToDate(String str) {
        Locale locale = Locale.getDefault();
        DateFormat df1 = new SimpleDateFormat("yyyy", locale);
        DateFormat df2 = new SimpleDateFormat("yyyy-MM", locale);
        DateFormat df3 = new SimpleDateFormat("yyyy-MM-dd", locale);

        try {
            return df3.parse(str);
        } catch (ParseException e) {
        }

        try {
            return df2.parse(str);
        } catch (ParseException e) {
        }

        try {
            return df1.parse(str);
        } catch (ParseException e) {
        }

        return null;
    }

    private void getSermonsList(Intent intent) {
        String from = intent.getStringExtra(EXTRA_FROM);
        GethApi api = App.getGethAPI();
        try {
            Response<List<Sermon>> response;
            if (from == null)
                response = api.getSermonsList().execute();
            else
                response = api.getSermonsList(from).execute();

            if (response.isSuccessful()) {
                List<Sermon> sermonList = response.body();

                ActiveAndroid.beginTransaction();
                for (Sermon sermon : sermonList) {
                    apply(sermon);
                }
                ActiveAndroid.setTransactionSuccessful();
                ActiveAndroid.endTransaction();

                if (BuildConfig.DB_DUMP_ENABLED)
                    FileUtils.dumbDBFile(this);

                AppPreferences.getInstance().onSermonsUpdated();
                processRequestState(this, intent, ACTION_REQUEST_SUCCESSFUL);
            } else {
                processRequestState(this, intent, ACTION_REQUEST_ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
            processRequestState(this, intent, ACTION_REQUEST_ERROR);
        }
    }

    private void getSermon(Intent intent) {
        long id = intent.getLongExtra(EXTRA_ID, 0);
        GethApi api = App.getGethAPI();
        try {
            Response<Sermon> response = api.getSermon(id).execute();
            if (response.isSuccessful()) {
                Sermon sermon = response.body();
                apply(sermon);

                if (BuildConfig.DB_DUMP_ENABLED)
                    FileUtils.dumbDBFile(this);

                processRequestState(this, intent, ACTION_REQUEST_SUCCESSFUL);
            } else {
                processRequestState(this, intent, ACTION_REQUEST_ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
            processRequestState(this, intent, ACTION_REQUEST_ERROR);
        }
    }

    private void getWitnessesList(Intent intent) {
        String from = intent.getStringExtra(EXTRA_FROM);
        GethApi api = App.getGethAPI();
        try {
            Response<List<Witness>> response;
            if (from == null)
                response = api.getWitnessesList().execute();
            else
                response = api.getWitnessesList(from).execute();

            if (response.isSuccessful()) {
                List<Witness> witnessesList = response.body();

                ActiveAndroid.beginTransaction();
                for (Witness witness : witnessesList) {
                    apply(witness);
                }
                ActiveAndroid.setTransactionSuccessful();
                ActiveAndroid.endTransaction();

                if (BuildConfig.DB_DUMP_ENABLED)
                    FileUtils.dumbDBFile(this);

                AppPreferences.getInstance().onWitnessesUpdated();
                processRequestState(this, intent, ACTION_REQUEST_SUCCESSFUL);
            } else {
                processRequestState(this, intent, ACTION_REQUEST_ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
            processRequestState(this, intent, ACTION_REQUEST_ERROR);
        }
    }

    private void getWitness(Intent intent) {
        long id = intent.getLongExtra(EXTRA_ID, 0);
        GethApi api = App.getGethAPI();
        try {
            Response<Witness> response = api.getWitness(id).execute();
            if (response.isSuccessful()) {
                Witness witness = response.body();
                apply(witness);

                if (BuildConfig.DB_DUMP_ENABLED)
                    FileUtils.dumbDBFile(this);

                processRequestState(this, intent, ACTION_REQUEST_SUCCESSFUL);
            } else {
                processRequestState(this, intent, ACTION_REQUEST_ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
            processRequestState(this, intent, ACTION_REQUEST_ERROR);
        }
    }

    private void getCategoryList(Intent intent) {
        boolean isSilent = intent.getBooleanExtra(EXTRA_IS_SILENT, false);
        try {
            String type = intent.getStringExtra(EXTRA_TYPE);
            Response<List<Category>> response;
            if (type == null) {
                response = App.getGethAPI().getCategoryList().execute();
            } else {
                response = App.getGethAPI().getCategoryList(type).execute();
            }
            if (response.isSuccessful()) {
                List<Category> categoryList = response.body();
                if (categoryList != null) {
                    List<Category> oldCategoryList = new Select().from(Category.class)
                            .where(Category.COLUMN_TYPE + " = ?", type).execute();
                    for (Category oldCategory : oldCategoryList) {
                        boolean isDeleted = true;
                        for (Category category : categoryList) {
                            if (oldCategory.getBackendId() == category.getBackendId()) {
                                isDeleted = false;
                                break;
                            }
                        }
                        if (isDeleted) {
                            List<CategoryAlbum> categoryAlbumList = new Select()
                                    .from(CategoryAlbum.class)
                                    .where(CategoryAlbum.COLUMN_CATEGORY_ID + " = ?",
                                            oldCategory.getBackendId())
                                    .execute();
                            List<Long> albumIdList = new ArrayList<>();
                            for (CategoryAlbum categoryAlbum : categoryAlbumList) {
                                albumIdList.add(categoryAlbum.getAlbumId());
                                categoryAlbum.delete();
                            }
                            for (long albumId : albumIdList) {
                                if (!(new Select().from(CategoryAlbum.class)
                                        .where(CategoryAlbum.COLUMN_ALBUM_ID + " = ?", albumId)
                                        .exists())) {
                                    new Delete().from(Photo.class)
                                            .where(Photo.COLUMN_ALBUM_ID + " = ?", albumId)
                                            .execute();
                                    new Delete().from(Album.class)
                                            .where(Album.COLUMN_BACKEND_ID + " = ?", albumId)
                                            .execute();
                                }
                            }
                            oldCategory.delete();
                        }
                    }

                    for (Category category : categoryList) {
                        if (new Select().from(Category.class)
                                .where(Category.COLUMN_BACKEND_ID + " = ?", category.getBackendId())
                                .exists()) {

                            new Update(Category.class)
                                    .set(Category.COLUMN_TITLE + " = ?, "
                                            + Category.COLUMN_COVER_URL + " = ?, "
                                            + Category.COLUMN_TYPE + " = ?",
                                        category.getTitle() == null ? "" : category.getTitle(),
                                        category.getCoverUrl() == null ? "" : category.getCoverUrl(),
                                        type == null ? "" : type)
                                    .where(Category.COLUMN_BACKEND_ID + " = ?", category.getBackendId())
                                    .execute();
                        } else {
                            category.setType(type);
                            category.save();
                        }
                    }

                    if (BuildConfig.DEBUG) {
                        FileUtils.dumbDBFile(this);
                    }

                    intent.putParcelableArrayListExtra(EXTRA_BODY, new ArrayList<>(categoryList));
                    processRequestState(this, intent, ACTION_REQUEST_SUCCESSFUL);
                } else if (!isSilent) {
                    processRequestState(this, intent, ACTION_REQUEST_ERROR);
                }
            } else if (!isSilent) {
                processRequestState(this, intent, ACTION_REQUEST_ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (!isSilent) {
                processRequestState(this, intent, ACTION_REQUEST_ERROR);
            }
        }
    }

    private void getAlbumList(Intent intent) {
        boolean isSilent = intent.getBooleanExtra(EXTRA_IS_SILENT, false);
        try {
            long categoryId = intent.getLongExtra(EXTRA_CATEGORY_ID, 0);
            Response<List<Album>> response = App.getGethAPI().getAlbumList(categoryId).execute();
            if (response.isSuccessful()) {
                List<Album> albumList = response.body();
                if (albumList != null) {
                    List<CategoryAlbum> oldCategoryAlbumList = new Select()
                            .from(CategoryAlbum.class)
                            .where(CategoryAlbum.COLUMN_CATEGORY_ID + " = ?", categoryId)
                            .execute();
                    for (CategoryAlbum oldCategoryAlbum : oldCategoryAlbumList) {
                        boolean isDeleted = true;
                        long albumId = oldCategoryAlbum.getAlbumId();
                        for (Album album : albumList) {
                            if (albumId == album.getBackendId()) {
                                isDeleted = false;
                                break;
                            }
                        }
                        if (isDeleted) {
                            oldCategoryAlbum.delete();
                            if (!(new Select().from(CategoryAlbum.class)
                                    .where(CategoryAlbum.COLUMN_ALBUM_ID + " = ?", albumId)
                                    .exists())) {
                                new Delete().from(Photo.class)
                                        .where(Photo.COLUMN_ALBUM_ID + " = ?", albumId)
                                        .execute();
                                new Delete().from(Album.class)
                                        .where(Album.COLUMN_BACKEND_ID + " = ?", albumId)
                                        .execute();
                            }
                        }
                    }

                    for (Album album : albumList) {
                        if (new Select().from(Album.class)
                                .where(Album.COLUMN_BACKEND_ID + " = ?", album.getBackendId())
                                .exists()) {
                            new Update(Album.class)
                                    .set(Album.COLUMN_TITLE + " = ?, " + Album.COLUMN_COVER_URL + " = ?",
                                        album.getTitle() == null ? "" : album.getTitle(),
                                        album.getCoverUrl() == null ? "" : album.getCoverUrl())
                                    .where(Album.COLUMN_BACKEND_ID + " = ?", album.getBackendId())
                                    .execute();
                        } else {
                            album.save();
                        }
                        if (!(new Select().from(CategoryAlbum.class)
                                .where(CategoryAlbum.COLUMN_CATEGORY_ID + " = ? AND "
                                        + CategoryAlbum.COLUMN_ALBUM_ID + " = ?",
                                        categoryId, album.getBackendId())
                                .exists())) {
                            new CategoryAlbum(categoryId, album.getBackendId()).save();
                        }
                    }

                    if (BuildConfig.DEBUG) {
                        FileUtils.dumbDBFile(this);
                    }

                    intent.putParcelableArrayListExtra(EXTRA_BODY, new ArrayList<>(albumList));
                    processRequestState(this, intent, ACTION_REQUEST_SUCCESSFUL);
                } else if (!isSilent) {
                    processRequestState(this, intent, ACTION_REQUEST_ERROR);
                }
            } else if (!isSilent) {
                processRequestState(this, intent, ACTION_REQUEST_ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (!isSilent) {
                processRequestState(this, intent, ACTION_REQUEST_ERROR);
            }
        }
    }

    private void getAlbum(Intent intent) {
        try {
            long albumId = intent.getLongExtra(EXTRA_ALBUM_ID, 0);
            Response<Album> response = App.getGethAPI().getAlbum(albumId).execute();
            if (response.isSuccessful()) {
                Album album = response.body();
                if (album != null) {
                    if (new Select().from(Album.class).where(Album.COLUMN_BACKEND_ID + " = ?", albumId).exists()) {
                        new Update(Album.class)
                                .set(Album.COLUMN_TITLE + " = ?, " + Album.COLUMN_COVER_URL + " = ?",
                                        album.getTitle() == null ? "" : album.getTitle(),
                                        album.getCoverUrl() == null ? "" : album.getCoverUrl())
                                .where(Album.COLUMN_BACKEND_ID + " = ?", albumId)
                                .execute();
                    } else {
                        album.save();
                    }
                    intent.putExtra(EXTRA_BODY, album);
                    processRequestState(this, intent, ACTION_REQUEST_SUCCESSFUL);
                } else {
                    processRequestState(this, intent, ACTION_REQUEST_ERROR);
                }
            } else {
                processRequestState(this, intent, ACTION_REQUEST_ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
            processRequestState(this, intent, ACTION_REQUEST_ERROR);
        }
    }

    private void getPhotoList(Intent intent) {
        boolean isSilent = intent.getBooleanExtra(EXTRA_IS_SILENT, false);
        try {
            long albumId = intent.getLongExtra(EXTRA_ALBUM_ID, 0);
            String idsJson = intent.getStringExtra(EXTRA_IDS);
            Response<List<Photo>> response = null;
            if (albumId > 0) {
                response = App.getGethAPI().getPhotoList(albumId).execute();
            } else if (idsJson != null) {
                try {
                    JSONArray idsJsonArray = new JSONArray(idsJson);
                    StringBuilder idsBuilder = new StringBuilder();
                    for (int i = 0; i < idsJsonArray.length(); i++) {
                        long id = idsJsonArray.getLong(i);
                        if (i > 0) {
                            idsBuilder.append(',');
                        }
                        idsBuilder.append(id);
                    }
                    String ids = idsBuilder.toString();
                    response = App.getGethAPI().getPhotoList(ids).execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (response != null && response.isSuccessful()) {
                List<Photo> photoList = response.body();
                if (photoList != null) {
                    List<Photo> deletedPhotoList = new ArrayList<>();
                    if (albumId > 0) {
                        List<Photo> oldPhotoList = new Select().from(Photo.class)
                                .where(Photo.COLUMN_ALBUM_ID + " = ?", albumId)
                                .execute();
                        if (oldPhotoList != null) {
                            for (Photo oldPhoto : oldPhotoList) {
                                boolean isDeleted = true;
                                for (Photo photo : photoList) {
                                    if (oldPhoto.getBackendId() == photo.getBackendId()) {
                                        isDeleted = false;
                                        break;
                                    }
                                }
                                if (isDeleted) {
                                    deletedPhotoList.add(oldPhoto);
                                }
                            }
                        }
                    }

                    for (Photo photo : photoList) {
                        if (new Select().from(Photo.class)
                                .where(Photo.COLUMN_BACKEND_ID + " = ?", photo.getBackendId())
                                .exists()) {
                            if (albumId > 0) {
                                new Update(Photo.class)
                                        .set(Photo.COLUMN_TITLE + " = ?, "
                                                        + Photo.COLUMN_PREVIEW_URL + " = ?, "
                                                        + Photo.COLUMN_PHOTO_URL + " = ?, "
                                                        + Photo.COLUMN_DATE + " = ?, "
                                                        + Photo.COLUMN_ALBUM_ID + " = ?",
                                                photo.getTitle() == null ? "" : photo.getTitle(),
                                                photo.getPreviewUrl() == null ? "" : photo.getPreviewUrl(),
                                                photo.getPhotoUrl() == null ? "" : photo.getPhotoUrl(),
                                                photo.getDateString() == null ? "" : photo.getDateString(),
                                                albumId)
                                        .where(Photo.COLUMN_BACKEND_ID + " = ?", photo.getBackendId())
                                        .execute();
                            } else {
                                new Update(Photo.class)
                                        .set(Photo.COLUMN_TITLE + " = ?, "
                                                        + Photo.COLUMN_PREVIEW_URL + " = ?, "
                                                        + Photo.COLUMN_PHOTO_URL + " = ?, "
                                                        + Photo.COLUMN_DATE + " = ?",
                                                photo.getTitle() == null ? "" : photo.getTitle(),
                                                photo.getPreviewUrl() == null ? "" : photo.getPreviewUrl(),
                                                photo.getPhotoUrl() == null ? "" : photo.getPhotoUrl(),
                                                photo.getDateString() == null ? "" : photo.getDateString())
                                        .where(Photo.COLUMN_BACKEND_ID + " = ?", photo.getBackendId())
                                        .execute();
                            }
                        } else {
                            if (albumId > 0) {
                                photo.setAlbumId(albumId);
                            }
                            photo.save();
                        }
                    }
                    for (Photo deletedPhoto : deletedPhotoList) {
                        deletedPhoto.delete();
                    }

                    if (BuildConfig.DEBUG) {
                        FileUtils.dumbDBFile(this);
                    }

                    if (albumId > 0) {
                        Collections.sort(photoList);
                    }
                    intent.putParcelableArrayListExtra(EXTRA_BODY, new ArrayList<>(photoList));
                    processRequestState(this, intent, ACTION_REQUEST_SUCCESSFUL);
                } else if (!isSilent) {
                    processRequestState(this, intent, ACTION_REQUEST_ERROR);
                }
            } else if (!isSilent) {
                processRequestState(this, intent, ACTION_REQUEST_ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (!isSilent) {
                processRequestState(this, intent, ACTION_REQUEST_ERROR);
            }
        }
    }

    private void getRecentPhotoList(Intent intent) {
        boolean isSilent = intent.getBooleanExtra(EXTRA_IS_SILENT, false);
        try {
            int page = intent.getIntExtra(EXTRA_PAGE, 1);
            int perPage = intent.getIntExtra(EXTRA_PER_PAGE, 20);
            Response<ResponseBody> response = App.getGethAPI().getRecentPhotoList(page, perPage).execute();
            if (response.isSuccessful()) {
                String responseString = response.body().string();
                JSONObject responseJson = new JSONObject(responseString);
                JSONArray photosJsonArray = responseJson.getJSONArray("data");
                List<Photo> photoList = new ArrayList<>();
                Gson gson = new GsonBuilder()
                        .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                        .create();
                for (int i = 0; i < photosJsonArray.length(); i++) {
                    JSONObject photoJsonObject = photosJsonArray.getJSONObject(i);
                    Photo photo = gson.fromJson(photoJsonObject.toString(), Photo.class);
                    photo.setRecent(true);
                    photoList.add(photo);
                }

                if (page == 1) {
                    new Update(Photo.class)
                            .set(Photo.COLUMN_IS_RECENT + " = 0")
                            .execute();
                }
                for (Photo photo : photoList) {
                    if (new Select()
                            .from(Photo.class)
                            .where(Photo.COLUMN_BACKEND_ID + " = ?", photo.getBackendId())
                            .exists()) {
                        new Update(Photo.class)
                                .set(Photo.COLUMN_TITLE + " = ?, "
                                    + Photo.COLUMN_PREVIEW_URL + " = ?, "
                                    + Photo.COLUMN_PHOTO_URL + " = ?, "
                                    + Photo.COLUMN_DATE + " = ?, "
                                    + Photo.COLUMN_IS_RECENT + " = ?",
                                    photo.getTitle() == null ? "" : photo.getTitle(),
                                    photo.getPreviewUrl() == null ? "" : photo.getPreviewUrl(),
                                    photo.getPhotoUrl() == null ? "" : photo.getPhotoUrl(),
                                    photo.getDateString() == null ? "" : photo.getDateString(),
                                    photo.isRecent() ? 1 : 0)
                                .where(Photo.COLUMN_BACKEND_ID + " = ?", photo.getBackendId())
                                .execute();
                    } else {
                        photo.save();
                    }
                }

                if (BuildConfig.DEBUG) {
                    FileUtils.dumbDBFile(this);
                }

                intent.putParcelableArrayListExtra(EXTRA_BODY, new ArrayList<>(photoList));
                processRequestState(this, intent, ACTION_REQUEST_SUCCESSFUL);
            } else if (!isSilent) {
                processRequestState(this, intent, ACTION_REQUEST_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (!isSilent) {
                processRequestState(this, intent, ACTION_REQUEST_ERROR);
            }
        }
    }

    private static void processRequestState(Context context, Intent requestIntent, String action) {
        processRequestState(context, requestIntent, action, false);
    }

    private static void processRequestState(Context context, Intent requestIntent, String action, boolean isSilent) {
        String requestType = requestIntent.getStringExtra(EXTRA_REQUEST_TYPE);
        AppTaskManager.Task task = new AppTaskManager.Task(requestType);
        switch (requestType) {
            case REQUEST_GET_WORSHIP:
                task.getArguments().put(EXTRA_ID, requestIntent.getLongExtra(EXTRA_ID, 0));
                break;
            case REQUEST_GET_RECENT_PHOTO_LIST:
                task.getArguments().put(EXTRA_PAGE, requestIntent.getIntExtra(EXTRA_PAGE, 1));
                task.getArguments().put(EXTRA_PER_PAGE, requestIntent.getIntExtra(EXTRA_PER_PAGE, 20));
                break;
        }

        if (task != null) {
            switch (action) {
                case ACTION_REQUEST_STARTED:
                    AppTaskManager.getInstance().add(task);
                    break;
                case ACTION_REQUEST_SUCCESSFUL:
                case ACTION_REQUEST_ERROR:
                    AppTaskManager.getInstance().remove(task);
                    break;
            }
        }

        if (!isSilent) {
            Intent intent = new Intent(action);
            intent.putExtras(requestIntent);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }
}
