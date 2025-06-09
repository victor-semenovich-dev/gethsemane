package by.geth.gethsemane.data.source.local.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import by.geth.gethsemane.data.source.local.db.dao.BirthdaysDao
import by.geth.gethsemane.data.source.local.db.dao.EventsDao
import by.geth.gethsemane.data.source.local.db.dao.MusicGroupsDao
import by.geth.gethsemane.data.source.local.db.dao.PhotoDao
import by.geth.gethsemane.data.source.local.db.dao.SongsDao
import by.geth.gethsemane.data.source.local.db.dao.SpeechDao
import by.geth.gethsemane.data.source.local.db.model.BirthdaysEntity
import by.geth.gethsemane.data.source.local.db.model.EventEntity
import by.geth.gethsemane.data.source.local.db.model.MusicGroupEntity
import by.geth.gethsemane.data.source.local.db.model.PhotoEntity
import by.geth.gethsemane.data.source.local.db.model.SongEntity
import by.geth.gethsemane.data.source.local.db.model.SpeechEntity

@Database(entities = [
    EventEntity::class,
    MusicGroupEntity::class,
    BirthdaysEntity::class,
    SongEntity::class,
    SpeechEntity::class,
    PhotoEntity::class,
], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun eventsDao(): EventsDao
    abstract fun musicGroupsDao(): MusicGroupsDao
    abstract fun birthdaysDao(): BirthdaysDao
    abstract fun songsDao(): SongsDao
    abstract fun speechDao(): SpeechDao
    abstract fun photoDao(): PhotoDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor: RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
