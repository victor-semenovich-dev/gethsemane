package by.geth.gethsemane.data.repository

import by.geth.gethsemane.domain.model.Author
import by.geth.gethsemane.domain.repository.AuthorsRepository

// TODO load data from the server
// TODO save data to the database
class AuthorsRepositoryImpl: AuthorsRepository {
    private val authorsList = emptyList<Author>()

    override suspend fun getAllAuthors(): List<Author> {
        return authorsList
    }

    override suspend fun getSingleAuthor(authorId: Long): Author? {
        return authorsList.firstOrNull { it.id == authorId }
    }

    override suspend fun loadAllAuthors(): Result<List<Author>> {
        return Result.failure(Exception())
    }

    override suspend fun loadSingleAuthor(authorId: Long): Result<Author> {
        return Result.failure(Exception())
    }
}
