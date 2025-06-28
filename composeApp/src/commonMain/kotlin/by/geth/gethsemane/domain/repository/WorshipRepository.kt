package by.geth.gethsemane.domain.repository

import by.geth.gethsemane.domain.model.Worship
import kotlinx.coroutines.flow.Flow

interface WorshipRepository {
    fun getWorship(id: Int): Flow<Worship>
    suspend fun loadWorship(id: Int): Result<Unit>
}
