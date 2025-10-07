package by.geth.gethsemane.data.base

import by.geth.gethsemane.data.model.articles.ArticleListItem
import by.geth.gethsemane.data.model.news.NewsListItem
import com.activeandroid.ActiveAndroid
import com.activeandroid.Model
import com.activeandroid.query.Delete
import com.activeandroid.query.Select

abstract class DataModel<T: DataModel<T>>: Model() {
    abstract fun externalId(): Long
    abstract fun updateWith(other: T)
    open fun fullDelete() { delete() }
}

private fun <T: DataModel<T>> getItemById(clazz: Class<T>, id: Long): T? {
    return when (clazz) {
        NewsListItem::class.java -> Select().from(NewsListItem::class.java)
                .where("${NewsListItem.COLUMN_EXTERNAL_ID} = $id").executeSingle()
        ArticleListItem::class.java -> Select().from(ArticleListItem::class.java)
                .where("${ArticleListItem.COLUMN_PAGE_ID} = $id").executeSingle()
        else -> null
    }
}

private fun <T: DataModel<T>> clear(clazz: Class<T>) {
    when (clazz) {
        NewsListItem::class.java -> Delete().from(NewsListItem::class.java).execute<NewsListItem>()
        ArticleListItem::class.java -> Delete().from(ArticleListItem::class.java).execute<ArticleListItem>()
    }
}

fun <T: DataModel<T>> merge(clazz: Class<T>, newItemList: List<T>, page: Int, oldItemList: List<T>? = null) {
    if (newItemList.isNotEmpty()) {
        ActiveAndroid.beginTransaction()

        if (page == 1) {
            oldItemList?.forEach { it.delete() } ?: clear(clazz)
            newItemList.forEach { it.save() }
        } else {
            newItemList.forEach {
                val oldItem = getItemById(clazz, it.externalId())
                if (oldItem == null) {
                    it.save()
                } else {
                    oldItem.updateWith(it)
                    oldItem.save()
                }
            }
        }

        ActiveAndroid.setTransactionSuccessful()
        ActiveAndroid.endTransaction()
    }
}

fun <T: DataModel<T>> merge(clazz: Class<T>, newItemList: List<T>) {
    val currentItemList = Select().from(clazz).execute<T>()

    ActiveAndroid.beginTransaction()

    for (currentItem in currentItemList) {
        var isRemoved = true
        for (newItem in newItemList) {
            if (currentItem.externalId() == newItem.externalId()) {
                isRemoved = false
                break
            }
        }
        if (isRemoved) {
            currentItem.fullDelete()
        }
    }

    for (newItem in newItemList) {
        var isAdded = true
        for (currentItem in currentItemList) {
            if (newItem.externalId() == currentItem.externalId()) {
                isAdded = false
                currentItem.updateWith(newItem)
                currentItem.save()
                break
            }
        }
        if (isAdded) {
            newItem.save()
        }
    }

    ActiveAndroid.setTransactionSuccessful()
    ActiveAndroid.endTransaction()
}