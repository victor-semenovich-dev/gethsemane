package by.geth.gethsemane.di.module

import by.geth.gethsemane.data.source.local.db.AppDatabase
import by.geth.gethsemane.data.source.local.db.dao.AuthorsDao
import by.geth.gethsemane.data.source.local.db.dao.BirthdaysDao
import by.geth.gethsemane.data.source.local.db.dao.EventsDao
import by.geth.gethsemane.data.source.local.db.dao.MusicGroupsDao
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
}