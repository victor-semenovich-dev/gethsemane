package by.geth.gethsemane.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(platformModule, httpModule, servicesModule, daoModule, repositoriesModule,
            managersModule, viewModelsModule)
    }
}
