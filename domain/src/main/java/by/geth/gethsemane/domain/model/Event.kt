package by.geth.gethsemane.domain.model

import java.util.Date

data class Event(
    val id: Int,
    val categoryId: Int,
    val musicGroupId: Int?,
    val title: String,
    val date: Date,
    val note: String?,
    val shortDesc: String?,
    val audio: String?,
    val video: String?,
    val isDraft: Boolean,
    val isArchive: Boolean,
)
