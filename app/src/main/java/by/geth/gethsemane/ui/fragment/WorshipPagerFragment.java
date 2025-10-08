package by.geth.gethsemane.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.activeandroid.query.Select;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import by.geth.gethsemane.R;
import by.geth.gethsemane.app.AppPreferences;
import by.geth.gethsemane.data.Event;
import by.geth.gethsemane.service.ApiService;
import by.geth.gethsemane.ui.adapter.WorshipPagerAdapter;
import by.geth.gethsemane.ui.fragment.base.BaseFragment;

public class WorshipPagerFragment extends BaseFragment {

    public static final String ARGS_WORSHIP_ID = "ARGS_WORSHIP_ID";

    private ViewPager mPager;
    private WorshipPagerAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_worship_pager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mAdapter = new WorshipPagerAdapter(getChildFragmentManager());
        mAdapter.setEventList(getEventsFromDatabase());

        mPager = view.findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.addOnPageChangeListener(mOnPageChangeListener);

        Bundle args = getArguments();
        if (args.containsKey(ARGS_WORSHIP_ID)) {
            long eventId = args.getLong(ARGS_WORSHIP_ID);
            int pos = 0;
            for (int i = 0; i < mAdapter.getEventList().size(); i++) {
                if (eventId == mAdapter.getEventList().get(i).externalId()) {
                    pos = i;
                    break;
                }
            }
            mPager.setCurrentItem(pos);
        }

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                updateTitle();
            }
        });

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
        lbm.registerReceiver(mApiServiceReceiver, ApiService.getFullIntentFilter());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPager.removeOnPageChangeListener(mOnPageChangeListener);

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
        lbm.unregisterReceiver(mApiServiceReceiver);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateEvents();
    }

    private List<Event> getEventsFromDatabase() {
        return new Select()
                .from(Event.class)
                .where("(" + Event.COLUMN_IS_ARCHIVE + " == 0) AND " +
                        "(" + Event.COLUMN_IS_DRAFT + " == 0) AND " +
                        "(" + Event.COLUMN_CATEGORY_ID + " = ?) AND " +
                        "(" + Event.COLUMN_DATE + " < ?)", Event.ID_CATEGORY_WORSHIP, System.currentTimeMillis())
                .orderBy(Event.COLUMN_DATE + " DESC")
                .execute();
    }

    private void updateTitle() {
        int position = mPager.getCurrentItem();
        Event event = mAdapter.getEventList().get(position);
        setTitle(event.getName());
    }

    private void updateEvents() {
        long lastUpdate = AppPreferences.getInstance().getUpdateTimeWorships();
        long interval = (long)31 * 24 * 3600 * 1000; // 1 month
        Date date = new Date(lastUpdate - interval);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        ApiService.getEvents(getContext(), dateFormat.format(date));
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            updateTitle();
        }
    };

    private BroadcastReceiver mApiServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String requestType = intent.getStringExtra(ApiService.EXTRA_REQUEST_TYPE);
            if (requestType.equals(ApiService.REQUEST_GET_EVENTS) && intent.getAction() != null) {
                switch (intent.getAction()) {
                    case ApiService.ACTION_REQUEST_SUCCESSFUL:
                        long selectedEventId = mAdapter.getEventList().get(mPager.getCurrentItem()).externalId();
                        mAdapter.setEventList(getEventsFromDatabase());
                        mAdapter.notifyDataSetChanged();
                        if (mPager.getCurrentItem() > 0) {
                            // save selected position
                            int pos = mPager.getCurrentItem();
                            for (int i = 0; i < mAdapter.getEventList().size(); i++) {
                                if (selectedEventId == mAdapter.getEventList().get(i).externalId()) {
                                    pos = i;
                                    break;
                                }
                            }
                            mPager.setCurrentItem(pos);
                        }
                        updateTitle();
                        break;
                }
            }
        }
    };
}
