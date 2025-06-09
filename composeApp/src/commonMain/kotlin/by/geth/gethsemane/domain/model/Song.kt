package by.geth.gethsemane.domain.model

data class Song(
    val id: Int,
    val title: String,
    val musicGroup: MusicGroup,
    val audioRemote: String,
    val audioLocal: String?,
)
