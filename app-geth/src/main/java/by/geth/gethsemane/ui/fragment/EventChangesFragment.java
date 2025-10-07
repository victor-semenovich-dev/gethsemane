package by.geth.gethsemane.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import by.geth.gethsemane.R;
import by.geth.gethsemane.api.BaseRequest;
import by.geth.gethsemane.api.GetMusicGroupRequest;
import by.geth.gethsemane.api.Server;
import by.geth.gethsemane.data.Event;
import by.geth.gethsemane.ui.adapter.EventChangesAdapter;
import by.geth.gethsemane.ui.fragment.base.BaseFragment;

public class EventChangesFragment extends BaseFragment {
    private static final String TAG = EventChangesFragment.class.getSimpleName();

    public static final String ARGS_ADDED_EVENTS = "ARGS_ADDED_EVENTS";
    public static final String ARGS_DELETED_EVENTS = "ARGS_DELETED_EVENTS";
    public static final String ARGS_MODIFIED_OLD_EVENTS = "ARGS_MODIFIED_OLD_EVENTS";
    public static final String ARGS_MODIFIED_NEW_EVENTS = "ARGS_MODIFIED_NEW_EVENTS";

    private RecyclerView mRecyclerView;
    private EventChangesAdapter mAdapter;

    private List<Event> mAddedEvents;
    private List<Event> mDeletedEvents;
    private List<Event> mModifiedOldEvents;
    private List<Event> mModifiedNewEvents;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mAddedEvents = args.getParcelableArrayList(ARGS_ADDED_EVENTS);
            mDeletedEvents = args.getParcelableArrayList(ARGS_DELETED_EVENTS);
            mModifiedOldEvents = args.getParcelableArrayList(ARGS_MODIFIED_OLD_EVENTS);
            mModifiedNewEvents = args.getParcelableArrayList(ARGS_MODIFIED_NEW_EVENTS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_changes_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (mAddedEvents != null && mDeletedEvents != null
                && mModifiedOldEvents != null && mModifiedNewEvents != null
                && (mModifiedOldEvents.size() == mModifiedNewEvents.size())) {
            mAdapter = new EventChangesAdapter(getContext(), mAddedEvents, mDeletedEvents,
                    mModifiedOldEvents, mModifiedNewEvents);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(mAdapter));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Server.INSTANCE.addCallback(serverCallback);
    }

    @Override
    public void onStop() {
        Server.INSTANCE.removeCallback(serverCallback);
        super.onStop();
    }

    private Server.Callback serverCallback = new Server.SimpleCallback() {
        @Override
        public void onSuccess(@NotNull BaseRequest request, @org.jetbrains.annotations.Nullable Object result) {
            if (request instanceof GetMusicGroupRequest) {
                mAdapter.notifyDataSetChanged();
            }
        }
    };
}
