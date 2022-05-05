package ru.netcracker.bikepacker.adapter.trackmenu;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import ru.netcracker.bikepacker.view.UserMenuInformationAccountFragment;
import ru.netcracker.bikepacker.view.TrackMenuRecycleViewFragment;

public class TrackMenuPagerAdapter extends FragmentStateAdapter {

    public TrackMenuPagerAdapter(@NonNull Fragment fragment, Context context) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            //all used tracks
            case 0:
                return new TrackMenuRecycleViewFragment(position);
            //favorite tracks
            case 1:
                return new TrackMenuRecycleViewFragment(position);
            default:
                return new TrackMenuRecycleViewFragment(position);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
