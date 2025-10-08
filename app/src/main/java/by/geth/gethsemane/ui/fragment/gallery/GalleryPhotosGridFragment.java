package by.geth.gethsemane.ui.fragment.gallery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import by.geth.gethsemane.R;
import by.geth.gethsemane.app.AppTaskManager;
import by.geth.gethsemane.data.Album;
import by.geth.gethsemane.data.Photo;
import by.geth.gethsemane.service.ApiService;
import by.geth.gethsemane.ui.activity.PhotosFullscreenActivity;
import by.geth.gethsemane.ui.adapter.GalleryPhotosGridAdapter;
import by.geth.gethsemane.ui.fragment.base.BaseFragment;
import by.geth.gethsemane.ui.style.SpacesItemDecoration;
import by.geth.gethsemane.util.ConnectionUtils;
import by.geth.gethsemane.util.Utils;

public class GalleryPhotosGridFragment extends BaseFragment {

    public static final String TAG = GalleryPhotosGridFragment.class.getSimpleName();

    private static final String ARGS_ALBUM_ID = "album_id";
    private static final String ARGS_IS_RECENT = "is_recent";
    private static final String ARGS_PHOTOS_IDS = "photos_ids";

    private static final int REQUEST_CODE_FULLSCREEN_VIEW = 1;

    /**
     * Create a fragment to display <u>recent</u> photos
     */
    public static GalleryPhotosGridFragment newInstance() {
        GalleryPhotosGridFragment fragment = new GalleryPhotosGridFragment();
        fragment.getArguments().putBoolean(ARGS_IS_RECENT, true);
        return fragment;
    }

    /**
     * Create a fragment to display <u>album</u> photos
     */
    public static GalleryPhotosGridFragment newInstance(long albumId) {
        GalleryPhotosGridFragment fragment = new GalleryPhotosGridFragment();
        fragment.getArguments().putInt(BaseFragment.ARGS_UI_FLAGS, BaseFragment.UI_FLAG_DISPLAY_HOME_AS_UP);
        fragment.getArguments().putLong(ARGS_ALBUM_ID, albumId);
        return fragment;
    }

    public static GalleryPhotosGridFragment newInstance(String photosIds) {
        GalleryPhotosGridFragment fragment = new GalleryPhotosGridFragment();
        fragment.getArguments().putString(ARGS_PHOTOS_IDS, photosIds);
        return fragment;
    }

    private long mAlbumId;
    private Album mAlbum;
    private boolean mIsRecent;
    private String mPhotosIds;

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private GalleryPhotosGridAdapter mAdapter;
    private GridLayoutManager mLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAlbumId = getArguments().getLong(ARGS_ALBUM_ID);
        mIsRecent = getArguments().getBoolean(ARGS_IS_RECENT);
        mPhotosIds = getArguments().getString(ARGS_PHOTOS_IDS);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery_photos_grid, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mAlbum = new Select().from(Album.class).where(Album.COLUMN_BACKEND_ID + " = ?", mAlbumId).executeSingle();

        mRefreshLayout = view.findViewById(R.id.refresh);
        mRefreshLayout.setOnRefreshListener(mOnRefreshListener);

        int screenWidth = Utils.getScreenWidth(getActivity());
        int minItemWidth = getResources().getDimensionPixelSize(R.dimen.grid_item_min_width);
        int spanCount = screenWidth / minItemWidth;

