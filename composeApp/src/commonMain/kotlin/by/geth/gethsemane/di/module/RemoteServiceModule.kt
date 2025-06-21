package by.geth.gethsemane.di.module

import by.geth.gethsemane.data.source.remote.service.AuthorsService
import by.geth.gethsemane.data.source.remote.service.BirthdaysService
import by.geth.gethsemane.data.source.remote.service.EventsService
import by.geth.gethsemane.data.source.remote.service.MusicGroupsService
import by.geth.gethsemane.data.source.remote.service.WorshipService
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

val servicesModule = module {
    single<EventsService> {
        val httpClient: HttpClient by inject(qualifier = named("api.geth.by"))
        EventsService(httpClient = httpClient)
    }
    single<WorshipService> {
        val httpClient: HttpClient by inject(qualifier = named("api.geth.by"))
        WorshipService(httpClient = httpClient)
    }
    single<BirthdaysService> {
        val httpClient: HttpClient by inject(qualifier = named("api.geth.by"))
        BirthdaysService(httpClient = httpClient)
    }
    single<MusicGroupsService> {
        val httpClient: HttpClient by inject(qualifier = named("api.gethsemane.by"))
        MusicGroupsService(httpClient = httpClient)
    }
    single<AuthorsService> {
        val httpClient: HttpClient by inject(qualifier = named("api.geth.by"))
        AuthorsService(httpClient)
    }
}