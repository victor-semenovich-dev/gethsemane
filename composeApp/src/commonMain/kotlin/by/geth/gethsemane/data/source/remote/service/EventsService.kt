package by.geth.gethsemane.data.source.remote.service

import by.geth.gethsemane.data.source.remote.model.EventDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class EventsService(private val httpClient: HttpClient) {
    suspend fun getEvents(): Result<List<EventDTO>> {
        try {
            return Result.success(httpClient.get("/events").body())
        } catch (t: Throwable) {
            t.printStackTrace()
            return Result.failure(t)
        }
    }
}
