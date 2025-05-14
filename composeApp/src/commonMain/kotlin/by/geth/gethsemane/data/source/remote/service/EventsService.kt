package by.geth.gethsemane.data.source.remote.service

import by.geth.gethsemane.data.source.remote.model.EventDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class EventsService(private val httpClient: HttpClient) {
    suspend fun getEvents(date: String?): Result<List<EventDTO>> = withContext(Dispatchers.IO) {
        try {
            val response = httpClient.get("/events") {
                url {
                    date?.let { parameters.append("date", it) }
                }
            }
            val body = withContext(Dispatchers.Default) { response.body<List<EventDTO>>() }
            Result.success(body)
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
    }
}
