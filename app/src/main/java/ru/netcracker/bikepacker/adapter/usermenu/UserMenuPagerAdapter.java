package ru.netcracker.bikepacker.adapter.usermenu;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import ru.netcracker.bikepacker.UserMenuInformationAccountFragment;
import ru.netcracker.bikepacker.UserMenuRecycleViewFragment;

public class UserMenuPagerAdapter extends FragmentStateAdapter {

    public UserMenuPagerAdapter(@NonNull Fragment fragment, Context context) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            //all used tracks
            case 0:
                return new UserMenuRecycleViewFragment(position);
            //favorite tracks
            case 1:
                return new UserMenuRecycleViewFragment(position);
            //user information
            case 2:
                return new UserMenuInformationAccountFragment();
            default:
                return new UserMenuInformationAccountFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
