package by.geth.gethsemane.data.source.remote.service

import by.geth.gethsemane.data.source.remote.model.BirthdaysDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class BirthdaysService(private val httpClient: HttpClient) {
    suspend fun getBirthdays(): Result<List<BirthdaysDTO>> {
        try {
            val response = withContext(Dispatchers.IO) {
                httpClient.get("/birthdays")
            }
            val body = withContext(Dispatchers.Default) {
                response.body<List<BirthdaysDTO>>()
            }
            return Result.success(body)
        } catch (t: Throwable) {
            t.printStackTrace()
            return Result.failure(t)
        }
    }
}
