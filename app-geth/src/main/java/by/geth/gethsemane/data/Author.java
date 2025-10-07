package by.geth.gethsemane.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

@Table(name = "Authors", id = "_id")
public class Author extends Model implements Parcelable {

    public static final Creator<Author> CREATOR = new Creator<Author>() {
        @Override
        public Author createFromParcel(Parcel in) {
            return new Author(in);
        }

        @Override
        public Author[] newArray(int size) {
            return new Author[size];
        }
    };

	public static final String COLUMN_ID = "id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_BIOGRAPHY = "biography";

    @SerializedName("id")
    @Column(name = COLUMN_ID)
    private long mID;

    @SerializedName("name")
    @Column(name = COLUMN_NAME)
    private String mName;

    @SerializedName("biography")
    @Column(name = COLUMN_BIOGRAPHY)
    private String mBiography;

    private Author(Parcel in) {
        mID = in.readLong();
        mName = in.readString();
        mBiography = in.readString();
    }

    public Author() {
    }

    public Author(long id, String name, String biography) {
        setID(id);
        setName(name);
        setBiography(biography);
    }

    public long getID() {
        return mID;
    }

    public void setID(long id) {
        mID = id;
    }

    public String getName() {
        return mName.trim();
    }

    public void setName(String name) {
        mName = name;
    }

    public String getBiography() {
        return mBiography;
    }

    public void setBiography(String biography) {
        mBiography = biography;
    }

    @Override
    public int hashCode() {
        int code = 0;
        code += mID;
        code += (mName == null) ? 0 : mName.hashCode();
        code += (mBiography == null) ? 0 : mBiography.hashCode();
        return code;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Author) {
            Author author = (Author) obj;
            boolean isEquals = true;
            isEquals &= (this.getID() == author.getID());
            isEquals &= ((this.getName() == null && author.getName() == null)
                    || (this.getName().equals(author.getName())));
            isEquals &= ((this.getBiography() == null && author.getBiography() == null)
                    || (this.getBiography().equals(author.getBiography())));
            return isEquals;
        } else {
            return false;
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mID);
        dest.writeString(mName);
        dest.writeString(mBiography);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Author{" +
                "ID=" + getID() +
                ", name='" + getName() + '\'' +
                ", biography='" + getBiography() + '\'' +
                '}';
    }
}
