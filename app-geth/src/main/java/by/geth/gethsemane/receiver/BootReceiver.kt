package by.geth.gethsemane.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import by.geth.gethsemane.app.AppPreferences
import by.geth.gethsemane.util.NotificationUtils

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED && AppPreferences.getInstance().isBirthdayNotifEnabled) {
            NotificationUtils.registerBirthdayNotifications()
        }
    }
}