        mRecyclerView = view.findViewById(R.id.recycler);
        mLayoutManager = new GridLayoutManager(getContext(), spanCount);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(8, 8));

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
        lbm.registerReceiver(mApiServiceReceiver, ApiService.getFullIntentFilter());

        List<Photo> photoList = new ArrayList<>();
        if (mAlbum != null) {
            // Display photos from the album
            new Handler().post(() -> setTitle(mAlbum.getTitle()));
            photoList = new Select().from(Photo.class)
                    .where(Photo.COLUMN_ALBUM_ID + " = ?", mAlbum.getBackendId()).execute();
            Collections.sort(photoList);
            if (photoList.isEmpty()) {
                ApiService.getPhotoList(getContext(), mAlbumId, false);
            }
        } else if (mAlbumId > 0) {
            // Download the album
            ApiService.getAlbum(getContext(), mAlbumId);
            setTitle(null);
        } else if (mIsRecent) {
            // Display recent photos
            photoList = getRecentPhotos();
            if (savedInstanceState == null) {
                downloadRecentPhotos(1);
            }
        } else if (mPhotosIds != null) {
            ApiService.getPhotoList(getContext(), mPhotosIds);
        }

        mAdapter = new GalleryPhotosGridAdapter();
        mAdapter.setPhotos(photoList);
        mAdapter.setOnPhotoClickListener(mOnPhotoClickListener);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(mOnScrollListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
        lbm.unregisterReceiver(mApiServiceReceiver);
        mRecyclerView.removeOnScrollListener(mOnScrollListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAlbumId > 0 && mAdapter.getItemCount() > 0) {
            ApiService.getPhotoList(getContext(), mAlbumId, true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_FULLSCREEN_VIEW && resultCode == Activity.RESULT_OK) {
            int pos = data.getIntExtra(PhotosFullscreenActivity.EXTRA_RESULT_POSITION, 0);
            mRecyclerView.scrollToPosition(pos);
        }
    }

    private int getPageSize() {
        return getResources().getInteger(R.integer.page_size);
    }

    private List<Photo> getRecentPhotos() {
        return new Select()
                .from(Photo.class)
                .where(Photo.COLUMN_IS_RECENT + " > 0")
                .execute();
    }

    private void downloadRecentPhotos(int page) {
        ApiService.getRecentPhotoList(getContext(), page, getPageSize());
    }

    private GalleryPhotosGridAdapter.OnPhotoClickListener mOnPhotoClickListener = new GalleryPhotosGridAdapter.OnPhotoClickListener() {
        @Override
        public void onPhotoClick(List<Photo> photoList, int position) {
            if (mIsRecent) {
                PhotosFullscreenActivity.startForResult(GalleryPhotosGridFragment.this,
                        REQUEST_CODE_FULLSCREEN_VIEW, photoList, position);
            } else if (mAlbumId > 0) {
                PhotosFullscreenActivity.startForResult(GalleryPhotosGridFragment.this,
                        REQUEST_CODE_FULLSCREEN_VIEW, mAlbumId, position);
            } else if (mPhotosIds != null) {
                PhotosFullscreenActivity.startForResult(GalleryPhotosGridFragment.this,
                        REQUEST_CODE_FULLSCREEN_VIEW, photoList, position);
            }
        }
    };

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (mAlbumId > 0) {
                ApiService.getAlbum(getContext(), mAlbumId);
            } else if (mIsRecent) {
                downloadRecentPhotos(1);
            } else if (mPhotosIds != null) {
                ApiService.getPhotoList(getContext(), mPhotosIds);
            }
        }
    };

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (mIsRecent && mLayoutManager.findLastVisibleItemPosition() + getPageSize() / 2 >= mAdapter.getItemCount()) {
                int page = mAdapter.getItemCount() / getPageSize() + 1;

                AppTaskManager.Task task = new AppTaskManager.Task(ApiService.REQUEST_GET_RECENT_PHOTO_LIST);
                task.getArguments().put(ApiService.EXTRA_PAGE, page);
                task.getArguments().put(ApiService.EXTRA_PER_PAGE, getPageSize());

                if (!AppTaskManager.getInstance().contains(task) && ConnectionUtils.isNetworkConnected(getContext())) {
                    downloadRecentPhotos(page);
                }
            }
        }
    };

    private BroadcastReceiver mApiServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String requestType = intent.getStringExtra(ApiService.EXTRA_REQUEST_TYPE);
            if (requestType != null) {
                switch (requestType) {
                    case ApiService.REQUEST_GET_ALBUM:
                        onGetAlbum(intent);
                        break;
                    case ApiService.REQUEST_GET_PHOTO_LIST:
                        onGetPhotoList(intent);
                        break;
                    case ApiService.REQUEST_GET_RECENT_PHOTO_LIST:
                        onGetRecentPhotoList(intent);
                        break;
                }
            }
        }

        private void onGetAlbum(Intent intent) {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case ApiService.ACTION_REQUEST_STARTED:
                        mRefreshLayout.setRefreshing(true);
                        break;
                    case ApiService.ACTION_REQUEST_SUCCESSFUL:
                        mRefreshLayout.setRefreshing(false);
                        mAlbum = intent.getParcelableExtra(ApiService.EXTRA_BODY);
                        setTitle(mAlbum.getTitle());
                        ApiService.getPhotoList(getContext(), mAlbumId, false);
                        break;
                    case ApiService.ACTION_REQUEST_ERROR:
                        mRefreshLayout.setRefreshing(false);
                        Toast.makeText(getContext(), R.string.error_data_load, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }

        private void onGetPhotoList(Intent intent) {
            if ((intent.hasExtra(ApiService.EXTRA_IDS) && mPhotosIds != null) ||
                    (intent.hasExtra(ApiService.EXTRA_ALBUM_ID) && mAlbumId > 0) && intent.getAction() != null) {
                switch (intent.getAction()) {
                    case ApiService.ACTION_REQUEST_STARTED:
                        mRefreshLayout.setRefreshing(true);
                        break;
                    case ApiService.ACTION_REQUEST_SUCCESSFUL:
                        mRefreshLayout.setRefreshing(false);
                        List<Photo> photoList = intent.getParcelableArrayListExtra(ApiService.EXTRA_BODY);
                        mAdapter.setPhotos(photoList);
                        mAdapter.notifyDataSetChanged();
                        break;
                    case ApiService.ACTION_REQUEST_ERROR:
                        mRefreshLayout.setRefreshing(false);
                        Toast.makeText(getContext(), R.string.error_data_load, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }

        private void onGetRecentPhotoList(Intent intent) {
            if (intent.getAction() != null && mIsRecent) {
                switch (intent.getAction()) {
                    case ApiService.ACTION_REQUEST_STARTED:
                        mRefreshLayout.setRefreshing(true);
                        break;
                    case ApiService.ACTION_REQUEST_SUCCESSFUL:
                        mRefreshLayout.setRefreshing(false);
                        List<Photo> photoList = intent.getParcelableArrayListExtra(ApiService.EXTRA_BODY);
                        int page = intent.getIntExtra(ApiService.EXTRA_PAGE, 1);
                        if (page == 1) {
                            mAdapter.setPhotos(photoList);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            mAdapter.addPhotos(photoList);
                        }
                        break;
                    case ApiService.ACTION_REQUEST_ERROR:
                        mRefreshLayout.setRefreshing(false);
                        Toast.makeText(getContext(), R.string.error_data_load, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };
}
