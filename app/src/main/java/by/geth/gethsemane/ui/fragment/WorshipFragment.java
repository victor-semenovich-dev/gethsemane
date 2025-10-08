package by.geth.gethsemane.ui.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.jetbrains.annotations.NotNull;

import by.geth.gethsemane.R;
import by.geth.gethsemane.api.BaseRequest;
import by.geth.gethsemane.api.GetMusicGroupRequest;
import by.geth.gethsemane.api.GetMusicGroupsRequest;
import by.geth.gethsemane.api.Server;
import by.geth.gethsemane.app.GlideApp;
import by.geth.gethsemane.data.Sermon;
import by.geth.gethsemane.data.Witness;
import by.geth.gethsemane.data.Worship;
import by.geth.gethsemane.data.base.AudioItem;
import by.geth.gethsemane.service.ApiService;
import by.geth.gethsemane.ui.activity.PhotosFullscreenActivity;
import by.geth.gethsemane.ui.adapter.WorshipItemsAdapter;
import by.geth.gethsemane.ui.adapter.WorshipPhotosAdapter;
import by.geth.gethsemane.ui.fragment.base.AudioFragment;
import by.geth.gethsemane.ui.style.HorizontalSpaceItemDecoration;
import by.geth.gethsemane.ui.view.ScheduleView;
import by.geth.gethsemane.util.ConnectionUtils;
import by.geth.gethsemane.util.DBUtils;

