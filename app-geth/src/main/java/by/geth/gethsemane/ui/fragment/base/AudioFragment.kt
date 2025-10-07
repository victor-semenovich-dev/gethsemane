package by.geth.gethsemane.ui.fragment.base

import android.content.*
import android.os.IBinder
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import by.geth.gethsemane.R
import by.geth.gethsemane.app.AppPreferences
import by.geth.gethsemane.data.base.AudioItem
import by.geth.gethsemane.download.DownloadController
import by.geth.gethsemane.service.AudioService
import by.geth.gethsemane.util.ConnectionUtils
import by.geth.gethsemane.util.DialogUtils
import by.geth.gethsemane.util.FileUtils

abstract class AudioFragment : BaseFragment(), ServiceConnection {
    // --------------------------------------------------------------------------------------
    //
    // Private fields
    //
    // --------------------------------------------------------------------------------------
    private var mAudioService: AudioService.Binder? = null

    // --------------------------------------------------------------------------------------
    //
    // Overridden methods
    //
    // --------------------------------------------------------------------------------------
    override fun onStart() {
        super.onStart()
        val audioServiceIntent = Intent(context, AudioService::class.java)
        context!!.bindService(audioServiceIntent, this, Context.BIND_AUTO_CREATE)

        val lbm = androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(context!!)
        lbm.registerReceiver(mAudioReceiver, AudioService.getFullIntentFilter())
        lbm.registerReceiver(mDownloadReceiver, DownloadController.getFullIntentFilter())
    }

    override fun onStop() {
        super.onStop()
        context!!.unbindService(this)

        val lbm = androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(context!!)
        lbm.unregisterReceiver(mAudioReceiver)
        lbm.unregisterReceiver(mDownloadReceiver)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        mAudioService = service as AudioService.Binder
        updateAudioUI()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        mAudioService = null
    }

    // --------------------------------------------------------------------------------------
    //
    // Public methods
    //
    // --------------------------------------------------------------------------------------
    fun isInProgress(item: AudioItem): Boolean {
        return if (mAudioService != null) {
            val currentUri = mAudioService?.uri
            isInProgress() && (currentUri == item.getRemoteUrl() || currentUri == item.getLocalPath())
        } else {
            false
        }
    }

    fun playStop(item: AudioItem) {
        when {
            isInProgress(item) -> stop()
            isInProgress() -> playCheckingState(item) { stop(); mAudioService?.play(item) }
            else -> playCheckingState(item) { mAudioService?.play(item) }
        }
    }

    fun download(item: AudioItem) {
        download(item, false)
    }

    fun download(item: AudioItem, isAutoPlay: Boolean) {
        if (ConnectionUtils.isNetworkConnected(context)) {
            checkWifiConnectionAndPerform {
                if (ConnectionUtils.isNetworkConnected(context)) {
                    DownloadController.getInstance().download(item, isAutoPlay)
                } else {
                    Toast.makeText(context, R.string.download_error_no_internet, Toast.LENGTH_LONG).show()
                }
            }
        } else {
            DialogUtils.showAlertDialog(context, R.string.error_download_connection_missing)
        }
    }

    fun delete(item: AudioItem) {
        AlertDialog.Builder(context!!)
            .setMessage(R.string.download_delete_warning)
            .setPositiveButton(R.string.dialog_yes) { _, _ ->
                DownloadController.getInstance().delete(item)
            }
            .setNegativeButton(R.string.dialog_no, null)
            .show()
    }

    // --------------------------------------------------------------------------------------
    //
    // Abstract methods
    //
    // --------------------------------------------------------------------------------------
    protected abstract fun updateAudioUI()

    protected abstract fun updateDownloadUI()

    // --------------------------------------------------------------------------------------
    //
    // Private methods
    //
    // --------------------------------------------------------------------------------------
    private fun isInProgress(): Boolean = (mAudioService?.state == AudioService.State.PREPARE) ||
        (mAudioService?.state == AudioService.State.PLAY)

    private fun playCheckingState(item: AudioItem, task: () -> Unit) {
        if (item.getLocalPath() == null) {
            if (!DownloadController.getInstance().isDownloadInProgress(item)) {
                if (ConnectionUtils.isNetworkConnected(context)) {
                    when {
                        AppPreferences.getInstance().isShowAudioNotDownloadedWarning ->
                            DialogUtils.showAudioNotDownloadedDialog(context,
                                    { checkWifiConnectionAndPerform(task) },
                                    { checkWifiConnectionAndPerform { download(item, true) } }
                            )
                        AppPreferences.getInstance().isAutoPlayNotDownloadedAudio ->
                            checkWifiConnectionAndPerform(task)
                        AppPreferences.getInstance().isAutoDownloadAudioBeforePlay ->
                            checkWifiConnectionAndPerform { download(item, true) }
                    }
                } else {
                    DialogUtils.showAlertDialog(context, R.string.error_play_audio_connection_missing)
                }
            }
        } else {
            if (FileUtils.isFileExists(item)) {
                task()
            } else {
                DownloadController.getInstance().delete(item)
                AlertDialog.Builder(requireContext())
                        .setMessage(R.string.warning_file_not_exist)
                        .setPositiveButton(R.string.dialog_yes) { _, _ ->
                            DownloadController.getInstance().download(item, true)
                        }
                        .setNeutralButton(R.string.dialog_cancel, null)
                        .show()
            }
        }
    }

    private fun checkWifiConnectionAndPerform(task: () -> Unit) {
        if (ConnectionUtils.isCellular(context) && AppPreferences.getInstance().isPlayViaWifiOnly) {
            DialogUtils.showCellularAlertDialog(context) {
                task()
            }
        } else {
            task()
        }
    }

    private fun stop() { mAudioService?.stop() }

    // --------------------------------------------------------------------------------------
    //
    // Broadcast receivers
    //
    // --------------------------------------------------------------------------------------
    private val mAudioReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateAudioUI()
        }
    }

    private val mDownloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateDownloadUI()
        }
    }
}