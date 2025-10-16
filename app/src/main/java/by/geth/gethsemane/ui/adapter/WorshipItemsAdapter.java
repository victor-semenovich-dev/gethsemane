package by.geth.gethsemane.ui.adapter;

import static by.geth.gethsemane.util.UtilsKt.share;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import by.geth.gethsemane.R;
import by.geth.gethsemane.api.GetMusicGroupsRequest;
import by.geth.gethsemane.api.Server;
import by.geth.gethsemane.data.MusicGroup;
import by.geth.gethsemane.data.Sermon;
import by.geth.gethsemane.data.Song;
import by.geth.gethsemane.data.Witness;
import by.geth.gethsemane.data.Worship;
import by.geth.gethsemane.data.base.AudioItem;
import by.geth.gethsemane.data.model.local.activeAndroid.AuthorEntity;
import by.geth.gethsemane.domain.model.Author;
import by.geth.gethsemane.download.DownloadController;
import by.geth.gethsemane.ui.view.AnimatableImageView;

public class WorshipItemsAdapter extends BaseAdapter {
    public interface OnAudioClickListener {
        void onAudioClick(AudioItem item);
    }
    public interface OnSermonClickListener {
        void onSermonClick(Sermon sermon);
    }
    public interface OnWitnessClickListener {
        void onWitnessClick(Witness witness);
    }
	public interface OnDownloadClickListener {
		void onDownloadClick(AudioItem item);
		void onDeleteClick(AudioItem item);
	}
    public interface AudioInfoProvider {
        boolean isAudioInProgress(AudioItem item);
    }

    private Worship mWorship;
    private List<Sermon> mSermonList = new ArrayList<>();
    private List<Witness> mWitnessList = new ArrayList<>();
    private List<Song> mSongList = new ArrayList<>();
    private Map<Long, Author> mAuthorsList = new HashMap<>();

    private OnAudioClickListener mOnAudioClickListener;
    private OnSermonClickListener mOnSermonClickListener;
    private OnWitnessClickListener mOnWitnessClickListener;
	private OnDownloadClickListener mOnDownloadClickListener;
    private AudioInfoProvider mAudioInfoProvider;

    public Worship getWorship() {
        return mWorship;
    }

    public void setWorship(Worship worship) {
        mWorship = worship;
    }

    public void setSermonList(List<Sermon> sermonList) {
        mSermonList = sermonList;
    }

    public void setWitnessList(List<Witness> witnessList) {
        mWitnessList = witnessList;
    }

    public void setSongList(List<Song> songList) {
        mSongList = songList;
    }

    public void setAuthors(Map<Long, Author> authorsMap) {
        mAuthorsList = authorsMap;
    }

    public void setOnAudioClickListener(OnAudioClickListener listener) {
        mOnAudioClickListener = listener;
    }

    public void setOnSermonClickListener(OnSermonClickListener listener) {
        mOnSermonClickListener = listener;
    }

    public void setOnWitnessClickListener(OnWitnessClickListener listener) {
        mOnWitnessClickListener = listener;
    }

	public void setOnDownloadClickListener(OnDownloadClickListener listener) {
		mOnDownloadClickListener = listener;
	}

    public void setAudioInfoProvider(AudioInfoProvider provider) {
        mAudioInfoProvider = provider;
    }

