package by.geth.gethsemane.data.source.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import by.geth.gethsemane.data.source.local.db.model.AuthorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthorsDao {
    @Query("SELECT * FROM ${AuthorEntity.TABLE_NAME}")
    fun getAll(): Flow<List<AuthorEntity>>

    @Query("SELECT * FROM ${AuthorEntity.TABLE_NAME} WHERE ${AuthorEntity.COLUMN_ID} == :id")
    fun getById(id: Int): Flow<AuthorEntity?>

    @Query("DELETE FROM ${AuthorEntity.TABLE_NAME}")
    suspend fun clear()

    @Upsert
    suspend fun insertOrUpdate(vararg authors: AuthorEntity)

    @Transaction
    suspend fun replaceAll(authors: List<AuthorEntity>) {
        clear()
        insertOrUpdate(*authors.toTypedArray())
    }
}