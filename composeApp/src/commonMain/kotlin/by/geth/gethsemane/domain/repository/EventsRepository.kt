package by.geth.gethsemane.domain.repository

import by.geth.gethsemane.domain.model.Event
import kotlinx.coroutines.flow.Flow

interface EventsRepository {
    val eventsFlow: Flow<List<Event>>

    suspend fun loadEvents(): Result<Unit>
}