@SuppressWarnings("FieldCanBeLocal")
public class WorshipFragment extends AudioFragment implements
        WorshipItemsAdapter.OnAudioClickListener, WorshipItemsAdapter.AudioInfoProvider,
        WorshipItemsAdapter.OnSermonClickListener, WorshipItemsAdapter.OnWitnessClickListener,
		WorshipItemsAdapter.OnDownloadClickListener {

    public static WorshipFragment newInstance(long id) {
        WorshipFragment fragment = new WorshipFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGS_UI_FLAGS, UI_FLAG_DISPLAY_HOME_AS_UP);
        arguments.putLong(ARGS_WORSHIP_ID, id);
        fragment.setArguments(arguments);
        return fragment;
    }

    public static final String ARGS_WORSHIP_ID = "ARGS_WORSHIP_ID";

    private static final int REQUEST_CODE_PHOTOS = 1;

    private long mWorshipId;
    private Worship mWorship;

    private ViewGroup mCardViewGroup;
    private View mVideoView;
    private ImageView mCoverView;
    private RecyclerView mPhotosRecyclerView;
    private WorshipPhotosAdapter mPhotosAdapter;
    private TextView mDescriptionView;
    private WorshipItemsAdapter mItemsListAdapter;
    private View mCommonErrorView;
    private View mConnectionErrorView;
    private SwipeRefreshLayout mRefreshLayout;

    private boolean mIsFailedNoInternet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_worship, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mCardViewGroup = view.findViewById(R.id.card_worship_root);
        mVideoView = view.findViewById(R.id.card_worship_video);
        mCoverView = view.findViewById(R.id.card_worship_video_cover);
        mPhotosRecyclerView = view.findViewById(R.id.card_worship_photos);
        mDescriptionView = view.findViewById(R.id.card_worship_description);
        mCommonErrorView = view.findViewById(R.id.error_common);
        mConnectionErrorView = view.findViewById(R.id.error_connection);
        mRefreshLayout = view.findViewById(R.id.refresh);
        mRefreshLayout.setEnabled(false);

        mVideoView.setOnClickListener(mOnVideoClickListener);

        ScheduleView itemsListView = view.findViewById(R.id.card_worship_items_list);
        mItemsListAdapter = new WorshipItemsAdapter();
        mItemsListAdapter.setOnAudioClickListener(this);
        mItemsListAdapter.setOnSermonClickListener(this);
        mItemsListAdapter.setOnWitnessClickListener(this);
		mItemsListAdapter.setOnDownloadClickListener(this);
        mItemsListAdapter.setAudioInfoProvider(this);
        itemsListView.setAdapter(mItemsListAdapter);

        mPhotosRecyclerView.setHasFixedSize(true);
        mPhotosRecyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.space_8), 0));
        mPhotosAdapter = new WorshipPhotosAdapter();
        mPhotosAdapter.setOnClickListener(mOnPhotoClickListener);
        mPhotosRecyclerView.setAdapter(mPhotosAdapter);

        mRefreshLayout.setOnRefreshListener(mOnRefreshListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getContext() == null) {
            return;
        }
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
        lbm.registerReceiver(mAPIBroadcastReceiver, ApiService.getFullIntentFilter());

        Bundle args = getArguments();
        if (args != null && args.containsKey(ARGS_WORSHIP_ID)) {
            mWorshipId = args.getLong(ARGS_WORSHIP_ID);
            mWorship = DBUtils.getWorship(mWorshipId);
            if (mWorship != null && getParentFragment() == null) {
                new Handler().post(() -> setTitle(mWorship.getName()));
            }
            if (ConnectionUtils.isNetworkConnected(getContext())) {
                mIsFailedNoInternet = false;
                boolean isSilent = mWorship != null;
                ApiService.getWorship(getContext(), mWorshipId, isSilent);
            } else if (mWorship == null) {
                mIsFailedNoInternet = true;
                mCommonErrorView.setVisibility(View.GONE);
                mConnectionErrorView.setVisibility(View.VISIBLE);
            }
        }
        updateCardView();

        getContext().registerReceiver(mNetworkBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        Server.INSTANCE.addCallback(serverCallback);
    }

    @Override
    public void onStop() {
        if (getContext() == null) {
            return;
        }
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mAPIBroadcastReceiver);
        getContext().unregisterReceiver(mNetworkBroadcastReceiver);
        Server.INSTANCE.removeCallback(serverCallback);
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PHOTOS && resultCode == Activity.RESULT_OK && data != null) {
            int pos = data.getIntExtra(PhotosFullscreenActivity.EXTRA_RESULT_POSITION, 0);
            mPhotosRecyclerView.scrollToPosition(pos);
        }
    }

    @Override
    public void onAudioClick(AudioItem item) {
        playStop(item);
    }

    @Override
    public void onDownloadClick(AudioItem item) {
        download(item);
    }

    @Override
    public void onDeleteClick(AudioItem item) {
        delete(item);
    }

    @Override
    public void onSermonClick(Sermon sermon) {
        Fragment sermonFragment = new SermonFragment();
        Bundle args = new Bundle();
        args.putInt(SermonFragment.ARGS_UI_FLAGS, SermonFragment.UI_FLAG_DISPLAY_HOME_AS_UP);
        args.putString(SermonFragment.ARGS_AB_TITLE, sermon.getTitle());
        args.putLong(SermonFragment.ARGS_SERMON_ID, sermon.getExternalId());
        sermonFragment.setArguments(args);
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right_short, R.anim.slide_out_left_short,
                            R.anim.slide_in_left_short, R.anim.slide_out_right_short)
                    .replace(R.id.fragment_container, sermonFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onWitnessClick(Witness witness) {
        Fragment witnessFragment = new WitnessFragment();
        Bundle args = new Bundle();
        args.putInt(WitnessFragment.ARGS_UI_FLAGS, WitnessFragment.UI_FLAG_DISPLAY_HOME_AS_UP);
        args.putString(WitnessFragment.ARGS_AB_TITLE, witness.getTitle());
        args.putLong(WitnessFragment.ARGS_WITNESS_ID, witness.getExternalId());
        witnessFragment.setArguments(args);
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right_short, R.anim.slide_out_left_short,
                            R.anim.slide_in_left_short, R.anim.slide_out_right_short)
                    .replace(R.id.fragment_container, witnessFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public boolean isAudioInProgress(AudioItem item) {
        return isInProgress(item);
    }

    @Override
    protected void updateAudioUI() {
        if (mItemsListAdapter != null)
            mItemsListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void updateDownloadUI() {
        if (mItemsListAdapter != null)
            mItemsListAdapter.notifyDataSetChanged();
    }

    private void updateCardView() {
        if (mWorship != null) {
            mCardViewGroup.setVisibility(View.VISIBLE);

            if (!TextUtils.isEmpty(mWorship.getPosterUri())) {
                GlideApp.with(this).load(mWorship.getPosterUri())
                        .placeholder(R.drawable.cover_worship).into(mCoverView);
            } else {
                mCoverView.setImageResource(R.drawable.cover_worship);
            }

            if (mWorship.getPhotoList() == null || mWorship.getPhotoList().isEmpty()) {
                mPhotosRecyclerView.setVisibility(View.GONE);
            } else {
                mPhotosRecyclerView.setVisibility(View.VISIBLE);
                mPhotosAdapter.setWorship(mWorship);
            }

            if (TextUtils.isEmpty(mWorship.getDescription())) {
                mDescriptionView.setVisibility(View.GONE);
            } else {
                mDescriptionView.setText(mWorship.getDescription());
                mDescriptionView.setVisibility(View.VISIBLE);
            }

            mItemsListAdapter.setSermonList(mWorship.getSermonList());
            mItemsListAdapter.setWitnessList(mWorship.getWitnessList());
            mItemsListAdapter.setSongList(mWorship.getSongList());
            if (!TextUtils.isEmpty(mWorship.getAudioUri())) {
                mItemsListAdapter.setWorship(mWorship);
            }
            mItemsListAdapter.notifyDataSetChanged();
        }
    }

    private View.OnClickListener mOnVideoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mWorship != null && !TextUtils.isEmpty(mWorship.getVideoUri())) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(String.format("https://www.youtube.com/watch?v=%s", mWorship.getVideoUri())));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private WorshipPhotosAdapter.OnClickListener mOnPhotoClickListener = new WorshipPhotosAdapter.OnClickListener() {
        @Override
        public void onClick(int position) {
            PhotosFullscreenActivity.startForResult(WorshipFragment.this, REQUEST_CODE_PHOTOS,
                    mWorship.getPhotoList(), position);
        }
    };

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (ConnectionUtils.isNetworkConnected(getContext())) {
                mIsFailedNoInternet = false;
                ApiService.getWorship(getContext(), mWorshipId);
            } else {
                mRefreshLayout.setRefreshing(false);
                if (mWorship == null) {
                    mIsFailedNoInternet = true;
                    mCommonErrorView.setVisibility(View.GONE);
                    mConnectionErrorView.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    private BroadcastReceiver mAPIBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String requestType = intent.getStringExtra(ApiService.EXTRA_REQUEST_TYPE);
            if (requestType != null && intent.getAction() != null) {
                switch (requestType) {
                    case ApiService.REQUEST_GET_WORSHIP:
                        onLoadWorship(intent);
                        break;
                    case ApiService.REQUEST_GET_AUTHOR:
                        if (intent.getAction().equals(ApiService.ACTION_REQUEST_SUCCESSFUL)) {
                            updateCardView();
                        }
                        break;
                }
            }
        }

        private void onLoadWorship(Intent intent) {
            long id = intent.getLongExtra(ApiService.EXTRA_ID, 0);
            if (intent.getAction() != null && mWorshipId == id) {
                switch (intent.getAction()) {
                    case ApiService.ACTION_REQUEST_STARTED:
                        mRefreshLayout.setRefreshing(true);
                        break;
                    case ApiService.ACTION_REQUEST_SUCCESSFUL:
                        mRefreshLayout.setRefreshing(false);
                        mWorship = DBUtils.getWorship(mWorshipId);
                        if (mWorship != null) {
                            mConnectionErrorView.setVisibility(View.GONE);
                            mCommonErrorView.setVisibility(View.GONE);
                            if (getParentFragment() == null) {
                                setTitle(mWorship.getName());
                            }
                            updateCardView();
                        } else {
                            mConnectionErrorView.setVisibility(View.GONE);
                            mCommonErrorView.setVisibility(View.VISIBLE);
                        }
                        break;
                    case ApiService.ACTION_REQUEST_ERROR:
                        mRefreshLayout.setRefreshing(false);
                        if (mWorship == null) {
                            mConnectionErrorView.setVisibility(View.GONE);
                            mCommonErrorView.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            }
        }
    };

    private BroadcastReceiver mNetworkBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mIsFailedNoInternet && ConnectionUtils.isNetworkConnected(getContext())) {
                mIsFailedNoInternet = false;
                ApiService.getWorship(getContext(), mWorshipId);
            }
        }
    };

    private Server.Callback serverCallback = new Server.SimpleCallback() {
        @Override
        public void onSuccess(@NotNull BaseRequest request, @Nullable Object result) {
            if (request instanceof GetMusicGroupsRequest || request instanceof GetMusicGroupRequest) {
                updateCardView();
            }
        }
    };
}
