package by.geth.gethsemane.util;

import android.content.Context;
import android.net.Uri;

import com.google.firebase.messaging.RemoteMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import by.geth.gethsemane.app.App;
import by.geth.gethsemane.data.Event;
import by.geth.gethsemane.data.Photo;
import by.geth.gethsemane.data.Sermon;
import by.geth.gethsemane.data.Song;
import by.geth.gethsemane.data.Witness;
import by.geth.gethsemane.data.Worship;
import by.geth.gethsemane.data.base.AudioItem;

public class FileUtils {
    public static void copyFile(File inputFile, File outputFile) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(inputFile);
            fos = new FileOutputStream(outputFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = fis.read(buf)) > 0)
                fos.write(buf, 0, len);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
                fos.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

	/**
	 * Make copy of database file for debug purposes.
	 * Required WRITE_EXTERNAL_STORAGE permission.
	 */
    public static void dumbDBFile(Context context) {
        File dbFile = new File(context.getFilesDir().getParent() + "/databases/geth.db");
        File outputFile = new File(context.getExternalFilesDir(null), "dump.db");
        copyFile(dbFile, outputFile);
    }

    public static boolean isFileExists(AudioItem item) {
        if (item.getLocalPath() == null) {
            return false;
        } else {
            File file = new File(item.getLocalPath());
            return file.exists();
        }
    }

    public static Uri getDownloadDestinationUri(AudioItem item) {
        if (item instanceof Event) {
            return getDownloadDestinationUri((Event) item);
        } else if (item instanceof Worship) {
            return getDownloadDestinationUri((Worship) item);
        } else if (item instanceof Sermon) {
            return getDownloadDestinationUri((Sermon) item);
        } else if (item instanceof Witness) {
            return getDownloadDestinationUri((Witness) item);
        } else if (item instanceof Song) {
            return getDownloadDestinationUri((Song) item);
        } else if (item instanceof Photo) {
            return getDownloadDestinationUri((Photo) item);
        } else {
            return null;
        }
    }

    private static Uri getDownloadDestinationUri(Event event) {
        File dir = new File(App.getContext().getExternalFilesDir(null) + "/Гефсимания/Богослужения");
        if (dir.exists() || dir.mkdirs()) {
            File file = new File(dir, event.getAudioFileName());
            return Uri.fromFile(file);
        }
        return null;
    }

    private static Uri getDownloadDestinationUri(Worship worship) {
        File dir = new File(App.getContext().getExternalFilesDir(null) + "/Гефсимания/Богослужения");
        if (dir.exists() || dir.mkdirs()) {
            Uri remoteUri = Uri.parse(worship.getAudioUri());
            File file = new File(dir, remoteUri.getLastPathSegment());
            return Uri.fromFile(file);
        }
        return null;
    }

    private static Uri getDownloadDestinationUri(Sermon sermon) {
        File dir = new File(App.getContext().getExternalFilesDir(null) + "/Гефсимания/Проповеди");
        if (dir.exists() || dir.mkdirs()) {
            Uri remoteUri = Uri.parse(sermon.getAudioUri());
            int pathSegmentsSize = remoteUri.getPathSegments().size();
            String penultSegment = remoteUri.getPathSegments().get(pathSegmentsSize - 2);
            String lastSegment = remoteUri.getLastPathSegment();
            File file = new File(dir, penultSegment + "/" + lastSegment);
            return Uri.fromFile(file);
        }
        return null;
    }

    private static Uri getDownloadDestinationUri(Witness witness) {
        File dir = new File(App.getContext().getExternalFilesDir(null) + "/Гефсимания/Свидетельства");
        if (dir.exists() || dir.mkdirs()) {
            Uri remoteUri = Uri.parse(witness.getAudioUri());
            File file = new File(dir, remoteUri.getLastPathSegment());
            return Uri.fromFile(file);
        }
        return null;
    }

    private static Uri getDownloadDestinationUri(Song song) {
        File dir = new File(App.getContext().getExternalFilesDir(null) + "/Гефсимания/Псалмы");
        if (dir.exists() || dir.mkdirs()) {
            Uri remoteUri = Uri.parse(song.getAudioUri());
            int pathSegmentsSize = remoteUri.getPathSegments().size();
            String penultSegment = remoteUri.getPathSegments().get(pathSegmentsSize - 2);
            String lastSegment = remoteUri.getLastPathSegment();
            File file = new File(dir, penultSegment + "/" + lastSegment);
            return Uri.fromFile(file);
        }
        return null;
    }

    private static Uri getDownloadDestinationUri(Photo photo) {
        File dir = new File(App.getContext().getExternalFilesDir(null) + "/Гефсимания/Фотографии");
        if (dir.exists() || dir.mkdirs()) {
            Uri remoteUri = Uri.parse(photo.getPhotoUrl());
            File file = new File(dir, remoteUri.getLastPathSegment());
            return Uri.fromFile(file);
        }
        return null;
    }

    public static void writeFcmLog(RemoteMessage message) {
        File file = new File(App.getContext().getExternalFilesDir(null), "fcm.txt");
        try {
            OutputStream os = new FileOutputStream(file, true);
            OutputStreamWriter osw = new OutputStreamWriter(os);
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
            String date = dateFormat.format(new Date(System.currentTimeMillis()));
            osw.write(date + " - " + message.getFrom() + " - " + message.getData() + "\n");
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
