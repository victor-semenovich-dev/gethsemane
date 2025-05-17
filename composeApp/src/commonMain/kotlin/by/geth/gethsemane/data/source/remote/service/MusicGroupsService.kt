package by.geth.gethsemane.data.source.remote.service

import by.geth.gethsemane.data.source.remote.model.MusicGroupDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class MusicGroupsService(private val httpClient: HttpClient) {
    suspend fun getMusicGroups(): Result<List<MusicGroupDTO>> {
        try {
            val response = httpClient.get("/MusicGroups")
            return Result.success(response.body())
        } catch (t: Throwable) {
            t.printStackTrace()
            return Result.failure(t)
        }
    }

    suspend fun getMusicGroup(id: Int): Result<MusicGroupDTO> {
        try {
            val response = httpClient.get("/MusicGroups/$id")
            return Result.success(response.body())
        } catch (t: Throwable) {
            t.printStackTrace()
            return Result.failure(t)
        }
    }
}
