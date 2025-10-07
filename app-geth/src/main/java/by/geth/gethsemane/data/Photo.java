package by.geth.gethsemane.data;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import by.geth.gethsemane.data.base.AudioItem;

/**
 * Created by Victor Semenovich on 17.02.2018.
 */
@Table(name = "Photos", id = "_id")
public class Photo extends Model implements Parcelable, Comparable<Photo>, AudioItem {

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public static final String COLUMN_BACKEND_ID = "backend_id";
    public static final String COLUMN_ALBUM_ID = "album_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_PREVIEW_URL = "preview_url";
    public static final String COLUMN_PHOTO_URL = "photo_url";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_FILE_PATH = "file_path";
    public static final String COLUMN_IS_RECENT = "is_recent";
    public static final String COLUMN_EVENT_ID = "event_id";

    private static final DateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @SerializedName("id")
    @Column(name = COLUMN_BACKEND_ID)
    private long backendId;

    @Column(name = COLUMN_ALBUM_ID)
    private long albumId;

    @SerializedName("title")
    @Column(name = COLUMN_TITLE)
    private String title;

    @SerializedName("preview")
    @Column(name = COLUMN_PREVIEW_URL)
    private String previewUrl;

    @SerializedName("photo")
    @Column(name = COLUMN_PHOTO_URL)
    private String photoUrl;

    @SerializedName("date")
    @Column(name = COLUMN_DATE)
    private String dateString;

    @Column(name = COLUMN_FILE_PATH)
    private String filePath;

    @Column(name = COLUMN_IS_RECENT)
    private boolean isRecent = false;

    @Column(name = COLUMN_EVENT_ID)
    private long eventId;

    private Photo(Parcel in) {
        backendId = in.readLong();
        albumId = in.readLong();
        title = in.readString();
        previewUrl = in.readString();
        photoUrl = in.readString();
        dateString = in.readString();
        filePath = in.readString();
        isRecent = in.readInt() > 0;
        eventId = in.readLong();
    }

    public Photo() {
    }

    @Nullable
    @Override
    public String getRemoteUrl() {
        return photoUrl;
    }

    @Nullable
    @Override
    public String getLocalPath() {
        return filePath;
    }

    @NonNull
    @Override
    public String getTitle() {
        return title;
    }

    @Nullable
    @Override
    public String getAuthor() {
        return "Катя Ладнова :)";
    }

    public long getBackendId() {
        return backendId;
    }

    public void setBackendId(long backendId) {
        this.backendId = backendId;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public Date getDate() {
        if (dateString != null) {
            try {
                return sDateFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isRecent() {
        return isRecent;
    }

    public void setRecent(boolean recent) {
        isRecent = recent;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(backendId);
        dest.writeLong(albumId);
        dest.writeString(title);
        dest.writeString(previewUrl);
        dest.writeString(photoUrl);
        dest.writeString(dateString);
        dest.writeString(filePath);
        dest.writeInt(isRecent ? 1 : 0);
        dest.writeLong(eventId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public int compareTo(@NonNull Photo photo) {
        Date thisDate = this.getDate();
        Date photoDate = photo.getDate();
        int idComparison = (int) (photo.getBackendId() - this.getBackendId());
        if (thisDate == null && photoDate == null) {
            return idComparison;
        } else if (thisDate == null) {
            return 1;
        } else if (photoDate == null) {
            return -1;
        } else {
            int dateComparison = photoDate.compareTo(thisDate);
            return dateComparison == 0 ? idComparison : dateComparison;
        }
    }

    @SuppressWarnings("EqualsReplaceableByObjectsCall")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Photo photo = (Photo) o;

        if (backendId != photo.backendId) return false;
        if (albumId != photo.albumId) return false;
        if (isRecent != photo.isRecent) return false;
        if (eventId != photo.eventId) return false;
        if (title != null ? !title.equals(photo.title) : photo.title != null) return false;
        if (previewUrl != null ? !previewUrl.equals(photo.previewUrl) : photo.previewUrl != null)
            return false;
        if (photoUrl != null ? !photoUrl.equals(photo.photoUrl) : photo.photoUrl != null)
            return false;
        return dateString != null ? dateString.equals(photo.dateString) : photo.dateString == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (backendId ^ (backendId >>> 32));
        result = 31 * result + (int) (albumId ^ (albumId >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (previewUrl != null ? previewUrl.hashCode() : 0);
        result = 31 * result + (photoUrl != null ? photoUrl.hashCode() : 0);
        result = 31 * result + (dateString != null ? dateString.hashCode() : 0);
        result = 31 * result + (isRecent ? 1 : 0);
        result = 31 * result + (int) (eventId ^ (eventId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "backendId=" + backendId +
                ", albumId=" + albumId +
                ", title='" + title + '\'' +
                ", previewUrl='" + previewUrl + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", dateString='" + dateString + '\'' +
                ", filePath='" + filePath + '\'' +
                ", isRecent=" + isRecent +
                ", eventId=" + eventId +
                '}';
    }
}
