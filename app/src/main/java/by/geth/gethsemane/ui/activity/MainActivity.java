package by.geth.gethsemane.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.activeandroid.query.Select;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import by.geth.gethsemane.BuildConfig;
import by.geth.gethsemane.R;
import by.geth.gethsemane.api.Server;
import by.geth.gethsemane.app.AppPreferences;
import by.geth.gethsemane.data.Event;
import by.geth.gethsemane.data.MusicGroup;
import by.geth.gethsemane.data.model.Birthday;
import by.geth.gethsemane.data.model.articles.ArticleListItem;
import by.geth.gethsemane.data.model.news.NewsListItem;
import by.geth.gethsemane.service.FirebaseMessagingService;
import by.geth.gethsemane.ui.controller.AudioController;
import by.geth.gethsemane.ui.controller.DeepLinksController;
import by.geth.gethsemane.ui.fragment.BirthdaysFragment;
import by.geth.gethsemane.ui.fragment.EventChangesFragment;
import by.geth.gethsemane.ui.fragment.EventListFragment;
import by.geth.gethsemane.ui.fragment.init.InitFragment;
import by.geth.gethsemane.ui.fragment.WorshipListFragment;
import by.geth.gethsemane.ui.fragment.WorshipPagerFragment;
import by.geth.gethsemane.ui.fragment.article.ArticleDetailsFragment;
import by.geth.gethsemane.ui.fragment.article.ToSeeChristListFragment;
import by.geth.gethsemane.ui.fragment.article.WorshipNotesListFragment;
import by.geth.gethsemane.ui.fragment.base.BaseFragment;
import by.geth.gethsemane.ui.fragment.downloads.DownloadsFragment;
import by.geth.gethsemane.ui.fragment.gallery.GalleryFragment;
import by.geth.gethsemane.ui.fragment.news.NewsDetailsFragment;
import by.geth.gethsemane.ui.fragment.news.NewsListFragment;
import by.geth.gethsemane.ui.fragment.psalms.MusicGroupsFragment;
import by.geth.gethsemane.ui.fragment.sermons.SermonListFragment;
import by.geth.gethsemane.ui.fragment.sermons.WitnessListFragment;
import by.geth.gethsemane.util.DBUtils;
import by.geth.gethsemane.util.FileUtils;
import by.geth.gethsemane.util.NotificationUtils;
import by.geth.gethsemane.util.WorkerUtils;

