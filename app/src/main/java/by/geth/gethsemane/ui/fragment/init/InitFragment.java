package by.geth.gethsemane.ui.fragment.init;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.koin.java.KoinJavaComponent;

import by.geth.gethsemane.R;
import by.geth.gethsemane.service.ApiService;
import by.geth.gethsemane.ui.fragment.base.BaseFragment;
import by.geth.gethsemane.util.ConnectionUtils;
import by.geth.gethsemane.util.DBUtils;
import by.geth.gethsemane.util.DialogUtils;

public class InitFragment extends BaseFragment {
    private static final String TAG = InitFragment.class.getSimpleName();

    public interface DataInitListener {
        void onDataInitSuccess();
    }

    private enum UI_STATE {
        PROGRESS,
        ERROR
    }

    private final InitViewModel initViewModel = KoinJavaComponent.get(InitViewModel.class);

    private DataInitListener mInitListener;

    private ProgressBar mProgressView;
    private TextView mLoadingTextView;
    private Button mReloadButton;

    private long tstamp = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initViewModel.loadAllAuthors();
        return inflater.inflate(R.layout.fragment_init, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mProgressView = (ProgressBar) view.findViewById(R.id.progress);
        mLoadingTextView = (TextView) view.findViewById(R.id.loading_text);
        mReloadButton = (Button) view.findViewById(R.id.reload);
        mReloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUIState(UI_STATE.PROGRESS);
                doRequest();
            }
        });

        tstamp = System.currentTimeMillis();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mInitListener = (DataInitListener) getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mBroadcastReceiver,
                ApiService.getFullIntentFilter());
        doRequest();
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mBroadcastReceiver);
        super.onStop();
    }

    private void doRequest() {
        if (ConnectionUtils.isNetworkConnected(getContext())) {
            if (!DBUtils.isEventsLoaded())
                ApiService.getEvents(getContext(), "2019-01-01");
            else if (!DBUtils.isAuthorsLoaded())
                ApiService.getAuthorList(getContext());
        } else {
            DialogUtils.showAlertDialog(getContext(), R.string.error_data_load_connection_missing);
            setUIState(UI_STATE.ERROR);
        }
    }

    private void setUIState(UI_STATE state) {
        switch (state) {
            case PROGRESS:
                mProgressView.setVisibility(View.VISIBLE);
                mLoadingTextView.setVisibility(View.VISIBLE);
                mReloadButton.setVisibility(View.GONE);
                break;
            case ERROR:
                mProgressView.setVisibility(View.GONE);
                mLoadingTextView.setVisibility(View.GONE);
                mReloadButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ApiService.ACTION_REQUEST_STARTED:
                    switch (intent.getStringExtra(ApiService.EXTRA_REQUEST_TYPE)) {
                        case ApiService.REQUEST_GET_AUTHOR_LIST:
                            mLoadingTextView.setText(R.string.fragment_init_text_authors);
                            break;
                        case ApiService.REQUEST_GET_EVENTS:
                            mLoadingTextView.setText(R.string.fragment_init_text_events);
                            break;
                        case ApiService.REQUEST_GET_SERMONS_LIST:
                            mLoadingTextView.setText(R.string.fragment_init_text_sermons);
                            break;
                        case ApiService.REQUEST_GET_WITNESSES_LIST:
                            mLoadingTextView.setText(R.string.fragment_init_text_witnesses);
                            break;
                    }
                    break;
                case ApiService.ACTION_REQUEST_SUCCESSFUL:
                    mLoadingTextView.setText(null);
                    if (DBUtils.isDataInitialized()) {
                        mInitListener.onDataInitSuccess();
                    } else
                        doRequest();
                    break;
                case ApiService.ACTION_REQUEST_ERROR:
                    Toast.makeText(getContext(), R.string.error_data_load, Toast.LENGTH_LONG).show();
                    setUIState(UI_STATE.ERROR);
                    break;
            }
        }
    };
}
