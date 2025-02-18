package by.geth.gethsemane.data.repository

import by.geth.gethsemane.data.source.remote.service.EventsService
import by.geth.gethsemane.domain.model.Event
import by.geth.gethsemane.domain.repository.EventsRepository
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

class EventsRepositoryImpl(
    private val eventsService: EventsService,
): EventsRepository {
    private val formatPattern = "yyyy-MM-dd HH:mm:ss"

    @OptIn(FormatStringsInDatetimeFormats::class)
    private val dateTimeFormat = LocalDateTime.Format {
        byUnicodePattern(formatPattern)
    }

    override suspend fun loadEvents(): Result<List<Event>> {
        return eventsService.getEvents().map { dtoList ->
            dtoList.map {
                Event(
                    id = it.id,
                    musicGroupId = it.musicGroupId,
                    title = it.title,
                    dateTime = dateTimeFormat.parse(it.date)
                )
            }
        }
    }
}
