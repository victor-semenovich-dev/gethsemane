package by.geth.gethsemane.data.source.remote.service

import by.geth.gethsemane.data.source.remote.model.EventDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class EventsService(private val httpClient: HttpClient) {
    suspend fun getEvents(): Result<List<EventDTO>> {
        try {
            val response = withContext(Dispatchers.IO) {
                httpClient.get("/events")
            }
            val body = withContext(Dispatchers.Default) {
                response.body<List<EventDTO>>()
            }
            return Result.success(body)
        } catch (t: Throwable) {
            t.printStackTrace()
            return Result.failure(t)
        }
    }
}
