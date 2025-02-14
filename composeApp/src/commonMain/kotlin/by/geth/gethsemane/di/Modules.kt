package by.geth.gethsemane.di

import by.geth.gethsemane.data.repository.EventsRepositoryImpl
import by.geth.gethsemane.data.source.remote.service.EventsService
import by.geth.gethsemane.domain.repository.EventsRepository
import by.geth.gethsemane.ui.route.schedule.ScheduleViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val httpModule = module {
    single<HttpClient> {
        HttpClient {
            defaultRequest {
                url("https://api.geth.by")
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }
}

val servicesModule = module {
    singleOf(::EventsService)
}

val repositoriesModule = module {
    single<EventsRepository> { EventsRepositoryImpl(get()) }
}

val viewModelsModule = module {
    viewModelOf(::ScheduleViewModel)
}
