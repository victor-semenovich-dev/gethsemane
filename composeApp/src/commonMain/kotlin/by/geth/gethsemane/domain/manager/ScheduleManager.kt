package by.geth.gethsemane.domain.manager

import by.geth.gethsemane.data.source.local.datastore.AppPreferences
import by.geth.gethsemane.domain.model.MusicGroup
import by.geth.gethsemane.domain.model.Schedule
import by.geth.gethsemane.domain.model.ScheduleItem
import by.geth.gethsemane.domain.repository.EventsRepository
import by.geth.gethsemane.domain.repository.MusicGroupsRepository
import by.geth.gethsemane.domain.util.dateNow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

class ScheduleManager(
    private val eventsRepository: EventsRepository,
    private val musicGroupsRepository: MusicGroupsRepository,
    private val appPreferences: AppPreferences,
) {
    private val eventsFlow = eventsRepository.getEventsFromDate(dateNow)

    val scheduleFlow: Flow<Schedule> = combine(
        eventsFlow,
        musicGroupsRepository.musicGroupsFlow
    ) { events, musicGroups ->
        val scheduleItemList = events.map { event ->
            ScheduleItem(
                id = event.id,
                title = event.title,
                dateTime = event.dateTime,
                musicGroup = musicGroups.firstOrNull { event.musicGroupId == it.id }?.title,
            )
        }
        Schedule(items = scheduleItemList)
    }

    suspend fun loadSchedule(): Result<Unit> {
        // TODO parallel execution
        val musicGroupsList = checkAndLoadMusicGroups()
        return eventsRepository.loadEvents().onSuccess {
            val eventsList = eventsFlow.first()
            for (event in eventsList) {
                val musicGroup = musicGroupsList.firstOrNull { event.musicGroupId == it.id }
                if (event.musicGroupId != null && musicGroup == null) {
                    musicGroupsRepository.loadMusicGroup(event.musicGroupId)
                }
            }
        }
    }

    private suspend fun checkAndLoadMusicGroups(): List<MusicGroup> {
        val musicGroupsLoaded = appPreferences.musicGroupsLoaded.first()
        if (!musicGroupsLoaded) {
            musicGroupsRepository.loadMusicGroups().onSuccess {
                appPreferences.setMusicGroupsLoaded()
            }
        }
        return musicGroupsRepository.musicGroupsFlow.first()
    }
}
