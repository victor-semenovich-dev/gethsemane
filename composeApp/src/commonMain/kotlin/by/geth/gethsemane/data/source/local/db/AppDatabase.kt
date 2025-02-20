package by.geth.gethsemane.data.source.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import by.geth.gethsemane.data.source.local.db.model.EventEntity
import by.geth.gethsemane.data.source.local.db.model.MusicGroupEntity

@Database(entities = [
    EventEntity::class,
    MusicGroupEntity::class,
], version = 1)
//@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase: RoomDatabase() {
}

//// The Room compiler generates the `actual` implementations.
//@Suppress("NO_ACTUAL_FOR_EXPECT")
//expect object AppDatabaseConstructor: RoomDatabaseConstructor<AppDatabase> {
//    override fun initialize(): AppDatabase
//}
