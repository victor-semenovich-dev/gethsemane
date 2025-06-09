package by.geth.gethsemane.domain.model

import kotlinx.datetime.LocalDateTime

data class Schedule(
    val items: List<ScheduleItem>
)

data class ScheduleItem(
    val id: Int,
    val title: String,
    val dateTime: LocalDateTime,
    val musicGroup: String?,
)
