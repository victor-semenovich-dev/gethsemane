package by.geth.gethsemane.di.data

import by.geth.gethsemane.data.source.authors.base.BaseAuthorsRemoteSource
import by.geth.gethsemane.data.source.authors.remote.AuthorsKtorSource
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

val remoteSourceModule = module {
    single<BaseAuthorsRemoteSource> {
        val httpClient: HttpClient by inject(qualifier = named(API_GETH_BY))
        AuthorsKtorSource(httpClient)
    }
}