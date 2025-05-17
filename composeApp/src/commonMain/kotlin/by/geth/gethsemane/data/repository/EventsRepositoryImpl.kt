package by.geth.gethsemane.data.repository

import by.geth.gethsemane.data.source.local.db.dao.EventsDao
import by.geth.gethsemane.data.source.local.db.model.EventEntity
import by.geth.gethsemane.data.source.remote.model.EventDTO
import by.geth.gethsemane.data.source.remote.service.EventsService
import by.geth.gethsemane.domain.model.Event
import by.geth.gethsemane.domain.repository.EventsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

class EventsRepositoryImpl(
    private val eventsService: EventsService,
    private val eventsDao: EventsDao,
): EventsRepository {
    @OptIn(FormatStringsInDatetimeFormats::class)
    private val dateTimeFormat = LocalDateTime.Format {
        byUnicodePattern("yyyy-MM-dd HH:mm:ss")
    }
    @OptIn(FormatStringsInDatetimeFormats::class)
    private val dateFormat = LocalDate.Format {
        byUnicodePattern("yyyy-MM-dd")
    }

    override val eventsFlow: Flow<List<Event>> = eventsDao.getAll().map { entityList ->
        entityList.map { it.toDomainModel() }
    }

    override suspend fun loadEvents(dateFrom: LocalDate): Result<List<Event>> {
        val dateFormatted = dateFormat.format(dateFrom)
        return eventsService.getEvents(dateFormatted).map { dtoList ->
            val entityList = dtoList.map { it.toDbModel() }
            eventsDao.replaceFromDate(dateFormatted, entityList)
            entityList.map { it.toDomainModel() }
        }
    }
    
    private fun EventDTO.toDbModel() = EventEntity(
        id = this.id,
        categoryId = this.categoryId,
        title = this.title,
        date = this.date,
        note = this.note,
        audio = this.audio,
        shortDesc = this.shortDesc,
        isDraft = this.isDraft != 0,
        isArchive = this.isArchive != 0,
        musicGroupId = this.musicGroupId,
        video = this.video,
    )

    private fun EventEntity.toDomainModel() = Event(
        id = this.id,
        musicGroupId = this.musicGroupId,
        title = this.title,
        dateTime = dateTimeFormat.parse(this.date)
    )
}
