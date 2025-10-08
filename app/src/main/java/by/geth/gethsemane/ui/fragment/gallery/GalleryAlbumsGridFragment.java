package by.geth.gethsemane.ui.fragment.gallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import by.geth.gethsemane.R;
import by.geth.gethsemane.data.Album;
import by.geth.gethsemane.data.Category;
import by.geth.gethsemane.data.CategoryAlbum;
import by.geth.gethsemane.service.ApiService;
import by.geth.gethsemane.ui.adapter.GalleryAlbumsAdapter;
import by.geth.gethsemane.ui.fragment.base.BaseFragment;
import by.geth.gethsemane.ui.style.SpacesItemDecoration;
import by.geth.gethsemane.util.Utils;

public class GalleryAlbumsGridFragment extends BaseFragment {

    private static final String ARGS_CATEGORY = "category";

    public static GalleryAlbumsGridFragment newInstance(Category category) {
        GalleryAlbumsGridFragment fragment = new GalleryAlbumsGridFragment();
        fragment.getArguments().putParcelable(ARGS_CATEGORY, category);
        return fragment;
    }

    private Category mCategory;

    private SwipeRefreshLayout mRefreshLayout;
    private GalleryAlbumsAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mCategory = getArguments().getParcelable(ARGS_CATEGORY);

        mRefreshLayout = view.findViewById(R.id.refresh);
        mRefreshLayout.setOnRefreshListener(mOnRefreshListener);

        int screenWidth = Utils.getScreenWidth(getActivity());
        int minItemWidth = getResources().getDimensionPixelSize(R.dimen.grid_item_min_width);
        int spanCount = screenWidth / minItemWidth;

        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        recyclerView.setHasFixedSize(false);
        recyclerView.addItemDecoration(new SpacesItemDecoration(8, 8));

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
        lbm.registerReceiver(mApiServiceReceiver, ApiService.getFullIntentFilter());

        List<CategoryAlbum> categoryAlbumList = new Select().from(CategoryAlbum.class)
                .where(CategoryAlbum.COLUMN_CATEGORY_ID + " = ?", mCategory.getBackendId())
                .execute();
        if (categoryAlbumList.isEmpty()) {
            ApiService.getAlbumList(getContext(), mCategory.getBackendId(), false);
        }

        List<Album> albumList = new ArrayList<>();
        for (CategoryAlbum categoryAlbum : categoryAlbumList) {
            Album album = new Select().from(Album.class)
                    .where(Album.COLUMN_BACKEND_ID + " = ?", categoryAlbum.getAlbumId())
                    .executeSingle();
            albumList.add(album);
        }

        mAdapter = new GalleryAlbumsAdapter();
        mAdapter.setAlbumList(albumList);
        mAdapter.setOnAlbumClickListener(mOnAlbumClickListener);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
        lbm.unregisterReceiver(mApiServiceReceiver);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter.getItemCount() > 0) {
            ApiService.getAlbumList(getContext(), mCategory.getBackendId(), true);
        }
    }

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            ApiService.getAlbumList(getContext(), mCategory.getBackendId(), false);
        }
    };

    private GalleryAlbumsAdapter.OnAlbumClickListener mOnAlbumClickListener = new GalleryAlbumsAdapter.OnAlbumClickListener() {
        @Override
        public void onAlbumClick(Album album) {
            Fragment albumFragment = GalleryPhotosGridFragment.newInstance(album.getBackendId());
            albumFragment.getArguments().putString(BaseFragment.ARGS_AB_TITLE, album.getTitle());
            getFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right_short, R.anim.slide_out_left_short,
                            R.anim.slide_in_left_short, R.anim.slide_out_right_short)
                    .replace(R.id.fragment_container, albumFragment)
                    .addToBackStack(null)
                    .commit();
        }
    };

    private BroadcastReceiver mApiServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String requestType = intent.getStringExtra(ApiService.EXTRA_REQUEST_TYPE);
            if (ApiService.REQUEST_GET_ALBUM_LIST.equals(requestType) && intent.getAction() != null) {
                switch (intent.getAction()) {
                    case ApiService.ACTION_REQUEST_STARTED:
                        mRefreshLayout.setRefreshing(true);
                        break;
                    case ApiService.ACTION_REQUEST_SUCCESSFUL:
                        mRefreshLayout.setRefreshing(false);
                        List<Album> albumList = intent.getParcelableArrayListExtra(ApiService.EXTRA_BODY);
                        mAdapter.setAlbumList(albumList);
                        mAdapter.notifyDataSetChanged();
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
