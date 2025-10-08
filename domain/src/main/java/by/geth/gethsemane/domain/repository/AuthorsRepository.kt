package by.geth.gethsemane.domain.repository

import by.geth.gethsemane.domain.model.Author

interface AuthorsRepository {

    suspend fun getAllAuthors(): List<Author>

    suspend fun getSingleAuthor(authorId: Long): Author?

    suspend fun loadAllAuthors(): Result<List<Author>>

    suspend fun loadSingleAuthor(authorId: Long): Result<Author>
}
