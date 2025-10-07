package by.geth.gethsemane.ui.fragment.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import by.geth.gethsemane.R
import by.geth.gethsemane.api.GetNewsListRequest
import by.geth.gethsemane.api.Server
import by.geth.gethsemane.app.GlideApp
import by.geth.gethsemane.data.model.news.NewsDetails
import by.geth.gethsemane.data.model.news.NewsListItem
import by.geth.gethsemane.ui.fragment.base.PagesFragment
import by.geth.gethsemane.util.ConnectionUtils
import com.activeandroid.query.Select

class NewsListFragment: PagesFragment<NewsListItem>() {
    companion object {
        fun newInstance(): NewsListFragment {
            val fragment = NewsListFragment()
            fragment.arguments = Bundle().apply {
                putInt(ARGS_AB_TITLE_RES_ID, R.string.news)
                putInt(ARGS_UI_FLAGS, UI_FLAG_DISPLAY_HOME_AS_UP)
            }
            return fragment
        }
    }

    override lateinit var adapter: Adapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = Adapter(::onNewsClick)
    }

    override fun getData(): List<NewsListItem> = Select().from(NewsListItem::class.java).execute()

    override fun loadData(page: Int) {
        Server.getNewsList(page)
    }

    override fun isUpdating() = Server.isRunning(GetNewsListRequest())

    override fun applyData(data: List<NewsListItem>) {
        adapter.submitList(data)
    }

    override fun getItemCount() = adapter.itemCount

    override fun getRequest() = GetNewsListRequest()

    private fun onNewsClick(news: NewsListItem) {
        if (ConnectionUtils.isNetworkConnected(context) ||
                Select().from(NewsDetails::class.java)
                        .where("${NewsDetails.COLUMN_EXTERNAL_ID} = ${news.externalId}")
                        .exists()) {
            showFragment(NewsDetailsFragment.newInstance(news.externalId), false)
        } else {
            Toast.makeText(context, R.string.error_no_internet, Toast.LENGTH_SHORT).show()
        }
    }

    class Adapter(private val onNewsClick: (NewsListItem) -> Unit):
            ListAdapter<NewsListItem, Adapter.ViewHolder>(DiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val itemView = inflater.inflate(R.layout.item_list_article, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = getItem(position)
            GlideApp.with(holder.imageView).load(item.previewUrl).into(holder.imageView)
            holder.titleView.text = item.title
            holder.rootView.setOnClickListener { onNewsClick(item) }
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val rootView: View = itemView.findViewById(R.id.rootView)
            val imageView: ImageView = itemView.findViewById(R.id.imageView)
            val titleView: TextView = itemView.findViewById(R.id.titleView)
        }

        class DiffCallback: DiffUtil.ItemCallback<NewsListItem>() {
            override fun areItemsTheSame(oldItem: NewsListItem, newItem: NewsListItem): Boolean {
                return oldItem.externalId == newItem.externalId
            }

            override fun areContentsTheSame(oldItem: NewsListItem, newItem: NewsListItem): Boolean {
                return oldItem.title == newItem.title && oldItem.previewUrl == newItem.previewUrl
            }
        }
    }
}