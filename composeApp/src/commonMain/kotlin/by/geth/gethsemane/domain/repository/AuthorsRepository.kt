package by.geth.gethsemane.domain.repository

import by.geth.gethsemane.domain.model.Author
import kotlinx.coroutines.flow.Flow

interface AuthorsRepository {
    val authorsFlow: Flow<List<Author>>

    suspend fun loadAllAuthors(): Result<List<Author>>
    suspend fun loadAuthor(id: Int): Result<Author>
}
