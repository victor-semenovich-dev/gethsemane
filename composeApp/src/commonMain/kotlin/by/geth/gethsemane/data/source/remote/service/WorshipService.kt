package by.geth.gethsemane.data.source.remote.service

import by.geth.gethsemane.data.source.remote.model.WorshipDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments

class WorshipService(private val httpClient: HttpClient) {
    suspend fun getWorship(id: Int): Result<WorshipDTO> {
        try {
            val response = httpClient.get("/mobile/worship") {
                url {
                    appendPathSegments(id.toString())
                }
            }
            return Result.success(response.body())
        } catch (t: Throwable) {
            t.printStackTrace()
            return Result.failure(t)
        }
    }
}
