package by.geth.gethsemane.app;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import by.geth.gethsemane.R;
import by.geth.gethsemane.data.Event;
import by.geth.gethsemane.service.AudioService;
import by.geth.gethsemane.ui.activity.GalleryPhotosGridActivity;
import by.geth.gethsemane.ui.activity.HtmlActivity;
import by.geth.gethsemane.ui.activity.MainActivity;
import by.geth.gethsemane.util.Utils;

public class AppNotificationManager {
    private static final String NOTIFICATION_CHANNEL_ID_LIVE = "live";
    private static final String NOTIFICATION_CHANNEL_ID_BIRTHDAYS = "birthdays";
    private static final String NOTIFICATION_CHANNEL_ID_NEWS = "news";
    private static final String NOTIFICATION_CHANNEL_ID_WORSHIP_NOTES = "worship notes";
    private static final String NOTIFICATION_CHANNEL_ID_TO_SEE_CHRIST = "to see christ";
    private static final String NOTIFICATION_CHANNEL_ID_PHOTOS = "photos";
    private static final String NOTIFICATION_CHANNEL_ID_SCHEDULE = "schedule";
    private static final String NOTIFICATION_CHANNEL_ID_INFO = "info";
    private static final String NOTIFICATION_CHANNEL_ID_AUDIO = "audio";

    public static final int ID_AUDIO = 1;
    private static final int ID_SCHEDULE = 2;
    private static final int ID_BIRTHDAYS = 3;
    private static final int ID_LIVE = 4;
    private static final int ID_NEWS = 5;
    private static final int ID_INFO = 6;
    private static final int ID_PHOTOS = 7;
    private static final int ID_WORSHIP_NOTES = 8;
    private static final int ID_TO_SEE_CHRIST = 9;

    @SuppressLint("StaticFieldLeak")
    private static AppNotificationManager sInstance;

    public static void init(Context context) {
        sInstance = new AppNotificationManager(context);
    }

    public static AppNotificationManager getInstance() {
        return sInstance;
    }

    private final Context mContext;
    private final NotificationManager mManager;

