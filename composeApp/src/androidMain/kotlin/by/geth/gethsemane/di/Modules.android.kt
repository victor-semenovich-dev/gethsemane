package by.geth.gethsemane.di

import by.geth.gethsemane.data.source.local.db.AppDatabase
import by.geth.gethsemane.data.source.local.db.getDatabase
import org.koin.dsl.module

actual val platformModule = module {
    single<AppDatabase> { getDatabase(get()) }
}
