package by.geth.gethsemane.di

import by.geth.gethsemane.di.module.daoModule
import by.geth.gethsemane.di.module.httpModule
import by.geth.gethsemane.di.module.platformModule
import by.geth.gethsemane.di.module.repositoriesModule
import by.geth.gethsemane.di.module.servicesModule
import by.geth.gethsemane.di.module.useCaseModule
import by.geth.gethsemane.di.module.viewModelsModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            platformModule, httpModule, servicesModule, daoModule, repositoriesModule,
            useCaseModule, viewModelsModule
        )
    }
}
