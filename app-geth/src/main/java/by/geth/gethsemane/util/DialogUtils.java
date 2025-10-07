package by.geth.gethsemane.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import by.geth.gethsemane.R;
import by.geth.gethsemane.app.AppPreferences;
import by.geth.gethsemane.ui.dialog.DialogBuilder;
import by.geth.gethsemane.ui.listener.OnClickListener;

public class DialogUtils {
    public static void showAlertDialog(Context context, int messageResId) {
        showAlertDialog(context, messageResId, null);
    }

    public static void showAlertDialog(Context context, int messageResId,
                                       DialogInterface.OnClickListener onClick) {
        new AlertDialog.Builder(context)
                .setMessage(messageResId)
                .setPositiveButton(R.string.dialog_ok, onClick)
                .show();
    }

    public static void showCellularAlertDialog(Context context, final OnClickListener positiveClickListener) {
        new DialogBuilder(context)
                .setMessage(R.string.warning_cellular)
                .setPositiveButton(R.string.dialog_yes, new DialogBuilder.OnClickListener() {
                    @Override
                    public void onClick(boolean dontAskAgain) {
                        positiveClickListener.onClick();
                        if (dontAskAgain) {
                            AppPreferences.getInstance().setPlayViaWifiOnly(false);
                        }
                    }
                })
                .setNeutralButton(R.string.dialog_cancel, null)
                .addDontAskAgainOption()
                .show();
    }

    public static void showAudioNotDownloadedDialog(Context context,
                                                    final OnClickListener onPlayOnlineClick,
                                                    final OnClickListener onDownloadClick) {
        new DialogBuilder(context)
                .setMessage(R.string.warning_audio_not_downloaded_message)
                .setPositiveButton(R.string.warning_audio_not_downloaded_option_download, new DialogBuilder.OnClickListener() {
                    @Override
                    public void onClick(boolean dontAskAgain) {
                        onDownloadClick.onClick();
                        if (dontAskAgain) {
                            AppPreferences.getInstance().setAutoDownloadAudioOnPlay();
                        }
                    }
                })
                .setNegativeButton(R.string.warning_audio_not_downloaded_option_play, new DialogBuilder.OnClickListener() {
                    @Override
                    public void onClick(boolean dontAskAgain) {
                        onPlayOnlineClick.onClick();
                        if (dontAskAgain) {
                            AppPreferences.getInstance().setAutoPlayNotDownloadedAudio();
                        }
                    }
                })
                .addDontAskAgainOption()
                .show();
    }

    public static void showPermissionsSettingsDialog(final Context context, @StringRes int messageResId) {
        new AlertDialog.Builder(context)
                .setMessage(messageResId)
                .setPositiveButton(R.string.dialog_settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                        context.startActivity(intent);
                    }
                })
                .setNeutralButton(R.string.dialog_cancel, null)
                .show();
    }
}
