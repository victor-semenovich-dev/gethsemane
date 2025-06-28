package by.geth.gethsemane.di.module

import by.geth.gethsemane.data.source.local.db.AppDatabase
import by.geth.gethsemane.data.source.local.db.dao.AuthorsDao
import by.geth.gethsemane.data.source.local.db.dao.BirthdaysDao
import by.geth.gethsemane.data.source.local.db.dao.EventsDao
import by.geth.gethsemane.data.source.local.db.dao.MusicGroupsDao
import by.geth.gethsemane.data.source.local.db.dao.PhotosDao
import by.geth.gethsemane.data.source.local.db.dao.SongsDao
import by.geth.gethsemane.data.source.local.db.dao.SpeechDao
import org.koin.dsl.module

val daoModule = module {
    single<EventsDao> {
        val appDatabase: AppDatabase = get()
        appDatabase.eventsDao()
    }
    single<MusicGroupsDao> {
        val appDatabase: AppDatabase = get()
        appDatabase.musicGroupsDao()
    }
    single<BirthdaysDao> {
        val appDatabase: AppDatabase = get()
        appDatabase.birthdaysDao()
    }
    single<AuthorsDao> {
        val appDatabase: AppDatabase = get()
        appDatabase.authorsDao()
    }
    single<SpeechDao> {
        val appDatabase: AppDatabase = get()
        appDatabase.speechDao()
    }
    single<SongsDao> {
        val appDatabase: AppDatabase = get()
        appDatabase.songsDao()
    }
    single<PhotosDao> {
        val appDatabase: AppDatabase = get()
        appDatabase.photosDao()
    }
}