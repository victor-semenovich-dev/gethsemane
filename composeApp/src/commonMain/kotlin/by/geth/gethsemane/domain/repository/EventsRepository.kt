package by.geth.gethsemane.domain.repository

import by.geth.gethsemane.domain.model.Event
import by.geth.gethsemane.domain.util.dateNow
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface EventsRepository {
    val eventsFlow: Flow<List<Event>>

    suspend fun loadEvents(dateFrom: LocalDate = dateNow, replaceAll: Boolean = false): Result<List<Event>>
}
