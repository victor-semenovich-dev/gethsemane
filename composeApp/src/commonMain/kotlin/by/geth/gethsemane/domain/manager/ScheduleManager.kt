package by.geth.gethsemane.domain.manager

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
import kotlinx.coroutines.withContext

class ScheduleManager(
    private val eventsRepository: EventsRepository,
    private val musicGroupsRepository: MusicGroupsRepository,
) {
    val scheduleFlow: Flow<Schedule> = combine(
        eventsRepository.eventsFlow,
        musicGroupsRepository.musicGroupsFlow
    ) { events, musicGroups -> buildSchedule(events, musicGroups) }

    suspend fun loadSchedule(): Result<Unit> = coroutineScope {
        val musicGroupsListResultDeferred = async {
            musicGroupsRepository.loadMusicGroups(useCache = true)
        }
        val eventsListResultDeferred = async {
            eventsRepository.loadEvents()
        }

        val musicGroupsListResult = musicGroupsListResultDeferred.await()
        val eventsListResult = eventsListResultDeferred.await()

        if (musicGroupsListResult.isSuccess && eventsListResult.isSuccess) {
            val eventsList = eventsListResult.getOrThrow()
            val musicGroupsList = musicGroupsListResult.getOrThrow()

            val missingMusicGroupIds = withContext(Dispatchers.Default) {
                eventsList.filter {
                    it.musicGroupId != null &&
                            musicGroupsList.none { musicGroup -> musicGroup.id == it.musicGroupId }
                }.map { it.musicGroupId!! }.distinct()
            }

            val loadMissingMusicGroupsDeferredList = missingMusicGroupIds.map { async {
                musicGroupsRepository.loadMusicGroup(it)
            } }
            loadMissingMusicGroupsDeferredList.awaitAll()

            Result.success(Unit)
        } else {
            Result.failure(
                musicGroupsListResult.exceptionOrNull() ?:
                eventsListResult.exceptionOrNull() ?:
                Exception()
            )
        }
    }

    private suspend fun buildSchedule(
        events: List<Event>, musicGroups: List<MusicGroup>
    ): Schedule = withContext(Dispatchers.Default) {
        val scheduleEvents = events.filter { it.dateTime > dateTimeNow }
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
