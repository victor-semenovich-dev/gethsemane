package by.geth.gethsemane.service

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import by.geth.gethsemane.api.Server

class SyncDataWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    companion object {
        const val TAG = "SyncDataWorker"
    }

    override fun doWork(): Result {
        var isSuccess = true
        isSuccess = isSuccess && (Server.getBirthdays(isSync = true) != null)
        isSuccess = isSuccess && (Server.getNewsList(isSync = true) != null)
        isSuccess = isSuccess && (Server.getWorshipNotesList(isSync = true) != null)
        return if (isSuccess) Result.success() else Result.retry()
    }
}