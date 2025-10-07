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
@Table(name = "Categories", id = "_id")
public class Category extends Model implements Parcelable, Comparable<Category> {

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public static final String TYPE_PHOTOS = "photos";

    public static final String COLUMN_BACKEND_ID = "backend_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_COVER_URL = "cover_url";
    public static final String COLUMN_TYPE = "type";

    @SerializedName("id")
    @Column(name = COLUMN_BACKEND_ID)
    private long backendId;

    @SerializedName("title")
    @Column(name = COLUMN_TITLE)
    private String title;

    @SerializedName("cover")
    @Column(name = COLUMN_COVER_URL)
    private String coverUrl;

    @Column(name = COLUMN_TYPE)
    private String type;

    private Category(Parcel in) {
        backendId = in.readLong();
        title = in.readString();
        coverUrl = in.readString();
        type = in.readString();
    }

    public Category() {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(backendId);
        dest.writeString(title);
        dest.writeString(coverUrl);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public int compareTo(@NonNull Category category) {
        if (this.title == null && category.title == null) {
            return 0;
        } else if (category.title == null) {
            return -1;
        } else {
            return this.title.compareTo(category.title);
        }
    }

    @Override
    public String toString() {
        return "Category{" +
                "backendId=" + backendId +
                ", title='" + title + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
