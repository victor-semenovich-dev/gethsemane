package by.geth.gethsemane.data.source.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import by.geth.gethsemane.data.source.local.db.model.PhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotosDao {
    @Query("SELECT * FROM ${PhotoEntity.TABLE_NAME} WHERE ${PhotoEntity.COLUMN_ID} == :id")
    fun getById(id: Int): Flow<PhotoEntity?>

    @Upsert
    suspend fun insertOrUpdate(vararg songs: PhotoEntity)

    @Transaction
    suspend fun replaceForEvent(eventId: Int, songs: List<PhotoEntity>) {
        deleteNotMediaPhotosForEvent(eventId)
        unbindFromEvent(eventId)
        insertOrUpdate(*songs.toTypedArray())
    }

    @Query("DELETE FROM ${PhotoEntity.TABLE_NAME} WHERE " +
            "${PhotoEntity.COLUMN_EVENT_ID} == :eventId AND " +
            "${PhotoEntity.COLUMN_ALBUM_ID} IS NULL AND " +
            "${PhotoEntity.COLUMN_SHOW_IN_LAST_PHOTOS} == false")
    suspend fun deleteNotMediaPhotosForEvent(eventId: Int)

    @Query("UPDATE ${PhotoEntity.TABLE_NAME} " +
            "SET ${PhotoEntity.COLUMN_EVENT_ID} = NULL " +
            "WHERE ${PhotoEntity.COLUMN_EVENT_ID} == :eventId")
    suspend fun unbindFromEvent(eventId: Int)
}
