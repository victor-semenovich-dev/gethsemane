package by.geth.gethsemane.data.repository

import by.geth.gethsemane.data.source.authors.base.BaseAuthorsLocalSource
import by.geth.gethsemane.data.source.authors.base.BaseAuthorsRemoteSource
import by.geth.gethsemane.domain.model.Author
import by.geth.gethsemane.domain.repository.AuthorsRepository

// TODO save data to the database
class AuthorsRepositoryImpl(
    private val authorsLocalSource: BaseAuthorsLocalSource,
    private val authorsKtorSource: BaseAuthorsRemoteSource,
): AuthorsRepository {

    override suspend fun getAllAuthors(): List<Author> {
        return authorsLocalSource.getAllAuthors()
    }

    override suspend fun getSingleAuthor(authorId: Long): Author? {
        return authorsLocalSource.getSingleAuthor(authorId)
    }

    override suspend fun loadAllAuthors(): Result<List<Author>> {
        return authorsKtorSource.loadAllAuthors()
            .onSuccess { authors -> authorsLocalSource.putAllAuthors(authors) }
    }

    override suspend fun loadSingleAuthor(authorId: Long): Result<Author> {
        return authorsKtorSource.loadSingleAuthor(authorId)
            .onSuccess { author -> authorsLocalSource.putSingleAuthor(author) }
    }
}
