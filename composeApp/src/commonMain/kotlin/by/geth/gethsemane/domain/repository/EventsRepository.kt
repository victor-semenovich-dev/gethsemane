package by.geth.gethsemane.domain.repository

import by.geth.gethsemane.domain.model.Event

interface EventsRepository {
    suspend fun loadEvents(): List<Event>
}
