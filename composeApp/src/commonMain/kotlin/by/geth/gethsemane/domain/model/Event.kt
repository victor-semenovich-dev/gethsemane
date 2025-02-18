package by.geth.gethsemane.domain.model

import kotlinx.datetime.LocalDateTime

data class Event(
    val id: Int,
    val musicGroupId: Int?,
    val title: String,
    val dateTime: LocalDateTime,
)
