package by.geth.gethsemane.data.source.local.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(tableName = EventEntity.TABLE_NAME)
data class EventEntity(
    @PrimaryKey
    @ColumnInfo(COLUMN_ID)
    val id: Int,
    @ColumnInfo(COLUMN_CATEGORY_ID)
    val categoryId: Int,
    @ColumnInfo(COLUMN_TITLE)
    val title: String,
    @ColumnInfo(COLUMN_DATE)
    val date: LocalDateTime,
    @ColumnInfo(COLUMN_NOTE)
    val note: String? = null,
    @ColumnInfo(COLUMN_AUDIO_REMOTE)
    val audioRemote: String? = null,
    @ColumnInfo(COLUMN_AUDIO_LOCAL)
    val audioLocal: String? = null,
    @ColumnInfo(COLUMN_SHORT_DESC)
    val shortDesc: String? = null,
    @ColumnInfo(COLUMN_IS_DRAFT)
    val isDraft: Boolean,
    @ColumnInfo(COLUMN_IS_ARCHIVE)
    val isArchive: Boolean,
    @ColumnInfo(COLUMN_MUSIC_GROUP_ID)
    val musicGroupId: Int? = null,
    @ColumnInfo(COLUMN_VIDEO)
    val video: String? = null,
    @ColumnInfo(COLUMN_POSTER)
    val poster: String? = null,
) {
    companion object {
        const val TABLE_NAME = "Events"
        const val COLUMN_ID = "id"
        const val COLUMN_CATEGORY_ID = "categoryId"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DATE = "date"
        const val COLUMN_NOTE = "note"
        const val COLUMN_AUDIO_REMOTE = "audio_remote"
        const val COLUMN_AUDIO_LOCAL = "audio_local"
        const val COLUMN_SHORT_DESC = "shortDesc"
        const val COLUMN_IS_DRAFT = "isDraft"
        const val COLUMN_IS_ARCHIVE = "isArchive"
        const val COLUMN_MUSIC_GROUP_ID = "musicGroupId"
        const val COLUMN_VIDEO = "video"
        const val COLUMN_POSTER = "poster"
    }
}
