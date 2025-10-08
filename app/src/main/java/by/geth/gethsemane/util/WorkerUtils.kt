package by.geth.gethsemane.util

import android.content.Context
import androidx.work.*
import by.geth.gethsemane.service.SyncDataWorker
import java.util.concurrent.TimeUnit

object WorkerUtils {

    fun registerDataUpdates(context: Context) {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val syncRequest = PeriodicWorkRequestBuilder<SyncDataWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints).build()
        WorkManager.getInstance(context.applicationContext).enqueueUniquePeriodicWork(
                "sync", ExistingPeriodicWorkPolicy.KEEP, syncRequest)
    }

}