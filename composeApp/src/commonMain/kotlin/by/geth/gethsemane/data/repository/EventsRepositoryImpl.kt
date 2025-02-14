package by.geth.gethsemane.data.repository

import by.geth.gethsemane.domain.model.Event
import by.geth.gethsemane.domain.repository.EventsRepository
import kotlinx.coroutines.delay

class EventsRepositoryImpl: EventsRepository {
    override suspend fun loadEvents(): Result<List<Event>> {
        delay(3000)
        return Result.success(listOf(
            Event(title = "Богослужение 16.02 (ВС, утро)"),
            Event(title = "Богослужение 16.02 (ВС, вечер)"),
            Event(title = "Богослужение 20.02 (ЧТ)"),
        ))
    }
}