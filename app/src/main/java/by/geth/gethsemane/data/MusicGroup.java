package by.geth.gethsemane.data;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;

import java.util.Date;

import by.geth.gethsemane.data.base.DataModel;

@Table(name = "Groups", id = "_id")
public class MusicGroup extends DataModel<MusicGroup> {
	public static final String COLUMN_EXTERNAL_ID = "id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_HISTORY = "history";
	public static final String COLUMN_LEADER = "leader";
	public static final String COLUMN_IMAGE = "image";
	public static final String COLUMN_IS_SHOW = "isShow";
	public static final String COLUMN_LAST_UPDATE = "lastUpdate";

    @Column(name = COLUMN_EXTERNAL_ID)
    private long externalId;

    @Column(name = COLUMN_TITLE)
    private String title;

    @Column(name = COLUMN_HISTORY)
    private String history;

    @Column(name = COLUMN_LEADER)
    private String leader;

    @Column(name = COLUMN_IMAGE)
    private String image;

    @Column(name = COLUMN_IS_SHOW)
    private boolean isShow;

    @Column(name = COLUMN_LAST_UPDATE)
    private Date lastUpdate;

    @Override
    public long externalId() {
        return externalId;
    }

    public long getExternalId() {
        return externalId;
    }

    public void setExternalId(long mID) {
        this.externalId = mID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String mTitle) {
        this.title = mTitle;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    //-------------------------------------------------------------------------
    @Override
    public void updateWith(MusicGroup other) {
        this.title = other.title;
        this.history = other.history;
        this.leader = other.leader;
        this.image = other.image;
        this.isShow = other.isShow;
    }

    @Override
    public void fullDelete() {
        ActiveAndroid.beginTransaction();

        new Delete().from(Song.class).where(Song.COLUMN_GROUP_ID + " = " + externalId).execute();
        delete();

        ActiveAndroid.setTransactionSuccessful();
        ActiveAndroid.endTransaction();
    }
}
