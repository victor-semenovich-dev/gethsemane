package by.geth.gethsemane.data.repository

import by.geth.gethsemane.data.model.remote.AuthorDTO
import by.geth.gethsemane.data.source.remote.AuthorsRemoteSource
import by.geth.gethsemane.domain.model.Author
import by.geth.gethsemane.domain.repository.AuthorsRepository

// TODO save data to the database
class AuthorsRepositoryImpl(
    private val authorsRemoteSource: AuthorsRemoteSource,
): AuthorsRepository {
    private val authorsList = mutableListOf<Author>()

    override suspend fun getAllAuthors(): List<Author> {
        return authorsList
    }

    override suspend fun getSingleAuthor(authorId: Long): Author? {
        return authorsList.firstOrNull { it.id == authorId }
    }

    override suspend fun loadAllAuthors(): Result<List<Author>> {
        return authorsRemoteSource.loadAllAuthors().map { dtoList ->
            dtoList.map { it.toDomainModel() }
        }.onSuccess { resultList ->
            authorsList.clear()
            authorsList.addAll(resultList)
        }
    }

    override suspend fun loadSingleAuthor(authorId: Long): Result<Author> {
        return authorsRemoteSource.loadSingleAuthor(authorId).mapCatching { dtoList ->
            dtoList.first().toDomainModel()
        }.onSuccess { author ->
            val index = authorsList.indexOfFirst { it.id == author.id }
            if (index >= 0) {
                authorsList[index] = author
            } else {
                authorsList.add(author)
            }
        }
    }

    private fun AuthorDTO.toDomainModel() = Author(
        id = this.id,
        name = this.name,
        biography = this.biography,
    )
}
