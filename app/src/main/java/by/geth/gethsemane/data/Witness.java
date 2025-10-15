package by.geth.gethsemane.data;

import androidx.annotation.NonNull;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

import java.util.Date;

import by.geth.gethsemane.data.base.AudioItem;
import by.geth.gethsemane.data.model.local.activeAndroid.AuthorEntity;

@SuppressWarnings("unused")
@Table(name = "Witnesses", id = "_id")
public class Witness extends Model implements AudioItem {
    public static final int DONT_SHOW_IN_LIST = 0;
    public static final int SHOW_IN_LIST = 1;
    public static final int SHOW_IN_LIST_WITH_QUERY = 2;

	public static final String COLUMN_ID = "external_id";
	public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_AUTHOR = "author";
	public static final String COLUMN_AUTHOR_ID = "author_id";
	public static final String COLUMN_CONTENT = "content";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_AUDIO_REMOTE = "audio_uri";
	public static final String COLUMN_AUDIO_LOCAL = "audio_local_uri";
	public static final String COLUMN_WORSHIP_ID = "worship_id";
    public static final String COLUMN_SHOW_IN_LIST = "show_in_list";

    @SerializedName("id")
    @Column(name = COLUMN_ID)
    private long mExternalId;

    @SerializedName("title")
    @Column(name = COLUMN_TITLE)
    private String mTitle;

    @Column(name = COLUMN_AUTHOR)
    private String mAuthor;

    @SerializedName("author_id")
    @Column(name = COLUMN_AUTHOR_ID)
    private int mAuthorId;

    @SerializedName("content")
    @Column(name = COLUMN_CONTENT)
    private String mContent;

    @SerializedName("date")
    @Column(name = COLUMN_DATE)
    private Date mDate;

    @SerializedName("audio")
    @Column(name = COLUMN_AUDIO_REMOTE)
    private String mAudioUri;

	@Column(name = COLUMN_AUDIO_LOCAL)
	private String mAudioLocalUri;

    @Column(name = COLUMN_WORSHIP_ID)
    private long mWorshipId;

    @Column(name = COLUMN_SHOW_IN_LIST)
    private int mShowInList;

    @Nullable
    @Override
    public String getRemoteUrl() {
        return mAudioUri;
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
        if (mAuthor != null) {
            return mAuthor;
        }
        AuthorEntity author = new Select()
                .from(AuthorEntity.class)
                .where(AuthorEntity.COLUMN_ID + " = ?", getAuthorId())
                .executeSingle();
        return author == null ? null : author.getName();
    }

    public long getExternalId() {
        return mExternalId;
    }

    public void setExternalId(long externalId) {
        mExternalId = externalId;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getAuthorCol() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public int getAuthorId() {
        return mAuthorId;
    }

    public void setAuthorId(int authorId) {
        mAuthorId = authorId;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getAudioUri() {
        return mAudioUri;
    }

    public void setAudioUri(String audioUri) {
        mAudioUri = audioUri;
    }

	public String getAudioLocalUri() {
		return mAudioLocalUri;
	}

	public void setAudioLocalUri(String audioLocalUri) {
		mAudioLocalUri = audioLocalUri;
	}

    public long getWorshipId() {
        return mWorshipId;
    }

    public void setWorshipId(long worshipId) {
        mWorshipId = worshipId;
    }

    public int getShowInList() {
        return mShowInList;
    }

    public void setShowInList(int showInList) {
        mShowInList = showInList;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Witness witness = (Witness) o;

        if (mExternalId != witness.mExternalId) return false;
        if (mAuthorId != witness.mAuthorId) return false;
        if (mWorshipId != witness.mWorshipId) return false;
        if (!mTitle.equals(witness.mTitle)) return false;
        if (mContent != null ? !mContent.equals(witness.mContent) : witness.mContent != null)
            return false;
        if (!mDate.equals(witness.mDate)) return false;
        if (!mAudioUri.equals(witness.mAudioUri)) return false;
        return mAudioLocalUri != null ? mAudioLocalUri.equals(witness.mAudioLocalUri) : witness.mAudioLocalUri == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (mExternalId ^ (mExternalId >>> 32));
        result = 31 * result + mTitle.hashCode();
        result = 31 * result + mAuthorId;
        result = 31 * result + (mContent != null ? mContent.hashCode() : 0);
        result = 31 * result + mDate.hashCode();
        result = 31 * result + mAudioUri.hashCode();
        result = 31 * result + (mAudioLocalUri != null ? mAudioLocalUri.hashCode() : 0);
        result = 31 * result + (int) (mWorshipId ^ (mWorshipId >>> 32));
        return result;
    }

    @Override
	public String toString() {
		return "Witness{" +
				"mExternalId=" + mExternalId +
				", mTitle='" + mTitle + '\'' +
				", mAuthorId=" + mAuthorId +
				", mContent='" + mContent + '\'' +
				", mDate=" + mDate +
				", mAudioUri='" + mAudioUri + '\'' +
				", mAudioLocalUri='" + mAudioLocalUri + '\'' +
				", mWorshipId=" + mWorshipId +
				'}';
	}
}
