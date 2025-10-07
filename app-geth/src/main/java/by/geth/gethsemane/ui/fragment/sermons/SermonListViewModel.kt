package by.geth.gethsemane.ui.fragment.sermons

import by.geth.gethsemane.api.response.SermonResponse
import by.geth.gethsemane.data.Sermon
import com.activeandroid.query.Delete
import com.activeandroid.query.Select
import com.activeandroid.query.Update
import java.text.SimpleDateFormat
import java.util.*

class SermonListViewModel: BaseSermonListViewModel<Sermon>() {
    override val sermonCategory = 24

    override suspend fun loadFromDatabase() {
        val showInList = if (_query.isEmpty()) Sermon.SHOW_IN_LIST else Sermon.SHOW_IN_LIST_WITH_QUERY
        val sermons: List<Sermon> = Select()
            .from(Sermon::class.java)
            .where("${Sermon.COLUMN_SHOW_IN_LIST} = $showInList")
            .orderBy("${Sermon.COLUMN_DATE} DESC, ${Sermon.COLUMN_TITLE} ASC")
            .execute()
        _itemList.postValue(sermons)
    }

    override suspend fun clearDatabase() {
        if (_query.isEmpty()) {
            Update(Sermon::class.java)
                .set("${Sermon.COLUMN_SHOW_IN_LIST} = ${Sermon.DONT_SHOW_IN_LIST}")
                .execute()
        } else {
            Update(Sermon::class.java)
                .set("${Sermon.COLUMN_SHOW_IN_LIST} = ${Sermon.DONT_SHOW_IN_LIST}")
                .where("${Sermon.COLUMN_SHOW_IN_LIST} = ${Sermon.SHOW_IN_LIST_WITH_QUERY}")
                .execute()
        }
        Delete().from(Sermon::class.java).where(
            "${Sermon.COLUMN_WORSHIP_ID} = 0 " +
                    "AND ${Sermon.COLUMN_AUDIO_LOCAL} IS NULL " +
                    "AND ${Sermon.COLUMN_SHOW_IN_LIST} = ${Sermon.DONT_SHOW_IN_LIST}"
        ).execute<Sermon>()
    }

    override suspend fun applyResponse(response: SermonResponse) {
        val sermon: Sermon? = Select().from(Sermon::class.java)
            .where("${Sermon.COLUMN_ID} = ${response.id}").executeSingle()
        (sermon ?: Sermon()).apply {
            this.externalId = response.id.toLong()
            this.setTitle(response.title)
            this.setAuthor(response.author)
            this.setDate(SimpleDateFormat("yyyyMMdd", Locale.US).parse(response.date))
            this.audioUri = response.audio
            this.showInList = if (_query.isEmpty()) Sermon.SHOW_IN_LIST else Sermon.SHOW_IN_LIST_WITH_QUERY

            save()
        }
    }
}
