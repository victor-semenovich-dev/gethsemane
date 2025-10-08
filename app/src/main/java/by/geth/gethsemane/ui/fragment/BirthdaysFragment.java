package by.geth.gethsemane.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.activeandroid.query.Select;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import by.geth.gethsemane.R;
import by.geth.gethsemane.api.BaseRequest;
import by.geth.gethsemane.api.GetBirthdaysRequest;
import by.geth.gethsemane.api.Server;
import by.geth.gethsemane.data.model.Birthday;
import by.geth.gethsemane.ui.adapter.BirthdaysAdapter;
import by.geth.gethsemane.ui.fragment.base.BaseFragment;
import by.geth.gethsemane.util.ConnectionUtils;

@SuppressWarnings("ConstantConditions")
public class BirthdaysFragment extends BaseFragment {

    private SwipeRefreshLayout mRefreshView;
    private BirthdaysAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_birthdays, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRefreshView = view.findViewById(R.id.refresh);
        mRefreshView.setOnRefreshListener(mOnRefreshListener);

        mAdapter = new BirthdaysAdapter();

        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(mAdapter);

        updateList(new Select().from(Birthday.class).execute());

        Server.INSTANCE.addCallback(serverCallback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Server.INSTANCE.removeCallback(serverCallback);
    }

    private void updateList(List<Birthday> birthdays) {
        mAdapter.setBirthdays(birthdays);
        mAdapter.notifyDataSetChanged();
    }

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = () -> {
        if (ConnectionUtils.isNetworkConnected(getContext())) {
            Server.INSTANCE.getBirthdays(false);
        } else {
            Toast.makeText(getContext(), R.string.error_no_internet, Toast.LENGTH_SHORT).show();
        }
    };

    private Server.Callback serverCallback = new Server.Callback() {
        @Override
        public void onStarted(@NotNull BaseRequest request) {
            if (request.equals(new GetBirthdaysRequest())) {
                mRefreshView.setRefreshing(true);
            }
        }

        @Override
        public void onSuccess(@NotNull BaseRequest request, @org.jetbrains.annotations.Nullable Object result) {
            if (request.equals(new GetBirthdaysRequest())) {
                mRefreshView.setRefreshing(false);
                //noinspection unchecked
                updateList((List<Birthday>) result);
            }
        }

        @Override
        public void onFailure(@NotNull BaseRequest request, int code, @NotNull String message) {
            if (request.equals(new GetBirthdaysRequest())) {
                mRefreshView.setRefreshing(false);
                Toast.makeText(getContext(), R.string.error_data_load, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(@NotNull BaseRequest request, @NotNull Throwable t) {
            if (request.equals(new GetBirthdaysRequest())) {
                mRefreshView.setRefreshing(false);
                Toast.makeText(getContext(), R.string.error_data_load, Toast.LENGTH_SHORT).show();
            }
        }
    };
}
