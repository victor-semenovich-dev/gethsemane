package by.geth.gethsemane.api.response

import by.geth.gethsemane.data.model.news.NewsDetails
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

class NewsDetailsResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("title")
    val title: String,

    @SerializedName("html")
    val html: String,

    @SerializedName("imageUrl")
    val imageUrl: String?,

    @SerializedName("largeImageUrl")
    val previewUrl: String?,

    @SerializedName("date")
    val date: String,

    @SerializedName("author")
    val author: String,

    @SerializedName("category")
    val category: String
) {
    val dbEntity: NewsDetails
        get() {
            val parsedDate = SimpleDateFormat("yyyyMMdd", Locale.US).parse(date)
            return NewsDetails(id, title, html, previewUrl, imageUrl, parsedDate, author, category)
        }
}