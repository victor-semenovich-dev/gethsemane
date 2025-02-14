package by.geth.gethsemane.data.repository

import by.geth.gethsemane.data.source.remote.service.EventsService
import by.geth.gethsemane.domain.model.Event
import by.geth.gethsemane.domain.repository.EventsRepository

class EventsRepositoryImpl(
    private val eventsService: EventsService,
): EventsRepository {
    override suspend fun loadEvents(): Result<List<Event>> {
        return eventsService.getEvents().map { dtoList ->
            dtoList.map { Event(it.title) }
        }
    }
}
