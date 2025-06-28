package by.geth.gethsemane.data.source.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import by.geth.gethsemane.data.source.local.db.model.MusicGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicGroupsDao {
    @Upsert
    suspend fun insertOrUpdate(vararg musicGroups: MusicGroupEntity)

    @Query("SELECT * FROM ${MusicGroupEntity.TABLE_NAME}")
    fun getAll(): Flow<List<MusicGroupEntity>>

    @Query("SELECT * FROM ${MusicGroupEntity.TABLE_NAME} WHERE ${MusicGroupEntity.COLUMN_ID} == :id")
    fun getById(id: Int): Flow<MusicGroupEntity?>

    @Query("DELETE FROM ${MusicGroupEntity.TABLE_NAME}")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(musicGroups: List<MusicGroupEntity>) {
        clear()
        insertOrUpdate(*musicGroups.toTypedArray())
    }
}
