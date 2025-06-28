package by.geth.gethsemane.data.source.local.db

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

class DbConverters {
    @OptIn(FormatStringsInDatetimeFormats::class)
    private val dateTimeFormat = LocalDateTime.Format {
        byUnicodePattern("yyyy-MM-dd HH:mm")
    }

    @OptIn(FormatStringsInDatetimeFormats::class)
    private val dateFormat = LocalDate.Format {
        byUnicodePattern("yyyy-MM-dd")
    }

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime) = dateTimeFormat.format(value)

    @TypeConverter
    fun toLocalDateTime(value: String) = dateTimeFormat.parse(value)

    @TypeConverter
    fun fromLocalDate(value: LocalDate) = dateFormat.format(value)

    @TypeConverter
    fun toLocalDate(value: String) = dateFormat.parse(value)
}
