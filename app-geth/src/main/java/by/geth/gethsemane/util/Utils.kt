package by.geth.gethsemane.util

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.core.content.FileProvider
import by.geth.gethsemane.BuildConfig
import by.geth.gethsemane.R
import by.geth.gethsemane.data.base.AudioItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

val Int.dpToPx: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.pxToDp: Int get() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun <T> debounce(
    waitMs: Long = 300L,
    coroutineScope: CoroutineScope,
    destinationFunction: (T) -> Unit
): (T) -> Unit {
    var debounceJob: Job? = null
    return { param: T ->
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            delay(waitMs)
            destinationFunction(param)
        }
    }
}

fun share(item: AudioItem, context: Context) {
    item.getLocalPath()?.let { path ->
        val file = File(path)
        val uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file)

        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_TEXT, item.getTitle())
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.type = "*/*"

        context.startActivity(Intent.createChooser(shareIntent, context.resources.getText(R.string.send_to)))
    }
}
