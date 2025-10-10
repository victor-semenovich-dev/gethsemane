package by.geth.gethsemane.di.data

import by.geth.gethsemane.data.source.authors.AuthorsRemoteSource
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

val remoteSourceModule = module {
    single<AuthorsRemoteSource> {
        val httpClient: HttpClient by inject(qualifier = named(API_GETH_BY))
        AuthorsRemoteSource(httpClient)
    }
}