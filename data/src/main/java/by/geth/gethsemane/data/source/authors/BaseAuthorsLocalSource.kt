package by.geth.gethsemane.data.source.authors

import by.geth.gethsemane.domain.model.Author
import kotlinx.coroutines.flow.Flow

interface BaseAuthorsLocalSource {
    fun getAllAuthors(): Flow<List<Author>>
    fun getSingleAuthor(authorId: Long): Flow<Author>

    suspend fun putAllAuthors(authors: List<Author>)
    suspend fun putSingleAuthor(author: Author)
}
