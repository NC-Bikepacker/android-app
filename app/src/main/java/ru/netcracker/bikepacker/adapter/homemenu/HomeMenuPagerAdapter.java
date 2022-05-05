package ru.netcracker.bikepacker.adapter.homemenu;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import ru.netcracker.bikepacker.view.HomeFragment;
import ru.netcracker.bikepacker.view.HomeMenuRecycleViewFragment;
import ru.netcracker.bikepacker.view.TrackMenuRecycleViewFragment;

public class HomeMenuPagerAdapter extends FragmentStateAdapter {

    public HomeMenuPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public HomeMenuPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public HomeMenuPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public HomeMenuPagerAdapter(HomeFragment homeFragment, Context context) {
        super(homeFragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            //all used tracks
            case 0:
                return new HomeMenuRecycleViewFragment(position);
            //favorite tracks
            case 1:
                return new HomeMenuRecycleViewFragment(position);
            default:
                return new HomeMenuRecycleViewFragment(0);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