    @Override
    public int getCount() {
        int count = mSermonList.size() + mWitnessList.size() + mSongList.size();
        if (mWorship != null) {
            count += 1;
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        if (position < mSermonList.size()) {
            return mSermonList.get(position);
        } else if (position < mSermonList.size() + mWitnessList.size()) {
            return mWitnessList.get(position - mSermonList.size());
        } else if (position < mSermonList.size() + mWitnessList.size() + mSongList.size()) {
            int index = position - mSermonList.size() - mWitnessList.size();
            return mSongList.get(index);
        } else {
            return mWorship;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            rowView = inflater.inflate(R.layout.item_media, parent, false);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.titleView = rowView.findViewById(R.id.media_title);
            viewHolder.subtitleView = rowView.findViewById(R.id.media_subtitle);
            viewHolder.audioView = rowView.findViewById(R.id.media_audio_play);
            viewHolder.downloadGroup = rowView.findViewById(R.id.media_download_group);
            viewHolder.downloadButton = rowView.findViewById(R.id.media_download_button);
            viewHolder.downloadProgressView = rowView.findViewById(R.id.media_download_progress);
            viewHolder.deleteButton = rowView.findViewById(R.id.media_download_delete);
            viewHolder.shareButton = rowView.findViewById(R.id.media_share);
            rowView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) rowView.getTag();
        Object item = getItem(position);
        viewHolder.downloadGroup.setVisibility(View.VISIBLE);
        if (item instanceof Sermon) {
            final Sermon sermon = (Sermon) item;
            final Author author = mAuthorsList.get((long) sermon.getAuthorId());
            rowView.setEnabled(true);
//            rowView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mOnSermonClickListener != null)
//                        mOnSermonClickListener.onSermonClick(sermon);
//                }
//            });
            viewHolder.titleView.setText(sermon.getTitle());
            if (author == null) {
                viewHolder.subtitleView.setText(
                        R.string.fragment_worship_pattern_item_sermon_subtitle_no_author);
            } else {
                viewHolder.subtitleView.setText(context.getString(
                        R.string.fragment_worship_pattern_item_sermon_subtitle, author.getName()));
            }
            viewHolder.audioView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnAudioClickListener != null) {
                        mOnAudioClickListener.onAudioClick(sermon);
                    }
                }
            });
            if (mAudioInfoProvider != null) {
                if (mAudioInfoProvider.isAudioInProgress(sermon))
                    viewHolder.audioView.startAnimation();
                else
                    viewHolder.audioView.stopAnimation();
            }
            if (TextUtils.isEmpty(sermon.getAudioLocalUri())) {
                if (DownloadController.getInstance().isDownloadInProgress(sermon)) {
                    viewHolder.downloadButton.setVisibility(View.GONE);
                    viewHolder.downloadProgressView.setVisibility(View.VISIBLE);
                    viewHolder.deleteButton.setVisibility(View.GONE);
                } else {
                    viewHolder.downloadButton.setVisibility(View.VISIBLE);
                    viewHolder.downloadProgressView.setVisibility(View.GONE);
                    viewHolder.deleteButton.setVisibility(View.GONE);
                }
            } else {
                viewHolder.downloadButton.setVisibility(View.GONE);
                viewHolder.downloadProgressView.setVisibility(View.GONE);
                viewHolder.deleteButton.setVisibility(View.VISIBLE);
            }
			viewHolder.downloadButton.setOnClickListener(v -> {
                if (mOnDownloadClickListener != null) {
                    mOnDownloadClickListener.onDownloadClick(sermon);
                }
            });
            viewHolder.deleteButton.setOnClickListener(view -> {
                if (mOnDownloadClickListener != null) {
                    mOnDownloadClickListener.onDeleteClick(sermon);
                }
            });
        } else if (item instanceof Witness) {
            final Witness witness = (Witness) item;
            final Author author = mAuthorsList.get((long) witness.getAuthorId());
            rowView.setEnabled(true);
//            rowView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mOnWitnessClickListener != null)
//                        mOnWitnessClickListener.onWitnessClick(witness);
//                }
//            });
            viewHolder.titleView.setText(witness.getTitle());
            if (author == null) {
                viewHolder.subtitleView.setText(
                        R.string.fragment_worship_pattern_item_witness_subtitle_no_author);
            } else {
                viewHolder.subtitleView.setText(context.getString(
                        R.string.fragment_worship_pattern_item_witness_subtitle, author.getName()));
            }
            viewHolder.audioView.setOnClickListener(v -> {
                if (mOnAudioClickListener != null) {
                    mOnAudioClickListener.onAudioClick(witness);
                }
            });
            if (mAudioInfoProvider != null) {
                if (mAudioInfoProvider.isAudioInProgress(witness))
                    viewHolder.audioView.startAnimation();
                else
                    viewHolder.audioView.stopAnimation();
            }
            if (TextUtils.isEmpty(witness.getAudioLocalUri())) {
                if (DownloadController.getInstance().isDownloadInProgress(witness)) {
                    viewHolder.downloadButton.setVisibility(View.GONE);
                    viewHolder.downloadProgressView.setVisibility(View.VISIBLE);
                    viewHolder.deleteButton.setVisibility(View.GONE);
                } else {
                    viewHolder.downloadButton.setVisibility(View.VISIBLE);
                    viewHolder.downloadProgressView.setVisibility(View.GONE);
                    viewHolder.deleteButton.setVisibility(View.GONE);
                }
            } else {
                viewHolder.downloadButton.setVisibility(View.GONE);
                viewHolder.downloadProgressView.setVisibility(View.GONE);
                viewHolder.deleteButton.setVisibility(View.VISIBLE);
            }
			viewHolder.downloadButton.setOnClickListener(v -> {
                if (mOnDownloadClickListener != null) {
                    mOnDownloadClickListener.onDownloadClick(witness);
                }
            });
            viewHolder.deleteButton.setOnClickListener(view -> {
                if (mOnDownloadClickListener != null) {
                    mOnDownloadClickListener.onDeleteClick(witness);
                }
            });
        } else if (item instanceof Song) {
            final Song song = (Song) item;
            final MusicGroup group = new Select()
                    .from(MusicGroup.class)
                    .where(AuthorEntity.COLUMN_ID + " = ?", song.getGroupID())
                    .executeSingle();
            rowView.setEnabled(false);
            viewHolder.titleView.setText(song.getTitle());
            if (group == null) {
                viewHolder.subtitleView.setText(
                        R.string.fragment_worship_pattern_item_song_subtitle_no_group);
                if (!Server.INSTANCE.isRunning(new GetMusicGroupsRequest())) {
                    Server.INSTANCE.getMusicGroup(song.getGroupID(), false);
                }
            } else {
                viewHolder.subtitleView.setText(context.getString(
                        R.string.fragment_worship_pattern_item_song_subtitle, group.getTitle()));
            }
            viewHolder.audioView.setOnClickListener(v -> {
                if (mOnAudioClickListener != null) {
                    mOnAudioClickListener.onAudioClick(song);
                }
            });
            if (mAudioInfoProvider != null) {
                if (mAudioInfoProvider.isAudioInProgress(song))
                    viewHolder.audioView.startAnimation();
                else
                    viewHolder.audioView.stopAnimation();
            }

            if (TextUtils.isEmpty(song.getAudioLocalUri())) {
                if (DownloadController.getInstance().isDownloadInProgress(song)) {
                    viewHolder.downloadButton.setVisibility(View.GONE);
                    viewHolder.downloadProgressView.setVisibility(View.VISIBLE);
                    viewHolder.deleteButton.setVisibility(View.GONE);
                } else {
                    viewHolder.downloadButton.setVisibility(View.VISIBLE);
                    viewHolder.downloadProgressView.setVisibility(View.GONE);
                    viewHolder.deleteButton.setVisibility(View.GONE);
                }
            } else {
                viewHolder.downloadButton.setVisibility(View.GONE);
                viewHolder.downloadProgressView.setVisibility(View.GONE);
                viewHolder.deleteButton.setVisibility(View.VISIBLE);
            }

			viewHolder.downloadButton.setOnClickListener(v -> {
                if (mOnDownloadClickListener != null) {
                    mOnDownloadClickListener.onDownloadClick(song);
                }
            });

            viewHolder.deleteButton.setOnClickListener(view -> {
                if (mOnDownloadClickListener != null) {
                    mOnDownloadClickListener.onDeleteClick(song);
                }
            });
        } else if (item instanceof Worship) {
            final Worship worship = (Worship) item;
            rowView.setEnabled(false);
            viewHolder.titleView.setText(worship.getTitle());
            viewHolder.subtitleView.setText(R.string.fragment_worship_pattern_item_worship_subtitle);
            viewHolder.audioView.setOnClickListener(v -> {
                if (mOnAudioClickListener != null) {
                    mOnAudioClickListener.onAudioClick(worship);
                }
            });
            if (mAudioInfoProvider != null) {
                if (mAudioInfoProvider.isAudioInProgress(worship)) {
                    viewHolder.audioView.startAnimation();
                } else {
                    viewHolder.audioView.stopAnimation();
                }
            }
            if (TextUtils.isEmpty(worship.getAudioLocalUri())) {
                if (DownloadController.getInstance().isDownloadInProgress(worship)) {
                    viewHolder.downloadButton.setVisibility(View.GONE);
                    viewHolder.downloadProgressView.setVisibility(View.VISIBLE);
                    viewHolder.deleteButton.setVisibility(View.GONE);
                } else {
                    viewHolder.downloadButton.setVisibility(View.VISIBLE);
                    viewHolder.downloadProgressView.setVisibility(View.GONE);
                    viewHolder.deleteButton.setVisibility(View.GONE);
                }
            } else {
                viewHolder.downloadButton.setVisibility(View.GONE);
                viewHolder.downloadProgressView.setVisibility(View.GONE);
                viewHolder.deleteButton.setVisibility(View.VISIBLE);
            }
            viewHolder.downloadButton.setOnClickListener(v -> {
                if (mOnDownloadClickListener != null) {
                    mOnDownloadClickListener.onDownloadClick(worship);
                }
            });
            viewHolder.deleteButton.setOnClickListener(view -> {
                if (mOnDownloadClickListener != null) {
                    mOnDownloadClickListener.onDeleteClick(worship);
                }
            });
        }

        if (item instanceof AudioItem) {
            if (((AudioItem) item).getLocalPath() == null) {
                viewHolder.shareButton.setVisibility(View.GONE);
            } else {
                viewHolder.shareButton.setVisibility(View.VISIBLE);
                viewHolder.shareButton.setOnClickListener(v -> share((AudioItem) item, parent.getContext()));
            }
        }

        return rowView;
    }

    private static class ViewHolder {
        TextView titleView;
        TextView subtitleView;
        AnimatableImageView audioView;
        ViewGroup downloadGroup;
        ImageView downloadButton;
        ProgressBar downloadProgressView;
        ImageView deleteButton;
        View shareButton;
    }
}
