package by.geth.gethsemane.di

import Gethsemane.composeApp.BuildConfig
import by.geth.gethsemane.data.repository.EventsRepositoryImpl
import by.geth.gethsemane.data.source.remote.service.EventsService
import by.geth.gethsemane.domain.repository.EventsRepository
import by.geth.gethsemane.ui.route.schedule.ScheduleViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val httpModule = module {
    single<HttpClient>(named("api.geth.by")) {
        HttpClient {
            defaultRequest {
                url("https://api.geth.by")
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = BuildConfig.BASIC_AUTH_USERNAME,
                            password = BuildConfig.BASIC_AUTH_PASSWORD,
                        )
                    }
                }
            }
        }
    }
}

val servicesModule = module {
    single<EventsService> {
        val httpClient: HttpClient by inject(qualifier = named("api.geth.by"))
        EventsService(httpClient = httpClient)
    }
}

val repositoriesModule = module {
    single<EventsRepository> { EventsRepositoryImpl(get()) }
}

val viewModelsModule = module {
    viewModelOf(::ScheduleViewModel)
}
