package by.geth.gethsemane.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import by.geth.gethsemane.R;
import by.geth.gethsemane.app.AppPreferences;
import by.geth.gethsemane.util.NotificationUtils;

public class PreferenceActivity extends AppCompatActivity {
    public static void start(Context context) {
        Intent intent = new Intent(context, PreferenceActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.pref_ab_title);

        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new PreferenceFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class PreferenceFragment extends android.preference.PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs);
        }

        @Override
        public void onStart() {
            super.onStart();
            AppPreferences.getInstance().addListener(mListener);
        }

        @Override
        public void onStop() {
            super.onStop();
            AppPreferences.getInstance().removeListener(mListener);
        }

        private SharedPreferences.OnSharedPreferenceChangeListener mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (getString(R.string.pref_notif_birthday_key).equals(key)) {
                    if (AppPreferences.getInstance().isBirthdayNotifEnabled()) {
                        NotificationUtils.INSTANCE.registerBirthdayNotifications();
                    } else {
                        NotificationUtils.INSTANCE.cancelBirthdayNotifications();
                    }
                } else if (getString(R.string.pref_notif_birthday_time_key).equals(key)) {
                    if (AppPreferences.getInstance().isBirthdayNotifEnabled()) {
                        NotificationUtils.INSTANCE.registerBirthdayNotifications();
                    }
                }
            }
        };
    }
}
