package by.geth.gethsemane.data.model.news

import by.geth.gethsemane.data.base.DataModel
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table
import java.util.*

@Table(name = "NewsDetails", id = "_id")
class NewsDetails(
    @Column(name = COLUMN_EXTERNAL_ID)
    val externalId: Long = 0,

    @Column(name = COLUMN_TITLE)
    var title: String = "",

    @Column(name = COLUMN_HTML)
    var html: String = "",

    @Column(name = COLUMN_PREVIEW_URL)
    var previewUrl: String? = null,

    @Column(name = COLUMN_IMAGE_URL)
    var imageUrl: String? = null,

    @Column(name = COLUMN_DATE)
    var date: Date = Date(),

    @Column(name = COLUMN_AUTHOR)
    var author: String = "",

    @Column(name = COLUMN_CATEGORY)
    var category: String = ""
): DataModel<NewsDetails>() {
    companion object {
        const val COLUMN_EXTERNAL_ID = "externalId"
        const val COLUMN_TITLE = "title"
        const val COLUMN_HTML = "html"
        const val COLUMN_PREVIEW_URL = "previewUrl"
        const val COLUMN_IMAGE_URL = "imageUrl"
        const val COLUMN_DATE = "date"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_CATEGORY = "category"
    }

    override fun externalId() = externalId

    override fun updateWith(other: NewsDetails) {
        this.title = other.title
        this.html = other.html
        this.previewUrl = other.previewUrl
        this.imageUrl = other.imageUrl
        this.date = other.date
        this.author = other.author
        this.category = other.category
    }
}