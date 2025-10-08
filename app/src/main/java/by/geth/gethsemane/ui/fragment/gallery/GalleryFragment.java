package by.geth.gethsemane.ui.fragment.gallery;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import by.geth.gethsemane.R;
import by.geth.gethsemane.app.AppPreferences;
import by.geth.gethsemane.ui.fragment.base.BaseFragment;

public class GalleryFragment extends BaseFragment {

    public static final int SCREEN_CATEGORIES = 1;
    public static final int SCREEN_RECENT_PHOTOS = 2;

    private GalleryCategoriesGridFragment mCategoriesFragment;
    private GalleryPhotosGridFragment mPhotosFragment;

    private MenuItem mPhotosMenuItem;
    private MenuItem mCategoriesMenuItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mCategoriesFragment = new GalleryCategoriesGridFragment();
        mCategoriesFragment.getArguments().putInt(BaseFragment.ARGS_UI_FLAGS,
                BaseFragment.UI_FLAG_DISPLAY_HOME_AS_UP);
        mCategoriesFragment.getArguments().putInt(BaseFragment.ARGS_AB_TITLE_RES_ID,
                R.string.categories);

        mPhotosFragment = GalleryPhotosGridFragment.newInstance();
        mPhotosFragment.getArguments().putInt(BaseFragment.ARGS_UI_FLAGS,
                BaseFragment.UI_FLAG_DISPLAY_HOME_AS_UP);
        mPhotosFragment.getArguments().putInt(BaseFragment.ARGS_AB_TITLE_RES_ID,
                R.string.recent_photos);

        if (getChildFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            switch (AppPreferences.getInstance().getGalleryDefaultScreen()) {
                case SCREEN_CATEGORIES:
                    getChildFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, mCategoriesFragment, GalleryCategoriesGridFragment.TAG)
                            .commit();
                    break;
                case SCREEN_RECENT_PHOTOS:
                    getChildFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, mPhotosFragment, GalleryPhotosGridFragment.TAG)
                            .commit();
                    break;
            }
        }
        getChildFragmentManager().addOnBackStackChangedListener(mOnBackStackChangedListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getChildFragmentManager().removeOnBackStackChangedListener(mOnBackStackChangedListener);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_gallery, menu);
        mPhotosMenuItem = menu.findItem(R.id.photos);
        mCategoriesMenuItem = menu.findItem(R.id.categories);

        if (getChildFragmentManager().getBackStackEntryCount() > 0) {
            mPhotosMenuItem.setVisible(false);
            mCategoriesMenuItem.setVisible(false);
        } else if (getChildFragmentManager().findFragmentByTag(GalleryPhotosGridFragment.TAG) != null) {
            mPhotosMenuItem.setVisible(false);
            mCategoriesMenuItem.setVisible(true);
        } else if (getChildFragmentManager().findFragmentByTag(GalleryCategoriesGridFragment.TAG) != null) {
            mPhotosMenuItem.setVisible(true);
            mCategoriesMenuItem.setVisible(false);
        } else {
            mPhotosMenuItem.setVisible(false);
            mCategoriesMenuItem.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.photos:
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, mPhotosFragment, GalleryPhotosGridFragment.TAG)
                        .commit();
                mPhotosMenuItem.setVisible(false);
                mCategoriesMenuItem.setVisible(true);
                AppPreferences.getInstance().setGalleryDefaultScreen(SCREEN_RECENT_PHOTOS);
                return true;
            case R.id.categories:
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, mCategoriesFragment, GalleryCategoriesGridFragment.TAG)
                        .commit();
                mCategoriesMenuItem.setVisible(false);
                mPhotosMenuItem.setVisible(true);
                AppPreferences.getInstance().setGalleryDefaultScreen(SCREEN_CATEGORIES);
                return true;
            default:
                return false;
        }
    }

    private FragmentManager.OnBackStackChangedListener mOnBackStackChangedListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            int backStackEntryCount = getChildFragmentManager().getBackStackEntryCount();
            mPhotosMenuItem.setVisible(backStackEntryCount == 0);
        }
    };
}
