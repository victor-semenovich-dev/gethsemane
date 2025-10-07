package by.geth.gethsemane.data;

import androidx.annotation.NonNull;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import by.geth.gethsemane.data.base.AudioItem;

@Table(name = "Worships", id = "_id")
public class Worship extends Model implements AudioItem {
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_AUDIO_REMOTE = "url_audio";
	public static final String COLUMN_AUDIO_LOCAL = "url_audio_local";
	public static final String COLUMN_VIDEO_REMOTE = "url_video";
	public static final String COLUMN_POSTER_REMOTE = "url_poster";

    @SerializedName("id")
    @Column(name = COLUMN_ID)
    private long mID;

    @SerializedName("date")
    @Column(name = COLUMN_DATE)
    private Date mDate;

    @SerializedName("title")
    @Column(name = COLUMN_TITLE)
    private String mTitle;

    @SerializedName("short_desc")
    @Column(name = COLUMN_DESCRIPTION)
    private String mDescription;

    @SerializedName("audio")
    @Column(name = COLUMN_AUDIO_REMOTE)
    private String mAudioURL;

	@Column(name = COLUMN_AUDIO_LOCAL)
	private String mAudioLocalUri;

    @SerializedName("video")
    @Column(name = COLUMN_VIDEO_REMOTE)
    private String mVideoUri;

    @SerializedName("poster")
    @Column(name = COLUMN_POSTER_REMOTE)
    private String mPosterUri;

    @SerializedName("songs")
    private List<Song> mSongList = new ArrayList<>();

    @SerializedName("sermons")
    private List<Sermon> mSermonList = new ArrayList<>();

    @SerializedName("witnesses")
    private List<Witness> mWitnessList = new ArrayList<>();

    @SerializedName("photos")
    private List<Photo> mPhotoList = new ArrayList<>();

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
        return "Гефсимания";
    }

    public long getExternalId() {
        return mID;
    }

    public void setID(long id) {
        mID = id;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
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

	public String getVideoUri() {
        return mVideoUri;
    }

    public void setVideoUri(String videoUri) {
        mVideoUri = videoUri;
    }

    public String getPosterUri() {
        return mPosterUri;
    }

    public void setPosterUri(String posterUri) {
        mPosterUri = posterUri;
    }

    public List<Song> getSongList() {
        return mSongList;
    }

    public void setSongList(List<Song> songList) {
        mSongList = songList;
    }

    public List<Sermon> getSermonList() {
        return mSermonList;
    }

    public void setSermonList(List<Sermon> sermonList) {
        mSermonList = sermonList;
    }

    public List<Witness> getWitnessList() {
        return mWitnessList;
    }

    public void setWitnessList(List<Witness> witnessList) {
        mWitnessList = witnessList;
    }

    public List<Photo> getPhotoList() {
        return mPhotoList;
    }

    public void setPhotoList(List<Photo> mPhotoList) {
        this.mPhotoList = mPhotoList;
    }

    public String getName() {
        if (mDate == null) {
            return mTitle;
        } else {
            DateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", new Locale("ru"));
            String part1 = dateFormat.format(mDate);

            Pattern pattern = Pattern.compile("\\(.+\\)");
            Matcher matcher = pattern.matcher(mTitle);
            if (matcher.find()) {
                String part2 = matcher.group();
                return part1 + " " + part2;
            } else {
                return part1;
            }
        }
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		Worship worship = (Worship) o;

		if (mID != worship.mID) return false;
		if (!mDate.equals(worship.mDate)) return false;
		if (!mTitle.equals(worship.mTitle)) return false;
		if (mDescription != null ? !mDescription.equals(worship.mDescription) : worship.mDescription != null)
			return false;
		if (mAudioURL != null ? !mAudioURL.equals(worship.mAudioURL) : worship.mAudioURL != null)
			return false;
		if (mAudioLocalUri != null ? !mAudioLocalUri.equals(worship.mAudioLocalUri) : worship.mAudioLocalUri != null)
			return false;
		if (mVideoUri != null ? !mVideoUri.equals(worship.mVideoUri) : worship.mVideoUri != null)
			return false;
		if (mPosterUri != null ? !mPosterUri.equals(worship.mPosterUri) : worship.mPosterUri != null)
			return false;
		if (mSongList != null ? !mSongList.equals(worship.mSongList) : worship.mSongList != null)
			return false;
		if (mSermonList != null ? !mSermonList.equals(worship.mSermonList) : worship.mSermonList != null)
			return false;
		if (mPhotoList != null ? !mPhotoList.equals(worship.mPhotoList) : worship.mPhotoList != null)
		    return false;
		return mWitnessList != null ? mWitnessList.equals(worship.mWitnessList) : worship.mWitnessList == null;

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (int) (mID ^ (mID >>> 32));
		result = 31 * result + mDate.hashCode();
		result = 31 * result + mTitle.hashCode();
		result = 31 * result + (mDescription != null ? mDescription.hashCode() : 0);
		result = 31 * result + (mAudioURL != null ? mAudioURL.hashCode() : 0);
		result = 31 * result + (mAudioLocalUri != null ? mAudioLocalUri.hashCode() : 0);
		result = 31 * result + (mVideoUri != null ? mVideoUri.hashCode() : 0);
		result = 31 * result + (mPosterUri != null ? mPosterUri.hashCode() : 0);
		result = 31 * result + (mSongList != null ? mSongList.hashCode() : 0);
		result = 31 * result + (mSermonList != null ? mSermonList.hashCode() : 0);
        result = 31 * result + (mPhotoList != null ? mPhotoList.hashCode() : 0);
		result = 31 * result + (mWitnessList != null ? mWitnessList.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Worship{" +
				"mID=" + mID +
				", mDate=" + mDate +
				", mTitle='" + mTitle + '\'' +
				", mDescription='" + mDescription + '\'' +
				", mAudioURL='" + mAudioURL + '\'' +
				", mAudioLocalUri='" + mAudioLocalUri + '\'' +
				", mVideoUri='" + mVideoUri + '\'' +
				", mPosterUri='" + mPosterUri + '\'' +
				", mSongList=" + mSongList +
				", mSermonList=" + mSermonList +
				", mWitnessList=" + mWitnessList +
                ", mPhotoList=" + mPhotoList +
				'}';
	}
}
