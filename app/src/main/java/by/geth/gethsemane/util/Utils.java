package by.geth.gethsemane.util;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.List;

import by.geth.gethsemane.R;
import by.geth.gethsemane.app.App;

public class Utils {
    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String buildBirthdaysMessage(Context context, List<String> birthdays) {
        Resources res = context.getResources();
        StringBuilder messageBuilder = new StringBuilder();
        String shortMessage = res.getQuantityString(R.plurals.notification_birthdays,
                birthdays.size(), birthdays.size());
        messageBuilder.append(shortMessage).append(':').append('\n');
        for (String birthday : birthdays) {
            messageBuilder.append('\n').append(birthday);
        }
        return messageBuilder.toString();
    }

    public static void setMenuItemEnabled(MenuItem item, boolean isEnabled) {
        if (item.isEnabled() != isEnabled) {
            item.setEnabled(isEnabled);
        }
    }

    public static boolean isNotificationPolicyAccessGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManager notificationManager = (NotificationManager) App.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            return notificationManager != null && notificationManager.isNotificationPolicyAccessGranted();
        } else {
            return true;
        }
    }

    public static int dpToPx(int dp) {
        float density = App.getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }

    public static int pxToDp(int px) {
        float density = App.getContext().getResources().getDisplayMetrics().density;
        return (int) (px / density);
    }

    public static int getScreenWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            view.clearFocus();
        }
    }
}
