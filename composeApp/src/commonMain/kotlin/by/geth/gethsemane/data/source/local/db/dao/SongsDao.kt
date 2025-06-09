package by.geth.gethsemane.data.source.local.db.dao

import androidx.room.Dao
import androidx.room.Query
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
}
