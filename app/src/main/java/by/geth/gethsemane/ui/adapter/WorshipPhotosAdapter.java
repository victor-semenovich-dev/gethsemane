package by.geth.gethsemane.ui.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import by.geth.gethsemane.R;
import by.geth.gethsemane.app.GlideApp;
import by.geth.gethsemane.data.Photo;
import by.geth.gethsemane.data.Worship;

public class WorshipPhotosAdapter extends RecyclerView.Adapter<WorshipPhotosAdapter.ViewHolder> {

    public interface OnClickListener {
        void onClick(int position);
    }

    private Worship mWorship;
    private OnClickListener mOnClickListener;

    public void setWorship(Worship worship) {
        mWorship = worship;
        notifyDataSetChanged();
    }

    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_worship_photo, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Photo photo = mWorship.getPhotoList().get(position);
        GlideApp.with(holder.photoView)
                .load(photo.getPreviewUrl())
                .placeholder(R.drawable.img_placeholder)
                .into(holder.photoView);

        if (mOnClickListener != null) {
            holder.photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.onClick(holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mWorship == null || mWorship.getPhotoList() == null) {
            return 0;
        } else {
            return mWorship.getPhotoList().size();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView photoView;

        public ViewHolder(View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.photo);
        }
    }
}
