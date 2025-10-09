package by.geth.gethsemane.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthorDTO(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("biography")
    val biography: String?,
)
