package by.geth.gethsemane.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import by.geth.gethsemane.data.base.AudioItem;

@Table(name = "Events", id = "_id")
public class Event extends Model implements Parcelable, AudioItem {
	public static final String COLUMN_ID = "external_id";
	public static final String COLUMN_CATEGORY_ID = "category_id";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_SHORT_DESC = "short_desc";
	public static final String COLUMN_NOTE = "note";
	public static final String COLUMN_IS_DRAFT = "is_draft";
	public static final String COLUMN_IS_ARCHIVE = "is_archive";
	public static final String COLUMN_AUDIO_REMOTE = "audio";
	public static final String COLUMN_AUDIO_LOCAL = "audio_local";
	public static final String COLUMN_GROUP_ID = "music_group_id";
	public static final String COLUMN_VIDEO_REMOTE = "video";

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public static final int ID_CATEGORY_WORSHIP = 10;

    public static boolean isScheduleEventsEqual(Event ev1, Event ev2) {
        boolean result = true;
        result &= ev1.externalId() == ev2.externalId();
        result &= ev1.getTitle().equals(ev2.getTitle());
        result &= (TextUtils.isEmpty(ev1.getNote()) && TextUtils.isEmpty(ev2.getNote())) ||
                (ev1.getNote() != null && ev1.getNote().equals(ev2.getNote()));
        result &= ev1.getMusicGroupId() == ev2.getMusicGroupId();
        result &= ev1.getDate().equals(ev2.getDate());
        return result;
    }

    public Event() {
    }

    public Event(Parcel in) {
        setExternalId(in.readLong());
        setCategoryId(in.readLong());
        setDate(new Date(in.readLong()));
        setTitle(in.readString());
        setShortDesc(in.readString());
        setNote(in.readString());
        setDraft(in.readInt() > 0);
        setArchive(in.readInt() > 0);
        setAudioFileName(in.readString());
        setAudioLocalUri(in.readString());
        setMusicGroupId(in.readLong());
        setVideoFileName(in.readString());
    }

    @SerializedName("id")
    @Column(name = COLUMN_ID)
    private long mExternalId;

    @SerializedName("category_id")
    @Column(name = COLUMN_CATEGORY_ID)
    private long mCategoryId;

    @SerializedName("date")
    @Column(name = COLUMN_DATE)
    private Date mDate;

    @SerializedName("title")
    @Column(name = COLUMN_TITLE)
    private String mTitle;

    @SerializedName("short_desc")
    @Column(name = COLUMN_SHORT_DESC)
    private String mShortDesc;

    @SerializedName("note")
    @Column(name = COLUMN_NOTE)
    private String mNote;

    @SerializedName("is_draft")
    @Column(name = COLUMN_IS_DRAFT)
    private int mIsDraft;

    @SerializedName("is_archive")
    @Column(name = COLUMN_IS_ARCHIVE)
    private int mIsArchive;

    @SerializedName("audio")
    @Column(name = COLUMN_AUDIO_REMOTE)
    private String mAudioFileName;

    @Column(name = COLUMN_AUDIO_LOCAL)
    private String mAudioLocalUri;

    @SerializedName("music_group_id")
    @Column(name = COLUMN_GROUP_ID)
    private long mMusicGroupId;

    @SerializedName("video")
    @Column(name = COLUMN_VIDEO_REMOTE)
    private String mVideoFileName;

