package by.geth.gethsemane.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import by.geth.gethsemane.R;
import by.geth.gethsemane.data.Event;
import by.geth.gethsemane.download.DownloadController;
import by.geth.gethsemane.ui.view.AnimatableImageView;
import by.geth.gethsemane.util.Utils;

public class WorshipListAdapter extends RecyclerView.Adapter<WorshipListAdapter.ViewHolder> {
    private static final int ITEM_LAYOUT_RES_ID = R.layout.item_media_card;

    private static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy";

    private List<Event> mEvents = new ArrayList<>();
    private String mCurrentAudioUri;

    private OnClickListener mOnClickListener;

    public List<Event> getEvents() {
        return mEvents;
    }

    public void setEvents(List<Event> events) {
        mEvents = events;
    }

    public void setCurrentAudioUri(String uri) {
        mCurrentAudioUri = uri;
    }

    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    public void removeWorshipById(long id) {
        int pos = -1;
        for (int i = 0; i < mEvents.size(); i++) {
            if (mEvents.get(i).externalId() == id) {
                pos = i;
                break;
            }
        }
        if (pos >= 0) {
            mEvents.remove(pos);
            notifyItemRemoved(pos);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(ITEM_LAYOUT_RES_ID, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.getDefault());

        final Event event = mEvents.get(position);
        String title = event.getTitle();
        String subtitle = dateFormat.format(event.getDate());

        holder.titleView.setText(title);
        holder.subtitleView.setText(subtitle);

        holder.audioView.setVisibility(View.GONE);
        holder.downloadGroup.setVisibility(View.GONE);
        holder.contentView.setPadding(Utils.dpToPx(8), Utils.dpToPx(8), Utils.dpToPx(8), Utils.dpToPx(8));

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null)
                    mOnClickListener.onItemClick(event);
            }
        });

        holder.audioView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null)
                    mOnClickListener.onAudioClick(event);
            }
        });

        if (TextUtils.isEmpty(event.getAudioLocalUri())) {
            if (DownloadController.getInstance().isDownloadInProgress(event)) {
                holder.downloadButton.setVisibility(View.GONE);
                holder.downloadProgressView.setVisibility(View.VISIBLE);
                holder.downloadDeleteButton.setVisibility(View.GONE);
            } else {
                holder.downloadButton.setVisibility(View.VISIBLE);
                holder.downloadProgressView.setVisibility(View.GONE);
                holder.downloadDeleteButton.setVisibility(View.GONE);
            }
        } else {
            holder.downloadButton.setVisibility(View.GONE);
            holder.downloadProgressView.setVisibility(View.GONE);
            holder.downloadDeleteButton.setVisibility(View.VISIBLE);
        }

        holder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null)
                    mOnClickListener.onDownloadClick(event);
            }
        });

        holder.downloadDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnClickListener != null) {
                    mOnClickListener.onDeleteClick(event);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEvents == null ? 0 : mEvents.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        View contentView;
        AnimatableImageView audioView;
        TextView titleView;
        TextView subtitleView;

        ViewGroup downloadGroup;
        ImageView downloadButton;
        ProgressBar downloadProgressView;
        ImageView downloadDeleteButton;

        private ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.item_media);
            contentView = itemView.findViewById(R.id.content_root);
            audioView = itemView.findViewById(R.id.media_audio_play);
            titleView = itemView.findViewById(R.id.media_title);
            subtitleView = itemView.findViewById(R.id.media_subtitle);

            downloadGroup = itemView.findViewById(R.id.media_download_group);
            downloadButton = itemView.findViewById(R.id.media_download_button);
            downloadProgressView = itemView.findViewById(R.id.media_download_progress);
            downloadDeleteButton = itemView.findViewById(R.id.media_download_delete);
        }
    }

    public interface OnClickListener {
        void onItemClick(Event event);
        void onAudioClick(Event event);
        void onDownloadClick(Event event);
        void onDeleteClick(Event event);
    }
}
