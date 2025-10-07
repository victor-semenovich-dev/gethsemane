package by.geth.gethsemane.data.model.articles

import by.geth.gethsemane.data.base.DataModel
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table

@Table(name = "ArticleListItem", id = "_id")
class ArticleListItem(
    @Column(name = COLUMN_PAGE_ID)
    val pageId: Long = 0,

    @Column(name = COLUMN_TITLE)
    var title: String = "",

    @Column(name = COLUMN_PREVIEW_URL)
    var previewUrl: String? = null,

    @Column(name = COLUMN_CATEGORY)
    var category: String? = null
): DataModel<ArticleListItem>() {
    companion object {
        const val COLUMN_PAGE_ID = "pageId"
        const val COLUMN_TITLE = "title"
        const val COLUMN_PREVIEW_URL = "previewUrl"
        const val COLUMN_CATEGORY = "category"

        const val CATEGORY_WORSHIP_NOTES = "Заметки на служении"
        const val CATEGORY_TO_SEE_CHRIST = "Увидеть Христа"
    }

    override fun externalId() = pageId

    override fun updateWith(other: ArticleListItem) {
        this.title = other.title
        this.previewUrl = other.previewUrl
    }
}