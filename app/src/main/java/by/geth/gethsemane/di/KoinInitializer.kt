package by.geth.gethsemane.di

import org.koin.core.context.startKoin

object KoinInitializer {
    fun init() {
        startKoin {
            modules(repositoriesModule)
        }
    }
}
