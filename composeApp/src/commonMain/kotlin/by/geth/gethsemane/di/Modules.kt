package by.geth.gethsemane.di

import Gethsemane.composeApp.BuildConfig
import by.geth.gethsemane.data.repository.EventsRepositoryImpl
import by.geth.gethsemane.data.repository.MusicGroupsRepositoryImpl
import by.geth.gethsemane.data.source.remote.service.EventsService
import by.geth.gethsemane.data.source.remote.service.MusicGroupsService
import by.geth.gethsemane.domain.repository.EventsRepository
import by.geth.gethsemane.domain.repository.MusicGroupsRepository
import by.geth.gethsemane.domain.manager.ScheduleManager
import by.geth.gethsemane.ui.route.schedule.ScheduleViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
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
    single<HttpClient>(named("api.gethsemane.by")) {
        HttpClient {
            defaultRequest {
                url("https://api.gethsemane.by")
                header("X-Api-Key", BuildConfig.X_API_KEY)
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
    single<EventsService> {
        val httpClient: HttpClient by inject(qualifier = named("api.geth.by"))
        EventsService(httpClient = httpClient)
    }
    single<MusicGroupsService> {
        val httpClient: HttpClient by inject(qualifier = named("api.gethsemane.by"))
        MusicGroupsService(httpClient = httpClient)
    }
}

val repositoriesModule = module {
    single<EventsRepository> { EventsRepositoryImpl(get()) }
    single<MusicGroupsRepository> { MusicGroupsRepositoryImpl(get()) }
}

val useCaseModule = module {
    singleOf(::ScheduleManager)
}

val viewModelsModule = module {
    viewModelOf(::ScheduleViewModel)
}
