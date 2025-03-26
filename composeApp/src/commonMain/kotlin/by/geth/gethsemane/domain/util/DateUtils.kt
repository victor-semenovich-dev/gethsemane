package by.geth.gethsemane.domain.util

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

fun LocalDate.isToday(): Boolean {
    val dateNow = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    return this == dateNow
}

fun LocalDate.isTomorrow(): Boolean {
    val dateNow = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    return this == dateNow.plus(1, DateTimeUnit.DAY)
}