    private AppNotificationManager(Context context) {
        mContext = context;
        mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            List<NotificationChannel> channels = new ArrayList<>();
            channels.add(new NotificationChannel(NOTIFICATION_CHANNEL_ID_LIVE,
                    mContext.getString(R.string.notification_channel_live),
                    NotificationManager.IMPORTANCE_HIGH));
            channels.add(new NotificationChannel(NOTIFICATION_CHANNEL_ID_BIRTHDAYS,
                    mContext.getString(R.string.notification_channel_birthdays),
                    NotificationManager.IMPORTANCE_DEFAULT));
            channels.add(new NotificationChannel(NOTIFICATION_CHANNEL_ID_NEWS,
                    mContext.getString(R.string.notification_channel_news),
                    NotificationManager.IMPORTANCE_DEFAULT));
            channels.add(new NotificationChannel(NOTIFICATION_CHANNEL_ID_PHOTOS,
                    mContext.getString(R.string.notification_channel_photos),
                    NotificationManager.IMPORTANCE_DEFAULT));
            channels.add(new NotificationChannel(NOTIFICATION_CHANNEL_ID_SCHEDULE,
                    mContext.getString(R.string.notification_channel_schedule),
                    NotificationManager.IMPORTANCE_DEFAULT));
            channels.add(new NotificationChannel(NOTIFICATION_CHANNEL_ID_INFO,
                    mContext.getString(R.string.notification_channel_info),
                    NotificationManager.IMPORTANCE_DEFAULT));
            channels.add(new NotificationChannel(NOTIFICATION_CHANNEL_ID_WORSHIP_NOTES,
                    mContext.getString(R.string.worship_notes),
                    NotificationManager.IMPORTANCE_DEFAULT));
            channels.add(new NotificationChannel(NOTIFICATION_CHANNEL_ID_TO_SEE_CHRIST,
                    mContext.getString(R.string.to_see_christ),
                    NotificationManager.IMPORTANCE_DEFAULT));
            NotificationChannel audioChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID_AUDIO,
                    mContext.getString(R.string.notification_channel_audio),
                    NotificationManager.IMPORTANCE_LOW);
            channels.add(audioChannel);
            mManager.createNotificationChannels(channels);
        }
    }

    public Notification getAudioNotification(String author, String title, boolean isPlaying) {
        Intent resultIntent = new Intent(mContext, MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
                );
        NotificationCompat.Builder notificationBuilder =
            new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID_AUDIO)
                .setSmallIcon(R.drawable.ic_notif_audio)
                .setContentTitle(title)
                .setContentText(author)
                .setContentIntent(resultPendingIntent)
                .setOngoing(true);
        if (isPlaying) {
            Intent pauseIntent = new Intent(mContext, AudioService.class);
            pauseIntent.setAction(AudioService.ACTION_PAUSE);
            PendingIntent pendingPauseIntent = PendingIntent.getService(
                mContext, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT |
                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0));
            notificationBuilder.addAction(R.drawable.ic_notif_action_pause,
                mContext.getString(R.string.pause), pendingPauseIntent);
        } else {
            Intent playIntent = new Intent(mContext, AudioService.class);
            playIntent.setAction(AudioService.ACTION_PLAY);
            PendingIntent pendingPlayIntent = PendingIntent.getService(
                mContext, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT |
                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0));
            notificationBuilder.addAction(R.drawable.ic_notif_action_play,
                mContext.getString(R.string.play), pendingPlayIntent);
        }

        Intent stopIntent = new Intent(mContext, AudioService.class);
        stopIntent.setAction(AudioService.ACTION_STOP);
        PendingIntent pendingStopIntent = PendingIntent.getService(
            mContext, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT |
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0));

        notificationBuilder.addAction(R.drawable.ic_notif_action_stop, mContext.getString(R.string.stop), pendingStopIntent);
        if (!Build.MANUFACTURER.toLowerCase(Locale.getDefault()).contains("huawei")) {
            notificationBuilder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1));
        }
        return notificationBuilder.build();
    }

    public void notifySchedule(ArrayList<Event> addedEvents, ArrayList<Event> deletedEvents,
                               ArrayList<Event> modifiedOldEvents, ArrayList<Event> modifiedNewEvents) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(mContext.getString(R.string.notification_schedule)).append(':');
        if (addedEvents != null && !addedEvents.isEmpty()) {
            messageBuilder
                    .append('\n')
                    .append(mContext.getResources().getQuantityString(
                            R.plurals.notification_schedule_added,
                            addedEvents.size(), addedEvents.size()));
        }
        if (deletedEvents != null && !deletedEvents.isEmpty()) {
            messageBuilder
                    .append('\n')
                    .append(mContext.getResources().getQuantityString(
                            R.plurals.notification_schedule_deleted,
                            deletedEvents.size(), deletedEvents.size()));
        }
        if (modifiedOldEvents != null && !modifiedOldEvents.isEmpty()) {
            messageBuilder
                    .append('\n')
                    .append(mContext.getResources().getQuantityString(
                            R.plurals.notification_schedule_modified,
                            modifiedOldEvents.size(), modifiedOldEvents.size()));
        }

        Intent resultIntent = new Intent(mContext, MainActivity.class);
        if (addedEvents != null) {
            resultIntent.putParcelableArrayListExtra(MainActivity.EXTRA_SCHEDULE_EVENTS_ADDED, addedEvents);
        }
        if (deletedEvents != null) {
            resultIntent.putParcelableArrayListExtra(MainActivity.EXTRA_SCHEDULE_EVENTS_DELETED, deletedEvents);
        }
        if (modifiedOldEvents != null) {
            resultIntent.putParcelableArrayListExtra(MainActivity.EXTRA_SCHEDULE_EVENTS_MODIFIED_OLD, modifiedOldEvents);
        }
        if (modifiedNewEvents != null) {
            resultIntent.putParcelableArrayListExtra(MainActivity.EXTRA_SCHEDULE_EVENTS_MODIFIED_NEW, modifiedNewEvents);
        }
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
                );
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Notification notification = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID_SCHEDULE)
                        .setSmallIcon(R.drawable.ic_notif_schedule)
                        .setContentTitle(mContext.getString(R.string.app_title))
                        .setContentText(mContext.getString(R.string.notification_schedule))
                        .setContentIntent(resultPendingIntent)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBuilder))
                        .setSound(alarmSound)
                        .setAutoCancel(true)
                        .build();
        mManager.notify(ID_SCHEDULE, notification);
    }

    public void notifyBirthdays(List<String> birthdayList) {
        Resources res = mContext.getResources();
        String shortMessage = res.getQuantityString(R.plurals.notification_birthdays,
                birthdayList.size(), birthdayList.size());

        String message = Utils.buildBirthdaysMessage(mContext, birthdayList);

        Intent resultIntent = new Intent(mContext, MainActivity.class);
        resultIntent.setAction(MainActivity.ACTION_BIRTHDAYS);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
                );
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID_BIRTHDAYS)
                .setSmallIcon(R.drawable.ic_notif_birthday)
                .setContentTitle(mContext.getString(R.string.app_title))
                .setContentText(shortMessage)
                .setContentIntent(resultPendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSound(alarmSound)
                .setAutoCancel(true)
                .build();
        mManager.notify(ID_BIRTHDAYS, notification);
    }

    public void notifyLiveStarted() {
        Intent resultIntent = new Intent(mContext, MainActivity.class);
        resultIntent.putExtra(MainActivity.EXTRA_SHOW_LIVE_VIDEO, true);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
                );
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String message = mContext.getString(R.string.notification_live_start);
        String shortMessage = mContext.getString(R.string.notification_live_start_short);
        Notification notification = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID_LIVE)
                .setSmallIcon(R.drawable.ic_notif_video)
                .setContentTitle(mContext.getString(R.string.app_title))
                .setContentText(shortMessage)
                .setContentIntent(resultPendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSound(alarmSound)
                .setLights(Color.YELLOW, 3000, 2000)
                .setAutoCancel(true)
                .build();
        mManager.notify(ID_LIVE, notification);
    }

    public void cancelLiveStarted() {
        mManager.cancel(ID_LIVE);
    }

    public void notifyNews(String title, String description, String image, long id) {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setAction(MainActivity.ACTION_NEWS);
        intent.putExtra(MainActivity.EXTRA_ID, id);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
                );

        Glide.with(mContext).asBitmap().load(image).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Notification notification = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID_NEWS)
                    .setSmallIcon(R.drawable.ic_notif_news)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setContentIntent(resultPendingIntent)
                    .setLargeIcon(resource)
                    .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(resource))
                    .setSound(alarmSound)
                    .setLights(Color.YELLOW, 3000, 2000)
                    .setAutoCancel(true)
                    .build();
                mManager.notify(ID_NEWS, notification);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Notification notification = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID_NEWS)
                    .setSmallIcon(R.drawable.ic_notif_news)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setContentIntent(resultPendingIntent)
                    .setSound(alarmSound)
                    .setLights(Color.YELLOW, 3000, 2000)
                    .setAutoCancel(true)
                    .build();
                mManager.notify(ID_NEWS, notification);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });
    }

    public void notifyWorshipNotes(String title, String description, String image, long id) {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setAction(MainActivity.ACTION_WORSHIP_NOTES);
        intent.putExtra(MainActivity.EXTRA_ID, id);
        PendingIntent resultPendingIntent =
            PendingIntent.getActivity(
                mContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT |
                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
            );

        Glide.with(mContext).asBitmap().load(image).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Notification notification = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID_WORSHIP_NOTES)
                    .setSmallIcon(R.drawable.ic_notif_note)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setContentIntent(resultPendingIntent)
                    .setLargeIcon(resource)
                    .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(resource))
                    .setSound(alarmSound)
                    .setLights(Color.YELLOW, 3000, 2000)
                    .setAutoCancel(true)
                    .build();
                mManager.notify(ID_WORSHIP_NOTES, notification);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Notification notification = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID_WORSHIP_NOTES)
                    .setSmallIcon(R.drawable.ic_notif_note)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setContentIntent(resultPendingIntent)
                    .setSound(alarmSound)
                    .setLights(Color.YELLOW, 3000, 2000)
                    .setAutoCancel(true)
                    .build();
                mManager.notify(ID_WORSHIP_NOTES, notification);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });
    }

    public void notifyToSeeChrist(String title, String description, String image, long id) {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setAction(MainActivity.ACTION_TO_SEE_CHRIST);
        intent.putExtra(MainActivity.EXTRA_ID, id);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
                );

        Glide.with(mContext).asBitmap().load(image).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Notification notification = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID_TO_SEE_CHRIST)
                        .setSmallIcon(R.drawable.ic_notif_cross)
                        .setContentTitle(title)
                        .setContentText(description)
                        .setContentIntent(resultPendingIntent)
                        .setLargeIcon(resource)
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(resource))
                        .setSound(alarmSound)
                        .setLights(Color.YELLOW, 3000, 2000)
                        .setAutoCancel(true)
                        .build();
                mManager.notify(ID_TO_SEE_CHRIST, notification);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Notification notification = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID_TO_SEE_CHRIST)
                        .setSmallIcon(R.drawable.ic_notif_cross)
                        .setContentTitle(title)
                        .setContentText(description)
                        .setContentIntent(resultPendingIntent)
                        .setSound(alarmSound)
                        .setLights(Color.YELLOW, 3000, 2000)
                        .setAutoCancel(true)
                        .build();
                mManager.notify(ID_TO_SEE_CHRIST, notification);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });
    }

    public void notifyInfo(String title, String description, String html) {
        Intent intent = new Intent(mContext, HtmlActivity.class);
        intent.putExtra(HtmlActivity.EXTRA_TITLE, title);
        intent.putExtra(HtmlActivity.EXTRA_HTML, html);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
                );

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID_INFO)
                .setSmallIcon(R.drawable.ic_notif_note)
                .setContentTitle(title)
                .setContentText(description)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setLights(Color.YELLOW, 3000, 2000)
                .setAutoCancel(true)
                .build();
        mManager.notify(ID_INFO, notification);
    }

    public void notifyPhotos(JSONArray idsJsonArray) {
        Intent intent = new Intent(mContext, GalleryPhotosGridActivity.class);
        intent.putExtra(GalleryPhotosGridActivity.EXTRA_PHOTOS_IDS, idsJsonArray.toString());
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
                );

        int count = idsJsonArray.length();
        String message = mContext.getResources().getQuantityString(R.plurals.new_photos_notif, count, count);

        if (count > 0) {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Notification notification = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID_PHOTOS)
                    .setSmallIcon(R.drawable.ic_notif_photos)
                    .setContentTitle(mContext.getString(R.string.app_title))
                    .setContentText(message)
                    .setContentIntent(resultPendingIntent)
                    .setSound(alarmSound)
                    .setLights(Color.YELLOW, 3000, 2000)
                    .setAutoCancel(true)
                    .build();
            mManager.notify(ID_PHOTOS, notification);
        }
    }
}
