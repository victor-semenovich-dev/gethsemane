package by.geth.gethsemane.data.source.local.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = MusicGroupEntity.TABLE_NAME)
data class MusicGroupEntity(
    @PrimaryKey
    @ColumnInfo(COLUMN_ID)
    val id: Int,
    @ColumnInfo(COLUMN_TITLE)
    val title: String,
    @ColumnInfo(COLUMN_HISTORY)
    val history: String? = null,
    @ColumnInfo(COLUMN_LEADER)
    val leader: String? = null,
    @ColumnInfo(COLUMN_IMAGE)
    val image: String? = null,
    @ColumnInfo(COLUMN_IS_ACTIVE)
    val isActive: Boolean,
) {
    companion object {
        const val TABLE_NAME = "MusicGroups"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_HISTORY = "history"
        const val COLUMN_LEADER = "leader"
        const val COLUMN_IMAGE = "image"
        const val COLUMN_IS_ACTIVE = "isActive"
    }
}
