package by.geth.gethsemane.data.source.authors

import by.geth.gethsemane.domain.model.Author

interface BaseAuthorsLocalSource {
    suspend fun getAllAuthors(): List<Author>
    suspend fun getSingleAuthor(authorId: Long): Author?

    suspend fun putAllAuthors(authors: List<Author>)
    suspend fun putSingleAuthor(author: Author)
}
