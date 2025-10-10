package by.geth.gethsemane.data.source.authors

import by.geth.gethsemane.data.model.remote.AuthorDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class AuthorsRemoteSource(private val httpClient: HttpClient) {
    suspend fun loadAllAuthors(): Result<List<AuthorDTO>> {
        try {
            val response = httpClient.get("/sermoners")
            return Result.success(response.body())
        } catch (t: Throwable) {
            t.printStackTrace()
            return Result.failure(t)
        }
    }

    suspend fun loadSingleAuthor(id: Long): Result<List<AuthorDTO>> {
        try {
            val response = httpClient.get("/sermoners") {
                parameter("id", id)
            }
            val responseBody: List<AuthorDTO> = response.body()
            return Result.success(responseBody)
        } catch (t: Throwable) {
            t.printStackTrace()
            return Result.failure(t)
        }
    }
}
