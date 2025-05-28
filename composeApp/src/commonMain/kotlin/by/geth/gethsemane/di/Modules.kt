package by.geth.gethsemane.di

import Gethsemane.composeApp.BuildConfig
import by.geth.gethsemane.data.repository.BirthdaysRepositoryImpl
import by.geth.gethsemane.data.repository.EventsRepositoryImpl
import by.geth.gethsemane.data.repository.MusicGroupsRepositoryImpl
import by.geth.gethsemane.data.source.local.db.AppDatabase
import by.geth.gethsemane.data.source.local.db.dao.BirthdaysDao
import by.geth.gethsemane.data.source.local.db.dao.EventsDao
import by.geth.gethsemane.data.source.local.db.dao.MusicGroupsDao
import by.geth.gethsemane.data.source.remote.service.BirthdaysService
import by.geth.gethsemane.data.source.remote.service.EventsService
import by.geth.gethsemane.data.source.remote.service.MusicGroupsService
import by.geth.gethsemane.domain.manager.ScheduleManager
import by.geth.gethsemane.domain.repository.BirthdaysRepository
import by.geth.gethsemane.domain.repository.EventsRepository
import by.geth.gethsemane.domain.repository.MusicGroupsRepository
import by.geth.gethsemane.ui.route.birthdays.BirthdaysViewModel
import by.geth.gethsemane.ui.route.home.events.WorshipListViewModel
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
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

expect val platformModule: Module

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
    single<BirthdaysService> {
        val httpClient: HttpClient by inject(qualifier = named("api.geth.by"))
        BirthdaysService(httpClient = httpClient)
    }
    single<MusicGroupsService> {
        val httpClient: HttpClient by inject(qualifier = named("api.gethsemane.by"))
        MusicGroupsService(httpClient = httpClient)
    }
}

val daoModule = module {
    single<EventsDao> {
        val appDatabase: AppDatabase = get()
        appDatabase.eventsDao()
    }
    single<MusicGroupsDao> {
        val appDatabase: AppDatabase = get()
        appDatabase.musicGroupsDao()
    }
    single<BirthdaysDao> {
        val appDatabase: AppDatabase = get()
        appDatabase.birthdaysDao()
    }
}

val repositoriesModule = module {
    single<EventsRepository> { EventsRepositoryImpl(get(), get()) }
    single<BirthdaysRepository> { BirthdaysRepositoryImpl(get(), get()) }
    single<MusicGroupsRepository> { MusicGroupsRepositoryImpl(get(), get(), get()) }
}

val managersModule = module {
    singleOf(::ScheduleManager)
}

val viewModelsModule = module {
    viewModelOf(::ScheduleViewModel)
    viewModelOf(::BirthdaysViewModel)
    viewModelOf(::WorshipListViewModel)
}