    @Nullable
    @Override
    public String getRemoteUrl() {
        if (TextUtils.isEmpty(getAudioFileName())) {
            return null;
        } else {
            return "http://geth.by/audio/" + getAudioFileName();
        }
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

    public long externalId() {
        return mExternalId;
    }

    public void setExternalId(long externalId) {
        mExternalId = externalId;
    }

    public long getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(long categoryId) {
        mCategoryId = categoryId;
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

    public String getShortDesc() {
        return mShortDesc;
    }

    public void setShortDesc(String shortDesc) {
        mShortDesc = shortDesc;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String note) {
        mNote = note;
    }

    public boolean isDraft() {
        return mIsDraft > 0;
    }

    public void setDraft(boolean isDraft) {
        mIsDraft = isDraft ? 1 : 0;
    }

    public boolean isArchive() {
        return mIsArchive > 0;
    }

    public void setArchive(boolean isArchive) {
        mIsArchive = isArchive ? 1 : 0;
    }

    public String getAudioFileName() {
        return mAudioFileName;
    }

    public void setAudioFileName(String audioUri) {
        mAudioFileName = audioUri;
    }

    public String getAudioLocalUri() {
        return mAudioLocalUri;
    }

    public void setAudioLocalUri(String audioLocalUri) {
        mAudioLocalUri = audioLocalUri;
    }

    public long getMusicGroupId() {
        return mMusicGroupId;
    }

    public void setMusicGroupId(long musicGroupId) {
        mMusicGroupId = musicGroupId;
    }

    public String getVideoFileName() {
        return mVideoFileName;
    }

    public void setVideoFileName(String videoFileName) {
        mVideoFileName = videoFileName;
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(externalId());
        dest.writeLong(getCategoryId());
        dest.writeLong(getDate().getTime());
        dest.writeString(getTitle());
        dest.writeString(getShortDesc());
        dest.writeString(getNote());
        dest.writeInt(isDraft() ? 1 : 0);
        dest.writeInt(isArchive() ? 1 : 0);
        dest.writeString(getAudioFileName());
        dest.writeString(getAudioLocalUri());
        dest.writeLong(getMusicGroupId());
        dest.writeString(getVideoFileName());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Event) {
            Event other = (Event) obj;

            boolean result = true;
            result &= externalId() == other.externalId();
            result &= getCategoryId() == other.getCategoryId();
            result &= getDate().equals(other.getDate());
            result &= getTitle().equals(other.getTitle());
            result &= (getShortDesc() == null && other.getShortDesc() == null) ||
                    (getShortDesc() != null && getShortDesc().equals(other.getShortDesc()));
            result &= (getNote() == null && other.getNote() == null) ||
                    (getNote() != null && getNote().equals(other.getNote()));
            result &= isDraft() == other.isDraft();
            result &= isArchive() == other.isArchive();
            result &= (getAudioFileName() == null && other.getAudioFileName() == null) ||
                    (getAudioFileName() != null && getAudioFileName().equals(other.getAudioFileName()));
            result &= getMusicGroupId() == other.getMusicGroupId();
            result &= (getVideoFileName() == null && other.getVideoFileName() == null) ||
                    (getVideoFileName() != null && getVideoFileName().equals(other.getVideoFileName()));
            return result;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int code = 1;
        code += 37 * code + hashCode(externalId());
        code += 37 * code + hashCode(getCategoryId());
        code += 37 * code + hashCode(getDate());
        code += 37 * code + hashCode(getTitle());
        code += 37 * code + hashCode(getShortDesc());
        code += 37 * code + hashCode(getNote());
        code += 37 * code + hashCode(isDraft());
        code += 37 * code + hashCode(isArchive());
        code += 37 * code + hashCode(getAudioFileName());
        code += 37 * code + hashCode(getMusicGroupId());
        code += 37 * code + hashCode(getVideoFileName());
        return code;
    }

    private int hashCode(boolean value) {
        return value ? 1 : 0;
    }

    private int hashCode(long value) {
        return (int)(value ^ (value >>> 32));
    }

    private int hashCode(Object value) {
        return value == null ? 0 : value.hashCode();
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + getId() +
                ", externalId=" + externalId() +
                ", categoryId=" + getCategoryId() +
                ", date=" + getDate() +
                ", title=" + getTitle() +
                ", shortDesc='" + getShortDesc() + '\'' +
                ", note='" + getNote() + '\'' +
                ", isDraft=" + isDraft() +
                ", isArchive=" + isArchive() +
                ", audio='" + getAudioFileName() + '\'' +
                ", musicGroupId=" + getMusicGroupId() +
                ", video=" + getVideoFileName() +
                '}';
    }
}
