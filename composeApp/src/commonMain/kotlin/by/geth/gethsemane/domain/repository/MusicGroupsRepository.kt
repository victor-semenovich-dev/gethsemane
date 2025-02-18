package by.geth.gethsemane.domain.repository

import by.geth.gethsemane.domain.model.MusicGroup

interface MusicGroupsRepository {
    suspend fun loadMusicGroups(): Result<List<MusicGroup>>
    suspend fun loadMusicGroup(id: Int): Result<MusicGroup>
}
