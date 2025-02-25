package by.geth.gethsemane.data.source.local.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = EventEntity.TABLE_NAME)
data class EventEntity(
    @PrimaryKey
    @ColumnInfo("id")
    val id: Int,
    @ColumnInfo("categoryId")
    val categoryId: Int,
    @ColumnInfo("title")
    val title: String,
    @ColumnInfo("date")
    val date: String,
    @ColumnInfo("note")
    val note: String? = null,
    @ColumnInfo("audio")
    val audio: String? = null,
    @ColumnInfo("shortDesc")
    val shortDesc: String? = null,
    @ColumnInfo("isDraft")
    val isDraft: Boolean,
    @ColumnInfo("isArchive")
    val isArchive: Boolean,
    @ColumnInfo("musicGroupId")
    val musicGroupId: Int? = null,
    @ColumnInfo("video")
    val video: String? = null,
) {
    companion object {
        const val TABLE_NAME = "Events"
    }
}
