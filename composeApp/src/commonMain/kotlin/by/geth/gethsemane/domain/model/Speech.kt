package by.geth.gethsemane.domain.model

data class Speech(
    val id: Int,
    val title: String,
    val author: Author,
    val audioRemote: String,
    val audioLocal: String?,
)
