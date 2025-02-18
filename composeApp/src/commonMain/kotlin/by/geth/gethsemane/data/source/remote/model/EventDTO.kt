package by.geth.gethsemane.data.source.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventDTO(
    @SerialName("id")
    val id: Int,
    @SerialName("category_id")
    val categoryId: Int,
    @SerialName("title")
    val title: String,
    @SerialName("date")
    val date: String,
    @SerialName("note")
    val note: String? = null,
    @SerialName("audio")
    val audio: String? = null,
    @SerialName("short_desc")
    val shortDesc: String? = null,
    @SerialName("is_draft")
    val isDraft: Int,
    @SerialName("is_archive")
    val isArchive: Int,
    @SerialName("music_group_id")
    val musicGroupId: Int? = null,
    @SerialName("video")
    val video: String? = null,
)
