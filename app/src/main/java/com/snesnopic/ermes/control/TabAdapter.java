package com.snesnopic.ermes.control;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.snesnopic.ermes.activitypkg.MyGroupsFragment;
import com.snesnopic.ermes.activitypkg.OtherGroupsFragment;
import com.snesnopic.ermes.activitypkg.RequestsFragment;

public class TabAdapter extends FragmentStateAdapter {

    public TabAdapter(FragmentActivity fragment)
    {
        super(fragment);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            default:
            case 0:
                return new MyGroupsFragment();
            case 1:
                return new OtherGroupsFragment();
            case 2:
                return new RequestsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}