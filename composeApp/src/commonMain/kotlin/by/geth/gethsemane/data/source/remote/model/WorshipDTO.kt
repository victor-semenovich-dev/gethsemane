package by.geth.gethsemane.data.source.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorshipDTO(
    @SerialName("id")
    val id: Int,
    @SerialName("date")
    val date: String,
    @SerialName("title")
    val title: String,
    @SerialName("short_desc")
    val shortDesc: String?,
    @SerialName("audio")
    val audio: String?,
    @SerialName("video")
    val video: String?,
    @SerialName("poster")
    val poster: String?,
    @SerialName("songs")
    val songs: List<SongDTO>,
    @SerialName("sermons")
    val sermons: List<SpeechDTO>,
    @SerialName("witnesses")
    val witnesses: List<SpeechDTO>,
    @SerialName("photos")
    val photos: List<PhotoDTO>,
) {
    @Serializable
    data class SongDTO(
        @SerialName("id")
        val id: Int,
        @SerialName("title")
        val title: String,
        @SerialName("music_group_id")
        val musicGroupId: Int,
        @SerialName("audio")
        val audio: String,
    )

    @Serializable
    data class SpeechDTO(
        @SerialName("id")
        val id: Int,
        @SerialName("title")
        val title: String,
        @SerialName("author_id")
        val authorId: Int,
        @SerialName("audio")
        val audio: String,
    )

    @Serializable
    data class PhotoDTO(
        @SerialName("id")
        val id: Int,
        @SerialName("title")
        val title: String,
        @SerialName("preview")
        val preview: String,
        @SerialName("photo")
        val photo: String,
        @SerialName("date")
        val date: String,
    )
}
