package by.geth.gethsemane.data.source.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import by.geth.gethsemane.data.source.local.db.model.SpeechEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SpeechDao {
    @Query("SELECT * FROM ${SpeechEntity.TABLE_NAME} WHERE ${SpeechEntity.COLUMN_ID} == :id")
    fun getById(id: Int): Flow<SpeechEntity?>

    @Upsert
    suspend fun insertOrUpdate(vararg speeches: SpeechEntity)

    @Transaction
    suspend fun replaceForEvent(eventId: Int, speeches: List<SpeechEntity>) {
        deleteNotMediaSpeechesForEvent(eventId)
        unbindFromEvent(eventId)
        insertOrUpdate(*speeches.toTypedArray())
    }

    @Query("DELETE FROM ${SpeechEntity.TABLE_NAME} WHERE " +
            "${SpeechEntity.COLUMN_EVENT_ID} == :eventId AND " +
            "${SpeechEntity.COLUMN_SHOW_IN_MEDIA} == false")
    suspend fun deleteNotMediaSpeechesForEvent(eventId: Int)

    @Query("UPDATE ${SpeechEntity.TABLE_NAME} " +
            "SET ${SpeechEntity.COLUMN_EVENT_ID} = NULL " +
            "WHERE ${SpeechEntity.COLUMN_EVENT_ID} == :eventId")
    suspend fun unbindFromEvent(eventId: Int)
}
