package by.geth.gethsemane.ui.fragment.gallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.activeandroid.query.Select;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import by.geth.gethsemane.R;
import by.geth.gethsemane.app.AppPreferences;
import by.geth.gethsemane.data.Photo;
import by.geth.gethsemane.download.DownloadController;

public class GalleryPhotoFullscreenFragment extends Fragment {

    public interface PhotoFragmentCallback {
        void onScreenClick();
    }

    private static final String ARGS_PHOTO_ID = "photo_id";

    public static GalleryPhotoFullscreenFragment newInstance(long photoId) {
        Bundle args = new Bundle();
        args.putLong(ARGS_PHOTO_ID, photoId);

        GalleryPhotoFullscreenFragment fragment = new GalleryPhotoFullscreenFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private Photo mPhoto;
    private PhotoFragmentCallback mPhotoFragmentCallback;

    private ImageView mPhotoView;
    private ProgressBar mProgressView;
    private View mFallbackView;

    private ViewGroup mInfoGroup;
    private TextView mDescriptionView;
    private TextView mDateView;

    public Photo getPhoto() {
        return mPhoto;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PhotoFragmentCallback) {
            mPhotoFragmentCallback = (PhotoFragmentCallback) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long photoId = getArguments().getLong(ARGS_PHOTO_ID);
        mPhoto = new Select().from(Photo.class).where(Photo.COLUMN_BACKEND_ID + " = ?", photoId).executeSingle();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mPhotoView = view.findViewById(R.id.photo);
        mProgressView = view.findViewById(R.id.progress);
        mFallbackView = view.findViewById(R.id.fallback);

        mInfoGroup = view.findViewById(R.id.info);
        mDescriptionView = view.findViewById(R.id.description);
        mDateView = view.findViewById(R.id.date);

        mDescriptionView.setText(mPhoto.getTitle());
        mDateView.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(mPhoto.getDate()));

        updateInfoState();
        AppPreferences.getInstance().addListener(mOnPreferenceChangedListener);

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
        lbm.registerReceiver(mDownloadReceiver, DownloadController.getFullIntentFilter());

        mPhotoView.setOnClickListener(mOnScreenClickListener);
        mFallbackView.setOnClickListener(mOnScreenClickListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mPhoto.getFilePath() != null) {
            File file = new File(mPhoto.getFilePath());
            if (file.exists()) {
                mPhotoView.setImageURI(Uri.fromFile(file));
            } else {
                mPhoto.setFilePath(null);
                mPhoto.save();
                DownloadController.getInstance().download(mPhoto);
            }
        } else {
            DownloadController.getInstance().download(mPhoto);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
        lbm.unregisterReceiver(mDownloadReceiver);
        AppPreferences.getInstance().removeListener(mOnPreferenceChangedListener);
    }

    private void updateInfoState() {
        if (AppPreferences.getInstance().isGalleryShowInfo()) {
            mInfoGroup.setVisibility(View.VISIBLE);
        } else {
            mInfoGroup.setVisibility(View.GONE);
        }
    }

    private BroadcastReceiver mDownloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadController.ItemType itemType = (DownloadController.ItemType) intent.getSerializableExtra(DownloadController.EXTRA_ITEM_TYPE);
            long itemId = intent.getLongExtra(DownloadController.EXTRA_ITEM_ID, 0);
            if (itemType == DownloadController.ItemType.PHOTO && itemId == mPhoto.getBackendId() && intent.getAction() != null) {
                switch (intent.getAction()) {
                    case DownloadController.ACTION_ITEM_DOWNLOAD_STARTED:
                        mProgressView.setVisibility(View.VISIBLE);
                        mFallbackView.setVisibility(View.GONE);
                        break;
                    case DownloadController.ACTION_ITEM_DOWNLOAD_FINISHED:
                        mProgressView.setVisibility(View.GONE);
                        mFallbackView.setVisibility(View.GONE);
                        File file = new File(mPhoto.getFilePath());
                        mPhotoView.setImageURI(Uri.fromFile(file));
                        break;
                    case DownloadController.ACTION_ITEM_DOWNLOAD_CANCELLED:
                    case DownloadController.ACTION_ITEM_DOWNLOAD_ERROR:
                        mProgressView.setVisibility(View.GONE);
                        mFallbackView.setVisibility(View.VISIBLE);
                        break;
                }
            }
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

    private View.OnClickListener mOnScreenClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mPhotoFragmentCallback != null) {
                mPhotoFragmentCallback.onScreenClick();
            }
        }
    };
}
