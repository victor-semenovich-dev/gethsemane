package by.geth.gethsemane.data.source.authors.remote

import by.geth.gethsemane.data.model.remote.AuthorDTO
import by.geth.gethsemane.data.source.authors.base.BaseAuthorsRemoteSource
import by.geth.gethsemane.domain.model.Author
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class AuthorsKtorSource(private val httpClient: HttpClient): BaseAuthorsRemoteSource {
    override suspend fun loadAllAuthors(): Result<List<Author>> {
        try {
            val response = httpClient.get("/sermoners")
            val authorDtoList: List<AuthorDTO> = response.body()
            val authorList = authorDtoList.map { it.toDomainModel() }
            return Result.success(authorList)
        } catch (t: Throwable) {
            t.printStackTrace()
            return Result.failure(t)
        }
    }

    override suspend fun loadSingleAuthor(id: Long): Result<Author> {
        try {
            val response = httpClient.get("/sermoners") {
                parameter("id", id)
            }
            val authorDtoList: List<AuthorDTO> = response.body()
            val author = authorDtoList.first().toDomainModel()
            return Result.success(author)
        } catch (t: Throwable) {
            t.printStackTrace()
            return Result.failure(t)
        }
    }

    private fun AuthorDTO.toDomainModel() = Author(
        id = this.id,
        name = this.name,
        biography = this.biography,
    )
}