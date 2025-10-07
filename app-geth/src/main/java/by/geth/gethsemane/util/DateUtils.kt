package by.geth.gethsemane.util

import java.text.SimpleDateFormat
import java.util.*

fun keepDateOnly(date: Date): Date {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.US)
    val formattedDate = dateFormat.format(date)
    return dateFormat.parse(formattedDate)!!
}
