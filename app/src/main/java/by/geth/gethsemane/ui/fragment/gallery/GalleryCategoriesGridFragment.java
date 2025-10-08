package by.geth.gethsemane.ui.fragment.gallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.activeandroid.query.Select;

import java.util.List;

import by.geth.gethsemane.R;
import by.geth.gethsemane.data.Category;
import by.geth.gethsemane.service.ApiService;
import by.geth.gethsemane.ui.adapter.GalleryCategoriesAdapter;
import by.geth.gethsemane.ui.fragment.base.BaseFragment;
import by.geth.gethsemane.ui.style.SpacesItemDecoration;
import by.geth.gethsemane.util.Utils;

/**
 * Created by Victor Semenovich on 17.02.2018.
 */
public class GalleryCategoriesGridFragment extends BaseFragment {

    public static final String TAG = GalleryCategoriesGridFragment.class.getSimpleName();

    private SwipeRefreshLayout mRefreshLayout;
    private GalleryCategoriesAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery_categories_grid, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRefreshLayout = view.findViewById(R.id.refresh);
        mRefreshLayout.setOnRefreshListener(mOnRefreshListener);

        int screenWidth = Utils.getScreenWidth(getActivity());
        int minItemWidth = getResources().getDimensionPixelSize(R.dimen.grid_item_min_width);
        int spanCount = screenWidth / minItemWidth;

        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        recyclerView.setHasFixedSize(false);
        recyclerView.addItemDecoration(new SpacesItemDecoration(8, 8));

        List<Category> categoryList = new Select().from(Category.class).execute();
        mAdapter = new GalleryCategoriesAdapter();
        mAdapter.setCategoryList(categoryList);
        mAdapter.setOnCategoryClickListener(mOnCategoryClickListener);
        recyclerView.setAdapter(mAdapter);

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
        lbm.registerReceiver(mApiServiceReceiver, ApiService.getFullIntentFilter());

        if (categoryList.isEmpty()) {
            ApiService.getCategoryList(getContext(), Category.TYPE_PHOTOS, false);
        }
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
            ApiService.getCategoryList(getContext(), Category.TYPE_PHOTOS, true);
        }
    }

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            ApiService.getCategoryList(getContext(), Category.TYPE_PHOTOS, false);
        }
    };

    private GalleryCategoriesAdapter.OnCategoryClickListener mOnCategoryClickListener = new GalleryCategoriesAdapter.OnCategoryClickListener() {
        @Override
        public void onCategoryClick(Category category) {
            Fragment categoryFragment = GalleryAlbumsGridFragment.newInstance(category);
            categoryFragment.getArguments().putString(BaseFragment.ARGS_AB_TITLE, category.getTitle());
            categoryFragment.getArguments().putInt(BaseFragment.ARGS_UI_FLAGS, BaseFragment.UI_FLAG_DISPLAY_HOME_AS_UP);
            getFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right_short, R.anim.slide_out_left_short,
                            R.anim.slide_in_left_short, R.anim.slide_out_right_short)
                    .replace(R.id.fragment_container, categoryFragment)
                    .addToBackStack(null)
                    .commit();
        }
    };

    private BroadcastReceiver mApiServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String requestType = intent.getStringExtra(ApiService.EXTRA_REQUEST_TYPE);
            if (ApiService.REQUEST_GET_CATEGORY_LIST.equals(requestType) && intent.getAction() != null) {
                switch (intent.getAction()) {
                    case ApiService.ACTION_REQUEST_STARTED:
                        mRefreshLayout.setRefreshing(true);
                        break;
                    case ApiService.ACTION_REQUEST_SUCCESSFUL:
                        mRefreshLayout.setRefreshing(false);
                        List<Category> categoryList = intent.getParcelableArrayListExtra(ApiService.EXTRA_BODY);
                        mAdapter.setCategoryList(categoryList);
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
