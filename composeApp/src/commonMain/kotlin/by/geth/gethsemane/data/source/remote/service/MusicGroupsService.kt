package by.geth.gethsemane.data.source.remote.service

import by.geth.gethsemane.data.source.remote.model.MusicGroupDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class MusicGroupsService(private val httpClient: HttpClient) {
    suspend fun getMusicGroups(): Result<List<MusicGroupDTO>> {
        try {
            val response = withContext(Dispatchers.IO) {
                httpClient.get("/MusicGroups")
            }
            val body = withContext(Dispatchers.Default) {
                response.body<List<MusicGroupDTO>>()
            }
            return Result.success(body)
        } catch (t: Throwable) {
            t.printStackTrace()
            return Result.failure(t)
        }
    }

    suspend fun getMusicGroup(id: Int): Result<MusicGroupDTO> {
        try {
            return Result.success(httpClient.get("/MusicGroups/$id").body())
        } catch (t: Throwable) {
            t.printStackTrace()
            return Result.failure(t)
        }
    }
}
