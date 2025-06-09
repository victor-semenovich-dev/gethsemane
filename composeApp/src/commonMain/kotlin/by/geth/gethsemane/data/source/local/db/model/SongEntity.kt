package by.geth.gethsemane.data.source.local.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = SongEntity.TABLE_NAME)
data class SongEntity(
    @PrimaryKey
    @ColumnInfo(COLUMN_ID)
    val id: Int,
    @ColumnInfo(COLUMN_MUSIC_GROUP_ID)
    val musicGroupId: Int,
    @ColumnInfo(COLUMN_EVENT_ID)
    val eventId: Int?,
    @ColumnInfo(COLUMN_TITLE)
    val title: String,
    @ColumnInfo(COLUMN_AUDIO_REMOTE)
    val audioRemote: String,
    @ColumnInfo(COLUMN_AUDIO_LOCAL)
    val audioLocal: String?,
    @ColumnInfo(COLUMN_SHOW_IN_MEDIA)
    val showInMedia: Boolean,
) {
    companion object {
        const val TABLE_NAME = "Songs"
        const val COLUMN_ID = "id"
        const val COLUMN_MUSIC_GROUP_ID = "music_group_id"
        const val COLUMN_EVENT_ID = "event_id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_AUDIO_REMOTE = "audio_remote"
        const val COLUMN_AUDIO_LOCAL = "audio_local"
        const val COLUMN_SHOW_IN_MEDIA = "show_in_list"
    }
}
