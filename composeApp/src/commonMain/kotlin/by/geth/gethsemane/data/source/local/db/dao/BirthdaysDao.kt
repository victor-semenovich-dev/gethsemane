package by.geth.gethsemane.data.source.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import by.geth.gethsemane.data.source.local.db.model.BirthdaysEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BirthdaysDao {
    @Upsert
    suspend fun insertOrUpdate(vararg birthdays: BirthdaysEntity)

    @Query("SELECT * FROM ${BirthdaysEntity.TABLE_NAME}")
    fun getAll(): Flow<List<BirthdaysEntity>>

    @Query("DELETE FROM ${BirthdaysEntity.TABLE_NAME}")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(birthdays: List<BirthdaysEntity>) {
        clear()
        insertOrUpdate(*birthdays.toTypedArray())
    }
}
