package by.geth.gethsemane.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import by.geth.gethsemane.R;
import by.geth.gethsemane.app.GlideApp;
import by.geth.gethsemane.data.MusicGroup;

public class MusicGroupsAdapter extends RecyclerView.Adapter<MusicGroupsAdapter.ViewHolder> {

    public interface Callback {
        void onMusicGroupClick(MusicGroup group);
    }

    private List<MusicGroup> mMusicGroupList;
    private Callback callback;

    public MusicGroupsAdapter(Callback callback) {
        this.callback = callback;
    }

    public void setMusicGroupList(List<MusicGroup> musicGroupList) {
        mMusicGroupList = musicGroupList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_list_article, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();

        MusicGroup musicGroup = mMusicGroupList.get(position);
        GlideApp.with(context).load(musicGroup.getImage()).into(holder.imageView);
        holder.titleView.setText(musicGroup.getTitle());
        holder.clickableView.setOnClickListener(v -> callback.onMusicGroupClick(musicGroup));
    }

    @Override
    public int getItemCount() {
        return mMusicGroupList == null ? 0 : mMusicGroupList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView titleView;
        private View clickableView;

        private ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            titleView = itemView.findViewById(R.id.titleView);
            clickableView = itemView.findViewById(R.id.clickableView);
        }
    }
}
