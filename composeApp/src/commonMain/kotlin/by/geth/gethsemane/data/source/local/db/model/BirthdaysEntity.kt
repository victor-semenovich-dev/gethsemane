package by.geth.gethsemane.data.source.local.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = BirthdaysEntity.TABLE_NAME)
data class BirthdaysEntity(
    @PrimaryKey
    @ColumnInfo("date")
    val date: String,
    @ColumnInfo("persons")
    val persons: String,
) {
    companion object {
        const val TABLE_NAME = "Birthdays"
    }
}
