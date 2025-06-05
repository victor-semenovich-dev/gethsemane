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
    @ColumnInfo(COLUMN_WORSHIP_ID)
    val worshipId: Int?,
    @ColumnInfo(COLUMN_TITLE)
    val title: String,
    @ColumnInfo(COLUMN_AUDIO)
    val audio: String,
    @ColumnInfo(COLUMN_SHOW_IN_LIST)
    val showInList: Boolean,
) {
    companion object {
        const val TABLE_NAME = "Songs"
        const val COLUMN_ID = "id"
        const val COLUMN_MUSIC_GROUP_ID = "music_group_id"
        const val COLUMN_WORSHIP_ID = "worship_id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_AUDIO = "audio"
        const val COLUMN_SHOW_IN_LIST = "show_in_list"
    }
}
