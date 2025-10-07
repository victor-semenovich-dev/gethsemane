package by.geth.gethsemane.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.activeandroid.query.Select;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import by.geth.gethsemane.R;
import by.geth.gethsemane.api.Server;
import by.geth.gethsemane.data.Event;
import by.geth.gethsemane.data.MusicGroup;

public class EventChangesAdapter
        extends RecyclerView.Adapter<EventChangesAdapter.ItemViewHolder>
        implements StickyRecyclerHeadersAdapter<EventChangesAdapter.HeaderViewHolder> {

    private static final int HEADER_LAYOUT_RES_ID = R.layout.header_event;
    private static final int ITEM_LAYOUT_RES_ID = R.layout.item_event_changes;

    private static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy HH:mm";

    private Context mContext;

    private List<Event> mAddedEvents;
    private List<Event> mDeletedEvents;
    private List<Event> mModifiedOldEvents;
    private List<Event> mModifiedNewEvents;

    public EventChangesAdapter(Context context,
                               List<Event> addedEvents, List<Event> deletedEvents,
                               List<Event> modifiedOldEvents, List<Event> modifiedNewEvents) {
        mContext = context;
        mAddedEvents = addedEvents;
        mDeletedEvents = deletedEvents;
        mModifiedOldEvents = modifiedOldEvents;
        mModifiedNewEvents = modifiedNewEvents;
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(HEADER_LAYOUT_RES_ID, parent, false);
        return new HeaderViewHolder(itemView);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(ITEM_LAYOUT_RES_ID, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder holder, int position) {
        int id = (int) getHeaderId(position);
        switch (id) {
            case 0:
                holder.textView.setText(R.string.fragment_event_changes_header_added);
                break;
            case 1:
                holder.textView.setText(R.string.fragment_event_changes_header_deleted);
                break;
            case 2:
                holder.textView.setText(R.string.fragment_event_changes_header_modified);
                break;
        }
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.getDefault());
        if (position < mAddedEvents.size() + mDeletedEvents.size()) {
            Event event;
            if (position < mAddedEvents.size())
                event = mAddedEvents.get(position);
            else
                event = mDeletedEvents.get(position - mAddedEvents.size());

            String dateStr = dateFormat.format(event.getDate());

            MusicGroup group = new Select()
                    .from(MusicGroup.class)
                    .where(MusicGroup.COLUMN_EXTERNAL_ID + " = ?", event.getMusicGroupId())
                    .executeSingle();
            if (event.getMusicGroupId() > 0 && group == null)
                Server.INSTANCE.getMusicGroup(event.getMusicGroupId(), false);

            String title = event.getTitle();
            String note = event.getNote();
            String subtitle = group == null ? dateStr : mContext.getString(
                    R.string.fragment_event_list_subtitle_pattern, dateStr, group.getTitle());

            holder.titleView.setText(title);
            holder.noteView.setText(note);
            holder.subtitleView.setText(subtitle);

            if (TextUtils.isEmpty(note)) {
                holder.noteView.setVisibility(View.GONE);
            } else {
                holder.noteView.setVisibility(View.VISIBLE);
            }
        } else {
            int index = position - (mAddedEvents.size() + mDeletedEvents.size());
            Event oldEvent = mModifiedOldEvents.get(index);
            Event newEvent = mModifiedNewEvents.get(index);

            String oldDateStr = dateFormat.format(oldEvent.getDate());
            String newDateStr = dateFormat.format(newEvent.getDate());

            MusicGroup oldGroup = new Select()
                    .from(MusicGroup.class)
                    .where(MusicGroup.COLUMN_EXTERNAL_ID + " = ?", oldEvent.getMusicGroupId())
                    .executeSingle();
            MusicGroup newGroup = new Select()
                    .from(MusicGroup.class)
                    .where(MusicGroup.COLUMN_EXTERNAL_ID + " = ?", newEvent.getMusicGroupId())
                    .executeSingle();
            if (oldEvent.getMusicGroupId() > 0 && oldGroup == null)
                Server.INSTANCE.getMusicGroup(oldEvent.getMusicGroupId(), false);
            if (newEvent.getMusicGroupId() > 0 && newGroup == null)
                Server.INSTANCE.getMusicGroup(newEvent.getMusicGroupId(), false);

            String oldTitle = oldEvent.getTitle().toUpperCase();
            String oldNote = oldEvent.getNote();
            String oldSubtitle = oldGroup == null ? oldDateStr : mContext.getString(
                    R.string.fragment_event_list_subtitle_pattern, oldDateStr, oldGroup.getTitle());

            String newTitle = newEvent.getTitle().toUpperCase();
            String newNote = newEvent.getNote();
            String newSubtitle = newGroup == null ? newDateStr : mContext.getString(
                    R.string.fragment_event_list_subtitle_pattern, newDateStr, newGroup.getTitle());

            setupTextView(holder.titleView, oldTitle, newTitle);
            setupTextView(holder.noteView, oldNote, newNote);
            setupTextView(holder.subtitleView, oldSubtitle, newSubtitle);
        }
    }

    private void setupTextView(TextView textView, String oldText, String newText) {
        if (TextUtils.isEmpty(oldText) && TextUtils.isEmpty(newText))
            textView.setVisibility(View.GONE);
        else
            textView.setVisibility(View.VISIBLE);

        textView.setSingleLine(true);
        if (!TextUtils.isEmpty(oldText) && oldText.equals(newText)) {
            textView.setText(newText);
        } else {
            if (TextUtils.isEmpty(oldText)) {
                String html = "<b>" + newText + "</b>";
                textView.setText(Html.fromHtml(html));
            } else if (TextUtils.isEmpty(newText)) {
                textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                textView.setText(oldText);
            } else {
                String text = oldText + '\n' + newText;
                textView.setSingleLine(false);
                textView.setText(text, TextView.BufferType.SPANNABLE);
                Spannable spannable = (Spannable)textView.getText();
                spannable.setSpan(new StrikethroughSpan(), 0, oldText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    @Override
    public long getHeaderId(int position) {
        if (position < mAddedEvents.size())
            return 0;
        else if (position < mAddedEvents.size() + mDeletedEvents.size())
            return 1;
        else if (position < mAddedEvents.size() + mDeletedEvents.size() + mModifiedNewEvents.size())
            return 2;
        else
            return -1;
    }

    @Override
    public int getItemCount() {
        return mAddedEvents.size() + mDeletedEvents.size() + mModifiedNewEvents.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        TextView titleView;
        TextView noteView;
        TextView subtitleView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.item_media);
            titleView = (TextView) itemView.findViewById(R.id.event_title);
            noteView = (TextView) itemView.findViewById(R.id.event_note);
            subtitleView = (TextView) itemView.findViewById(R.id.event_subtitle);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
        }
    }
}
