package by.geth.gethsemane.ui.fragment.sermons

import by.geth.gethsemane.api.response.SermonResponse
import by.geth.gethsemane.data.Witness
import com.activeandroid.query.Delete
import com.activeandroid.query.Select
import com.activeandroid.query.Update
import java.text.SimpleDateFormat
import java.util.*

class WitnessListViewModel: BaseSermonListViewModel<Witness>() {
    override val sermonCategory = 25

    override suspend fun loadFromDatabase() {
        val showInList = if (_query.isEmpty()) Witness.SHOW_IN_LIST else Witness.SHOW_IN_LIST_WITH_QUERY
        val witnesses: List<Witness> = Select()
            .from(Witness::class.java)
            .where("${Witness.COLUMN_SHOW_IN_LIST} = $showInList")
            .orderBy("${Witness.COLUMN_DATE} DESC, ${Witness.COLUMN_TITLE} ASC")
            .execute()
        _itemList.postValue(witnesses)
    }

    override suspend fun clearDatabase() {
        if (_query.isEmpty()) {
            Update(Witness::class.java)
                .set("${Witness.COLUMN_SHOW_IN_LIST} = ${Witness.DONT_SHOW_IN_LIST}")
                .execute()
        } else {
            Update(Witness::class.java)
                .set("${Witness.COLUMN_SHOW_IN_LIST} = ${Witness.DONT_SHOW_IN_LIST}")
                .where("${Witness.COLUMN_SHOW_IN_LIST} = ${Witness.SHOW_IN_LIST_WITH_QUERY}")
                .execute()
        }
        Delete().from(Witness::class.java).where(
            "${Witness.COLUMN_WORSHIP_ID} = 0 " +
                    "AND ${Witness.COLUMN_AUDIO_LOCAL} IS NULL " +
                    "AND ${Witness.COLUMN_SHOW_IN_LIST} = ${Witness.DONT_SHOW_IN_LIST}"
        ).execute<Witness>()
    }

    override suspend fun applyResponse(response: SermonResponse) {
        val witness: Witness? = Select().from(Witness::class.java)
            .where("${Witness.COLUMN_ID} = ${response.id}").executeSingle()
        (witness ?: Witness()).apply {
            this.externalId = response.id.toLong()
            this.setTitle(response.title)
            this.setAuthor(response.author)
            this.setDate(SimpleDateFormat("yyyyMMdd", Locale.US).parse(response.date))
            this.audioUri = response.audio
            this.showInList = if (_query.isEmpty()) Witness.SHOW_IN_LIST else Witness.SHOW_IN_LIST_WITH_QUERY

            save()
        }
    }
}