public class MainActivity extends AppCompatActivity implements InitFragment.DataInitListener,
        NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String ACTION_BIRTHDAYS = "ACTION_BIRTHDAYS";
    public static final String ACTION_NEWS = "ACTION_NEWS";
    public static final String ACTION_WORSHIP_NOTES = "ACTION_WORSHIP_NOTES";
    public static final String ACTION_TO_SEE_CHRIST = "ACTION_TO_SEE_CHRIST";

    public static final String EXTRA_SCHEDULE_EVENTS_ADDED = "EXTRA_SCHEDULE_EVENTS_ADDED";
    public static final String EXTRA_SCHEDULE_EVENTS_DELETED = "EXTRA_SCHEDULE_EVENTS_DELETED";
    public static final String EXTRA_SCHEDULE_EVENTS_MODIFIED_OLD = "EXTRA_SCHEDULE_EVENTS_MODIFIED_OLD";
    public static final String EXTRA_SCHEDULE_EVENTS_MODIFIED_NEW = "EXTRA_SCHEDULE_EVENTS_MODIFIED_NEW";
    public static final String EXTRA_SHOW_LIVE_VIDEO = "EXTRA_SHOW_LIVE_VIDEO";
    public static final String EXTRA_ID = "EXTRA_ID";

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    @SuppressWarnings("FieldCanBeLocal")
    private NavigationView mNavigationView;

    private AudioController mAudioController;
    private DeepLinksController mDeepLinksController;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    isGranted -> {});

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.activity_main_navigation_drawer_open, R.string.activity_main_navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerToggle.syncState();

        mNavigationView = findViewById(R.id.left_drawer);
        mNavigationView.setNavigationItemSelectedListener(this);

        mAudioController = new AudioController(this);
        mAudioController.onCreate();
        mDeepLinksController = new DeepLinksController(this);

		if (getSupportFragmentManager().getFragments() == null || getSupportFragmentManager().getFragments().isEmpty()) {
			if (DBUtils.isDataInitialized()) {
				showMainFragment();
			} else {
				Fragment initFragment = new InitFragment();
				getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.fragment_container, initFragment)
						.commit();
			}
		}

		if (savedInstanceState == null) {
            onNewIntent(getIntent());
        }

        WorkerUtils.INSTANCE.registerDataUpdates(this);

        if (AppPreferences.getInstance().isBirthdayNotifEnabled()) {
            NotificationUtils.INSTANCE.registerBirthdayNotifications();
        } else {
            NotificationUtils.INSTANCE.cancelBirthdayNotifications();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String action = intent.getAction();
        long id = intent.getLongExtra(EXTRA_ID, 0);
        if (action != null) {
            switch (action) {
                case ACTION_BIRTHDAYS: {
                    showBirthdaysFragment();
                    return;
                }
                case ACTION_NEWS: {
                    showFragment(NewsDetailsFragment.Companion.newInstance(id), false);
                    return;
                }
                case ACTION_WORSHIP_NOTES: {
                    showFragment(ArticleDetailsFragment.Companion.newInstance(id,
                        ArticleDetailsFragment.BASE_URL_WORSHIP_NOTES), false);
                    return;
                }
                case ACTION_TO_SEE_CHRIST: {
                    showFragment(ArticleDetailsFragment.Companion.newInstance(id,
                            ArticleDetailsFragment.BASE_URL_TO_SEE_CHRIST), false);
                    return;
                }
            }
        }

        ArrayList<Event> addedEvents = intent.getParcelableArrayListExtra(EXTRA_SCHEDULE_EVENTS_ADDED);
        ArrayList<Event> deletedEvents = intent.getParcelableArrayListExtra(EXTRA_SCHEDULE_EVENTS_DELETED);
        ArrayList<Event> modifiedOldEvents = intent.getParcelableArrayListExtra(EXTRA_SCHEDULE_EVENTS_MODIFIED_OLD);
        ArrayList<Event> modifiedNewEvents = intent.getParcelableArrayListExtra(EXTRA_SCHEDULE_EVENTS_MODIFIED_NEW);
        if (addedEvents != null && deletedEvents != null && modifiedOldEvents != null && modifiedNewEvents != null) {
            int count = addedEvents.size() + deletedEvents.size() + modifiedOldEvents.size();
            if (count > 0) {
                showEventChangesFragment(addedEvents, deletedEvents, modifiedOldEvents, modifiedNewEvents);
                return;
            }
        }

        if (intent.getBooleanExtra(EXTRA_SHOW_LIVE_VIDEO, false)) {
            showLiveVideo();
            return;
        }

        mDeepLinksController.onNewIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAudioController.onStart();

        registerFcm();

        if (BuildConfig.DB_DUMP_ENABLED) {
            FileUtils.dumbDBFile(this);
        }

        if (!(new Select().from(NewsListItem.class).exists()))
            Server.INSTANCE.getNewsList(1, 20, false);
        if (!(new Select().from(ArticleListItem.class)
            .where(ArticleListItem.COLUMN_CATEGORY + " = '" + ArticleListItem.CATEGORY_WORSHIP_NOTES + "'")
            .exists()))
            Server.INSTANCE.getWorshipNotesList(1, 20, false);
        if (!(new Select().from(ArticleListItem.class)
            .where(ArticleListItem.COLUMN_CATEGORY + " = '" + ArticleListItem.CATEGORY_TO_SEE_CHRIST + "'")
            .exists()))
            Server.INSTANCE.getToSeeChristList(1, 20, false);
        if (!(new Select().from(Birthday.class).exists()))
            Server.INSTANCE.getBirthdays(false);
        if (!(new Select().from(MusicGroup.class).exists()))
            Server.INSTANCE.getMusicGroups(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    @Override
    protected void onStop() {
        mAudioController.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mDrawerLayout.removeDrawerListener(mDrawerToggle);
        mAudioController.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerToggle.isDrawerIndicatorEnabled()) {
                    if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                    } else {
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                } else {
                    onBackPressed();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Fragment baseFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            try {
                if (baseFragment.getChildFragmentManager().getBackStackEntryCount() > 0) {
                    baseFragment.getChildFragmentManager().popBackStack();
                } else {
                    super.onBackPressed();
                }
            } catch (Exception e) {
                e.printStackTrace();
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDataInitSuccess() {
        showMainFragment();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_main_navigation_worships:
                showWorshipListFragment();
                break;
            case R.id.activity_main_navigation_sermons:
                showSermonListFragment();
                break;
            case R.id.activity_main_navigation_witnesses:
                showWitnessListFragment();
                break;
            case R.id.activity_main_navigation_psalms:
                showFragment(MusicGroupsFragment.newInstance());
                break;
            case R.id.activity_main_navigation_downloads:
                showFragment(DownloadsFragment.Companion.newInstance());
                break;
            case R.id.activity_main_navigation_schedule:
                showEventListFragment();
                break;
            case R.id.activity_main_navigation_birthdays:
                showBirthdaysFragment();
                break;
            case R.id.activity_main_navigation_gallery:
                showGalleryFragment();
                break;
            case R.id.activity_main_navigation_settings:
                PreferenceActivity.start(this);
                break;
            case R.id.activity_main_navigation_news:
                showFragment(NewsListFragment.Companion.newInstance());
                break;
            case R.id.activity_main_navigation_worship_notes:
                showFragment(WorshipNotesListFragment.Companion.newInstance());
                break;
            case R.id.activity_main_navigation_to_see_christ:
                showFragment(ToSeeChristListFragment.Companion.newInstance());
                break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setDrawerIndicatorEnabled(boolean isEnabled) {
        mDrawerToggle.setDrawerIndicatorEnabled(isEnabled);
    }

    private void showLiveVideo() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(String.format("https://www.youtube.com/channel/%s", BuildConfig.YOUTUBE_CHANNEL_ID)));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMainFragment() {
        Fragment mainFragment = new WorshipPagerFragment();
        mainFragment.getArguments().putInt(BaseFragment.ARGS_UI_FLAGS,
                BaseFragment.UI_FLAG_DISPLAY_HOME_AS_UP |
                BaseFragment.UI_FLAG_DRAWER_INDICATOR_ENABLED);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, mainFragment)
                .commit();
    }

    private void showWorshipListFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right_short, R.anim.slide_out_left_short,
                        R.anim.slide_in_left_short, R.anim.slide_out_right_short)
                .replace(R.id.fragment_container, WorshipListFragment.newInstance())
                .addToBackStack(null)
                .commit();
    }

    public void showFragment(Fragment fragment) {
        showFragment(fragment, true);
    }

    public void showFragment(Fragment fragment, boolean isAnimate) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (isAnimate) {
            transaction.setCustomAnimations(
                R.anim.slide_in_right_short, R.anim.slide_out_left_short,
                R.anim.slide_in_left_short, R.anim.slide_out_right_short);
        }
        transaction.replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
    }

    private void showSermonListFragment() {
        Fragment sermonListFragment = new SermonListFragment();
        sermonListFragment.getArguments().putInt(BaseFragment.ARGS_AB_TITLE_RES_ID,
                R.string.fragment_sermon_list_ab_title);
        sermonListFragment.getArguments().putInt(BaseFragment.ARGS_UI_FLAGS,
                BaseFragment.UI_FLAG_DISPLAY_HOME_AS_UP);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right_short, R.anim.slide_out_left_short,
                        R.anim.slide_in_left_short, R.anim.slide_out_right_short)
                .replace(R.id.fragment_container, sermonListFragment)
                .addToBackStack(null)
                .commit();
    }

    private void showWitnessListFragment() {
        Fragment witnessListFragment = new WitnessListFragment();
        witnessListFragment.getArguments().putInt(BaseFragment.ARGS_AB_TITLE_RES_ID,
                R.string.fragment_witness_list_ab_title);
        witnessListFragment.getArguments().putInt(BaseFragment.ARGS_UI_FLAGS,
                BaseFragment.UI_FLAG_DISPLAY_HOME_AS_UP);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right_short, R.anim.slide_out_left_short,
                        R.anim.slide_in_left_short, R.anim.slide_out_right_short)
                .replace(R.id.fragment_container, witnessListFragment)
                .addToBackStack(null)
                .commit();
    }

    private void showEventListFragment() {
        Fragment eventListFragment = new EventListFragment();
        eventListFragment.getArguments().putInt(BaseFragment.ARGS_AB_TITLE_RES_ID,
                R.string.fragment_event_list_ab_title);
        eventListFragment.getArguments().putInt(BaseFragment.ARGS_UI_FLAGS,
                BaseFragment.UI_FLAG_DISPLAY_HOME_AS_UP);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right_short, R.anim.slide_out_left_short,
                        R.anim.slide_in_left_short, R.anim.slide_out_right_short)
                .replace(R.id.fragment_container, eventListFragment)
                .addToBackStack(null)
                .commit();
    }

    public void showEventChangesFragment(ArrayList<Event> addedEvents, ArrayList<Event> deletedEvents,
                                          ArrayList<Event> modifiedOldEvents, ArrayList<Event> modifiedNewEvents) {
        Fragment eventChangesFragment = new EventChangesFragment();

        eventChangesFragment.getArguments().putInt(BaseFragment.ARGS_AB_TITLE_RES_ID,
                R.string.fragment_event_changes_ab_title);
        eventChangesFragment.getArguments().putInt(BaseFragment.ARGS_UI_FLAGS,
                BaseFragment.UI_FLAG_DISPLAY_HOME_AS_UP);

        eventChangesFragment.getArguments().putParcelableArrayList(
                EventChangesFragment.ARGS_ADDED_EVENTS, addedEvents);
        eventChangesFragment.getArguments().putParcelableArrayList(
                EventChangesFragment.ARGS_DELETED_EVENTS, deletedEvents);
        eventChangesFragment.getArguments().putParcelableArrayList(
                EventChangesFragment.ARGS_MODIFIED_OLD_EVENTS, modifiedOldEvents);
        eventChangesFragment.getArguments().putParcelableArrayList(
                EventChangesFragment.ARGS_MODIFIED_NEW_EVENTS, modifiedNewEvents);

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right_short, R.anim.slide_out_left_short,
                        R.anim.slide_in_left_short, R.anim.slide_out_right_short)
                .replace(R.id.fragment_container, eventChangesFragment)
                .addToBackStack(null)
                .commit();
    }

    public void showBirthdaysFragment() {
        Fragment birthdaysFragment = new BirthdaysFragment();
        birthdaysFragment.getArguments().putInt(BaseFragment.ARGS_AB_TITLE_RES_ID,
                R.string.birthdays_title);
        birthdaysFragment.getArguments().putInt(BaseFragment.ARGS_UI_FLAGS,
                BaseFragment.UI_FLAG_DISPLAY_HOME_AS_UP);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right_short, R.anim.slide_out_left_short,
                        R.anim.slide_in_left_short, R.anim.slide_out_right_short)
                .replace(R.id.fragment_container, birthdaysFragment)
                .addToBackStack(null)
                .commit();
    }

    public void showGalleryFragment() {
        Fragment galleryFragment = new GalleryFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right_short, R.anim.slide_out_left_short,
                        R.anim.slide_in_left_short, R.anim.slide_out_right_short)
                .replace(R.id.fragment_container, galleryFragment)
                .addToBackStack(null)
                .commit();
    }

    private void registerFcm() {
        try {
            int googleServicesStatus = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
            if (googleServicesStatus == ConnectionResult.SUCCESS) {
                if (BuildConfig.DEBUG) {
                    FirebaseMessaging.getInstance().subscribeToTopic(FirebaseMessagingService.NEWS_DEV_TOPIC);
                    FirebaseMessaging.getInstance().subscribeToTopic(FirebaseMessagingService.WORSHIP_NOTES_DEV_TOPIC);
                    FirebaseMessaging.getInstance().subscribeToTopic(FirebaseMessagingService.PHOTOS_DEV_TOPIC);
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(FirebaseMessagingService.NEWS_DEV_TOPIC);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(FirebaseMessagingService.WORSHIP_NOTES_DEV_TOPIC);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(FirebaseMessagingService.PHOTOS_DEV_TOPIC);
                }

                FirebaseMessaging.getInstance().subscribeToTopic(FirebaseMessagingService.LIVE_TOPIC);
                FirebaseMessaging.getInstance().subscribeToTopic(FirebaseMessagingService.SCHEDULE_TOPIC);
                FirebaseMessaging.getInstance().subscribeToTopic(FirebaseMessagingService.NEWS_TOPIC);
                FirebaseMessaging.getInstance().subscribeToTopic(FirebaseMessagingService.WORSHIP_NOTES_TOPIC);
                FirebaseMessaging.getInstance().subscribeToTopic(FirebaseMessagingService.TO_SEE_CHRIST_TOPIC);
                FirebaseMessaging.getInstance().subscribeToTopic(FirebaseMessagingService.PHOTOS_TOPIC);

                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task ->
                        Log.d(TAG, "push token: " + task.getResult()));
            } else {
                GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
