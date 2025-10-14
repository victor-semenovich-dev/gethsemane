package by.geth.gethsemane.domain.repository

import by.geth.gethsemane.domain.model.Author
import kotlinx.coroutines.flow.Flow

interface AuthorsRepository {

    fun getAllAuthors(): Flow<List<Author>>

    fun getSingleAuthor(authorId: Long): Flow<Author>

    suspend fun loadAllAuthors(): Result<List<Author>>

    suspend fun loadSingleAuthor(authorId: Long): Result<Author>
}
