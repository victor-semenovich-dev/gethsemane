package by.geth.gethsemane.data.repository

import by.geth.gethsemane.data.source.local.datastore.AppPreferences
import by.geth.gethsemane.data.source.local.db.dao.MusicGroupsDao
import by.geth.gethsemane.data.source.local.db.model.MusicGroupEntity
import by.geth.gethsemane.data.source.remote.model.MusicGroupDTO
import by.geth.gethsemane.data.source.remote.service.MusicGroupsService
import by.geth.gethsemane.domain.model.MusicGroup
import by.geth.gethsemane.domain.repository.MusicGroupsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class MusicGroupsRepositoryImpl(
    private val musicGroupsService: MusicGroupsService,
    private val musicGroupsDao: MusicGroupsDao,
    private val appPreferences: AppPreferences,
): MusicGroupsRepository {
    override val musicGroupsFlow: Flow<List<MusicGroup>> = musicGroupsDao.getAll().map { entityList ->
        entityList.map { it.toDomainModel() }
    }

    override suspend fun loadMusicGroups(useCache: Boolean): Result<List<MusicGroup>> {
        if (useCache && appPreferences.musicGroupsLoaded.first()) {
            return Result.success(musicGroupsFlow.first())
        }

        return musicGroupsService.getMusicGroups().map { musicGroupsDTO ->
            val dbEntityList = musicGroupsDTO.map { it.toDbEntity() }
            musicGroupsDao.replaceAll(dbEntityList)
            dbEntityList.map { it.toDomainModel() }
        }.onSuccess {
            if (!appPreferences.musicGroupsLoaded.first()) {
                appPreferences.setMusicGroupsLoaded()
            }
        }
    }

    override suspend fun loadMusicGroup(id: Int): Result<MusicGroup> {
        return musicGroupsService.getMusicGroup(id).map {
            val dbEntity = it.toDbEntity()
            musicGroupsDao.insertOrUpdate(dbEntity)
            dbEntity.toDomainModel()
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
