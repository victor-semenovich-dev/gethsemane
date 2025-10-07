package by.geth.gethsemane.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.activeandroid.query.Select;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import by.geth.gethsemane.BuildConfig;
import by.geth.gethsemane.R;
import by.geth.gethsemane.app.AppPreferences;
import by.geth.gethsemane.data.Photo;
import by.geth.gethsemane.ui.activity.base.FullscreenActivity;
import by.geth.gethsemane.ui.adapter.GalleryPhotosPagerAdapter;
import by.geth.gethsemane.ui.fragment.gallery.GalleryPhotoFullscreenFragment;

public class PhotosFullscreenActivity extends FullscreenActivity implements GalleryPhotoFullscreenFragment.PhotoFragmentCallback {

    private static final String EXTRA_PHOTO_LIST = "photo_list";
    private static final String EXTRA_ALBUM_ID = "album_id";
    private static final String EXTRA_POSITION = "position";

    public static final String EXTRA_RESULT_POSITION = "result_position";

    private static final String STATE_IS_ACTIONBAR_SHOWING = "is_actionbar_showing";
    private static final String STATE_POSITION = "position";

    /**
     * Start for the album
     */
    public static void startForResult(Fragment fragment, int requestCode, long albumId, int position) {
        Intent intent = new Intent(fragment.getContext(), PhotosFullscreenActivity.class);
        intent.putExtra(EXTRA_ALBUM_ID, albumId);
        intent.putExtra(EXTRA_POSITION, position);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * Start for the photos list
     */
    public static void startForResult(Fragment fragment, int requestCode, List<Photo> photoList, int position) {
        Intent intent = new Intent(fragment.getContext(), PhotosFullscreenActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_PHOTO_LIST, new ArrayList<Parcelable>(photoList));
        intent.putExtra(EXTRA_POSITION, position);
        fragment.startActivityForResult(intent, requestCode);
    }

    private int mDefaultPosition;

    private ViewPager mPager;
    private GalleryPhotosPagerAdapter mAdapter;
    private Toolbar mToolbar;
    private Animation mSlideInAnimation;
    private Animation mSlideOutAnimation;
    private MenuItem mInfoMenuItem;
    private ProgressBar mProgressView;

    private LoadPhotosTask mLoadPhotosTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos_fullscreen);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            getSupportActionBar().show();
        } else {
            boolean isActionBarShowing = savedInstanceState.getBoolean(STATE_IS_ACTIONBAR_SHOWING, false);
            if (isActionBarShowing) {
                getSupportActionBar().show();
            } else {
                getSupportActionBar().hide();
            }
        }

        if (savedInstanceState == null) {
            mDefaultPosition = getIntent().getIntExtra(EXTRA_POSITION, 0);
        } else {
            mDefaultPosition = savedInstanceState.getInt(STATE_POSITION);
        }

        mPager = findViewById(R.id.pager);
        mAdapter = new GalleryPhotosPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.addOnPageChangeListener(mOnPageChangeListener);
        mProgressView = findViewById(R.id.progress);

        mSlideOutAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);
        mSlideOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                getSupportActionBar().hide();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mSlideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_top);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPager.removeOnPageChangeListener(mOnPageChangeListener);
        AppPreferences.getInstance().removeListener(mOnPreferenceChangedListener);
        if (mLoadPhotosTask != null) {
            mLoadPhotosTask.cancel(true);
            mLoadPhotosTask = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_IS_ACTIONBAR_SHOWING, getSupportActionBar().isShowing());
        outState.putInt(STATE_POSITION, mPager.getCurrentItem());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_photos_fullscreen, menu);
        mInfoMenuItem = menu.findItem(R.id.info);
        updateInfoState();
        AppPreferences.getInstance().addListener(mOnPreferenceChangedListener);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent resultData = new Intent();
        resultData.putExtra(EXTRA_RESULT_POSITION, mPager.getCurrentItem());
        setResult(RESULT_OK, resultData);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.share:
                share();
                return true;
            case R.id.info:
                boolean isShowInfo = !AppPreferences.getInstance().isGalleryShowInfo();
                AppPreferences.getInstance().setGalleryShowInfo(isShowInfo);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onScreenClick() {
        if (getSupportActionBar().isShowing()) {
            mToolbar.startAnimation(mSlideOutAnimation);
        } else {
            getSupportActionBar().show();
            mToolbar.startAnimation(mSlideInAnimation);
        }
    }

    private void initData() {
        if (getIntent().hasExtra(EXTRA_PHOTO_LIST)) {
            List<Photo> photoList = getIntent().getParcelableArrayListExtra(EXTRA_PHOTO_LIST);
            initUIWithPhotos(photoList);
        } else if (getIntent().hasExtra(EXTRA_ALBUM_ID)) {
            long albumId = getIntent().getLongExtra(EXTRA_ALBUM_ID, 0);
            mLoadPhotosTask = new LoadPhotosTask(mLoadPhotosCallback);
            mLoadPhotosTask.execute(albumId);
        } else {
            // load recent photos
            mLoadPhotosTask = new LoadPhotosTask(mLoadPhotosCallback);
            mLoadPhotosTask.execute();
        }
    }

    private void initUIWithPhotos(List<Photo> photoList) {
        mAdapter.setPhotoList(photoList);
        mAdapter.notifyDataSetChanged();
        mPager.setCurrentItem(mDefaultPosition, false);
        getSupportActionBar().setTitle((mDefaultPosition + 1) + "/" + mPager.getAdapter().getCount());
    }

    private void updateInfoState() {
        if (AppPreferences.getInstance().isGalleryShowInfo()) {
            mInfoMenuItem.setIcon(R.drawable.ic_action_info_solid);
        } else {
            mInfoMenuItem.setIcon(R.drawable.ic_action_info_outline);
        }
    }

    private void share() {
        if (mAdapter.getPhotoList() == null) {
            return;
        }
        int pos = mPager.getCurrentItem();
        long photoId = mAdapter.getPhotoList().get(pos).getBackendId();
        Photo photo = new Select().from(Photo.class).where(Photo.COLUMN_BACKEND_ID + " = ?", photoId).executeSingle();
        if (photo != null && photo.getFilePath() != null) {
            File file = new File(photo.getFilePath());
            Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", file);

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, photo.getTitle());
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("*/*");

            startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
        }
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            getSupportActionBar().setTitle((position + 1) + "/" + mPager.getAdapter().getCount());
        }
    };

    private SharedPreferences.OnSharedPreferenceChangeListener mOnPreferenceChangedListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(AppPreferences.PREF_GALLERY_SHOW_INFO)) {
                updateInfoState();
            }
        }
    };

    private LoadPhotosTask.Callback mLoadPhotosCallback = new LoadPhotosTask.Callback() {
        @Override
        public void onPreExecute() {
            mProgressView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPostExecute(List<Photo> photoList) {
            mProgressView.setVisibility(View.GONE);
            initUIWithPhotos(photoList);
            mLoadPhotosTask = null;
        }
    };

    private static class LoadPhotosTask extends AsyncTask<Long, Void, List<Photo>> {
        private interface Callback {
            void onPreExecute();
            void onPostExecute(List<Photo> photoList);
        }

        private Callback mCallback;

        private LoadPhotosTask(Callback callback) {
            mCallback = callback;
        }

        @Override
        protected void onPreExecute() {
            mCallback.onPreExecute();
        }

        @Override
        protected List<Photo> doInBackground(Long... params) {
            List<Photo> photoList;
            if (params.length > 0) {
                long albumId = params[0];
                photoList = new Select()
                        .from(Photo.class)
                        .where(Photo.COLUMN_ALBUM_ID + " = ?", albumId)
                        .execute();
            } else {
                photoList = new Select()
                        .from(Photo.class)
                        .where(Photo.COLUMN_IS_RECENT + " > 0")
                        .execute();
            }
            Collections.sort(photoList);
            return photoList;
        }

        @Override
        protected void onPostExecute(List<Photo> photoList) {
            mCallback.onPostExecute(photoList);
        }
    }
}
