package by.geth.gethsemane.data.source.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import by.geth.gethsemane.data.source.local.db.model.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventsDao {
    @Upsert
    suspend fun insertOrUpdate(vararg events: EventEntity)

    @Query("SELECT * FROM ${EventEntity.TABLE_NAME}")
    fun getAll(): Flow<List<EventEntity>>

    @Query("SELECT * FROM ${EventEntity.TABLE_NAME} WHERE ${EventEntity.COLUMN_DATE} >= :from")
    fun getFromDate(from: String): Flow<List<EventEntity>>

    @Query("DELETE FROM ${EventEntity.TABLE_NAME}")
    suspend fun clear()

    @Query("DELETE FROM ${EventEntity.TABLE_NAME} WHERE ${EventEntity.COLUMN_DATE} >= :from")
    suspend fun clearFromDate(from: String)

    @Transaction
    suspend fun replaceFromDate(from: String, events: List<EventEntity>) {
        clearFromDate(from)
        insertOrUpdate(*events.toTypedArray())
    }
}
