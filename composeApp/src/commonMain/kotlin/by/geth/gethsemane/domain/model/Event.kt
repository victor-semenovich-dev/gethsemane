package by.geth.gethsemane.domain.model

import kotlinx.datetime.LocalDateTime

data class Event(
    val title: String,
    val dateTime: LocalDateTime,
)
