package by.geth.gethsemane.data.source.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BirthdaysDTO(
    @SerialName("month")
    val month: Int,
    @SerialName("day")
    val day: Int,
    @SerialName("persons")
    val persons: List<String>,
)

