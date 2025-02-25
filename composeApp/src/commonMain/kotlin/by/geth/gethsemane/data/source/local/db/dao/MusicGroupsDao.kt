package by.geth.gethsemane.data.source.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import by.geth.gethsemane.data.source.local.db.model.MusicGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicGroupsDao {
    @Upsert
    suspend fun insertOrUpdate(vararg musicGroups: MusicGroupEntity)

    @Query("SELECT * FROM ${MusicGroupEntity.TABLE_NAME}")
    fun getAll(): Flow<List<MusicGroupEntity>>

    @Query("DELETE FROM ${MusicGroupEntity.TABLE_NAME}")
    suspend fun clear()
}
