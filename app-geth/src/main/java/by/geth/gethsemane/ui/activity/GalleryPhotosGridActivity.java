package by.geth.gethsemane.ui.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;

import by.geth.gethsemane.R;
import by.geth.gethsemane.ui.fragment.gallery.GalleryPhotosGridFragment;

/**
 * Created by Victor Semenovich on 30.03.2018.
 */
public class GalleryPhotosGridActivity extends AppCompatActivity {

    public static final String EXTRA_PHOTOS_IDS = "EXTRA_PHOTOS_IDS";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_photos_grid);

        String photosIds = getIntent().getStringExtra(EXTRA_PHOTOS_IDS);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.new_photos_title);
        }

        if (photosIds != null) {
            GalleryPhotosGridFragment fragment = GalleryPhotosGridFragment.newInstance(photosIds);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }
}
