package by.geth.gethsemane.data.model.news

import by.geth.gethsemane.data.base.DataModel
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table

@Table(name = "NewsListItem", id = "_id")
class NewsListItem(
    @Column(name = COLUMN_EXTERNAL_ID)
    val externalId: Long = 0,

    @Column(name = COLUMN_TITLE)
    var title: String = "",

    @Column(name = COLUMN_PREVIEW_URL)
    var previewUrl: String? = null
): DataModel<NewsListItem>() {
    companion object {
        const val COLUMN_EXTERNAL_ID = "externalId"
        const val COLUMN_TITLE = "title"
        const val COLUMN_PREVIEW_URL = "previewUrl"
    }

    override fun externalId() = externalId

    override fun updateWith(other: NewsListItem) {
        this.title = other.title
        this.previewUrl = other.previewUrl
    }
}