package by.geth.gethsemane.ui.fragment.article

import android.os.Bundle
import by.geth.gethsemane.R
import by.geth.gethsemane.api.GetWorshipNotesListRequest
import by.geth.gethsemane.api.Server
import by.geth.gethsemane.data.model.articles.ArticleListItem
import by.geth.gethsemane.ui.fragment.base.ArticleFragment
import by.geth.gethsemane.ui.fragment.base.ArticlesListFragment
import com.activeandroid.query.Select

class WorshipNotesListFragment: ArticlesListFragment() {
    companion object {
        fun newInstance(): WorshipNotesListFragment {
            val fragment = WorshipNotesListFragment()
            fragment.arguments = Bundle().apply {
                putInt(ARGS_AB_TITLE_RES_ID, R.string.worship_notes)
                putInt(ARGS_UI_FLAGS, UI_FLAG_DISPLAY_HOME_AS_UP)
            }
            return fragment
        }
    }

    override fun getData(): List<ArticleListItem> = Select().from(ArticleListItem::class.java)
        .where("${ArticleListItem.COLUMN_CATEGORY} = '${ArticleListItem.CATEGORY_WORSHIP_NOTES}'")
        .execute()

    override fun loadData(page: Int) {
        Server.getWorshipNotesList(page)
    }

    override fun isUpdating() = Server.isRunning(GetWorshipNotesListRequest())

    override fun getRequest() = GetWorshipNotesListRequest()

    override fun getDetailsFragment(item: ArticleListItem): ArticleFragment {
        return ArticleDetailsFragment.newInstance(item.pageId, ArticleDetailsFragment.BASE_URL_WORSHIP_NOTES)
    }
}