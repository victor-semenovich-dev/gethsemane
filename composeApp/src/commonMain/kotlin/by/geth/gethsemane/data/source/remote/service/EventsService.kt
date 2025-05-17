package by.geth.gethsemane.data.source.remote.service

import by.geth.gethsemane.data.source.remote.model.EventDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class EventsService(private val httpClient: HttpClient) {
    suspend fun getEvents(date: String? = null): Result<List<EventDTO>> {
        try {
            val response = httpClient.get("/events") {
                url {
                    date?.let { parameters.append("date", it) }
                }
            }
            return Result.success(response.body())
        } catch (t: Throwable) {
            t.printStackTrace()
            return Result.failure(t)
        }
    }
}
