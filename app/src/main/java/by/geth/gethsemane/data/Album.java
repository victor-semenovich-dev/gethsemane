package by.geth.gethsemane.data;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Victor Semenovich on 17.02.2018.
 */
@Table(name = "Albums", id = "_id")
public class Album extends Model implements Parcelable, Comparable<Album> {

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    public static final String COLUMN_BACKEND_ID = "backend_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_COVER_URL = "cover_url";

    @SerializedName("id")
    @Column(name = COLUMN_BACKEND_ID)
    private long backendId;

    @SerializedName("title")
    @Column(name = COLUMN_TITLE)
    private String title;

    @SerializedName("cover")
    @Column(name = COLUMN_COVER_URL)
    private String coverUrl;

    private Album(Parcel in) {
        backendId = in.readLong();
        title = in.readString();
        coverUrl = in.readString();
    }

    public Album() {
    }

    public long getBackendId() {
        return backendId;
    }

    public void setBackendId(long backendId) {
        this.backendId = backendId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(backendId);
        dest.writeString(title);
        dest.writeString(coverUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public int compareTo(@NonNull Album album) {
        return Long.compare(album.backendId, this.backendId);
    }

    @Override
    public String toString() {
        return "Album{" +
                "backendId=" + backendId +
                ", title='" + title + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                '}';
    }
}
