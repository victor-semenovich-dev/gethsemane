package by.geth.gethsemane.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import by.geth.gethsemane.R;
import by.geth.gethsemane.data.base.AudioItem;
import by.geth.gethsemane.download.DownloadController;
import by.geth.gethsemane.ui.fragment.base.AudioFragment;
import by.geth.gethsemane.ui.view.AnimatableImageView;

import static by.geth.gethsemane.util.UtilsKt.share;

public class AudioItemsListAdapter extends RecyclerView.Adapter<AudioItemsListAdapter.ViewHolder> {
    private static final int ITEM_LAYOUT_RES_ID = R.layout.item_media_card;

    public static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy";

    private final AudioFragment mFragment;

    private List<AudioItem> mAudioItems = new ArrayList<>();

    private OnClickListener mOnClickListener;

    public AudioItemsListAdapter(AudioFragment fragment) {
        mFragment = fragment;
    }

    public List<AudioItem> getAudioItems() {
        return mAudioItems;
    }

    public void setAudioItems(List<AudioItem> sermons) {
        mAudioItems = new ArrayList<>(sermons);
    }

    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    public void applyFilter(List<AudioItem> removedSermons, List<AudioItem> addedSermons,
                            List<AudioItem> modifiedSermons) {
        for (AudioItem sermon : removedSermons) {
            int index = mAudioItems.indexOf(sermon);
            if (index >= 0) {
                mAudioItems.remove(index);
                notifyItemRemoved(index);
            }
        }
        for (AudioItem sermon : addedSermons) {
            int index = 0;
            for (AudioItem oldSermon : mAudioItems) {
                if (sermon.getDate().getTime() > oldSermon.getDate().getTime())
                    break;
                index++;
            }
            mAudioItems.add(index, sermon);
            notifyItemInserted(index);
        }
        for (AudioItem sermon : modifiedSermons) {
            int index = mAudioItems.indexOf(sermon);
            if (index >= 0) {
                notifyItemChanged(index);
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(ITEM_LAYOUT_RES_ID, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.getDefault());

        final AudioItem audioItem = mAudioItems.get(position);
        String author = audioItem.getAuthor();
        String title = audioItem.getTitle();
        String dateStr = dateFormat.format(audioItem.getDate());
        String subtitle = dateStr;
        if (author != null) {
            subtitle = mFragment.getString(R.string.fragment_sermon_list_subtitle_pattern, dateStr,
                    author);
        }

        holder.titleView.setText(title);
        holder.subtitleView.setText(subtitle);

        if (mFragment.isInProgress(audioItem))
            holder.audioView.startAnimation();
        else
            holder.audioView.stopAnimation();

//        holder.rootView.setOnClickListener(v -> {
//            if (mOnClickListener != null)
//                mOnClickListener.onItemClick(audioItem);
//        });

        holder.audioView.setOnClickListener(v -> {
            if (mOnClickListener != null)
                mOnClickListener.onAudioClick(audioItem);
        });

        holder.downloadGroup.setVisibility(View.VISIBLE);

		if (TextUtils.isEmpty(audioItem.getLocalPath())) {
		    if (DownloadController.getInstance().isDownloadInProgress(audioItem)) {
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
        holder.downloadButton.setOnClickListener(v -> {
            if (mOnClickListener != null)
                mOnClickListener.onDownloadClick(audioItem);
        });
		holder.downloadDeleteButton.setOnClickListener(view -> {
            if (mOnClickListener != null)
                mOnClickListener.onDeleteClick(audioItem);
        });

		if (audioItem.getLocalPath() == null) {
		    holder.shareButton.setVisibility(View.GONE);
        } else {
            holder.shareButton.setVisibility(View.VISIBLE);
            holder.shareButton.setOnClickListener(v -> share(audioItem, holder.itemView.getContext()));
        }
    }

    @Override
    public int getItemCount() {
        return mAudioItems == null ? 0 : mAudioItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        AnimatableImageView audioView;
        TextView titleView;
        TextView subtitleView;

        ViewGroup downloadGroup;
        ImageView downloadButton;
        ProgressBar downloadProgressView;
        ImageView downloadDeleteButton;
        View shareButton;

        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.item_media);
            audioView = itemView.findViewById(R.id.media_audio_play);
            titleView = itemView.findViewById(R.id.media_title);
            subtitleView = itemView.findViewById(R.id.media_subtitle);

            downloadGroup = itemView.findViewById(R.id.media_download_group);
            downloadButton = itemView.findViewById(R.id.media_download_button);
            downloadProgressView = itemView.findViewById(R.id.media_download_progress);
            downloadDeleteButton = itemView.findViewById(R.id.media_download_delete);

            shareButton = itemView.findViewById(R.id.media_share);
        }
    }

    public interface OnClickListener {
        void onItemClick(AudioItem audioItem);
        void onAudioClick(AudioItem audioItem);
        void onDownloadClick(AudioItem audioItem);
        void onDeleteClick(AudioItem audioItem);
    }
}
