package by.geth.gethsemane.download

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import by.geth.gethsemane.ui.activity.MainActivity

class DownloadNotificationClickedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.startActivity(
                Intent(context, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}