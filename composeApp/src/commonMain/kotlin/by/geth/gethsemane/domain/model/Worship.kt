package by.geth.gethsemane.domain.model

import kotlinx.datetime.LocalDateTime

data class Worship(
    val id: Int,
    val dateTime: LocalDateTime,
    val title: String,
    val shortDesc: String,
    val video: String?,
    val poster: String?,
    val songList: List<Song>,
    val sermonList: List<Speech>,
    val witnessList: List<Speech>,
    val photoList: List<Photo>,
) {
    data class Speech(
        val id: Int,
        val title: String,
        val author: Author?,
        val audioRemote: String,
        val audioLocal: String?,
    )

    data class Song(
        val id: Int,
        val title: String,
        val musicGroup: MusicGroup?,
        val audioRemote: String,
        val audioLocal: String?,
    )

    data class Photo(
        val id: Int,
        val title: String,
        val preview: String,
        val photo: String,
        val date: String,
    )
}
