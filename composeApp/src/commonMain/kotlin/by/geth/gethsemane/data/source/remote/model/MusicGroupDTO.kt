package by.geth.gethsemane.data.source.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MusicGroupDTO(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("history")
    val history: String? = null,
    @SerialName("leader")
    val leader: String? = null,
    @SerialName("image")
    val image: String? = null,
    @SerialName("isActive")
    val isActive: Boolean,
)
