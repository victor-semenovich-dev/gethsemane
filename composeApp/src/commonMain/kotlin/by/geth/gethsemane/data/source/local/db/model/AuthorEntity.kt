package by.geth.gethsemane.data.source.local.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = AuthorEntity.TABLE_NAME)
data class AuthorEntity(
    @PrimaryKey
    @ColumnInfo(name = COLUMN_ID)
    val id: Int,
    @ColumnInfo(name = COLUMN_NAME)
    val name: String,
    @ColumnInfo(name = COLUMN_BIOGRAPHY)
    val biography: String?,
) {
    companion object {
        const val TABLE_NAME = "Authors"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_BIOGRAPHY = "biography"
    }
}
