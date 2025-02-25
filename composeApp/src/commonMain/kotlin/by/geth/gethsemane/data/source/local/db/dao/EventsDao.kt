package by.geth.gethsemane.data.source.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import by.geth.gethsemane.data.source.local.db.model.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventsDao {
    @Upsert
    suspend fun insertOrUpdate(vararg events: EventEntity)

    @Query("SELECT * FROM ${EventEntity.TABLE_NAME}")
    fun getAll(): Flow<List<EventEntity>>

    @Query("DELETE FROM ${EventEntity.TABLE_NAME}")
    suspend fun clear()
}
