package by.geth.gethsemane.ui.fragment.article

import android.os.Bundle
import by.geth.gethsemane.R
import by.geth.gethsemane.api.BaseRequest
import by.geth.gethsemane.api.GetToSeeChristListRequest
import by.geth.gethsemane.api.GetWorshipNotesListRequest
import by.geth.gethsemane.api.Server
import by.geth.gethsemane.data.model.articles.ArticleListItem
import by.geth.gethsemane.ui.fragment.base.ArticleFragment
import by.geth.gethsemane.ui.fragment.base.ArticlesListFragment
import com.activeandroid.query.Select

class ToSeeChristListFragment: ArticlesListFragment() {
    companion object {
        fun newInstance(): ToSeeChristListFragment {
            val fragment = ToSeeChristListFragment()
            fragment.arguments = Bundle().apply {
                putInt(ARGS_AB_TITLE_RES_ID, R.string.to_see_christ)
                putInt(ARGS_UI_FLAGS, UI_FLAG_DISPLAY_HOME_AS_UP)
            }
            return fragment
        }
    }

    override fun getDetailsFragment(item: ArticleListItem): ArticleFragment {
        return ArticleDetailsFragment.newInstance(item.pageId, ArticleDetailsFragment.BASE_URL_TO_SEE_CHRIST)
    }

    override fun getData(): List<ArticleListItem> = Select().from(ArticleListItem::class.java)
        .where("${ArticleListItem.COLUMN_CATEGORY} = '${ArticleListItem.CATEGORY_TO_SEE_CHRIST}'")
        .execute()

    override fun loadData(page: Int) {
        Server.getToSeeChristList(page)
    }

    override fun isUpdating(): Boolean {
        return Server.isRunning(GetWorshipNotesListRequest())
    }

    override fun getRequest(): BaseRequest {
        return GetToSeeChristListRequest()
    }

}