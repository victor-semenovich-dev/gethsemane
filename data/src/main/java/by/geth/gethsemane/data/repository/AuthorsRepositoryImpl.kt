package by.geth.gethsemane.data.repository

import by.geth.gethsemane.data.model.remote.AuthorDTO
import by.geth.gethsemane.data.source.authors.AuthorsInMemoryCacheSource
import by.geth.gethsemane.data.source.authors.AuthorsRemoteSource
import by.geth.gethsemane.domain.model.Author
import by.geth.gethsemane.domain.repository.AuthorsRepository

// TODO save data to the database
class AuthorsRepositoryImpl(
    private val authorsLocalSource: AuthorsInMemoryCacheSource,
    private val authorsRemoteSource: AuthorsRemoteSource,
): AuthorsRepository {

    override suspend fun getAllAuthors(): List<Author> {
        return authorsLocalSource.getAllAuthors()
    }

    override suspend fun getSingleAuthor(authorId: Long): Author? {
        return authorsLocalSource.getSingleAuthor(authorId)
    }

    override suspend fun loadAllAuthors(): Result<List<Author>> {
        return authorsRemoteSource.loadAllAuthors().map { dtoList ->
            dtoList.map { it.toDomainModel() }
        }.onSuccess { authors -> authorsLocalSource.setAllAuthors(authors) }
    }

    override suspend fun loadSingleAuthor(authorId: Long): Result<Author> {
        return authorsRemoteSource.loadSingleAuthor(authorId).mapCatching { dtoList ->
            dtoList.first().toDomainModel()
        }.onSuccess { author ->
            authorsLocalSource.setSingleAuthor(author)
        }
    }

    private fun AuthorDTO.toDomainModel() = Author(
        id = this.id,
        name = this.name,
        biography = this.biography,
    )
}
