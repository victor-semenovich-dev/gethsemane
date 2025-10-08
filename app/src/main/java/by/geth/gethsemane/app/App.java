package by.geth.gethsemane.app;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Base64;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.multidex.MultiDex;

import com.activeandroid.Cache;
import com.activeandroid.app.Application;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;

import by.geth.gethsemane.BuildConfig;
import by.geth.gethsemane.data.Event;
import by.geth.gethsemane.data.Photo;
import by.geth.gethsemane.data.model.articles.ArticleListItem;
import by.geth.gethsemane.di.KoinInitializer;
import by.geth.gethsemane.download.DownloadController;
import by.geth.gethsemane.service.ApiService;
import by.geth.gethsemane.service.GethApi;
import by.geth.gethsemane.util.DBUtils;
import by.geth.gethsemane.util.JsonDateDeserializer;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {
    private static GethApi sGethApi;

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    public static GethApi getGethAPI() {
        return sGethApi;
    }

    public static Context getContext() {
        return sContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG);

        KoinInitializer.INSTANCE.init();

        DBUtils.createColumnIfNotExists(Cache.getTableInfo(Photo.class).getTableName(), Photo.COLUMN_IS_RECENT);
        DBUtils.createColumnIfNotExists(Cache.getTableInfo(ArticleListItem.class).getTableName(),
                ArticleListItem.COLUMN_CATEGORY);

        initGethAPI();
        AppNotificationManager.init(this);
        AppPreferences.init(this);
        DownloadController.init(this);

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.registerReceiver(mAPIServiceReceiver, ApiService.getFullIntentFilter());

        sContext = this;
    }

    private void initGethAPI() {
        String credentials = BuildConfig.API_BASE_AUTH_LOGIN + ":" + BuildConfig.API_BASE_AUTH_PASS;
        final String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request wrappedRequest = originalRequest.newBuilder()
                        .header("Authorization", basic)
                        .header("Accept", "applicaton/json")
                        .method(originalRequest.method(), originalRequest.body())
                        .build();
                return chain.proceed(wrappedRequest);
            }
        }).addInterceptor(loggingInterceptor);
        OkHttpClient client = builder.build();

        Gson gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .registerTypeAdapter(Date.class, new JsonDateDeserializer())
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
        sGethApi = retrofit.create(GethApi.class);
    }

    private BroadcastReceiver mAPIServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String requestType = intent.getStringExtra(ApiService.EXTRA_REQUEST_TYPE);
            boolean showNotification =
                    intent.getBooleanExtra(ApiService.EXTRA_SHOW_NOTIFICATION, false)
                            && AppPreferences.getInstance().isScheduleNotifEnabled();
            if (action.equals(ApiService.ACTION_REQUEST_SUCCESSFUL) &&
                    requestType.equals(ApiService.REQUEST_GET_EVENTS) && showNotification) {
                ArrayList<Event> addedEvents = intent.getParcelableArrayListExtra(ApiService.EXTRA_SCHEDULE_EVENTS_ADDED);
                ArrayList<Event> deletedEvents = intent.getParcelableArrayListExtra(ApiService.EXTRA_SCHEDULE_EVENTS_DELETED);
                ArrayList<Event> modifiedOldEvents = intent.getParcelableArrayListExtra(ApiService.EXTRA_SCHEDULE_EVENTS_MODIFIED_OLD);
                ArrayList<Event> modifiedNewEvents = intent.getParcelableArrayListExtra(ApiService.EXTRA_SCHEDULE_EVENTS_MODIFIED_NEW);
                int addedEventsCount = (addedEvents == null) ? 0 : addedEvents.size();
                int deletedEventsCount = (deletedEvents == null) ? 0 : deletedEvents.size();
                int modifiedNewEventsCount = (modifiedNewEvents == null) ? 0 : modifiedNewEvents.size();
                if (addedEventsCount + deletedEventsCount + modifiedNewEventsCount > 0) {
                    AppNotificationManager.getInstance().notifySchedule(addedEvents, deletedEvents,
                            modifiedOldEvents, modifiedNewEvents);
                }
            }
        }
    };
}
