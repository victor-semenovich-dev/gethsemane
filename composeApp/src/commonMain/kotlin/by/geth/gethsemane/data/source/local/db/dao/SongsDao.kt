package by.geth.gethsemane.data.source.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import by.geth.gethsemane.data.source.local.db.model.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SongsDao {
    @Upsert
    suspend fun insertOrUpdate(vararg songs: SongEntity)

    @Query("SELECT * FROM ${SongEntity.TABLE_NAME} WHERE ${SongEntity.COLUMN_ID} = :id")
    fun getById(id: Int): Flow<SongEntity?>

    @Query("SELECT * FROM ${SongEntity.TABLE_NAME} WHERE ${SongEntity.COLUMN_EVENT_ID} = :worshipId")
    fun getByWorshipId(worshipId: Int): Flow<List<SongEntity>>

    @Transaction
    suspend fun replaceForEvent(eventId: Int, songs: List<SongEntity>) {
        deleteNotMediaSongsForEvent(eventId)
        unbindFromEvent(eventId)
        insertOrUpdate(*songs.toTypedArray())
    }

    @Query("DELETE FROM ${SongEntity.TABLE_NAME} WHERE " +
            "${SongEntity.COLUMN_EVENT_ID} == :eventId AND " +
            "${SongEntity.COLUMN_SHOW_IN_MEDIA} == false")
    suspend fun deleteNotMediaSongsForEvent(eventId: Int)

    @Query("UPDATE ${SongEntity.TABLE_NAME} " +
            "SET ${SongEntity.COLUMN_EVENT_ID} = NULL " +
            "WHERE ${SongEntity.COLUMN_EVENT_ID} == :eventId")
    suspend fun unbindFromEvent(eventId: Int)
}
