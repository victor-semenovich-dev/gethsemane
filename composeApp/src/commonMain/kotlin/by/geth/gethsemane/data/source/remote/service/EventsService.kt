package by.geth.gethsemane.data.source.remote.service

import by.geth.gethsemane.data.source.remote.model.EventDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class EventsService(private val httpClient: HttpClient) {
    suspend fun getEvents(): List<EventDTO> {
        return httpClient.get("https://api.geth.by/events").body()
    }
}
