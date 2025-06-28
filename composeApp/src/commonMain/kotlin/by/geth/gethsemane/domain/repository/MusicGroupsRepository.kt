package by.geth.gethsemane.domain.repository

import by.geth.gethsemane.domain.model.MusicGroup
import kotlinx.coroutines.flow.Flow

interface MusicGroupsRepository {
    val musicGroupsFlow: Flow<List<MusicGroup>>

    suspend fun loadAllMusicGroups(): Result<List<MusicGroup>>
    suspend fun loadMusicGroup(id: Int): Result<MusicGroup>
    fun getMusicGroup(id: Int): Flow<MusicGroup?>
}
