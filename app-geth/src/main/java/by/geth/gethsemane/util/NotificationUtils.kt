package by.geth.gethsemane.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import by.geth.gethsemane.app.App
import by.geth.gethsemane.app.AppPreferences
import by.geth.gethsemane.receiver.BirthdaysNotificationReceiver
import java.util.*

object NotificationUtils {

    fun registerBirthdayNotifications() {
        val timeStr = AppPreferences.getInstance().birthdayNotifTime
        val parts = timeStr.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val hours = Integer.valueOf(parts[0])
        val minutes = Integer.valueOf(parts[1])
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hours)
        calendar.set(Calendar.MINUTE, minutes)
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val intent = Intent(App.getContext(), BirthdaysNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(App.getContext(), 0, intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        val alarmManager = App.getContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY, pendingIntent)
    }

    fun cancelBirthdayNotifications() {
        val intent = Intent(App.getContext(), BirthdaysNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(App.getContext(), 0, intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        val alarmManager = App.getContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

}