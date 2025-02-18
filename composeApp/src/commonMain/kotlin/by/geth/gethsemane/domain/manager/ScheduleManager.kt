package by.geth.gethsemane.domain.manager

import by.geth.gethsemane.domain.model.Schedule
import by.geth.gethsemane.domain.model.ScheduleItem
import by.geth.gethsemane.domain.repository.EventsRepository
import by.geth.gethsemane.domain.repository.MusicGroupsRepository
import by.geth.gethsemane.domain.util.chain

class ScheduleManager(
    private val eventsRepository: EventsRepository,
    private val musicGroupsRepository: MusicGroupsRepository,
) {
    suspend fun loadSchedule(): Result<Schedule> {
        return eventsRepository.loadEvents().chain { events ->
            musicGroupsRepository.loadMusicGroups().map { musicGroups ->
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
        }
    }
}
