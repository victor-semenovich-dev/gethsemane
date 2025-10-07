package by.geth.gethsemane.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.activeandroid.query.Select;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import by.geth.gethsemane.R;
import by.geth.gethsemane.api.Server;
import by.geth.gethsemane.data.Event;
import by.geth.gethsemane.data.MusicGroup;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {
    private static final int ITEM_LAYOUT_RES_ID = R.layout.item_event_card;

    private static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy HH:mm";

    private Context mContext;
    private List<Event> mEvents;

    public EventListAdapter(Context context) {
        mContext = context;
    }

    public void setEvents(List<Event> events) {
        mEvents = events;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(ITEM_LAYOUT_RES_ID, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event event = mEvents.get(position);

        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.getDefault());
        String dateStr = dateFormat.format(event.getDate());

        MusicGroup group = new Select()
                .from(MusicGroup.class)
                .where(MusicGroup.COLUMN_EXTERNAL_ID + " = ?", event.getMusicGroupId())
                .executeSingle();
        if (event.getMusicGroupId() > 0 && group == null)
            Server.INSTANCE.getMusicGroup(event.getMusicGroupId(), false);

        String title = event.getTitle();
        String note = event.getNote();
        String subtitle = (group == null || group.getTitle() == null) ? dateStr :
            mContext.getString(R.string.fragment_event_list_subtitle_pattern, dateStr, group.getTitle());

        holder.titleView.setText(title);
        holder.noteView.setText(note);
        holder.subtitleView.setText(subtitle);

        if (TextUtils.isEmpty(note)) {
            holder.noteView.setVisibility(View.GONE);
        } else {
            holder.noteView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mEvents == null ? 0 : mEvents.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        TextView titleView;
        TextView noteView;
        TextView subtitleView;

        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.item_media);
            titleView = (TextView) itemView.findViewById(R.id.event_title);
            noteView = (TextView) itemView.findViewById(R.id.event_note);
            subtitleView = (TextView) itemView.findViewById(R.id.event_subtitle);
        }
    }
}
