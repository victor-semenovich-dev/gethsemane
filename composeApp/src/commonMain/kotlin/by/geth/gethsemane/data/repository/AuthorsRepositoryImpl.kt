package by.geth.gethsemane.data.repository

import by.geth.gethsemane.data.source.local.datastore.AppPreferences
import by.geth.gethsemane.data.source.local.db.dao.AuthorsDao
import by.geth.gethsemane.data.source.local.db.model.AuthorEntity
import by.geth.gethsemane.data.source.remote.model.AuthorDTO
import by.geth.gethsemane.data.source.remote.service.AuthorsService
import by.geth.gethsemane.domain.model.Author
import by.geth.gethsemane.domain.repository.AuthorsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AuthorsRepositoryImpl(
    private val authorsService: AuthorsService,
    private val authorsDao: AuthorsDao,
    private val appPreferences: AppPreferences,
): AuthorsRepository {
    override val authorsFlow: Flow<List<Author>> = authorsDao.getAll().map { entityList ->
        entityList.map { it.toDomainModel() }
    }

    override suspend fun loadAllAuthors(): Result<List<Author>> {
        if (appPreferences.authorsLoaded.first()) {
            return Result.success(authorsFlow.first())
        }

        return authorsService.getAllAuthors().map { authorsListDTO ->
            val dbEntityList = authorsListDTO.map { it.toDbEntity() }
            authorsDao.replaceAll(dbEntityList)
            dbEntityList.map { it.toDomainModel() }
        }.onSuccess {
            appPreferences.setAuthorsLoaded()
        }
    }

    override suspend fun loadAuthor(id: Int): Result<Author> {
        return authorsService.getAuthor(id).map {
            val dbEntity = it.toDbEntity()
            authorsDao.insertOrUpdate(dbEntity)
            dbEntity.toDomainModel()
        }
    }

    override fun getAuthor(id: Int): Flow<Author?> {
        return authorsDao.getById(id).map { it?.toDomainModel() }
    }

    private fun AuthorEntity.toDomainModel() = Author(
        id = this.id,
        name = this.name,
        biography = this.biography,
    )

    private fun AuthorDTO.toDbEntity() = AuthorEntity(
        id = this.id,
        name = this.name,
        biography = this.biography,
    )
}