package by.geth.gethsemane.di

import by.geth.gethsemane.di.app.viewModelsModule
import by.geth.gethsemane.di.data.remote.httpModule
import by.geth.gethsemane.di.data.remote.localSourceModule
import by.geth.gethsemane.di.data.remote.remoteSourceModule
import by.geth.gethsemane.di.domain.repositoriesModule
import org.koin.core.context.startKoin

object KoinInitializer {
    fun init() {
        startKoin {
            val dataModules = listOf(httpModule, localSourceModule, remoteSourceModule)
            val domainModules = listOf(repositoriesModule)
            val appModules = listOf(viewModelsModule)
            modules(
                *dataModules.toTypedArray(),
                *domainModules.toTypedArray(),
                *appModules.toTypedArray(),
            )
        }
    }
}
