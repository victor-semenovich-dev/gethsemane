package by.geth.gethsemane.data.source.remote.service

import by.geth.gethsemane.data.source.remote.model.AuthorDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class AuthorsService(private val httpClient: HttpClient) {
    suspend fun getAllAuthors(): Result<List<AuthorDTO>> {
        try {
            val response = httpClient.get("/sermoners")
            return Result.success(response.body())
        } catch (t: Throwable) {
            t.printStackTrace()
            return Result.failure(t)
        }
    }

    suspend fun getAuthor(id: Int): Result<AuthorDTO> {
        try {
            val response = httpClient.get("/sermoners/$id")
            val responseBody: List<AuthorDTO> = response.body()
            return Result.success(responseBody.first())
        } catch (t: Throwable) {
            t.printStackTrace()
            return Result.failure(t)
        }
    }
}
