package by.geth.gethsemane.data.source.authors

import by.geth.gethsemane.domain.model.Author
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

class AuthorsInMemoryCacheSource: BaseAuthorsLocalSource {
    private val authorsListFlow = MutableStateFlow<List<Author>>(emptyList())

    override fun getAllAuthors(): Flow<List<Author>> {
        return authorsListFlow.asStateFlow()
    }

    override fun getSingleAuthor(authorId: Long): Flow<Author> {
        return flow {
            val authorsList = authorsListFlow.value
            val author = authorsList.firstOrNull { it.id == authorId }
            author?.let { emit(it) }
        }
    }

    override suspend fun putAllAuthors(authors: List<Author>) {
        authorsListFlow.value = authors
    }

    override suspend fun putSingleAuthor(author: Author) {
        val authorsList = authorsListFlow.value
        val newAuthorsList = authorsList.toMutableList()
        val index = newAuthorsList.indexOfFirst { it.id == author.id }
        if (index >= 0) {
            newAuthorsList[index] = author
        } else {
            newAuthorsList.add(author)
        }
    }
}
