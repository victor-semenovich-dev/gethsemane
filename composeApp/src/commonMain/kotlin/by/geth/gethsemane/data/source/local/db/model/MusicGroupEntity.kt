package by.geth.gethsemane.data.source.local.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MusicGroupEntity(
    @PrimaryKey
    @ColumnInfo("id")
    val id: Int,
    @ColumnInfo("title")
    val title: String,
    @ColumnInfo("history")
    val history: String? = null,
    @ColumnInfo("leader")
    val leader: String? = null,
    @ColumnInfo("image")
    val image: String? = null,
    @ColumnInfo("isActive")
    val isActive: Boolean,
)
