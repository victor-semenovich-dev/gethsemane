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
    val note: String?,
    @SerialName("audio")
    val audio: String?,
    @SerialName("short_desc")
    val shortDesc: String?,
    @SerialName("is_draft")
    val isDraft: Int,
    @SerialName("is_archive")
    val isArchive: Int,
    @SerialName("music_group_id")
    val musicGroupId: Int?,
    @SerialName("video")
    val video: String?,
)
