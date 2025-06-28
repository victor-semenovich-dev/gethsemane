package by.geth.gethsemane.data.source.local.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

@Entity(tableName = BirthdaysEntity.TABLE_NAME)
data class BirthdaysEntity(
    @PrimaryKey
    @ColumnInfo(COLUMN_DATE)
    val date: LocalDate,
    @ColumnInfo(COLUMN_PERSONS)
    val persons: String,
) {
    companion object {
        const val TABLE_NAME = "Birthdays"
        const val COLUMN_DATE = "date"
        const val COLUMN_PERSONS = "persons"
    }
}
