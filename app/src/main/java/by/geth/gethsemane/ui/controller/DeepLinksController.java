package by.geth.gethsemane.ui.controller;

import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.List;

import by.geth.gethsemane.R;
import by.geth.gethsemane.ui.fragment.gallery.GalleryPhotosGridFragment;

public class DeepLinksController {

    private static final String HTTP = "http";
    private static final String GETH_BY = "geth.by";

    private static final String GALLERY = "gallery";
    private static final String ALBUM = "album";

    private AppCompatActivity mActivity;

    public DeepLinksController(AppCompatActivity activity) {
        mActivity = activity;
    }

    public void onNewIntent(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null && uri.getScheme().equals(HTTP) && uri.getHost().equals(GETH_BY)) {
            List<String> pathSegments = uri.getPathSegments();
            if (pathSegments.size() == 3 && pathSegments.get(0).equals(GALLERY) && pathSegments.get(1).equals(ALBUM)) {
                try {
                    long albumId = Long.parseLong(pathSegments.get(2));
                    showAlbumFragment(albumId);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showAlbumFragment(long albumId) {
        Fragment albumFragment = GalleryPhotosGridFragment.newInstance(albumId);
        mActivity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(0, R.anim.slide_out_left_short, 0, R.anim.slide_out_right_short)
                .replace(R.id.fragment_container, albumFragment)
                .addToBackStack(null)
                .commit();
    }
}
