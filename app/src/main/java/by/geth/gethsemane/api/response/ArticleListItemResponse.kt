package by.geth.gethsemane.api.response

import by.geth.gethsemane.data.model.articles.ArticleListItem
import com.google.gson.annotations.SerializedName

class ArticleListItemResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("title")
    val title: String,

    @SerializedName("image")
    val previewUrl: String?
) {
    val dbEntity: ArticleListItem
        get() {
            return ArticleListItem(id, title, previewUrl)
        }
}