package by.geth.gethsemane.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import by.geth.gethsemane.R;
import by.geth.gethsemane.data.model.Birthday;

public class BirthdaysAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_BIRTHDAY = 1;

    private static final DateFormat sDateFormat = new SimpleDateFormat("d MMMM", new Locale("ru", "RU"));

    private List<BirthdayGroup> mBirthdayGroupList;

    public void setBirthdays(@NonNull List<Birthday> birthdays) {
        List<Birthday> birthdayList = new ArrayList<>();
        for (Birthday birthday : birthdays) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, birthday.getMonth() - 1);
            calendar.set(Calendar.DAY_OF_MONTH, birthday.getDay());
            int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
            int currentDayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

            if ((dayOfYear >= currentDayOfYear && dayOfYear - currentDayOfYear < 90) ||
                    (dayOfYear < currentDayOfYear && dayOfYear + 365 - currentDayOfYear < 90)) {
                birthdayList.add(birthday);
            }
        }
        Collections.sort(birthdayList);

        mBirthdayGroupList = new ArrayList<>();
        if (!birthdayList.isEmpty()) {
            Calendar currentCalendar = Calendar.getInstance();
            int currentMonth = currentCalendar.get(Calendar.MONTH) + 1;
            int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);

            Birthday nearestBirthday = birthdayList.get(0);
            if (nearestBirthday.getMonth() == currentMonth && nearestBirthday.getDay() == currentDay) {
                List<Birthday> todayBirthdayList = new ArrayList<>();
                todayBirthdayList.add(nearestBirthday);

                BirthdayGroup todayBirthdayGroup = new BirthdayGroup(BirthdayGroup.Type.TODAY, todayBirthdayList);
                mBirthdayGroupList.add(todayBirthdayGroup);
                birthdayList.remove(nearestBirthday);
            }

            BirthdayGroup soonBirthdayGroup = new BirthdayGroup(BirthdayGroup.Type.SOON, birthdayList);
            mBirthdayGroupList.add(soonBirthdayGroup);
        }
    }

    @Override
    public int getItemViewType(int position) {
        for (BirthdayGroup group : mBirthdayGroupList) {
            if (position == 0) {
                return ITEM_VIEW_TYPE_HEADER;
            } else {
                position -= 1;
                if (position < group.getBirthdays().size()) {
                    return ITEM_VIEW_TYPE_BIRTHDAY;
                } else {
                    position -= group.getBirthdays().size();
                }
            }
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView;
        switch (viewType) {
            case ITEM_VIEW_TYPE_HEADER:
                itemView = inflater.inflate(R.layout.item_birthday_group, parent, false);
                return new HeaderViewHolder(itemView);
            case ITEM_VIEW_TYPE_BIRTHDAY:
                itemView = inflater.inflate(R.layout.item_birthday_child, parent, false);
                return new BirthdayViewHolder(itemView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM_VIEW_TYPE_HEADER:
                onBindViewHolder((HeaderViewHolder) holder, position);
                break;
            case ITEM_VIEW_TYPE_BIRTHDAY:
                onBindViewHolder((BirthdayViewHolder) holder, position);
                break;
        }
    }

    private void onBindViewHolder(final HeaderViewHolder holder, int position) {
        BirthdayGroup group = null;
        for (BirthdayGroup aGroup : mBirthdayGroupList) {
            if (position == 0) {
                group = aGroup;
                break;
            } else {
                position -= 1 + aGroup.getBirthdays().size();
            }
        }
        if (group == null) {
            return;
        }

        switch (group.getType()) {
            case TODAY:
                holder.titleView.setText(R.string.birthdays_header_today);
                break;
            case SOON:
                holder.titleView.setText(R.string.birthdays_header_soon);
                break;
        }
    }

    private void onBindViewHolder(BirthdayViewHolder holder, int position) {
        if (mBirthdayGroupList.size() == 2 && position == 1) {
            holder.dividerView.setVisibility(View.GONE);
        } else if (position == getItemCount() - 1) {
            holder.dividerView.setVisibility(View.GONE);
        } else {
            holder.dividerView.setVisibility(View.VISIBLE);
        }

        Birthday birthday = null;
        for (BirthdayGroup group : mBirthdayGroupList) {
            position -= 1;
            if (position < group.getBirthdays().size()) {
                birthday = group.getBirthdays().get(position);
                break;
            } else {
                position -= group.getBirthdays().size();
            }
        }
        if (birthday == null) {
            return;
        }

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.MONTH, birthday.getMonth() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, birthday.getDay());
        Date date = calendar.getTime();

        holder.birthdayView.setText(sDateFormat.format(date));

        holder.personsGroup.removeAllViews();
        Context context = holder.itemView.getContext();
        for (String person : birthday.getPersons()) {
            TextView personView = new TextView(context);
            personView.setText(person);
            holder.personsGroup.addView(personView);
        }
    }

    @Override
    public int getItemCount() {
        if (mBirthdayGroupList == null) {
            return 0;
        } else {
            int count = 0;
            for (BirthdayGroup group : mBirthdayGroupList) {
                count += 1 + group.getBirthdays().size();
            }
            return count;
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;

        HeaderViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.text);
        }
    }

    private static class BirthdayViewHolder extends RecyclerView.ViewHolder {
        ViewGroup rootView;
        TextView birthdayView;
        ViewGroup personsGroup;
        View dividerView;

        BirthdayViewHolder(View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.root);
            birthdayView = itemView.findViewById(R.id.date);
            personsGroup = itemView.findViewById(R.id.persons);
            dividerView = itemView.findViewById(R.id.divider);
        }
    }

    @SuppressWarnings("WeakerAccess")
    private static class BirthdayGroup {
        private enum Type {TODAY, SOON}

        private Type mType;
        private List<Birthday> mBirthdays;

        public BirthdayGroup(@NonNull Type type, @NonNull List<Birthday> birthdays) {
            mType = type;
            mBirthdays = birthdays;
        }

        @NonNull
        public Type getType() {
            return mType;
        }

        @NonNull
        public List<Birthday> getBirthdays() {
            return mBirthdays;
        }
    }
}
