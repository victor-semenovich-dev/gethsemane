package by.geth.gethsemane.ui.adapter;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import by.geth.gethsemane.data.Photo;
import by.geth.gethsemane.ui.fragment.gallery.GalleryPhotoFullscreenFragment;

public class GalleryPhotosPagerAdapter extends FragmentStatePagerAdapter {

    private List<Photo> mPhotoList;

    public GalleryPhotosPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setPhotoList(List<Photo> photoList) {
        mPhotoList = photoList;
    }

    public List<Photo> getPhotoList() {
        return mPhotoList;
    }

    @Override
    public Fragment getItem(int position) {
        Photo photo = mPhotoList.get(position);
        return GalleryPhotoFullscreenFragment.newInstance(photo.getBackendId());
    }

    @Override
    public int getCount() {
        return mPhotoList == null ? 0 : mPhotoList.size();
    }
}
