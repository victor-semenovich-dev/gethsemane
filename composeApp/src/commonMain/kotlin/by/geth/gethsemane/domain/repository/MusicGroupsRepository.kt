package by.geth.gethsemane.domain.repository

import by.geth.gethsemane.domain.model.MusicGroup
import kotlinx.coroutines.flow.Flow

interface MusicGroupsRepository {
    val musicGroupsFlow: Flow<List<MusicGroup>>

    suspend fun loadMusicGroups(): Result<Unit>
    suspend fun loadMusicGroup(id: Int): Result<Unit>
}
