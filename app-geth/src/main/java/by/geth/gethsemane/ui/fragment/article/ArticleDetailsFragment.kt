package by.geth.gethsemane.ui.fragment.article

import android.os.Bundle
import by.geth.gethsemane.api.GetArticleDetailsRequest
import by.geth.gethsemane.api.Server
import by.geth.gethsemane.data.model.articles.ArticleDetails
import by.geth.gethsemane.ui.fragment.base.ArticleFragment
import com.activeandroid.query.Select
import java.util.*

class ArticleDetailsFragment: ArticleFragment() {
    companion object {
        const val BASE_URL_WORSHIP_NOTES = "http://geth.by/worship-notes/"
        const val BASE_URL_TO_SEE_CHRIST = "http://geth.by/to-see-christ/"

        private const val ARGS_ID = "ARGS_ID"
        private const val ARGS_BASE_URL = "ARGS_BASE_URL"

        fun newInstance(id: Long, baseUrl: String): ArticleDetailsFragment {
            val fragment = ArticleDetailsFragment()
            fragment.arguments = Bundle().apply {
                putLong(ARGS_ID, id)
                putString(ARGS_BASE_URL, baseUrl)
            }
            return fragment
        }
    }

    private var pageId: Long = 0L
    private var articleDetails: ArticleDetails? = null

    override lateinit var baseUrl: String
    override val id: Long
        get() = pageId
    override val category: String?
        get() = articleDetails?.category
    override val previewImageUrl: String?
        get() = articleDetails?.previewUrl
    override val imageUrl: String?
        get() = articleDetails?.imageUrl
    override val title: String?
        get() = articleDetails?.title
    override val html: String?
        get() = articleDetails?.html
    override val author: String?
        get() = articleDetails?.author
    override val date: Date?
        get() = articleDetails?.date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageId = arguments?.getLong(ARGS_ID) ?: 0
        baseUrl = arguments?.getString(ARGS_BASE_URL) ?: "http://geth.by/"
        articleDetails = Select().from(ArticleDetails::class.java)
                .where("${ArticleDetails.COLUMN_PAGE_ID} = $pageId").executeSingle()
    }

    override fun isUpdating() = Server.isRunning(GetArticleDetailsRequest(pageId))

    override fun loadData() {
        Server.getArticleDetails(pageId)
    }

    override fun getRequest() = GetArticleDetailsRequest(pageId)

    override fun applyData(result: Any?) {
        articleDetails = result as ArticleDetails?
    }
}