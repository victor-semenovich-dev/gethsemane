package by.geth.gethsemane.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.Calendar;

import by.geth.gethsemane.R;
import by.geth.gethsemane.ui.fragment.gallery.GalleryFragment;
import by.geth.gethsemane.ui.fragment.psalms.SongsListFragment;

public class AppPreferences {
    private static AppPreferences sInstance;

    public static void init(Context context) {
        sInstance = new AppPreferences(context);
    }

    public static AppPreferences getInstance() {
        return sInstance;
    }

    public static final String PREF_UPDATE_TIME_WORSHIPS = "pref_update_time_worships";
    public static final String PREF_UPDATE_TIME_SERMONS = "pref_update_time_sermons";
    public static final String PREF_UPDATE_TIME_WITNESSES = "pref_update_time_witnesses";
    public static final String PREF_UPDATE_TIME_SCHEDULE = "pref_update_time_schedule";
    public static final String PREF_FONT_SIZE = "pref_font_size";
    public static final String PREF_IS_LIVE_STARTED = "pref_is_live_started";

    public static final String PREF_GALLERY_SHOW_INFO = "pref_gallery_show_info";
    public static final String PREF_GALLERY_DEFAULT_SCREEN = "pref_gallery_default_screen";

    public static final String PREF_SONGS_SORT_TYPE = "pref_songs_sort_type";

    private Context mContext;
    private SharedPreferences mPrefs;

    private AppPreferences(Context context) {
        mContext = context;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void addListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mPrefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public void removeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mPrefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public boolean isGalleryShowInfo() {
        return mPrefs.getBoolean(PREF_GALLERY_SHOW_INFO, true);
    }

    public void setGalleryShowInfo(boolean showInfo) {
        mPrefs.edit().putBoolean(PREF_GALLERY_SHOW_INFO, showInfo).apply();
    }

    public int getGalleryDefaultScreen() {
        return mPrefs.getInt(PREF_GALLERY_DEFAULT_SCREEN, GalleryFragment.SCREEN_CATEGORIES);
    }

    public void setGalleryDefaultScreen(int defaultScreen) {
        mPrefs.edit().putInt(PREF_GALLERY_DEFAULT_SCREEN, defaultScreen).apply();
    }

    public boolean isBirthdayNotifEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return true;
        } else {
            String key = mContext.getString(R.string.pref_notif_birthday_key);
            return mPrefs.getBoolean(key, true);
        }
    }

    public String getBirthdayNotifTime() {
        String key = mContext.getString(R.string.pref_notif_birthday_time_key);
        return mPrefs.getString(key, "9:00");
    }

    public boolean isScheduleNotifEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return true;
        } else {
            String key = mContext.getString(R.string.pref_notif_schedule_key);
            return mPrefs.getBoolean(key, true);
        }
    }

    public boolean isLiveNotifEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return true;
        } else {
            String key = mContext.getString(R.string.pref_notif_live_video_key);
            return mPrefs.getBoolean(key, true);
        }
    }

    public boolean isNewsNotifEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return true;
        } else {
            String key = App.getContext().getString(R.string.pref_notif_news_key);
            return mPrefs.getBoolean(key, true);
        }
    }

    public boolean isWorshipNotesNotifEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return true;
        } else {
            String key = App.getContext().getString(R.string.pref_notif_worship_notes_key);
            return mPrefs.getBoolean(key, true);
        }
    }

    public boolean isPhotosNotifEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return true;
        } else {
            String key = App.getContext().getString(R.string.pref_notif_photos_key);
            return mPrefs.getBoolean(key, true);
        }
    }

    public long getUpdateTimeWorships() {
        return mPrefs.getLong(PREF_UPDATE_TIME_WORSHIPS, 0);
    }

    public void onWorshipsUpdated() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        mPrefs.edit().putLong(PREF_UPDATE_TIME_WORSHIPS, currentTime).apply();
    }

    public long getUpdateTimeSermons() {
        return mPrefs.getLong(PREF_UPDATE_TIME_SERMONS, 0);
    }

    public void onSermonsUpdated() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        mPrefs.edit().putLong(PREF_UPDATE_TIME_SERMONS, currentTime).apply();
    }

    public long getUpdateTimeWitnesses() {
        return mPrefs.getLong(PREF_UPDATE_TIME_WITNESSES, 0);
    }

    public void onWitnessesUpdated() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        mPrefs.edit().putLong(PREF_UPDATE_TIME_WITNESSES, currentTime).apply();
    }

    public long getUpdateTimeSchedule() {
        return mPrefs.getLong(PREF_UPDATE_TIME_SCHEDULE, 0);
    }

    public void onScheduleUpdated() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        mPrefs.edit().putLong(PREF_UPDATE_TIME_SCHEDULE, currentTime).apply();
    }

    public int getFontSize() {
        return mPrefs.getInt(PREF_FONT_SIZE, 1);
    }

    public void setFontSize(int size) {
        mPrefs.edit().putInt(PREF_FONT_SIZE, size).apply();
    }

    public boolean isLiveStarted() {
        return mPrefs.getBoolean(PREF_IS_LIVE_STARTED, false);
    }

    public void setLiveStarted(boolean isStarted) {
        mPrefs.edit().putBoolean(PREF_IS_LIVE_STARTED, isStarted).apply();
    }

    public boolean isPlayViaWifiOnly() {
        String key = mContext.getString(R.string.pref_data_cellular_key);
        return mPrefs.getBoolean(key, true);
    }

    public void setPlayViaWifiOnly(boolean playViaWifiOnly) {
        String key = mContext.getString(R.string.pref_data_cellular_key);
        mPrefs.edit().putBoolean(key, playViaWifiOnly).apply();
    }

    public boolean isShowAudioNotDownloadedWarning() {
        String key = mContext.getString(R.string.pref_data_not_downloaded_key);
        return mPrefs.getBoolean(key, true);
    }

    public boolean isAutoPlayNotDownloadedAudio() {
        String key = mContext.getString(R.string.pref_data_auto_play_key);
        return mPrefs.getBoolean(key, false);
    }

    public void setAutoPlayNotDownloadedAudio() {
        String warningKey = mContext.getString(R.string.pref_data_not_downloaded_key);
        String autoPlayKey = mContext.getString(R.string.pref_data_auto_play_key);
        String autoDownloadKey = mContext.getString(R.string.pref_data_auto_download_key);
        mPrefs.edit()
                .putBoolean(warningKey, false)
                .putBoolean(autoPlayKey, true)
                .putBoolean(autoDownloadKey, false)
                .apply();
    }

    public boolean isAutoDownloadAudioBeforePlay() {
        String key = mContext.getString(R.string.pref_data_auto_download_key);
        return mPrefs.getBoolean(key, false);
    }

    public void setAutoDownloadAudioOnPlay() {
        String warningKey = mContext.getString(R.string.pref_data_not_downloaded_key);
        String autoPlayKey = mContext.getString(R.string.pref_data_auto_play_key);
        String autoDownloadKey = mContext.getString(R.string.pref_data_auto_download_key);
        mPrefs.edit()
                .putBoolean(warningKey, false)
                .putBoolean(autoPlayKey, false)
                .putBoolean(autoDownloadKey, true)
                .apply();
    }

    public SongsListFragment.SortType getSongsSortType() {
        return SongsListFragment.SortType.values()[
                mPrefs.getInt(PREF_SONGS_SORT_TYPE, 0)];
    }

    public void setSongsSortType(SongsListFragment.SortType sortType) {
        mPrefs.edit().putInt(PREF_SONGS_SORT_TYPE, sortType.ordinal()).apply();
    }
}
