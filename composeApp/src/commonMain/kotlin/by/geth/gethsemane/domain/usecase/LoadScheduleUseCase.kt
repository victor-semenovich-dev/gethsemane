package by.geth.gethsemane.domain.usecase

import by.geth.gethsemane.domain.model.Event
import by.geth.gethsemane.domain.model.MusicGroup
import by.geth.gethsemane.domain.model.Schedule
import by.geth.gethsemane.domain.model.ScheduleItem
import by.geth.gethsemane.domain.repository.EventsRepository
import by.geth.gethsemane.domain.repository.MusicGroupsRepository
import by.geth.gethsemane.domain.util.dateTimeNow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class LoadScheduleUseCase(
    private val eventsRepository: EventsRepository,
    private val musicGroupsRepository: MusicGroupsRepository,
) {
    val dataFlow: Flow<Schedule> = combine(
        eventsRepository.eventsFlow,
        musicGroupsRepository.musicGroupsFlow
    ) { events, musicGroups -> buildSchedule(events, musicGroups) }

    suspend operator fun invoke(): Result<Unit> = coroutineScope {
        eventsRepository.loadEvents().map { eventsList ->
            val musicGroupsList = musicGroupsRepository.musicGroupsFlow.first()

            val missingMusicGroupIds = withContext(Dispatchers.Default) {
                eventsList.filter {
                    it.musicGroupId != null &&
                            musicGroupsList.none { musicGroup -> musicGroup.id == it.musicGroupId }
                }.map { it.musicGroupId!! }.distinct()
            }

            missingMusicGroupIds.map { async {
                musicGroupsRepository.loadMusicGroup(it)
            } }.awaitAll()

            Unit
        }
    }

    private suspend fun buildSchedule(
        events: List<Event>, musicGroups: List<MusicGroup>
    ): Schedule = withContext(Dispatchers.Default) {
        val scheduleEvents = events.filter { it.dateTime > dateTimeNow }.sortedBy { it.dateTime }
        val scheduleItemList = scheduleEvents.map { event ->
            ScheduleItem(
                id = event.id,
                title = event.title,
                dateTime = event.dateTime,
                musicGroup = musicGroups.firstOrNull { event.musicGroupId == it.id }?.title,
            )
        }
        Schedule(items = scheduleItemList)
    }
}
