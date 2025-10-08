package by.geth.gethsemane.ui.fragment.base

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
import by.geth.gethsemane.app.GlideApp
import by.geth.gethsemane.data.model.articles.ArticleDetails
import by.geth.gethsemane.data.model.articles.ArticleListItem
import by.geth.gethsemane.util.ConnectionUtils
import com.activeandroid.query.Select

abstract class ArticlesListFragment: PagesFragment<ArticleListItem>() {

    override lateinit var adapter: Adapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = Adapter(::onItemClick)
    }

    override fun applyData(data: List<ArticleListItem>) {
        adapter.submitList(data)
    }

    override fun getItemCount() = adapter.itemCount

    private fun onItemClick(item: ArticleListItem) {
        if (ConnectionUtils.isNetworkConnected(context) ||
            Select().from(ArticleDetails::class.java)
                .where("${ArticleDetails.COLUMN_PAGE_ID} = ${item.pageId}")
                .exists()) {
            showFragment(getDetailsFragment(item), false)
        } else {
            Toast.makeText(context, R.string.error_no_internet, Toast.LENGTH_SHORT).show()
        }
    }

    abstract fun getDetailsFragment(item: ArticleListItem): ArticleFragment

    class Adapter(private val onItemClick: (ArticleListItem) -> Unit):
        ListAdapter<ArticleListItem, Adapter.ViewHolder>(DiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val itemView = inflater.inflate(R.layout.item_list_article, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = getItem(position)
            GlideApp.with(holder.imageView).load(item.previewUrl).into(holder.imageView)
            holder.titleView.text = item.title
            holder.rootView.setOnClickListener { onItemClick(item) }
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val rootView: View = itemView.findViewById(R.id.rootView)
            val imageView: ImageView = itemView.findViewById(R.id.imageView)
            val titleView: TextView = itemView.findViewById(R.id.titleView)
        }

        class DiffCallback: DiffUtil.ItemCallback<ArticleListItem>() {
            override fun areItemsTheSame(oldItem: ArticleListItem, newItem: ArticleListItem): Boolean {
                return oldItem.pageId == newItem.pageId
            }

            override fun areContentsTheSame(oldItem: ArticleListItem, newItem: ArticleListItem): Boolean {
                return oldItem.title == newItem.title && oldItem.previewUrl == newItem.previewUrl
            }
        }
    }
}