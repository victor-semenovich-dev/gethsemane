package by.geth.gethsemane.data.source.remote.service

import by.geth.gethsemane.data.source.remote.model.BirthdaysDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class BirthdaysService(private val httpClient: HttpClient) {
    suspend fun getBirthdays(): Result<List<BirthdaysDTO>> {
        try {
            val response = httpClient.get("/birthdays")
            return Result.success(response.body())
        } catch (t: Throwable) {
            t.printStackTrace()
            return Result.failure(t)
        }
    }
}
