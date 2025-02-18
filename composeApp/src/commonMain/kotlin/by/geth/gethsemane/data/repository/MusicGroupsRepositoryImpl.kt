package by.geth.gethsemane.data.repository

import by.geth.gethsemane.data.source.remote.model.MusicGroupDTO
import by.geth.gethsemane.data.source.remote.service.MusicGroupsService
import by.geth.gethsemane.domain.model.MusicGroup
import by.geth.gethsemane.domain.repository.MusicGroupsRepository

class MusicGroupsRepositoryImpl(
    private val musicGroupsService: MusicGroupsService,
): MusicGroupsRepository {
    override suspend fun loadMusicGroups(): Result<List<MusicGroup>> {
        return musicGroupsService.getMusicGroups().map { musicGroupsDTO ->
            musicGroupsDTO.map { it.toDomainModel() }
        }
    }

    override suspend fun loadMusicGroup(id: Int): Result<MusicGroup> {
        return musicGroupsService.getMusicGroup(id).map { it.toDomainModel() }
    }

    private fun MusicGroupDTO.toDomainModel() = MusicGroup(
        id = this.id,
        title = this.title,
    )
}
