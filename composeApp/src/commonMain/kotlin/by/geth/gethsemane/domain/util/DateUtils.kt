package by.geth.gethsemane.domain.util

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

val dateNow: LocalDate
    get() = dateTimeNow.date

val dateTimeNow: LocalDateTime
    get() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

fun LocalDate.isToday(): Boolean {
    return this == dateNow
}

fun LocalDate.isTomorrow(): Boolean {
    return this == dateNow.plus(1, DateTimeUnit.DAY)
}
