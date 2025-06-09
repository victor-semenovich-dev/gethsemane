package by.geth.gethsemane.data.source.local.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = PhotoEntity.TABLE_NAME)
data class PhotoEntity(
    @PrimaryKey
    @ColumnInfo(name = COLUMN_ID)
    val id: Int,
    @ColumnInfo(name = COLUMN_EVENT_ID)
    val eventId: Int?,
    @ColumnInfo(name = COLUMN_TITLE)
    val title: String,
    @ColumnInfo(name = COLUMN_PREVIEW)
    val preview: String,
    @ColumnInfo(name = COLUMN_PHOTO)
    val photo: String,
    @ColumnInfo(name = COLUMN_DATE)
    val date: String,
    @ColumnInfo(name = COLUMN_SHOW_IN_MEDIA)
    val showInMedia: Boolean,
) {
    companion object {
        const val TABLE_NAME = "Photos"
        const val COLUMN_ID = "id"
        const val COLUMN_EVENT_ID = "eventId"
        const val COLUMN_TITLE = "title"
        const val COLUMN_PREVIEW = "preview"
        const val COLUMN_PHOTO = "photo"
        const val COLUMN_DATE = "date"
        const val COLUMN_SHOW_IN_MEDIA = "show_in_media"
    }
}
