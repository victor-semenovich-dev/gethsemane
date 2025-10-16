package by.geth.gethsemane.ui.adapter;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

import by.geth.gethsemane.data.Event;
import by.geth.gethsemane.ui.fragment.worship.WorshipFragment;
import by.geth.gethsemane.ui.fragment.base.BaseFragment;

public class WorshipPagerAdapter extends FragmentStatePagerAdapter {

    private List<Event> mEventList;

    public WorshipPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public List<Event> getEventList() {
        return mEventList;
    }

    public void setEventList(List<Event> eventList) {
        this.mEventList = eventList;
    }

    @Override
    public Fragment getItem(int position) {
        Event event = mEventList.get(position);
        Bundle args = new Bundle();
        args.putLong(WorshipFragment.ARGS_WORSHIP_ID, event.externalId());
        args.putBoolean(BaseFragment.ARGS_APPLY_UI_ARGUMENTS, false);
        WorshipFragment fragment = new WorshipFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mEventList == null ? 0 : mEventList.size();
    }
}
