package by.geth.gethsemane.data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

import java.util.Date;

import androidx.annotation.NonNull;
import by.geth.gethsemane.data.base.AudioItem;

@Table(name = "Songs", id = "_id")
public class Song extends Model implements AudioItem {
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_WORSHIP_ID = "id_worship";
	public static final String COLUMN_GROUP_ID = "id_group";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_AUDIO_REMOTE = "url_audio";
	public static final String COLUMN_AUDIO_LOCAL = "audio_local_uri";
	public static final String COLUMN_DATE = "date";

    public Song() {
    }

    public Song(long id, long worshipID, long groupID, String title, String audioURL) {
        setExternalId(id);
        setWorshipID(worshipID);
        setGroupID(groupID);
        setTitle(title);
        setAudioUri(audioURL);
    }

    @SerializedName("id")
    @Column(name = COLUMN_ID)
    private long mID;

    @Column(name = COLUMN_WORSHIP_ID)
    private long mWorshipID;

    @SerializedName("music_group_id")
    @Column(name = COLUMN_GROUP_ID)
    private long mGroupID;

    @SerializedName("title")
    @Column(name = COLUMN_TITLE)
    private String mTitle;

    @SerializedName("audio")
    @Column(name = COLUMN_AUDIO_REMOTE)
    private String mAudioURL;

	@Column(name = COLUMN_AUDIO_LOCAL)
	private String mAudioLocalUri;

	@SerializedName("date")
	@Column(name = COLUMN_DATE)
	private Date mDate;

    @Nullable
    @Override
    public String getRemoteUrl() {
        return mAudioURL;
    }

    @Nullable
    @Override
    public String getLocalPath() {
        return mAudioLocalUri;
    }

    @NonNull
    @Override
    public String getTitle() {
        return mTitle;
    }

    @Nullable
    @Override
    public String getAuthor() {
        MusicGroup group = new Select()
                .from(MusicGroup.class)
                .where(MusicGroup.COLUMN_EXTERNAL_ID + " = ?", getGroupID())
                .executeSingle();
        return group == null ? null : group.getTitle();
    }

    public long externalId() {
        return mID;
    }

    public void setExternalId(long id) {
        mID = id;
    }

    public long getWorshipID() {
        return mWorshipID;
    }

    public void setWorshipID(long worshipID) {
        mWorshipID = worshipID;
    }

    public long getGroupID() {
        return mGroupID;
    }

    public void setGroupID(long groupID) {
        mGroupID = groupID;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getAudioUri() {
        return mAudioURL;
    }

    public void setAudioUri(String audioURL) {
        mAudioURL = audioURL;
    }

	public String getAudioLocalUri() {
		return mAudioLocalUri;
	}

	public void setAudioLocalUri(String audioLocalUri) {
		mAudioLocalUri = audioLocalUri;
	}

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Song song = (Song) o;
        return mID == song.mID &&
            mWorshipID == song.mWorshipID &&
            mGroupID == song.mGroupID &&
            mTitle.equals(song.mTitle) &&
            mAudioURL.equals(song.mAudioURL) &&
            (mDate == null && song.mDate == null) || (mDate != null && mDate.equals(song.mDate));
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (mID ^ (mID >>> 32));
        result = 31 * result + (int) (mWorshipID ^ (mWorshipID >>> 32));
        result = 31 * result + (int) (mGroupID ^ (mGroupID >>> 32));
        result = 31 * result + mTitle.hashCode();
        result = 31 * result + mAudioURL.hashCode();
        result = 31 * result + (mAudioLocalUri != null ? mAudioLocalUri.hashCode() : 0);
        result = 31 * result + (mDate != null ? mDate.hashCode() : 0);
        return result;
    }

    @Override
	public String toString() {
		return "Song{" +
				"mID=" + mID +
				", mWorshipID=" + mWorshipID +
				", mGroupID=" + mGroupID +
				", mTitle='" + mTitle + '\'' +
				", mAudioURL='" + mAudioURL + '\'' +
				", mAudioLocalUri='" + mAudioLocalUri + '\'' +
				'}';
	}
}
