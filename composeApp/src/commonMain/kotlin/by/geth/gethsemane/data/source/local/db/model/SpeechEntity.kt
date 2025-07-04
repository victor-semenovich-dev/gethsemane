package by.geth.gethsemane.data.source.local.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

@Entity(tableName = SpeechEntity.TABLE_NAME)
data class SpeechEntity(
    @PrimaryKey
    @ColumnInfo(name = COLUMN_ID)
    val id: Int,
    @ColumnInfo(name = COLUMN_AUTHOR_ID)
    val authorId: Int,
    @ColumnInfo(name = COLUMN_EVENT_ID)
    val eventId: Int?,
    @ColumnInfo(name = COLUMN_TITLE)
    val title: String,
    @ColumnInfo(name = COLUMN_AUTHOR)
    val author: String?,
    @ColumnInfo(name = COLUMN_DATE)
    val date: LocalDate?,
    @ColumnInfo(name = COLUMN_AUDIO_REMOTE)
    val audioRemote: String,
    @ColumnInfo(name = COLUMN_AUDIO_LOCAL)
    val audioLocal: String?,
    @ColumnInfo(name = COLUMN_CATEGORY)
    val category: Category,
    @ColumnInfo(name = COLUMN_SHOW_IN_MEDIA)
    val showInMedia: Boolean,
) {
    enum class Category { SERMON, WITNESS }

    companion object {
        const val TABLE_NAME = "Speeches"
        const val COLUMN_ID = "id"
        const val COLUMN_AUTHOR_ID = "author_id"
        const val COLUMN_EVENT_ID = "event_id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_DATE = "date"
        const val COLUMN_AUDIO_REMOTE = "audioRemote"
        const val COLUMN_AUDIO_LOCAL = "audioLocal"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_SHOW_IN_MEDIA = "show_in_media"
    }
}
