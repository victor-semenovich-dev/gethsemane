package by.geth.gethsemane.data.repository

import by.geth.gethsemane.data.source.remote.service.EventsService
import by.geth.gethsemane.domain.model.Event
import by.geth.gethsemane.domain.repository.EventsRepository

class EventsRepositoryImpl(
    private val eventsService: EventsService,
): EventsRepository {
    override suspend fun loadEvents(): Result<List<Event>> {
        try {
            val events = eventsService.getEvents().map {
                Event(it.title)
            }
            return Result.success(events)
        } catch (t: Throwable) {
            t.printStackTrace()
            return Result.failure(t)
        }
    }
}
