package by.geth.gethsemane.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import by.geth.gethsemane.app.AppNotificationManager
import by.geth.gethsemane.app.AppPreferences
import by.geth.gethsemane.data.model.Birthday
import com.activeandroid.query.Select
import java.util.*

class BirthdaysNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val from = Select()
                .from(Birthday::class.java)
                .where(Birthday.COLUMN_MONTH + " = ? AND " + Birthday.COLUMN_DAY + " = ?",
                        month, day)
        if (from.exists() && AppPreferences.getInstance().isBirthdayNotifEnabled) {
            val birthday = from.executeSingle<Birthday>()
            AppNotificationManager.getInstance().notifyBirthdays(birthday.persons)
        }
    }
}