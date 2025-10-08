package by.geth.gethsemane.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.activeandroid.query.Select;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import by.geth.gethsemane.R;
import by.geth.gethsemane.app.AppPreferences;
import by.geth.gethsemane.data.Event;
import by.geth.gethsemane.data.Worship;
import by.geth.gethsemane.service.ApiService;
import by.geth.gethsemane.ui.adapter.WorshipListAdapter;
import by.geth.gethsemane.ui.fragment.base.AudioFragment;
import by.geth.gethsemane.ui.fragment.base.BaseFragment;
import by.geth.gethsemane.ui.style.VerticalSpaceItemDecoration;
import by.geth.gethsemane.util.ConnectionUtils;

public class WorshipListFragment extends AudioFragment implements
        WorshipListAdapter.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    public static WorshipListFragment newInstance() {
        WorshipListFragment worshipListFragment = new WorshipListFragment();
        worshipListFragment.getArguments().putInt(BaseFragment.ARGS_AB_TITLE_RES_ID,
            R.string.fragment_worship_list_ab_title);
        worshipListFragment.getArguments().putInt(BaseFragment.ARGS_UI_FLAGS,
            BaseFragment.UI_FLAG_DISPLAY_HOME_AS_UP);
        return worshipListFragment;
    }

    private static final int WORSHIP_LOAD_TIMEOUT_MS = 1500;
    private static final long AUTO_UPDATE_INTERVAL_MILLIS = 24 * 60 * 60 * 1000; // 24 hours

    private List<Event> mEvents = new ArrayList<>();

    private SwipeRefreshLayout mRefreshLayout;
    private WorshipListAdapter mWorshipsAdapter;

    private Handler mHandler = new Handler();
    private long mWorshipLoadingId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEvents = new Select()
                .from(Event.class)
                .where("(" + Event.COLUMN_IS_ARCHIVE + " == 0) AND " +
						"(" + Event.COLUMN_IS_DRAFT + " == 0) AND " +
                        "(" + Event.COLUMN_CATEGORY_ID + " = ?)", Event.ID_CATEGORY_WORSHIP)
                .orderBy(Event.COLUMN_DATE + " DESC")
                .execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_worship_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRefreshLayout = view.findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(this);

        mWorshipsAdapter = new WorshipListAdapter();
        mWorshipsAdapter.setEvents(mEvents);
        mWorshipsAdapter.setOnClickListener(this);

        Resources res = getResources();
        int outerSpace = res.getDimensionPixelSize(R.dimen.space_8);

        RecyclerView recyclerView = view.findViewById(R.id.worships_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(0, outerSpace));
        recyclerView.setAdapter(mWorshipsAdapter);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getContext() == null) {
            return;
        }

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
        lbm.registerReceiver(mApiServiceReceiver, ApiService.getFullIntentFilter());

        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - AppPreferences.getInstance().getUpdateTimeWorships() > AUTO_UPDATE_INTERVAL_MILLIS)
            getEvents();
    }

    @Override
    public void onStop() {
        if (getContext() == null) {
            return;
        }

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
        lbm.unregisterReceiver(mApiServiceReceiver);

        super.onStop();
    }

    @Override
    public void onItemClick(Event event) {
        long id = event.externalId();
        Worship worship = new Select()
                .from(Worship.class)
                .where(Worship.COLUMN_ID + " = ?", id)
                .executeSingle();
        if (worship == null) {
            if (mWorshipLoadingId == 0) {
                Context context = getContext();
                if (ConnectionUtils.isNetworkConnected(context)) {
                    mWorshipLoadingId = id;
                    ApiService.getWorship(context, id);
                } else {
                    Toast.makeText(context, R.string.error_worship_load_connection_missing,
                            Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            openWorship(id);
        }
    }

    @Override
    public void onAudioClick(Event event) {
        playStop(event);
    }

    @Override
    public void onDownloadClick(Event event) {
        download(event);
    }

    @Override
    public void onDeleteClick(final Event event) {
        delete(event);
    }

    @Override
    public void onRefresh() {
        if (ConnectionUtils.isNetworkConnected(getContext())) {
            getEvents();
        } else {
            mRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), R.string.error_data_load_connection_missing, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void updateAudioUI() {
        mWorshipsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void updateDownloadUI() {
        mWorshipsAdapter.notifyDataSetChanged();
    }

    private void getEvents() {
        long lastUpdate = AppPreferences.getInstance().getUpdateTimeWorships();
        long threeMonths = (long)3 * 31 * 24 * 3600 * 1000;
        Date date = new Date(lastUpdate - threeMonths);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        ApiService.getEvents(getContext(), dateFormat.format(date));
    }

    private void openWorship(long id) {
        Fragment worshipPagerFragment = new WorshipPagerFragment();
        Bundle args = new Bundle();
        args.putInt(BaseFragment.ARGS_UI_FLAGS, BaseFragment.UI_FLAG_DISPLAY_HOME_AS_UP);
        args.putLong(WorshipPagerFragment.ARGS_WORSHIP_ID, id);
        worshipPagerFragment.setArguments(args);
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right_short, R.anim.slide_out_left_short,
                            R.anim.slide_in_left_short, R.anim.slide_out_right_short)
                    .replace(R.id.fragment_container, worshipPagerFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private BroadcastReceiver mApiServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String requestType = intent.getStringExtra(ApiService.EXTRA_REQUEST_TYPE);
            if (requestType.equals(ApiService.REQUEST_GET_EVENTS) && intent.getAction() != null) {
                switch (intent.getAction()) {
                    case ApiService.ACTION_REQUEST_STARTED:
                        mRefreshLayout.setRefreshing(true);
                        break;
                    case ApiService.ACTION_REQUEST_SUCCESSFUL:
                        mRefreshLayout.setRefreshing(false);

                        mEvents = new Select()
                                .from(Event.class)
                                .where("(" + Event.COLUMN_IS_ARCHIVE + " == 0) AND " +
                                        "(" + Event.COLUMN_IS_DRAFT + " == 0) AND " +
                                        "(" + Event.COLUMN_CATEGORY_ID + " = ?)", Event.ID_CATEGORY_WORSHIP)
                                .orderBy(Event.COLUMN_DATE + " DESC")
                                .execute();
                        mWorshipsAdapter.setEvents(mEvents);
                        mWorshipsAdapter.notifyDataSetChanged();
                        break;
                    case ApiService.ACTION_REQUEST_ERROR:
                        mRefreshLayout.setRefreshing(false);
                        Toast.makeText(getContext(), R.string.error_data_load, Toast.LENGTH_LONG).show();
                        break;
                }
            }
            if (requestType.equals(ApiService.REQUEST_GET_WORSHIP)) {
                long id = intent.getLongExtra(ApiService.EXTRA_ID, -1);
                if (id == mWorshipLoadingId) {
                    switch (intent.getAction()) {
                        case ApiService.ACTION_REQUEST_STARTED:
                            mHandler.postDelayed(mWorshipLoadingTimeoutRunnable,
                                    WORSHIP_LOAD_TIMEOUT_MS);
                            break;
                        case ApiService.ACTION_REQUEST_SUCCESSFUL:
                            mHandler.removeCallbacks(mWorshipLoadingTimeoutRunnable);
                            mWorshipLoadingId = 0;
                            openWorship(id);
                            break;
                        case ApiService.ACTION_REQUEST_ERROR:
                            mHandler.removeCallbacks(mWorshipLoadingTimeoutRunnable);
                            mWorshipLoadingId = 0;
                            int errorCode = intent.getIntExtra(ApiService.EXTRA_RESPONSE_CODE, 0);
                            if (errorCode == ApiService.HTTP_CODE_NOT_FOUND) {
                                mWorshipsAdapter.removeWorshipById(id);
                            }
                            String errorMessage = intent.getStringExtra(ApiService.EXTRA_ERROR_MESSAGE);
                            if (errorMessage == null) {
                                Toast.makeText(getContext(), R.string.error_worship_load, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }
                }
            }
        }
    };

    private Runnable mWorshipLoadingTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            mWorshipLoadingId = 0;
            Toast.makeText(getContext(), R.string.error_worship_load_timeout, Toast.LENGTH_SHORT).show();
        }
    };
}
