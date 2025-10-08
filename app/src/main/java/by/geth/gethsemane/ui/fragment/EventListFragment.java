package by.geth.gethsemane.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.activeandroid.query.Select;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import by.geth.gethsemane.R;
import by.geth.gethsemane.api.BaseRequest;
import by.geth.gethsemane.api.GetMusicGroupRequest;
import by.geth.gethsemane.api.Server;
import by.geth.gethsemane.app.AppPreferences;
import by.geth.gethsemane.data.Event;
import by.geth.gethsemane.service.ApiService;
import by.geth.gethsemane.ui.activity.MainActivity;
import by.geth.gethsemane.ui.adapter.EventListAdapter;
import by.geth.gethsemane.ui.fragment.base.BaseFragment;
import by.geth.gethsemane.ui.style.VerticalSpaceItemDecoration;
import by.geth.gethsemane.util.ConnectionUtils;

public class EventListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = EventListFragment.class.getSimpleName();

    private static final long AUTO_UPDATE_INTERVAL_MILLIS = 24 * 60 * 60 * 1000; // 24 hours

    private List<Event> mEvents;

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mEventsView;
    private EventListAdapter mEventsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long currentTime = new Date().getTime();
        mEvents = new Select()
                .from(Event.class)
                .where(Event.COLUMN_DATE + " > ?", currentTime)
                .orderBy(Event.COLUMN_DATE + " ASC")
                .execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(this);

        mEventsAdapter = new EventListAdapter(getContext());
        mEventsAdapter.setEvents(mEvents);

        Resources res = getResources();
        int outerSpace = res.getDimensionPixelSize(R.dimen.space_8);

        mEventsView = (RecyclerView) view.findViewById(R.id.events_view);
        mEventsView.setLayoutManager(new LinearLayoutManager(getContext()));
        mEventsView.addItemDecoration(new VerticalSpaceItemDecoration(0, outerSpace));
        mEventsView.setAdapter(mEventsAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
        lbm.registerReceiver(mAPIServiceReceiver, ApiService.getFullIntentFilter());

        Server.INSTANCE.addCallback(serverCallback);

        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - AppPreferences.getInstance().getUpdateTimeSchedule() > AUTO_UPDATE_INTERVAL_MILLIS)
            ApiService.getEvents(getContext());
    }

    @Override
    public void onStop() {
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
        lbm.unregisterReceiver(mAPIServiceReceiver);

        Server.INSTANCE.removeCallback(serverCallback);

        super.onStop();
    }

    @Override
    public void onRefresh() {
        if (ConnectionUtils.isNetworkConnected(getContext())) {
            ApiService.getEvents(getContext());
        } else {
            mRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), R.string.error_data_load_connection_missing, Toast.LENGTH_LONG).show();
        }
    }

    private BroadcastReceiver mAPIServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String requestType = intent.getStringExtra(ApiService.EXTRA_REQUEST_TYPE);
            if (requestType.equals(ApiService.REQUEST_GET_EVENTS)) {
                switch (intent.getAction()) {
                    case ApiService.ACTION_REQUEST_STARTED:
                        mRefreshLayout.setRefreshing(true);
                        break;
                    case ApiService.ACTION_REQUEST_SUCCESSFUL:
                        mRefreshLayout.setRefreshing(false);

                        long currentTime = new Date().getTime();
                        mEvents = new Select()
                                .from(Event.class)
                                .where(Event.COLUMN_DATE + " > ?", currentTime)
                                .orderBy(Event.COLUMN_DATE + " ASC")
                                .execute();
                        mEventsAdapter.setEvents(mEvents);
                        mEventsAdapter.notifyDataSetChanged();

                        final ArrayList<Event> addedEvents = intent.getParcelableArrayListExtra(
                                ApiService.EXTRA_SCHEDULE_EVENTS_ADDED);
                        final ArrayList<Event> deletedEvents = intent.getParcelableArrayListExtra(
                                ApiService.EXTRA_SCHEDULE_EVENTS_DELETED);
                        final ArrayList<Event> modifiedOldEvents = intent.getParcelableArrayListExtra(
                                ApiService.EXTRA_SCHEDULE_EVENTS_MODIFIED_OLD);
                        final ArrayList<Event> modifiedNewEvents = intent.getParcelableArrayListExtra(
                                ApiService.EXTRA_SCHEDULE_EVENTS_MODIFIED_NEW);
                        if (addedEvents != null && deletedEvents != null && modifiedOldEvents != null && modifiedNewEvents != null) {
                            int count = addedEvents.size() + deletedEvents.size() + modifiedOldEvents.size();
                            if (count > 0) {
                                MainActivity activity = (MainActivity) getActivity();
                                activity.showEventChangesFragment(addedEvents, deletedEvents,
                                        modifiedOldEvents, modifiedNewEvents);
                            }
                        }
                        break;
                    case ApiService.ACTION_REQUEST_ERROR:
                        mRefreshLayout.setRefreshing(false);
                        Toast.makeText(getContext(), R.string.error_data_load, Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }
    };

    private Server.Callback serverCallback = new Server.SimpleCallback() {
        @Override
        public void onSuccess(@NotNull BaseRequest request, @org.jetbrains.annotations.Nullable Object result) {
            if (request instanceof GetMusicGroupRequest) {
                mEventsAdapter.notifyDataSetChanged();
            }
        }
    };
}
