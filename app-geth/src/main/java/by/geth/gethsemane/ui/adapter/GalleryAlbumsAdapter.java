package by.geth.gethsemane.ui.adapter;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import by.geth.gethsemane.R;
import by.geth.gethsemane.app.GlideApp;
import by.geth.gethsemane.data.Album;

/**
 * Created by Victor Semenovich on 18.02.2018.
 */
public class GalleryAlbumsAdapter extends RecyclerView.Adapter<GalleryAlbumsAdapter.ViewHolder> {

    public interface OnAlbumClickListener {
        void onAlbumClick(Album album);
    }

    private List<Album> mAlbumList;
    private OnAlbumClickListener mOnAlbumClickListener;

    public void setAlbumList(List<Album> albumList) {
        mAlbumList = new ArrayList<>(albumList);
        Collections.sort(mAlbumList);
    }

    public void setOnAlbumClickListener(OnAlbumClickListener onAlbumClickListener) {
        mOnAlbumClickListener = onAlbumClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_gallery_album, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Album album = mAlbumList.get(position);
        GlideApp.with(holder.coverView)
                .load(album.getCoverUrl())
                .placeholder(R.drawable.img_placeholder)
                .into(holder.coverView);
        holder.titleView.setText(album.getTitle());

        if (mOnAlbumClickListener != null) {
            holder.cardView.setEnabled(true);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnAlbumClickListener.onAlbumClick(album);
                }
            });
        } else {
            holder.cardView.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return mAlbumList == null ? 0 : mAlbumList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView coverView;
        TextView titleView;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card);
            coverView = itemView.findViewById(R.id.cover);
            titleView = itemView.findViewById(R.id.title);
        }
    }
}
