package by.geth.gethsemane.api.response

import by.geth.gethsemane.data.model.news.NewsListItem
import com.google.gson.annotations.SerializedName

class NewsListItemResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("title")
    val title: String,

    @SerializedName("image")
    val previewUrl: String?
) {
    val dbEntity: NewsListItem
        get() {
            return NewsListItem(id, title, previewUrl)
        }
}