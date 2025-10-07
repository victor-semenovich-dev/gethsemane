package by.geth.gethsemane.service

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import by.geth.gethsemane.app.App
import by.geth.gethsemane.app.AppPreferences
import by.geth.gethsemane.data.Event
import by.geth.gethsemane.data.Worship
import com.activeandroid.query.Select
import com.activeandroid.query.Update
import java.io.IOException
import java.util.*

class ScheduleUpdateWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val api = App.getGethAPI()
        try {
            val response = api.events.execute()
            val dateFrom = Calendar.getInstance().time
            val newEventsList = response.body()
            if (response.isSuccessful && newEventsList != null) {
                val oldEventsList : List<Event> = Select()
                        .from(Event::class.java)
                        .where(Event.COLUMN_DATE + " >= ?", dateFrom.time)
                        .execute()
                val scheduleAddedEvents = ArrayList<Event>()
                val scheduleDeletedEvents = ArrayList<Event>()
                val scheduleModifiedOldEvents = ArrayList<Event>()
                val scheduleModifiedNewEvents = ArrayList<Event>()

                for (newEvent in newEventsList) {
                    var isAdded = true
                    for (oldEvent in oldEventsList) {
                        if (newEvent.externalId() == oldEvent.externalId()) {
                            isAdded = false
                            break
                        }
                    }
                    if (isAdded)
                        scheduleAddedEvents.add(newEvent)
                }

                val intent = Intent(ApiService.ACTION_REQUEST_SUCCESSFUL)
                intent.putExtra(ApiService.EXTRA_REQUEST_TYPE, ApiService.REQUEST_GET_EVENTS)
                intent.putExtra(ApiService.EXTRA_SHOW_NOTIFICATION, true)

                for (oldEvent in oldEventsList) {
                    var isDeleted = true
                    for (newEvent in newEventsList) {
                        if (newEvent.externalId() == oldEvent.externalId()) {
                            isDeleted = false
                            if (!Event.isScheduleEventsEqual(oldEvent, newEvent)) {
                                scheduleModifiedOldEvents.add(oldEvent)
                                scheduleModifiedNewEvents.add(newEvent)
                            }
                            break
                        }
                    }
                    if (isDeleted)
                        scheduleDeletedEvents.add(oldEvent)

                    intent.putParcelableArrayListExtra(ApiService.EXTRA_SCHEDULE_EVENTS_ADDED, scheduleAddedEvents)
                    intent.putParcelableArrayListExtra(ApiService.EXTRA_SCHEDULE_EVENTS_DELETED, scheduleDeletedEvents)
                    intent.putParcelableArrayListExtra(ApiService.EXTRA_SCHEDULE_EVENTS_MODIFIED_OLD, scheduleModifiedOldEvents)
                    intent.putParcelableArrayListExtra(ApiService.EXTRA_SCHEDULE_EVENTS_MODIFIED_NEW, scheduleModifiedNewEvents)
                }

                for (newEvent in newEventsList) {
                    if (Select().from(Event::class.java).where(Event.COLUMN_ID + " = ?", newEvent.externalId()).exists()) {
                        Update(Event::class.java)
                                .set(
                                        Event.COLUMN_CATEGORY_ID + " = ?, " +
                                                Event.COLUMN_DATE + " = ?, " +
                                                Event.COLUMN_TITLE + " = ?, " +
                                                Event.COLUMN_SHORT_DESC + " = ?, " +
                                                Event.COLUMN_NOTE + " = ?, " +
                                                Event.COLUMN_IS_DRAFT + " = ?, " +
                                                Event.COLUMN_IS_ARCHIVE + " = ?, " +
                                                Event.COLUMN_AUDIO_REMOTE + " = ?, " +
                                                Event.COLUMN_GROUP_ID + " = ?, " +
                                                Event.COLUMN_VIDEO_REMOTE + " = ?",

                                        newEvent.categoryId,
                                        newEvent.getDate().time,
                                        newEvent.getTitle(),
                                        if (newEvent.shortDesc == null) "" else newEvent.shortDesc,
                                        if (newEvent.note == null) "" else newEvent.note,
                                        if (newEvent.isDraft) 1 else 0,
                                        if (newEvent.isArchive) 1 else 0,
                                        if (newEvent.audioFileName == null) "" else newEvent.audioFileName,
                                        newEvent.musicGroupId,
                                        if (newEvent.videoFileName == null) "" else newEvent.videoFileName
                                )
                                .where(Event.COLUMN_ID + " = ?", newEvent.externalId())
                                .execute()
                    } else {
                        val worship : Worship? = Select().from(Worship::class.java)
                                .where(Worship.COLUMN_ID + " = ?", newEvent.externalId())
                                .executeSingle()
                        if (worship != null) {
                            newEvent.audioLocalUri = worship.audioLocalUri
                        }
                        newEvent.save()
                    }
                }
                for (oldEvent in oldEventsList) {
                    var isDeleted = true
                    for (newEvent in newEventsList) {
                        if (oldEvent.externalId() == newEvent.externalId()) {
                            isDeleted = false
                            break
                        }
                    }
                    if (isDeleted) {
                        oldEvent.delete()
                    }
                }

                AppPreferences.getInstance().onScheduleUpdated()

                val lbm = LocalBroadcastManager.getInstance(applicationContext)
                lbm.sendBroadcast(intent)
                return Result.success()
            } else {
                return Result.retry()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return Result.retry()
        }
    }
}