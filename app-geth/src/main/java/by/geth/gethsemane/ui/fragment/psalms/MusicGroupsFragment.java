package by.geth.gethsemane.ui.fragment.psalms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.activeandroid.query.Select;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import by.geth.gethsemane.R;
import by.geth.gethsemane.api.BaseRequest;
import by.geth.gethsemane.api.GetMusicGroupsRequest;
import by.geth.gethsemane.api.Server;
import by.geth.gethsemane.data.MusicGroup;
import by.geth.gethsemane.ui.adapter.MusicGroupsAdapter;
import by.geth.gethsemane.ui.fragment.base.BaseFragment;
import by.geth.gethsemane.util.ConnectionUtils;

public class MusicGroupsFragment extends BaseFragment implements MusicGroupsAdapter.Callback {

    public static MusicGroupsFragment newInstance() {
        MusicGroupsFragment fragment = new MusicGroupsFragment();
        Bundle args = new Bundle();
        args.putInt(BaseFragment.ARGS_AB_TITLE_RES_ID, R.string.music_groups);
        args.putInt(BaseFragment.ARGS_UI_FLAGS, BaseFragment.UI_FLAG_DISPLAY_HOME_AS_UP);
        fragment.setArguments(args);
        return fragment;
    }

    private MusicGroupsAdapter mAdapter = new MusicGroupsAdapter(this);
    private SwipeRefreshLayout mRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music_groups, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter.setMusicGroupList(getMusicGroupsList());
        recyclerView.setAdapter(mAdapter);

        mRefreshLayout = view.findViewById(R.id.refresh);
        mRefreshLayout.setOnRefreshListener(() -> {
            if (ConnectionUtils.isNetworkConnected(requireContext())) {
                Server.INSTANCE.getMusicGroups(false);
            } else {
                Toast.makeText(requireContext(), R.string.error_no_internet, Toast.LENGTH_SHORT).show();
                mRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Server.INSTANCE.isRunning(new GetMusicGroupsRequest())) {
            mRefreshLayout.setRefreshing(true);
        }
        Server.INSTANCE.addCallback(serverCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        Server.INSTANCE.removeCallback(serverCallback);
    }

    @Override
    public void onMusicGroupClick(MusicGroup group) {
        showFragment(SongsListFragment.Companion.newInstance(group));
    }

    private List<MusicGroup> getMusicGroupsList() {
        return new Select().from(MusicGroup.class)
            .where(MusicGroup.COLUMN_IS_SHOW + " > 0 AND " + MusicGroup.COLUMN_IMAGE + " IS NOT NULL")
            .orderBy(MusicGroup.COLUMN_TITLE + " ASC")
            .execute();
    }

    private Server.Callback serverCallback = new Server.Callback() {
        @Override
        public void onFailure(@NotNull BaseRequest request, @NotNull Throwable t) {
            if (request instanceof GetMusicGroupsRequest) {
                Toast.makeText(requireContext(), R.string.error_data_load, Toast.LENGTH_SHORT).show();
            }
            mRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onFailure(@NotNull BaseRequest request, int code, @NotNull String message) {
            if (request instanceof GetMusicGroupsRequest) {
                Toast.makeText(requireContext(), R.string.error_data_load, Toast.LENGTH_SHORT).show();
            }
            mRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onSuccess(@NotNull BaseRequest request, @org.jetbrains.annotations.Nullable Object result) {
            if (request instanceof GetMusicGroupsRequest) {
                mAdapter.setMusicGroupList(getMusicGroupsList());
                mRefreshLayout.setRefreshing(false);
            }
        }

        @Override
        public void onStarted(@NotNull BaseRequest request) {
            if (request instanceof GetMusicGroupsRequest) {
                mRefreshLayout.setRefreshing(true);
            }
        }
    };
}
