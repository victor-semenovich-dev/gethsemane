package by.geth.gethsemane.di.module

import Gethsemane.composeApp.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
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