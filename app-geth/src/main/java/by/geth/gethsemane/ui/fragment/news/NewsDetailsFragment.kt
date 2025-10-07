package by.geth.gethsemane.ui.fragment.news

import android.os.Bundle
import by.geth.gethsemane.api.GetNewsRequest
import by.geth.gethsemane.api.Server
import by.geth.gethsemane.data.model.news.NewsDetails
import by.geth.gethsemane.ui.fragment.base.ArticleFragment
import com.activeandroid.query.Select
import java.util.*

class NewsDetailsFragment: ArticleFragment() {
    companion object {
        private const val ARGS_ID = "ARGS_ID"

        fun newInstance(id: Long): NewsDetailsFragment {
            val fragment = NewsDetailsFragment()
            fragment.arguments = Bundle().apply {
                putLong(ARGS_ID, id)
            }
            return fragment
        }
    }

    private var newsId: Long = 0L
    private var news: NewsDetails? = null

    override val baseUrl = "http://geth.by/news/"
    override val id: Long
        get() = newsId
    override val category: String?
        get() = news?.category
    override val previewImageUrl: String?
        get() = news?.previewUrl
    override val imageUrl: String?
        get() = news?.imageUrl
    override val title: String?
        get() = news?.title
    override val html: String?
        get() = news?.html
    override val author: String?
        get() = news?.author
    override val date: Date?
        get() = news?.date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newsId = arguments?.getLong(ARGS_ID) ?: 0
        news = Select().from(NewsDetails::class.java)
                .where("${NewsDetails.COLUMN_EXTERNAL_ID} = $newsId").executeSingle()
    }

    override fun isUpdating() = Server.isRunning(GetNewsRequest(newsId))

    override fun loadData() {
        Server.getNewsDetails(newsId)
    }

    override fun getRequest() = GetNewsRequest(newsId)

    override fun applyData(result: Any?) {
        news = result as NewsDetails?
    }
}