package by.geth.gethsemane.data.repository

import by.geth.gethsemane.data.source.local.db.dao.MusicGroupsDao
import by.geth.gethsemane.data.source.local.db.model.MusicGroupEntity
import by.geth.gethsemane.data.source.remote.model.MusicGroupDTO
import by.geth.gethsemane.data.source.remote.service.MusicGroupsService
import by.geth.gethsemane.domain.model.MusicGroup
import by.geth.gethsemane.domain.repository.MusicGroupsRepository

class MusicGroupsRepositoryImpl(
    private val musicGroupsService: MusicGroupsService,
    private val musicGroupsDao: MusicGroupsDao,
): MusicGroupsRepository {
    override suspend fun loadMusicGroups(): Result<List<MusicGroup>> {
        return musicGroupsService.getMusicGroups().map { musicGroupsDTO ->
            val dbEntityList = musicGroupsDTO.map { it.toDbEntity() }
            musicGroupsDao.clear()
            musicGroupsDao.insertOrUpdate(*dbEntityList.toTypedArray())
            musicGroupsDTO.map { it.toDomainModel() }
        }
    }

    override suspend fun loadMusicGroup(id: Int): Result<MusicGroup> {
        return musicGroupsService.getMusicGroup(id).map {
            val dbEntity = it.toDbEntity()
            musicGroupsDao.insertOrUpdate(dbEntity)
            it.toDomainModel()
        }
    }

    private fun MusicGroupDTO.toDomainModel() = MusicGroup(
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
