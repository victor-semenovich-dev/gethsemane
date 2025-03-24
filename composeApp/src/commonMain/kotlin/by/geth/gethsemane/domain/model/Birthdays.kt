package by.geth.gethsemane.domain.model

import kotlinx.datetime.LocalDate

data class Birthdays(
    val date: LocalDate,
    val persons: List<String>,
)
