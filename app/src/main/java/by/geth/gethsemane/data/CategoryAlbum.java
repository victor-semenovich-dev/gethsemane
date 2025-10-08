package by.geth.gethsemane.data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Victor Semenovich on 18.02.2018.
 */
@Table(name = "CategoryAlbum", id = "_id")
public class CategoryAlbum extends Model {

    public static final String COLUMN_CATEGORY_ID = "category_id";
    public static final String COLUMN_ALBUM_ID = "album_id";

    @Column(name = COLUMN_CATEGORY_ID)
    private long categoryId;
    @Column(name = COLUMN_ALBUM_ID)
    private long albumId;

    public CategoryAlbum() {
    }

    public CategoryAlbum(long categoryId, long albumId) {
        this.categoryId = categoryId;
        this.albumId = albumId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    @Override
    public String toString() {
        return "CategoryAlbum{" +
                "categoryId=" + categoryId +
                ", albumId=" + albumId +
                '}';
    }
}
