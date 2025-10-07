package by.geth.gethsemane.data.model.articles

import by.geth.gethsemane.data.base.DataModel
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table
import java.util.*

@Table(name = "ArticleDetails", id = "_id")
class ArticleDetails(
    @Column(name = COLUMN_PAGE_ID)
    val pageId: Long = 0,

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
): DataModel<ArticleDetails>() {
    companion object {
        const val COLUMN_PAGE_ID = "pageId"
        const val COLUMN_TITLE = "title"
        const val COLUMN_HTML = "html"
        const val COLUMN_PREVIEW_URL = "previewUrl"
        const val COLUMN_IMAGE_URL = "imageUrl"
        const val COLUMN_DATE = "date"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_CATEGORY = "category"
    }

    override fun externalId() = pageId

    override fun updateWith(other: ArticleDetails) {
        this.title = other.title
        this.html = other.html
        this.previewUrl = other.previewUrl
        this.imageUrl = other.imageUrl
        this.date = other.date
        this.author = other.author
        this.category = other.category
    }
}