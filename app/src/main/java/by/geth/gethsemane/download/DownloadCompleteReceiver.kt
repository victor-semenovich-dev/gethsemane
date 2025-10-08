package by.geth.gethsemane.download

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import by.geth.gethsemane.data.*
import com.activeandroid.query.Update

class DownloadCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadController = DownloadController.getInstance()
        val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)

        val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadId))

        val event = downloadController.eventIdSparseArray.get(downloadId)
        val worship = downloadController.worshipIdSparseArray.get(downloadId)
        val sermon = downloadController.sermonIdSparseArray.get(downloadId)
        val witness = downloadController.witnessIdSparseArray.get(downloadId)
        val song = downloadController.songIdSparseArray.get(downloadId)
        val photo = downloadController.photoIdSparseArray.get(downloadId)

        when {
            event   != null -> onEventCompleted(event, context, cursor, downloadId)
            worship != null -> onWorshipCompleted(worship, context, cursor, downloadId)
            sermon  != null -> onSermonCompleted(sermon, context, cursor, downloadId)
            witness != null -> onWitnessCompleted(witness, context, cursor, downloadId)
            song    != null -> onSongCompleted(song, context, cursor, downloadId)
            photo   != null -> onPhotoCompleted(photo, context, cursor, downloadId)
        }

        cursor.close()
    }

    private fun onEventCompleted(event: Event, context: Context, cursor: Cursor, downloadId: Long) {
        if (cursor.moveToNext()) {
            when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                DownloadManager.STATUS_SUCCESSFUL -> {
                    val localUri = Uri.parse(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)))
                    event.audioLocalUri = localUri.path
                    event.save()
                    Update(Worship::class.java)
                            .set(Worship.COLUMN_AUDIO_LOCAL + " = ?", localUri)
                            .where(Worship.COLUMN_ID + " = ?", event.externalId())
                            .execute()
                    DownloadController.getInstance().eventIdSparseArray.remove(downloadId)
                    notifyItemDownloadFinished(context, DownloadController.ItemType.EVENT, event.externalId(), localUri.path)
                    notifyItemDownloadFinished(context, DownloadController.ItemType.WORSHIP, event.externalId(), localUri.path)
                    if (DownloadController.getInstance().autoPlayItem == event) {
                        DownloadController.getInstance().autoPlay()
                    }
                }
                DownloadManager.STATUS_FAILED -> {
                    DownloadController.getInstance().eventIdSparseArray.remove(downloadId)
                    notifyItemDownloadError(context, DownloadController.ItemType.EVENT, event.externalId())
                    notifyItemDownloadError(context, DownloadController.ItemType.WORSHIP, event.externalId())
                }
            }
        } else {
            DownloadController.getInstance().eventIdSparseArray.remove(downloadId)
            notifyItemDownloadCancelled(context, DownloadController.ItemType.EVENT, event.externalId())
            notifyItemDownloadCancelled(context, DownloadController.ItemType.WORSHIP, event.externalId())
        }
    }

    private fun onWorshipCompleted(worship: Worship, context: Context, cursor: Cursor, downloadId: Long) {
        if (cursor.moveToNext()) {
            when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                DownloadManager.STATUS_SUCCESSFUL -> {
                    val localUri = Uri.parse(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)))
                    worship.audioLocalUri = localUri.path
                    worship.save()
                    Update(Event::class.java)
                            .set(Event.COLUMN_AUDIO_LOCAL + " = ?", localUri)
                            .where(Event.COLUMN_ID + " = ?", worship.externalId)
                            .execute()
                    DownloadController.getInstance().worshipIdSparseArray.remove(downloadId)
                    notifyItemDownloadFinished(context, DownloadController.ItemType.EVENT, worship.externalId, localUri.path)
                    notifyItemDownloadFinished(context, DownloadController.ItemType.WORSHIP, worship.externalId, localUri.path)
                    if (DownloadController.getInstance().autoPlayItem == worship) {
                        DownloadController.getInstance().autoPlay()
                    }
                }
                DownloadManager.STATUS_FAILED -> {
                    DownloadController.getInstance().worshipIdSparseArray.remove(downloadId)
                    notifyItemDownloadError(context, DownloadController.ItemType.EVENT, worship.externalId)
                    notifyItemDownloadError(context, DownloadController.ItemType.WORSHIP, worship.externalId)
                }
            }
        } else {
            DownloadController.getInstance().worshipIdSparseArray.remove(downloadId)
            notifyItemDownloadCancelled(context, DownloadController.ItemType.EVENT, worship.externalId)
            notifyItemDownloadCancelled(context, DownloadController.ItemType.WORSHIP, worship.externalId)
        }
    }

    private fun onSermonCompleted(sermon: Sermon, context: Context, cursor: Cursor, downloadId: Long) {
        if (cursor.moveToNext()) {
            when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                DownloadManager.STATUS_SUCCESSFUL -> {
                    val localUri = Uri.parse(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)))
                    sermon.audioLocalUri = localUri.path
                    sermon.save()
                    DownloadController.getInstance().sermonIdSparseArray.remove(downloadId)
                    notifyItemDownloadFinished(context, DownloadController.ItemType.SERMON, sermon.externalId, localUri.path)
                    if (DownloadController.getInstance().autoPlayItem == sermon) {
                        DownloadController.getInstance().autoPlay()
                    }
                }
                DownloadManager.STATUS_FAILED -> {
                    DownloadController.getInstance().sermonIdSparseArray.remove(downloadId)
                    notifyItemDownloadError(context, DownloadController.ItemType.SERMON, sermon.externalId)
                }
            }
        } else {
            DownloadController.getInstance().sermonIdSparseArray.remove(downloadId)
            notifyItemDownloadCancelled(context, DownloadController.ItemType.SERMON, sermon.externalId)
        }
    }

    private fun onWitnessCompleted(witness: Witness, context: Context, cursor: Cursor, downloadId: Long) {
        if (cursor.moveToNext()) {
            when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                DownloadManager.STATUS_SUCCESSFUL -> {
                    val localUri = Uri.parse(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)))
                    witness.audioLocalUri = localUri.path
                    witness.save()
                    DownloadController.getInstance().witnessIdSparseArray.remove(downloadId)
                    notifyItemDownloadFinished(context, DownloadController.ItemType.WITNESS, witness.externalId, localUri.path)
                    if (DownloadController.getInstance().autoPlayItem == witness) {
                        DownloadController.getInstance().autoPlay()
                    }
                }
                DownloadManager.STATUS_FAILED -> {
                    DownloadController.getInstance().witnessIdSparseArray.remove(downloadId)
                    notifyItemDownloadError(context, DownloadController.ItemType.WITNESS, witness.externalId)
                }
            }
        } else {
            DownloadController.getInstance().witnessIdSparseArray.remove(downloadId)
            notifyItemDownloadCancelled(context, DownloadController.ItemType.WITNESS, witness.externalId)
        }
    }

    private fun onSongCompleted(song: Song, context: Context, cursor: Cursor, downloadId: Long) {
        if (cursor.moveToNext()) {
            when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                DownloadManager.STATUS_SUCCESSFUL -> {
                    val localUri = Uri.parse(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)))
                    song.audioLocalUri = localUri.path
                    song.save()
                    DownloadController.getInstance().songIdSparseArray.remove(downloadId)
                    notifyItemDownloadFinished(context, DownloadController.ItemType.SONG, song.externalId(), localUri.path)
                    if (DownloadController.getInstance().autoPlayItem == song) {
                        DownloadController.getInstance().autoPlay()
                    }
                }
                DownloadManager.STATUS_FAILED -> {
                    DownloadController.getInstance().songIdSparseArray.remove(downloadId)
                    notifyItemDownloadError(context, DownloadController.ItemType.SONG, song.externalId())
                }
            }
        } else {
            DownloadController.getInstance().songIdSparseArray.remove(downloadId)
            notifyItemDownloadCancelled(context, DownloadController.ItemType.SONG, song.externalId())
        }
    }

    private fun onPhotoCompleted(photo: Photo, context: Context, cursor: Cursor, downloadId: Long) {
        if (cursor.moveToNext()) {
            when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                DownloadManager.STATUS_SUCCESSFUL -> {
                    val localUri = Uri.parse(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)))
                    photo.filePath = localUri.path
                    photo.save()
                    DownloadController.getInstance().photoIdSparseArray.remove(downloadId)
                    notifyItemDownloadFinished(context, DownloadController.ItemType.PHOTO, photo.backendId, localUri.path)
                }
                DownloadManager.STATUS_FAILED -> {
                    DownloadController.getInstance().photoIdSparseArray.remove(downloadId)
                    notifyItemDownloadError(context, DownloadController.ItemType.PHOTO, photo.backendId)
                }
            }
        } else {
            DownloadController.getInstance().photoIdSparseArray.remove(downloadId)
            notifyItemDownloadCancelled(context, DownloadController.ItemType.PHOTO, photo.backendId)
        }
    }

    private fun notifyItemDownloadFinished(context: Context, itemType: DownloadController.ItemType, itemId: Long, filePath: String?) {
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(context).sendBroadcast(
                Intent(DownloadController.ACTION_ITEM_DOWNLOAD_FINISHED)
                        .putExtra(DownloadController.EXTRA_ITEM_TYPE, itemType)
                        .putExtra(DownloadController.EXTRA_ITEM_ID, itemId)
                        .putExtra(DownloadController.EXTRA_FILE_PATH, filePath))
    }

    private fun notifyItemDownloadCancelled(context: Context, itemType: DownloadController.ItemType, itemId: Long) {
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(context).sendBroadcast(
                Intent(DownloadController.ACTION_ITEM_DOWNLOAD_CANCELLED)
                        .putExtra(DownloadController.EXTRA_ITEM_TYPE, itemType)
                        .putExtra(DownloadController.EXTRA_ITEM_ID, itemId))
    }

    private fun notifyItemDownloadError(context: Context, itemType: DownloadController.ItemType, itemId: Long) {
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(context).sendBroadcast(
                Intent(DownloadController.ACTION_ITEM_DOWNLOAD_ERROR)
                        .putExtra(DownloadController.EXTRA_ITEM_TYPE, itemType)
                        .putExtra(DownloadController.EXTRA_ITEM_ID, itemId))
    }
}