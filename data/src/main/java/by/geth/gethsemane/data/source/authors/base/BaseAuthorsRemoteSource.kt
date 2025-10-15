package by.geth.gethsemane.data.source.authors.base

import by.geth.gethsemane.domain.model.Author

interface BaseAuthorsRemoteSource {
    suspend fun loadAllAuthors(): Result<List<Author>>
    suspend fun loadSingleAuthor(id: Long): Result<Author>
}
