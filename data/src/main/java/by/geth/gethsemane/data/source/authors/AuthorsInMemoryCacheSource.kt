package by.geth.gethsemane.data.source.authors

import by.geth.gethsemane.domain.model.Author

class AuthorsInMemoryCacheSource {
    private val authorsList = mutableListOf<Author>()

    fun getAllAuthors(): List<Author> {
        return authorsList
    }

    fun setAllAuthors(authors: List<Author>) {
        authorsList.clear()
        authorsList.addAll(authors)
    }

    fun getSingleAuthor(authorId: Long): Author? {
        return authorsList.firstOrNull { it.id == authorId }
    }

    fun setSingleAuthor(author: Author) {
        val index = authorsList.indexOfFirst { it.id == author.id }
        if (index >= 0) {
            authorsList[index] = author
        } else {
            authorsList.add(author)
        }
    }
}
