package by.geth.gethsemane.data.base

import java.util.*

interface AudioItem {

    fun getRemoteUrl(): String?
    fun getLocalPath(): String?

    fun getTitle(): String
    fun getAuthor(): String?

    fun getDate(): Date

}