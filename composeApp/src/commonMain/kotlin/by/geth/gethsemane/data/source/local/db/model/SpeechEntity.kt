package by.geth.gethsemane.data.source.local.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
    @ColumnInfo(name = COLUMN_AUDIO)
    val audio: String,
    @ColumnInfo(name = COLUMN_TYPE)
    val type: Type,
    @ColumnInfo(name = COLUMN_SHOW_IN_MEDIA)
    val showInMedia: Boolean,
) {
    enum class Type { SERMON, WITNESS }

    companion object {
        const val TABLE_NAME = "Speech"
        const val COLUMN_ID = "id"
        const val COLUMN_AUTHOR_ID = "author_id"
        const val COLUMN_EVENT_ID = "event_id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_AUDIO = "audio"
        const val COLUMN_TYPE = "type"
        const val COLUMN_SHOW_IN_MEDIA = "show_in_media"
    }
}
