package by.geth.gethsemane.di.data.remote

import by.geth.gethsemane.data.source.remote.AuthorsRemoteSource
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

val remoteSourceModule = module {
    single<AuthorsRemoteSource> {
        val httpClient: HttpClient by inject(qualifier = named(API_GETH_BY))
        AuthorsRemoteSource(httpClient)
    }
}
