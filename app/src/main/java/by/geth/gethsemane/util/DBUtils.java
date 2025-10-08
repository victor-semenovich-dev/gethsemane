package by.geth.gethsemane.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;

import java.util.List;

import by.geth.gethsemane.data.Author;
import by.geth.gethsemane.data.Event;
import by.geth.gethsemane.data.Photo;
import by.geth.gethsemane.data.Sermon;
import by.geth.gethsemane.data.Song;
import by.geth.gethsemane.data.Witness;
import by.geth.gethsemane.data.Worship;

public class DBUtils {
    public static Worship getWorship(long id) {
        Worship worship = new Select()
                .from(Worship.class)
                .where(Worship.COLUMN_ID + " = ?", id)
                .executeSingle();
        if (worship != null) {
            List<Sermon> sermons = new Select()
                    .from(Sermon.class)
                    .where(Sermon.COLUMN_WORSHIP_ID + " = ?", worship.getExternalId())
                    .execute();
            List<Witness> witnesses = new Select()
                    .from(Witness.class)
                    .where(Witness.COLUMN_WORSHIP_ID + " = ?", worship.getExternalId())
                    .execute();
            List<Song> songs = new Select()
                    .from(Song.class)
                    .where(Song.COLUMN_WORSHIP_ID + " = ?", worship.getExternalId())
                    .execute();
            List<Photo> photos = new Select()
                    .from(Photo.class)
                    .where(Photo.COLUMN_EVENT_ID + " = ?", worship.getExternalId())
                    .execute();
            worship.setSermonList(sermons);
            worship.setWitnessList(witnesses);
            worship.setSongList(songs);
            worship.setPhotoList(photos);
        }
        return worship;
    }

    public static boolean isAuthorsLoaded() {
        return new Select().from(Author.class).exists();
    }

    public static boolean isEventsLoaded() {
        return new Select().from(Event.class).exists();
    }

    public static boolean isDataInitialized() {
        return isAuthorsLoaded() && isEventsLoaded();
    }

    public static void createColumnIfNotExists(String table, String column) {
        SQLiteDatabase db = ActiveAndroid.getDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table + " LIMIT 0", null);
        if (cursor.getColumnIndex(column) < 0) {
            ActiveAndroid.execSQL("ALTER TABLE " + table + " ADD COLUMN " + column + " TEXT;");
        }
        cursor.close();
        db.close();
    }
}
