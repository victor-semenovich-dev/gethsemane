package by.geth.gethsemane.data.repository

import by.geth.gethsemane.data.source.local.db.dao.MusicGroupsDao
import by.geth.gethsemane.data.source.local.db.model.MusicGroupEntity
import by.geth.gethsemane.data.source.remote.model.MusicGroupDTO
import by.geth.gethsemane.data.source.remote.service.MusicGroupsService
import by.geth.gethsemane.domain.model.MusicGroup
import by.geth.gethsemane.domain.repository.MusicGroupsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MusicGroupsRepositoryImpl(
    private val musicGroupsService: MusicGroupsService,
    private val musicGroupsDao: MusicGroupsDao,
): MusicGroupsRepository {
    override val musicGroupsFlow: Flow<List<MusicGroup>> = musicGroupsDao.getAll().map { entityList ->
        entityList.map { it.toDomainModel() }
    }

    override suspend fun loadMusicGroups(): Result<Unit> = withContext(Dispatchers.IO) {
        musicGroupsService.getMusicGroups().map { musicGroupsDTO ->
            val dbEntityList = musicGroupsDTO.map { it.toDbEntity() }
            musicGroupsDao.replaceAll(dbEntityList)
        }
    }

    override suspend fun loadMusicGroup(id: Int): Result<Unit> = withContext(Dispatchers.IO) {
        musicGroupsService.getMusicGroup(id).map {
            val dbEntity = it.toDbEntity()
            musicGroupsDao.insertOrUpdate(dbEntity)
        }
    }

    private fun MusicGroupEntity.toDomainModel() = MusicGroup(
        id = this.id,
        title = this.title,
    )

    private fun MusicGroupDTO.toDbEntity() = MusicGroupEntity(
        id = this.id,
        title = this.title,
        history = this.history,
        leader = this.leader,
        image = this.image,
        isActive = this.isActive,
    )
}
