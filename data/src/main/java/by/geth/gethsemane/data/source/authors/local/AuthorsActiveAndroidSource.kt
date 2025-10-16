package by.geth.gethsemane.data.source.authors.local

import by.geth.gethsemane.data.model.local.activeAndroid.AuthorEntity
import by.geth.gethsemane.data.source.authors.base.BaseAuthorsLocalSource
import by.geth.gethsemane.domain.model.Author
import com.activeandroid.ActiveAndroid
import com.activeandroid.query.Delete
import com.activeandroid.query.Select
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthorsActiveAndroidSource: BaseAuthorsLocalSource {
    override suspend fun getAllAuthors(): List<Author> = withContext(Dispatchers.IO) {
        Select()
            .from(AuthorEntity::class.java)
            .execute<AuthorEntity>()
            .map { it.toDomainModel() }
    }

    override suspend fun getSingleAuthor(authorId: Long): Author? = withContext(Dispatchers.IO) {
        Select()
            .from(AuthorEntity::class.java)
            .where("${AuthorEntity.COLUMN_ID} = $authorId")
            .executeSingle<AuthorEntity>()?.toDomainModel()
    }

    override suspend fun putAllAuthors(authors: List<Author>) = withContext(Dispatchers.IO) {
        ActiveAndroid.beginTransaction()
        Delete().from(AuthorEntity::class.java).execute<AuthorEntity>()
        authors.forEach { it.toDbEntity().save() }
        ActiveAndroid.setTransactionSuccessful()
        ActiveAndroid.endTransaction()
    }

    override suspend fun putSingleAuthor(author: Author) {
        withContext(Dispatchers.IO) {
            ActiveAndroid.beginTransaction()
            Delete()
                .from(AuthorEntity::class.java)
                .where("${AuthorEntity.COLUMN_ID} == ${author.id}")
                .execute<AuthorEntity>()
            author.toDbEntity().save()
            ActiveAndroid.setTransactionSuccessful()
            ActiveAndroid.endTransaction()
        }
    }

    private fun Author.toDbEntity() = AuthorEntity(
        id = this.id,
        name = this.name,
        biography = this.biography ?: "",
    )

    private fun AuthorEntity.toDomainModel() = Author(
        id = this.id,
        name = this.name,
        biography = this.biography,
    )
}
