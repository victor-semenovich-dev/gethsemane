package by.geth.gethsemane.di.data.remote

import by.geth.gethsemane.BuildConfig
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

const val API_GETH_BY = "api.geth.by"
const val API_GETHSEMANE_BY = "api.gethsemane.by"

val httpModule = module {
    single<HttpClient>(named(API_GETH_BY)) {
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
                            username = BuildConfig.API_BASE_AUTH_LOGIN,
                            password = BuildConfig.API_BASE_AUTH_PASS,
                        )
                    }
                }
            }
        }
    }
    single<HttpClient>(named(API_GETHSEMANE_BY)) {
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
