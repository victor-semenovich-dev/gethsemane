package by.geth.gethsemane.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import by.geth.gethsemane.R;
import by.geth.gethsemane.app.GlideApp;
import by.geth.gethsemane.data.Photo;

public class GalleryPhotosGridAdapter extends RecyclerView.Adapter<GalleryPhotosGridAdapter.ViewHolder> {

    public interface OnPhotoClickListener {
        void onPhotoClick(List<Photo> photoList, int position);
    }

    private List<Photo> mPhotoList = new ArrayList<>();
    private OnPhotoClickListener mOnPhotoClickListener;

    public void setPhotos(List<Photo> photoList) {
        mPhotoList = new ArrayList<>(photoList);
    }

    public void addPhotos(List<Photo> photoList) {
        int positionStart = mPhotoList.size();
        mPhotoList.addAll(photoList);
        notifyItemRangeInserted(positionStart, photoList.size());
    }

    public void setOnPhotoClickListener(OnPhotoClickListener onPhotoClickListener) {
        mOnPhotoClickListener = onPhotoClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_gallery_photo, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Photo photo = mPhotoList.get(position);
        GlideApp.with(holder.previewView)
                .load(photo.getPreviewUrl())
                .placeholder(R.drawable.img_placeholder)
                .into(holder.previewView);

        if (mOnPhotoClickListener != null) {
            holder.previewView.setEnabled(true);
            holder.previewView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnPhotoClickListener.onPhotoClick(mPhotoList, holder.getAdapterPosition());
                }
            });
        } else {
            holder.previewView.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return mPhotoList == null ? 0 : mPhotoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView previewView;

        ViewHolder(View itemView) {
            super(itemView);
            previewView = itemView.findViewById(R.id.preview);
        }
    }
}
