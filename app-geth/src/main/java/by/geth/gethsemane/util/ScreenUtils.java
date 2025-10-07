package by.geth.gethsemane.util;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;

public class ScreenUtils {

	public static int getScreenHeight(Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		display.getMetrics(displayMetrics);
		return displayMetrics.heightPixels;
	}
}
