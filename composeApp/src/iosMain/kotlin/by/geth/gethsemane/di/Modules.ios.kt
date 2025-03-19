package by.geth.gethsemane.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import by.geth.gethsemane.data.source.local.datastore.createDataStore
import by.geth.gethsemane.data.source.local.db.AppDatabase
import by.geth.gethsemane.data.source.local.db.getDatabase
import org.koin.dsl.module

actual val platformModule = module {
    single<AppDatabase> { getDatabase() }
    single<DataStore<Preferences>> { createDataStore() }
}
