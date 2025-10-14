package by.geth.gethsemane.data.repository

import by.geth.gethsemane.data.model.remote.AuthorDTO
import by.geth.gethsemane.data.source.authors.AuthorsRemoteSource
import by.geth.gethsemane.data.source.authors.BaseAuthorsLocalSource
import by.geth.gethsemane.domain.model.Author
import by.geth.gethsemane.domain.repository.AuthorsRepository
import kotlinx.coroutines.flow.Flow

// TODO save data to the database
class AuthorsRepositoryImpl(
    private val authorsLocalSource: BaseAuthorsLocalSource,
    private val authorsRemoteSource: AuthorsRemoteSource,
): AuthorsRepository {

    override fun getAllAuthors(): Flow<List<Author>> {
        return authorsLocalSource.getAllAuthors()
    }

    override fun getSingleAuthor(authorId: Long): Flow<Author> {
        return authorsLocalSource.getSingleAuthor(authorId)
    }

    override suspend fun loadAllAuthors(): Result<List<Author>> {
        return authorsRemoteSource.loadAllAuthors().map { dtoList ->
            dtoList.map { it.toDomainModel() }
        }.onSuccess { authors -> authorsLocalSource.putAllAuthors(authors) }
    }

    override suspend fun loadSingleAuthor(authorId: Long): Result<Author> {
        return authorsRemoteSource.loadSingleAuthor(authorId).mapCatching { dtoList ->
            dtoList.first().toDomainModel()
        }.onSuccess { author ->
            authorsLocalSource.putSingleAuthor(author)
        }
    }

    private fun AuthorDTO.toDomainModel() = Author(
        id = this.id,
        name = this.name,
        biography = this.biography,
    )
}
